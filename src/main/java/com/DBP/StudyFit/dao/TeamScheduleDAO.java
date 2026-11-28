package com.DBP.StudyFit.dao;

import com.DBP.StudyFit.dto.ScheduleDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 팀 일정 DAO (MyBatis) - SQL은 resources/mapper/ScheduleMapper.xml
@Mapper
public interface TeamScheduleDAO {

    /** 팀의 전체 일정을 시작 시각 순으로 조회 */
    List<ScheduleDTO> findByTeamId(@Param("teamId") Long teamId);

    /** 일정 등록. scheduleId는 시퀀스(SEQ_SCHEDULE)로 채워져 dto에 반환된다. */
    int insert(ScheduleDTO schedule);
}