package mn.erin.lms.base.domain.usecase.exam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.domain.model.exam.Exam;
import mn.erin.lms.base.domain.model.exam.ExamConfig;
import mn.erin.lms.base.domain.model.exam.ExamId;
import mn.erin.lms.base.domain.model.exam.RandomQuestionConfig;
import mn.erin.lms.base.domain.model.exam.ShowAnswerResult;
import mn.erin.lms.base.domain.model.exam.question.Answer;
import mn.erin.lms.base.domain.model.exam.question.LearnerAnswerEntity;
import mn.erin.lms.base.domain.model.exam.question.LearnerQuestionDto;
import mn.erin.lms.base.domain.model.exam.question.Question;
import mn.erin.lms.base.domain.model.exam.question.QuestionStatus;
import mn.erin.lms.base.domain.repository.ExamRepository;
import mn.erin.lms.base.domain.repository.ExamRuntimeDataRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.repository.QuestionRepository;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.exam.ExamInteractionService;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamInteractionDto;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamRuntimeData;

import static mn.erin.lms.base.domain.usecase.exam.ExamTestUtils.AUTHOR;
import static mn.erin.lms.base.domain.usecase.exam.ExamTestUtils.CURRENT_DATE;
import static mn.erin.lms.base.domain.usecase.exam.ExamTestUtils.EXAM_ID;
import static mn.erin.lms.base.domain.usecase.exam.ExamTestUtils.NAME;
import static mn.erin.lms.base.domain.usecase.exam.ExamTestUtils.generateQuestions;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

/**
 * @author Temuulen Naranbold
 */
@Ignore
public class StartExamTest
{
  private static final String USERNAME = "admin";
  private static final String EXAM_RUN_TIME_ID = "runTimeId";
  private static final String IMAGE_NAME = "image";

  private LmsRepositoryRegistry lmsRepositoryRegistry;
  private LmsServiceRegistry lmsServiceRegistry;
  private ExamRepository examRepository;
  private AccessIdentityManagement accessIdentityManagement;
  private ExamRuntimeDataRepository examRuntimeDataRepository;
  private ExamInteractionService examInteractionService;
  private QuestionRepository questionRepository;
  private StartExam startExam;
  private Exam exam;
  private ExamId examId;
  private ExamRuntimeData examRuntimeData;
  private ExamInteractionDto interactionDto;
  private List<ExamInteractionDto> interactionDtos = new ArrayList<>();
  private RandomQuestionConfig randomQuestionConfig;
  private Set<RandomQuestionConfig> randomQuestionConfigs = new HashSet<>();
  private List<Question> questions = new ArrayList<>();
  private Set<String> questionIds = new HashSet<>();

  @Before
  public void setUp() throws LmsRepositoryException
  {
    lmsRepositoryRegistry = mock(LmsRepositoryRegistry.class);
    lmsServiceRegistry = mock(LmsServiceRegistry.class);
    examRepository = mock(ExamRepository.class);
    accessIdentityManagement = mock(AccessIdentityManagement.class);
    examRuntimeDataRepository = mock(ExamRuntimeDataRepository.class);
    examInteractionService = mock(ExamInteractionService.class);
    questionRepository = mock(QuestionRepository.class);

    when(lmsRepositoryRegistry.getExamRepository()).thenReturn(examRepository);
    when(lmsServiceRegistry.getAccessIdentityManagement()).thenReturn(accessIdentityManagement);
    when(lmsRepositoryRegistry.getExamRuntimeDataRepository()).thenReturn(examRuntimeDataRepository);
    when(lmsServiceRegistry.getExamInteractionService()).thenReturn(examInteractionService);
    when(lmsRepositoryRegistry.getQuestionRepository()).thenReturn(questionRepository);

    examId = ExamId.valueOf(EXAM_ID);
    randomQuestionConfig = new RandomQuestionConfig("group", "category", 0, 0);
    randomQuestionConfigs.add(randomQuestionConfig);
    questions = generateQuestions(5);
    questionIds = questions.stream().map(Question::getId).map(EntityId::getId).collect(Collectors.toSet());
    ExamConfig examConfig = new ExamConfig(randomQuestionConfigs, 5, ShowAnswerResult.SHOW_NOTHING,
        false, false, false, true, 5);
    examConfig.setMaxScore(10);
    examConfig.setDuration(60);
    examConfig.setAttempt(2);
    examConfig.setStartDate(CURRENT_DATE);
    examConfig.setStartTime("17:30");
    examConfig.setQuestionIds(questionIds);

    exam = new Exam.Builder(examId, AUTHOR, CURRENT_DATE, NAME)
        .withExamConfig(examConfig)
        .build();

    examRuntimeData = new ExamRuntimeData(EXAM_RUN_TIME_ID, EXAM_ID, USERNAME, examConfig.getMaxScore(),
        examConfig.getDuration(), examConfig.getAttempt(), examConfig.getThresholdScore());

    interactionDto = ExamInteractionDto.newOngoingInteraction(CURRENT_DATE);
    interactionDto.setOngoing(false);
    interactionDto.setGivenQuestions(null);
    interactionDtos.add(interactionDto);
    examRuntimeData.setInteractions(interactionDtos);

    when(examRepository.findById(any())).thenReturn(exam);
    when(accessIdentityManagement.getCurrentUsername()).thenReturn(USERNAME);
    when(examRuntimeDataRepository.checkIfExists(any(), any())).thenReturn(true);
    when(examRuntimeDataRepository.create(USERNAME, EXAM_ID, 10, 2, 60, 5)).thenReturn(examRuntimeData);
    when(examRuntimeDataRepository.getRuntimeData(any(), any())).thenReturn(examRuntimeData);

    startExam = new StartExam(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Test(expected = UseCaseException.class)
  public void whenId_isBlank() throws UseCaseException
  {
    startExam.executeImpl("");
  }

  @Test(expected = UseCaseException.class)
  public void whenId_isNull() throws UseCaseException
  {
    startExam.executeImpl(null);
  }

  @Test(expected = UseCaseException.class)
  public void whenUser_hasOngoingExam() throws UseCaseException
  {
    when(examInteractionService.getCurrentUserOngoingExam()).thenReturn(Optional.of(exam));
    startExam.executeImpl("test");
  }

  @Test
  public void whenLearnerAlready_hasRuntimeData() throws UseCaseException, LmsRepositoryException
  {
    startExam.executeImpl(EXAM_ID);
    verify(examRuntimeDataRepository, times(1)).getRuntimeData(any(), any());
  }

  @Test
  public void whenLearnerHas_noRuntimeData() throws LmsRepositoryException, UseCaseException
  {
    when(examRuntimeDataRepository.checkIfExists(any(), any())).thenReturn(false);
    startExam.executeImpl(EXAM_ID);
    verify(examRuntimeDataRepository, times(1)).create(USERNAME, EXAM_ID, 10, 2, 60, 5);
  }

  @Test
  public void whenSuccess_withOngoingInteraction() throws UseCaseException
  {
    interactionDto.setOngoing(true);
    Assert.assertEquals(NAME, startExam.executeImpl(EXAM_ID).getTitle());
  }

  @Test
  public void whenSuccess_withNoOngoingInteraction_shouldCreateInteraction() throws LmsRepositoryException, UseCaseException
  {
    Assert.assertEquals(NAME, startExam.executeImpl(EXAM_ID).getTitle());
    verify(examRuntimeDataRepository, times(1)).update(EXAM_ID, USERNAME, interactionDtos);
  }

  @Test
  public void whenExam_hasNewQuestion_updatesTheQuestionStatus() throws UseCaseException
  {
    Question question = questions.get(0);
    question.setStatus(QuestionStatus.NEW);
    when(questionRepository.getByIds(any())).thenReturn(questions);

    startExam.executeImpl(EXAM_ID);
    verify(questionRepository, times(1)).updateStatus(any(), any());
  }

  @Test
  public void whenShuffleQuestion_isTrue() throws UseCaseException
  {
    List<String> questionIds = questions.stream().map(Question::getId).map(EntityId::getId).collect(Collectors.toList());
    setExamConfig();

    List<String> questionDtoIds = startExam.executeImpl(EXAM_ID).getLearnerQuestion().stream()
        .map(LearnerQuestionDto::getId).collect(Collectors.toList());

    Assert.assertNotEquals(questionIds, questionDtoIds);
  }

  @Test
  public void whenShuffleAnswer_isTrue() throws UseCaseException
  {
    setExamConfig();
    when(questionRepository.getByIds(any())).thenReturn(questions);
    Question question = questions.get(0);
    List<String> answerValues = question.getAnswers().stream().map(Answer::getValue).collect(Collectors.toList());
    LearnerQuestionDto learnerQuestionDto = startExam.executeImpl(EXAM_ID).getLearnerQuestion().stream()
        .filter(dtoQuestion -> question.getId().getId().equals(dtoQuestion.getId())).findFirst().orElse(null);

    assert learnerQuestionDto != null;
    List<String> dtoAnswerValues = learnerQuestionDto.getAnswers().stream().map(LearnerAnswerEntity::getValue).collect(Collectors.toList());
    Assert.assertNotEquals(answerValues, dtoAnswerValues);
  }

  @Test
  public void whenQuestionHasImage_getImagePath_returnsExpectedPath() throws UseCaseException
  {
    Question question = questions.get(0);
    question.getDetail().setHasImage(true);
    question.getDetail().setImageName(IMAGE_NAME);
    when(questionRepository.getByIds(any())).thenReturn(questions);

    String imagePath = "/alfresco/Exams/" + question.getId().getId() + "/" + IMAGE_NAME;
    String expectedImagePath = startExam.executeImpl(EXAM_ID).getLearnerQuestion().get(0).getImagePath();
    Assert.assertEquals(imagePath, expectedImagePath);
  }

  @Test(expected = UseCaseException.class)
  public void whenRandomQuestionAmount_isGreaterThan_existingQuestions() throws UseCaseException
  {
    Set<RandomQuestionConfig> randomQuestionConfigs = new HashSet<>();
    RandomQuestionConfig randomQuestionConfig = new RandomQuestionConfig("group", "category", 1, 10);
    randomQuestionConfigs.add(randomQuestionConfig);
    ExamConfig examConfig = new ExamConfig(randomQuestionConfigs, 10, ShowAnswerResult.SHOW_NOTHING,
        false, false, false, true, 5);
    examConfig.setMaxScore(10);
    examConfig.setDuration(60);
    examConfig.setAttempt(2);
    examConfig.setStartDate(CURRENT_DATE);
    examConfig.setStartTime("17:30");
    exam.setExamConfig(examConfig);

    when(questionRepository.findByGroupIdAndCategoryAndScore(randomQuestionConfig.getGroupId(),
        randomQuestionConfig.getCategoryId(), randomQuestionConfig.getAmount())).thenReturn(questions);

    startExam.executeImpl(EXAM_ID);
  }

  @Test
  public void whenSuccessfully_getQuestions() throws UseCaseException
  {
    List<Question> randomQuestions = generateQuestions(10);

    RandomQuestionConfig randomQuestionConfig = new RandomQuestionConfig("group", "category", 0, 5);
    Set<RandomQuestionConfig> randomQuestionConfigs = new HashSet<>();
    randomQuestionConfigs.add(randomQuestionConfig);

    ExamConfig examConfig = new ExamConfig(randomQuestionConfigs, 10, ShowAnswerResult.SHOW_NOTHING,
        false, false, false, true, 5);
    examConfig.setStartDate(CURRENT_DATE);
    examConfig.setStartTime("17:30");
    examConfig.setQuestionIds(questionIds);

    exam.setExamConfig(examConfig);

    when(questionRepository.findByGroupIdAndCategoryAndScore(anyString(),
        anyString(), anyInt())).thenReturn(randomQuestions);
    for (String id : questionIds)
    {
      randomQuestions.removeIf(randomQuestion -> randomQuestion.getId().getId().equals(id));
    }
    questions.addAll(randomQuestions);
    when(questionRepository.getByIds(any())).thenReturn(questions);

    int expected = exam.getExamConfig().getQuestionIds().size() + exam.getExamConfig().getRandomQuestionConfigs()
        .stream().map(RandomQuestionConfig::getAmount).reduce(Integer::sum).orElse(0);

    Assert.assertEquals(expected, startExam.executeImpl(EXAM_ID).getLearnerQuestion().size());
  }

  @Test(expected = UseCaseException.class)
  public void whenExamConfigHas_noQuestion() throws UseCaseException
  {
    when(questionRepository.getByIds(any())).thenReturn(Collections.emptyList());
    Set<RandomQuestionConfig> randomQuestionConfigs = new HashSet<>();
    RandomQuestionConfig randomQuestionConfig = new RandomQuestionConfig("group", "category", 1, 0);
    randomQuestionConfigs.add(randomQuestionConfig);
    ExamConfig examConfig = new ExamConfig(randomQuestionConfigs, 0, ShowAnswerResult.SHOW_NOTHING,
        false, false, false, true, 5);
    examConfig.setMaxScore(1);
    examConfig.setDuration(20);
    examConfig.setAttempt(2);
    examConfig.setStartDate(CURRENT_DATE);
    examConfig.setStartTime("17:30");
    examConfig.setQuestionIds(new HashSet<>());
    exam.setExamConfig(examConfig);

    startExam.executeImpl(EXAM_ID);
  }

  private void setExamConfig()
  {
    ExamConfig examConfig = new ExamConfig(randomQuestionConfigs, 5, ShowAnswerResult.SHOW_NOTHING,
        true, true, false, true, 5);
    examConfig.setStartDate(CURRENT_DATE);
    examConfig.setStartTime("17:30");
    examConfig.setQuestionIds(new HashSet<>(questionIds));
    exam.setExamConfig(examConfig);
  }
}