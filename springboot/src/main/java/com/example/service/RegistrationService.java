package com.example.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.example.common.enums.CallEnum;
import com.example.common.enums.PayEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Doctor;
import com.example.entity.Registration;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.DoctorMapper;
import com.example.mapper.RegistrationMapper;
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
public class RegistrationService {

    @Resource
    private RegistrationMapper registrationMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private DoctorMapper doctorMapper;

    /**
     * 新增
     */
    public void add(Registration registration) {
        //判断是否在住院中
        List<Registration> registrations = registrationMapper.selectAll(registration);
        if(CollectionUtil.isEmpty(registrations)){
            registrationMapper.insert(registration);
        }
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        registrationMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            registrationMapper.deleteById(id);
        }
    }
    /**
     * 付费
     */
    public void payById(Registration registration){
        //判断余额够不够
        User user = userMapper.selectById(registration.getUserId());
        if(user.getAccount() < registration.getPrice()){
            throw new CustomException("-1","您的余额不足，请在个人中心充值");
        }
        registration.setStatus(PayEnum.YES.pay);
        registrationMapper.updateById(registration);
        //扣除金额
        user.setAccount(user.getAccount() - registration.getPrice());
        userMapper.updateById(user);
    }

    /**
     * 修改
     */
    public void updateById(Registration registration) {
        if("已退款".equals(registration.getStatus())){
            User user = userMapper.selectById(registration.getUserId());
            user.setAccount(user.getAccount() + registration.getPrice());
            userMapper.updateById(user);
            registrationMapper.updateById(registration);
        }else if("不予退款".equals(registration.getStatus())){
            registrationMapper.updateById(registration);
        }else {
            registrationMapper.updateById(registration);
        }
    }

    /**
     * 退费
     */
    public void refundById(Registration registration) {
        //修改状态
        registration.setStatus(PayEnum.CHECK.pay);
        registrationMapper.updateById(registration);
        //退费
//        User user = userMapper.selectById(registration.getUserId());
//        user.setAccount(user.getAccount() + registration.getPrice());
//        userMapper.updateById(user);
    }

    /**
     * 根据ID查询
     */
    public Registration selectById(Integer id) {
        return registrationMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Registration> selectAll(Registration registration) {
        return registrationMapper.selectAll(registration);
    }

    /**
     * 分页查询
     */
    public PageInfo<Registration> selectPage(Registration registration, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if(RoleEnum.USER.name().equals(currentUser.getRole())){
            registration.setUserId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Registration> list = registrationMapper.selectAll(registration);
        return PageInfo.of(list);
    }

}