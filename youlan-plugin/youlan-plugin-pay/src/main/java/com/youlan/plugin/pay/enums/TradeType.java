package com.youlan.plugin.pay.enums;

import com.youlan.common.core.helper.EnumHelper;
import com.youlan.plugin.pay.client.AbstractPayClient;
import com.youlan.plugin.pay.client.wx.*;
import com.youlan.plugin.pay.params.PayParams;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

import static com.youlan.plugin.pay.constant.PayConstant.*;

@Getter
@AllArgsConstructor
public enum TradeType {

    WX_JSAPI(TRADE_TYPE_WX_JSAPI, "微信JSAPI支付", WxJsApiPayClient.class),
    WX_APP(TRADE_TYPE_WX_APP, "微信APP支付", WxAppPayClient.class),
    WX_H5(TRADE_TYPE_WX_H5, "微信H5支付", WxH5PayClient.class),
    WX_NATIVE(TRADE_TYPE_WX_NATIVE, "微信Native支付", WxNativePayClient.class),
    WX_MINI(TRADE_TYPE_WX_MINI, "微信小程序支付", WxMiniPayClient.class),
    WX_SCAN(TRADE_TYPE_WX_SCAN, "微信付款码支付", WxScanPayClient.class);

    private static final Map<String, TradeType> TRADE_TYPE_CACHE = EnumHelper.getEnumMap(TradeType.class, TradeType::getCode);

    private final String code;
    private final String text;
    private final Class<? extends AbstractPayClient<? extends PayParams>> clientClass;

}
