package mn.erin.lms.base.analytics.usecase.dto;

import java.util.List;

public class AnnouncementAnalytic
{
  private List<AnnouncementUserInfo> userInfo;
  private int totalView;

  public AnnouncementAnalytic(List<AnnouncementUserInfo> userInfo, int totalView)
  {
    this.userInfo = userInfo;
    this.totalView = totalView;
  }

  public List<AnnouncementUserInfo> getUserInfo()
  {
    return userInfo;
  }

  public int getTotalView()
  {
    return totalView;
  }
}
