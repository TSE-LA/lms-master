package mn.erin.lms.base.domain.usecase.exam;

import java.util.List;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Learner;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.exam.Exam;
import mn.erin.lms.base.domain.model.exam.ExamId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamInteractionDto;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamRuntimeData;
import mn.erin.lms.base.domain.usecase.exam.dto.LearnerExamLaunchDto;

/**
 * @author Byambajav
 */
@Authorized(users = { Author.class, Manager.class, Instructor.class, Supervisor.class, Learner.class })
public class GetExamLaunchData extends ExamUseCase<String, LearnerExamLaunchDto>
{
  public GetExamLaunchData(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  protected LearnerExamLaunchDto executeImpl(String examId) throws UseCaseException, LmsRepositoryException
  {
    try
    {
      Validate.notBlank(examId);
    }
    catch (NullPointerException e)
    {
      throw new UseCaseException("Get exam info: exam id cannot be null");
    }
    try
    {
      String learnerId = lmsServiceRegistry.getAuthenticationService().getCurrentUsername();
      Exam exam = examRepository.findById(ExamId.valueOf(examId));
      ExamRuntimeData examRuntimeData;
      if (examRuntimeDataRepository.checkIfExists(examId, learnerId))
      {
        examRuntimeData = examRuntimeDataRepository.getRuntimeData(examId, learnerId);
        List<ExamInteractionDto> examInteractions = examRuntimeData.getInteractions();
        ExamInteractionDto currentInteraction = examInteractions.stream().filter(ExamInteractionDto::isOngoing).findFirst().orElse(null);
        if (currentInteraction != null)
        {
          return toLearnerExamLaunchDto(
              examRuntimeData,
              exam,
              currentInteraction.getScore(),
              getPercentage(examRuntimeData.getMaxScore(), currentInteraction.getScore()),
              getRemainingAttempt(examRuntimeData.getMaxAttempt(), examRuntimeData.getInteractions().size()),
              getSpentTime(currentInteraction.getSpentTime()), currentInteraction.isOngoing());
        }
        else if (!examInteractions.isEmpty())
        {
          currentInteraction = examInteractions.get(examInteractions.size() - 1);
          return toLearnerExamLaunchDto(
              examRuntimeData,
              exam,
              currentInteraction.getScore(),
              getPercentage(examRuntimeData.getMaxScore(), currentInteraction.getScore()),
              getRemainingAttempt(examRuntimeData.getMaxAttempt(), examRuntimeData.getInteractions().size()),
              getSpentTime(currentInteraction.getSpentTime()), currentInteraction.isOngoing());
        }
        else
        {
          return toLearnerExamLaunchDto(
              examRuntimeData,
              exam,
              0,
              "0%",
              examRuntimeData.getMaxAttempt(),
              "0 сек",
              false);
        }
      }
      else
      {
        LearnerExamLaunchDto examLaunchDto = new LearnerExamLaunchDto(exam.getAuthor(), exam.getExamConfig().getDuration() + "мин",
            exam.getExamConfig().getThresholdScore(), exam.getName(), exam.getDescription(), exam.getExamStatus());
        examLaunchDto.setMaxAttempt(exam.getExamConfig().getAttempt());
        examLaunchDto.setRemainingAttempt(exam.getExamConfig().getAttempt());
        examLaunchDto.setMaxScore(exam.getExamConfig().getMaxScore());
        examLaunchDto.setScore(0);
        examLaunchDto.setScorePercentage("0%");
        examLaunchDto.setSpentTime("0 сек");
        return examLaunchDto;
      }
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }

  private String getPercentage(double maxScore, double score)
  {
    if (score != 0 && maxScore != 0)
    {
      double percentage = Math.floor((score * 100) / maxScore);
      if(percentage > 100 ){
        return 100 + "%";
      }
      else {
        return percentage + "%";
      }
    }
    else
    {
      return "0%";
    }
  }

  private int getRemainingAttempt(int maxAttempt, int attempts)
  {
    int remainingAttempts = maxAttempt - attempts;
    if (remainingAttempts < 0)
    {
      return 0;
    }
    else
    {
      return remainingAttempts;
    }
  }

  private String getSpentTime(double spentTime)
  {
    int minutes = (int) Math.floor(spentTime / 60);
    if (minutes < 1)
    {
      int seconds = (int) Math.floor(spentTime);
      return seconds + " сек";
    }
    else
    {
      return minutes + " мин";
    }
  }
}
