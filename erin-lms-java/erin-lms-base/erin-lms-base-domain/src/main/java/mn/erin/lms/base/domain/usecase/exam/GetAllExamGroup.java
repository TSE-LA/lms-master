package mn.erin.lms.base.domain.usecase.exam;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.exam.ExamGroup;
import mn.erin.lms.base.domain.model.exam.ExamGroupId;
import mn.erin.lms.base.domain.model.exam.ExamGroupTree;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.LmsUseCase;

/**
 * @author Galsan Bayart.
 */
@Authorized(users = { Author.class, Manager.class, Instructor.class, Supervisor.class })
public class GetAllExamGroup extends LmsUseCase<String, List<ExamGroupTree>>
{
  public GetAllExamGroup(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(Objects.requireNonNull(lmsRepositoryRegistry), Objects.requireNonNull(lmsServiceRegistry));
  }

  @Override
  protected List<ExamGroupTree> executeImpl(String input) throws UseCaseException
  {
    try
    {
      OrganizationId organizationId = OrganizationId.valueOf(lmsServiceRegistry.getOrganizationIdProvider().getOrganizationId());
      GetExamGroupTree getExamGroupTree = new GetExamGroupTree(lmsRepositoryRegistry, lmsServiceRegistry);
      if (StringUtils.isBlank(input))
      {
        return getExamGroupTree.executeImpl(lmsRepositoryRegistry.getExamGroupRepository().findByOrganizationId(organizationId.getId()));
      }

      ExamGroupId id = ExamGroupId.valueOf(input);
      List<ExamGroup> examGroups = lmsRepositoryRegistry.getExamGroupRepository().findByParentIdAndOrganizationId(id, organizationId);
      examGroups.add(lmsRepositoryRegistry.getExamGroupRepository().findById(id));
      return getExamGroupTree.executeImpl(examGroups);
    }
    catch (NullPointerException | LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

