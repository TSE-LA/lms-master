package mn.erin.lms.base.rest.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestPublishOption
{
  private boolean sendEmail;
  private boolean sendSms;
  private String note;
  private Date publishDate;
  private Set<String> assignedDepartments = new HashSet<>();
  private Set<String> assignedLearners = new HashSet<>();
  //    TODO: Remove this logic when version is 3.0.0
  private boolean autoChildDepartmentEnroll = true;

  public boolean isSendEmail()
  {
    return sendEmail;
  }

  public void setSendEmail(boolean sendEmail)
  {
    this.sendEmail = sendEmail;
  }

  public boolean isSendSms()
  {
    return sendSms;
  }

  public void setSendSms(boolean sendSms)
  {
    this.sendSms = sendSms;
  }

  public String getNote()
  {
    return note;
  }

  public void setNote(String note)
  {
    this.note = note;
  }

  public Date getPublishDate()
  {
    return publishDate;
  }

  public void setPublishDate(Date publishDate)
  {
    this.publishDate = publishDate;
  }

  public Set<String> getAssignedDepartments()
  {
    return assignedDepartments;
  }

  public void setAssignedDepartments(Set<String> assignedDepartments)
  {
    this.assignedDepartments = assignedDepartments;
  }

  public Set<String> getAssignedLearners()
  {
    return assignedLearners;
  }

  public void setAssignedLearners(Set<String> assignedLearners)
  {
    this.assignedLearners = assignedLearners;
  }

  public boolean isAutoChildDepartmentEnroll()
  {
    return autoChildDepartmentEnroll;
  }

  public void setAutoChildDepartmentEnroll(boolean autoChildDepartmentEnroll)
  {
    this.autoChildDepartmentEnroll = autoChildDepartmentEnroll;
  }
}
