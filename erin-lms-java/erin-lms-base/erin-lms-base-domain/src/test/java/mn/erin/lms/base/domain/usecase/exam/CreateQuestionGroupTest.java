package mn.erin.lms.base.domain.usecase.exam;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

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
import mn.erin.lms.base.domain.usecase.exam.dto.question.QuestionGroupInput;
import mn.erin.lms.base.domain.usecase.exam.question.CreateQuestionGroup;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

/**
 * @author Temuulen Naranbold
 */
public class CreateQuestionGroupTest
{
  private LmsRepositoryRegistry lmsRepositoryRegistry;
  private LmsServiceRegistry lmsServiceRegistry;
  private QuestionGroupRepository questionGroupRepository;
  private OrganizationIdProvider organizationIdProvider;
  private LmsUserService lmsUserService;
  private CreateQuestionGroup createQuestionGroup;

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
    when(lmsServiceRegistry.getLmsUserService()).thenReturn(lmsUserService);
    LmsUser user = new LmsAdmin(new PersonId("user"));
    when(lmsUserService.getCurrentUser()).thenReturn(user);

    createQuestionGroup = new CreateQuestionGroup(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Test(expected = UseCaseException.class)
  public void whenInput_isNull() throws UseCaseException
  {
    createQuestionGroup.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void whenOrganizationId_isNull() throws UseCaseException
  {
    when(organizationIdProvider.getOrganizationId()).thenReturn(null);
    createQuestionGroup.execute(new QuestionGroupInput(null, "test", "testing"));
  }

  @Test(expected = UseCaseException.class)
  public void whenRepository_isNull() throws UseCaseException
  {
    when(organizationIdProvider.getOrganizationId()).thenReturn("jarvis");
    when(lmsRepositoryRegistry.getQuestionGroupRepository()).thenReturn(null);
    createQuestionGroup.execute(new QuestionGroupInput(null, "test", "testing"));
  }

  @Test
  public void whenSuccess() throws UseCaseException
  {
    when(organizationIdProvider.getOrganizationId()).thenReturn("jarvis");
    QuestionGroupInput input = new QuestionGroupInput(null, "test", "testing");
    QuestionGroup group = new QuestionGroup(QuestionGroupId.valueOf("id"), null, "test", OrganizationId.valueOf("jarvis"), "testing");
    when(questionGroupRepository.create(any(), any(), any(),any())).thenReturn(group);
    Assert.assertEquals(createQuestionGroup.execute(input), group.getId().getId());
  }
}
