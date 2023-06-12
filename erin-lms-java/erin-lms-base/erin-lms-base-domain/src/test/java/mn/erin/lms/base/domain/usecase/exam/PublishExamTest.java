package mn.erin.lms.base.domain.usecase.exam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.model.person.PersonId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.LmsUserService;
import mn.erin.lms.base.aim.user.LmsAdmin;
import mn.erin.lms.base.domain.model.category.ExamCategoryId;
import mn.erin.lms.base.domain.model.certificate.CertificateId;
import mn.erin.lms.base.domain.model.exam.Exam;
import mn.erin.lms.base.domain.model.exam.ExamConfig;
import mn.erin.lms.base.domain.model.exam.ExamId;
import mn.erin.lms.base.domain.model.exam.ExamPublishConfig;
import mn.erin.lms.base.domain.model.exam.ExamStatus;
import mn.erin.lms.base.domain.model.exam.ShowAnswerResult;
import mn.erin.lms.base.domain.repository.ExamEnrollmentRepository;
import mn.erin.lms.base.domain.repository.ExamRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.repository.task.ScheduledTaskRepository;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.LmsTaskScheduler;
import mn.erin.lms.base.domain.service.exam.ExamExpirationService;
import mn.erin.lms.base.domain.service.exam.ExamPublicationServiceImpl;
import mn.erin.lms.base.domain.service.exam.ExamStartService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author EBazarragchaa
 */
public class PublishExamTest
{
  private LmsRepositoryRegistry lmsRepositoryRegistry;
  private LmsServiceRegistry lmsServiceRegistry;
  private LmsUserService lmsUserService;
  private ExamStartService examStartService;
  private ExamExpirationService examExpirationService;
  private ScheduledTaskRepository scheduledTaskRepository;
  private ExamEnrollmentRepository examEnrollmentRepository;
  private ExamRepository examRepository;
  private PublishExam publishExam;

  private final String examId = "examId1";

  @Before
  public void setUp()
  {
    lmsRepositoryRegistry = mock(LmsRepositoryRegistry.class);
    lmsServiceRegistry = mock(LmsServiceRegistry.class);
    lmsUserService = mock(LmsUserService.class);
    scheduledTaskRepository = mock(ScheduledTaskRepository.class);
    examRepository = mock(ExamRepository.class);
    examEnrollmentRepository = mock(ExamEnrollmentRepository.class);

    scheduledTaskRepository = mock(ScheduledTaskRepository.class);
    examStartService = mock(ExamStartService.class);
    examExpirationService = mock(ExamExpirationService.class);

    when(lmsServiceRegistry.getLmsUserService()).thenReturn(lmsUserService);
    when(lmsServiceRegistry.getExamPublicationService()).thenReturn(new ExamPublicationServiceImpl(lmsRepositoryRegistry, mock(LmsTaskScheduler.class)));

    when(lmsUserService.getCurrentUser()).thenReturn(new LmsAdmin(PersonId.valueOf("admin")));
    when(lmsRepositoryRegistry.getExamRepository()).thenReturn(examRepository);
    when(lmsRepositoryRegistry.getExamEnrollmentRepository()).thenReturn(examEnrollmentRepository);
    when(lmsRepositoryRegistry.getScheduledTaskRepository()).thenReturn(scheduledTaskRepository);
    when(scheduledTaskRepository.exists(Mockito.anyString())).thenReturn(true);

    when(lmsServiceRegistry.getExamStartService()).thenReturn(examStartService);
    when(lmsServiceRegistry.getExamExpirationService()).thenReturn(examExpirationService);

    publishExam = new PublishExam(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Test(expected = UseCaseException.class)
  public void exam_not_found_exception() throws UseCaseException, LmsRepositoryException
  {
    when(examRepository.findById(ExamId.valueOf(examId))).thenThrow(LmsRepositoryException.class);
    publishExam.executeImpl(examId);
  }

  @Test()
  public void should_create_start_end_schedulers() throws UseCaseException, LmsRepositoryException, ParseException
  {
    Date publishDate = new Date();
    Exam exam = createExam();
    exam.setExamPublishConfig(new ExamPublishConfig(publishDate, "00:00", false, false));

    Set<String> learners = new HashSet<>();
    learners.add("dorj");
    learners.add("dulam");

    exam.setEnrolledLearners(learners);
    exam.setExamPublishConfig(new ExamPublishConfig(publishDate, "00:00", false, false));

    when(examRepository.findById(ExamId.valueOf(examId))).thenReturn(exam);
    when(scheduledTaskRepository.exists(Mockito.anyString())).thenReturn(false);

    publishExam.executeImpl(examId);

    verify(examStartService).startExamOn(examId, exam.getActualStartDate());
    verify(examExpirationService).expireExamOn(examId, exam.getActualEndDate());
  }

  @Test(expected = UseCaseException.class)
  public void should_not_schedule_without_start_date() throws UseCaseException, LmsRepositoryException, ParseException
  {
    Exam exam = createExam();
    ExamConfig examConfig = exam.getExamConfig();
    examConfig.setStartDate(null);
    exam.setExamConfig(examConfig);
    when(examRepository.findById(ExamId.valueOf(examId))).thenReturn(exam);
    when(scheduledTaskRepository.exists(Mockito.anyString())).thenReturn(false);
    publishExam.executeImpl(examId);
  }

  @Test(expected = UseCaseException.class)
  public void should_not_schedule_without_end_date() throws UseCaseException, LmsRepositoryException, ParseException
  {
    Exam exam = createExam();
    ExamConfig examConfig = exam.getExamConfig();
    examConfig.setEndDate(null);
    exam.setExamConfig(examConfig);
    when(examRepository.findById(ExamId.valueOf(examId))).thenReturn(exam);
    when(scheduledTaskRepository.exists(Mockito.anyString())).thenReturn(false);
    publishExam.executeImpl(examId);
  }

  @Test
  public void publishExam_enrolls_learners() throws UseCaseException, LmsRepositoryException, ParseException
  {
    Set<String> learners = new HashSet<>();
    learners.add("dorj");
    learners.add("dulam");

    Date publishDate = new Date();

    Exam exam = createExam();
    exam.setEnrolledLearners(learners);
    exam.setExamPublishConfig(new ExamPublishConfig(publishDate, "00:00", false, false));

    when(examRepository.findById(ExamId.valueOf(examId))).thenReturn(exam);
    when(scheduledTaskRepository.exists(Mockito.anyString())).thenReturn(false);

    publishExam.executeImpl(examId);

    verify(examEnrollmentRepository).createEnrollment(examId, "dorj", "r");
    verify(examEnrollmentRepository).createEnrollment(examId, "dulam", "r");
  }

  private Exam createExam() throws ParseException
  {
    ExamConfig examConfig = new ExamConfig(Collections.emptySet(), 0, ShowAnswerResult.AFTER_EXAM, false, false, false, true, 1);
    examConfig.setCertificateId(CertificateId.valueOf("certificateId"));
    examConfig.setStartDate(new SimpleDateFormat("dd/MM/yyyy").parse("19/12/1996"));
    examConfig.setStartTime("20:00");
    examConfig.setEndDate(new SimpleDateFormat("dd/MM/yyyy").parse("31/12/1996"));
    examConfig.setEndTime("20:01");
    return new Exam.Builder(ExamId.valueOf("examId1"), "author", new Date(), "exam1")
        .withExamStatus(ExamStatus.NEW)
        .withExamCategoryId(ExamCategoryId.valueOf("category"))
        .withExamConfig(examConfig)
        .build();
  }
}
