package com.youlan.tools.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.tools.constant.GeneratorConstant;
import com.youlan.tools.entity.GeneratorColumn;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class GeneratorUtil {
    /**
     * 列是否主键
     */
    public static boolean columnIsPk(String columnKey) {
        return "PRI".equals(columnKey);
    }

    /**
     * 列是否自增
     */
    public static boolean columnIsIncrement(String extra) {
        return "auto_increment".equals(extra);
    }

    /**
     * 列是否不允许为空
     */
    public static boolean columnIsRequired(String isNullable) {
        return "NO".equals(isNullable);
    }

    public static void setJavaTypeComponentType(GeneratorColumn column) {
        column.setJavaType(GeneratorConstant.JAVA_TYPE_STRING)
                .setComponentType(GeneratorConstant.COMPONENT_TYPE_INPUT);
        String columnType = column.getColumnType();
        String dbType = columnType.split("\\(")[0];
        //字符类型
        if (ArrayUtil.contains(GeneratorConstant.COLUMN_TYPE_STR, dbType)) {
            String between = StrUtil.subBetween(columnType, "(", ")");
            int columnLength = Integer.parseInt(between);
            //大于500使用textarea
            if (columnLength > 500) {
                column.setComponentType(GeneratorConstant.COMPONENT_TYPE_TEXTAREA);
            }
        }
        //文本类型
        if (ArrayUtil.contains(GeneratorConstant.COLUMN_TYPE_TEXT, dbType)) {
            column.setComponentType(GeneratorConstant.COMPONENT_TYPE_TEXTAREA);
        }
        //时间类型
        if (ArrayUtil.contains(GeneratorConstant.COLUMN_TYPE_TIME, dbType)) {
            column.setJavaType(GeneratorConstant.JAVA_TYPE_DATE)
                    .setComponentType(GeneratorConstant.COMPONENT_TYPE_DATETIME);
        }
        //数字类型
        if (ArrayUtil.contains(GeneratorConstant.COLUMN_TYPE_NUMBER, dbType)) {
            System.out.println(columnType);
            String between = StrUtil.subBetween(columnType, "(", ")");
            String[] split = between.split(",");
            if (split.length == 2) {
                column.setJavaType(GeneratorConstant.JAVA_TYPE_BIG_DECIMAL);
            }
            if (split.length == 1 && Integer.parseInt(split[0]) <= 10) {
                column.setJavaType(GeneratorConstant.JAVA_TYPE_INTEGER);
            } else {
                column.setJavaType(GeneratorConstant.JAVA_TYPE_LONG);
            }
        }
    }

    /**
     * 查找第一个主键列
     */
    public static GeneratorColumn getPkGeneratorColumn(List<GeneratorColumn> generatorColumnList) {
        return CollectionUtil.findOne(generatorColumnList, column -> column.getIsPk().equals(DBConstant.VAL_YES));
    }

    /**
     * 去除表名称前面指定的前缀
     */
    public static String tableNameRemovePrefix(String tableName, List<String> prefixList) {
        if (CollectionUtil.isEmpty(prefixList)) {
            return tableName;
        }
        for (String prefix : prefixList) {
            if (tableName.startsWith(prefix)) {
                return tableName.replaceFirst(prefix, "");
            }
        }
        return tableName;
    }

    public static String generateTempHomePath() {
        return FileUtil.getTmpDirPath() + File.separator + IdUtil.simpleUUID();
    }

    public static String packageName2ModuleName(String packageName) {
        String[] split = packageName.split("\\.");
        return split[split.length - 1];
    }

    public static String packageName2Path(String packageName) {
        return formatPackageName(packageName).replace(".", File.separator);
    }

    /**
     * 包路径拼接
     */
    public static String packageNameConcat(String packageName, String... concatNames) {
        StringBuilder finalPackageName = new StringBuilder();
        finalPackageName.append(formatPackageName(packageName));

        Arrays.asList(concatNames)
                .forEach(name -> {
                    finalPackageName.append(formatPackageName(packageName));
                });
        return finalPackageName.toString();
    }

    /**
     * 包路径格式化
     */
    public static String formatPackageName(String packageName) {
        if (StrUtil.isBlank(packageName)) {
            return StrUtil.EMPTY;
        }
        return Arrays.stream(packageName.split("\\."))
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.joining("."));
    }

    /**
     * 表描述转为功能名称
     */
    public static String tableComment2FeatureName(String tableComment, String replaceRegex) {
        if (StrUtil.isBlank(tableComment)) {
            return tableComment;
        }
        try {
            return ReUtil.replaceAll(tableComment, replaceRegex, "");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return tableComment;
        }
    }

    /**
     * 获取@NotBlank校验注解
     */
    public static String getNotBlankValidatorAnno(String columnComment) {
        return StrUtil.format("@NotBlank(message=\"{}\")", columnComment + "不能为空");
    }

    /**
     * 获取@NotNull校验注解
     */
    public static String getNotNullValidatorAnno(String columnComment) {
        return StrUtil.format("@NotNull(message=\"{}\")", columnComment + "不能为空");
    }

    /**
     * 获取@TableFile注解
     */
    public static String getTableFieldUpdateAnno() {
        return "@TableField(fill = FieldFill.UPDATE)";
    }

    /**
     * 获取@TableFile注解
     */
    public static String getTableFieldInsertAnno() {
        return "@TableField(fill = FieldFill.INSERT)";
    }

    /**
     * 获取@TableLogic注解
     */
    public static String getTableLogicAnno() {
        return "@TableLogic(value = DBConstant.VAL_STS_NO, delval = DBConstant.VAL_STS_YES)";
    }

    /**
     * 获取@TableFile注解
     */
    public static String getTableFieldNotExistAnno() {
        return "@TableField(exist = false)";
    }

    /**
     * 获取@TableId注解
     */
    public static String getTableIdAnno() {
        return "@TableId";
    }

    /**
     * 获取@TableId注解
     */
    public static String getTableIdAutoAnno() {
        return "@TableId(type = IdType.AUTO)";
    }

    /**
     * 获取@ExcelDictProperty注解
     */
    public static String getExcelDictProperty(String desc, String typeKey) {
        //第二行添加四个空格缩进
        return String.format("@ExcelProperty(value = \"%s\", converter = DictConverter.class)", desc)
                + "\n"
                + String.format("    @ExcelDictProperty(\"%s\")", typeKey);
    }

    /**
     * 获取@ExcelProperty注解
     */
    public static String getExcelProperty(String desc) {
        return String.format("@ExcelProperty(value = \"%s\")", desc);
    }

    /**
     * 获取@Schema注解
     */
    public static String getSchemaAnnoFromDBConstant(String constantName) {
        return String.format("@Schema(title = DBConstant.%s)", constantName);
    }

    /**
     * 获取@Schema注解
     */
    public static String getSchemaAnno(String title) {
        return String.format("@Schema(title = \"%s\")", title);
    }

    /**
     * 获取@QueryType注解
     */
    public static String getQueryTypeAnno(String queryType) {
        return String.format("@Query(type = QueryType.%s)", queryType);
    }
}
