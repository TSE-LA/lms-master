/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.rest;

/**
 * author Tamir Batmagnai.
 */
public class ErrorMessages
{
  private ErrorMessages() {}

  public static final String ERROR_MSG_COURSE_ID = "Course ID cannot be null!";
  public static final String ERROR_MSG_COURSE_CONTENT_ID = "Course content ID cannot be null!";

  public static final String ERROR_MSG_COURSE_CONTENT = "The course content cannot be null!";
  public static final String ERROR_MSG_COURSE_MODULE = "The course content module cannot be null!";
  public static final String ERR_MSG_COURSE_CONTENT_NOT_FOUND = "The course content with the ID: [%s] does not exist!";

  public static final String ERROR_MSG_CONTENT_REPO = "The course content repository is required!";
  public static final String ERROR_MSG_COURSE_REPO = "The course repository is required!";

  public static final String ERR_MSG_DOCUMENT_REPO = "The document repository required!";
  public static final String ERR_MSG_FOLDER_REPO = "The folder repository required!";
}
