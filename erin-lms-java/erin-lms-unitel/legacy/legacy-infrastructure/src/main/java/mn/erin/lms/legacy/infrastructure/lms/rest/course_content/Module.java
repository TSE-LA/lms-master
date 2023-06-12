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
public class Module
{
  private String name;
  private Map<String, byte[]> sectionFiles;

  public Module(String name, Map<String, byte[]> sectionFiles)
  {
    this.name = name;
    this.sectionFiles = sectionFiles;
  }

  public String getName()
  {
    return name;
  }

  public Map<String, byte[]> getSectionFiles()
  {
    return sectionFiles;
  }
}
