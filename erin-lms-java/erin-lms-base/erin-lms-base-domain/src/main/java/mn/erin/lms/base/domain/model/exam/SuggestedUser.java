package mn.erin.lms.base.domain.model.exam;

import java.util.Set;

/**
 * @author Galsan Bayart.
 */
public class SuggestedUser
{
  String examId;
  Set<String> suggestedUsers;

  public SuggestedUser(String examId, Set<String> suggestedUsers)
  {
    this.examId = examId;
    this.suggestedUsers = suggestedUsers;
  }

  public String getExamId()
  {
    return examId;
  }

  public void setExamId(String examId)
  {
    this.examId = examId;
  }

  public Set<String> getSuggestedUsers()
  {
    return suggestedUsers;
  }

  public void setSuggestedUsers(Set<String> suggestedUsers)
  {
    this.suggestedUsers = suggestedUsers;
  }
}
