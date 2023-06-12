package mn.erin.lms.base.domain.model.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mn.erin.domain.base.model.Entity;
import mn.erin.lms.base.domain.model.course.CourseId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseContent implements Entity<CourseContent>
{
  private final CourseContentId id;
  private CourseId courseId;

  private List<CourseModule> modules = new ArrayList<>();
  private List<Attachment> attachments = new ArrayList<>();

  public CourseContent(CourseContentId id)
  {
    this.id = Objects.requireNonNull(id, "Course content id cannot be null!");
  }

  public CourseContent(CourseContentId id, CourseId courseId)
  {
    this.id = Objects.requireNonNull(id, "Course content id cannot be null!");
    this.courseId = Objects.requireNonNull(courseId, "Course id cannot be null!");
  }

  public void addModule(CourseModule module)
  {
    if (module != null && !this.modules.contains(module))
    {
      this.modules.add(module);
    }
  }

  public void addAttachment(Attachment attachment)
  {
    if (attachment != null && !this.attachments.contains(attachment))
    {
      this.attachments.add(attachment);
    }
  }

  public CourseContentId getId()
  {
    return id;
  }

  public CourseId getCourseId()
  {
    return courseId;
  }

  public List<CourseModule> getModules()
  {
    return modules;
  }

  public List<Attachment> getAttachments()
  {
    return attachments;
  }

  @Override
  public boolean sameIdentityAs(CourseContent other)
  {
    return this.id.equals(other.id);
  }
}
