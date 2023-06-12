package mn.erin.lms.base.rest.api;

import java.util.NoSuchElementException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.domain.model.AssessmentConstants;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.assessment.CreateCourseAssessment;
import mn.erin.lms.base.domain.usecase.assessment.GetCourseAssessment;
import mn.erin.lms.base.domain.usecase.assessment.UpdateCourseAssessment;
import mn.erin.lms.base.domain.usecase.assessment.dto.AnswerInfo;
import mn.erin.lms.base.domain.usecase.assessment.dto.CourseAssessmentDto;
import mn.erin.lms.base.domain.usecase.assessment.dto.CreateCourseAssessmentInput;
import mn.erin.lms.base.domain.usecase.assessment.dto.CreateQuizInput;
import mn.erin.lms.base.domain.usecase.assessment.dto.QuestionInfo;
import mn.erin.lms.base.domain.usecase.assessment.dto.QuizDto;
import mn.erin.lms.base.domain.usecase.assessment.dto.UpdateCourseAssessmentInput;
import mn.erin.lms.base.domain.usecase.assessment.dto.UpdateQuizInput;
import mn.erin.lms.base.domain.usecase.assessment.quiz.CreateQuiz;
import mn.erin.lms.base.domain.usecase.assessment.quiz.GetQuiz;
import mn.erin.lms.base.domain.usecase.assessment.quiz.UpdateQuiz;
import mn.erin.lms.base.rest.model.RestAnswer;
import mn.erin.lms.base.rest.model.RestQuestion;
import mn.erin.lms.base.rest.model.RestQuiz;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Course assessment REST API")
@RequestMapping(value = "/lms/courses", name = "Provides LMS course assessment features")
@RestController
public class CourseAssessmentRestApi extends BaseLmsRestApi
{
  public CourseAssessmentRestApi(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @ApiOperation("Creates a new assessment for a course")
  @PostMapping("/{courseId}/assessment")
  public ResponseEntity<RestResult> create(@PathVariable String courseId,
      @RequestBody RestQuiz body)
  {
    CreateQuizInput createQuizInput = new CreateQuizInput(AssessmentConstants.DEFAULT_QUIZ_NAME, false, null, null);

    createQuizInput.setMaxAttempts(body.getMaxAttempts());
    createQuizInput.setThresholdScore(body.getThresholdScore());

    for (RestQuestion question : body.getQuestions())
    {
      createQuizInput.addQuestion(mapToQuestionInfo(question));
    }

    CreateQuiz createQuiz = new CreateQuiz(lmsRepositoryRegistry.getQuizRepository());
    CreateCourseAssessment createCourseAssessment = new CreateCourseAssessment(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      QuizDto quizDto = createQuiz.execute(createQuizInput);
      CreateCourseAssessmentInput input = new CreateCourseAssessmentInput(courseId);
      input.addQuiz(quizDto.getId());
      createCourseAssessment.execute(input);

      return RestResponse.success(quizDto);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Updates a assessment for a course")
  @PutMapping("/{courseId}/assessment")
  public ResponseEntity<RestResult> update(@PathVariable String courseId,
      @RequestBody RestQuiz body)
  {
    UpdateQuizInput updateQuizInput = new UpdateQuizInput(body.getQuizId());
    updateQuizInput.setMaxAttempts(body.getMaxAttempts());
    updateQuizInput.setThresholdScore(body.getThresholdScore());
    for(RestQuestion question : body.getQuestions())
    {
      updateQuizInput.addQuestionInfo(mapToQuestionInfo(question));
    }
    UpdateQuiz updateQuiz = new UpdateQuiz(lmsRepositoryRegistry);
    UpdateCourseAssessment updateCourseAssessment = new UpdateCourseAssessment(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
    QuizDto quizDto = updateQuiz.execute(updateQuizInput);
    UpdateCourseAssessmentInput input = new UpdateCourseAssessmentInput(courseId);
    input.addQuiz(quizDto.getId());
    updateCourseAssessment.execute(input);
    return RestResponse.success(quizDto);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Fetches a course assessment by ID")
  @GetMapping("/{courseId}/assessment")
  public ResponseEntity<RestResult> readById(@PathVariable String courseId)
  {
    GetCourseAssessment getCourseAssessment = new GetCourseAssessment(lmsRepositoryRegistry.getCourseAssessmentRepository());

    String quizId;
    try
    {
      CourseAssessmentDto output = getCourseAssessment.execute(courseId);
      quizId = output.getQuizIdList().iterator().next();
    }
    catch (UseCaseException | NoSuchElementException e)
    {
      return RestResponse.internalError(e.getMessage());
    }

    GetQuiz getQuiz = new GetQuiz(lmsRepositoryRegistry.getQuizRepository());
    try
    {
      QuizDto quizDto = getQuiz.execute(quizId);
      return RestResponse.success(quizDto);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  private QuestionInfo mapToQuestionInfo(RestQuestion restQuestion)
  {
    QuestionInfo questionInfo = new QuestionInfo(restQuestion.getQuestion(), restQuestion.getQuestionType(), restQuestion.isRequired());
    for (RestAnswer answer : restQuestion.getAnswers())
    {
      questionInfo.addAnswers(new AnswerInfo(answer.getValue(), answer.isCorrect(), 1.0));
    }
    return questionInfo;
  }
}
