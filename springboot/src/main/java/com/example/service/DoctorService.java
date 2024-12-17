package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.*;
import com.example.exception.CustomException;
import com.example.mapper.DepartmentMapper;
import com.example.mapper.DoctorMapper;
import com.example.mapper.PlanMapper;
import com.example.mapper.ReserveMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.print.Doc;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 医生业务处理
 **/
@Service
public class DoctorService {

    @Resource
    private DoctorMapper doctorMapper;
    @Resource
    private DepartmentMapper departmentMapper;
    @Resource
    private ReserveMapper reserveMapper;
    @Resource
    private PlanMapper planMapper;

    /**
     * 新增
     */
    public void add(Doctor doctor) {
        Doctor dbDoctor = doctorMapper.selectByUsername(doctor.getUsername());
        if (ObjectUtil.isNotNull(dbDoctor)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);  //用户已经存在
        }
        if (ObjectUtil.isEmpty(doctor.getPassword())) {
            doctor.setPassword(Constants.USER_DEFAULT_PASSWORD);   //给初始密码
        }
        if (ObjectUtil.isEmpty(doctor.getName())) {
            doctor.setName(doctor.getUsername());
        }
        doctor.setRole(RoleEnum.DOCTOR.name());    //设置role
        doctorMapper.insert(doctor);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        doctorMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            doctorMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Doctor doctor) {
        doctorMapper.updateById(doctor);
    }

    /**
     * 根据ID查询
     */
    public Doctor selectById(Integer id) {
        return doctorMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Doctor> selectAll(Doctor doctor) {
        return doctorMapper.selectAll(doctor);
    }

    /**
     * 分页查询
     */
    public PageInfo<Doctor> selectPage(Doctor doctor, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Doctor> list = doctorMapper.selectAll(doctor);
        return PageInfo.of(list);
    }

    /**
     * 医生挂号页面的分页查询
     * @param doctor
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageInfo<Doctor> selectPage2(Doctor doctor, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<Doctor> list = doctorMapper.selectAll2(doctor);  //全筛选

        //测试循环次数，当天在值班的医生
        int count = 0;
        //计算剩余多少个号
        for(Doctor dbDoctor : list){
            count++;
            System.out.println("当前是第 " + count + " 次循环");

            Reserve reserve = new Reserve();
            reserve.setDoctorId(dbDoctor.getId());
            reserve.setTime(dbDoctor.getWeek()); //拿取前端选择挂号的week
//            reserve.setTime(today);
            List<Reserve> reserves = reserveMapper.selectAll(reserve);
            Plan plan = planMapper.selectByDoctorIdAndWeek(dbDoctor.getId(),dbDoctor.getWeek());
            //总数-已挂=剩余数量
            dbDoctor.setNum(plan.getNum() - reserves.size());
        }
        System.out.println("总共循环了 " + count + " 次");
        return PageInfo.of(list);
    }

    /**
     * 获取今天是星期几
     */
    private String getTodayWeek(){
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        return dayOfWeek.getDisplayName(TextStyle.FULL_STANDALONE, Locale.CHINA);
    }

    /**
     * 登录
     */
    public Account login(Account account) {
        Doctor dbDoctor = doctorMapper.selectByUsername(account.getUsername()); //查询医生是否存在
        if (ObjectUtil.isNull(dbDoctor)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbDoctor.getPassword())) {           //验证密码
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        // 生成token
        String tokenData = dbDoctor.getId() + "-" + RoleEnum.DOCTOR.name();
        String token = TokenUtils.createToken(tokenData, dbDoctor.getPassword());   //使用 tokenData（用户信息）和密码作为参数
        dbDoctor.setToken(token);  //将生成的 token 保存到医生对象中
        //关联科室
        if(ObjectUtil.isNotEmpty(dbDoctor.getDepartmentId())){
            Department department = departmentMapper.selectById(dbDoctor.getDepartmentId());
            if(ObjectUtil.isNotEmpty(department)){
                //测试
                System.out.println("关联科室："+department.getName());
                dbDoctor.setDepartmentName(department.getName());
            }
        }
        return dbDoctor;
    }

    /**
     * 注册
     */
//    public void register(Account account) {
//        Doctor doctor = new Doctor();
//        BeanUtils.copyProperties(account, doctor);
//        add(doctor);
//    }

    /**
     * 修改密码
     */
    public void updatePassword(Account account) {
        Doctor dbDoctor = doctorMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbDoctor)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbDoctor.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        dbDoctor.setPassword(account.getNewPassword());
        doctorMapper.updateById(dbDoctor);
    }

}