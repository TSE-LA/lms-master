/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_questionnaire.get_questionnaire;

import java.util.Objects;
import java.util.stream.Collectors;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.assessment.Test;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseQuestionnaireRepository;
import mn.erin.lms.legacy.domain.lms.usecase.course_test.AnswerInfo;
import mn.erin.lms.legacy.domain.lms.usecase.course_test.QuestionInfo;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class GetQuestionnaire implements UseCase<GetQuestionnaireInput, GetQuestionnaireOutput>
{
  private CourseQuestionnaireRepository courseQuestionnaireRepository;

  public GetQuestionnaire(CourseQuestionnaireRepository courseQuestionnaireRepository)
  {
    this.courseQuestionnaireRepository = Objects.requireNonNull(courseQuestionnaireRepository, "Questionnaire repository cannot be null!");
  }

  @Override
  public GetQuestionnaireOutput execute(GetQuestionnaireInput input) throws UseCaseException
  {
    Test questionnaire = this.courseQuestionnaireRepository.get(new CourseId(input.getCourseId()));
    return toOutput(questionnaire);
  }

  private GetQuestionnaireOutput toOutput(Test questionnaire)
  {
    return new GetQuestionnaireOutput(
        questionnaire.getId().getId(), questionnaire.getQuestions().stream()
        .map(question -> {
          QuestionInfo questionInfo = new QuestionInfo(question.getTitle(), question.getType().name());
          question.getAnswers()
              .forEach(answerInfo -> questionInfo.addAnswers(
                  new AnswerInfo(answerInfo.getValue(), answerInfo.isCorrect(), answerInfo.getScore())));
          return questionInfo;
        })
        .collect(Collectors.toList()),
        questionnaire.getName()
    );
  }
}
