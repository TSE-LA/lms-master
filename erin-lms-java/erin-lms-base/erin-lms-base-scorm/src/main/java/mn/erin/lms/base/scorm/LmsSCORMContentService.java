package mn.erin.lms.base.scorm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.dms.repository.DMSRepositoryException;
import mn.erin.lms.base.domain.model.assessment.Assessment;
import mn.erin.lms.base.domain.model.assessment.AssessmentId;
import mn.erin.lms.base.domain.model.assessment.CourseAssessment;
import mn.erin.lms.base.domain.model.assessment.Question;
import mn.erin.lms.base.domain.model.assessment.Questionnaire;
import mn.erin.lms.base.domain.model.assessment.Quiz;
import mn.erin.lms.base.domain.model.assessment.QuizId;
import mn.erin.lms.base.domain.model.content.CourseContent;
import mn.erin.lms.base.domain.model.content.CourseContentId;
import mn.erin.lms.base.domain.model.content.CourseModule;
import mn.erin.lms.base.domain.model.content.CourseSection;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.repository.AssessmentRepository;
import mn.erin.lms.base.domain.repository.CourseAssessmentRepository;
import mn.erin.lms.base.domain.repository.CourseContentRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.QuizRepository;
import mn.erin.lms.base.domain.service.CourseLauncher;
import mn.erin.lms.base.domain.service.CoursePublisher;
import mn.erin.lms.base.domain.service.LmsFileSystemService;
import mn.erin.lms.base.domain.service.QuestionnaireService;
import mn.erin.lms.base.domain.service.ThumbnailService;
import mn.erin.lms.base.scorm.model.AssetId;
import mn.erin.lms.base.scorm.model.ContentAggregation;
import mn.erin.lms.base.scorm.model.RuntimeData;
import mn.erin.lms.base.scorm.model.SCO;
import mn.erin.lms.base.scorm.model.SCORMContent;
import mn.erin.lms.base.scorm.model.SCORMContentId;
import mn.erin.lms.base.scorm.model.SCORMQuiz;
import mn.erin.lms.base.scorm.model.SCORMWrapper;
import mn.erin.lms.base.scorm.repository.RuntimeDataRepository;
import mn.erin.lms.base.scorm.repository.SCORMContentRepository;
import mn.erin.lms.base.scorm.repository.SCORMQuizRepository;
import mn.erin.lms.base.scorm.repository.SCORMRepositoryException;
import mn.erin.lms.base.scorm.repository.SCORMWrapperRepository;

import static mn.erin.lms.base.scorm.RuntimeDataConverter.convertRuntimeData;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LmsSCORMContentService implements CoursePublisher, CourseLauncher<List<SCOModel>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(LmsSCORMContentService.class);
  private static final String SCORM_CONTENT_FOLDER_NAME = "SCORM";

  private CourseRepository courseRepository;
  private CourseAssessmentRepository courseAssessmentRepository;
  private QuizRepository quizRepository;
  private CourseContentRepository courseContentRepository;
  private SCORMContentRepository scormContentRepository;
  private SCORMQuizRepository scormQuizRepository;
  private SCORMWrapperRepository scormWrapperRepository;
  private RuntimeDataRepository runtimeDataRepository;
  private LmsFileSystemService lmsFileSystemService;
  private QuestionnaireService questionnaireService;
  private AssessmentRepository assessmentRepository;
  private ThumbnailService thumbnailService;

  @Inject
  public void setCourseRepository(CourseRepository courseRepository)
  {
    this.courseRepository = courseRepository;
  }

  @Inject
  public void setCourseAssessmentRepository(CourseAssessmentRepository courseAssessmentRepository)
  {
    this.courseAssessmentRepository = courseAssessmentRepository;
  }

  @Inject
  public void setThumbnailService(ThumbnailService thumbnailService)
  {
    this.thumbnailService = thumbnailService;
  }

  @Inject
  public void setQuizRepository(QuizRepository quizRepository)
  {
    this.quizRepository = quizRepository;
  }

  @Inject
  public void setCourseContentRepository(CourseContentRepository courseContentRepository)
  {
    this.courseContentRepository = courseContentRepository;
  }

  @Inject
  public void setScormContentRepository(SCORMContentRepository scormContentRepository)
  {
    this.scormContentRepository = scormContentRepository;
  }

  @Inject
  public void setScormQuizRepository(SCORMQuizRepository scormQuizRepository)
  {
    this.scormQuizRepository = scormQuizRepository;
  }

  @Inject
  public void setScormWrapperRepository(SCORMWrapperRepository scormWrapperRepository)
  {
    this.scormWrapperRepository = scormWrapperRepository;
  }

  @Inject
  public void setRuntimeDataRepository(RuntimeDataRepository runtimeDataRepository)
  {
    this.runtimeDataRepository = runtimeDataRepository;
  }

  @Inject
  public void setLmsFileSystemService(LmsFileSystemService lmsFileSystemService)
  {
    this.lmsFileSystemService = lmsFileSystemService;
  }

  @Inject
  public void setQuestionnaireService(QuestionnaireService questionnaireService)
  {
    this.questionnaireService = questionnaireService;
  }

  @Inject
  public void setAssessmentRepository(AssessmentRepository assessmentRepository)
  {
    this.assessmentRepository = assessmentRepository;
  }

  @Override
  public boolean publishCourseContent(String courseId)
  {
    try
    {
      Course course = courseRepository.fetchById(CourseId.valueOf(courseId));
      return publishCourseContent(course);
    }
    catch (LmsRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return false;
    }
  }

  @Override
  public void delete(String scormContentId)
  {
    SCORMContentId id = SCORMContentId.valueOf(scormContentId);

    try
    {
      scormContentRepository.delete(id);
    }
    catch (SCORMRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
    }

    runtimeDataRepository.delete(id);
  }

  @Override
  public List<SCOModel> launch(String courseId, String courseContentId)
  {
    try
    {
      Set<SCO> scos = scormContentRepository.getShareableContentObjects(SCORMContentId.valueOf(courseContentId));
      return toResult(courseId, scos);
    }
    catch (SCORMRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return Collections.emptyList();
    }
  }

  private List<SCOModel> toResult(String courseId, Set<SCO> scos)
  {
    List<SCOModel> result = new ArrayList<>();

    String scormContentName = scos.iterator().next().getRootEntity().getName();
    for (SCO sco : scos)
    {
      String pathComponent = sco.getPath();
      String fullPath = "Courses/" + courseId + "/SCORM/" + scormContentName + "/" + pathComponent;

      RuntimeData runtimeData;
      try
      {
        runtimeData = runtimeDataRepository.getRuntimeData(sco.getRootEntity().getScormContentId(), sco.getName());
      }
      catch (SCORMRepositoryException e)
      {
        runtimeData = runtimeDataRepository.create(sco);
      }
      SCOModel scoModel = new SCOModel(sco.getName(), fullPath, convertRuntimeData(runtimeData.getData()));
      result.add(scoModel);
    }

    return result;
  }

  private boolean publishCourseContent(Course course)
  {
    try
    {
      CourseContent courseContent = courseContentRepository.fetchById(course.getCourseId());
      return createSCORMContent(course, courseContent.getModules());
    }
    catch (LmsRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return false;
    }
  }

  private boolean createSCORMContent(Course course, List<CourseModule> modules)
  {
    String courseFolderId = lmsFileSystemService.getCourseFolderId(course.getCourseId().getId());

    if (StringUtils.isBlank(courseFolderId))
    {
      return false;
    }

    checkExistingSCORMAndDelete(course, courseFolderId);

    String scormFolderId = lmsFileSystemService.createFolderInside(courseFolderId, SCORM_CONTENT_FOLDER_NAME);

    if (scormFolderId == null)
    {
      LOGGER.error("Could not create SCORM folder for: [{}]", course.getCourseId().getId());
      return false;
    }

    Set<ContentAggregation.Organization> organizations = getOrganizations(modules);

    if (thumbnailService.getDefaultThumbnailUrl().equals(course.getCourseDetail().getThumbnailUrl()))
    {
      thumbnailService.setThumbnailUrlFromCourseContents(modules, course, courseFolderId);
    }

    if (course.getCourseDetail().hasQuiz())
    {
      createSCORMQuiz(scormFolderId, course.getCourseId(), organizations);
    }
    if (course.getCourseDetail().hasFeedbackOption())
    {
      createSCORMQuestionnaire(scormFolderId, organizations);
    }
    if (course.getCourseDetail().hasAssessment())
    {
      createSCORMSurvey(scormFolderId, course.getAssessmentId(), organizations);
    }

    ContentAggregation contentAggregation = new ContentAggregation(UUID.randomUUID().toString());
    organizations.forEach(contentAggregation::addOrganization);

    try
    {
      SCORMWrapper scormWrapper = scormWrapperRepository.getDefaultWrapper();
      SCORMContent scormContent = scormContentRepository.create(scormFolderId, scormWrapper, contentAggregation);
      courseRepository.update(course.getCourseId(), CourseContentId.valueOf(scormContent.getScormContentId().getId()));
      return true;
    }
    catch (SCORMRepositoryException | LmsRepositoryException e)
    {
      LOGGER.error(e.getMessage());
      return false;
    }
  }

  private void checkExistingSCORMAndDelete(Course course, String courseFolderId)
  {
    try
    {
      String existingFolder = lmsFileSystemService.getSCORMFolderId(courseFolderId);
      boolean deleted = lmsFileSystemService.deleteFolder(existingFolder);
      if (!deleted)
      {
        throw new DMSRepositoryException("Could not delete already existing SCORM folder! [" + course.getCourseId().getId() + "]");
      }
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.warn(e.getMessage(), e);
    }
  }

  private Set<ContentAggregation.Organization> getOrganizations(List<CourseModule> modules)
  {
    Set<ContentAggregation.Organization> organizations = new HashSet<>();

    int counter = 0;
    for (CourseModule module : modules)
    {
      Set<ContentAggregation.Resource> resources = new LinkedHashSet<>();

      for (CourseSection section : module.getSectionList())
      {
        AssetId assetId = AssetId.valueOf(section.getAttachmentId().getId());
        try
        {
          ContentAggregation.Resource resource = scormContentRepository.getResource(assetId);
          resources.add(resource);
        }
        catch (SCORMRepositoryException e)
        {
          LOGGER.error(e.getMessage());
        }
      }

      ContentAggregation.Organization organization = new ContentAggregation.Organization(module.getName());
      resources.forEach(organization::addResource);
      organization.setShortID("sco" + counter);
      organizations.add(organization);
      counter++;
    }

    return organizations;
  }

  private void createSCORMQuiz(String scormFolderId, CourseId courseId, Set<ContentAggregation.Organization> organizations)
  {
    Set<QuizId> quizzes;
    SCORMWrapper quizWrapper;
    try
    {
      CourseAssessment courseAssessment = courseAssessmentRepository.fetchById(courseId);
      quizzes = courseAssessment.getQuizzes();
      quizWrapper = scormWrapperRepository.getWrapper("Quiz");
    }
    catch (LmsRepositoryException | SCORMRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return;
    }

    for (QuizId quizId : quizzes)
    {
      try
      {
        Quiz quiz = quizRepository.fetchById(quizId);

        SCORMQuiz scormQuiz;
        if (quiz.getMaxAttempts() != null && quiz.getThresholdScore() != null)
        {
          scormQuiz = scormQuizRepository.createSCORMQuiz(scormFolderId, quiz.getName(), getQuestions(quiz.getQuestions()), quizWrapper,
              "quiz-data.json", quiz.getMaxAttempts(), quiz.getThresholdScore());
        }
        else
        {
          scormQuiz = scormQuizRepository.createSCORMQuiz(scormFolderId, quiz.getName(), getQuestions(quiz.getQuestions()), quizWrapper,
              "quiz-data.json");
        }

        ContentAggregation.Organization organization = new ContentAggregation.Organization(scormQuiz.getName());

        for (AssetId assetId : scormQuiz.getQuizAssets())
        {
          organization.addResource(scormContentRepository.getResource(assetId));
        }
        organization.setShortID("sco" + organizations.size());
        organizations.add(organization);
      }
      catch (LmsRepositoryException | SCORMRepositoryException e)
      {
        LOGGER.error(e.getMessage());
      }
    }
  }

  private void createSCORMQuestionnaire(String scormFolderId, Set<ContentAggregation.Organization> organizations)
  {
    try
    {
      SCORMWrapper questionnaireWrapper = scormWrapperRepository.getWrapper("Questionnaire");
      Questionnaire questionnaire = questionnaireService.newInstance();
      SCORMQuiz scormQuestionnaire = scormQuizRepository.createSCORMQuiz(scormFolderId, questionnaire.getName(), getQuestions(questionnaire.getQuestions()),
          questionnaireWrapper, "questionnaire-data.json");
      ContentAggregation.Organization organization = new ContentAggregation.Organization(scormQuestionnaire.getName());
      for (AssetId assetId : scormQuestionnaire.getQuizAssets())
      {
        organization.addResource(scormContentRepository.getResource(assetId));
      }
      organizations.add(organization);
    }
    catch (SCORMRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
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
      organization.setShortID("sco" + organizations.size());
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
