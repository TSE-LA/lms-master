package mn.erin.lms.base.domain.model.exam;

import java.util.List;

/**
 * @author Galsan Bayart.
 */
public class DeclinedUser
{
  String examId;
  List<DeclinedUserInfo> declinedUserInfos;

  public DeclinedUser(String examId, List<DeclinedUserInfo> declinedUserInfos)
  {
    this.examId = examId;
    this.declinedUserInfos = declinedUserInfos;
  }

  public String getExamId()
  {
    return examId;
  }

  public void setExamId(String examId)
  {
    this.examId = examId;
  }

  public List<DeclinedUserInfo> getDeclinedUserInfos()
  {
    return declinedUserInfos;
  }

  public void setDeclinedUserInfos(List<DeclinedUserInfo> declinedUserInfos)
  {
    this.declinedUserInfos = declinedUserInfos;
  }
}
