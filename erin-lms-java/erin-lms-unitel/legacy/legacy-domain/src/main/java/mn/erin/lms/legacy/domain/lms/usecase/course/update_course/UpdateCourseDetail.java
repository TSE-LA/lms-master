/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.update_course;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.course.CourseDetail;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseNote;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseOutput;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class UpdateCourseDetail extends UpdateCourseUseCase<UpdateCourseDetailInput, GetCourseOutput>
{
  public UpdateCourseDetail(CourseRepository courseRepository)
  {
    super(courseRepository);
  }

  @Override
  public GetCourseOutput execute(UpdateCourseDetailInput input) throws UseCaseException
  {
    Validate.notNull(input, "Input is required to update a course!");
    CourseDetail courseDetail = new CourseDetail(input.getTitle());
    courseDetail.setDescription(input.getDescription());
    courseDetail.addNote(new CourseNote(LocalDateTime.now().toLocalDate(), input.getNote()));

    for (Map.Entry<String, Object> property : input.getProperties().entrySet())
    {
      courseDetail.addProperty(property.getKey(), (Serializable) property.getValue());
    }
    CourseId courseId = new CourseId(input.getCourseId());
    if (userGroupsInput(input) != null)
    {
      UpdateUserGroup updateUserGroup = new UpdateUserGroup(courseRepository);
      updateUserGroup.execute(new UpdateUserGroupInput(courseId.getId(), userGroupsInput(input)));
    }
    return updateCourse(courseId, courseDetail);
  }

  private UserGroupsInput userGroupsInput(UpdateCourseDetailInput input)
  {
    return input.getUserGroupsInput();
  }
}
