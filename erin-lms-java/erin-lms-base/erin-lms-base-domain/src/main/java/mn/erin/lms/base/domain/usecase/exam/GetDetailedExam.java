package mn.erin.lms.base.domain.usecase.exam;

import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.exam.ExamId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamDto;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;

/**
 * @author Oyungerel Chuluunsukh
 */

@Authorized(users = { Author.class, Instructor.class })
public class GetDetailedExam extends ExamUseCase<String, ExamDto>
{
  public GetDetailedExam(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  protected ExamDto executeImpl(String examId) throws UseCaseException, LmsRepositoryException
  {
    try
    {
      return this.toDetailedExamDto(examRepository.findById(ExamId.valueOf(examId)));
    }
    catch (LmsRepositoryException | NullPointerException | IllegalArgumentException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }
}
