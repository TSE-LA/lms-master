/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.repository;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class LMSRepositoryException extends Exception
{
  public LMSRepositoryException(String message)
  {
    super(message);
  }

  public LMSRepositoryException(String message, Throwable cause)
  {
    super(message, cause);
  }
}

