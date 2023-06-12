package mn.erin.lms.base.domain.usecase.course.classroom.dto;

import java.util.List;

import mn.erin.lms.base.domain.usecase.course.dto.LearnerAttendanceInput;

/**
 * @author Erdenetulga
 */
public class CloseClassroomCourseInput
{
  private final String courseId;
  private String learnerId;
  private String certificateId;
  private String certificatePdfId;
  private final List<LearnerAttendanceInput> attendances;

  public CloseClassroomCourseInput(String courseId, List<LearnerAttendanceInput> attendances)
  {
    this.courseId = courseId;
    this.attendances = attendances;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public String getCertificateId()
  {
    return certificateId;
  }

  public String getCertificatePdfId()
  {
    return certificatePdfId;
  }

  public void setLearnerId(String learnerId)
  {
    this.learnerId = learnerId;
  }

  public void setCertificateId(String certificateId)
  {
    this.certificateId = certificateId;
  }

  public void setCertificatePdfId(String certificatePdfId)
  {
    this.certificatePdfId = certificatePdfId;
  }

  public List<LearnerAttendanceInput> getAttendances()
  {
    return attendances;
  }
}
