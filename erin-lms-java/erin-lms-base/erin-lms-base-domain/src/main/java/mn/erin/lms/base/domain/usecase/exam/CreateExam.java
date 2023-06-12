package mn.erin.lms.base.domain.usecase.exam;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.category.ExamCategoryId;
import mn.erin.lms.base.domain.model.exam.Exam;
import mn.erin.lms.base.domain.model.exam.ExamConstants;
import mn.erin.lms.base.domain.model.exam.ExamGroup;
import mn.erin.lms.base.domain.model.exam.ExamGroupId;
import mn.erin.lms.base.domain.model.exam.ExamPublishConfig;
import mn.erin.lms.base.domain.model.exam.ExamType;
import mn.erin.lms.base.domain.model.exam.HistoryOfModification;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.exam.dto.CreateUpdateExamOutput;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamInput;

/**
 * @author Galsan Bayart
 */
@Authorized(users = { Author.class, Manager.class, Instructor.class, Supervisor.class })
public class CreateExam extends ExamUseCase<ExamInput, CreateUpdateExamOutput>
{
  public CreateExam(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  protected CreateUpdateExamOutput executeImpl(ExamInput input) throws UseCaseException
  {
    try
    {
      Validate.notNull(input);
      Validate.notBlank(input.getName());
      Validate.notBlank(input.getExamCategoryId());

      if (!input.getStartDate().before(input.getEndDate()) && !input.getStartDate().equals(input.getEndDate()))
      {
        throw new UseCaseException("End date must be in the future of start date!");
      }

      String currentUser = lmsServiceRegistry.getAccessIdentityManagement().getCurrentUsername();
      HistoryOfModification historyOfModification = new HistoryOfModification(currentUser, CURRENT_DATE);
      List<HistoryOfModification> historyOfModifications = new ArrayList<>();
      historyOfModifications.add(historyOfModification);

      ExamPublishConfig examPublishConfig = mapToExamPublishConfig(input);
      ExamGroup examGroup = lmsRepositoryRegistry.getExamGroupRepository().findByIdAndOrganizationId(
          ExamGroupId.valueOf(input.getGroupId()), lmsServiceRegistry.getOrganizationIdProvider().getOrganizationId());

      Exam createdExam = examRepository.create(
          input.getName(), input.getDescription(),
          ExamCategoryId.valueOf(input.getExamCategoryId()),
          examGroup.getId(),
          ExamType.valueOf(input.getExamType()),
          historyOfModifications,
          examPublishConfig,
          mapToExamConfig(input)
      );
      Exam updatedExam = examRepository.updateEnrollments(createdExam.getId().getId(), input.getEnrolledLearners(), input.getEnrolledGroups());
      this.lmsRepositoryRegistry.getExamEnrollmentRepository().createEnrollment(updatedExam.getId().getId(), currentUser, ExamConstants.WRITE_PERMISSION);
      return new CreateUpdateExamOutput(updatedExam);
    }
    catch (LmsRepositoryException | NullPointerException | UseCaseException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}
