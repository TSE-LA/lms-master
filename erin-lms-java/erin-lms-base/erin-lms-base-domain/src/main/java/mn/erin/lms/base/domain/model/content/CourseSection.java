package mn.erin.lms.base.domain.model.content;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.ValueObject;

public class CourseSection implements ValueObject<CourseSection>, Comparable<CourseSection>
{
  private static final String ERROR_MSG_SECTION_NAME = "The section name cannot be blank!";
  private static final String ERROR_MSG_ATTACHMENT_ID = "The section attachment id  cannot be blank!";
  private static final String ERROR_MSG_SECTION_INDEX = "The section index cannot be null!";

  private final String name;
  private Integer index;
  private AttachmentId attachmentId;

  public CourseSection(String name)
  {
    this.name = Validate.notBlank(name, ERROR_MSG_SECTION_NAME);
  }

  public CourseSection(String name, AttachmentId attachmentId)
  {
    this.name = Validate.notBlank(name, ERROR_MSG_SECTION_NAME);
    this.attachmentId = Objects.requireNonNull(attachmentId, ERROR_MSG_ATTACHMENT_ID);
  }

  public CourseSection(String name, AttachmentId attachmentId, Integer index)
  {
    this.name = Validate.notBlank(name, ERROR_MSG_SECTION_NAME);
    this.attachmentId = Objects.requireNonNull(attachmentId, ERROR_MSG_ATTACHMENT_ID);
    this.index = Objects.requireNonNull(index, ERROR_MSG_SECTION_INDEX);
  }

  public String getName()
  {
    return name;
  }

  public AttachmentId getAttachmentId()
  {
    return attachmentId;
  }

  public Integer getIndex()
  {
    return index;
  }

  @Override
  public boolean sameValueAs(CourseSection other)
  {
    return other != null && (this.name.equals(other.name) && this.attachmentId.equals(other.attachmentId));
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof CourseSection)
    {
      return sameValueAs((CourseSection) obj);
    }
    return false;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(name, attachmentId);
  }

  @Override
  public String toString()
  {
    return "CourseSection{" +
        "name='" + name + '\'' +
        ", attachmentId=" + attachmentId +
        '}';
  }

  @Override
  public int compareTo(CourseSection other)
  {
    return Integer.compare(this.index, other.getIndex());
  }
}
