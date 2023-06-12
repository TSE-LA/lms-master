package mn.erin.lms.base.domain.usecase.exam.dto;

import java.util.List;

import mn.erin.lms.base.domain.model.exam.question.LearnerQuestionDto;

/**
 * @author Byambajav
 */
public class QuestionBundleDto
{
  private String questionCategoryName;
  private List<LearnerQuestionDto> learnerQuestions;

  public QuestionBundleDto(String questionCategoryName, List<LearnerQuestionDto> learnerQuestions)
  {
    this.questionCategoryName = questionCategoryName;
    this.learnerQuestions = learnerQuestions;
  }

  public String getQuestionCategoryName()
  {
    return questionCategoryName;
  }

  public void setQuestionCategoryName(String questionCategoryName)
  {
    this.questionCategoryName = questionCategoryName;
  }

  public List<LearnerQuestionDto> getLearnerQuestions()
  {
    return learnerQuestions;
  }

  public void setLearnerQuestions(List<LearnerQuestionDto> learnerQuestions)
  {
    this.learnerQuestions = learnerQuestions;
  }
}
