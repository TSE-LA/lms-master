package mn.erin.lms.base.domain.usecase.exam;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.*;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.exam.ExamId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.exam.dto.*;

import org.apache.commons.lang3.Validate;

/**
 * @author Oyungerel Chuluunsukh
 */

@Authorized(users = { LmsUser.class })
public class GetLearnerExam extends ExamUseCase<String, ExamLearnerDto>
{

  public GetLearnerExam(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  protected ExamLearnerDto executeImpl(String examId) throws UseCaseException, LmsRepositoryException
  {
    try
    {
        Validate.notBlank(examId);
      return this.toExamLearnerDto(examRepository.findById(ExamId.valueOf(examId)));
    }
    catch (LmsRepositoryException | NullPointerException | IllegalArgumentException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }
}

