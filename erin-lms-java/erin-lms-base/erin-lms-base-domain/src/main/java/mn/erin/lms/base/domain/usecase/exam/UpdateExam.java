package mn.erin.lms.base.domain.usecase.exam;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.category.ExamCategoryId;
import mn.erin.lms.base.domain.model.exam.Exam;
import mn.erin.lms.base.domain.model.exam.ExamGroupId;
import mn.erin.lms.base.domain.model.exam.ExamPublishConfig;
import mn.erin.lms.base.domain.model.exam.ExamPublishStatus;
import mn.erin.lms.base.domain.model.exam.ExamType;
import mn.erin.lms.base.domain.model.exam.HistoryOfModification;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.exam.ExamPublishTaskInfo;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamInput;
import mn.erin.lms.base.domain.util.DateUtil;

/**
 * @author Galsan Bayart
 */
@Authorized(users = { Author.class, Manager.class, Instructor.class, Supervisor.class }) 
public class UpdateExam extends ExamUseCase<ExamInput, String>
{
  public UpdateExam(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  protected String executeImpl(ExamInput input) throws UseCaseException
  {
    Validate.notNull(input);
    Validate.notBlank(input.getId());

    if (!input.getStartDate().before(input.getEndDate()) && !input.getStartDate().equals(input.getEndDate()))
    {
      throw new UseCaseException("End date must be in the future of start date!");
    }

    Exam exam = getExam(input.getId());
    boolean rescheduleExamStart = !exam.getActualStartDate().equals(DateUtil.combine(input.getStartDate(), input.getStartTime()));
    boolean rescheduleExamExpiration = !exam.getActualEndDate().equals(DateUtil.combine(input.getEndDate(), input.getEndTime()));
    boolean rescheduleExamPublication = !exam.getActualPublicationDate().equals(DateUtil.combine(input.getPublishDate(), input.getPublishTime()));

    String currentUser = lmsServiceRegistry.getAccessIdentityManagement().getCurrentUsername();
    ExamPublishConfig examPublishConfig = mapToExamPublishConfig(input);

    exam.setName(input.getName());
    exam.setDescription(input.getDescription());
    exam.setExamGroupId(ExamGroupId.valueOf(input.getGroupId()));
    exam.setExamCategoryId(ExamCategoryId.valueOf(input.getExamCategoryId()));
    exam.setExamType(ExamType.valueOf(input.getExamType()));
    exam.setModifiedDate(CURRENT_DATE);
    exam.setModifiedUser(currentUser);
    exam.addModifyInfo(new HistoryOfModification(currentUser, CURRENT_DATE));
    exam.setExamPublishConfig(examPublishConfig);
    exam.setExamConfig(mapToExamConfig(input));
    exam.setEnrolledLearners(input.getEnrolledLearners());
    exam.setEnrolledGroups(input.getEnrolledGroups());

    try
    {
      String id = examRepository.update(exam);

      scheduleFutureTasks(exam, rescheduleExamStart, rescheduleExamExpiration, rescheduleExamPublication, id);

      return id;
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private void scheduleFutureTasks(Exam exam, boolean rescheduleExamStart, boolean rescheduleExamExpiration, boolean rescheduleExamPublication, String id)
  {
    if (exam.getExamPublishStatus() != ExamPublishStatus.UNPUBLISHED)
    {
      if (rescheduleExamStart)
      {
        // update exam start schedule
        lmsServiceRegistry.getExamStartService().startExamOn(id, exam.getActualStartDate());
      }
      if (rescheduleExamExpiration)
      {
        // update exam expiration
        lmsServiceRegistry.getExamExpirationService().expireExamOn(id, exam.getActualEndDate());
      }
      if (rescheduleExamPublication)
      {
        // update exam publication schedule
        lmsServiceRegistry.getExamPublicationService().publishExamOn(new ExamPublishTaskInfo(exam), exam.getActualPublicationDate());
      }
    }
  }
}
