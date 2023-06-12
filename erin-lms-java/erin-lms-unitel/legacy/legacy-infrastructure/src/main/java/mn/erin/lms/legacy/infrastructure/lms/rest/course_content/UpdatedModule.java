/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.rest.course_content;

import java.util.Map;

/**
 * author Tamir Batmagnai.
 */
public class UpdatedModule
{
  private String name;
  private String updatedName;
  private Map<String, byte[]> sectionFiles;
  private String fileType;

  public UpdatedModule(String name, String updatedName, Map<String, byte[]> sectionFiles, String fileType)
  {
    this.name = name;
    this.updatedName = updatedName;
    this.sectionFiles = sectionFiles;
    this.fileType = fileType;
  }

  public String getName()
  {
    return name;
  }

  public String getUpdatedName()
  {
    return updatedName;
  }

  public Map<String, byte[]> getSectionFiles()
  {
    return sectionFiles;
  }

  public String getFileType() { return fileType;}
}
