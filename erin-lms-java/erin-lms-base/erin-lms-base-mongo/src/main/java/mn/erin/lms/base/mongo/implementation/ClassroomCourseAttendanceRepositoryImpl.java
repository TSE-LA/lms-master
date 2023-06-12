package mn.erin.lms.base.mongo.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.model.classroom_course.ClassroomCourseAttendance;
import mn.erin.lms.base.domain.model.classroom_course.Attendance;
import mn.erin.lms.base.domain.repository.ClassroomCourseAttendanceRepository;
import mn.erin.lms.base.mongo.document.course.MongoAttendance;
import mn.erin.lms.base.mongo.document.course.MongoClassroomCourseAttendance;
import mn.erin.lms.base.mongo.repository.MongoClassroomCourseAttendanceRepository;

/**
 * @author Munkh
 */
public class ClassroomCourseAttendanceRepositoryImpl implements ClassroomCourseAttendanceRepository
{
  public static final String NOT_FOUND = "not found";
  private final MongoClassroomCourseAttendanceRepository mongoClassroomCourseAttendanceRepository;

  public ClassroomCourseAttendanceRepositoryImpl(MongoClassroomCourseAttendanceRepository mongoClassroomCourseAttendanceRepository)
  {
    this.mongoClassroomCourseAttendanceRepository = mongoClassroomCourseAttendanceRepository;
  }

  @Override
  public void save(ClassroomCourseAttendance courseAttendance)
  {
    List<MongoAttendance> mongoAttendances = courseAttendance.getAttendances().stream()
        .map(attendance -> {
          MongoAttendance mongoAttendance = new MongoAttendance(attendance.getLearnerId().getId(), attendance.isPresent(), attendance.getScores());
          mongoAttendance.setInvited(attendance.isInvited());
          mongoAttendance.setGroupName(attendance.getGroupName());
          mongoAttendance.setSupervisorId(attendance.getSupervisorId() != null ? attendance.getSupervisorId().getId() : null);
          return mongoAttendance;
        })
        .collect(Collectors.toList());
    deleteCourseAttendance(courseAttendance.getCourseId());
    mongoClassroomCourseAttendanceRepository.save(new MongoClassroomCourseAttendance(courseAttendance.getCourseId().getId(), mongoAttendances));
  }

  @Override
  public ClassroomCourseAttendance findByCourseId(CourseId courseId) throws LmsRepositoryException
  {
    MongoClassroomCourseAttendance mongoCourseAttendance = mongoClassroomCourseAttendanceRepository.findById(courseId.getId()).orElse(null);
    if (mongoCourseAttendance == null)
    {
      throw new LmsRepositoryException(NOT_FOUND);
    }
    List<Attendance> attendances = new ArrayList<>();
    for (MongoAttendance mongoAttendance : mongoCourseAttendance.getAttendances())
    {
      Attendance attendance = new Attendance(LearnerId.valueOf(mongoAttendance.getLearnerId()), mongoAttendance.isPresent(), mongoAttendance.getScores());
      attendance.setGroupName(mongoAttendance.getGroupName());
      attendance.setInvited(mongoAttendance.isInvited());
      attendance.setSupervisorId(mongoAttendance.getSupervisorId() != null ? LearnerId.valueOf(mongoAttendance.getSupervisorId()) : null);
      attendances.add(attendance);
    }
    return new ClassroomCourseAttendance(CourseId.valueOf(mongoCourseAttendance.getId()), attendances);
  }

  @Override
  public List<ClassroomCourseAttendance>  fetchAll()
  {
    List<MongoClassroomCourseAttendance> mongoClassroomCourseAttendances = mongoClassroomCourseAttendanceRepository.findAll();
    return mongoClassroomCourseAttendances.stream()
        .map(mongoCourseAttendance -> new ClassroomCourseAttendance(CourseId.valueOf(mongoCourseAttendance.getId()),
            mongoCourseAttendance.getAttendances().stream().map(attendance -> {
              Attendance mapped = new Attendance(LearnerId.valueOf(attendance.getLearnerId()), attendance.isPresent(), attendance.getScores());
              mapped.setInvited(attendance.isInvited());
              mapped.setGroupName(attendance.getGroupName());
              mapped.setSupervisorId(attendance.getSupervisorId() != null ? LearnerId.valueOf(attendance.getSupervisorId()) : null);
              return mapped;
            }).collect(Collectors.toList()))).collect(Collectors.toList());
  }

  @Override
  public Attendance findByCourseIdAndLearnerId(CourseId courseId, LearnerId learnerId) throws LmsRepositoryException
  {
    MongoClassroomCourseAttendance mongoClassroomCourseAttendance = mongoClassroomCourseAttendanceRepository.findById(courseId.getId()).orElse(null);
    if (mongoClassroomCourseAttendance == null)
    {
      throw new LmsRepositoryException(NOT_FOUND);
    }
    Optional<MongoAttendance> first = mongoClassroomCourseAttendance.getAttendances().stream()
        .filter(mongoAttendance -> mongoAttendance.getLearnerId().equals(learnerId.getId())).findFirst();
    if (!first.isPresent())
    {
      throw new LmsRepositoryException(NOT_FOUND);
    }
    MongoAttendance mongoCourseAttendance = first.get();
    return new Attendance(LearnerId.valueOf(mongoCourseAttendance.getLearnerId()), mongoCourseAttendance.isPresent(), mongoCourseAttendance.getScores());
  }

  @Override
  public List<ClassroomCourseAttendance> findByLearnerId(LearnerId learnerId)
  {
    List<MongoClassroomCourseAttendance> mongoClassroomCourseAttendances = mongoClassroomCourseAttendanceRepository
        .findAllByAttendancesRegex(learnerId.getId());
    for (MongoClassroomCourseAttendance courseAttendance : mongoClassroomCourseAttendances)
    {
      List<MongoAttendance> attendances = courseAttendance.getAttendances();
      attendances = attendances.stream().filter(attendance -> attendance.getLearnerId().equals(learnerId.getId())).collect(Collectors.toList());
      courseAttendance.setAttendances(attendances);
    }

    return mongoClassroomCourseAttendances.stream()
        .map(mongoCourseAttendance -> new ClassroomCourseAttendance(CourseId.valueOf(mongoCourseAttendance.getId()),
            mongoCourseAttendance.getAttendances().stream().map(attendance -> {
              Attendance mapped = new Attendance(LearnerId.valueOf(attendance.getLearnerId()), attendance.isPresent(), attendance.getScores());
              mapped.setInvited(attendance.isInvited());
              mapped.setGroupName(attendance.getGroupName());
              mapped.setSupervisorId(attendance.getSupervisorId() != null ? LearnerId.valueOf(attendance.getSupervisorId()) : null);
              return mapped;
            }).collect(Collectors.toList()))).collect(Collectors.toList());
  }

  @Override
  public boolean deleteCourseAttendance(CourseId courseId)
  {
    try
    {
      mongoClassroomCourseAttendanceRepository.deleteById(courseId.getId());
      return true;
    }
    catch (IllegalArgumentException e)
    {
      return false;
    }
  }
}
