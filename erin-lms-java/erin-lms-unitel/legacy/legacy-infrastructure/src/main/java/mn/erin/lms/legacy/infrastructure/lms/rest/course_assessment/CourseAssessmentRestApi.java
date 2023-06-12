/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.rest.course_assessment;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.lms.legacy.domain.lms.repository.CourseAssessmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseTestRepository;
import mn.erin.lms.legacy.domain.lms.usecase.course_assessment.create_assessment.CreateAssessment;
import mn.erin.lms.legacy.domain.lms.usecase.course_assessment.create_assessment.CreateAssessmentInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_assessment.get_assessment.GetAssessment;
import mn.erin.lms.legacy.domain.lms.usecase.course_assessment.get_assessment.GetAssessmentInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_assessment.get_assessment.GetAssessmentOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course_test.AnswerInfo;
import mn.erin.lms.legacy.domain.lms.usecase.course_test.QuestionInfo;
import mn.erin.lms.legacy.domain.lms.usecase.course_test.create_course_test.CreateCourseTest;
import mn.erin.lms.legacy.domain.lms.usecase.course_test.create_course_test.CreateCourseTestInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_test.create_course_test.CreateCourseTestOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course_test.get_course_test.GetCourseTest;
import mn.erin.lms.legacy.domain.lms.usecase.course_test.get_course_test.GetCourseTestInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_test.get_course_test.GetCourseTestOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course_test.update_course_test.UpdateCourseTest;
import mn.erin.lms.legacy.domain.lms.usecase.course_test.update_course_test.UpdateCourseTestInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_test.update_course_test.UpdateCourseTestOutput;
import mn.erin.lms.legacy.infrastructure.lms.rest.course_assessment.rest_models.RestAnswer;
import mn.erin.lms.legacy.infrastructure.lms.rest.course_assessment.rest_models.RestCourseAssessment;
import mn.erin.lms.legacy.infrastructure.lms.rest.course_assessment.rest_models.RestCourseTest;

/**
 * @author Oyungerel Chuluunsukh.
 */
@Api("Course")
@RequestMapping(value = "/legacy/course-assessments", name = "Provides 'ERIN' LMS course assessments")
public class CourseAssessmentRestApi
{
  private static final Double SCORE = 1.0;
  private static final Logger LOGGER = LoggerFactory.getLogger(CourseAssessmentRestApi.class);
  private CourseAssessmentRepository courseAssessmentRepository;
  private CourseRepository courseRepository;
  private CourseTestRepository courseTestRepository;

  public CourseAssessmentRestApi(CourseAssessmentRepository courseAssessmentRepository, CourseTestRepository courseTestRepository,
      CourseRepository courseRepository)
  {
    this.courseAssessmentRepository = Objects.requireNonNull(courseAssessmentRepository, "Course assessment repo cannot be null!");
    this.courseTestRepository = Objects.requireNonNull(courseTestRepository, "Course test repo cannot be null!");
    this.courseRepository = Objects.requireNonNull(courseRepository, "Course repository cannot be null!");
  }

  @ApiOperation("Create assessment for course")
  @PostMapping()
  public ResponseEntity create(@RequestBody RestCourseAssessment request)
  {
    String courseId = request.getCourseId();
    if (courseId == null)
    {
      return RestResponse.badRequest("Course id cannot be null!");
    }

    CreateCourseTestInput createCourseTestInput = new CreateCourseTestInput("ТЕСТ", false, null, null);

    Optional<Integer> maxAttempts = Optional.ofNullable(request.getMaxAttempts());
    Optional<Double> threshold = Optional.ofNullable(request.getThresholdScore());

    maxAttempts.ifPresent(createCourseTestInput::setMaxAttempts);
    threshold.ifPresent(createCourseTestInput::setThresholdScore);

    for (RestCourseTest restCourseTest : request.getCourseTests())
    {
      createCourseTestInput.addQuestions(mapToInfoModel(restCourseTest));
    }

    CreateCourseTest createCourseTest = new CreateCourseTest(courseTestRepository);
    CreateCourseTestOutput createCourseTestOutput = null;
    try
    {
      createCourseTestOutput = createCourseTest.execute(createCourseTestInput);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError("Failed to create course test cause: " + e.getMessage());
    }

    CreateAssessment createAssessment = new CreateAssessment(courseAssessmentRepository, courseRepository);
    CreateAssessmentInput createAssessmentInput = new CreateAssessmentInput(courseId);
    if (createCourseTestOutput == null)
    {
      return RestResponse.internalError("Could not create id for course test!");
    }
    createAssessmentInput.addTestIds(createCourseTestOutput.getId());
    try
    {
      createAssessment.execute(createAssessmentInput);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError("Failed to create assessment cause: " + e.getMessage());
    }

    return RestResponse.success(createCourseTestOutput);
  }

  @ApiOperation("Update test for course")
  @PutMapping()
  public ResponseEntity update(@RequestBody RestCourseAssessment request)
  {
    if (request == null)
    {
      return RestResponse.badRequest("Request cannot be null!");
    }
    UpdateCourseTest updateCourseTest = new UpdateCourseTest(courseTestRepository);
    UpdateCourseTestInput input = new UpdateCourseTestInput(request.getTestId());

    Optional<Integer> maxAttempts = Optional.ofNullable(request.getMaxAttempts());
    Optional<Double> threshold = Optional.ofNullable(request.getThresholdScore());

    maxAttempts.ifPresent(input::setMaxAttempts);
    threshold.ifPresent(input::setThresholdScore);

    for (RestCourseTest restCourseTest : request.getCourseTests())
    {
      input.addQuestionInfo(mapToInfoModel(restCourseTest));
    }
    UpdateCourseTestOutput output = null;
    try
    {
      output = updateCourseTest.execute(input);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError("Failed to update test cause: " + e.getMessage());
    }
    return RestResponse.success(output);
  }

  private QuestionInfo mapToInfoModel(RestCourseTest restCourseTest)
  {
    QuestionInfo questionInfo = new QuestionInfo(restCourseTest.getQuestion(), "SINGLE_CHOICE");
    for (RestAnswer answer : restCourseTest.getAnswers())
    {
      questionInfo.addAnswers(new AnswerInfo(answer.getValue(), answer.isCorrect(), SCORE));
    }
    return questionInfo;
  }

  @ApiOperation("Get assessment test for course")
  @GetMapping("/{courseId}")
  public ResponseEntity read(@PathVariable String courseId)
  {
    GetAssessment getAssessment = new GetAssessment(courseAssessmentRepository, courseRepository);
    GetAssessmentOutput getAssessmentOutput = null;
    try
    {
      getAssessmentOutput = getAssessment.execute(new GetAssessmentInput(courseId));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError("Could not get course assessment cause: " + e.getMessage());
    }

    if (getAssessmentOutput == null)
    {
      return RestResponse.internalError("Test assessment is null for course ID:[" + courseId + "]");
    }
    List<String> testIds = getAssessmentOutput.getCourseTestIds();
    GetCourseTestOutput output = null;
    GetCourseTest getCourseTest = new GetCourseTest(courseTestRepository);
    if (!testIds.isEmpty())
    {

      try
      {
        output = getCourseTest.execute(new GetCourseTestInput(testIds.get(0)));
      }
      catch (UseCaseException e)
      {
        return RestResponse.internalError("Could not get course tests cause: " + e.getMessage());
      }
    }
    else
    {
      return RestResponse.internalError("Could not get course tests no course tests found");
    }
    return RestResponse.success(output);
  }
}
