package mn.erin.lms.base.mongo.document.exam;

import java.util.Date;

/**
 * @author Temuulen Naranbold
 */
public class MongoHistoryOfModification
{
  private String modifiedUser;
  private Date modifiedDate;

  public MongoHistoryOfModification()
  {
  }

  public MongoHistoryOfModification(String modifiedUser, Date modifiedDate)
  {
    this.modifiedUser = modifiedUser;
    this.modifiedDate = modifiedDate;
  }

  public String getModifiedUser()
  {
    return modifiedUser;
  }

  public void setModifiedUser(String modifiedUser)
  {
    this.modifiedUser = modifiedUser;
  }

  public Date getModifiedDate()
  {
    return modifiedDate;
  }

  public void setModifiedDate(Date modifiedDate)
  {
    this.modifiedDate = modifiedDate;
  }
}
