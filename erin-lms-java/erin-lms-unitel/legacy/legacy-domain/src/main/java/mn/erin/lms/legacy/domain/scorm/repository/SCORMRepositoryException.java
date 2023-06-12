/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.repository;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class SCORMRepositoryException extends Exception
{
  public SCORMRepositoryException(String message)
  {
    super(message);
  }

  public SCORMRepositoryException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
