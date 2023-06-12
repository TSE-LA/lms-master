package mn.erin.lms.base.domain.usecase.content.dto;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class SplitPdfToImagesInput
{
  private final byte[] imagePayload;
  private final ImageExtension imageExtension;
  private final String courseId;
  private final String courseType;

  public SplitPdfToImagesInput(byte[] imagePayload, ImageExtension imageExtension, String courseId, String courseType)
  {
    this.imagePayload = imagePayload;
    this.imageExtension = Validate.notNull(imageExtension);
    this.courseId = Validate.notNull(courseId);
    this.courseType = courseType;
  }

  public byte[] getImagePayload()
  {
    return imagePayload;
  }

  public ImageExtension getImageExtension()
  {
    return imageExtension;
  }

  public String getCourseId() { return courseId;}

  public String getCourseType() { return courseType;}
}
