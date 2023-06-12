package mn.erin.lms.base.domain.usecase.exam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mn.erin.domain.base.model.person.PersonId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.LmsUserService;
import mn.erin.lms.base.aim.user.LmsAdmin;
import mn.erin.lms.base.domain.model.exam.Exam;
import mn.erin.lms.base.domain.model.exam.ExamConfig;
import mn.erin.lms.base.domain.model.exam.ExamId;
import mn.erin.lms.base.domain.model.exam.ShowAnswerResult;
import mn.erin.lms.base.domain.repository.ExamEnrollmentRepository;
import mn.erin.lms.base.domain.repository.ExamRepository;
import mn.erin.lms.base.domain.repository.ExamRuntimeDataRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamInteractionDto;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamRuntimeData;
import mn.erin.lms.base.domain.usecase.exam.dto.LearnerScoreForExam;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * TODO: add javadoc
 *
 * @author EBazarragchaa
 */
public class GetLearnerScoresForExamTest
{

  private LmsRepositoryRegistry lmsRepositoryRegistry;
  private LmsServiceRegistry lmsServiceRegistry;
  private ExamRepository examRepository;
  private ExamEnrollmentRepository examEnrollmentRepository;
  private ExamRuntimeDataRepository examRuntimeDataRepository;
  private GetLearnerScoresForExam getLearnerScoresForExam;
  private LmsUserService lmsUserService;

  private final String examId = "MyExam";

  @Before
  public void setUp()
  {
    lmsRepositoryRegistry = mock(LmsRepositoryRegistry.class);
    lmsServiceRegistry = mock(LmsServiceRegistry.class);
    lmsUserService = mock(LmsUserService.class);

    examRuntimeDataRepository = mock(ExamRuntimeDataRepository.class);
    examRepository = mock(ExamRepository.class);
    examEnrollmentRepository = mock(ExamEnrollmentRepository.class);

    when(lmsServiceRegistry.getLmsUserService()).thenReturn(lmsUserService);
    when(lmsUserService.getCurrentUser()).thenReturn(new LmsAdmin(PersonId.valueOf("admin")));
    when(lmsRepositoryRegistry.getExamRuntimeDataRepository()).thenReturn(examRuntimeDataRepository);
    when(lmsRepositoryRegistry.getExamRepository()).thenReturn(examRepository);
    when(lmsRepositoryRegistry.getExamEnrollmentRepository()).thenReturn(examEnrollmentRepository);

    getLearnerScoresForExam = new GetLearnerScoresForExam(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Test(expected = UseCaseException.class)
  public void when_examId_is_null() throws UseCaseException
  {
    getLearnerScoresForExam.execute(null);
  }

  @Test
  public void noExamRuntime_returns_empty_scores() throws LmsRepositoryException, UseCaseException
  {
    when(examRuntimeDataRepository.listExamRuntimeData(examId)).thenReturn(new ArrayList<>());

    Assert.assertTrue(getLearnerScoresForExam.execute(examId).isEmpty());
  }

  @Test
  public void runtime_but_noInteraction_returns_zero_finalScore() throws LmsRepositoryException, UseCaseException
  {
    ExamRuntimeData examRuntimeData = createExamRuntimeData(examId, "learner1");

    ExamConfig config = new ExamConfig(Collections.emptySet(), 3, ShowAnswerResult.AFTER_EXAM, false, false, false, true, 60);
    config.setMaxScore(100);
    Exam exam = new Exam.Builder(ExamId.valueOf("test_exam"), "admin", new Date(), "TEST EXAM")
        .withExamConfig(config).build();
    when(examRepository.findById(ExamId.valueOf(examId))).thenReturn(exam);
    when(examEnrollmentRepository.getAllReadLearnerByExamId(examId)).thenReturn(Collections.singleton("learner1"));
    when(examRuntimeDataRepository.listExamRuntimeData(examId)).thenReturn(Collections.singletonList(examRuntimeData));

    List<LearnerScoreForExam> examScores = getLearnerScoresForExam.execute(examId);

    Assert.assertEquals(1, examScores.size());

    LearnerScoreForExam examScore = examScores.get(0);

    Assert.assertEquals("learner1", examScore.getLearnerName());
    Assert.assertEquals(100.0d, examScore.getExamMaxScore(), 0.0d);
    Assert.assertEquals(60.0d, examScore.getExamThresholdScore(), 0.0d);
    Assert.assertEquals(-1.0d, examScore.getLearnerFinalScore(), 0.0d);
  }

  @Test
  public void runtime_with_oneInteraction_returns_finalScore() throws LmsRepositoryException, UseCaseException
  {
    ExamRuntimeData examRuntimeData = createExamRuntimeData(examId, "learner1");
    ExamInteractionDto examInteraction = createExamInteraction(new Date(), 75.0d);
    examRuntimeData.setInteractions(Collections.singletonList(examInteraction));

    ExamConfig config = new ExamConfig(Collections.emptySet(), 3, ShowAnswerResult.AFTER_EXAM, false, false, false, true, 60);
    config.setMaxScore(100);
    Exam exam = new Exam.Builder(ExamId.valueOf("test_exam"), "admin", new Date(), "TEST EXAM")
        .withExamConfig(config).build();
    when(examRepository.findById(ExamId.valueOf(examId))).thenReturn(exam);
    when(examEnrollmentRepository.getAllReadLearnerByExamId(examId)).thenReturn(Collections.singleton("learner1"));
    when(examRuntimeDataRepository.listExamRuntimeData(examId)).thenReturn(Collections.singletonList(examRuntimeData));

    List<LearnerScoreForExam> examScores = getLearnerScoresForExam.execute(examId);

    Assert.assertEquals(1, examScores.size());

    LearnerScoreForExam examScore = examScores.get(0);

    Assert.assertEquals("learner1", examScore.getLearnerName());
    Assert.assertEquals(75.0d, examScore.getLearnerFinalScore(), 0.0d);
  }

  @Test
  public void runtime_with_moreInteraction_returns_max_finalScore() throws LmsRepositoryException, UseCaseException
  {
    ExamRuntimeData examRuntimeData = createExamRuntimeData(examId, "learner1");
    List<ExamInteractionDto> learner1Interactions = new ArrayList<>();
    learner1Interactions.add(createExamInteraction(new Date(), 99.0d));
    learner1Interactions.add(createExamInteraction(new Date(), 75.0d));
    learner1Interactions.add(createExamInteraction(new Date(), 65.0d));


    ExamConfig config = new ExamConfig(Collections.emptySet(), 3, ShowAnswerResult.AFTER_EXAM, false, false, false, true, 60);
    config.setMaxScore(100);
    Exam exam = new Exam.Builder(ExamId.valueOf("test_exam"), "admin", new Date(), "TEST EXAM")
        .withExamConfig(config).build();
    when(examRepository.findById(ExamId.valueOf(examId))).thenReturn(exam);
    when(examEnrollmentRepository.getAllReadLearnerByExamId(examId)).thenReturn(Collections.singleton("learner1"));
    examRuntimeData.setInteractions(learner1Interactions);

    when(examRuntimeDataRepository.listExamRuntimeData(examId)).thenReturn(Collections.singletonList(examRuntimeData));

    List<LearnerScoreForExam> examScores = getLearnerScoresForExam.execute(examId);

    Assert.assertEquals(1, examScores.size());

    LearnerScoreForExam examScore = examScores.get(0);

    Assert.assertEquals("learner1", examScore.getLearnerName());
    Assert.assertEquals(99.0d, examScore.getLearnerFinalScore(), 0.0d);
  }

  @Test
  public void moreLearner_with_moreInteraction_returns_max_finalScore() throws LmsRepositoryException, UseCaseException
  {
    List<ExamRuntimeData> learnerRuntimeData = new ArrayList<>();
    ExamRuntimeData learner1RuntimeData = createExamRuntimeData(examId, "learner1");

    List<ExamInteractionDto> learner1Interactions = new ArrayList<>();
    learner1Interactions.add(createExamInteraction(new Date(), 99.0d));
    learner1Interactions.add(createExamInteraction(new Date(), 75.0d));
    learner1Interactions.add(createExamInteraction(new Date(), 65.0d));

    ExamRuntimeData learner2RuntimeData = createExamRuntimeData(examId, "learner2");
    List<ExamInteractionDto> learner2Interactions = new ArrayList<>();
    learner2Interactions.add(createExamInteraction(new Date(), 85.0d));
    learner2Interactions.add(createExamInteraction(new Date(), 70.0d));

    learner1RuntimeData.setInteractions(learner1Interactions);
    learner2RuntimeData.setInteractions(learner2Interactions);

    learnerRuntimeData.add(learner1RuntimeData);
    learnerRuntimeData.add(learner2RuntimeData);

    ExamConfig config = new ExamConfig(Collections.emptySet(), 3, ShowAnswerResult.AFTER_EXAM, false, false, false,  true,60);
    config.setMaxScore(100);
    Exam exam = new Exam.Builder(ExamId.valueOf("test_exam"), "admin", new Date(), "TEST EXAM")
        .withExamConfig(config).build();
    when(examRepository.findById(ExamId.valueOf(examId))).thenReturn(exam);
    LinkedHashSet<String> userSet = new LinkedHashSet<>();
    userSet.add("learner1");
    userSet.add("learner2");
    when(examEnrollmentRepository.getAllReadLearnerByExamId(examId)).thenReturn(userSet);
    when(examRuntimeDataRepository.listExamRuntimeData(examId)).thenReturn(learnerRuntimeData);

    List<LearnerScoreForExam> examScores = getLearnerScoresForExam.execute(examId);

    Assert.assertEquals(2, examScores.size());

    LearnerScoreForExam learner1Score = examScores.get(0);

    Assert.assertEquals("learner1", learner1Score.getLearnerName());
    Assert.assertEquals(99.0d, learner1Score.getLearnerFinalScore(), 0.0d);

    LearnerScoreForExam learner2Score = examScores.get(1);

    Assert.assertEquals("learner2", learner2Score.getLearnerName());
    Assert.assertEquals(85.0d, learner2Score.getLearnerFinalScore(), 0.0d);
  }

  private ExamRuntimeData createExamRuntimeData(String examId, String learnerId)
  {
    return new ExamRuntimeData(UUID.randomUUID().toString(), examId, learnerId, 100.0d, 45, 3, 60.0d);
  }

  private ExamInteractionDto createExamInteraction(Date date, double score)
  {
    ExamInteractionDto examInteraction = new ExamInteractionDto(date);
    examInteraction.setScore(score);
    return examInteraction;
  }
}
