package mn.erin.lms.base.domain.usecase.exam;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.certificate.CertificateId;
import mn.erin.lms.base.domain.model.exam.Exam;
import mn.erin.lms.base.domain.model.exam.ExamConfig;
import mn.erin.lms.base.domain.model.exam.ExamId;
import mn.erin.lms.base.domain.model.exam.ExamPublishConfig;
import mn.erin.lms.base.domain.model.exam.RandomQuestionConfig;
import mn.erin.lms.base.domain.model.exam.ShowAnswerResult;
import mn.erin.lms.base.domain.repository.ExamCategoryRepository;
import mn.erin.lms.base.domain.repository.ExamEnrollmentRepository;
import mn.erin.lms.base.domain.repository.ExamRepository;
import mn.erin.lms.base.domain.repository.ExamRuntimeDataRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.repository.QuestionRepository;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.LmsUseCase;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamDto;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamInput;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamLearnerDto;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamRuntimeData;
import mn.erin.lms.base.domain.usecase.exam.dto.LeanerExamListDto;
import mn.erin.lms.base.domain.usecase.exam.dto.LearnerExamLaunchDto;

/**
 * @author Temuulen Naranbold
 */
public abstract class ExamUseCase<I, O> extends LmsUseCase<I, O>
{
  protected static final Date CURRENT_DATE = Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Ulaanbaatar")).toInstant());
  private static final long SECONDS_IN_A_WEEK = 604800;

  protected static final Logger LOGGER = LoggerFactory.getLogger(ExamUseCase.class);

  protected ExamRepository examRepository;
  protected ExamEnrollmentRepository examEnrollmentRepository;
  protected ExamCategoryRepository examCategoryRepository;
  protected ExamRuntimeDataRepository examRuntimeDataRepository;
  protected QuestionRepository questionRepository;

  protected ExamUseCase(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);

    this.examRepository = lmsRepositoryRegistry.getExamRepository();
    this.examEnrollmentRepository = lmsRepositoryRegistry.getExamEnrollmentRepository();
    this.examCategoryRepository = lmsRepositoryRegistry.getExamCategoryRepository();
    this.examRuntimeDataRepository = lmsRepositoryRegistry.getExamRuntimeDataRepository();
    this.questionRepository = lmsRepositoryRegistry.getQuestionRepository();
  }

  protected boolean exists(String examId)
  {
    return examRepository.exists(ExamId.valueOf(examId));
  }

  protected Exam getExam(String examId) throws UseCaseException
  {
    try
    {
      return examRepository.findById(ExamId.valueOf(examId));
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException("The exam with the ID: [" + examId + "] does not exist!");
    }
  }

  protected ExamConfig mapToExamConfig(ExamInput input)
  {
    int totalQuestions =
        input.getQuestionIds().size() + input.getRandomQuestionConfigs().stream().map(RandomQuestionConfig::getAmount).reduce(Integer::sum).orElse(0);

    ExamConfig examConfig = new ExamConfig(
        input.getRandomQuestionConfigs(),
        totalQuestions,
        ShowAnswerResult.valueOf(input.getAnswerResult()),
        input.isShuffleQuestion(),
        input.isShuffleAnswer(),
        input.getQuestionsPerPage(),
        input.isAutoStart(),
        input.getThresholdScore());

    examConfig.setAttempt(input.getAttempt());
    examConfig.setQuestionIds(input.getQuestionIds());
    examConfig.setMaxScore(input.getMaxScore());
    if (input.getCertificateId() != null)
    {
      examConfig.setCertificateId(CertificateId.valueOf(input.getCertificateId()));
    }
    examConfig.setStartDate(input.getStartDate());
    examConfig.setEndDate(input.getEndDate());
    examConfig.setStartTime(input.getStartTime());
    examConfig.setEndTime(input.getEndTime());
    examConfig.setDuration(input.getDuration());

    return examConfig;
  }

  protected ExamPublishConfig mapToExamPublishConfig(ExamInput input)
  {
    ExamPublishConfig publishConfigure = new ExamPublishConfig(
        input.getPublishDate(),
        input.getPublishTime(),
        input.isSendEmail(),
        input.isSendSMS()
    );
    if (input.isSendEmail())
    {
      publishConfigure.setMailText(input.getMailText());
    }
    if (input.isSendSMS())
    {
      publishConfigure.setSmsText(input.getSmsText());
    }
    return publishConfigure;
  }

  protected ExamLearnerDto toExamLearnerDto(Exam exam)
  {
    ExamLearnerDto examLearnerDto = new ExamLearnerDto(
        exam.getId().getId(),
        exam.getExamConfig().getThresholdScore(),
        exam.getExamStatus().name());
    examLearnerDto.setAuthor(exam.getAuthor());
    examLearnerDto.setExamPublishStatus(exam.getExamPublishStatus().name());
    //TODO: assign if user has gotten certificate
    examLearnerDto.setStartDate(exam.getExamConfig().getStartDate());
    examLearnerDto.setEndDate(exam.getExamConfig().getEndDate());
    examLearnerDto.setDuration(exam.getExamConfig().getDuration());
    examLearnerDto.setStartTime(exam.getExamConfig().getStartTime());
    //TODO: assign actual user score
    return examLearnerDto;
  }

  protected ExamDto toDetailedExamDto(Exam exam)
  {
    ExamDto examDto = new ExamDto(
        exam.getId().getId(),
        exam.getName()
    );
    examDto.setExamConfigure(exam.getExamConfig());
    examDto.setPublishConfig(exam.getExamPublishConfig());
    examDto.setDescription(exam.getDescription());
    examDto.setCategoryId(exam.getExamCategoryId().getId());
    examDto.setExamType(exam.getExamType().name());
    examDto.setExamStatus(exam.getExamStatus().name());
    examDto.setPublishState(exam.getExamPublishStatus().name());
    examDto.setEnrolledLearners(exam.getEnrolledLearners());
    examDto.setEnrolledGroups(exam.getEnrolledGroups());
    examDto.setGroupId(exam.getExamGroupId() != null ? exam.getExamGroupId().getId() : null);
    examDto.setCreatedDate(exam.getCreatedDate());
    examDto.setModifiedDate(exam.getModifiedDate());
    examDto.setModifiedUser(exam.getModifiedUser());

    return examDto;
  }

  protected LeanerExamListDto toLeanerExamListDto(Exam exam)
  {
    LeanerExamListDto leanerExamListDto = new LeanerExamListDto(
        exam.getId().getId(),
        exam.getName(),
        exam.getExamConfig().getThresholdScore(),
        exam.getAuthor(),
        exam.getExamStatus().name());
    leanerExamListDto.setCategoryId(exam.getExamCategoryId().getId());
    leanerExamListDto.setThresholdScore(exam.getExamConfig().getThresholdScore());
    leanerExamListDto.setHasCertificate(exam.getExamConfig().getCertificateId() != null);
    leanerExamListDto.setStartDate(exam.getExamConfig().getStartDate());
    leanerExamListDto.setUpcoming(checkUpcoming(exam.getExamConfig().getStartDate()));
    leanerExamListDto.setStartTime(exam.getExamConfig().getStartTime());
    leanerExamListDto.setEndDate(exam.getExamConfig().getEndDate());
    leanerExamListDto.setEndTime(exam.getExamConfig().getEndTime());
    return leanerExamListDto;
  }

  protected LearnerExamLaunchDto toLearnerExamLaunchDto(ExamRuntimeData examRuntimeData, Exam exam)
  {
    LearnerExamLaunchDto learnerExamLaunch = new LearnerExamLaunchDto(
        exam.getAuthor(),
        examRuntimeData.getDuration() + " мин",
        examRuntimeData.getThresholdScore(),
        exam.getName(),
        exam.getDescription(),
        exam.getExamStatus());
    learnerExamLaunch.setMaxAttempt(examRuntimeData.getMaxAttempt());
    learnerExamLaunch.setMaxScore(examRuntimeData.getMaxScore());
    learnerExamLaunch.setRemainingAttempt(examRuntimeData.getMaxAttempt());
    return learnerExamLaunch;
  }

  protected LearnerExamLaunchDto toLearnerExamLaunchDto(ExamRuntimeData examRuntimeData, Exam exam, double score,
      String percentage, int remainingAttempt, String spentTime, boolean ongoing)
  {
    LearnerExamLaunchDto learnerExamLaunch = new LearnerExamLaunchDto(
        exam.getAuthor(),
        examRuntimeData.getDuration() + " мин",
        examRuntimeData.getThresholdScore(),
        exam.getName(),
        exam.getDescription(),
        exam.getExamStatus());
    learnerExamLaunch.setMaxAttempt(examRuntimeData.getMaxAttempt());
    learnerExamLaunch.setMaxScore(examRuntimeData.getMaxScore());
    learnerExamLaunch.setScore(score);
    learnerExamLaunch.setScorePercentage(percentage);
    learnerExamLaunch.setRemainingAttempt(remainingAttempt);
    learnerExamLaunch.setSpentTime(spentTime);
    learnerExamLaunch.setOngoing(ongoing);
    return learnerExamLaunch;
  }

  private boolean checkUpcoming(Date startDate)
  {
    long result = (startDate.getTime() - CURRENT_DATE.getTime()) / 1000;
    return SECONDS_IN_A_WEEK >= result;
  }
}
