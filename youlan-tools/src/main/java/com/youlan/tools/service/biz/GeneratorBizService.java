package com.youlan.tools.service.biz;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.NamingCase;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youlan.common.core.db.constant.DBConstant;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.db.enums.QueryType;
import com.youlan.tools.config.GeneratorProperties;
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
import com.youlan.tools.utils.VelocityUtil;
import lombok.AllArgsConstructor;
import org.apache.velocity.VelocityContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.youlan.common.core.db.constant.DBConstant.*;
import static com.youlan.tools.constant.GeneratorConstant.*;

@Service
@AllArgsConstructor
public class GeneratorBizService {
    private final GeneratorTableService generatorTableService;
    private final DBTableService dbTableService;
    private final DBTableColumnService dbTableColumnService;
    private final GeneratorProperties generatorProperties;
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
     * 查询数据库表
     */
    public List<DBTable> getDbTableList(DBTable dbTable) {
        List<DBTable> dbTableList = generatorTableService.getBaseMapper().getDbTableList(dbTable);
        return dbTableList.stream()
                .filter(table -> !generatorProperties.getTableExclude().contains(table.getTableName()))
                .collect(Collectors.toList());
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
        List<GeneratorColumn> generatorColumnList = dbTableColumnList.stream().map(dbTableColumn -> {
                    String columnType = dbTableColumn.getColumnType();
                    String columnKey = dbTableColumn.getColumnKey();
                    String extra = dbTableColumn.getExtra();
                    String isNullable = dbTableColumn.getIsNullable();
                    GeneratorColumn generatorColumn = new GeneratorColumn()
                            .setTableId(tableId)
                            .setColumnName(dbTableColumn.getColumnName())
                            .setColumnComment(dbTableColumn.getColumnComment())
                            .setColumnType(columnType)
                            .setJavaField(StrUtil.toCamelCase(dbTableColumn.getColumnName()))
                            .setIsPk(DBConstant.boolean2YesNo(GeneratorUtil.columnIsPk(columnKey)))
                            .setIsIncrement(DBConstant.boolean2YesNo(GeneratorUtil.columnIsIncrement(extra)))
                            .setIsRequired(DBConstant.boolean2YesNo(GeneratorUtil.columnIsRequired(isNullable)))
                            .setIsEdit(DBConstant.VAL_YES)
                            .setIsTable(DBConstant.VAL_YES)
                            .setIsQuery(DBConstant.VAL_YES)
                            .setTypeKey(null);
                    GeneratorUtil.setJavaTypeComponentType(generatorColumn);
                    return generatorColumn;
                }
        ).collect(Collectors.toList());
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
                    if (generatorColumn == null) {
                        return null;
                    }
                    return generatorColumn.setColumnComment(dbColumn.getColumnComment())
                            .setColumnType(dbColumn.getColumnType())
                            .setJavaType(GeneratorConstant.JAVA_TYPE_STRING)
                            .setJavaField(StrUtil.toCamelCase(dbColumn.getColumnName()))
                            .setIsPk(DBConstant.boolean2YesNo(GeneratorUtil.columnIsPk(dbColumn.getColumnKey())))
                            .setIsIncrement(DBConstant.boolean2YesNo(GeneratorUtil.columnIsIncrement(dbColumn.getExtra())))
                            .setIsRequired(DBConstant.boolean2YesNo(GeneratorUtil.columnIsRequired(dbColumn.getIsNullable())));
                }).collect(Collectors.toList());
        generatorColumnService.remove(Wrappers.<GeneratorColumn>lambdaQuery().eq(GeneratorColumn::getTableId, generatorTable.getId()));
        return generatorColumnService.saveBatch(newGeneratorColumns);
    }

    /**
     * 数据库表信息转为生成表信息
     */
    public GeneratorTable toGeneratorTable(DBTable dbTable) {
        String tableName = dbTable.getTableName();
        List<String> tableMatchPrefix = generatorProperties.getTableMatchPrefix();
        String entityName = NamingCase.toPascalCase(GeneratorUtil.tableNameRemovePrefix(tableName, tableMatchPrefix));
        String bizName = NamingCase.toCamelCase(GeneratorUtil.tableNameRemovePrefix(tableName, tableMatchPrefix));
        String moduleName = GeneratorUtil.packageName2ModuleName(generatorProperties.getPackageName());
        String featureName = GeneratorUtil.tableComment2FeatureName(dbTable.getTableComment(), generatorProperties.getTableFeatureRegex());
        return new GeneratorTable()
                .setTableName(tableName)
                .setTableComment(dbTable.getTableComment())
                .setFeatureName(featureName)
                .setModuleName(moduleName)
                .setBizName(bizName)
                .setEntityName(entityName)
                .setEntityDto(DBConstant.boolean2YesNo(generatorProperties.isNeedEntityDto()))
                .setEntityPageDto(DBConstant.boolean2YesNo(generatorProperties.isNeedEntityPageDto()))
                .setEntityVo(DBConstant.boolean2YesNo(generatorProperties.isNeedEntityVo()))
                .setPackageName(generatorProperties.getPackageName());
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(GeneratorDTO dto) {
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
    public GeneratorCodeVO viewCode(Long tableId) {
        GeneratorVO generatorInfo = load(tableId);
        GeneratorTable generatorTable = generatorInfo.getGeneratorTable();
        List<GeneratorColumn> generatorColumnList = generatorInfo.getGeneratorColumnList();
        return renderCode(GeneratorUtil.generateTempHomePath(), generatorTable, generatorColumnList);
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
        //生成临时写入目录
        String homePath = GeneratorUtil.generateTempHomePath();
        tableIdList.forEach(tableId -> {
            GeneratorVO generatorInfo = load(tableId);
            GeneratorTable generatorTable = generatorInfo.getGeneratorTable();
            List<GeneratorColumn> generatorColumnList = generatorInfo.getGeneratorColumnList();
            renderCode(homePath, generatorTable, generatorColumnList);
        });
        File zipFile = ZipUtil.zip(homePath);
        return FileUtil.readBytes(zipFile);
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
            renderCode(homePath, generatorTable, generatorColumnList);
        });
    }

    /**
     * 渲染代码
     */
    public GeneratorCodeVO renderCode(String homePath, GeneratorTable generatorTable, List<GeneratorColumn> generatorColumnList) {
        //生成模版上下文
        VelocityContext velocityContext = generateContext(generatorTable, generatorColumnList);
        String packageName = generatorTable.getPackageName();
        String entityName = generatorTable.getEntityName();
        //生成controller
        String controller = VelocityUtil.mergeTemplate(GeneratorConstant.TPL_JAVA_CONTROLLER, velocityContext);
        String controllerPackageName = packageName + PACKAGE_NAME_SUFFIX_CONTROLLER;
        String controllerName = entityName + FILE_NAME_SUFFIX_CONTROLLER;
        VelocityUtil.writeTemplate(homePath, controllerPackageName, controllerName, controller);
        //生成service
        String service = VelocityUtil.mergeTemplate(GeneratorConstant.TPL_JAVA_SERVICE, velocityContext);
        String servicePackageName = packageName + PACKAGE_NAME_SUFFIX_SERVICE;
        String serviceName = entityName + FILE_NAME_SUFFIX_SERVICE;
        VelocityUtil.writeTemplate(homePath, servicePackageName, serviceName, service);
        //生成mapper
        String mapper = VelocityUtil.mergeTemplate(GeneratorConstant.TPL_JAVA_MAPPER, velocityContext);
        String mapperPackageName = packageName + PACKAGE_NAME_SUFFIX_MAPPER;
        String mapperName = entityName + FILE_NAME_SUFFIX_MAPPER;
        VelocityUtil.writeTemplate(homePath, mapperPackageName, mapperName, mapper);
        //生成xml
        String mapperXml = VelocityUtil.mergeTemplate(GeneratorConstant.TPL_XML_MAPPER, velocityContext);
        String mapperXmlPackageName = packageName + PACKAGE_NAME_SUFFIX_XML;
        String mapperXmlName = entityName + FILE_NAME_SUFFIX_MAPPER_XML;
        VelocityUtil.writeTemplate(homePath, mapperXmlPackageName, mapperXmlName, mapperXml);
        //生成entity
        String entity = VelocityUtil.mergeTemplate(GeneratorConstant.TPL_JAVA_ENTITY, velocityContext);
        String entityPackage = packageName + PACKAGE_NAME_SUFFIX_ENTITY;
        VelocityUtil.writeTemplate(homePath, entityPackage, entityName + FILE_NAME_SUFFIX_ENTITY, entity);
        //生成dto
        String entityDto = null;
        if (yesNo2Boolean(generatorTable.getEntityDto())) {
            entityDto = VelocityUtil.mergeTemplate(TPL_JAVA_ENTITY_DTO, velocityContext);
            String entityDtoPackageName = packageName + PACKAGE_NAME_SUFFIX_ENTITY_DTO;
            String entityDtoName = entityName + FILE_NAME_SUFFIX_DTO;
            VelocityUtil.writeTemplate(homePath, entityDtoPackageName, entityDtoName, entityDto);
        }
        //生成page dto
        String entityPageDto = null;
        if (yesNo2Boolean(generatorTable.getEntityPageDto())) {
            entityPageDto = VelocityUtil.mergeTemplate(TPL_JAVA_ENTITY_PAGE_DTO, velocityContext);
            String entityPageDtoPackageName = packageName + PACKAGE_NAME_SUFFIX_ENTITY_DTO;
            String entityPageDtoName = entityName + FILE_NAME_SUFFIX_PAGE_DTO;
            VelocityUtil.writeTemplate(homePath, entityPageDtoPackageName, entityPageDtoName, entityPageDto);
        }
        //生成vo
        String entityVo = null;
        if (yesNo2Boolean(generatorTable.getEntityVo())) {
            entityVo = VelocityUtil.mergeTemplate(TPL_JAVA_ENTITY_VO, velocityContext);
            String entityVoPackageName = packageName + PACKAGE_NAME_SUFFIX_ENTITY_VO;
            String entityVoName = entityName + FILE_NAME_SUFFIX_VO;
            VelocityUtil.writeTemplate(homePath, entityVoPackageName, entityVoName, entityVo);
        }
        return new GeneratorCodeVO()
                .setController(controller)
                .setService(service)
                .setMapper(mapper)
                .setMapperXml(mapperXml)
                .setEntity(entity)
                .setEntityDto(entityDto)
                .setEntityPageDto(entityPageDto)
                .setEntityVo(entityVo)
                .setBizName(generatorTable.getBizName())
                .setModuleName(generatorTable.getModuleName())
                .setPackageName(generatorTable.getPackageName());
    }


    public VelocityContext generateContext(GeneratorTable generatorTable, List<GeneratorColumn> generatorColumnList) {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("tableName", generatorTable.getTableName());
        velocityContext.put("tableComment", generatorTable.getTableComment());
        velocityContext.put("EntityName", generatorTable.getEntityName());
        velocityContext.put("packageName", generatorTable.getPackageName());
        velocityContext.put("bizName", generatorTable.getBizName());
        velocityContext.put("BizName", StrUtil.upperFirst(generatorTable.getBizName()));
        velocityContext.put("moduleName", generatorTable.getModuleName());
        velocityContext.put("featureName", generatorTable.getFeatureName());
        velocityContext.put("columns", generatorColumnList);
        //设置首字母小写实体类名
        String entityName = StrUtil.lowerFirst(generatorTable.getEntityName());
        velocityContext.put("entityName", entityName);
        //查找主键列
        GeneratorColumn pkGeneratorColumn = GeneratorUtil.getPkGeneratorColumn(generatorColumnList);
        velocityContext.put("pkColumn", pkGeneratorColumn);
        velocityContext.put("pkJavaField", NamingCase.toPascalCase(pkGeneratorColumn.getColumnName()));
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
        velocityContext.put("pageDtoColumns", generatorPageDtoColumnList(generatorTable, generatorColumnList));
        //设置VO里面要生成的列
        velocityContext.put("voColumns", generatorVoColumnList(generatorTable, generatorColumnList));
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
            if (generatorProperties.inViewColumnExclude(voColumn.getColumnName()) || !yesNo2Boolean(voColumn.getIsTable())) {
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
            if (generatorProperties.inQueryColumnExclude(pageDtoColumn.getColumnName()) || !DBConstant.yesNo2Boolean(generatorColumn.getIsQuery())) {
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
            if (generatorProperties.inEditColumnExclude(dtoColumn.getColumnName()) || !DBConstant.yesNo2Boolean(generatorColumn.getIsEdit())) {
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
                extendColumn.setQueryAnno(GeneratorUtil.getQueryTypeAnno(queryType.name()));
                setValidatorAnno(extendColumn);
                extendColumnList.add(extendColumn);
                //将原列的验证注解设为空
                generatorColumn.setQueryAnno(null);
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
        String columnName = generatorColumn.getColumnName();
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
        switch (columnName) {
            case COL_STATUS:
                validatorAnnoList.add(GeneratorUtil.getStatusValidatorAnno());
                break;
            case COL_CREATE_BY:
            case COL_CREATE_ID:
            case COL_CREATE_TIME:
            case COL_UPDATE_ID:
            case COL_UPDATE_BY:
            case COL_UPDATE_TIME:
            default:
                break;
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
}
