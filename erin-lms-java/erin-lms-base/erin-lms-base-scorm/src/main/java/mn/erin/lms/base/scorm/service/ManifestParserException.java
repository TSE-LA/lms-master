/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.scorm.service;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class ManifestParserException extends Exception
{
  public ManifestParserException(String message)
  {
    super(message);
  }

  public ManifestParserException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
