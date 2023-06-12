package mn.erin.lms.base.domain.usecase.course;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.course.LearnerCourseHistory;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.dto.CourseHistoryInput;

/**
 * @author Temuulen Naranbold
 */
public class GetLearnerCourseHistoryByDate extends CourseUseCase<CourseHistoryInput, List<LearnerCourseHistory>>
{
  public GetLearnerCourseHistoryByDate(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  public List<LearnerCourseHistory> execute(CourseHistoryInput input) throws UseCaseException
  {
    try
    {
      Validate.notBlank(input.getYear());
    }
    catch (NullPointerException | IllegalArgumentException e)
    {
      throw new UseCaseException(e.getMessage());
    }
    LocalDateTime startDate = null;
    LocalDateTime endDate = null;
    try
    {
      startDate = (new SimpleDateFormat("yyyy-MM-dd").parse(input.getYear() + "-01-01")).toInstant().atZone(ZoneId.of("Asia/Ulaanbaatar")).toLocalDateTime();
      endDate = (new SimpleDateFormat("yyyy-MM-dd").parse(input.getYear() + "-12-31")).toInstant().atZone(ZoneId.of("Asia/Ulaanbaatar")).toLocalDateTime();
    }
    catch (ParseException e)
    {
      e.printStackTrace();
    }

    String username = StringUtils.isBlank(input.getUsername()) ? lmsServiceRegistry.getAuthenticationService().getCurrentUsername() : input.getUsername();
    return lmsRepositoryRegistry.getLearnerCourseHistoryRepository().getAllByUserIdAndDate(username, startDate, endDate);
  }
}
