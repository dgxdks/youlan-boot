package com.youlan.system.service;

import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.system.entity.Notice;
import com.youlan.system.mapper.NoticeMapper;
import org.springframework.stereotype.Service;

@Service
public class NoticeService extends BaseServiceImpl<NoticeMapper, Notice> {
}
