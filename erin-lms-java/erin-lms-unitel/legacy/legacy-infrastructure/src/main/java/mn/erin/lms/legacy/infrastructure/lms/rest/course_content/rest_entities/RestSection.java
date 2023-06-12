/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.rest.course_content.rest_entities;

import java.util.Objects;

/**
 * author Tamir Batmagnai.
 */
public class RestSection
{
  private String name;
  private Integer index;
  private String fileId;

  public RestSection() {}

  public RestSection(String name, Integer index, String fileId)
  {
    this.name = Objects.requireNonNull(name, "Course section name cannot be null!");
    this.index = Objects.requireNonNull(index, "Course section index cannot be null!");
    this.fileId = Objects.requireNonNull(fileId, "Course section file id cannot be null!");
  }

  public String getName()
  {
    return name;
  }

  public Integer getIndex()
  {
    return index;
  }

  public String getFileId()
  {
    return fileId;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public void setIndex(Integer index)
  {
    this.index = index;
  }

  public void setFileId(String fileId)
  {
    this.fileId = fileId;
  }

  public void updateFileId(String fileId)
  {
    if (null != fileId)
    {
      this.fileId = fileId;
    }
  }
}
