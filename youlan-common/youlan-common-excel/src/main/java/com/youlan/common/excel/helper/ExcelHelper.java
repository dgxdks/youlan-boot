package com.youlan.common.excel.helper;

import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.builder.ExcelWriterTableBuilder;
import com.youlan.common.excel.listener.DefaultReadListener;
import org.apache.poi.ss.formula.functions.T;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class ExcelHelper {
    public static ExcelWriterBuilder write() {
        return new ExcelWriterBuilder();
    }

    public static ExcelWriterBuilder write(File file) {
        return write(file, null);
    }

    public static ExcelWriterBuilder write(File file, Class head) {
        ExcelWriterBuilder excelWriterBuilder = new ExcelWriterBuilder();
        excelWriterBuilder.file(file);
        if (head != null) {
            excelWriterBuilder.head(head);
        }
        return excelWriterBuilder;
    }

    public static ExcelWriterBuilder write(String pathName) {
        return write(pathName, null);
    }

    public static ExcelWriterBuilder write(String pathName, Class head) {
        ExcelWriterBuilder excelWriterBuilder = new ExcelWriterBuilder();
        excelWriterBuilder.file(pathName);
        if (head != null) {
            excelWriterBuilder.head(head);
        }
        return excelWriterBuilder;
    }

    public static ExcelWriterBuilder write(OutputStream outputStream) {
        return write(outputStream, null);
    }

    public static ExcelWriterBuilder write(OutputStream outputStream, Class head) {
        ExcelWriterBuilder excelWriterBuilder = new ExcelWriterBuilder();
        excelWriterBuilder.file(outputStream);
        if (head != null) {
            excelWriterBuilder.head(head);
        }
        return excelWriterBuilder;
    }

    public static ExcelWriterSheetBuilder writerSheet() {
        return writerSheet(null, null);
    }

    public static ExcelWriterSheetBuilder writerSheet(Integer sheetNo) {
        return writerSheet(sheetNo, null);
    }


    public static ExcelWriterSheetBuilder writerSheet(String sheetName) {
        return writerSheet(null, sheetName);
    }

    public static ExcelWriterSheetBuilder writerSheet(Integer sheetNo, String sheetName) {
        ExcelWriterSheetBuilder excelWriterSheetBuilder = new ExcelWriterSheetBuilder();
        if (sheetNo != null) {
            excelWriterSheetBuilder.sheetNo(sheetNo);
        }
        if (sheetName != null) {
            excelWriterSheetBuilder.sheetName(sheetName);
        }
        return excelWriterSheetBuilder;
    }

    public static ExcelWriterTableBuilder writerTable() {
        return writerTable(null);
    }

    public static ExcelWriterTableBuilder writerTable(Integer tableNo) {
        ExcelWriterTableBuilder excelWriterTableBuilder = new ExcelWriterTableBuilder();
        if (tableNo != null) {
            excelWriterTableBuilder.tableNo(tableNo);
        }
        return excelWriterTableBuilder;
    }

    public static ExcelReaderBuilder read() {
        return new ExcelReaderBuilder();
    }

    public static ExcelReaderBuilder read(File file) {
        return read(file, null, null);
    }

    public static List<T> readList(File file, Class<T> head) {
        DefaultReadListener<T> defaultReadListener = new DefaultReadListener<>();
        read(file, head, defaultReadListener).sheet().doRead();
        return defaultReadListener.getCacheList();
    }

    public static <T> List<T> readList(String pathName, Class<T> head) {
        DefaultReadListener<T> defaultReadListener = new DefaultReadListener<>();
        read(pathName, head, defaultReadListener).sheet().doRead();
        return defaultReadListener.getCacheList();
    }

    public static <T> List<T> readList(InputStream inputStream, Class<T> head) {
        DefaultReadListener<T> defaultReadListener = new DefaultReadListener<>();
        read(inputStream, head, defaultReadListener).sheet().doRead();
        return defaultReadListener.getCacheList();
    }

    public static ExcelReaderBuilder read(File file, ReadListener readListener) {
        return read(file, null, readListener);
    }

    public static ExcelReaderBuilder read(File file, Class head, ReadListener readListener) {
        ExcelReaderBuilder excelReaderBuilder = new ExcelReaderBuilder();
        excelReaderBuilder.file(file);
        if (head != null) {
            excelReaderBuilder.head(head);
        }
        if (readListener != null) {
            excelReaderBuilder.registerReadListener(readListener);
        }
        return excelReaderBuilder;
    }

    public static ExcelReaderBuilder read(String pathName) {
        return read(pathName, null, null);
    }

    public static ExcelReaderBuilder read(String pathName, ReadListener readListener) {
        return read(pathName, null, readListener);
    }

    public static ExcelReaderBuilder read(String pathName, Class head, ReadListener readListener) {
        ExcelReaderBuilder excelReaderBuilder = new ExcelReaderBuilder();
        excelReaderBuilder.file(pathName);
        if (head != null) {
            excelReaderBuilder.head(head);
        }
        if (readListener != null) {
            excelReaderBuilder.registerReadListener(readListener);
        }
        return excelReaderBuilder;
    }

    public static ExcelReaderBuilder read(InputStream inputStream) {
        return read(inputStream, null, null);
    }

    public static ExcelReaderBuilder read(InputStream inputStream, ReadListener readListener) {
        return read(inputStream, null, readListener);
    }

    public static ExcelReaderBuilder read(InputStream inputStream, Class head, ReadListener readListener) {
        ExcelReaderBuilder excelReaderBuilder = new ExcelReaderBuilder();
        excelReaderBuilder.file(inputStream);
        if (head != null) {
            excelReaderBuilder.head(head);
        }
        if (readListener != null) {
            excelReaderBuilder.registerReadListener(readListener);
        }
        return excelReaderBuilder;
    }

    public static ExcelReaderSheetBuilder readSheet() {
        return readSheet(null, null);
    }

    public static ExcelReaderSheetBuilder readSheet(Integer sheetNo) {
        return readSheet(sheetNo, null);
    }

    public static ExcelReaderSheetBuilder readSheet(String sheetName) {
        return readSheet(null, sheetName);
    }

    public static ExcelReaderSheetBuilder readSheet(Integer sheetNo, String sheetName) {
        ExcelReaderSheetBuilder excelReaderSheetBuilder = new ExcelReaderSheetBuilder();
        if (sheetNo != null) {
            excelReaderSheetBuilder.sheetNo(sheetNo);
        }
        if (sheetName != null) {
            excelReaderSheetBuilder.sheetName(sheetName);
        }
        return excelReaderSheetBuilder;
    }
}
