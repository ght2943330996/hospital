package com.example.mapper;

import com.example.entity.Doctor;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 操作doctor相关数据接口
*/
public interface DoctorMapper {

    /**
      * 新增
    */
    int insert(Doctor doctor);

    /**
      * 删除
    */
    int deleteById(Integer id);

    /**
      * 修改
    */
    int updateById(Doctor doctor);

    /**
      * 根据ID查询
    */
    Doctor selectById(Integer id);

    /**
      * 查询所有
    */
    List<Doctor> selectAll(Doctor doctor);
    /**
     * 查询所有card
     */
    List<Doctor> selectAll2(Doctor doctor);

    @Select("select * from doctor where username = #{username}")
    Doctor selectByUsername(String username);
}