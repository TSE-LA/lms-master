package mn.erin.lms.jarvis.server.registry;

import javax.inject.Inject;

import mn.erin.lms.base.domain.repository.ClassroomCourseAttendanceRepository;
import mn.erin.lms.base.domain.repository.ExamCategoryRepository;
import mn.erin.lms.base.domain.repository.ExamEnrollmentRepository;
import mn.erin.lms.base.domain.repository.ExamRepository;
import mn.erin.lms.base.domain.repository.LearnerCourseHistoryRepository;
import mn.erin.lms.base.domain.repository.SystemConfigRepository;
import mn.erin.lms.base.domain.repository.QuestionCategoryRepository;
import mn.erin.lms.base.domain.repository.QuestionGroupRepository;
import mn.erin.lms.base.domain.repository.QuestionRepository;
import mn.erin.lms.base.domain.repository.QuestionStateRepository;
import mn.erin.lms.jarvis.domain.report.repository.AssessmentReportRepository;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import mn.erin.lms.base.domain.repository.AnnouncementRepository;
import mn.erin.lms.base.domain.repository.AnnouncementRuntimeRepository;
import mn.erin.lms.base.domain.repository.AssessmentRepository;
import mn.erin.lms.base.domain.repository.CertificateRepository;
import mn.erin.lms.base.domain.repository.CourseAssessmentRepository;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.repository.CourseContentRepository;
import mn.erin.lms.base.domain.repository.CourseEnrollmentRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.CourseSuggestedUsersRepository;
import mn.erin.lms.base.domain.repository.ExamGroupRepository;
import mn.erin.lms.base.domain.repository.ExamRuntimeDataRepository;
import mn.erin.lms.base.domain.repository.LearnerCertificateRepository;
import mn.erin.lms.base.domain.repository.QuizRepository;
import mn.erin.lms.base.domain.repository.task.ScheduledTaskRepository;
import mn.erin.lms.jarvis.domain.report.repository.CourseReportRepository;
import mn.erin.lms.jarvis.domain.report.repository.FieldRepository;
import mn.erin.lms.jarvis.domain.report.repository.LearnerSuccessRepository;
import mn.erin.lms.jarvis.domain.repository.JarvisLmsRepositoryRegistry;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class JarvisLmsRepositoryRegistryImpl implements JarvisLmsRepositoryRegistry, BeanFactoryAware
{
  private CourseRepository courseRepository;
  private CourseCategoryRepository courseCategoryRepository;
  private CourseContentRepository courseContentRepository;
  private CourseAssessmentRepository courseAssessmentRepository;
  private CourseEnrollmentRepository courseEnrollmentRepository;
  private QuizRepository quizRepository;
  private CourseReportRepository courseReportRepository;
  private AssessmentRepository assessmentRepository;
  private CertificateRepository certificateRepository;
  private LearnerCertificateRepository learnerCertificateRepository;
  private AssessmentReportRepository assessmentReportRepository;
  private CourseSuggestedUsersRepository courseSuggestedUsersRepository;
  private ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository;
  private LearnerSuccessRepository learnerSuccessRepository;
  private ExamRepository examRepository;
  private ExamGroupRepository examGroupRepository;
  private ExamEnrollmentRepository examEnrollmentRepository;
  private ExamCategoryRepository examCategoryRepository;
  private QuestionCategoryRepository questionCategoryRepository;
  private QuestionGroupRepository questionGroupRepository;
  private QuestionRepository questionRepository;
  private QuestionStateRepository questionStateRepository;
  private ExamRuntimeDataRepository examRuntimeDataRepository;
  private SystemConfigRepository systemConfigRepository;
  private FieldRepository fieldRepository;
  private LearnerCourseHistoryRepository learnerCourseHistoryRepository;
  private AnnouncementRepository announcementRepository;
  private AnnouncementRuntimeRepository announcementRuntimeRepository;

  private BeanFactory beanFactory;

  @Inject
  public void setCourseRepository(CourseRepository courseRepository)
  {
    this.courseRepository = courseRepository;
  }

  @Inject
  public void setCourseCategoryRepository(CourseCategoryRepository courseCategoryRepository)
  {
    this.courseCategoryRepository = courseCategoryRepository;
  }

  @Inject
  public void setCourseContentRepository(CourseContentRepository courseContentRepository)
  {
    this.courseContentRepository = courseContentRepository;
  }

  @Inject
  public void setCourseAssessmentRepository(CourseAssessmentRepository courseAssessmentRepository)
  {
    this.courseAssessmentRepository = courseAssessmentRepository;
  }

  @Inject
  public void setCourseEnrollmentRepository(CourseEnrollmentRepository courseEnrollmentRepository)
  {
    this.courseEnrollmentRepository = courseEnrollmentRepository;
  }

  @Inject
  public void setQuizRepository(QuizRepository quizRepository)
  {
    this.quizRepository = quizRepository;
  }

  @Inject
  public void setCourseReportRepository(CourseReportRepository courseReportRepository)
  {
    this.courseReportRepository = courseReportRepository;
  }

  @Inject
  public void setAssessmentRepository(AssessmentRepository assessmentRepository)
  {
    this.assessmentRepository = assessmentRepository;
  }

  @Inject
  public void setCertificateRepository(CertificateRepository certificateRepository)
  {
    this.certificateRepository = certificateRepository;
  }

  @Inject
  public void setLearnerCertificateRepository(LearnerCertificateRepository learnerCertificateRepository)
  {
    this.learnerCertificateRepository = learnerCertificateRepository;
  }

  @Inject
  public void setAssessmentReportRepository(AssessmentReportRepository assessmentReportRepository)
  {
    this.assessmentReportRepository = assessmentReportRepository;
  }

  @Inject
  public void setCourseSuggestedUsersRepository(CourseSuggestedUsersRepository courseSuggestedUsersRepository)
  {
    this.courseSuggestedUsersRepository = courseSuggestedUsersRepository;
  }

  @Inject
  public void setClassroomCourseAttendanceRepository(ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository)
  {
    this.classroomCourseAttendanceRepository = classroomCourseAttendanceRepository;
  }

  @Inject
  public void setLearnerSuccessRepository(LearnerSuccessRepository learnerSuccessRepository)
  {
    this.learnerSuccessRepository = learnerSuccessRepository;
  }

  @Inject
  public void setExamRepository(ExamRepository examRepository)
  {
    this.examRepository = examRepository;
  }

  @Inject
  public void setExamGroupRepository(ExamGroupRepository examGroupRepository)
  {
    this.examGroupRepository = examGroupRepository;
  }

  @Inject
  public void setExamEnrollmentRepository(ExamEnrollmentRepository examEnrollmentRepository)
  {
    this.examEnrollmentRepository = examEnrollmentRepository;
  }

  @Inject
  public void setExamCategoryRepository(ExamCategoryRepository examCategoryRepository)
  {
    this.examCategoryRepository = examCategoryRepository;
  }

  @Inject
  public void setQuestionCategoryRepository(QuestionCategoryRepository questionCategoryRepository)
  {
    this.questionCategoryRepository = questionCategoryRepository;
  }

  @Inject
  public void setQuestionGroupRepository(QuestionGroupRepository questionGroupRepository)
  {
    this.questionGroupRepository = questionGroupRepository;
  }

  @Inject
  public void setQuestionRepository(QuestionRepository questionRepository)
  {
    this.questionRepository = questionRepository;
  }

  @Inject
  public void setQuestionStateRepository(QuestionStateRepository questionStateRepository)
  {
    this.questionStateRepository = questionStateRepository;
  }

  @Inject
  public void setExamRuntimeDataRepository(ExamRuntimeDataRepository examRuntimeDataRepository)
  {
    this.examRuntimeDataRepository = examRuntimeDataRepository;
  }

  @Inject
  public void setSystemConfigRepository(SystemConfigRepository systemConfigRepository)
  {
    this.systemConfigRepository = systemConfigRepository;
  }

  @Inject
  public void setFieldRepository(FieldRepository fieldRepository)
  {
    this.fieldRepository = fieldRepository;
  }

  @Inject
  public void setLearnerCourseHistoryRepository(LearnerCourseHistoryRepository learnerCourseHistoryRepository)
  {
    this.learnerCourseHistoryRepository = learnerCourseHistoryRepository;
  }

  @Inject
  public void setAnnouncementRepository(AnnouncementRepository announcementRepository)
  {
    this.announcementRepository = announcementRepository;
  }

  @Inject
  public void setAnnouncementRuntimeRepository(AnnouncementRuntimeRepository announcementRuntimeRepository)
  {
    this.announcementRuntimeRepository = announcementRuntimeRepository;
  }

  @Override
  public CourseRepository getCourseRepository()
  {
    return this.courseRepository;
  }

  @Override
  public CourseCategoryRepository getCourseCategoryRepository()
  {
    return this.courseCategoryRepository;
  }

  @Override
  public CourseContentRepository getCourseContentRepository()
  {
    return this.courseContentRepository;
  }

  @Override
  public CourseAssessmentRepository getCourseAssessmentRepository()
  {
    return this.courseAssessmentRepository;
  }

  @Override
  public CourseEnrollmentRepository getCourseEnrollmentRepository()
  {
    return this.courseEnrollmentRepository;
  }

  @Override
  public QuizRepository getQuizRepository()
  {
    return this.quizRepository;
  }

  @Override
  public CourseReportRepository getCourseReportRepository()
  {
    return this.courseReportRepository;
  }

  @Override
  public AssessmentRepository getAssessmentRepository()
  {
    return this.assessmentRepository;
  }

  @Override
  public CertificateRepository getCertificateRepository()
  {
    return this.certificateRepository;
  }

  @Override
  public LearnerCertificateRepository getLearnerCertificateRepository()
  {
    return this.learnerCertificateRepository;
  }

  @Override
  public AssessmentReportRepository getAssessmentReportRepository()
  {
    return this.assessmentReportRepository;
  }

  @Override
  public LearnerSuccessRepository getLearnerSuccessRepository()
  {
    return this.learnerSuccessRepository;
  }

  @Override
  public CourseSuggestedUsersRepository getCourseSuggestedUsersRepository()
  {
    return this.courseSuggestedUsersRepository;
  }

  @Override
  public ClassroomCourseAttendanceRepository getClassroomAttendanceRepository()
  {
    return this.classroomCourseAttendanceRepository;
  }

  @Override
  public ExamRepository getExamRepository()
  {
    return this.examRepository;
  }

  @Override
  public ExamGroupRepository getExamGroupRepository()
  {
    return this.examGroupRepository;
  }

  @Override
  public ExamEnrollmentRepository getExamEnrollmentRepository()
  {
    return this.examEnrollmentRepository;
  }

  @Override
  public ExamCategoryRepository getExamCategoryRepository()
  {
    return this.examCategoryRepository;
  }

  @Override
  public QuestionCategoryRepository getQuestionCategoryRepository()
  {
    return this.questionCategoryRepository;
  }

  @Override
  public QuestionGroupRepository getQuestionGroupRepository()
  {
    return this.questionGroupRepository;
  }

  @Override
  public QuestionRepository getQuestionRepository()
  {
    return this.questionRepository;
  }

  @Override
  public QuestionStateRepository getQuestionStateRepository()
  {
    return this.questionStateRepository;
  }

  @Override
  public ExamRuntimeDataRepository getExamRuntimeDataRepository()
  {
    return this.examRuntimeDataRepository;
  }

  @Override
  public SystemConfigRepository getSystemConfigRepository()
  {
    return this.systemConfigRepository;
  }

  @Override
  public ScheduledTaskRepository getScheduledTaskRepository()
  {
    return beanFactory.getBean(ScheduledTaskRepository.class);
  }

  @Override
  public FieldRepository getFieldRepository()
  {
    return this.fieldRepository;
  }

  @Override
  public LearnerCourseHistoryRepository getLearnerCourseHistoryRepository()
  {
    return this.learnerCourseHistoryRepository;
  }

  @Override
  public AnnouncementRepository getAnnouncementRepository()
  {
    return this.announcementRepository;
  }

  @Override
  public AnnouncementRuntimeRepository getAnnouncementRuntimeRepository()
  {
    return this.announcementRuntimeRepository;
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException
  {
    this.beanFactory = beanFactory;
  }
}
