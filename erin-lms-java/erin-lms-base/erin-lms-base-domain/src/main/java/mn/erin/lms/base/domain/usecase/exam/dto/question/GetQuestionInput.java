package mn.erin.lms.base.domain.usecase.exam.dto.question;

/**
 * @author Oyungerel Chuluunsukh
 **/
public class GetQuestionInput
{
  private String categoryId;
  private String groupId;
  private int score;

  public GetQuestionInput(String categoryId, String groupId, int score)
  {
    this.categoryId = categoryId;
    this.groupId = groupId;
    this.score = score;
  }

  public GetQuestionInput(String categoryId, String groupId)
  {
    this.categoryId = categoryId;
    this.groupId = groupId;
  }

  public String getCategoryId()
  {
    return categoryId;
  }

  public void setCategoryId(String categoryId)
  {
    this.categoryId = categoryId;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String groupId)
  {
    this.groupId = groupId;
  }

  public int getScore()
  {
    return score;
  }

  public void setScore(int score)
  {
    this.score = score;
  }
}
