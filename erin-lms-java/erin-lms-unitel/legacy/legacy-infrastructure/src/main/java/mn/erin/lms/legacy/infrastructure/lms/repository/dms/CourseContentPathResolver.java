package mn.erin.lms.legacy.infrastructure.lms.repository.dms;

public interface CourseContentPathResolver
{
  void setCourseFolderPath(String courseId);

  String getCourseFolderPath(String courseId, String moduleName, String fileId);
}
