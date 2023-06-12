package mn.erin.lms.base.domain.usecase.course.classroom.dto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class AttendanceDto
{
  private String employeeName;
  private String department;
  private String supervisor;
  private String state;
  private boolean isPresent;
  private String grade1;
  private String grade2;
  private String grade3;

  public String getEmployeeName()
  {
    return employeeName;
  }

  public void setEmployeeName(String employeeName)
  {
    this.employeeName = employeeName;
  }

  public String getDepartment()
  {
    return department;
  }

  public void setDepartment(String department)
  {
    this.department = department;
  }

  public String getSupervisor()
  {
    return supervisor;
  }

  public void setSupervisor(String supervisor)
  {
    this.supervisor = supervisor;
  }

  public String getState()
  {
    return state;
  }

  public void setState(String state)
  {
    this.state = state;
  }

  public boolean isPresent()
  {
    return isPresent;
  }

  public void setPresent(boolean present)
  {
    isPresent = present;
  }

  public String getGrade1()
  {
    return grade1;
  }

  public void setGrade1(String grade1)
  {
    this.grade1 = grade1;
  }

  public String getGrade2()
  {
    return grade2;
  }

  public void setGrade2(String grade2)
  {
    this.grade2 = grade2;
  }

  public String getGrade3()
  {
    return grade3;
  }

  public void setGrade3(String grade3)
  {
    this.grade3 = grade3;
  }
}
