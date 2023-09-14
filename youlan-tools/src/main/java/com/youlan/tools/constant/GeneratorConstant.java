package com.youlan.tools.constant;

import java.util.HashMap;
import java.util.Map;

import static com.youlan.common.db.constant.DBConstant.*;

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


    // ******************** 文件名称 ********************
    public static final String FILE_NAME_SUFFIX_CONTROLLER = "Controller.java";
    public static final String FILE_NAME_SUFFIX_MAPPER = "Mapper.java";
    public static final String FILE_NAME_SUFFIX_MAPPER_XML = "Mapper.xml";
    public static final String FILE_NAME_SUFFIX_DTO = "DTO.java";
    public static final String FILE_NAME_SUFFIX_PAGE_DTO = "PageDTO.java";
    public static final String FILE_NAME_SUFFIX_VO = "VO.java";
    public static final String FILE_NAME_SUFFIX_SERVICE = "Service.java";
    public static final String FILE_NAME_SUFFIX_ENTITY = ".java";
    public static final String FILE_NAME_SUFFIX_JS = ".js";
    public static final String FILE_NAME_VUE_INDEX = "index.vue";
    public static final String FILE_NAME_CODE_ZIP = "youlan.zip";

    // ******************** 包名称 ********************
    public static final String PACKAGE_NAME_PREFIX_VUE_API = "vue.api.";
    public static final String PACKAGE_NAME_PREFIX_VUE_INDEX = "vue.views.";
    public static final String PACKAGE_NAME_SUFFIX_CONTROLLER = ".controller";
    public static final String PACKAGE_NAME_SUFFIX_SERVICE = ".service";
    public static final String PACKAGE_NAME_SUFFIX_MAPPER = ".mapper";
    public static final String PACKAGE_NAME_SUFFIX_XML = ".xml";
    public static final String PACKAGE_NAME_SUFFIX_ENTITY = ".entity";
    public static final String PACKAGE_NAME_SUFFIX_ENTITY_DTO = PACKAGE_NAME_SUFFIX_ENTITY + ".dto";
    public static final String PACKAGE_NAME_SUFFIX_ENTITY_VO = PACKAGE_NAME_SUFFIX_ENTITY + ".vo";

    /**
     * 列与描述的映射
     */
    public static final Map<String, String> COL_DESC_FIELD_NAME_MAPPING = new HashMap<>() {{
        put(COL_ID, "DESC_ID");
        put(COL_STATUS, "DESC_STATUS");
        put(COL_CREATE_BY, "DESC_CREATE_BY");
        put(COL_CREATE_ID, "DESC_CREATE_ID");
        put(COL_UPDATE_ID, "DESC_UPDATE_ID");
        put(COL_UPDATE_BY, "DESC_UPDATE_BY");
        put(COL_CREATE_TIME, "DESC_CREATE_TIME");
        put(COL_UPDATE_TIME, "DESC_UPDATE_TIME");
        put(COL_STS, "DESC_STS");
        put(COL_SORT, "DESC_SORT");
        put(COL_REMARK, "DESC_REMARK");
    }};

}