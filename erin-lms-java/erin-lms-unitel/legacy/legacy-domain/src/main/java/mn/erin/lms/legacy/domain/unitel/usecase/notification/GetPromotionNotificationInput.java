package mn.erin.lms.legacy.domain.unitel.usecase.notification;

import org.apache.commons.lang3.Validate;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class GetPromotionNotificationInput
{
  private String learnerId;

  public GetPromotionNotificationInput(String learnerId)
  {
    this.learnerId = Validate.notBlank(learnerId, "Learner id cannot be null!");
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public void setLearnerId(String learnerId)
  {
    this.learnerId = learnerId;
  }
}
