package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.UserMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 患者业务处理
 **/
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 新增
     */
//  失效    @Transactional(rollbackFor = Exception.class)  //debug调试专用，让数据回滚不写入数据库（不加stop会继续运行），只对此方法生效
    public void add(User user) {
        User dbUser = userMapper.selectByUsername(user.getUsername());

        //测试,不要在Service层测试，显示你想要的数据 比如：注册用户:com.example.entity.User@3a4ea1ea
//        System.out.println("注册用户:"+user);
        if (ObjectUtil.isNotNull(dbUser)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);  //用户已经存在
        }
        if (ObjectUtil.isEmpty(user.getPassword())) {
            user.setPassword(Constants.USER_DEFAULT_PASSWORD);   //给初始密码
        }
        if (ObjectUtil.isEmpty(user.getName())) {
            user.setName(user.getUsername());
        }
        user.setRole(RoleEnum.USER.name());
        //测试
        System.out.println("------新增患者"+user.getRole()+"------"+user.getAccount());
        userMapper.insert(user);

//失效        throw new RuntimeException("测试回滚");  // 异常导致整个事务回滚，不然就写入数据库了
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            userMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(User user) {
        userMapper.updateById(user);
    }

    /**
     * 根据ID查询
     */
    public User selectById(Integer id) {
        //实时更新
        User dbUser = userMapper.selectById(id);
        String tokenData = dbUser.getId() + "-" + RoleEnum.USER.name();
        String token = TokenUtils.createToken(tokenData, dbUser.getPassword());
        dbUser.setToken(token);
        return dbUser;
    }

    /**
     * 查询所有
     */
    public List<User> selectAll(User user) {
        return userMapper.selectAll(user);
    }

    /**
     * 分页查询
     */
    public PageInfo<User> selectPage(User user, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userMapper.selectAll(user);
        return PageInfo.of(list);
    }

    /**
     * 分页查询所有
     */
    public PageInfo<User> selectPage2(User user, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userMapper.selectAll2(user);
        return PageInfo.of(list);
    }

    /**
     * 登录
     */
    public Account login(Account account) {
        Account dbUser = userMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbUser)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbUser.getPassword())) {
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        // 生成token
        String tokenData = dbUser.getId() + "-" + RoleEnum.USER.name();
        String token = TokenUtils.createToken(tokenData, dbUser.getPassword());
        dbUser.setToken(token);
        return dbUser;
    }

    /**
     * 注册
     */
    public void register(Account account) {
        User user = new User();
        BeanUtils.copyProperties(account, user);  //account拷贝到user中
        add(user);
    }

    /**
     * 修改密码
     */
    public void updatePassword(Account account) {
        User dbUser = userMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbUser)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbUser.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        dbUser.setPassword(account.getNewPassword());
        userMapper.updateById(dbUser);
    }

}