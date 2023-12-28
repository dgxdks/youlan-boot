package com.youlan.plugin.pay.service;

import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.plugin.pay.entity.WxPayProfile;
import com.youlan.plugin.pay.mapper.PayConfigMapper;
import org.springframework.stereotype.Service;

@Service
public class PayConfigService extends BaseServiceImpl<PayConfigMapper, WxPayProfile> {
}
