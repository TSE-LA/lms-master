/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.get_course_analytics;

import java.util.Date;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class Filter
{
  private final Date startDate;
  private final Date endDate;

  public Filter(Date startDate, Date endDate)
  {
    this.startDate = Validate.notNull(startDate, "Start date cannot be null!");
    this.endDate = Validate.notNull(endDate, "End date cannot be null!");
  }

  public Date getStartDate()
  {
    return startDate;
  }

  public Date getEndDate()
  {
    return endDate;
  }
}
