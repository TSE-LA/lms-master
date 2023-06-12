/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.scorm.model;

/**
 * SCORM data model constraints.
 * According to SCORM Run-Time specification some data models are read-only and some are write-only.
 * The comprehensive list of information on these data models can be found on:
 * https://scorm.com/scorm-explained/technical-scorm/run-time/run-time-reference/
 *
 * @author Bat-Erdene Tsogoo.
 */
public enum DataModelConstraint
{
  READ_ONLY, WRITE_ONLY, READ_WRITE
}
