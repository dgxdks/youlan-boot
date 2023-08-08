package com.youlan.tools.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youlan.common.core.db.constant.DBConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class DBTable {

    @Schema(title = "表名称")
    private String tableName;

    @Schema(title = "表描述")
    private String tableComment;

    @Schema(title = DBConstant.DESC_CREATE_TIME)
    private Date createTime;

    @Schema(title = DBConstant.DESC_UPDATE_TIME)
    private Date updateTime;

    @TableField(exist = false)
    public List<String> tableNames;
}
