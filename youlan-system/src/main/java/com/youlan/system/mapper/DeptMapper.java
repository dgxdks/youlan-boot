package com.youlan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlan.system.entity.Dept;
import com.youlan.system.entity.dto.OrgPageDTO;
import com.youlan.system.entity.vo.DeptVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeptMapper extends BaseMapper<Dept> {
    IPage<DeptVO> getDeptList(@Param("page") IPage<Dept> page, @Param("dto") OrgPageDTO dto);

    List<DeptVO> getDeptList(@Param("dto") OrgPageDTO dto);
}
