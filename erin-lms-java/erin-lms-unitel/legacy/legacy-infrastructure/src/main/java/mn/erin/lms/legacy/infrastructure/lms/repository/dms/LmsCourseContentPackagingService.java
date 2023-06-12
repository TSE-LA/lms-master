package mn.erin.lms.legacy.infrastructure.lms.repository.dms;

import org.apache.commons.lang3.Validate;

public class LmsCourseContentPackagingService implements CourseContentPathResolver
{
  private String courseFolderPath;

  @Override
  public void setCourseFolderPath(String courseId)
  {
    this.courseFolderPath = "/alfresco/Courses/" + Validate.notBlank(courseId, "Course ID cannot be null or blank");
  }

  @Override
  public String getCourseFolderPath(String courseId, String moduleName, String fileId)
  {
    return this.courseFolderPath + "/Course Content/" + moduleName + "/" + fileId;
  }
}
