/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.usecase.scorm_test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.scorm.model.SCORMTest;
import mn.erin.lms.legacy.domain.scorm.model.SCORMWrapper;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMRepositoryException;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMTestRepository;
import mn.erin.lms.legacy.domain.scorm.repository.SCORMWrapperRepository;
import mn.erin.lms.legacy.domain.scorm.usecase.scorm.OrganizationInfo;
import mn.erin.lms.legacy.domain.scorm.usecase.scorm.ResourceInfo;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreateSCORMTest implements UseCase<CreateSCORMTestInput, OrganizationInfo>
{
  private final SCORMTestRepository scormTestRepository;
  private final SCORMWrapperRepository scormWrapperRepository;

  public CreateSCORMTest(SCORMTestRepository scormTestRepository, SCORMWrapperRepository scormWrapperRepository)
  {
    this.scormTestRepository = Objects.requireNonNull(scormTestRepository, "SCORMTestRepository cannot be null!");
    this.scormWrapperRepository = Objects.requireNonNull(scormWrapperRepository, "SCORMWrapperRepository cannot be null!");
  }

  @Override
  public OrganizationInfo execute(CreateSCORMTestInput input) throws UseCaseException
  {
    Validate.notNull(input, "Input cannot be null!");

    List<SCORMTest.Question> questions = getQuestions(input.getQuestions());

    SCORMWrapper testWrapper;
    try
    {
      testWrapper = scormWrapperRepository.getWrapper("Test");
    }
    catch (SCORMRepositoryException e)
    {
      throw new UseCaseException("The 'Test' wrapper was not found!");
    }

    try
    {
      SCORMTest scormTest;

      if (input.getMaxAttemps() != null && input.getThresholdScore() != null)
      {
        scormTest = scormTestRepository.createSCORMTest(input.getName(), questions, testWrapper, "test-data.json", input.getMaxAttemps(),
            input.getThresholdScore());
      }
      else
      {
        scormTest = scormTestRepository.createSCORMTest(input.getName(), questions, testWrapper, "test-data.json");
      }
      return toResult(scormTest);
    }
    catch (SCORMRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private List<SCORMTest.Question> getQuestions(List<SCORMQuestionInfo> scormQuestionInfos)
  {
    List<SCORMTest.Question> questions = new ArrayList<>();

    for (SCORMQuestionInfo SCORMQuestionInfo : scormQuestionInfos)
    {
      List<AnswerInfo> answerInfos = SCORMQuestionInfo.getAnswers();
      SCORMTest.Question question = new SCORMTest.Question(SCORMQuestionInfo.getTitle());
      answerInfos.forEach(answerInfo -> {
        SCORMTest.Answer answer = new SCORMTest.Answer(answerInfo.getValue(), answerInfo.isCorrect(), answerInfo.getScore());
        question.addAnswer(answer);
      });
      questions.add(question);
    }

    return questions;
  }

  private OrganizationInfo toResult(SCORMTest scormTest)
  {
    OrganizationInfo result = new OrganizationInfo(scormTest.getName());
    scormTest.getTestAssets().forEach(assetId -> result.addResourceInfo(new ResourceInfo(assetId.getId())));
    return result;
  }
}
