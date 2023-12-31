package com.youlan.system.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youlan.common.db.service.BaseServiceImpl;
import com.youlan.system.entity.UserPost;
import com.youlan.system.mapper.UserPostMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserPostService extends BaseServiceImpl<UserPostMapper, UserPost> {

    /**
     * 新增用户关联岗位信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void addUserPostBatch(Long userId, List<Long> postIdList) {
        List<UserPost> userPosts = toUserPostList(userId, postIdList);
        saveBatch(userPosts);
    }

    /**
     * 修改用户关联岗位信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateUserPostBatch(Long userId, List<Long> postIdList) {
        List<UserPost> userPosts = toUserPostList(userId, postIdList);
        removeByUserId(userId);
        this.saveBatch(userPosts);
    }

    /**
     * 根据用户ID删除用户关联岗位信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeByUserId(Long userId) {
        this.remove(Wrappers.<UserPost>lambdaQuery().eq(UserPost::getUserId, userId));
    }

    /**
     * 根据用户ID查询用户关联岗位列表
     */
    public List<UserPost> getPostListByUserId(Long userId) {
        return this.lambdaQuery().eq(UserPost::getUserId, userId).list();
    }

    /**
     * 根据用户ID查询岗位ID列表
     */
    public List<Long> getPostIdListByUserId(Long userId) {
        return getPostListByUserId(userId)
                .stream()
                .map(UserPost::getPostId)
                .collect(Collectors.toList());
    }

    /**
     * 根据用户ID和岗位列表转为用户关联职位对象列表
     */
    public List<UserPost> toUserPostList(Long userId, List<Long> postIdList) {
        if (ObjectUtil.isNull(userId) || CollectionUtil.isEmpty(postIdList)) {
            return new ArrayList<>();
        }
        return postIdList.stream()
                .map(postId -> new UserPost().setUserId(userId).setPostId(postId))
                .collect(Collectors.toList());
    }
}
