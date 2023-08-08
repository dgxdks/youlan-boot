package com.youlan.tools.constant;

import static com.youlan.common.core.db.constant.DBConstant.*;

public class GeneratorConstant {
    // ******************** 组件类型 ********************
    /**
     * 输入框
     */
    public static final String COMPONENT_TYPE_INPUT = "INPUT";
    /**
     * 文本域
     */
    public static final String COMPONENT_TYPE_TEXTAREA = "TEXTAREA";
    /**
     * 下拉框
     */
    public static final String COMPONENT_TYPE_SELECT = "SELECT";
    /**
     * 复选框
     */
    public static final String COMPONENT_TYPE_CHECKBOX = "CHECKBOX";
    /**
     * 单选框
     */
    public static final String COMPONENT_TYPE_RADIO = "RADIO";
    /**
     * 日期选择器
     */
    public static final String COMPONENT_TYPE_DATE = "DATE";
    /**
     * 时间选择器
     */
    public static final String COMPONENT_TYPE_DATETIME = "DATETIME";
    /**
     * 日期范围选择器
     */
    public static final String COMPONENT_TYPE_DATE_RANGE = "DATE_RANGE";
    /**
     * 时间范围选择器
     */
    public static final String COMPONENT_TYPE_DATETIME_RANGE = "DATETIME_RANGE";
    /**
     * 图片上传
     */
    public static final String COMPONENT_TYPE_IMAGE_UPLOAD = "IMAGE_UPLOAD";
    /**
     * 图片展示
     */
    public static final String COMPONENT_TYPE_IMAGE_SHOW = "IMAGE_SHOW";
    /**
     * 文件上传
     */
    public static final String COMPONENT_TYPE_FILE_UPLOAD = "FILE_UPLOAD";
    /**
     * 富文本编辑器
     */
    public static final String COMPONENT_TYPE_RICH_TEXT = "RICH_TEXT";

    // ******************** Java类型 ********************
    public static final String JAVA_TYPE_STRING = "String";
    public static final String JAVA_TYPE_LONG = "Long";
    public static final String JAVA_TYPE_INTEGER = "Integer";
    public static final String JAVA_TYPE_DOUBLE = "Double";
    public static final String JAVA_TYPE_BIG_DECIMAL = "BigDecimal";
    public static final String JAVA_TYPE_DATE = "Date";
    public static final String JAVA_TYPE_BOOLEAN = "Boolean";

    // ******************** 数据库字段类型 ********************
    /**
     * 字符类型
     */
    public static final String[] COLUMN_TYPE_STR = {"char", "varchar", "nvarchar", "varchar2"};
    /**
     * 文本类型
     */
    public static final String[] COLUMN_TYPE_TEXT = {"tinytext", "text", "mediumtext", "longtext"};
    /**
     * 时间类型
     */
    public static final String[] COLUMN_TYPE_TIME = {"datetime", "time", "date", "timestamp"};
    /**
     * 数字类型
     */
    public static final String[] COLUMN_TYPE_NUMBER = {"tinyint", "smallint", "mediumint", "int", "number", "integer", "bit", "bigint", "float", "double", "decimal"};

    // ******************** 不参与生成的数据库字段 ********************
    /**
     * 不参与编辑的字段
     */
    public static final String[] COLUMN_EXCLUDE_EDIT = {COL_ID, COL_CREATE_ID, COL_CREATE_BY, COL_UPDATE_ID, COL_UPDATE_BY, COL_CREATE_TIME, COL_UPDATE_TIME, COL_STS};
    /**
     * 不参与查询的字段
     */
    public static final String[] COLUMN_EXCLUDE_SEARCH = {COL_ID, COL_CREATE_ID, COL_CREATE_BY, COL_UPDATE_ID, COL_UPDATE_BY, COL_CREATE_TIME, COL_UPDATE_TIME, COL_STS, COL_REMARK};
    /**
     * 不参与表格的字段
     */
    public static final String[] COLUMN_EXCLUDE_TABLE = {COL_STS};

    // ******************** 模版文件类路径 ********************
    public static final String TPL_JAVA_CONTROLLER = "vm/java/controller.java.vm";
    public static final String TPL_JAVA_ENTITY = "vm/java/entity.java.vm";
    public static final String TPL_JAVA_ENTITY_DTO = "vm/java/entity-dto.java.vm";
    public static final String TPL_JAVA_ENTITY_PAGE_DTO = "vm/java/entity-page-dto.java.vm";
    public static final String TPL_JAVA_ENTITY_VO = "vm/java/entity-vo.java.vm";
    public static final String TPL_JAVA_MAPPER = "vm/java/mapper.java.vm";
    public static final String TPL_JAVA_SERVICE = "vm/java/service.java.vm";
    public static final String TPL_JS_API = "vm/js/api.js.vm";
    public static final String TPL_VUE_INDEX = "vm/vue/index.vue.vm";
    public static final String TPL_XML_MAPPER = "vm/xml/mapper.xml.vm";

    // ******************** 模版内容 ********************
    public static final String TPL_SCHEMA_ANNO_NORMAL = "@Schema(title = \"{}\")";
    public static final String TPL_SCHEMA_ANNO_CONSTANT = "@Schema(title = DBConstant.{})";
    public static final String TPL_TABLE_FIELD_ANNO_INSERT = "@TableField(fill = FieldFill.INSERT)";
    public static final String TPL_TABLE_FIELD_ANNO_UPDATE = "@TableField(fill = FieldFill.UPDATE)";
    public static final String TPL_TABLE_LOGIC_ANNO = "@TableLogic(value = DBConstant.VAL_STS_NO, delval = DBConstant.VAL_STS_YES)";
    public static final String TPL_TABLE_FIELD_ANNO_EXIST_FALSE = "@TableField(exist = false)";
    public static final String TPL_TABLE_ID_ANNO_TYPE_AUTO = "@TableId(type = IdType.AUTO)";
    public static final String TPL_TABLE_ID_ANNO_TYPE_DEFAULT = "@TableId";
    public static final String TPL_VALIDATOR_NOT_BLANK = "@NotBlank(message=\"{}\")";
    public static final String TPL_VALIDATOR_NOT_NULL = "@NotNull(message=\"{}\")";
    public static final String TPL_VALIDATOR_STATUS = "@Status";
    public static final String TP_VALIDATOR_SUFFIX_NOT_NULL = "不能为空";
    public static final String TPL_QUERY_ANNO = "@Query(type = QueryType.{})";
    public static final String TP_FIELD_SUFFIX_RANGE = "Range";
    public static final String TPL_EXCEL_PROPERTY_ANNO = "@ExcelProperty(value = \"{}\")";
    public static final String TPL_EXCEL_PROPERTY_DICT_ANNO = "@ExcelProperty(value = \"{}\", converter = DictConverter.class)";
    public static final String TPL_EXCEL_PROPERTY_MAPPING_ANNO = "@ExcelProperty(value = \"{}\", converter = MappingConverter.class)";
    public static final String TPL_EXCEL_PROPERTY_PLUS_DICT_ANNO = "@ExcelPropertyPlus(dataType = ExcelPropertyType.DICT, typeKey = \"{}\")";
    public static final String TPL_EXCEL_PROPERTY_PLUS_MAPPING_STATUS_ANNO = "@ExcelPropertyPlus(dataType = ExcelPropertyType.MAPPING, mapping = {\n" +
            "            @PropertyMapping(name = \"正常\", value = DBConstant.VAL_STATUS_ENABLED),\n" +
            "            @PropertyMapping(name = \"停用\", value = DBConstant.VAL_STATUS_DISABLED)\n" +
            "    })";

    // ******************** 文件名称 ********************
    public static final String FILE_NAME_SUFFIX_CONTROLLER = "Controller.java";
    public static final String FILE_NAME_SUFFIX_MAPPER = "Mapper.java";
    public static final String FILE_NAME_SUFFIX_MAPPER_XML = "Mapper.xml";
    public static final String FILE_NAME_SUFFIX_DTO = "DTO.java";
    public static final String FILE_NAME_SUFFIX_PAGE_DTO = "PageDTO.java";
    public static final String FILE_NAME_SUFFIX_VO = "VO.java";
    public static final String FILE_NAME_SUFFIX_SERVICE = "Service.java";
    public static final String FILE_NAME_SUFFIX_ENTITY = ".java";
    public static final String FILE_NAME_CODE_ZIP = "youlan.zip";

    // ******************** 包名称 ********************
    public static final String PACKAGE_NAME_SUFFIX_CONTROLLER = ".controller";
    public static final String PACKAGE_NAME_SUFFIX_SERVICE = ".service";
    public static final String PACKAGE_NAME_SUFFIX_MAPPER = ".mapper";
    public static final String PACKAGE_NAME_SUFFIX_XML = ".xml";
    public static final String PACKAGE_NAME_SUFFIX_ENTITY = ".entity";
    public static final String PACKAGE_NAME_SUFFIX_ENTITY_DTO = PACKAGE_NAME_SUFFIX_ENTITY + ".dto";
    public static final String PACKAGE_NAME_SUFFIX_ENTITY_VO = PACKAGE_NAME_SUFFIX_ENTITY + ".vo";

    // ******************** 生成类型 ********************
    public static final String GENERATOR_TYPE_ZIP = "1";
    public static final String GENERATOR_TYPE_PATH = "2";

}