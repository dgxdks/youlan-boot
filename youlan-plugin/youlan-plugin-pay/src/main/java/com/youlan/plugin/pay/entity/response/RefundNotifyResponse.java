package com.youlan.plugin.pay.entity.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class RefundNotifyResponse extends RefundQueryResponse {
}
