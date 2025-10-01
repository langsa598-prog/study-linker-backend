package com.study.service.studyschedule;

import com.study.service.studyschedule.domain.StudySchedule;
import com.study.service.studyschedule.dto.StudyScheduleRequest;
import com.study.service.studygroup.repository.StudyGroupRepository;
import com.study.service.studygroup.domain.StudyGroup;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StudyScheduleService {

    private final StudyScheduleRepository scheduleRepository;
    private final StudyGroupRepository groupRepository;

    public StudyScheduleService(StudyScheduleRepository scheduleRepository,
                                StudyGroupRepository groupRepository) {
        this.scheduleRepository = scheduleRepository;
        this.groupRepository = groupRepository;
    }

    public List<StudySchedule> findAll() {
        return scheduleRepository.findAll();
    }

    public StudySchedule findById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("스터디 스케줄을 찾을 수 없습니다. ID: " + id));
    }

    @Transactional
    public StudySchedule save(StudyScheduleRequest request) {
        StudyGroup group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다. ID: " + request.getGroupId()));

        StudySchedule schedule = new StudySchedule();
        schedule.setGroup(group);
        schedule.setTitle(request.getTitle());
        schedule.setDescription(request.getDescription());
        schedule.setLocation(request.getLocation());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        schedule.setStartTime(LocalDateTime.parse(request.getStartTime(), formatter));
        schedule.setEndTime(LocalDateTime.parse(request.getEndTime(), formatter));

        return scheduleRepository.save(schedule);
    }

    @Transactional
    public StudySchedule update(Long id, StudyScheduleRequest request) {
        StudySchedule schedule = findById(id);

        if (request.getGroupId() != null) {
            StudyGroup group = groupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다. ID: " + request.getGroupId()));
            schedule.setGroup(group);
        }

        schedule.setTitle(request.getTitle());
        schedule.setDescription(request.getDescription());
        schedule.setLocation(request.getLocation());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        schedule.setStartTime(LocalDateTime.parse(request.getStartTime(), formatter));
        schedule.setEndTime(LocalDateTime.parse(request.getEndTime(), formatter));

        return scheduleRepository.save(schedule);
    }

    public void deleteById(Long id) {
        scheduleRepository.deleteById(id);
    }
}