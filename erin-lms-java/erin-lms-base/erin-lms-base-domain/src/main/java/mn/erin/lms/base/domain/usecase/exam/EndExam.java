package mn.erin.lms.base.domain.usecase.exam;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.exam.question.Answer;
import mn.erin.lms.base.domain.model.exam.question.LearnerAnswerEntity;
import mn.erin.lms.base.domain.model.exam.question.LearnerQuestionDto;
import mn.erin.lms.base.domain.model.exam.question.Question;
import mn.erin.lms.base.domain.model.exam.question.QuestionType;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamInteractionDto;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamRuntimeData;

/**
 * @author Byambajav
 */

@Authorized(users = { LmsUser.class })
public class EndExam extends ExamUseCase<String, Void>
{

  public EndExam(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  protected Void executeImpl(String examId) throws UseCaseException, LmsRepositoryException
  {
    try
    {
      Validate.notBlank(examId);
    }
    catch (NullPointerException e)
    {
      throw new UseCaseException("End an exam: exam id cannot be null");
    }
    String learnerId = lmsServiceRegistry.getAuthenticationService().getCurrentUsername();
    ExamRuntimeData examRuntimeData = examRuntimeDataRepository.getRuntimeData(examId, learnerId);
    for (ExamInteractionDto examInteraction : examRuntimeData.getInteractions())
    {
      if (examInteraction.isOngoing())
      {
        examInteraction.setScore(calculateScore(examInteraction.getGivenQuestions()));
        examInteraction.setLastLaunch(new Date());
        examInteraction.setOngoing(false);
      }
    }
    examRuntimeDataRepository.update(examId, learnerId, examRuntimeData.getInteractions());
    return null;
  }

  private double calculateScore(List<LearnerQuestionDto> learnerQuestions) throws LmsRepositoryException
  {
    double score = 0;
    for (LearnerQuestionDto learnerQuestion : learnerQuestions)
    {
      if (learnerQuestion.getType() == QuestionType.SINGLE_CHOICE)
      {
        Question currentQuestion = lmsRepositoryRegistry.getQuestionRepository().findById(learnerQuestion.getId());
        Answer correctAnswer = currentQuestion.getAnswers().stream().filter(Answer::isCorrect).findFirst().orElse(null);
        LearnerAnswerEntity learnerAnswer = learnerQuestion.getAnswers().stream().filter(LearnerAnswerEntity::isSelected).findFirst().orElse(null);
        if (correctAnswer != null && learnerAnswer != null && Objects.equals(correctAnswer.getValue(), learnerAnswer.getValue()))
        {
          score += currentQuestion.getScore();
        }
      }
    }
    return score;
  }
}
