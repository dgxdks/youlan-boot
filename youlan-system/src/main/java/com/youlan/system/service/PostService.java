package com.youlan.system.service;

import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.system.entity.Post;
import com.youlan.system.mapper.PostMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PostService extends BaseServiceImpl<PostMapper, Post> {

}
