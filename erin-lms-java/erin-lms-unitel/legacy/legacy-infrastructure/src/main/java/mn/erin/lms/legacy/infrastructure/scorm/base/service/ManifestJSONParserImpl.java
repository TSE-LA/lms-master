/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.scorm.base.service;

import org.apache.commons.lang3.Validate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.lms.legacy.domain.scorm.model.ContentAggregation;
import mn.erin.lms.legacy.domain.scorm.service.ManifestJSONParser;
import mn.erin.lms.legacy.domain.scorm.service.ManifestParserException;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class ManifestJSONParserImpl implements ManifestJSONParser
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ManifestJSONParserImpl.class);

  private static final String PATH_DELIMITER = "/";

  @Override
  public byte[] generateManifestJSONFile(ContentAggregation contentAggregation) throws ManifestParserException
  {
    Validate.notNull(contentAggregation, "Content Aggregation cannot be null!");

    JSONObject data = new JSONObject();

    for (ContentAggregation.Organization chapters : contentAggregation.getOrganizations())
    {
      JSONArray pages = new JSONArray();

      chapters.getResources().forEach(page -> pages.put(chapters.getShortID() + PATH_DELIMITER + page.getAssetName()));

      data.put(chapters.getShortID(), pages);
    }

    JSONObject object = new JSONObject();
    object.put("data", data);
    return object.toString().getBytes();
  }
}
