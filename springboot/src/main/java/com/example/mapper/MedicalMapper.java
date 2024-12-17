package com.example.mapper;

import com.example.entity.Medical;

import java.util.List;

/**
 * 操作medical相关数据接口
*/
public interface MedicalMapper {

    /**
      * 新增
    */
    int insert(Medical medical);

    /**
      * 删除
    */
    int deleteById(Integer id);

    /**
      * 修改
    */
    int updateById(Medical medical);

    /**
      * 根据ID查询
    */
    Medical selectById(String time);

    /**
      * 查询所有
    */
    List<Medical> selectAll(Medical medical);

}