package com.example.controller;

import com.example.common.Result;
import com.example.entity.Medical;
import com.example.entity.Registration;
import com.example.service.MedicalService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 公告信息表前端操作接口
 **/
@RestController
@RequestMapping("/medical")
public class MedicalController {

    @Resource
    private MedicalService medicalService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Medical medical) {
        medicalService.add(medical);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        medicalService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        medicalService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 付费
     */
    @PutMapping("/pay")
    public Result payById(@RequestBody Medical medical) {
        medicalService.payById(medical);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Medical medical) {
        medicalService.updateById(medical);
        return Result.success();
    }

    /**
     * 退费
     */
    @PutMapping("/refund")
    public Result refundById(@RequestBody Medical medical) {
        medicalService.refundById(medical);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
//    @GetMapping("/selectById/{id}")
//    public Result selectById(@PathVariable Integer id) {
//        Medical medical = medicalService.selectById(id);
//        return Result.success(medical);
//    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Medical medical ) {
        List<Medical> list = medicalService.selectAll(medical);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Medical medical,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Medical> page = medicalService.selectPage(medical, pageNum, pageSize);
        return Result.success(page);
    }

}