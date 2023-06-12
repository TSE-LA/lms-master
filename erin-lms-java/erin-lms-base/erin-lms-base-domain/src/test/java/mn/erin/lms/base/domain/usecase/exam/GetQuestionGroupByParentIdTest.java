package mn.erin.lms.base.domain.usecase.exam;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mn.erin.domain.base.model.person.PersonId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.LmsUserService;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.aim.user.LmsAdmin;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.domain.model.exam.question.QuestionGroup;
import mn.erin.lms.base.domain.model.exam.question.QuestionGroupId;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.repository.QuestionGroupRepository;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.OrganizationIdProvider;
import mn.erin.lms.base.domain.usecase.exam.question.GetQuestionGroupByParentId;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

/**
 * @author Temuulen Naranbold
 */
public class GetQuestionGroupByParentIdTest
{
  private LmsRepositoryRegistry lmsRepositoryRegistry;
  private LmsServiceRegistry lmsServiceRegistry;
  private QuestionGroupRepository questionGroupRepository;
  private OrganizationIdProvider organizationIdProvider;
  private LmsUserService lmsUserService;
  private GetQuestionGroupByParentId getQuestionGroupByParentId;

  @Before
  public void setUp()
  {
    lmsRepositoryRegistry = mock(LmsRepositoryRegistry.class);
    lmsServiceRegistry = mock(LmsServiceRegistry.class);
    questionGroupRepository = mock(QuestionGroupRepository.class);
    organizationIdProvider = mock(OrganizationIdProvider.class);
    lmsUserService = mock(LmsUserService.class);

    when(lmsRepositoryRegistry.getQuestionGroupRepository()).thenReturn(questionGroupRepository);
    when(lmsServiceRegistry.getOrganizationIdProvider()).thenReturn(organizationIdProvider);
    when(organizationIdProvider.getOrganizationId()).thenReturn("jarvis");
    when(lmsServiceRegistry.getLmsUserService()).thenReturn(lmsUserService);
    LmsUser user = new LmsAdmin(new PersonId("user"));
    when(lmsUserService.getCurrentUser()).thenReturn(user);

    getQuestionGroupByParentId = new GetQuestionGroupByParentId(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Test
  public void whenSuccess() throws UseCaseException
  {
    QuestionGroup group = new QuestionGroup(QuestionGroupId.valueOf("id"), null, "test", OrganizationId.valueOf("jarvis"), "testing");
    List<QuestionGroup> groups = new ArrayList<>();
    groups.add(group);
    groups.add(group);
    groups.add(group);
    when(questionGroupRepository.getAll(any(), any())).thenReturn(groups);
    Assert.assertEquals(getQuestionGroupByParentId.execute(null).size(), groups.size());
  }
}
