package mn.erin.lms.base.domain.model.exam;

import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

/**
 * @author Temuulen Naranbold
 */
public class HistoryOfModification
{
  private final String modifiedUser;
  private final Date modifiedDate;

  public HistoryOfModification(String modifiedUser, Date modifiedDate)
  {
    this.modifiedUser = Validate.notBlank(modifiedUser);
    this.modifiedDate = Objects.requireNonNull(modifiedDate);
  }

  public String getModifiedUser()
  {
    return modifiedUser;
  }

  public Date getModifiedDate()
  {
    return modifiedDate;
  }
}
