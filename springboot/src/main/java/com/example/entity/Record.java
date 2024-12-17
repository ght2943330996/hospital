package com.example.entity;

import java.io.Serializable;

/**
 * 公告信息表
*/
public class Record implements Serializable {
    private static final long serialVersionUID = 1L;

    /** ID */
    private Integer id;
    /** 标题 */
    private Integer userId;
    private Integer doctorId;
    private String time;
    private String medicalRecord;
    private String inhospital;
    private String inhospitalRecord;
    private String comment;
    private String userName;
    private String doctorName;
    private String medical;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMedicalRecord() {
        return medicalRecord;
    }

    public void setMedicalRecord(String medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    public String getInhospital() {
        return inhospital;
    }

    public void setInhospital(String inhospital) {
        this.inhospital = inhospital;
    }

    public String getInhospitalRecord() {
        return inhospitalRecord;
    }

    public void setInhospitalRecord(String inhospitalRecord) {
        this.inhospitalRecord = inhospitalRecord;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getMedical() {
        return medical;
    }

    public void setMedical(String medical) {
        this.medical = medical;
    }
}