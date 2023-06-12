package mn.erin.lms.base.domain.usecase.course;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.course.LearnerCourseHistory;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.dto.CourseHistoryInput;
import mn.erin.lms.base.domain.usecase.course.dto.LearnerCreditDto;

/**
 * @author Temuulen Naranbold
 */
public class GetLearnerCredit extends CourseUseCase<CourseHistoryInput, LearnerCreditDto>
{
  public GetLearnerCredit(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  public LearnerCreditDto execute(CourseHistoryInput input) throws UseCaseException
  {
    try
    {
      Validate.notBlank(input.getYear());
    }
    catch (NullPointerException | IllegalArgumentException e)
    {
      throw new UseCaseException(e.getMessage());
    }

    String username = StringUtils.isBlank(input.getUsername()) ? lmsServiceRegistry.getAuthenticationService().getCurrentUsername() : input.getUsername();

    List<LearnerCourseHistory> learnerCourseHistories = lmsRepositoryRegistry.getLearnerCourseHistoryRepository().getAllByUserId(username);
    double totalCredit = 0;
    double yearCredit = 0;

    for (LearnerCourseHistory learnerCourseHistory : learnerCourseHistories)
    {
      if (learnerCourseHistory.getCompletionDate().getYear() == Integer.parseInt(input.getYear()))
      {
        yearCredit += learnerCourseHistory.getCredit();
      }
      totalCredit += learnerCourseHistory.getCredit();
    }

    return new LearnerCreditDto(totalCredit, yearCredit);
  }
}
