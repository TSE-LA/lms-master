package mn.erin.lms.base.domain.usecase.exam;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.enrollment.ExamEnrollment;
import mn.erin.lms.base.domain.model.exam.Exam;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;

/**
 * @author Oyungerel Chuluunsukh
 **/
@Authorized(users = { Author.class, Manager.class, Instructor.class, Supervisor.class })
public class AssignInstructor extends ExamUseCase<String,  List<ExamEnrollment>>
{
  private static final Logger LOG = LoggerFactory.getLogger(AssignInstructor.class);

  public AssignInstructor(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  protected  List<ExamEnrollment> executeImpl(String input) throws UseCaseException, LmsRepositoryException
  {
    List<Exam> existingExams = examRepository.getAllExam();
    List<ExamEnrollment> existingEnrollments = examEnrollmentRepository.getAllWriteByUserId(input);
    existingExams.forEach(exam -> {
      List<ExamEnrollment> contains = existingEnrollments.stream().filter(enrollment -> enrollment.getExamId().equals(exam.getId().getId()))
          .collect(Collectors.toList());
      if (contains.isEmpty())
      {
        ExamEnrollment examEnrollment = examEnrollmentRepository.createEnrollment(exam.getId().getId(), input, "w");
        LOG.info("Successfully enrolled as instructor [{}] for exam [{}]: \"{}\"", input, exam.getId().getId(), (null != examEnrollment));
      }
    });
    return examEnrollmentRepository.getAllWriteByUserId(input);
  }
}
