package mn.erin.lms.base.scorm.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.lms.base.domain.model.assessment.Assessment;
import mn.erin.lms.base.domain.model.assessment.AssessmentId;
import mn.erin.lms.base.domain.model.assessment.Question;
import mn.erin.lms.base.domain.model.assessment.Quiz;
import mn.erin.lms.base.domain.model.content.CourseContentId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.repository.AssessmentRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.repository.QuizRepository;
import mn.erin.lms.base.domain.service.CourseContentCreator;
import mn.erin.lms.base.domain.service.LmsFileSystemService;
import mn.erin.lms.base.domain.usecase.course.classroom.CloseClassroomCourse;
import mn.erin.lms.base.scorm.model.AssetId;
import mn.erin.lms.base.scorm.model.ContentAggregation;
import mn.erin.lms.base.scorm.model.SCORMContent;
import mn.erin.lms.base.scorm.model.SCORMQuiz;
import mn.erin.lms.base.scorm.model.SCORMWrapper;
import mn.erin.lms.base.scorm.repository.SCORMContentRepository;
import mn.erin.lms.base.scorm.repository.SCORMQuizRepository;
import mn.erin.lms.base.scorm.repository.SCORMRepositoryException;
import mn.erin.lms.base.scorm.repository.SCORMRepositoryRegistry;
import mn.erin.lms.base.scorm.repository.SCORMWrapperRepository;

/**
 * @author Erdenetulga
 */
public class ScormContentCreator implements CourseContentCreator
{

  private static final Logger LOGGER = LoggerFactory.getLogger(CloseClassroomCourse.class);
  private LmsFileSystemService lmsFileSystemService;
  private static final String SCORM_CONTENT_FOLDER_NAME = "SCORM";
  private AssessmentRepository assessmentRepository;
  private QuizRepository quizRepository;
  private SCORMWrapperRepository scormWrapperRepository;
  private CourseRepository courseRepository;
  private SCORMContentRepository scormContentRepository;
  private SCORMQuizRepository scormQuizRepository;

  public ScormContentCreator(
      LmsFileSystemService lmsFileSystemService,
      LmsRepositoryRegistry lmsRepositoryRegistry,
      SCORMRepositoryRegistry scormRepositoryRegistry
  )
  {
    this.lmsFileSystemService = lmsFileSystemService;
    this.assessmentRepository = lmsRepositoryRegistry.getAssessmentRepository();
    this.quizRepository = lmsRepositoryRegistry.getQuizRepository();
    this.courseRepository = lmsRepositoryRegistry.getCourseRepository();
    this.scormWrapperRepository = scormRepositoryRegistry.getSCORMWrapperRepository();
    this.scormContentRepository = scormRepositoryRegistry.getSCORMContentRepository();
    this.scormQuizRepository = scormRepositoryRegistry.getSCORMQuizRepository();
  }

  @Override
  public boolean createContent(Course course)
  {

    String courseFolderId = lmsFileSystemService.getCourseFolderId(course.getCourseId().getId());

    if (StringUtils.isBlank(courseFolderId))
    {
      return false;
    }

    String scormFolderId = lmsFileSystemService.createFolderInside(courseFolderId, SCORM_CONTENT_FOLDER_NAME);
    Set<ContentAggregation.Organization> organizations = new HashSet<>();

    try
    {
      if (scormFolderId != null)
      {
        createSCORMSurvey(scormFolderId, course.getAssessmentId(), organizations);
        ContentAggregation contentAggregation = new ContentAggregation(UUID.randomUUID().toString());
        organizations.forEach(contentAggregation::addOrganization);
        SCORMWrapper scormWrapper = scormWrapperRepository.getDefaultWrapper();
        SCORMContent scormContent = scormContentRepository.create(scormFolderId, scormWrapper, contentAggregation);
        courseRepository.update(course.getCourseId(), CourseContentId.valueOf(scormContent.getScormContentId().getId()));
      }
      return true;
    }
    catch (SCORMRepositoryException | LmsRepositoryException e)
    {
      LOGGER.error(e.getMessage());
      return false;
    }
  }

  private void createSCORMSurvey(String scormFolderId, String assessmentId, Set<ContentAggregation.Organization> organizations)
  {
    try
    {
      SCORMWrapper surveyWrapper = scormWrapperRepository.getWrapper("Survey");
      Assessment assessment = assessmentRepository.findById(AssessmentId.valueOf(assessmentId));
      Quiz quiz = quizRepository.fetchById(assessment.getQuizId());
      SCORMQuiz scormQuestionnaire = scormQuizRepository.createSCORMQuiz(scormFolderId, "Үнэлгээний хуудас", getQuestions(quiz.getQuestions()),
          surveyWrapper, "survey-data.json");
      ContentAggregation.Organization organization = new ContentAggregation.Organization(scormQuestionnaire.getName());
      for (AssetId assetId : scormQuestionnaire.getQuizAssets())
      {
        organization.addResource(scormContentRepository.getResource(assetId));
      }
      organization.setShortID("sco" + 0);
      organizations.add(organization);
    }
    catch (SCORMRepositoryException | LmsRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
    }
  }

  private List<SCORMQuiz.Question> getQuestions(List<Question> questions)
  {
    List<SCORMQuiz.Question> result = new ArrayList<>();

    for (Question question : questions)
    {
      SCORMQuiz.Question scormQuestion = new SCORMQuiz.Question(question.getTitle(), question.getType().name(), question.isRequired());
      question.getAnswers().forEach(answer -> {
        SCORMQuiz.Answer scormAnswer = new SCORMQuiz.Answer(answer.getValue(), answer.isCorrect(), answer.getScore());
        scormQuestion.addAnswer(scormAnswer);
      });
      result.add(scormQuestion);
    }

    return result;
  }
}
