package com.example.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.example.common.enums.RoleEnum;
import com.example.common.enums.StatusEnum;
import com.example.entity.Account;
import com.example.entity.Record;
import com.example.mapper.RecordMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 公告信息表业务处理
 **/
@Service
public class RecordService {

    @Resource
    private RecordMapper recordMapper;

    /**
     * 新增
     */
    public void add(Record record) {
        //叫号逻辑，看一下患者记录，当天是否已经有叫号记录，重复叫号功能不新增
        String time = DateUtil.format(new Date(), "yyyy-MM-dd");
        record.setTime(time);
        List<Record> records = recordMapper.selectAll(record);
        if(CollectionUtil.isEmpty(records)){
            record.setInhospitalRecord(StatusEnum.NO.status);
            recordMapper.insert(record);
        }
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        recordMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            recordMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Record record) {
        recordMapper.updateById(record);
    }

    /**
     * 根据ID查询
     */
    public Record selectById(Integer id) {
        return recordMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Record> selectAll(Record record) {
        return recordMapper.selectAll(record);
    }

    /**
     * 分页查询
     */
    public PageInfo<Record> selectPage(Record record, Integer pageNum, Integer pageSize) {
        //医生和患者分别只能看到自己的就诊
        Account currentUser = TokenUtils.getCurrentUser();
        if(RoleEnum.USER.name().equals(currentUser.getRole())){
            record.setUserId(currentUser.getId());
        }
        if(RoleEnum.DOCTOR.name().equals(currentUser.getRole())){
            record.setDoctorId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Record> list = recordMapper.selectAll(record);
        return PageInfo.of(list);
    }

}