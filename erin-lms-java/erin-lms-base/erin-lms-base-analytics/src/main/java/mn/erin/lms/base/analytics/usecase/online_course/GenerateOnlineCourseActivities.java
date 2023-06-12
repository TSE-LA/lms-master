/*
 * (C)opyright, 2021, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.analytics.usecase.online_course;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.analytics.model.Analytic;
import mn.erin.lms.base.analytics.repository.AnalyticsRepositoryRegistry;
import mn.erin.lms.base.analytics.usecase.AnalyticsUseCase;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.service.CourseTypeResolver;

/**
 * @author Munkh
 */
public class GenerateOnlineCourseActivities extends AnalyticsUseCase<String, Analytic>
{
  public GenerateOnlineCourseActivities(AnalyticsRepositoryRegistry analyticsRepositoryRegistry,
      CourseTypeResolver courseTypeResolver)
  {
    super(analyticsRepositoryRegistry, courseTypeResolver);
  }

  @Override
  public Analytic execute(String input) throws UseCaseException
  {
    Validate.notBlank(input);

    return analyticsRepositoryRegistry.getLearnerActivityRepository().getAll(GroupId.valueOf(input), CourseCategoryId.valueOf("online-course"));
  }
}
