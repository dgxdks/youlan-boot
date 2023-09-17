package com.youlan.system.entity.dto;

import com.youlan.common.db.entity.dto.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OnlineUserPageDTO extends PageDTO {
    @Schema(description = "用户名称")
    private String userName;
}
