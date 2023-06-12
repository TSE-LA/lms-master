//package mn.erin.lms.base.domain.usecase.exam;
//
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Mockito;
//
//import mn.erin.domain.base.model.person.PersonId;
//import mn.erin.domain.base.usecase.UseCaseException;
//import mn.erin.lms.base.aim.AccessIdentityManagement;
//import mn.erin.lms.base.aim.LmsUserService;
//import mn.erin.lms.base.aim.user.LmsAdmin;
//import mn.erin.lms.base.aim.user.LmsUser;
//import mn.erin.lms.base.domain.model.category.QuestionCategoryId;
//import mn.erin.lms.base.domain.model.exam.question.AnswerEntity;
//import mn.erin.lms.base.domain.model.exam.question.Question;
//import mn.erin.lms.base.domain.model.exam.question.QuestionDetail;
//import mn.erin.lms.base.domain.model.exam.question.QuestionType;
//import mn.erin.lms.base.domain.repository.LmsRepositoryException;
//import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
//import mn.erin.lms.base.domain.repository.QuestionCategoryRepository;
//import mn.erin.lms.base.domain.repository.QuestionRepository;
//import mn.erin.lms.base.domain.service.LmsServiceRegistry;
//import mn.erin.lms.base.domain.usecase.exam.dto.question.QuestionInput;
//import mn.erin.lms.base.domain.usecase.exam.question.CreateQuestion;
//
///**
// * @author Temuulen Naranbold
// */
//public class CreateQuestionTest
//{
//  private static final String QUESTION_ID = "questionId";
//  private static final String TITLE = "title";
//  private static final String QUESTION_CATEGORY_ID = "categoryId";
//  private static final String QUESTION_STATE_ID = "stateId";
//  private static final String QUESTION_TYPE = "OPEN_QUESTION";
//  private static final int SCORE = 13;
//  private static final String PERSON_ID = "personId";
//
//  private LmsRepositoryRegistry lmsRepositoryRegistry;
//  private LmsServiceRegistry lmsServiceRegistry;
//  private LmsUserService lmsUserService;
//  private AccessIdentityManagement accessIdentityManagement;
//  private QuestionRepository questionRepository;
//  private QuestionCategoryRepository questionCategoryRepository;
//  private QuestionInput input;
//  private CreateQuestion createQuestion;
//
//  @Before
//  public void setUp() throws LmsRepositoryException
//  {
//    lmsRepositoryRegistry = Mockito.mock(LmsRepositoryRegistry.class);
//    lmsServiceRegistry = Mockito.mock(LmsServiceRegistry.class);
//    lmsUserService = Mockito.mock(LmsUserService.class);
//    accessIdentityManagement = Mockito.mock(AccessIdentityManagement.class);
//    questionRepository = Mockito.mock(QuestionRepository.class);
//    questionCategoryRepository = Mockito.mock(QuestionCategoryRepository.class);
//    input = generateInput();
//    PersonId personId = new PersonId(PERSON_ID);
//    LmsUser user = new LmsAdmin(personId);
//    Mockito.when(lmsServiceRegistry.getAccessIdentityManagement()).thenReturn(accessIdentityManagement);
//    Mockito.when(lmsServiceRegistry.getLmsUserService()).thenReturn(lmsUserService);
//    Mockito.when(lmsUserService.getCurrentUser()).thenReturn(user);
//    Mockito.when(accessIdentityManagement.getCurrentUserId()).thenReturn(PERSON_ID);
//    Mockito.when(questionRepository.create(Mockito.anyString(), Mockito.any(QuestionDetail.class),  Mockito.any(QuestionType.class), Mockito.anyList(), Mockito.anyInt())).thenReturn();
//    Mockito.when(questionCategoryRepository.exists(Mockito.any(QuestionCategoryId.class))).thenReturn(true);
//    Mockito.when(lmsRepositoryRegistry.getQuestionRepository()).thenReturn(questionRepository);
//    Mockito.when(lmsRepositoryRegistry.getQuestionCategoryRepository()).thenReturn(questionCategoryRepository);
//    createQuestion = new CreateQuestion(lmsRepositoryRegistry, lmsServiceRegistry);
//  }
//
//  @Test(expected = NullPointerException.class)
//  public void whenInput_isNull() throws UseCaseException
//  {
//    createQuestion.execute(null);
//  }
//
//  @Test
//  public void whenInput_isGiven() throws UseCaseException
//  {
//    Assert.assertEquals(QUESTION_ID, createQuestion.execute(input));
//  }
//
//  private QuestionInput generateInput()
//  {
//    String groupId = "group";
//    QuestionInput.Builder builder = new QuestionInput.Builder()
//        .ofQuestionId(QUESTION_ID)
//        .ofTitle(TITLE)
//        .ofQuestionCategoryId(QUESTION_CATEGORY_ID)
//        .ofQuestionStateId(QUESTION_STATE_ID)
//        .ofQuestionType(QUESTION_TYPE)
//        .ofAnswer(new Object())
//        .ofScore(SCORE)
//        .ofGroupId(groupId);
//    return builder.build();
//  }
//}
