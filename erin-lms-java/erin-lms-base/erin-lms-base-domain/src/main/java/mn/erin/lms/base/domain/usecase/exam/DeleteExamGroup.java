package mn.erin.lms.base.domain.usecase.exam;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.exam.Exam;
import mn.erin.lms.base.domain.model.exam.ExamGroup;
import mn.erin.lms.base.domain.model.exam.ExamGroupId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.LmsUseCase;

/**
 * @author Galsan Bayart.
 */
@Authorized(users = { Author.class, Manager.class, Instructor.class, Supervisor.class })
public class DeleteExamGroup extends LmsUseCase<String, Boolean>
{
  public DeleteExamGroup(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(Objects.requireNonNull(lmsRepositoryRegistry), Objects.requireNonNull(lmsServiceRegistry));
  }

  @Override
  protected Boolean executeImpl(String id) throws UseCaseException
  {
    try
    {
      Validate.notBlank(id);

      ExamGroup groupToDelete = lmsRepositoryRegistry.getExamGroupRepository().findById(ExamGroupId.valueOf(id));

      if (groupToDelete == null)
      {
        throw new UseCaseException("Could not find exam group with ID: " + id);
      }

      List<Exam> examsWithinGroup = lmsRepositoryRegistry.getExamRepository().listAllByGroup(groupToDelete.getId());
      if (examsWithinGroup != null && !examsWithinGroup.isEmpty())
      {
        throw new UseCaseException("Please delete all the exams under the group ID of: " + groupToDelete.getId().getId());
      }

      List<ExamGroup> childGroups = lmsRepositoryRegistry.getExamGroupRepository().findByParentIdAndOrganizationId(groupToDelete.getId(),
          OrganizationId.valueOf(lmsServiceRegistry.getOrganizationIdProvider().getOrganizationId()));
      if (childGroups != null && !childGroups.isEmpty())
      {
        throw new UseCaseException("Deleting group has child groups!");
      }

      return lmsRepositoryRegistry.getExamGroupRepository().delete(groupToDelete.getId());
    }
    catch (UseCaseException | LmsRepositoryException | NullPointerException | IllegalArgumentException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}
