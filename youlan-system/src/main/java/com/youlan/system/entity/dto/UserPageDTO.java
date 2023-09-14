package com.youlan.system.entity.dto;

import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.entity.dto.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserPageDTO extends PageDTO {

    @Schema(description = "机构ID")
    private Long orgId;

    @Schema(description = "用户账号")
    private String userName;

    @Schema(description = "用户手机")
    private String userMobile;

    @Schema(description = "用户昵称")
    private String nickName;

    @Schema(description = DBConstant.DESC_STATUS)
    private String status;

    @Schema(description = DBConstant.DESC_CREATE_TIME)
    private List<Date> createTimeRange;

}
