package com.youlan.plugin.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlan.plugin.pay.entity.WxPayProfile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PayConfigMapper extends BaseMapper<WxPayProfile> {
}
