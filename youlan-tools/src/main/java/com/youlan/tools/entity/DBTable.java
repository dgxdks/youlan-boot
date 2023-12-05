package com.youlan.tools.entity;

import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.entity.dto.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DBTable extends PageDTO {

    @Schema(description = "表名称")
    private String tableName;

    @Schema(description = "表描述")
    private String tableComment;

    @Schema(description = DBConstant.DESC_CREATE_TIME)
    private Date createTime;

    @Schema(description = DBConstant.DESC_UPDATE_TIME)
    private Date updateTime;

}
