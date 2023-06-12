package mn.erin.lms.unitel.mongo.document;

/**
 * @author Erdenetulga
 */
public class EnrolledGroup
{
  private float enrollmentCount;
  private String enrolledGroup;

  public EnrolledGroup(){

  }
  public EnrolledGroup(float enrollmentCount, String enrolledGroup)
  {
    this.enrollmentCount = enrollmentCount;
    this.enrolledGroup = enrolledGroup;
  }

  public float getEnrollmentCount()
  {
    return enrollmentCount;
  }

  public void setEnrollmentCount(float enrollmentCount)
  {
    this.enrollmentCount = enrollmentCount;
  }

  public String getEnrolledGroup()
  {
    return enrolledGroup;
  }

  public void setEnrolledGroup(String enrolledGroup)
  {
    this.enrolledGroup = enrolledGroup;
  }
}
