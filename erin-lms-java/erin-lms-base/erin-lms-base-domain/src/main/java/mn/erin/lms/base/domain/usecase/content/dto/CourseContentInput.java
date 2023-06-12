package mn.erin.lms.base.domain.usecase.content.dto;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseContentInput
{
  private final String courseId;
  private final List<ModuleInfo> moduleInfoList;
  private final Map<String, SectionDto> sectionFiles;

  private List<File> attachments;

  public CourseContentInput(String courseId, List<ModuleInfo> moduleInfoList, Map<String, SectionDto> sectionFiles)
  {
    this.courseId = Validate.notBlank(courseId);
    this.moduleInfoList = Validate.notEmpty(moduleInfoList);
    this.sectionFiles = sectionFiles;
  }

  public CourseContentInput(String courseId, List<ModuleInfo> moduleInfoList, Map<String, SectionDto> sectionFiles, List<File> attachments)
  {
    this.courseId = Validate.notBlank(courseId);
    this.moduleInfoList = Validate.notEmpty(moduleInfoList);
    this.sectionFiles = Validate.notEmpty(sectionFiles);
    this.attachments = attachments;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public List<ModuleInfo> getModuleInfoList()
  {
    return moduleInfoList;
  }

  public Map<String, SectionDto> getSectionFiles()
  {
    return Collections.unmodifiableMap(sectionFiles);
  }

  public List<File> getAttachments()
  {
    return attachments;
  }
}
