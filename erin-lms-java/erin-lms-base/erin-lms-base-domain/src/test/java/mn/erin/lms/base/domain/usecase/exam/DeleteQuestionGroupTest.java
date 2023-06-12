package mn.erin.lms.base.domain.usecase.exam;

import java.util.ArrayList;
import java.util.Date;
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
import mn.erin.lms.base.domain.model.exam.question.Question;
import mn.erin.lms.base.domain.model.exam.question.QuestionGroup;
import mn.erin.lms.base.domain.model.exam.question.QuestionGroupId;
import mn.erin.lms.base.domain.model.exam.question.QuestionId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.repository.QuestionGroupRepository;
import mn.erin.lms.base.domain.repository.QuestionRepository;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.OrganizationIdProvider;
import mn.erin.lms.base.domain.usecase.exam.question.DeleteQuestionGroup;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
/**
 * @author Temuulen Naranbold
 */
public class DeleteQuestionGroupTest
{
  private LmsRepositoryRegistry lmsRepositoryRegistry;
  private LmsServiceRegistry lmsServiceRegistry;
  private QuestionGroupRepository questionGroupRepository;
  private QuestionRepository questionRepository;
  private LmsUserService lmsUserService;
  private QuestionGroup questionGroup;
  private OrganizationIdProvider organizationIdProvider;
  private DeleteQuestionGroup deleteQuestionGroup;

  @Before
  public void setUp() throws LmsRepositoryException
  {
    lmsRepositoryRegistry = mock(LmsRepositoryRegistry.class);
    lmsServiceRegistry = mock(LmsServiceRegistry.class);
    questionGroupRepository = mock(QuestionGroupRepository.class);
    questionRepository = mock(QuestionRepository.class);
    lmsUserService = mock(LmsUserService.class);
    organizationIdProvider = mock(OrganizationIdProvider.class);

    when(lmsRepositoryRegistry.getQuestionGroupRepository()).thenReturn(questionGroupRepository);
    when(lmsRepositoryRegistry.getQuestionRepository()).thenReturn(questionRepository);
    when(lmsServiceRegistry.getLmsUserService()).thenReturn(lmsUserService);
    when(lmsServiceRegistry.getOrganizationIdProvider()).thenReturn(organizationIdProvider);
    when(organizationIdProvider.getOrganizationId()).thenReturn("jarvis");

    LmsUser user = new LmsAdmin(new PersonId("user"));
    when(lmsUserService.getCurrentUser()).thenReturn(user);

    questionGroup = new QuestionGroup(
        QuestionGroupId.valueOf("id"),
        QuestionGroupId.valueOf("parentId"),
        "Test",
        OrganizationId.valueOf("jarvis")
    );
    when(questionGroupRepository.findById(any())).thenReturn(questionGroup);

    deleteQuestionGroup = new DeleteQuestionGroup(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Test(expected = UseCaseException.class)
  public void whenInput_isNull() throws UseCaseException
  {
    deleteQuestionGroup.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void whenInput_isBlank() throws UseCaseException
  {
    deleteQuestionGroup.execute("");
  }

  @Test(expected = UseCaseException.class)
  public void whenGroup_notFound() throws UseCaseException, LmsRepositoryException
  {
    when(questionGroupRepository.findById(any())).thenReturn(null);
    deleteQuestionGroup.execute("id");
  }

  @Test(expected = UseCaseException.class)
  public void whenGroup_hasQuestions() throws LmsRepositoryException, UseCaseException
  {
    Question question = new Question.Builder(
        QuestionId.valueOf("id"),
        "Test",
        "Author",
        new Date()
    ).build();
    List<Question> questions = new ArrayList<>();
    questions.add(question);
    when(questionRepository.getAllActive(any(QuestionGroupId.class))).thenReturn(questions);
    deleteQuestionGroup.execute("id");
  }

  @Test(expected = UseCaseException.class)
  public void whenGroup_hasChildGroups() throws LmsRepositoryException, UseCaseException
  {
    when(questionRepository.getAllActive(any(QuestionGroupId.class))).thenReturn(null);

    List<QuestionGroup> questionGroups = new ArrayList<>();
    questionGroups.add(questionGroup);
    when(questionGroupRepository.getAll(any(), any())).thenReturn(questionGroups);
    deleteQuestionGroup.execute("id");
  }

  @Test
  public void when_success() throws LmsRepositoryException, UseCaseException
  {
    when(questionRepository.getAllActive(any(QuestionGroupId.class))).thenReturn(null);
    when(questionGroupRepository.getAll(any(), any())).thenReturn(null);
    when(questionGroupRepository.delete(any())).thenReturn(true);
    Assert.assertTrue(deleteQuestionGroup.execute("id"));
  }
}
