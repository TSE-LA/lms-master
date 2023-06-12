package mn.erin.lms.base.domain.usecase.exam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.domain.model.exam.Exam;
import mn.erin.lms.base.domain.model.exam.ExamConfig;
import mn.erin.lms.base.domain.model.exam.ExamId;
import mn.erin.lms.base.domain.model.exam.ExamPublishConfig;
import mn.erin.lms.base.domain.model.exam.ExamPublishStatus;
import mn.erin.lms.base.domain.model.exam.RandomQuestionConfig;
import mn.erin.lms.base.domain.model.exam.ShowAnswerResult;
import mn.erin.lms.base.domain.repository.ExamRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.exam.ExamExpirationService;
import mn.erin.lms.base.domain.service.exam.ExamPublicationService;
import mn.erin.lms.base.domain.service.exam.ExamPublishTaskInfo;
import mn.erin.lms.base.domain.service.exam.ExamStartService;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamInput;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Temuulen Naranbold
 */
public class UpdateExamTest
{
  private static final String USER_NAME = "user";
  private static final String EXAM_ID = "Id";

  private LmsRepositoryRegistry lmsRepositoryRegistry;
  private LmsServiceRegistry lmsServiceRegistry;
  private ExamRepository examRepository;
  private AccessIdentityManagement accessIdentityManagement;
  private ExamInput input;
  private Exam exam;
  private ExamStartService examStartService;
  private ExamExpirationService examExpirationService;
  private ExamPublicationService examPublicationService;
  private UpdateExam updateExam;

  @Before
  public void setUp() throws UseCaseException, LmsRepositoryException, ParseException
  {
    lmsRepositoryRegistry = mock(LmsRepositoryRegistry.class);
    lmsServiceRegistry = mock(LmsServiceRegistry.class);
    examRepository = mock(ExamRepository.class);
    accessIdentityManagement = mock(AccessIdentityManagement.class);
    examStartService = mock(ExamStartService.class);
    examExpirationService = mock(ExamExpirationService.class);
    examPublicationService = mock(ExamPublicationService.class);

    when(lmsServiceRegistry.getAccessIdentityManagement()).thenReturn(accessIdentityManagement);
    when(lmsRepositoryRegistry.getExamRepository()).thenReturn(examRepository);
    when(accessIdentityManagement.getCurrentUsername()).thenReturn(USER_NAME);
    when(lmsServiceRegistry.getExamStartService()).thenReturn(examStartService);
    when(lmsServiceRegistry.getExamExpirationService()).thenReturn(examExpirationService);
    when(lmsServiceRegistry.getExamPublicationService()).thenReturn(examPublicationService);

    input = ExamTestUtils.createInput();
    input.setId(EXAM_ID);

    exam = createExam();

    when(examRepository.findById(any())).thenReturn(exam);

    updateExam = new UpdateExam(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Test(expected = NullPointerException.class)
  public void when_input_is_null() throws UseCaseException
  {
    updateExam.executeImpl(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void when_id_is_blank() throws UseCaseException
  {
    input.setId("");
    updateExam.executeImpl(input);
  }

  @Test(expected = UseCaseException.class)
  public void whenEndDate_notAfterStartDate() throws ParseException, UseCaseException
  {
    Date startDate = DateUtils.parseDate("2021-11-19", "yyyy-mm-dd");
    Date endDate = DateUtils.parseDate("2021-11-18", "yyyy-mm-dd");
    input.setStartDate(startDate);
    input.setEndDate(endDate);
    updateExam.executeImpl(input);
  }

  @Test(expected = UseCaseException.class)
  public void whenCouldNot_getExam() throws UseCaseException, LmsRepositoryException
  {
    when(examRepository.findById(any())).thenThrow(LmsRepositoryException.class);
    updateExam.executeImpl(input);
  }

  @Test
  public void isExamStartService_called() throws ParseException, UseCaseException, LmsRepositoryException
  {
    exam.getExamConfig().setStartDate(DateUtils.parseDate("2021-12-01", "yyyy-mm-dd"));
    exam.getExamConfig().setStartTime("17:00");
    updateExam.executeImpl(input);
    verify(examStartService, times(1)).startExamOn(any(), any());
  }

  @Test
  public void isExamExpirationService_called() throws ParseException, UseCaseException, LmsRepositoryException
  {
    exam.getExamConfig().setEndDate(DateUtils.parseDate("2021-12-01", "yyyy-mm-dd"));
    exam.getExamConfig().setEndTime("17:00");
    updateExam.executeImpl(input);
    verify(examExpirationService, times(1)).expireExamOn(any(), any());
  }

  @Test
  public void isExamPublicationService_called() throws ParseException, UseCaseException, LmsRepositoryException
  {
    exam.getExamPublishConfig().setPublishDate(DateUtils.parseDate("2021-12-01", "yyyy-mm-dd"));
    exam.getExamPublishConfig().setPublishTime("17:00");
    updateExam.executeImpl(input);
    verify(examPublicationService, times(1)).publishExamOn(any(), any());
  }

  @Test
  public void whenSuccess() throws LmsRepositoryException, UseCaseException
  {
    when(examRepository.update(exam)).thenReturn(EXAM_ID);
    Assert.assertEquals(EXAM_ID, updateExam.executeImpl(input));
  }

  @Test
  public void should_not_schedule_when_unPublished() throws LmsRepositoryException, UseCaseException, ParseException
  {
    Exam exam = createExam();
    exam.setExamPublishStatus(ExamPublishStatus.UNPUBLISHED);
    when(examRepository.update(exam)).thenReturn(EXAM_ID);
    updateExam.executeImpl(input);
    verify(examPublicationService, never()).publishExamOn(new ExamPublishTaskInfo(exam), exam.getActualPublicationDate());
  }

  private Exam createExam() throws ParseException
  {
    Exam exam = new Exam.Builder(ExamId.valueOf(input.getId()), "author", new Date(), input.getName()).build();
    Set<RandomQuestionConfig> randomQuestionConfigs = new HashSet<>();
    randomQuestionConfigs.add(new RandomQuestionConfig("group", "category", 1, 1));
    exam.setExamConfig(new ExamConfig(randomQuestionConfigs, 13, ShowAnswerResult.SHOW_NOTHING, false, false, true, true, input.getThresholdScore()));
    exam.setExamPublishConfig(new ExamPublishConfig(input.getPublishDate(), input.getPublishTime(), false, false));
    exam.getExamConfig().setStartDate(input.getStartDate());
    exam.getExamConfig().setStartTime(input.getStartTime());
    exam.getExamConfig().setEndDate(input.getEndDate());
    exam.getExamConfig().setEndTime(input.getEndTime());

    exam.getExamConfig().setStartDate(new SimpleDateFormat("dd/MM/yyyy").parse("19/12/1996"));
    exam.getExamConfig().setStartTime("20:00");
    exam.getExamConfig().setEndDate(new SimpleDateFormat("dd/MM/yyyy").parse("31/12/1996"));
    exam.getExamConfig().setEndTime("20:01");
    exam.getExamPublishConfig().setPublishDate(new SimpleDateFormat("dd/MM/yyyy").parse("18/12/1996"));
    exam.getExamConfig().setEndTime("20:01");
    exam.setHistoryOfModifications(new ArrayList<>());
    return exam;
  }
}
