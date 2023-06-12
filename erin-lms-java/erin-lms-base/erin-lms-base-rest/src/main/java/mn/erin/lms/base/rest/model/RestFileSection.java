package mn.erin.lms.base.rest.model;

import java.util.List;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestFileSection
{
  private String attachmentFileId;
  private List<RestSection> restSections;
  private String absolutePath;
  private boolean codecSupported;

  public RestFileSection()
  {
  }

  public RestFileSection(String attachmentFileId, List<RestSection> restSections)
  {
    this.attachmentFileId = attachmentFileId;
    this.restSections = restSections;
  }

  public RestFileSection(String attachmentFileId, List<RestSection> restSections, String absolutePath, boolean codecSupported)
  {
    this.attachmentFileId = attachmentFileId;
    this.restSections = restSections;
    this.absolutePath = absolutePath;
    this.codecSupported = codecSupported;
  }

  public String getAttachmentFileId()
  {
    return attachmentFileId;
  }

  public List<RestSection> getRestSections()
  {
    return restSections;
  }

  public void setAttachmentFileId(String attachmentFileId)
  {
    this.attachmentFileId = attachmentFileId;
  }

  public void setRestSections(List<RestSection> restSections)
  {
    this.restSections = restSections;
  }

  public void setAbsolutePath(String absolutePath) { this.absolutePath = absolutePath;}

  public String getAbsolutePath() { return absolutePath;}

  public void setCodecSupported(boolean codecSupported) { this.codecSupported = codecSupported;}

  public boolean getCodecSupported() { return  codecSupported;}

}
