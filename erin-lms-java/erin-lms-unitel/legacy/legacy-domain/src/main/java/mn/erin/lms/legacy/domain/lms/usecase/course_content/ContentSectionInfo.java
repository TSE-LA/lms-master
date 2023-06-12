/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_content;

/**
 * author Tamir Batmagnai.
 */
public class ContentSectionInfo
{
  private String name;
  private Integer index;
  private String fileId;


  public ContentSectionInfo(String name, String fileId)
  {
    this.name = name;
    this.fileId = fileId;
  }

  public ContentSectionInfo(String name, String fileId, Integer index)
  {
    this.name = name;
    this.fileId = fileId;
    this.index = index;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getFileId()
  {
    return fileId;
  }

  public void setFileId(String fileId)
  {
    this.fileId = fileId;
  }

  public Integer getIndex()
  {
    return index;
  }

  public void setIndex(Integer index)
  {
    this.index = index;
  }
}
