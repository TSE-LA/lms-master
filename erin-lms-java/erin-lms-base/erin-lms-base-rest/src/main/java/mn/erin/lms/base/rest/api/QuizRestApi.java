package mn.erin.lms.base.rest.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.domain.model.assessment.AssessmentId;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.assessment.dto.AnswerInfo;
import mn.erin.lms.base.domain.usecase.assessment.dto.QuestionInfo;
import mn.erin.lms.base.domain.usecase.assessment.dto.QuizDto;
import mn.erin.lms.base.domain.usecase.assessment.dto.UpdateQuizInput;
import mn.erin.lms.base.domain.usecase.assessment.quiz.GetQuiz;
import mn.erin.lms.base.domain.usecase.assessment.quiz.UpdateQuiz;
import mn.erin.lms.base.rest.model.RestAnswer;
import mn.erin.lms.base.rest.model.RestQuestion;
import mn.erin.lms.base.rest.model.RestQuiz;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Quiz REST API")
@RequestMapping(value = "/lms/quizzes", name = "Provies LMS quiz features")
@RestController
public class QuizRestApi extends BaseLmsRestApi
{
  public QuizRestApi(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }


  @ApiOperation("Updates a quiz")
  @PutMapping("/{quizId}")
  public ResponseEntity<RestResult> update(@PathVariable String quizId,
      @RequestBody RestQuiz body)
  {
    UpdateQuiz updateQuiz = new UpdateQuiz(lmsRepositoryRegistry);
    UpdateQuizInput input = new UpdateQuizInput(quizId);

    input.setMaxAttempts(body.getMaxAttempts());
    input.setThresholdScore(body.getThresholdScore());

    for (RestQuestion question : body.getQuestions())
    {
      input.addQuestionInfo(mapToQuestionInfo(question));
      input.setAssessmentId(AssessmentId.valueOf(question.getAssessmentId()));
    }

    try
    {
      QuizDto quizDto = updateQuiz.execute(input);
      return RestResponse.success(quizDto);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Fetches a quiz by ID")
  @GetMapping("/{quizId}")
  public ResponseEntity<RestResult> readById(@PathVariable String quizId)
  {
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
    String questionType = StringUtils.isBlank(restQuestion.getQuestionType()) ? "SINGLE_CHOICE" : restQuestion.getQuestionType().toUpperCase();
    QuestionInfo questionInfo = new QuestionInfo(restQuestion.getQuestion(), questionType, restQuestion.isRequired() );
    for (RestAnswer answer : restQuestion.getAnswers())
    {
      questionInfo.addAnswers(new AnswerInfo(answer.getValue(), answer.isCorrect(), 1.0));
    }
    return questionInfo;
  }
}
