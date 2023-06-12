package mn.erin.lms.base.domain.usecase.exam;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.exam.ExamId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;

/**
 * @author Temuulen Naranbold
 */
@Authorized(users = {Author.class, Manager.class, Instructor.class, Supervisor.class})
public class DeleteExam extends ExamUseCase<String, Boolean> {
    public DeleteExam(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry) {
        super(lmsRepositoryRegistry, lmsServiceRegistry);
    }

    @Override
    protected Boolean executeImpl(String input) throws UseCaseException {
        Validate.notBlank(input);
        try {
            ExamId examId = ExamId.valueOf(input);
            if (this.examRepository.exists(examId)) {
                //TODO: delete certificates, archived questions
                boolean enrollmentsDeleted = examEnrollmentRepository.deleteAllByExamId(examId);
                boolean examDeleted = examRepository.delete(input);
                lmsServiceRegistry.getExamScheduledTaskRemover().deleteScheduledTasks(examId.getId());
                return enrollmentsDeleted && examDeleted;
            } else {
                throw new UseCaseException("Exam already deleted!");
            }
        } catch (LmsRepositoryException e) {
            throw new UseCaseException(e.getMessage(), e);
        }
    }
}
