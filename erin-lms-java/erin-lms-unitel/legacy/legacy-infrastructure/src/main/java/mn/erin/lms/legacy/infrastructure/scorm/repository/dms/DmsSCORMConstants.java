/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.scorm.repository.dms;

/**
 * @author Bat-Erdene Tsogoo.
 */
final class DmsSCORMConstants
{
  private DmsSCORMConstants()
  {

  }

  // Folders
  static final String BASE_SCORM_FOLDER = "SCORM";
  static final String BASE_SCORM_CONTENT_FOLDER = "SCORM Contents";
  static final String BASE_SCORM_WRAPPER_FOLDER = "SCORM Wrappers";
  static final String DEFAULT_WRAPPER_FOLDER = "Default";
  static final String XML_SCHEMA_DEFINITION_FILES_FOLDER = "xsds";
  static final String SHARED_FOLDER = "shared";

  // Files
  static final String MANIFEST_XML_FILE = "imsmanifest.xml";
  static final String MANIFEST_JSON_FILE = "data.json";
  static final String INDEX_HTML_FILE = "index.html";
}
