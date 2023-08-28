package com.youlan.system.entity.dto;

import com.youlan.common.db.anno.Query;
import com.youlan.common.db.constant.DBConstant;
import com.youlan.common.db.entity.dto.PageDTO;
import com.youlan.common.db.enums.QueryType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserPageDTO extends PageDTO {

    @Query(type = QueryType.EQUAL)
    @Schema(title = "机构ID")
    private Long orgId;

    @Query(type = QueryType.EQUAL)
    @Schema(title = "用户账号")
    private String userName;

    @Query(type = QueryType.EQUAL)
    @Schema(title = "用户手机")
    private String userMobile;

    @Query(type = QueryType.EQUAL)
    @Schema(title = "用户昵称")
    private String nickName;

    @Query(type = QueryType.EQUAL)
    @Schema(title = DBConstant.DESC_STATUS)
    private String status;

    @Query(column = "create_time", type = QueryType.BETWEEN)
    @Schema(title = DBConstant.DESC_CREATE_TIME)
    private List<Date> createTimeRange;

}
