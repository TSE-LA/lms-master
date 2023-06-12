package mn.erin.lms.base.domain.repository;

import java.util.List;
import java.util.Set;

import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.enrollment.ExamEnrollment;
import mn.erin.lms.base.domain.model.exam.ExamId;

/**
 * @author Galsan Bayart.
 */
public interface ExamEnrollmentRepository {

  ExamEnrollment createEnrollment(String examId, String learnerId, String permission);

  ExamEnrollment update(ExamEnrollment examEnrollment);

  List<ExamEnrollment> getAll();

  ExamEnrollment getByExamIdAndLearnerId(String examId, String learnerId);

  List<ExamEnrollment> getByLearnerId(LearnerId learnerId);

  boolean deleteAllByExamId(ExamId examId);

  boolean deleteByExamIdAndLearnerId(ExamId examId, LearnerId learnerId);

  List<ExamEnrollment> getAllReadByUserId(String userId);

  List<ExamEnrollment> getAllWriteByUserId(String userId);

  Set<String> getAllReadLearnerByExamId(String examId);

}
