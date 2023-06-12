package mn.erin.lms.unitel.mongo.document;

import mn.erin.lms.base.mongo.document.assessment.MongoQuestionType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class MongoAssessmentReportItem
{
  private Integer index;
  private String question;
  private MongoQuestionType questionType;

  private List<MongoAssessmentAnswer> answers = new ArrayList<>();

  public MongoAssessmentReportItem()
  {
  }

  public MongoAssessmentReportItem(Integer index, String question, MongoQuestionType questionType,
      List<MongoAssessmentAnswer> answers)
  {
    this.index = index;
    this.question = question;
    this.questionType = questionType;
    this.answers = answers;
  }

  public Integer getIndex()
  {
    return index;
  }

  public String getQuestion()
  {
    return question;
  }

  public MongoQuestionType getQuestionType()
  {
    return questionType;
  }

  public List<MongoAssessmentAnswer> getAnswers()
  {
    return answers;
  }
}
