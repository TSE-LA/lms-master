package mn.erin.lms.base.analytics.model.learner;

import mn.erin.lms.base.analytics.model.Analytic;

/**
 * @author Temuulen Naranbold
 */
public class LearnerProgress implements Analytic
{
  private String username;
  private String groupPath;
  private String role;
  private double progress;

  public LearnerProgress(String username, String groupPath, String role, double progress)
  {
    this.username = username;
    this.groupPath = groupPath;
    this.role = role;
    this.progress = progress;
  }

  public LearnerProgress(String username, double progress)
  {
    this.username = username;
    this.progress = progress;
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public String getGroupPath()
  {
    return groupPath;
  }

  public void setGroupPath(String groupPath)
  {
    this.groupPath = groupPath;
  }

  public String getRole()
  {
    return role;
  }

  public void setRole(String role)
  {
    this.role = role;
  }

  public double getProgress()
  {
    return progress;
  }

  public void setProgress(double progress)
  {
    this.progress = progress;
  }
}
