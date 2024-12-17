package com.example.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.example.common.enums.CallEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Doctor;
import com.example.entity.Reserve;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.DoctorMapper;
import com.example.mapper.ReserveMapper;
import com.example.mapper.UserMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 挂号表业务处理
 **/
@Service
public class ReserveService {

    @Resource
    private ReserveMapper reserveMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private DoctorMapper doctorMapper;
    /**
     * 新增
     */
    public void add(Reserve reserve) {
        //获取已经挂过号的时间
        reserve.setTime(reserve.getWeek());
        //不能挂同一时间同一个医生的号
        List<Reserve> reserves = reserveMapper.selectAll(reserve);
        if(CollectionUtil.isNotEmpty(reserves)){
            throw new CustomException("-1","您已经挂过该医生的号了，请勿重复挂号");
        }
        //判断余额够不够
        User user = userMapper.selectById(reserve.getUserId());
        Doctor doctor = doctorMapper.selectById(reserve.getDoctorId());
        if(user.getAccount() < doctor.getPrice()){
            throw new CustomException("-1","您的余额不足，请在个人中心充值");
        }
        reserve.setStatus(CallEnum.STATUS_NO.status);
        reserveMapper.insert(reserve);

        //扣除金额
        user.setAccount(user.getAccount() - doctor.getPrice());
        userMapper.updateById(user);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        //退费
        Reserve reserve = reserveMapper.selectById(id);
        reserveMapper.deleteById(id);
        User user = userMapper.selectById(reserve.getUserId());
        Doctor doctor = doctorMapper.selectById(reserve.getDoctorId());
        user.setAccount(user.getAccount() + doctor.getPrice());
        userMapper.updateById(user);

    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            reserveMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Reserve reserve) {
        reserveMapper.updateById(reserve);
    }

    /**
     * 根据ID查询
     */
    public Reserve selectById(Integer id) {
        return reserveMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Reserve> selectAll(Reserve reserve) {
        return reserveMapper.selectAll(reserve);
    }

    /**
     * 分页查询
     */
    public PageInfo<Reserve> selectPage(Reserve reserve, Integer pageNum, Integer pageSize) {
        //用户医生单独可见
        Account currentUser = TokenUtils.getCurrentUser();
        if(RoleEnum.USER.name().equals(currentUser.getRole())){
            reserve.setUserId(currentUser.getId());
        }
        if(RoleEnum.DOCTOR.name().equals(currentUser.getRole())){
            reserve.setDoctorId(currentUser.getId());
        }

        PageHelper.startPage(pageNum, pageSize);
        List<Reserve> list = reserveMapper.selectAll(reserve);
        return PageInfo.of(list);
    }

}