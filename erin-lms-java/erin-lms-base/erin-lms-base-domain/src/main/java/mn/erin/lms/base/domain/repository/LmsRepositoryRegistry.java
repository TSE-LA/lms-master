package mn.erin.lms.base.domain.repository;

import mn.erin.lms.base.domain.repository.task.ScheduledTaskRepository;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface LmsRepositoryRegistry
{
  CourseRepository getCourseRepository();

  CourseCategoryRepository getCourseCategoryRepository();

  CourseContentRepository getCourseContentRepository();

  CourseAssessmentRepository getCourseAssessmentRepository();

  CourseEnrollmentRepository getCourseEnrollmentRepository();

  QuizRepository getQuizRepository();

  AssessmentRepository getAssessmentRepository();

  CertificateRepository getCertificateRepository();

  LearnerCertificateRepository getLearnerCertificateRepository();

  CourseSuggestedUsersRepository getCourseSuggestedUsersRepository();

  ClassroomCourseAttendanceRepository getClassroomAttendanceRepository();

  ExamRepository getExamRepository();

  ExamGroupRepository getExamGroupRepository();

  ExamEnrollmentRepository getExamEnrollmentRepository();

  ExamCategoryRepository getExamCategoryRepository();

  QuestionCategoryRepository getQuestionCategoryRepository();

  QuestionGroupRepository getQuestionGroupRepository();

  QuestionRepository getQuestionRepository();

  QuestionStateRepository getQuestionStateRepository();

  ExamRuntimeDataRepository getExamRuntimeDataRepository();

  SystemConfigRepository getSystemConfigRepository();

  ScheduledTaskRepository getScheduledTaskRepository();

  LearnerCourseHistoryRepository getLearnerCourseHistoryRepository();

  AnnouncementRepository getAnnouncementRepository();

  AnnouncementRuntimeRepository getAnnouncementRuntimeRepository();
}
