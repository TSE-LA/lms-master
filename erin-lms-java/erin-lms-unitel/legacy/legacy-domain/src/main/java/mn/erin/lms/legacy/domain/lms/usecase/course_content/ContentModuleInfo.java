/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_content;

import java.util.List;

/**
 * author Tamir Batmagnai.
 */
public class ContentModuleInfo
{
  private String name;
  private List<ContentSectionInfo> sectionInfos;
  private Integer index;
  private String fileType;

  public ContentModuleInfo(String name, List<ContentSectionInfo> sectionInfos)
  {
    this.name = name;
    this.sectionInfos = sectionInfos;
  }

  public ContentModuleInfo(String name, List<ContentSectionInfo> sectionInfos, Integer index, String fileType)
  {
    this.name = name;
    this.sectionInfos = sectionInfos;
    this.index = index;
    this.fileType = fileType;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public List<ContentSectionInfo> getSectionInfos()
  {
    return sectionInfos;
  }

  public void setSectionInfos(List<ContentSectionInfo> sectionInfos)
  {
    this.sectionInfos = sectionInfos;
  }

  public Integer getIndex()
  {
    return index;
  }

  public void setIndex(Integer index)
  {
    this.index = index;
  }

  public String getFileType() { return fileType;}

}
