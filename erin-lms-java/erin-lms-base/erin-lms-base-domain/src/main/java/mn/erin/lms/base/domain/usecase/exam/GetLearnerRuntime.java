package mn.erin.lms.base.domain.usecase.exam;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.exam.question.Answer;
import mn.erin.lms.base.domain.model.exam.question.LearnerAnswerEntity;
import mn.erin.lms.base.domain.model.exam.question.LearnerQuestionDto;
import mn.erin.lms.base.domain.model.exam.question.Question;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamInteractionDto;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamRuntimeData;
import mn.erin.lms.base.domain.usecase.exam.dto.LearnerReportByCategory;

/**
 * @author Byambajav
 */
@Authorized(users = { LmsUser.class })
public class GetLearnerRuntime extends ExamUseCase<String, Map<String, LearnerReportByCategory>>
{
  public GetLearnerRuntime(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  protected Map<String, LearnerReportByCategory> executeImpl(String learnerId) throws UseCaseException, LmsRepositoryException
  {
    List<ExamRuntimeData> examRuntimeDataList = examRuntimeDataRepository.getRuntimeData(learnerId);
    ExamRuntimeData examRuntimeData = examRuntimeDataList.stream().filter(runtimeData -> !runtimeData.getInteractions().isEmpty()).findFirst().orElse(null);
    ExamInteractionDto interaction = null;
    List<LearnerQuestionDto> questionList = null;
    Set<String> categories = new HashSet<>();
    if (examRuntimeData != null)
    {
      interaction = examRuntimeData.getInteractions().stream().filter(runtimeData -> !runtimeData.getGivenQuestions().isEmpty()).findFirst().orElse(null);
    }
    if (interaction != null)
    {
      questionList = interaction.getGivenQuestions();
    }
    if (questionList != null)
    {
      for (LearnerQuestionDto learnerQuestion : questionList)
      {
        categories.add(lmsRepositoryRegistry.getQuestionRepository().findById(learnerQuestion.getId()).getDetail().getCategoryId().getId());
      }
    }
    return calculateScore(questionList, categories);
  }

  private Map<String, LearnerReportByCategory> calculateScore(List<LearnerQuestionDto> learnerQuestions, Set<String> categories) throws LmsRepositoryException
  {
    Map<String, LearnerReportByCategory> categorizedQuestions = new HashMap<>();
    for (String categoryId : categories)
    {
      double score = 0;
      for (LearnerQuestionDto learnerQuestion : learnerQuestions)
      {
        Question currentQuestion = lmsRepositoryRegistry.getQuestionRepository().findById(learnerQuestion.getId());
        if (Objects.equals(categoryId, currentQuestion.getDetail().getCategoryId().getId()))
        {
          Answer correctAnswer = currentQuestion.getAnswers().stream().filter(Answer::isCorrect).findFirst().orElse(null);
          LearnerAnswerEntity learnerAnswer = learnerQuestion.getAnswers().stream().filter(LearnerAnswerEntity::isSelected).findFirst().orElse(null);
          if (correctAnswer != null && learnerAnswer != null && Objects.equals(correctAnswer.getValue(), learnerAnswer.getValue()))
          {
            score += currentQuestion.getScore();
          }
        }
      }
      categorizedQuestions.put(categoryId,
          new LearnerReportByCategory(lmsRepositoryRegistry.getQuestionCategoryRepository().getCategoryName(categoryId), score));
    }

    return categorizedQuestions;
  }
}
