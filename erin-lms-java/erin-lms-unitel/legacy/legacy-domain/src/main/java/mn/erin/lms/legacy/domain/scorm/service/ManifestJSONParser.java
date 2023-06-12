/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.service;

import mn.erin.lms.legacy.domain.scorm.model.ContentAggregation;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface ManifestJSONParser
{
  /**
   * Creates a manifest JSON file according to the specified content aggregation model.
   * This JSON file is used for the SCORM Wrapper
   * and its content may look like:
   * {
   * "data": {
   * "queryString": ["path/to/asset"]
   * }
   * }
   * <p>
   * The query string represents the identifier for the SCO. For example, suppose there is a SCO
   * with the name "chapter1", and in order to access its assets, and of course its run-time data,
   * this query string will be used like "index.html?content=chapter1", where index.html is the
   * SCORM wrapper. By accessing the query string identifier, the wrapper will fetch all the
   * paths from which to get the Assets.
   *
   * @param contentAggregation The structure of the SCORM content
   * @return the generated manifest JSON file
   * @throws ManifestParserException if failed to generate the manifest JSON file
   */
  byte[] generateManifestJSONFile(ContentAggregation contentAggregation) throws ManifestParserException;
}
