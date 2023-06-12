/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.update_course;


import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.course.UserGroup;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.usecase.course.CourseUseCase;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseOutput;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class UpdateUserGroup extends CourseUseCase<UpdateUserGroupInput, GetCourseOutput>
{
  protected static final Logger LOGGER = LoggerFactory.getLogger(UpdateUserGroup.class);

  public UpdateUserGroup(CourseRepository courseRepository)
  {
    super(courseRepository);
  }

  @Override
  public GetCourseOutput execute(UpdateUserGroupInput input) throws UseCaseException
  {
    Validate.notNull(input, "UpdateUserGroupInput cannot be null!");
    try
    {
      CourseId courseId = new CourseId(input.getCourseId());
      UserGroup userGroup = new UserGroup(input.getUserGroups().getUsers(), input.getUserGroups().getGroups());
      return getCourseOutput(courseRepository.update(courseId, userGroup));
    }
    catch (LMSRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

