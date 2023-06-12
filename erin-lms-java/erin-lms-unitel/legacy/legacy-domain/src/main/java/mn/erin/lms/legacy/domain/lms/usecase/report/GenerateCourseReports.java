/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategory;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategoryId;
import mn.erin.lms.legacy.domain.lms.model.report.CourseReport;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.service.CourseReportService;
import mn.erin.lms.legacy.domain.lms.usecase.course.CourseUseCase;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseOutput;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GenerateCourseReports implements UseCase<ReportFilter, List<CourseReportResult>>
{
  private final CourseReportService courseReportService;
  private final CourseCategoryRepository courseCategoryRepository;

  public GenerateCourseReports(CourseReportService courseReportService, CourseCategoryRepository courseCategoryRepository)
  {
    this.courseReportService = Objects.requireNonNull(courseReportService, "CourseReportService cannot be null!");
    this.courseCategoryRepository = Objects.requireNonNull(courseCategoryRepository, "CourseCategoryRepository cannot be null!");
  }

  @Override
  public List<CourseReportResult> execute(ReportFilter filter) throws UseCaseException
  {
    Validate.notNull(filter, "Input is required!");

    List<CourseReport> courseReports;

    if (!StringUtils.isBlank(filter.getCategory()) && filter.getCoursePropertyFilter() != null)
    {
      courseReports = courseReportService.generateCourseReports(filter.getGroupId(), filter.getCategory(), filter.getCoursePropertyFilter());
    }
    else if (StringUtils.isBlank(filter.getCategory()) && filter.getCoursePropertyFilter() != null)
    {
      courseReports = courseReportService.generateCourseReports(filter.getGroupId(), filter.getCoursePropertyFilter());
    }
    else
    {
      courseReports = courseReportService.generateCourseReports(filter.getGroupId());
    }
    List<CourseReportResult> result = new ArrayList<>();

    for (CourseReport courseReport : courseReports)
    {
      GetCourseOutput courseOutput = CourseUseCase.getCourseOutput(courseReport.getRootEntity());

      CourseCategoryId courseCategoryId = new CourseCategoryId(courseOutput.getCourseCategory());
      CourseCategory courseCategory;

      String courseTitle = courseOutput.getTitle();

      try
      {
        courseCategory = courseCategoryRepository.getCourseCategory(courseCategoryId);
      }
      catch (LMSRepositoryException e)
      {
        throw new UseCaseException("Failed to get the category information of the course [" + courseTitle + "]");
      }

      List<String> enrolledLearners = courseReport.getEnrolledLearners().stream()
          .map(EntityId::getId)
          .collect(Collectors.toList());
      result.add(new CourseReportResult.Builder(courseTitle)
          .withId(courseOutput.getId())
          .withAuthor(courseOutput.getAuthorId())
          .withCategory(courseCategory.getName())
          .createdAt(courseOutput.getModifiedDate())
          .havingCourseProperties(courseOutput.getProperties())
          .havingReportData(courseReport.getReportData())
          .withEnrolledLearners(enrolledLearners)
          .build());
    }
    return result;
  }
}
