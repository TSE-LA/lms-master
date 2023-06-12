package mn.erin.lms.base.domain.usecase.tracking;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.enrollment.EnrollmentState;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Learner;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.aim.LmsUserService;
import mn.erin.lms.base.domain.service.ProgressTrackingService;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.base.domain.usecase.course.CreateLearnerCourseHistory;
import mn.erin.lms.base.domain.usecase.course.dto.LearnerCourseHistoryInput;
import mn.erin.lms.base.domain.usecase.tracking.dto.SaveLearnerProgressInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Supervisor.class, Learner.class, Manager.class })
public class SaveLearnerProgress extends CourseUseCase<SaveLearnerProgressInput, Map<String, Object>>
{

  private final ProgressTrackingService progressTrackingService;
  private final LmsUserService lmsUserService;

  public SaveLearnerProgress(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.progressTrackingService = lmsServiceRegistry.getProgressTrackingService();
    this.lmsUserService = lmsServiceRegistry.getLmsUserService();
  }

  @Override
  public Map<String, Object> execute(SaveLearnerProgressInput input) throws UseCaseException
  {
    Validate.notNull(input);
    LmsUser currentUser = lmsUserService.getCurrentUser();

    if (currentUser instanceof Instructor || currentUser instanceof Author)
    {
      return new HashMap<>();
    }

    Map<String, Object> savedData = progressTrackingService.saveLearnerData(currentUser.getId().getId(), input.getCourseId(), input.getModuleName(),
        input.getData());
    Float progress = progressTrackingService.getLearnerProgress(currentUser.getId().getId(), input.getCourseId());

    EnrollmentState updatedEnrollmentState;
    if (progress == 100)
    {
      updatedEnrollmentState = EnrollmentState.COMPLETED;
      LearnerCourseHistoryInput historyInput = new LearnerCourseHistoryInput(CourseId.valueOf(input.getCourseId()), currentUser.getId().getId(), progress);
      CreateLearnerCourseHistory createLearnerCourseHistory = new CreateLearnerCourseHistory(lmsRepositoryRegistry, lmsServiceRegistry);
      createLearnerCourseHistory.execute(historyInput);
    }
    else
    {
      updatedEnrollmentState = EnrollmentState.IN_PROGRESS;
    }

    courseEnrollmentRepository.changeEnrollmentState(LearnerId.valueOf(currentUser.getId().getId()), CourseId.valueOf(input.getCourseId()),
        updatedEnrollmentState);
    return savedData;
  }
}
