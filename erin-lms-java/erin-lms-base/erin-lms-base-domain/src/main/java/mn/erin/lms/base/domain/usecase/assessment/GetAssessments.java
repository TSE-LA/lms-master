package mn.erin.lms.base.domain.usecase.assessment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.assessment.Assessment;
import mn.erin.lms.base.domain.model.assessment.AssessmentStatus;
import mn.erin.lms.base.domain.model.assessment.Quiz;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.repository.QuizRepository;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.assessment.dto.AssessmentDto;
import mn.erin.lms.base.domain.usecase.assessment.dto.GetAssessmentsInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Instructor.class })
public class GetAssessments extends AssessmentUseCase<GetAssessmentsInput, List<AssessmentDto>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetAssessments.class);

  private final QuizRepository quizRepository;
  public GetAssessments(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.quizRepository = lmsRepositoryRegistry.getQuizRepository();
  }

  @Override
  protected List<AssessmentDto> executeImpl(GetAssessmentsInput input)
  {
    List<Assessment> assessments;
    if (input.getStartDate() == null || input.getEndDate() == null)
    {
      assessments = assessmentRepository.listAll();
    }
    else
    {
      assessments = assessmentRepository.listAll(input.getStartDate(), input.getEndDate());
    }

    if(input.getOnlyActive()){
      assessments = assessments.stream().filter(assessment -> assessment.getStatus().equals(AssessmentStatus.ACTIVE)).collect(Collectors.toList());
    }

    List<AssessmentDto> result = new ArrayList<>();

    for (Assessment assessment : assessments)
    {
      try
      {
        Quiz quiz = quizRepository.fetchById(assessment.getQuizId());
        result.add(convert(assessment, quiz));
      }
      catch (LmsRepositoryException e)
      {
        LOGGER.error(e.getMessage(), e);
      }
    }

    return result;
  }
}
