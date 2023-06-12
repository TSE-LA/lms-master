package mn.erin.lms.base.domain.model.course;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.ValueObject;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseDetail implements ValueObject<CourseDetail>
{
  private final String title;

  private PublishStatus publishStatus = PublishStatus.UNPUBLISHED;

  private String description;
  private String thumbnailUrl;
  private DateInfo dateInfo;
  private double credit;
  private int certifiedNumber = 0;

  private boolean hasQuiz = false;
  private boolean hasFeedbackOption = false;
  private boolean hasAssessment = false;
  private boolean hasCertificate = false;
  private Map<String, String> properties = new TreeMap<>();

  public CourseDetail(String title)
  {
    this.title = Validate.notBlank(title, "Course title cannot be null or blank!");
  }

  @Override
  public boolean sameValueAs(CourseDetail other)
  {
    return this.title.equals(other.title);
  }

  public void addProperties(Map<String, String> properties)
  {
    this.properties.putAll(properties);
  }

  public void addProperty(String key, String value)
  {
    if (!StringUtils.isBlank(key) && !StringUtils.isBlank(value))
    {
      properties.put(key, value);
    }
  }

  public void changePublishStatus(PublishStatus publishStatus)
  {
    if (publishStatus != null)
    {
      this.publishStatus = publishStatus;
    }
  }

  public void setThumbnailUrl(String thumbnailUrl)
  {
    this.thumbnailUrl = thumbnailUrl;
  }

  public void setHasQuiz(boolean hasQuiz)
  {
    this.hasQuiz = hasQuiz;
  }

  public void setHasFeedbackOption(boolean hasFeedbackOption)
  {
    this.hasFeedbackOption = hasFeedbackOption;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public void setDateInfo(DateInfo dateInfo)
  {
    this.dateInfo = dateInfo;
  }

  public void setCredit(double credit)
  {
    this.credit = credit;
  }

  public void setCertifiedNumber(int certifiedNumber)
  {
    this.certifiedNumber = certifiedNumber;
  }

  public boolean hasQuiz()
  {
    return hasQuiz;
  }

  public boolean hasFeedbackOption()
  {
    return hasFeedbackOption;
  }

  public Map<String, String> getProperties()
  {
    return properties;
  }

  public String getTitle()
  {
    return title;
  }

  public PublishStatus getPublishStatus()
  {
    return publishStatus;
  }

  public String getDescription()
  {
    return description;
  }

  public DateInfo getDateInfo()
  {
    return dateInfo;
  }

  public double getCredit()
  {
    return credit;
  }

  public int getCertifiedNumber()
  {
    return certifiedNumber;
  }

  public String getThumbnailUrl()
  {
    return thumbnailUrl;
  }

  public boolean hasAssessment() {
    return hasAssessment;
  }

  public void setHasAssessment(boolean hasAssessment) {
    this.hasAssessment = hasAssessment;
  }

  public boolean hasCertificate() {
    return hasCertificate;
  }

  public void setHasCertificate(boolean hasCertificate) {
    this.hasCertificate = hasCertificate;
  }
}
