/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_test.create_course_test;

import java.util.Objects;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.assessment.Answer;
import mn.erin.lms.legacy.domain.lms.model.assessment.Question;
import mn.erin.lms.legacy.domain.lms.model.assessment.Test;
import mn.erin.lms.legacy.domain.lms.repository.CourseTestRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.usecase.course_test.AnswerInfo;
import mn.erin.lms.legacy.domain.lms.usecase.course_test.QuestionInfo;

import static mn.erin.lms.legacy.domain.lms.usecase.course_test.CourseTestUtil.createQuestionList;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class CreateCourseTest implements UseCase<CreateCourseTestInput, CreateCourseTestOutput>
{
  private CourseTestRepository courseTestRepository;

  public CreateCourseTest(CourseTestRepository courseTestRepository)
  {
    this.courseTestRepository = Objects.requireNonNull(courseTestRepository, "Course test repository cannot be null!");
  }

  @Override
  public CreateCourseTestOutput execute(CreateCourseTestInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException("Create test input cannot be null!");
    }

    Test test;
    try
    {
      if (input.getMaxAttempts() != null && input.getThresholdScore() != null)
      {
        test = courseTestRepository.create(createQuestionList(input.getQuestions()), input.getName(), input.isGraded(), input.getTimeLimit(),
            input.getDueDate(), input.getMaxAttempts(), input.getThresholdScore());
      }
      else
      {
        test = courseTestRepository.create(createQuestionList(input.getQuestions()), input.getName(), input.isGraded(), input.getTimeLimit(), input.getDueDate());
      }
    }
    catch (LMSRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
    return createOutput(test);
  }

  private CreateCourseTestOutput createOutput(Test test)
  {
    CreateCourseTestOutput output = new CreateCourseTestOutput(test.getId().getId(), test.getName(), test.isGraded(),
        test.getTimeLimit(), test.getDueDate());
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
