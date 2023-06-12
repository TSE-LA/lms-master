package mn.erin.lms.base.domain.usecase.exam;

import java.util.List;
import java.util.stream.Collectors;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.enrollment.ExamEnrollment;
import mn.erin.lms.base.domain.model.exam.Exam;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.exam.dto.LeanerExamListDto;

/**
 * @author Galsan Bayart.
 */
@Authorized(users = { LmsUser.class })

public class GetLearnerExamList extends ExamUseCase<Void, List<LeanerExamListDto>>
{

  public GetLearnerExamList(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  protected List<LeanerExamListDto> executeImpl(Void input) throws UseCaseException, LmsRepositoryException
  {
    String learnerId = lmsServiceRegistry.getAccessIdentityManagement().getCurrentUsername();
    List<ExamEnrollment> examEnrollments = examEnrollmentRepository.getAllReadByUserId(learnerId);

    List<Exam> exams = examRepository.getAllByIds(examEnrollments.stream().map(ExamEnrollment::getExamId).collect(Collectors.toSet()));

    return exams.stream().map(this::toLeanerExamListDto).collect(Collectors.toList());
  }
}
