package com.youlan.tools.service.biz;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.NamingCase;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.core.helper.ListHelper;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.enums.QueryType;
import com.youlan.common.db.helper.DBHelper;
import com.youlan.tools.config.GeneratorProperties;
import com.youlan.tools.config.ToolsProperties;
import com.youlan.tools.constant.GeneratorConstant;
import com.youlan.tools.entity.DBTable;
import com.youlan.tools.entity.DBTableColumn;
import com.youlan.tools.entity.GeneratorColumn;
import com.youlan.tools.entity.GeneratorTable;
import com.youlan.tools.entity.dto.GeneratorDTO;
import com.youlan.tools.entity.vo.GeneratorCodeVO;
import com.youlan.tools.entity.vo.GeneratorVO;
import com.youlan.tools.service.DBTableColumnService;
import com.youlan.tools.service.DBTableService;
import com.youlan.tools.service.GeneratorColumnService;
import com.youlan.tools.service.GeneratorTableService;
import com.youlan.tools.utils.GeneratorUtil;
import lombok.AllArgsConstructor;
import org.apache.velocity.VelocityContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

import static com.youlan.common.db.constant.DBConstant.*;
import static com.youlan.tools.constant.GeneratorConstant.*;

@Service
@AllArgsConstructor
public class GeneratorBizService {
    private final GeneratorTableService generatorTableService;
    private final DBTableService dbTableService;
    private final DBTableColumnService dbTableColumnService;
    private final ToolsProperties toolsProperties;
    private final GeneratorColumnService generatorColumnService;

    /**
     * 查询生成表详情
     */
    public GeneratorVO load(Long tableId) {
        GeneratorTable generatorTable = generatorTableService.loadOneOpt(tableId)
                .orElseThrow(() -> new BizRuntimeException("生成表信息不存在"));

        List<GeneratorColumn> generatorColumnList = generatorColumnService.loadMore(GeneratorColumn::getTableId, generatorTable.getId());
        return new GeneratorVO().setGeneratorTable(generatorTable)
                .setGeneratorColumnList(generatorColumnList);
    }

    /**
     * 查询数据库表分页
     */
    public IPage<DBTable> getDbTablePageList(DBTable dbTable) {
        return generatorTableService.getBaseMapper()
                .getDbTablePageList(DBHelper.getPage(dbTable), dbTable);
    }

    /**
     * 导入输入库表集合
     */
    @Transactional(rollbackFor = Exception.class)
    public void importDbTableList(List<String> tableNames) {
        for (String tableName : tableNames) {
            importDbTable(tableName);
        }
    }

    /**
     * 导入输入库表
     */
    @Transactional(rollbackFor = Exception.class)
    public void importDbTable(String tableName) {
        GeneratorTable generatorTable = generatorTableService.loadOne(GeneratorTable::getTableName, tableName);
        //如果当前表存在则走同步逻辑
        if (generatorTable != null) {
            throw new BizRuntimeException(StrUtil.format("{}已存在", tableName));
        }
        //获取数据库表信息
        DBTable dbTable = dbTableService.getDbTable(tableName);
        generatorTable = toGeneratorTable(dbTable);
        generatorTableService.save(generatorTable);
        List<DBTableColumn> dbTableColumnList = dbTableColumnService.getDbTableColumnList(tableName);
        this.importDBColumnList(generatorTable, dbTableColumnList);
    }

    /**
     * 导出数据库表列
     */
    @Transactional(rollbackFor = Exception.class)
    public void importDBColumnList(GeneratorTable generatorTable, List<DBTableColumn> dbTableColumnList) {
        Long tableId = generatorTable.getId();
        List<GeneratorColumn> generatorColumnList = dbTableColumnList.stream()
                .map(dbTableColumn -> GeneratorUtil.getGeneratorColumn(tableId, dbTableColumn))
                .collect(Collectors.toList());
        generatorColumnService.saveBatch(generatorColumnList);
    }

    /**
     * 同步数据库表
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean syncDbTable(Long id) {
        GeneratorTable generatorTable = generatorTableService.getById(id);
        if (generatorTable == null) {
            throw new BizRuntimeException("数据库表不存在");
        }
        return this.syncDbTable(generatorTable);
    }

    /**
     * 同步数据库表
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean syncDbTable(GeneratorTable generatorTable) {
        DBTable dbTable = dbTableService.getDbTable(generatorTable.getTableName());
        generatorTable.setTableComment(dbTable.getTableComment());
        generatorTableService.updateById(generatorTable);
        List<DBTableColumn> dbTableColumnList = dbTableColumnService.getDbTableColumnList(generatorTable.getTableName());
        List<GeneratorColumn> generatorColumns = generatorColumnService.loadMore(GeneratorColumn::getTableId, generatorTable.getId());
        Map<String, GeneratorColumn> generatorColumnMap = generatorColumns.stream()
                .collect(Collectors.toMap(GeneratorColumn::getColumnName, Function.identity()));
        List<GeneratorColumn> newGeneratorColumns = dbTableColumnList.stream()
                .map(dbColumn -> {
                    GeneratorColumn generatorColumn = generatorColumnMap.get(dbColumn.getColumnName());
                    // 旧数据中不存在此列则需要创建
                    if (ObjectUtil.isNull(generatorColumn)) {
                        return GeneratorUtil.getGeneratorColumn(generatorTable.getId(), dbColumn);
                    }
                    // 如果旧数据中存在此列仅更新部分字段
                    return GeneratorUtil.syncGeneratorColumn(generatorColumn, dbColumn);
                })
                .collect(Collectors.toList());
        generatorColumnService.remove(Wrappers.<GeneratorColumn>lambdaQuery().eq(GeneratorColumn::getTableId, generatorTable.getId()));
        return generatorColumnService.saveBatch(newGeneratorColumns);
    }

    /**
     * 数据库表信息转为生成表信息
     */
    public GeneratorTable toGeneratorTable(DBTable dbTable) {
        String tableName = dbTable.getTableName();
        List<String> tableMatchPrefix = generatorProperties().getTableMatchPrefix();
        String entityName = NamingCase.toPascalCase(GeneratorUtil.tableNameRemovePrefix(tableName, tableMatchPrefix));
        String bizName = NamingCase.toCamelCase(GeneratorUtil.tableNameRemovePrefix(tableName, tableMatchPrefix));
        String moduleName = GeneratorUtil.packageName2ModuleName(generatorProperties().getPackageName());
        String featureName = GeneratorUtil.tableComment2FeatureName(dbTable.getTableComment(), generatorProperties().getTableFeatureRegex());
        return new GeneratorTable()
                .setTableName(tableName)
                .setTableComment(dbTable.getTableComment())
                .setFeatureName(featureName)
                .setModuleName(moduleName)
                .setBizName(bizName)
                .setEntityName(entityName)
                .setEntityDto(DBConstant.boolean2YesNo(generatorProperties().isNeedEntityDto()))
                .setEntityPageDto(DBConstant.boolean2YesNo(generatorProperties().isNeedEntityPageDto()))
                .setEntityVo(DBConstant.boolean2YesNo(generatorProperties().isNeedEntityVo()))
                .setPackageName(generatorProperties().getPackageName());
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(GeneratorDTO dto) {
        //如果指定了表为树表，则树表列不能与树表父列一致
        GeneratorTable generatorTable = dto.getGeneratorTable();
        String templateType = generatorTable.getTemplateType();
        if (TEMPLATE_TYPE_TREE.equals(templateType)) {
            Assert.notEquals(generatorTable.getColumnName(), generatorTable.getParentColumnName(), () -> new BizRuntimeException("树表列不能和树表父列一致"));
        }
        generatorTableService.updateById(dto.getGeneratorTable());
        generatorColumnService.updateBatchById(dto.getGeneratorColumnList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void remove(List<Long> tableIds) {
        generatorTableService.removeBatchByIds(tableIds);
        Wrapper<GeneratorColumn> columnWrapper = new QueryWrapper<GeneratorColumn>()
                .lambda()
                .in(GeneratorColumn::getTableId, tableIds);
        generatorColumnService.remove(columnWrapper);
    }

    /**
     * 预览代码
     */
    public List<GeneratorCodeVO> viewCode(Long tableId) {
        GeneratorVO generatorInfo = load(tableId);
        GeneratorTable generatorTable = generatorInfo.getGeneratorTable();
        List<GeneratorColumn> generatorColumnList = generatorInfo.getGeneratorColumnList();
        return renderCode(generatorTable, generatorColumnList);
    }

    /**
     * 下载代码
     */
    public byte[] downloadCode(List<Long> tableIdList) {
        //如果不指定则下载所有
        if (CollectionUtil.isEmpty(tableIdList)) {
            tableIdList = generatorTableService.lambdaQuery()
                    .select(GeneratorTable::getId)
                    .list()
                    .stream()
                    .map(GeneratorTable::getId)
                    .collect(Collectors.toList());
        }
        final ByteArrayOutputStream zipBos = new ByteArrayOutputStream();
        final ZipOutputStream zipOs = new ZipOutputStream(zipBos);
        tableIdList.forEach(tableId -> {
            GeneratorVO generatorInfo = load(tableId);
            GeneratorTable generatorTable = generatorInfo.getGeneratorTable();
            List<GeneratorColumn> generatorColumnList = generatorInfo.getGeneratorColumnList();
            List<GeneratorCodeVO> generatorCodes = renderCode(generatorTable, generatorColumnList);
            generatorCodes.forEach(generatorCode -> {
                String packageName = generatorCode.getPackageName();
                String codeName = generatorCode.getCodeName();
                String codeContent = generatorCode.getCodeContent();
                GeneratorUtil.writeToZip(packageName, codeName, codeContent, zipOs);
            });
        });
        IoUtil.close(zipOs);
        return zipBos.toByteArray();
    }

    /**
     * 生成代码
     */
    public void writeCode(List<Long> tableIdList) {
        tableIdList.forEach(tableId -> {
            GeneratorVO generatorInfo = load(tableId);
            GeneratorTable generatorTable = generatorInfo.getGeneratorTable();
            List<GeneratorColumn> generatorColumnList = generatorInfo.getGeneratorColumnList();
            String homePath = FileUtil.getAbsolutePath(generatorTable.getGeneratorPath());
            List<GeneratorCodeVO> generatorCodes = renderCode(generatorTable, generatorColumnList);
            generatorCodes.forEach(generatorCode -> {
                String packageName = generatorCode.getPackageName();
                String codeName = generatorCode.getCodeName();
                String codeContent = generatorCode.getCodeContent();
                GeneratorUtil.writeToPath(homePath, packageName, codeName, codeContent);
            });
        });
    }

    /**
     * 渲染代码
     */
    public List<GeneratorCodeVO> renderCode(GeneratorTable generatorTable, List<GeneratorColumn> generatorColumnList) {
        List<GeneratorCodeVO> generatorCodes = new ArrayList<>();
        //生成模版上下文
        VelocityContext velocityContext = generateContext(generatorTable, generatorColumnList);
        String moduleName = generatorTable.getModuleName();
        String bizName = generatorTable.getBizName();
        String packageName = generatorTable.getPackageName();
        String entityName = generatorTable.getEntityName();
        //生成entity
        String entity = GeneratorUtil.mergeTemplate(TPL_JAVA_ENTITY, velocityContext);
        String entityPackage = PACKAGE_NAME_SUFFIX_JAVA + packageName + PACKAGE_NAME_SUFFIX_ENTITY;
        generatorCodes.add(new GeneratorCodeVO(entity, entityPackage, TPL_JAVA_ENTITY, entityName + FILE_NAME_SUFFIX_ENTITY));
        //生成dto
        if (yesNo2Boolean(generatorTable.getEntityDto())) {
            String entityDto = GeneratorUtil.mergeTemplate(TPL_JAVA_ENTITY_DTO, velocityContext);
            String entityDtoPackageName = PACKAGE_NAME_SUFFIX_JAVA + packageName + PACKAGE_NAME_SUFFIX_ENTITY_DTO;
            String entityDtoName = entityName + FILE_NAME_SUFFIX_DTO;
            generatorCodes.add(new GeneratorCodeVO(entityDto, entityDtoPackageName, TPL_JAVA_ENTITY_DTO, entityDtoName));
        }
        //生成page dto
        if (yesNo2Boolean(generatorTable.getEntityPageDto())) {
            String entityPageDto = GeneratorUtil.mergeTemplate(TPL_JAVA_ENTITY_PAGE_DTO, velocityContext);
            String entityPageDtoPackageName = PACKAGE_NAME_SUFFIX_JAVA + packageName + PACKAGE_NAME_SUFFIX_ENTITY_DTO;
            String entityPageDtoName = entityName + FILE_NAME_SUFFIX_PAGE_DTO;
            generatorCodes.add(new GeneratorCodeVO(entityPageDto, entityPageDtoPackageName, TPL_JAVA_ENTITY_PAGE_DTO, entityPageDtoName));
        }
        //生成vo
        if (yesNo2Boolean(generatorTable.getEntityVo())) {
            String entityVo = GeneratorUtil.mergeTemplate(TPL_JAVA_ENTITY_VO, velocityContext);
            String entityVoPackageName = PACKAGE_NAME_SUFFIX_JAVA + packageName + PACKAGE_NAME_SUFFIX_ENTITY_VO;
            String entityVoName = entityName + FILE_NAME_SUFFIX_VO;
            generatorCodes.add(new GeneratorCodeVO(entityVo, entityVoPackageName, TPL_JAVA_ENTITY_VO, entityVoName));
        }
        //生成mapper
        String mapper = GeneratorUtil.mergeTemplate(TPL_JAVA_MAPPER, velocityContext);
        String mapperPackageName = PACKAGE_NAME_SUFFIX_JAVA + packageName + PACKAGE_NAME_SUFFIX_MAPPER;
        String mapperName = entityName + FILE_NAME_SUFFIX_MAPPER;
        generatorCodes.add(new GeneratorCodeVO(mapper, mapperPackageName, TPL_JAVA_MAPPER, mapperName));
        //生成service
        String service = GeneratorUtil.mergeTemplate(TPL_JAVA_SERVICE, velocityContext);
        String servicePackageName = PACKAGE_NAME_SUFFIX_JAVA + packageName + PACKAGE_NAME_SUFFIX_SERVICE;
        String serviceName = entityName + FILE_NAME_SUFFIX_SERVICE;
        generatorCodes.add(new GeneratorCodeVO(service, servicePackageName, TPL_JAVA_SERVICE, serviceName));
        //生成controller
        String controller = GeneratorUtil.mergeTemplate(TPL_JAVA_CONTROLLER, velocityContext);
        String controllerPackageName = PACKAGE_NAME_SUFFIX_JAVA + packageName + PACKAGE_NAME_SUFFIX_CONTROLLER;
        String controllerName = entityName + FILE_NAME_SUFFIX_CONTROLLER;
        generatorCodes.add(new GeneratorCodeVO(controller, controllerPackageName, TPL_JAVA_CONTROLLER, controllerName));
        //生成mapperXml
        String mapperXml = GeneratorUtil.mergeTemplate(TPL_XML_MAPPER, velocityContext);
        String mapperXmlPackageName = PACKAGE_NAME_SUFFIX_RESOURCES + packageName + PACKAGE_NAME_SUFFIX_XML;
        String mapperXmlName = entityName + FILE_NAME_SUFFIX_MAPPER_XML;
        generatorCodes.add(new GeneratorCodeVO(mapperXml, mapperXmlPackageName, TPL_XML_MAPPER, mapperXmlName));
        //生成菜单SQL
        if (StrUtil.isNotBlank(generatorTable.getParentMenuId())) {
            String menuSql = GeneratorUtil.mergeTemplate(TPL_MENU_SQL, velocityContext);
            String menuSqlName = bizName + FILE_NAME_SUFFIX_MENU_SQL;
            generatorCodes.add(new GeneratorCodeVO(menuSql, PACKAGE_NAME_MENU_SQL, TPL_MENU_SQL, menuSqlName));
        }
        //生成api.js
        String jsApi = GeneratorUtil.mergeTemplate(TPL_JS_API, velocityContext);
        String jsApiPackageName = PACKAGE_NAME_PREFIX_VUE_API + moduleName;
        String jsApiName = bizName + FILE_NAME_SUFFIX_JS;
        generatorCodes.add(new GeneratorCodeVO(jsApi, jsApiPackageName, TPL_JS_API, jsApiName));
        //生成index.vue
        String vueIndex = GeneratorUtil.mergeTemplate(TPL_VUE_INDEX, velocityContext);
        String vueIndexPackageName = PACKAGE_NAME_PREFIX_VUE_INDEX + moduleName + StrPool.DOT + bizName;
        generatorCodes.add(new GeneratorCodeVO(vueIndex, vueIndexPackageName, TPL_VUE_INDEX, FILE_NAME_VUE_INDEX));
        return generatorCodes;
    }


    public VelocityContext generateContext(GeneratorTable generatorTable, List<GeneratorColumn> generatorColumnList) {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("templateType", generatorTable.getTemplateType());
        velocityContext.put("tableName", generatorTable.getTableName());
        velocityContext.put("tableComment", generatorTable.getTableComment());
        velocityContext.put("EntityName", generatorTable.getEntityName());
        velocityContext.put("packageName", generatorTable.getPackageName());
        velocityContext.put("bizName", generatorTable.getBizName());
        velocityContext.put("BizName", StrUtil.upperFirst(generatorTable.getBizName()));
        velocityContext.put("moduleName", generatorTable.getModuleName());
        velocityContext.put("featureName", generatorTable.getFeatureName());
        velocityContext.put("columns", generatorColumnList);
        velocityContext.put("createId", ObjectUtil.defaultIfNull(generatorTable.getCreateId(), 100L));
        velocityContext.put("createBy", StrUtil.blankToDefault(generatorTable.getCreateBy(), "admin"));
        velocityContext.put("parentMenuId", generatorTable.getParentMenuId());
        velocityContext.put("menuIcon", StrUtil.blankToDefault(generatorTable.getMenuIcon(), StrUtil.EMPTY));
        //设置首字母小写实体类名
        String entityName = StrUtil.lowerFirst(generatorTable.getEntityName());
        velocityContext.put("entityName", entityName);
        //查找主键列
        GeneratorColumn pkGeneratorColumn = GeneratorUtil.getPkGeneratorColumn(generatorColumnList);
        velocityContext.put("pkColumn", pkGeneratorColumn);
        velocityContext.put("pkColumnComment", pkGeneratorColumn.getColumnComment());
        velocityContext.put("pkJavaField", StrUtil.toCamelCase(pkGeneratorColumn.getColumnName()));
        velocityContext.put("PkJavaField", NamingCase.toPascalCase(pkGeneratorColumn.getColumnName()));
        //判断是否需要DTO
        velocityContext.put("needDto", yesNo2Boolean(generatorTable.getEntityDto()));
        //判断是否需要VO
        velocityContext.put("needVo", yesNo2Boolean(generatorTable.getEntityVo()));
        //判断是否需要分页DTO
        velocityContext.put("needPageDto", yesNo2Boolean(generatorTable.getEntityPageDto()));
        //设置实体类里面要生成的列
        velocityContext.put("entityColumns", generatorEntityColumnList(generatorTable, generatorColumnList));
        //设置DTO里面要生成的列
        velocityContext.put("dtoColumns", generatorDtoColumnList(generatorTable, generatorColumnList));
        //设置PageDTO里面要生成的列
        List<GeneratorColumn> pageDtoColumns = generatorPageDtoColumnList(generatorTable, generatorColumnList);
        velocityContext.put("pageDtoColumns", pageDtoColumns);
        //设置搜索框里面要生成的列
        velocityContext.put("queryColumns", ListHelper.filterList(pageDtoColumns, column -> VAL_YES.equals(column.getIsQuery())));
        //设置表格里面要生成的列
        velocityContext.put("tableColumns", ListHelper.filterList(generatorColumnList, column -> VAL_YES.equals(column.getIsTable())));
        //设置编辑里面要生成的列
        velocityContext.put("editColumns", ListHelper.filterList(generatorColumnList, column -> VAL_YES.equals(column.getIsEdit())));
        //设置VO里面要生成的列
        velocityContext.put("voColumns", generatorVoColumnList(generatorTable, generatorColumnList));
        //树表列首字母大写驼峰Java字段
        velocityContext.put("TreeField", NamingCase.toPascalCase(generatorTable.getColumnName()));
        //树表父列首字母大写驼峰Java字段
        velocityContext.put("ParentTreeField", NamingCase.toPascalCase(generatorTable.getParentColumnName()));
        //树表排序列首字母大写驼峰Java字段
        if (StrUtil.isNotBlank(generatorTable.getSortColumnName())) {
            velocityContext.put("SortTreeField", NamingCase.toPascalCase(generatorTable.getSortColumnName()));
        }
        // Entity需要导入的包
        velocityContext.put("entityPackages", GeneratorUtil.getEntityPackages());
        // DTO需要导入的包
        velocityContext.put("dtoPackages", GeneratorUtil.getDtoPackages());
        // PageDTO需要导入的包
        velocityContext.put("pageDtoPackages", GeneratorUtil.getPageDtoPackages());
        // VO需要导入的包
        velocityContext.put("voPackages", GeneratorUtil.getVoPackages());
        // 需要导入的基础包
        velocityContext.put("basePackages", GeneratorUtil.getBasePackages());
        // 供菜单SQL使用的菜单ID，每次自增50个且唯一，避免重复
        List<Long> menuIds = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            menuIds.add(DBHelper.identifierGenerator().nextId(null).longValue());
        }
        velocityContext.put("menuIds", menuIds);
        return velocityContext;
    }

    /**
     * 生成VO列
     */
    public List<GeneratorColumn> generatorVoColumnList(GeneratorTable generatorTable, List<GeneratorColumn> allColumn) {
        List<GeneratorColumn> voColumnList = new ArrayList<>();
        for (GeneratorColumn generatorColumn : allColumn) {
            GeneratorColumn voColumn = generatorColumn.copy();
            //如果是要排除的列或者不是表格中的字段则不处理
            if (generatorProperties().inViewColumnExclude(voColumn.getColumnName()) || !yesNo2Boolean(voColumn.getIsTable())) {
                continue;
            }
            //设置swagger注解
            setApiModelPropertyAnno(voColumn);
            //设置excel注解
            setExcelAnno(voColumn);
            voColumnList.add(voColumn);
        }
        return voColumnList;
    }

    /**
     * 生成PageDTO列
     */
    public List<GeneratorColumn> generatorPageDtoColumnList(GeneratorTable generatorTable, List<GeneratorColumn> allColumn) {
        List<GeneratorColumn> pageDtoColumnList = new ArrayList<>();
        for (GeneratorColumn generatorColumn : allColumn) {
            GeneratorColumn pageDtoColumn = generatorColumn.copy();
            //如果是要排除的列或者不是查询字段则不处理
            if (generatorProperties().inQueryColumnExclude(pageDtoColumn.getColumnName()) || !DBConstant.yesNo2Boolean(generatorColumn.getIsQuery())) {
                continue;
            }
            //设置swagger注解
            setApiModelPropertyAnno(pageDtoColumn);
            //设置查询注解,可能会生成衍生列
            setQueryAnno(pageDtoColumn, pageDtoColumnList);
            pageDtoColumnList.add(pageDtoColumn);
        }
        return pageDtoColumnList;
    }

    /**
     * 生成DTO列
     */
    public List<GeneratorColumn> generatorDtoColumnList(GeneratorTable generatorTable, List<GeneratorColumn> allColumn) {
        List<GeneratorColumn> dtoColumnList = new ArrayList<>();
        for (GeneratorColumn generatorColumn : allColumn) {
            GeneratorColumn dtoColumn = generatorColumn.copy();
            //如果是要排除的列或者不是编辑字段则不生成
            if (generatorProperties().inEditColumnExclude(dtoColumn.getColumnName()) || !DBConstant.yesNo2Boolean(generatorColumn.getIsEdit())) {
                continue;
            }
            //设置swagger注解
            setApiModelPropertyAnno(dtoColumn);
            //设置验证注解
            setValidatorAnno(dtoColumn);
            dtoColumnList.add(dtoColumn);
        }
        return dtoColumnList;
    }

    /**
     * 生成Entity列
     */
    public List<GeneratorColumn> generatorEntityColumnList(GeneratorTable generatorTable, List<GeneratorColumn> allColumn) {
        List<GeneratorColumn> entityColumnList = new ArrayList<>();
        for (GeneratorColumn generatorColumn : allColumn) {
            GeneratorColumn entityColumn = generatorColumn.copy();
            //数据库实体设置@TableField
            setTableFieldAnno(entityColumn);
            //设置swagger注解
            setApiModelPropertyAnno(entityColumn);
            //如果不需要DTO则数据库实体需要承担编辑责任
            if (!yesNo2Boolean(generatorTable.getEntityDto())) {
                setValidatorAnno(entityColumn);
            }
            //如果不需要PageDTO则数据库实体需要承担查询责任
            if (!yesNo2Boolean(generatorTable.getEntityPageDto())) {
                //查询方法可能会衍生出新的列所以将待返回列也附带进去
                setQueryAnno(entityColumn, entityColumnList);
            }
            //如果不需要VO则数据库实体需要承担页面数据返回责任
            if (!yesNo2Boolean(generatorTable.getEntityVo())) {
                setExcelAnno(entityColumn);
            }
            entityColumnList.add(entityColumn);
        }
        return entityColumnList;
    }

    /**
     * 生成查询注解
     */
    public void setQueryAnno(GeneratorColumn generatorColumn, List<GeneratorColumn> extendColumnList) {
        QueryType queryType = QueryType.valueOf(generatorColumn.getQueryType());
        switch (queryType) {
            //当查询类型是between in not_in时强制生成自定义列来接收范围值
            case BETWEEN:
            case IN:
            case NOT_IN:
                GeneratorColumn extendColumn = generatorColumn.copy();
                extendColumn
                        .setJavaField(extendColumn.getJavaField() + "Range")
                        .setValidatorAnnoList(null)
                        .setIsCollection(VAL_YES)
                        .setTableFieldAnno(GeneratorUtil.getTableFieldNotExistAnno());
                extendColumn.setQueryAnno(GeneratorUtil.getQueryTypeAnnoWithColumn(extendColumn.getColumnName(), queryType.name()));
                setValidatorAnno(extendColumn);
                extendColumnList.add(extendColumn);
                //将原列的验证注解设为空，isQuery设置为否
                generatorColumn.setQueryAnno(null);
                generatorColumn.setIsQuery(VAL_NO);
                break;
            default:
                generatorColumn.setQueryAnno(GeneratorUtil.getQueryTypeAnno(queryType.name()));
                break;
        }
    }

    /**
     * 生成excel住家
     */
    public void setExcelAnno(GeneratorColumn generatorColumn) {
        String columnComment = generatorColumn.getColumnComment();
        String typeKey = generatorColumn.getTypeKey();
        List<String> excelAnno = new ArrayList<>();
        boolean isDictType = StrUtil.isNotBlank(typeKey);
        if (isDictType) {
            excelAnno.add(GeneratorUtil.getExcelDictProperty(columnComment, typeKey));
        } else {
            excelAnno.add(GeneratorUtil.getExcelProperty(columnComment));
        }
        if (CollectionUtil.isNotEmpty(excelAnno)) {
            generatorColumn.setExcelAnnoList(excelAnno);
        }
    }

    /**
     * 生成验证注解
     */
    public void setValidatorAnno(GeneratorColumn generatorColumn) {
        List<String> validatorAnnoList = new ArrayList<>();
        String columnComment = generatorColumn.getColumnComment();
        //此处要判断当前列是否为主键，如果是主键的话放开校验
        if (DBConstant.yesNo2Boolean(generatorColumn.getIsPk())) {
            return;
        }
        //判断基本类型
        if (DBConstant.yesNo2Boolean(generatorColumn.getIsRequired())) {
            if (GeneratorConstant.JAVA_TYPE_STRING.equals(generatorColumn.getJavaType())) {
                validatorAnnoList.add(GeneratorUtil.getNotBlankValidatorAnno(columnComment));
            } else {
                validatorAnnoList.add(GeneratorUtil.getNotNullValidatorAnno(columnComment));
            }
        }
        //只有不为空时才能设置值，否则会影响显示
        if (CollectionUtil.isNotEmpty(validatorAnnoList)) {
            generatorColumn.setValidatorAnnoList(validatorAnnoList);
        }
    }

    /**
     * 生成{@link com.baomidou.mybatisplus.annotation.TableField}注解
     */
    public void setTableFieldAnno(GeneratorColumn generatorColumn) {
        String columnName = generatorColumn.getColumnName();
        String isPk = generatorColumn.getIsPk();
        switch (columnName) {
            case COL_STATUS:
            case COL_CREATE_BY:
            case COL_CREATE_ID:
            case COL_CREATE_TIME:
                generatorColumn.setTableFieldAnno(GeneratorUtil.getTableFieldInsertAnno());
                break;
            case COL_UPDATE_ID:
            case COL_UPDATE_BY:
            case COL_UPDATE_TIME:
                generatorColumn.setTableFieldAnno(GeneratorUtil.getTableFieldUpdateAnno());
                break;
            case COL_STS:
                generatorColumn.setTableFieldAnno(GeneratorUtil.getTableLogicAnno());
            default:
                break;
        }
        if (yesNo2Boolean(isPk)) {
            if (yesNo2Boolean(generatorColumn.getIsIncrement())) {
                generatorColumn.setTableFieldAnno(GeneratorUtil.getTableIdAutoAnno());
            } else {
                generatorColumn.setTableFieldAnno(GeneratorUtil.getTableIdAnno());
            }
        }
    }

    /**
     * 生成{@link io.swagger.annotations.ApiModelProperty}注解
     */
    public void setApiModelPropertyAnno(GeneratorColumn generatorColumn) {
        String columnName = generatorColumn.getColumnName();
        if (COL_DESC_FIELD_NAME_MAPPING.containsKey(columnName)) {
            String constantName = COL_DESC_FIELD_NAME_MAPPING.get(columnName);
            generatorColumn.setApiModelPropertyAnno(GeneratorUtil.getSchemaAnnoFromDBConstant(constantName));
        } else {
            generatorColumn.setApiModelPropertyAnno(GeneratorUtil.getSchemaAnno(generatorColumn.getColumnComment()));
        }
    }

    public GeneratorProperties generatorProperties() {
        return toolsProperties.getGenerator();
    }
}
