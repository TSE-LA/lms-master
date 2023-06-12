/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_test.update_course_test;

import java.util.Objects;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.assessment.Answer;
import mn.erin.lms.legacy.domain.lms.model.assessment.Question;
import mn.erin.lms.legacy.domain.lms.model.assessment.Test;
import mn.erin.lms.legacy.domain.lms.model.assessment.TestId;
import mn.erin.lms.legacy.domain.lms.repository.CourseTestRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.usecase.course_test.AnswerInfo;
import mn.erin.lms.legacy.domain.lms.usecase.course_test.QuestionInfo;

import static mn.erin.lms.legacy.domain.lms.usecase.course_test.CourseTestUtil.createQuestionList;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class UpdateCourseTest implements UseCase<UpdateCourseTestInput, UpdateCourseTestOutput>
{
  private CourseTestRepository courseTestRepository;

  public UpdateCourseTest(CourseTestRepository courseTestRepository)
  {
    this.courseTestRepository = Objects.requireNonNull(courseTestRepository, "Course test repository cannot be null!");
  }

  @Override
  public UpdateCourseTestOutput execute(UpdateCourseTestInput input) throws UseCaseException
  {
    if(input == null){
      throw new UseCaseException("Update course test cannot be null!");
    }
    Test test;
    try
    {
      TestId testId = new TestId(input.getCourseTestId());
      if (input.getMaxAttempts() != null && input.getThresholdScore() != null)
      {
        test = courseTestRepository.update(testId, createQuestionList(input.getQuestionInfos()), input.getMaxAttempts(), input.getThresholdScore());
      }
      else
      {
        test = courseTestRepository.update(testId, createQuestionList(input.getQuestionInfos()));
      }
    }
    catch (LMSRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
    return createOutput(test);
  }

  private UpdateCourseTestOutput createOutput(Test test)
  {
    UpdateCourseTestOutput output = new UpdateCourseTestOutput(test.getId().getId(), test.getName(), test.isGraded(), test.getTimeLimit(),
        test.getDueDate());
    output.setMaxAttempts(test.getMaxAttempts());
    output.setThresholdScore(test.getThresholdScore());
    for (Question question : test.getQuestions())
    {
      QuestionInfo questionInfo = new QuestionInfo(question.getTitle(), question.getType().name());
      for (Answer answer : question.getAnswers())
      {
        questionInfo.addAnswers(new AnswerInfo(answer.getValue(), answer.isCorrect(), answer.getScore()));
      }
      output.addQuestions(questionInfo);
    }
    return output;
  }


}
