package mn.erin.lms.base.domain.model.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mn.erin.domain.base.model.ValueObject;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class CourseModule implements ValueObject<CourseModule>, Comparable<CourseSection>
{
  private static final String ERROR_MSG_COURSE_MODULE = "Course module name cannot be null!";
  private static final String ERROR_MSG_COURSE_INDEX = "Course module index cannot be null!";

  private String name;
  private Integer index;
  private List<CourseSection> sectionList = new ArrayList<>();
  private String fileType;
  private String moduleFolderId;
  public CourseModule(String name)
  {
    this.name = Objects.requireNonNull(name, ERROR_MSG_COURSE_MODULE);
  }

  public CourseModule(String name, Integer index, String fileType, String moduleFolderId)
  {
    this.name = Objects.requireNonNull(name, ERROR_MSG_COURSE_MODULE);
    this.index = Objects.requireNonNull(index, ERROR_MSG_COURSE_INDEX);
    this.fileType = fileType;
    this.moduleFolderId = moduleFolderId;
  }

  public String getName()
  {
    return name;
  }

  public String getFileType() {return fileType;}
  public void addSection(CourseSection section)
  {
    if (section != null && !this.sectionList.contains(section))
    {
      this.sectionList.add(section);
    }
  }
  public String getModuleFolderId() { return moduleFolderId;}
  public List<CourseSection> getSectionList()
  {
    return sectionList;
  }

  public Integer getIndex()
  {
    return index;
  }

  @Override
  public boolean sameValueAs(CourseModule other)
  {
    return other != null && (this.name.equals(other.name) && this.sectionList.equals(other.sectionList));
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof CourseModule)
    {
      return sameValueAs((CourseModule) obj);
    }
    return false;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(name, sectionList);
  }

  @Override
  public String toString()
  {
    return "CourseModule{" +
        "name='" + name + '\'' +
        ", sectionList=" + sectionList +
        '}';
  }

  @Override
  public int compareTo(CourseSection other)
  {
    return Integer.compare(this.index, other.getIndex());
  }
}
