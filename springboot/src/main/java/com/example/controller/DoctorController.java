package com.example.controller;

import com.example.common.Result;
import com.example.entity.Doctor;
import com.example.service.DoctorService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 *  医生前端操作接口
 **/
@RestController        //接待员，处理所有请求，告诉服务器这是rest请求
// 表示这是一个处理 REST 请求的控制器
// 相当于 @Controller + @ResponseBody
// 自动将返回值转换为 JSON 格式
@RequestMapping("/doctor")   // 基础路径    //部门牌
public class DoctorController {

    @Resource
    private DoctorService doctorService;

    /**
     * 新增
     */
    @PostMapping("/add")  //最终路径: /doctor/add    //服务窗口
    public Result add(@RequestBody Doctor doctor) {
        doctorService.add(doctor);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        doctorService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        doctorService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Doctor doctor) {
        doctorService.updateById(doctor);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Doctor doctor = doctorService.selectById(id);
        return Result.success(doctor);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Doctor doctor ) {
        List<Doctor> list = doctorService.selectAll(doctor);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Doctor doctor,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Doctor> page = doctorService.selectPage(doctor, pageNum, pageSize);
        return Result.success(page);
    }


    /**
     * 分页查询-挂号
     */
    @GetMapping("/selectPage2")
    public Result selectPage2(Doctor doctor,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Doctor> page = doctorService.selectPage2(doctor, pageNum, pageSize);
        return Result.success(page);
    }

}