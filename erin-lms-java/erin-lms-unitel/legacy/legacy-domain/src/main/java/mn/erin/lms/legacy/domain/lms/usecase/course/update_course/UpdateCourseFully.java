/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.update_course;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategoryId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseDetail;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseNote;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseOutput;

/**
 * author Tamir Batmagnai.
 */
public class UpdateCourseFully extends UpdateCourseUseCase<UpdateCourseInput, GetCourseOutput>
{
  private static final String COURSE_DETAIL_NULL = "Course detail cannot be null!";

  public UpdateCourseFully(CourseRepository courseRepository)
  {
    super(courseRepository);
  }

  @Override
  public GetCourseOutput execute(UpdateCourseInput input) throws UseCaseException
  {
    Validate.notNull(input, "Input is required to update a course!");

    CourseId courseId = new CourseId(input.getCourseId());
    CourseCategoryId categoryId = new CourseCategoryId(input.getCategoryId());

    CourseDetail updatedDetail = new CourseDetail(input.getTitle());
    updatedDetail.setDescription(input.getDescription());
    updatedDetail.setModifiedDate(new Date());

    CourseDetail existCourseDetail = getCourseDetail(input.getCourseId());

    if (null == existCourseDetail)
    {
      throw new UseCaseException(COURSE_DETAIL_NULL);
    }

    updatedDetail.changePublishStatus(existCourseDetail.getPublishStatus());
    updatedDetail.setCreatedDate(existCourseDetail.getCreatedDate());
    List<CourseNote> courseNotes = existCourseDetail.getNotes();
    courseNotes.add(new CourseNote(LocalDate.now(), input.getNote()));
    updatedDetail.setNotes(courseNotes);

    for (Map.Entry<String, Object> property : input.getProperties().entrySet())
    {
      updatedDetail.addProperty(property.getKey(), (Serializable) property.getValue());
    }
    return updateCourseFully(courseId, categoryId, updatedDetail);
  }

  private CourseDetail getCourseDetail(String courseId) throws UseCaseException
  {
    CourseDetail existCourseDetail = null;
    try
    {
      Course course = courseRepository.getCourse(new CourseId(courseId));
      if (null != course)
      {
        existCourseDetail = course.getCourseDetail();
      }
    }
    catch (LMSRepositoryException e)
    {
      LOGGER.error(e.getMessage());
      throw new UseCaseException("Failed to get course while updating!");
    }
    return existCourseDetail;
  }
}
