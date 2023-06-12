package mn.erin.lms.base.mongo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.domain.aim.service.AimApplicationDataChecker;
import mn.erin.domain.aim.service.UserDataResult;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.scorm.repository.RuntimeDataRepository;

public class LmsApplicationDataChecker implements AimApplicationDataChecker
{
  private final LmsRepositoryRegistry repositoryRegistry;
  private final RuntimeDataRepository runtimeDataRepository;

  public LmsApplicationDataChecker(LmsRepositoryRegistry repositoryRegistry, RuntimeDataRepository runtimeDataRepository)
  {
    this.repositoryRegistry = repositoryRegistry;
    this.runtimeDataRepository = runtimeDataRepository;
  }

  @Override
  public UserDataResult hasAssociatedData(String username)
  {
    if (username == null)
    {
      return new UserDataResult(false);
    }

    LearnerId learnerId = LearnerId.valueOf(username);

    Set<String> courseEnrollments = repositoryRegistry.getCourseEnrollmentRepository().listAll(learnerId).stream()
        .map(courseEnrollment -> courseEnrollment.getCourseId().getId()).collect(
            Collectors.toSet());
    Set<String> allRuntimeData = runtimeDataRepository.listRuntimeData(username).stream()
        .map(runtimeData -> runtimeData.getRuntimeDataId().getId()).collect(
            Collectors.toSet());

    UserDataResult userDataResult = new UserDataResult(!courseEnrollments.isEmpty() || !allRuntimeData.isEmpty());
    userDataResult.addCourses(courseEnrollments);
    userDataResult.addRuntimeData(allRuntimeData);

    return userDataResult;
  }

  @Override
  public Map<String, UserDataResult> getUserCourseData()
  {
    Map<String, UserDataResult> result = new HashMap<>();
    List<CourseEnrollment> allEnrollments = repositoryRegistry.getCourseEnrollmentRepository().listAll();
    Map<String, List<String>> allRuntimeData = runtimeDataRepository.listAllRuntimeData();
    for (CourseEnrollment enrollment : allEnrollments)
    {
      result.putIfAbsent(enrollment.getLearnerId().getId(), new UserDataResult(true));
      result.get(enrollment.getLearnerId().getId()).addCourse(enrollment.getCourseId().getId());
    }
    for (Map.Entry<String, List<String>> runtimeData : allRuntimeData.entrySet())
    {
      result.putIfAbsent(runtimeData.getKey(), new UserDataResult(true));
      result.get(runtimeData.getKey()).addRuntimeData(runtimeData.getValue());
    }
    return result;
  }
}
