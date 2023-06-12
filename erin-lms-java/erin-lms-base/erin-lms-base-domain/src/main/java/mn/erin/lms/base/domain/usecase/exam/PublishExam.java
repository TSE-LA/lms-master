package mn.erin.lms.base.domain.usecase.exam;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.exam.Exam;
import mn.erin.lms.base.domain.model.exam.ExamId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.exam.ExamPublishTaskInfo;

/**
 * @author Galsan Bayart.
 */
@Authorized(users = { Author.class, Manager.class, Instructor.class, Supervisor.class })
public class PublishExam extends ExamUseCase<String, String>
{
  public PublishExam(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  public String executeImpl(String id) throws UseCaseException
  {
    try
    {
      Exam exam = lmsRepositoryRegistry.getExamRepository().findById(ExamId.valueOf(id));

      // schedule exam start
      if (exam.getExamConfig().getStartDate() != null)
      {
        this.lmsServiceRegistry.getExamStartService().startExamOn(exam.getId().getId(), exam.getActualStartDate());
      }
      else
      {
        throw new UseCaseException("Exam start date cannot be null!");
      }
      // schedule exam expiration
      if (exam.getExamConfig().getEndDate() != null)
      {
        this.lmsServiceRegistry.getExamExpirationService().expireExamOn(exam.getId().getId(), exam.getActualEndDate());
      }
      else
      {
        throw new UseCaseException("Exam end date cannot be null!");
      }

      // schedule exam publication
      lmsServiceRegistry.getExamPublicationService().publishExamOn(new ExamPublishTaskInfo(exam), exam.getActualPublicationDate());

      return "true";
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }
}
