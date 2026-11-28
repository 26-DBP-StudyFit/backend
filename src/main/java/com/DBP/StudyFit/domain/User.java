package com.DBP.StudyFit.domain;
import java.util.Date;

public class User {
    public static final double DEFAULT_TEMPERATURE = 36.5;

    // 필드: 회원번호, 이메일(로그인 아이디), 비밀번호, 이름, 성별, 전공, 학번, 계정상태, 가입일시, 온도
    // MEMBER 테이블 컬럼과 대응: userId → member_id, password → password_hash, name → member_name

    private int userId;
    private String email;
    private String password;
    private String name;
    private String gender;      // 'M' 또는 'F'
    private String major;
    private String studentId;
    private String status;      // 'A'(정상) 또는 'W'(탈퇴)
    private Date createdAt;
    private Double temperature = DEFAULT_TEMPERATURE;  // 회원 온도 (가입 시 36.5, null 허용)

    public User() {

    }

    public User(String email, String password, String name, String gender, String major, String studentId) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.major = major;
        this.studentId = studentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return userId + ", " + email + ", " + name + ", " + gender + ", " + major + ", "
                + studentId + ", " + status + ", " + createdAt + ", " + temperature;
    }

}

