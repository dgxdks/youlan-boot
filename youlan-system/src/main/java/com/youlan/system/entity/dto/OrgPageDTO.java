package com.youlan.system.entity.dto;

import com.youlan.common.db.anno.Query;
import com.youlan.common.db.entity.dto.PageDTO;
import com.youlan.common.db.enums.QueryType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrgPageDTO extends PageDTO {

    @Query(type = QueryType.LIKE)
    @Schema(title = "机构名称")
    private String orgName;

    @Query(type = QueryType.EQUAL)
    @Schema(title = "机构状态(1-正常 2-停用)")
    private String orgStatus;
}
