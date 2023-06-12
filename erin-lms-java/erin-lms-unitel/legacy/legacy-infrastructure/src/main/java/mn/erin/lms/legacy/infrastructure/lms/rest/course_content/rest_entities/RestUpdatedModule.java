/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.rest.course_content.rest_entities;

import java.util.List;

/**
 * author Tamir Batmagnai.
 */
public class RestUpdatedModule
{
  private String initName;
  private String updatedName;
  private String fileType;
  private Integer index;
  private List<RestSection> sectionList;

  public RestUpdatedModule()
  {
  }

  public RestUpdatedModule(String initName, String updatedName, Integer index,
      List<RestSection> sectionList, String fileType)
  {
    this.initName = initName;
    this.updatedName = updatedName;
    this.index = index;
    this.sectionList = sectionList;
    this.fileType = fileType;
  }

  public String getInitName()
  {
    return initName;
  }

  public String getUpdatedName()
  {
    return updatedName;
  }

  public Integer getIndex()
  {
    return index;
  }

  public List<RestSection> getSectionList()
  {
    return sectionList;
  }

  public String getFileType() {return fileType;}

  public void setInitName(String initName)
  {
    this.initName = initName;
  }

  public void setUpdatedName(String updatedName)
  {
    this.updatedName = updatedName;
  }

  public void setIndex(Integer index)
  {
    this.index = index;
  }

  public void setFileType(String fileType) { this.fileType = fileType;}

  public void setSectionList(List<RestSection> sectionList)
  {
    this.sectionList = sectionList;
  }
}
