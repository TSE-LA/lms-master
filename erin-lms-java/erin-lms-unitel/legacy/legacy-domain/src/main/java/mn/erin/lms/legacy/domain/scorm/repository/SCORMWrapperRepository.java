/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.repository;

import mn.erin.lms.legacy.domain.scorm.model.SCORMWrapper;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface SCORMWrapperRepository
{
  /**
   * Gets the default SCORM wrapper.
   *
   * @return The default SCORM wrapper
   * @throws SCORMRepositoryException If there is no default wrapper in the repository
   */
  SCORMWrapper getDefaultWrapper() throws SCORMRepositoryException;

  /**
   * Gets a SCORM wrapper by type
   *
   * @param type Wrapper type
   * @return The wrapper with the specified type
   * @throws SCORMRepositoryException If the SCORM wrapper with the type does not exist.
   */
  SCORMWrapper getWrapper(String type) throws SCORMRepositoryException;
}
