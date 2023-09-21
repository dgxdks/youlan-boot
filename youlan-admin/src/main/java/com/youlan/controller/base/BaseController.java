package com.youlan.controller.base;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlan.common.core.restful.ApiResult;
import com.youlan.common.core.restful.controller.RestfulController;
import com.youlan.common.core.servlet.helper.ServletHelper;
import com.youlan.common.db.entity.vo.PageVO;
import com.youlan.common.excel.helper.ExcelHelper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static com.youlan.common.core.restful.enums.ApiResultCode.B0001;

@Slf4j
public class BaseController extends RestfulController {

    public ApiResult toSuccess(IPage<?> page) {
        return ApiResult.ok(new PageVO<>(page));
    }

    public void toExcel(String fileName, Class<?> head, List<?> dataList, HttpServletResponse response) throws IOException {
        toExcel(fileName, null, head, dataList, response);
    }

    public void toExcel(String fileName, Class<?> head, List<?> dataList) throws IOException {
        toExcel(fileName, head, dataList, ServletHelper.getHttpServletResponse());
    }

    public void toExcel(String fileName, String sheetName, Class<?> head, List<?> dataList, HttpServletResponse response) throws IOException {
        try {
            ByteArrayOutputStream cacheBos = new ByteArrayOutputStream(1024);
            ExcelWriterBuilder excelWriterBuilder = ExcelHelper.write(cacheBos, head)
                    .autoCloseStream(true);
            if (StrUtil.isNotBlank(sheetName)) {
                excelWriterBuilder.sheet(sheetName).doWrite(dataList);
            } else {
                excelWriterBuilder.sheet().doWrite(dataList);
            }
            String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            ServletHelper.download(fileName, cacheBos.toByteArray(), contentType);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            toObject(ApiResult.error(B0001), response);
        }
    }
}
