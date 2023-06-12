/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.constants;

import javax.xml.namespace.QName;

/**
 * The below constants are used in the "imsmanifest.xml" file of a SCORM content.
 *
 * @author Bat-Erdene Tsogoo.
 */
public final class ManifestConstants
{
  private ManifestConstants()
  {

  }

  // Manifest Template
  public static final String MANIFEST_TEMPLATE = "manifest-template.xml";

  // Elements
  public static final String ELEMENT_ORGANIZATIONS = "organizations";
  public static final String ELEMENT_ORGANIZATION = "organization";
  public static final String ELEMENT_ITEM = "item";
  public static final String ELEMENT_TITLE = "title";
  public static final String ELEMENT_RESOURCES = "resources";
  public static final String ELEMENT_RESOURCE = "resource";
  public static final String ELEMENT_FILE = "file";
  public static final String ELEMENT_DEPENDENCY = "dependency";

  // Attributes
  public static final String ATTR_DEFAULT = "default";
  public static final String ATTR_IDENTIFIER = "identifier";
  public static final String ATTR_IDENTIFIER_REF = "identifierref";
  public static final String ATTR_TYPE = "type";
  public static final String ATTR_HREF = "href";
  public static final String ATTR_SCORM_TYPE = "scormType";

  // Prefixes
  public static final String PREFIX_ADLCP = "adlcp";

  // Namespace URIs
  public static final String URI_ADL = "http://www.adlnet.org/xsd/adlcp_v1p3";
  public static final String URI_IMS = "http://www.imsglobal.org/xsd/imscp_v1p1";

  // QNames
  public static final QName QNAME_DEFAULT_ATTR = new QName("", ATTR_DEFAULT);
  public static final QName QNAME_SCORM_TYPE_ATTR = new QName(URI_ADL, ATTR_SCORM_TYPE);
  public static final QName QNAME_IDENTIFIER_ATTR = new QName("", ATTR_IDENTIFIER);
  public static final QName QNAME_IDENTIFIER_REF_ATTR = new QName("", ATTR_IDENTIFIER_REF);
  public static final QName QNAME_HREF_ATTR = new QName("", ATTR_HREF);
}
