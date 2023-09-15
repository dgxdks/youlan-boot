package com.youlan.tools.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.youlan.common.core.exception.BizRuntimeException;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.tools.constant.GeneratorConstant;
import com.youlan.tools.entity.GeneratorColumn;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    /**
     * 生成临时目录
     */
    public static String generateTempHomePath() {
        return FileUtil.getTmpDirPath() + File.separator + IdUtil.simpleUUID();
    }

    /**
     * 包名转模块名
     */
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
     * 生成指定模版内容
     */
    public static String mergeTemplate(String templateClassPath, VelocityContext velocityContext) {
        Template template = Velocity.getTemplate(templateClassPath);
        StringWriter stringWriter = new StringWriter();
        template.merge(velocityContext, stringWriter);
        return stringWriter.toString();
    }

    /**
     * 写入模版内容至指定目录
     */
    public static void writeToPath(String homePath, String packageName, String fileName, String fileContent) {
        String packagePath = packageName2Path(packageName);
        String writePath = homePath + File.separator + packagePath + File.separator + fileName;
        FileUtil.writeString(fileContent, writePath, StandardCharsets.UTF_8);
        log.info("写入模板文件: {homePath: {}, packageName: {}, fileName: {}}", homePath, packageName, fileName);
    }

    /**
     * 写入模版内容至zip文件
     */
    public static void writeToZip(String packageName, String fileName, String fileContent, ZipOutputStream zipOs) {
        String packagePath = packageName2Path(packageName);
        String zipPath = packagePath + File.separator + fileName;
        ZipEntry zipEntry = new ZipEntry(zipPath);
        try {
            zipOs.putNextEntry(zipEntry);
            IoUtil.write(zipOs, StandardCharsets.UTF_8, false, fileContent);
            zipOs.flush();
            zipOs.closeEntry();
        } catch (Exception e) {
            throw new BizRuntimeException(e);
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
        return String.format("@Schema(description = DBConstant.%s)", constantName);
    }

    /**
     * 获取@Schema注解
     */
    public static String getSchemaAnno(String title) {
        return String.format("@Schema(description = \"%s\")", title);
    }

    /**
     * 获取@QueryType注解
     */
    public static String getQueryTypeAnno(String queryType) {
        return String.format("@Query(type = QueryType.%s)", queryType);
    }

    /**
     * 获取@QueryType注解
     */
    public static String getQueryTypeAnnoWithColumn(String column, String queryType) {
        return String.format("@Query(column = \"%s\", type = QueryType.%s)", column, queryType);
    }

    /**
     * 实体类DTO需要导入的包
     */
    public static List<String> getDtoPackages() {
        return Arrays.asList(
                "import javax.validation.constraints.*;",
                "import com.youlan.common.validator.anno.*;"
        );
    }

    /**
     * 实体类PageDTO需要导入的包
     */
    public static List<String> getPageDtoPackages() {
        return Arrays.asList(
                "import lombok.EqualsAndHashCode;",
                "import com.youlan.common.db.anno.Query;",
                "import com.youlan.common.db.enums.QueryType;",
                "import com.youlan.common.db.entity.dto.PageDTO;"
        );
    }

    /**
     * 实体类VO需要导入的包
     */
    public static List<String> getVoPackages() {
        return Arrays.asList(
                "import com.alibaba.excel.annotation.ExcelProperty;",
                "import com.alibaba.excel.annotation.write.style.ColumnWidth;",
                "import com.alibaba.excel.annotation.write.style.HeadFontStyle;",
                "import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;",
                "import com.youlan.system.excel.anno.ExcelDictProperty;",
                "import com.youlan.system.excel.converter.DictConverter;"
        );
    }

    /**
     * 需要导入的基础包
     */
    public static List<String> getBasePackages() {
        return Arrays.asList(
                "import com.youlan.common.db.constant.DBConstant;",
                "import io.swagger.v3.oas.annotations.media.Schema;",
                "import lombok.Data;",
                "import java.util.Date;",
                "import java.util.List;"
        );
    }

    public static List<String> getEntityPackages() {
        return Arrays.asList(
                "import com.youlan.common.validator.anno.*;"
        );
    }
}