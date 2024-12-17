package com.example.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.example.common.enums.PayEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Medical;
import com.example.entity.Medical;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.MedicalMapper;
import com.example.mapper.UserMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 公告信息表业务处理
 **/
@Service
public class MedicalService {

    @Resource
    private MedicalMapper medicalMapper;

    @Resource
    private UserMapper userMapper;

    /**
     * 新增
     */
    public void add(Medical medical) {
        //判断id是否已经存在
//        System.out.println("-----------测试："+medical.getTime()+"-"+medical.getUserId()+"-"+medical.getDoctorId()+"-"+medical.getMedicalName());
        Medical medical1 = medicalMapper.selectById(medical.getTime());
        if(medical1 == null){
            medicalMapper.insert(medical);
        }else {
            medicalMapper.updateById(medical);
        }
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        medicalMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            medicalMapper.deleteById(id);
        }
    }


    /**
     * 付费
     */
    public void payById(Medical medical){
        //判断余额够不够
        User user = userMapper.selectById(medical.getUserId());
        if(user.getAccount() < medical.getPrice()){
            throw new CustomException("-1","您的余额不足，请在个人中心充值");
        }
        medical.setStatus(PayEnum.YES.pay);
        medicalMapper.updateById(medical);
        //扣除金额
        user.setAccount(user.getAccount() - medical.getPrice());
        userMapper.updateById(user);
    }
    /**
     * 修改
     */
    public void updateById(Medical medical) {
        // 判断状态
        if ("已退款".equals(medical.getStatus())) {
            //退费
            User user = userMapper.selectById(medical.getUserId());
            user.setAccount(user.getAccount() + medical.getPrice());
            userMapper.updateById(user);
            medicalMapper.updateById(medical);
        } else if ("不予退款".equals(medical.getStatus())) {
            // 直接更新数据库状态
            medicalMapper.updateById(medical);
        } else {
            // 其他状态（已缴费、未缴费）直接更新
            medicalMapper.updateById(medical);
        }
    }

    /**
     * 退费
     */
    public void refundById(Medical medical) {
        //修改状态
        medical.setStatus(PayEnum.CHECK.pay);
        medicalMapper.updateById(medical);

//        //退费
//        User user = userMapper.selectById(medical.getUserId());
//        user.setAccount(user.getAccount() + medical.getPrice());
//        userMapper.updateById(user);
    }

    /**
     * 根据ID查询
     */
//    public Medical selectById(Integer id) {
//        return medicalMapper.selectById(id);
//    }

    /**
     * 查询所有
     */
    public List<Medical> selectAll(Medical medical) {
        return medicalMapper.selectAll(medical);
    }

    /**
     * 分页查询
     */
    public PageInfo<Medical> selectPage(Medical medical, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if(RoleEnum.USER.name().equals(currentUser.getRole())){
            medical.setUserId(currentUser.getId());
        }
        if(RoleEnum.DOCTOR.name().equals(currentUser.getRole())){
            medical.setDoctorId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Medical> list = medicalMapper.selectAll(medical);
        return PageInfo.of(list);
    }

}