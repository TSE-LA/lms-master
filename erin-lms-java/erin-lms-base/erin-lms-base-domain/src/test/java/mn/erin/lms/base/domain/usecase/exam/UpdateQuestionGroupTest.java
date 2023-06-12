package mn.erin.lms.base.domain.usecase.exam;

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
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.repository.QuestionGroupRepository;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.exam.dto.question.QuestionGroupInput;
import mn.erin.lms.base.domain.usecase.exam.question.UpdateQuestionGroup;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

/**
 * @author Temuulen Naranbold
 */
public class UpdateQuestionGroupTest
{
  private LmsRepositoryRegistry lmsRepositoryRegistry;
  private LmsServiceRegistry lmsServiceRegistry;
  private QuestionGroupRepository questionGroupRepository;
  private LmsUserService lmsUserService;
  private UpdateQuestionGroup updateQuestionGroup;

  @Before
  public void setUp()
  {
    lmsRepositoryRegistry = mock(LmsRepositoryRegistry.class);
    lmsServiceRegistry = mock(LmsServiceRegistry.class);
    questionGroupRepository = mock(QuestionGroupRepository.class);
    lmsUserService = mock(LmsUserService.class);

    when(lmsRepositoryRegistry.getQuestionGroupRepository()).thenReturn(questionGroupRepository);
    when(lmsServiceRegistry.getLmsUserService()).thenReturn(lmsUserService);
    LmsUser user = new LmsAdmin(new PersonId("user"));
    when(lmsUserService.getCurrentUser()).thenReturn(user);

    updateQuestionGroup = new UpdateQuestionGroup(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Test(expected = UseCaseException.class)
  public void whenInput_isNull() throws UseCaseException
  {
    updateQuestionGroup.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void whenGroup_notFound() throws LmsRepositoryException, UseCaseException
  {
    when(questionGroupRepository.findById(any())).thenThrow(LmsRepositoryException.class);
    updateQuestionGroup.execute(new QuestionGroupInput(null, "test", "testing"));
  }

  @Test
  public void whenSuccess() throws UseCaseException, LmsRepositoryException
  {
    QuestionGroupInput input = new QuestionGroupInput("id",null, "test", "testing");
    QuestionGroup group = new QuestionGroup(QuestionGroupId.valueOf("id"), null, "test", OrganizationId.valueOf("jarvis"), "testing");
    when(questionGroupRepository.findById(QuestionGroupId.valueOf(input.getId()))).thenReturn(group);
    when(questionGroupRepository.update(any())).thenReturn(input.getId());
    Assert.assertEquals(updateQuestionGroup.execute(input), group.getId().getId());
  }
}
