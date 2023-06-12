/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.factory;

import java.util.Set;

import mn.erin.lms.legacy.domain.scorm.model.DataModel;

/**
 * A factory that creates the object of a SCORM run-time data model from a string representation.
 *
 * @author Bat-Erdene Tsogoo.
 */
public interface DataModelFactory
{
  DataModel getInstance(String dataModel);

  Set<DataModel> getAllInstances();
}
