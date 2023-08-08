package com.youlan.tools.service.biz;

import cn.hutool.core.bean.BeanUtil;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
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
                            .setIsInsert(DBConstant.VAL_YES)
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
        //预处理列中注解,设置顺序不能乱
        generatorColumnList.forEach(column -> {
            setTableFieldAnno(column);
            setApiModelPropertyAnno(column);
            setValidatorAnno(generatorTable, column);
            setQueryAnno(generatorTable, column);
            setExcelAnno(generatorTable, column);
            setExclude(column);
        });
        return velocityContext;
    }

    /**
     * 标记要排除的列
     */
    public void setExclude(GeneratorColumn generatorColumn) {
        String columnName = generatorColumn.getColumnName();
        if (generatorProperties.inEditColumnExclude(columnName)) {
            generatorColumn.setIsDtoExclude(VAL_YES);
        }
        if (generatorProperties.inViewColumnExclude(columnName)) {
            generatorColumn.setIsVoExclude(VAL_YES);
        }
    }

    /**
     * 生成查询注解
     */
    public void setQueryAnno(GeneratorTable generatorTable, GeneratorColumn generatorColumn) {
        String isQuery = generatorColumn.getIsQuery();
        //不是查询字段则不处理
        if (!yesNo2Boolean(isQuery)) {
            return;
        }
        QueryType queryType = QueryType.valueOf(generatorColumn.getQueryType());
        List<GeneratorColumn> extendColumnList = new ArrayList<>();
        switch (queryType) {
            //当查询类型是between时强制生成自定义列来接收范围值
            case BETWEEN:
                GeneratorColumn extendColumn = BeanUtil.copyProperties(generatorColumn, GeneratorColumn.class);
                extendColumn
                        .setJavaField(extendColumn.getJavaField() + TP_FIELD_SUFFIX_RANGE)
                        .setEntityValidatorAnnoList(null)
                        .setDtoValidatorAnnoList(null)
                        .setIsCollection(VAL_YES)
                        .setTableFieldAnno(TPL_TABLE_FIELD_ANNO_EXIST_FALSE);
                //判断注解生成在哪个实体上
                if (DBConstant.yesNo2Boolean(generatorTable.getEntityPageDto())) {
                    extendColumn.setPageDtoQueryAnno(StrUtil.format(TPL_QUERY_ANNO, QueryType.BETWEEN.name()));
                } else {
                    extendColumn.setEntityQueryAnno(StrUtil.format(TPL_QUERY_ANNO, QueryType.BETWEEN.name()));
                }
                setValidatorAnno(generatorTable, extendColumn);
                extendColumnList.add(extendColumn);
                break;
            //当查询类型是in或者not_in时强制生成自定义列来接收范围值
            case IN:
            case NOT_IN:
                extendColumn = BeanUtil.copyProperties(generatorColumn, GeneratorColumn.class);
                extendColumn
                        .setJavaField(extendColumn.getJavaField() + TP_FIELD_SUFFIX_RANGE)
                        .setEntityValidatorAnnoList(null)
                        .setDtoValidatorAnnoList(null)
                        .setIsCollection(VAL_YES)
                        .setTableFieldAnno(TPL_TABLE_FIELD_ANNO_EXIST_FALSE);
                //判断注解生成在哪个实体上
                if (DBConstant.yesNo2Boolean(generatorTable.getEntityPageDto())) {
                    extendColumn.setPageDtoQueryAnno(StrUtil.format(TPL_QUERY_ANNO, queryType.name()));
                } else {
                    extendColumn.setEntityQueryAnno(StrUtil.format(TPL_QUERY_ANNO, queryType.name()));
                }
                setValidatorAnno(generatorTable, extendColumn);
                extendColumnList.add(extendColumn);
                break;
            default:
                //判断注解生成在哪个实体上
                if (DBConstant.yesNo2Boolean(generatorTable.getEntityPageDto())) {
                    generatorColumn.setPageDtoQueryAnno(StrUtil.format(TPL_QUERY_ANNO, queryType.name()));
                } else {
                    generatorColumn.setEntityQueryAnno(StrUtil.format(TPL_QUERY_ANNO, queryType.name()));
                }
                break;
        }
        generatorColumn.setExtendColumnList(extendColumnList);
        //判断是否需要排除此列
        String columnName = generatorColumn.getColumnName();
        if (generatorProperties.inQueryColumnExclude(columnName)) {
            generatorColumn
                    .setExtendColumnList(null)
                    .setPageDtoQueryAnno(null)
                    .setEntityQueryAnno(null);
        }
    }

    /**
     * 生成excel住家
     */
    public void setExcelAnno(GeneratorTable generatorTable, GeneratorColumn generatorColumn) {
        String columnName = generatorColumn.getColumnName();
        String columnComment = generatorColumn.getColumnComment();
        String typeKey = generatorColumn.getTypeKey();
        List<String> excelAnno = new ArrayList<>();
        boolean isDictType = StrUtil.isNotBlank(typeKey);
        AtomicBoolean isMapping = new AtomicBoolean(false);
        Consumer<String> processExcelProperty = (desc) -> {
            String finalDesc = StrUtil.isBlank(desc) ? columnComment : desc;
            if (isDictType) {
                excelAnno.add(StrUtil.format(TPL_EXCEL_PROPERTY_DICT_ANNO, finalDesc));
                excelAnno.add(StrUtil.format(TPL_EXCEL_PROPERTY_PLUS_DICT_ANNO, typeKey));
            } else if (isMapping.get()) {
                excelAnno.add(StrUtil.format(TPL_EXCEL_PROPERTY_MAPPING_ANNO, finalDesc));
            } else {
                excelAnno.add(StrUtil.format(TPL_EXCEL_PROPERTY_ANNO, finalDesc));
            }
        };

        switch (columnName) {
            //状态列默认生成未MAPPING类型
            case COL_STATUS:
                isMapping.set(true);
                processExcelProperty.accept(DESC_STATUS);
                excelAnno.add(TPL_EXCEL_PROPERTY_PLUS_MAPPING_STATUS_ANNO);
                break;
            case COL_CREATE_ID:
                processExcelProperty.accept(DESC_CREATE_ID);
                break;
            case COL_CREATE_BY:
                processExcelProperty.accept(DESC_CREATE_BY);
                break;
            case COL_CREATE_TIME:
                processExcelProperty.accept(DESC_CREATE_TIME);
                break;
            case COL_UPDATE_ID:
                processExcelProperty.accept(DESC_UPDATE_ID);
                break;
            case COL_UPDATE_BY:
                processExcelProperty.accept(DESC_UPDATE_BY);
                break;
            case COL_UPDATE_TIME:
                processExcelProperty.accept(DESC_UPDATE_TIME);
                break;
            case COL_SORT:
                processExcelProperty.accept(DESC_SORT);
                break;
            case COL_ID:
                processExcelProperty.accept(DESC_ID);
                break;
            case COL_REMARK:
                processExcelProperty.accept(DESC_REMARK);
                break;
            //逻辑删除不展示
            case COL_STS:
                break;
            default:
                processExcelProperty.accept(null);
                break;
        }
        if (CollectionUtil.isNotEmpty(excelAnno)) {
            if (DBConstant.yesNo2Boolean(generatorTable.getEntityVo())) {
                generatorColumn.setVoExcelAnnoList(excelAnno);
            } else {
                generatorColumn.setEntityExcelAnnoList(excelAnno);
            }
        }
    }

    /**
     * 生成验证注解
     */
    public void setValidatorAnno(GeneratorTable generatorTable, GeneratorColumn generatorColumn) {
        List<String> validatorAnnoList = new ArrayList<>();
        String columnName = generatorColumn.getColumnName();
        String columnComment = generatorColumn.getColumnComment();
        //此处要判断当前列是否为主键，如果是主键的话放开校验
        if (DBConstant.yesNo2Boolean(generatorColumn.getIsPk())) {
            return;
        }
        if (DBConstant.yesNo2Boolean(generatorColumn.getIsRequired())) {
            if (GeneratorConstant.JAVA_TYPE_STRING.equals(generatorColumn.getJavaType())) {
                validatorAnnoList.add(StrUtil.format(TPL_VALIDATOR_NOT_BLANK, columnComment + TP_VALIDATOR_SUFFIX_NOT_NULL));
            } else {
                validatorAnnoList.add(StrUtil.format(TPL_VALIDATOR_NOT_NULL, columnComment + TP_VALIDATOR_SUFFIX_NOT_NULL));
            }
        }
        switch (columnName) {
            case COL_STATUS:
                validatorAnnoList.add(StrUtil.format(TPL_VALIDATOR_STATUS));
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
            if (DBConstant.yesNo2Boolean(generatorTable.getEntityDto())) {
                generatorColumn.setDtoValidatorAnnoList(validatorAnnoList);
            } else {
                generatorColumn.setEntityValidatorAnnoList(validatorAnnoList);
            }
        }
        //判断是否需要排除此列
        if (generatorProperties.inEditColumnExclude(columnName)) {
            generatorColumn
                    .setEntityValidatorAnnoList(null);
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
                generatorColumn.setTableFieldAnno(TPL_TABLE_FIELD_ANNO_INSERT);
                break;
            case COL_UPDATE_ID:
            case COL_UPDATE_BY:
            case COL_UPDATE_TIME:
                generatorColumn.setTableFieldAnno(TPL_TABLE_FIELD_ANNO_UPDATE);
                break;
            case COL_STS:
                generatorColumn.setTableFieldAnno(TPL_TABLE_LOGIC_ANNO);
            default:
                break;
        }
        if (yesNo2Boolean(isPk)) {
            if (yesNo2Boolean(generatorColumn.getIsIncrement())) {
                generatorColumn.setTableFieldAnno(TPL_TABLE_ID_ANNO_TYPE_AUTO);
            } else {
                generatorColumn.setTableFieldAnno(TPL_TABLE_ID_ANNO_TYPE_DEFAULT);
            }
        }
    }

    /**
     * 生成{@link io.swagger.annotations.ApiModelProperty}注解
     */
    public void setApiModelPropertyAnno(GeneratorColumn generatorColumn) {
        String columnName = generatorColumn.getColumnName();
        String apiModelPropertyAnno;
        switch (columnName) {
            case COL_ID:
                apiModelPropertyAnno = StrUtil.format(TPL_SCHEMA_ANNO_CONSTANT, "DESC_ID");
                break;
            case COL_STATUS:
                apiModelPropertyAnno = StrUtil.format(TPL_SCHEMA_ANNO_CONSTANT, "DESC_STATUS");
                break;
            case COL_CREATE_BY:
                apiModelPropertyAnno = StrUtil.format(TPL_SCHEMA_ANNO_CONSTANT, "DESC_CREATE_BY");
                break;
            case COL_CREATE_ID:
                apiModelPropertyAnno = StrUtil.format(TPL_SCHEMA_ANNO_CONSTANT, "DESC_CREATE_ID");
                break;
            case COL_UPDATE_ID:
                apiModelPropertyAnno = StrUtil.format(TPL_SCHEMA_ANNO_CONSTANT, "DESC_UPDATE_ID");
                break;
            case COL_UPDATE_BY:
                apiModelPropertyAnno = StrUtil.format(TPL_SCHEMA_ANNO_CONSTANT, "DESC_UPDATE_BY");
                break;
            case COL_CREATE_TIME:
                apiModelPropertyAnno = StrUtil.format(TPL_SCHEMA_ANNO_CONSTANT, "DESC_CREATE_TIME");
                break;
            case COL_UPDATE_TIME:
                apiModelPropertyAnno = StrUtil.format(TPL_SCHEMA_ANNO_CONSTANT, "DESC_UPDATE_TIME");
                break;
            case COL_STS:
                apiModelPropertyAnno = StrUtil.format(TPL_SCHEMA_ANNO_CONSTANT, "DESC_STS");
                break;
            case COL_SORT:
                apiModelPropertyAnno = StrUtil.format(TPL_SCHEMA_ANNO_CONSTANT, "DESC_SORT");
                break;
            case COL_REMARK:
                apiModelPropertyAnno = StrUtil.format(TPL_SCHEMA_ANNO_CONSTANT, "DESC_REMARK");
                break;
            default:
                apiModelPropertyAnno = StrUtil.format(TPL_SCHEMA_ANNO_NORMAL, generatorColumn.getColumnComment());
                break;
        }
        generatorColumn.setApiModelPropertyAnno(apiModelPropertyAnno);
    }
}
