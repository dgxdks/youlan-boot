package com.youlan.tools.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.entity.dto.PageDTO;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DBTable extends PageDTO {

    @Schema(title = "表名称")
    private String tableName;

    @Schema(title = "表描述")
    private String tableComment;

    @Schema(title = DBConstant.DESC_CREATE_TIME)
    private Date createTime;

    @Schema(title = DBConstant.DESC_UPDATE_TIME)
    private Date updateTime;

    @Hidden
    @TableField(exist = false)
    public Set<String> tableExclude;
}
