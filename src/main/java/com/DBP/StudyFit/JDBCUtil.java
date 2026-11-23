package com.DBP.StudyFit;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.sql.DataSource;
import java.sql.*;
import org.apache.commons.dbcp2.BasicDataSource; // Apache Commons DBCP2 라이브러리 이용

public class JDBCUtil {
    private String sql = null; // 실행할 SQL문
    private Object[] parameters = null; // PreparedStatement 의 매개변수 값을 저장하는 배열
    private static DataSource ds = null; // DBCP의 DataSource 객체 참조(공유)
    private static Connection conn = null; // Connection 객체 참조(공유)
    private PreparedStatement pstmt = null; // PreparedStatement 객체 참조
    private CallableStatement cstmt = null;
    private ResultSet rs = null; // ResultSet 객체 참조
    private static Properties prop = new Properties();

    { // static initializer
// property 파일에 저장된 DB 접속 정보를 Properties 객체로 읽어 들임 (예외 처리 생략)
        InputStream input
                = getClass().getResourceAsStream("/dbinfo.properties"); // DB 접속 정보 설정 파일
        try {
            prop.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initJDBCUtil() { // DBCP 초기화
        try {
            if (ds == null) { // DBCP DataSource 생성 및 설정
                BasicDataSource bds = new BasicDataSource();
                bds.setDriverClassName(prop.getProperty("db.driver"));
                bds.setUrl(prop.getProperty("db.url"));
                bds.setUsername(prop.getProperty("db.username"));
                bds.setPassword(prop.getProperty("db.password"));
                ds = bds;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public JDBCUtil() {
        initJDBCUtil();
    } // 기본 생성자

    public void setSql(String sql) { // sql 필드 setter
        this.sql = sql;
    }

    public void setParameters(Object[] parameters) { // parameters 필드 setter
        this.parameters = parameters;
    }

    public void setSqlAndParameters(String sql, Object[] params) { // SQL문 및 매개변수 저장
        this.sql = sql;
        this.parameters = parameters;
    }

    private int getParameterSize() { // 매개변수의 개수를 반환
        return parameters == null ? 0 : parameters.length;
    }

    private Object getParameter(int index) throws Exception {
// 매개변수 배열에서 특정 변수를 찾아 반환
        if (index >= getParameterSize())
            throw new Exception("INDEX 값이 파라미터의 갯수보다 많습니다.");
        return parameters[index];
    }

    private PreparedStatement getPreparedStatement() throws SQLException {
        if (conn == null) { // Connection이 없을 경우
            conn = ds.getConnection(); // 새로운 Connection 획득
            conn.setAutoCommit(false); // DML문에 대한 자동 commit 기능을 해제
        } // Connection이 있을 경우 재사용
        if (pstmt != null) pstmt.close(); // 기존 PreparedStatement 객체가 존재할 경우 반환
        pstmt = conn.prepareStatement(sql); // 새로운 PreparedStatement 객체 생성
        return pstmt;
    }

    public ResultSet executeQuery() { // PreparedStatement를 통해 질의 실행
        try {
            pstmt = getPreparedStatement(); // PreparedStatement 획득
            for (int i = 0; i < getParameterSize(); i++) {
                pstmt.setObject(i + 1, getParameter(i)); // 매개변수 존재 시 각각 설정
            }
            rs = pstmt.executeQuery(); // 질의 실행 후 ResultSet 객체 반환
            return rs;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public int executeUpdate() throws SQLException, Exception {
// PreparedStatement를 통해 DML문 실행
        pstmt = getPreparedStatement();
        for (int i = 0; i < getParameterSize(); i++) {
            if (getParameter(i) == null) {
                pstmt.setString(i + 1, null);
            } else {
                pstmt.setObject(i + 1, getParameter(i)); // 매개변수 설정
            }
        }
        return pstmt.executeUpdate(); // Insert/Delete/Update 실행 후 count 값 반환
    }

    public void close() { // 자원 해제
        if (rs != null) {
            try {
                rs.close();
                rs = null;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        if (pstmt != null) {
            try {
                pstmt.close();
                pstmt = null;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void commit() { // 트랜잭션 commit 수행
        try {
            conn.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void rollback() { // 트랜잭션 rollback 수행
        try {
            conn.rollback();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
