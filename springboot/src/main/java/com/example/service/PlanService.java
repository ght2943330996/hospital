package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.Account;
import com.example.entity.Plan;
import com.example.exception.CustomException;
import com.example.mapper.PlanMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 排版信息表业务处理
 **/
@Service
public class PlanService {

    @Resource
    private PlanMapper planMapper;

    /**
     * 新增
     */
    public void add(Plan plan) {
        //排班校验，不能重复排班
        Plan dbPlan = planMapper.selectByDoctorIdAndWeek(plan.getDoctorId(),plan.getWeek());
        if(ObjectUtil.isNotEmpty(dbPlan)){
            throw new CustomException(ResultCodeEnum.PLAN_EXIST_ERROR);
        }
        planMapper.insert(plan);
    }
    /**
     * 删除
     */
    public void deleteById(Integer id) {
        planMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            planMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Plan plan) {
        //排班校验，不能重复排班
        Plan dbPlan = planMapper.selectByDoctorIdAndWeek(plan.getDoctorId(),plan.getWeek());
        if(ObjectUtil.isNotEmpty(dbPlan) && !plan.getId().equals(dbPlan.getId())){
            throw new CustomException(ResultCodeEnum.PLAN_EXIST_ERROR);
        }
        planMapper.updateById(plan);
    }

    /**
     * 根据ID查询
     */
    public Plan selectById(Integer id) {
        return planMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Plan> selectAll(Plan plan) {
        return planMapper.selectAll(plan);
    }

    /**
     * 分页查询
     */
    public PageInfo<Plan> selectPage(Plan plan, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Plan> list = planMapper.selectAll(plan);
        return PageInfo.of(list);
    }

}