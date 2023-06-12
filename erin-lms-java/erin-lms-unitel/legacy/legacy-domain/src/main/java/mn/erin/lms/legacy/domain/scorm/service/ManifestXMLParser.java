/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.service;

import java.util.List;
import java.util.Set;

import mn.erin.lms.legacy.domain.scorm.model.ContentAggregation;
import mn.erin.lms.legacy.domain.scorm.model.ManifestXMLFile;
import mn.erin.lms.legacy.domain.scorm.model.SCO;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface ManifestXMLParser
{
  /**
   * Creates a manifest XML file ("imsmanifest.xml") according to the specified content aggregation model.
   * For more information on how an imsmanifest.xml looks like, please refer to:
   * https://scorm.com/scorm-explained/technical-scorm/golf-examples/
   *
   * @param contentAggregation   The structure of the SCORM content
   * @param commonFiles          Common files or dependencies used across every part of the SCORM content.
   *                             This may include components of a SCORM wrapper used to create the SCORM content.
   * @param wrapperPathComponent The path/query string parameter used for launching a SCO.
   *                             For example, the launch url of a SCO could be "index.html?content=sco",
   *                             in which case "index.html?content=" will be the wrapper path component,
   *                             and the query string parameter value "sco" will be the short identifier
   *                             of the Organization
   *                             in the content aggregation model.
   * @return the generated manifest XML file.
   * @throws ManifestParserException if failed to generate the manifest XML file.
   */
  byte[] generateManifestXMLFile(ContentAggregation contentAggregation, List<String> commonFiles, String wrapperPathComponent)
      throws ManifestParserException;

  /**
   * Reads the manifest XML (imsmanifest.xml) file of a SCORM content, and extracts its SCOs from it.
   *
   * Note: The order of the SCOs must be preserved as in the manifest XML file.
   *
   * @param manifestXMLFile The manifest XML file
   * @return The SCOs of the SCORM content.
   * @throws ManifestParserException if failed to read the manifest XML file
   */
  Set<SCO> readManifestXMLFile(ManifestXMLFile manifestXMLFile) throws ManifestParserException;
}
