/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.rest.course_content.rest_entities;

import java.util.List;
import java.util.Objects;

/**
 * author Tamir Batmagnai.
 */
public class RestModule
{
  private String name;
  private Integer index;
  private List<RestSection> sectionList;
  private String fileType;

  public RestModule(){}

  public RestModule(String name, Integer index, List<RestSection> sectionList, String fileType)
  {
    this.name = Objects.requireNonNull(name);
    this.index = Objects.requireNonNull(index);
    this.sectionList = Objects.requireNonNull(sectionList);
    this.fileType = fileType;
  }

  public String getName()
  {
    return this.name;
  }

  public Integer getIndex()
  {
    return this.index;
  }

  public List<RestSection> getSectionList()
  {
    return this.sectionList;
  }

  public String getFileType() {return this.fileType;}
}
