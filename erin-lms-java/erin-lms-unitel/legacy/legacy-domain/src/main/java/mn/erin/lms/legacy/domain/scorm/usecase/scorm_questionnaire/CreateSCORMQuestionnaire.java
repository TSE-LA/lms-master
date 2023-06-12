/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.usecase.scorm_questionnaire;

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
import mn.erin.lms.legacy.domain.scorm.usecase.scorm_test.SCORMQuestionInfo;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class CreateSCORMQuestionnaire implements UseCase<CreateSCORMQuestionnaireInput, OrganizationInfo>
{
  private final SCORMTestRepository scormTestRepository;
  private final SCORMWrapperRepository scormWrapperRepository;

  public CreateSCORMQuestionnaire(SCORMTestRepository scormTestRepository, SCORMWrapperRepository scormWrapperRepository)
  {
    this.scormTestRepository = Objects.requireNonNull(scormTestRepository, "SCORMTestRepository cannot be null!");
    this.scormWrapperRepository = Objects.requireNonNull(scormWrapperRepository, "SCORMWrapperRepository cannot be null!");
  }

  @Override
  public OrganizationInfo execute(CreateSCORMQuestionnaireInput input) throws UseCaseException
  {
    Validate.notNull(input, "Input cannot be null!");

    List<SCORMTest.Question> questions = getQuestions(input.getQuestions());
    SCORMWrapper questionnaireWrapper;
    try
    {

      questionnaireWrapper = scormWrapperRepository.getWrapper("Questionnaire");
    }

    catch (SCORMRepositoryException e)
    {
      throw new UseCaseException("The 'Questionnaire' wrapper was not found!");
    }

    if (questionnaireWrapper == null)
    {
      throw new UseCaseException("The 'Questionnaire' wrapper is null!");
    }
    try
    {
      SCORMTest scormQuestionnaire = scormTestRepository.createSCORMTest(input.getName(), questions, questionnaireWrapper, "questionnaire-data.json");
      return toResult(scormQuestionnaire, input.getName());
    }
    catch (SCORMRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private OrganizationInfo toResult(SCORMTest scormQuestionnaire, String name)
  {
    OrganizationInfo result = new OrganizationInfo(name);
    scormQuestionnaire.getTestAssets().forEach(assetId -> result.addResourceInfo(new ResourceInfo(assetId.getId())));
    return result;
  }

  private List<SCORMTest.Question> getQuestions(List<SCORMQuestionInfo> questionInfos)
  {
    List<SCORMTest.Question> questions = new ArrayList<>();
    for (SCORMQuestionInfo questionInfo : questionInfos)
    {
      SCORMTest.Question question = new SCORMTest.Question(questionInfo.getTitle());
      questionInfo.getAnswers().forEach(answerInfo -> {
        SCORMTest.Answer answer = new SCORMTest.Answer(answerInfo.getValue());
        question.addAnswer(answer);
      });
      questions.add(question);
    }
    return questions;
  }
}
