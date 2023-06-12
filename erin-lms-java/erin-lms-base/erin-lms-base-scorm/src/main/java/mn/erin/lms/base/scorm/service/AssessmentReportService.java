package mn.erin.lms.base.scorm.service;

import java.util.Map;


/**
 * @author Erdenetulga
 */
public interface AssessmentReportService
{
  void save(Map<String, Object> data, String courseId) throws AssessmentReportException;
}
