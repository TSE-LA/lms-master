/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.scorm.factory;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class UnsupportedDataModelException extends RuntimeException
{
  public UnsupportedDataModelException(String dataModel)
  {
    super("Unsupported SCORM run-time data model: " + dataModel);
  }
}
