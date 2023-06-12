package mn.erin.lms.unitel.domain.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.common.excel.ExcelTableDataConverter;
import mn.erin.common.excel.ExcelWriterUtil;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.UserIdentityRepository;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.assessment.Assessment;
import mn.erin.lms.base.domain.model.assessment.AssessmentId;
import mn.erin.lms.base.domain.model.assessment.Question;
import mn.erin.lms.base.domain.model.assessment.QuestionType;
import mn.erin.lms.base.domain.model.assessment.Quiz;
import mn.erin.lms.base.domain.model.certificate.LearnerCertificate;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.report.LearnerAnswer;
import mn.erin.lms.base.domain.model.report.LearnerAssessment;
import mn.erin.lms.base.domain.repository.AssessmentRepository;
import mn.erin.lms.base.domain.repository.CourseEnrollmentRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LearnerCertificateRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.QuizRepository;
import mn.erin.lms.base.scorm.constants.DataModelConstants;
import mn.erin.lms.base.scorm.model.DataModel;
import mn.erin.lms.base.scorm.model.RuntimeData;
import mn.erin.lms.base.scorm.model.SCORMContentId;
import mn.erin.lms.base.scorm.repository.RuntimeDataRepository;
import mn.erin.lms.base.scorm.repository.SCORMRepositoryException;
import mn.erin.lms.unitel.domain.model.analytics.CourseAnalyticData;
import mn.erin.lms.unitel.domain.model.analytics.CourseAnalytics;

import static java.lang.Integer.parseInt;
import static mn.erin.lms.unitel.domain.util.CourseAnalyticDataConverter.convert;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseAnalyticsServiceImpl implements CourseAnalyticsService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CourseAnalyticsServiceImpl.class);
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
  private AssessmentRepository assessmentRepository;
  private QuizRepository quizRepository;
  private final LmsDepartmentService departmentService;
  private RuntimeDataRepository runtimeDataRepository;
  private CourseRepository courseRepository;
  private CourseEnrollmentRepository courseEnrollmentRepository;
  private AccessIdentityManagement accessIdentityManagement;
  private GroupRepository groupRepository;
  private LearnerCertificateRepository learnerCertificateRepository;
  private UserIdentityRepository userIdentityRepository;
  private final ExcelTableDataConverter<List<LearnerAssessment>> surveyExcelTableDataConverter = new CourseAnalyticsSurveyExcelTableDataConverter();
  private final ExcelTableDataConverter<Map<String, Object>> analyticsExcelTableDataConverter = new CourseAnalyticsExcelTableDataConverter();

  public CourseAnalyticsServiceImpl(LmsDepartmentService departmentService)
  {
    this.departmentService = departmentService;
  }

  @Inject
  public void setCourseCertificateRepository(LearnerCertificateRepository learnerCertificateRepository)
  {
    this.learnerCertificateRepository = learnerCertificateRepository;
  }

  @Inject
  public void setQuizRepository(QuizRepository quizRepository)
  {
    this.quizRepository = quizRepository;
  }

  @Inject
  public void setAssessmentRepository(AssessmentRepository assessmentRepository)
  {
    this.assessmentRepository = assessmentRepository;
  }

  @Inject
  public void setAccessIdentityManagement(AccessIdentityManagement accessIdentityManagement)
  {
    this.accessIdentityManagement = accessIdentityManagement;
  }

  @Inject
  public void setGroupRepository(GroupRepository groupRepository)
  {
    this.groupRepository = groupRepository;
  }

  @Inject
  public void setRuntimeDataRepository(RuntimeDataRepository runtimeDataRepository)
  {
    this.runtimeDataRepository = runtimeDataRepository;
  }

  @Inject
  public void setCourseRepository(CourseRepository courseRepository)
  {
    this.courseRepository = courseRepository;
  }

  @Inject
  public void setCourseEnrollmentRepository(CourseEnrollmentRepository courseEnrollmentRepository)
  {
    this.courseEnrollmentRepository = courseEnrollmentRepository;
  }

  @Inject
  public void setUserIdentityRepository(UserIdentityRepository userIdentityRepository)
  {
    this.userIdentityRepository = userIdentityRepository;
  }

  @Override
  public CourseAnalytics getCourseAnalytics(CourseId courseId, DepartmentId departmentId, LocalDate startDate, LocalDate endDate)
  {
    SCORMContentId scormContentId;
    try
    {
      Course course = courseRepository.fetchById(courseId);
      scormContentId = SCORMContentId.valueOf(course.getCourseContentId().getId());
    }
    catch (LmsRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return null;
    }

    try
    {
      List<RuntimeData> runtimeData = runtimeDataRepository.listRuntimeData(scormContentId);
      return getCourseAnalytics(courseId, runtimeData, departmentId, startDate, endDate);
    }
    catch (SCORMRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
  }

  @Override
  public byte[] generateAnalyticsData(CourseId courseId, DepartmentId departmentId, LocalDate startDate, LocalDate endDate)
  {
    List<String> headerList = new ArrayList<>();
    headerList.add("№");
    headerList.add("Нэр");
    headerList.add("Групп");
    headerList.add("Статус");
    headerList.add("Үзсэн хугацаа");
    headerList.add("Сорилын оноо");
    headerList.add("Давтан үзсэн тоо");
    headerList.add("Зарцуулсан хугацаа");
    headerList.add("Сертификат авсан хугацаа");
    headerList.add("Үнэлгээ");
    String[] courseAnalyticsHeaders;
    String sheetTitle;
    LocalDateTime dateTime = LocalDateTime.now();
    SCORMContentId scormContentId;
    Map<String, Object> data;

    try
    {
      Course course = courseRepository.fetchById(courseId);
      String title = course.getCourseDetail().getTitle();
      String sheetName = ("Үүсгэсэн огноо: " + dateTime.format(dateFormatter));
      sheetTitle = (title + " нэртэй цахим сургалтын статистик" + "\n" + sheetName);
      courseAnalyticsHeaders = headerList.toArray(new String[0]);
      scormContentId = SCORMContentId.valueOf(course.getCourseContentId().getId());
      try
      {
        data = getAnalyticDataForExcel(courseId, departmentId, startDate, endDate, scormContentId);
      }
      catch (SCORMRepositoryException e)
      {
        LOGGER.error(e.getMessage(), e);
        return null;
      }
    }
    catch (LmsRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
    try
    {
      return writeExcelData(courseAnalyticsHeaders, sheetTitle, data);
    }
    catch (IOException e)
    {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
  }

  @Override
  public CourseAnalyticData getCourseAnalytics(CourseId courseId, LearnerId learnerId)
  {

    SCORMContentId scormContentId;
    try
    {
      Course course = courseRepository.fetchById(courseId);
      scormContentId = SCORMContentId.valueOf(course.getCourseContentId().getId());
    }
    catch (LmsRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return null;
    }

    List<RuntimeData> runtimeData = runtimeDataRepository.listRuntimeData(scormContentId, learnerId.getId());

    if(runtimeData.isEmpty()){
      return null;
    }

    return convert(runtimeData, null, null);
  }

  @NotNull
  private Map<String, Object> getAnalyticDataForExcel(CourseId courseId, DepartmentId departmentId, LocalDate startDate, LocalDate endDate,
      SCORMContentId scormContentId) throws SCORMRepositoryException
  {
    Map<String, Object> data;
    List<RuntimeData> runtimeData = runtimeDataRepository.listRuntimeData(scormContentId);
    LocalDateTime receivedCertificateDate = null;
    CourseAnalytics courseAnalytics = new CourseAnalytics(courseId);
    Set<String> learners = departmentService.getAllLearners(departmentId.getId());
    Map<String, List<RuntimeData>> sortedByUser = sortByUser(runtimeData, learners);
    Set<String> enrollmentsByDepartments = new HashSet<>();

    Set<String> enrolledLearners = courseEnrollmentRepository.listAll(courseId)
        .stream().map(courseEnrollment -> courseEnrollment.getLearnerId().getId())
        .collect(Collectors.toSet());

    for (String enrolledLearner : enrolledLearners)
    {
      boolean userInGroup = learners.stream().anyMatch(user -> user.equals(enrolledLearner));
      if (!userInGroup)
      {
        continue;
      }
      enrollmentsByDepartments.add(enrolledLearner);
      List<LearnerCertificate> learnerCertificates = learnerCertificateRepository.listAll(LearnerId.valueOf(enrolledLearner));
      for (LearnerCertificate learnerCertificate : learnerCertificates)
      {
        if (learnerCertificate.getCourseId().equals(courseId))
        {
          receivedCertificateDate = learnerCertificate.getReceivedDate();
        }
      }
    }
    for (Map.Entry<String, List<RuntimeData>> entry : sortedByUser.entrySet())
    {
      String userDepartmentId = accessIdentityManagement.getUserDepartmentId(entry.getKey());
      Group group = groupRepository.findById(GroupId.valueOf(userDepartmentId));

      CourseAnalyticData analyticData = getAnalyticData(entry.getValue(), startDate, endDate, group.getName(), receivedCertificateDate);

      if (analyticData != null)
      {
        courseAnalytics.addData(LearnerId.valueOf(entry.getKey()), analyticData);
        enrollmentsByDepartments.remove(entry.getKey());
      }
    }

    for (String enrolledLearner : enrollmentsByDepartments)
    {
      String userDepartmentId = accessIdentityManagement.getUserDepartmentId(enrolledLearner);
      Group group = groupRepository.findById(GroupId.valueOf(userDepartmentId));
      courseAnalytics.addData(LearnerId.valueOf(enrolledLearner), new CourseAnalyticData.Builder().buildDepartmentName(group.getName()));
    }
    data = courseAnalytics.getAnalyticData().entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().getId(),
        Map.Entry::getValue));
    return data;
  }

  @NotNull
  private byte[] writeExcelData(String[] courseAnalyticsHeaders, String sheetTitle, Map<String, Object> data) throws IOException
  {
    try (ByteArrayOutputStream os = new ByteArrayOutputStream())
    {
      Object[][] excelData = analyticsExcelTableDataConverter.convert(data);
      ExcelWriterUtil.write(false, sheetTitle, courseAnalyticsHeaders, excelData, os);
      return os.toByteArray();
    }
  }

  @Override
  public byte[] generateSurveyAnalyticsData(CourseId courseId, DepartmentId departmentId, LocalDate startDate, LocalDate endDate, String userId)
  {
    if (userId == null)
    {
      List<String> headerList = new ArrayList<>();
      headerList.add("№");
      headerList.add("Нэр");
      String[] courseAnalyticsSurveyHeaders;
      String sheetTitle;
      LocalDateTime dateTime = LocalDateTime.now();
      List<LearnerAssessment> learnerAssessments = new ArrayList<>();

      try
      {
        Course course = courseRepository.fetchById(courseId);
        sheetTitle = setExcelHeaders(headerList, dateTime, course, null);
        courseAnalyticsSurveyHeaders = headerList.toArray(new String[0]);

        try
        {
          getLearnersAssessmentData(courseId, departmentId, learnerAssessments, course);
        }
        catch (SCORMRepositoryException e)
        {
          LOGGER.error(e.getMessage(), e);
          return null;
        }
      }
      catch (LmsRepositoryException e)
      {
        LOGGER.error(e.getMessage(), e);
        return null;
      }
      try
      {
        return writeSurveyExcelData(courseAnalyticsSurveyHeaders, sheetTitle, learnerAssessments);
      }
      catch (IOException e)
      {
        LOGGER.error(e.getMessage(), e);
        return null;
      }
    }
    else
    {
      List<String> headerList = new ArrayList<>();
      headerList.add("№");
      headerList.add("Нэр");
      String[] courseAnalyticsSurveyHeaders;
      String sheetTitle;
      LocalDateTime dateTime = LocalDateTime.now();
      List<LearnerAssessment> learnerAssessments = new ArrayList<>();

      try
      {
        Course course = courseRepository.fetchById(courseId);
        sheetTitle = setExcelHeaders(headerList, dateTime, course, userId);
        courseAnalyticsSurveyHeaders = headerList.toArray(new String[0]);
        getLearnersAssessmentData(learnerAssessments, course, userId);
      }
      catch (LmsRepositoryException e)
      {
        LOGGER.error(e.getMessage(), e);
        return null;
      }
      try
      {
        return writeSurveyExcelData(courseAnalyticsSurveyHeaders, sheetTitle, learnerAssessments);
      }
      catch (IOException e)
      {
        LOGGER.error(e.getMessage(), e);
        return null;
      }
    }
  }

  @NotNull
  private String setExcelHeaders(List<String> headerList, LocalDateTime dateTime, Course course, String userId) throws LmsRepositoryException
  {
    String sheetTitle;
    String title = course.getCourseDetail().getTitle();
    String sheetName = ("Үүсгэсэн огноо: " + dateTime.format(dateFormatter));
    if (userId != null)
    {
      sheetTitle = (userId + " хэрэглэгчийн " + title + " нэртэй цахим сургалтын үнэлгээний хуудас" + "\n" + sheetName);
    }
    else
    {
      sheetTitle = (title + " нэртэй цахим сургалтын үнэлгээний хуудас" + "\n" + sheetName);
    }
    Assessment assessment = assessmentRepository.findById(AssessmentId.valueOf(course.getAssessmentId()));

    Quiz quiz = quizRepository.fetchById(assessment.getQuizId());
    List<Question> questions = quiz.getQuestions();
    for (Question question : questions)
    {
      headerList.add(question.getTitle() + "(" + getQuestionType(question.getType()) + ")");
    }
    return sheetTitle;
  }

  private String getQuestionType(QuestionType questionType)
  {
    String result;
    switch (questionType)
    {
    case MULTI_CHOICE:
      result = "Олон сонголттой";
      break;
    case FILL_IN_BLANK:
      result = "Нээлттэй";
      break;
    default:
      result = "Хаалттай";
    }
    return result;
  }

  private void getLearnersAssessmentData(CourseId courseId, DepartmentId departmentId, List<LearnerAssessment> learnerAssessments,
      Course course) throws SCORMRepositoryException
  {
    Map<String, List<RuntimeData>> sortedByUser = getEnrolledLearners(courseId, departmentId, course);
    getAssessmentData(learnerAssessments, sortedByUser);
  }

  @Override
  public List<Map<Integer, String>> getLearnersAssessmentDatas(String courseId, DepartmentId departmentId)
  {
    try
    {
      Course course = courseRepository.fetchById(CourseId.valueOf(courseId));
      Map<String, List<RuntimeData>> sortedByUser = getEnrolledLearners(CourseId.valueOf(courseId), departmentId, course);

      return getAssessmentAnswers(sortedByUser);
    }
    catch (SCORMRepositoryException | LmsRepositoryException e)
    {
      return Collections.emptyList();
    }
  }

  private void getLearnersAssessmentData(List<LearnerAssessment> learnerAssessments,
      Course course, String userId)
  {
    Map<String, List<RuntimeData>> sortedByUser = getEnrolledLearners(course, userId);
    getAssessmentData(learnerAssessments, sortedByUser);
  }

  @NotNull
  private Map<String, List<RuntimeData>> getEnrolledLearners(CourseId courseId, DepartmentId departmentId, Course course) throws SCORMRepositoryException
  {
    Map<String, List<RuntimeData>> sortedByUser = new HashMap<>();
    if (course.getCourseContentId() != null)
    {
      SCORMContentId scormContentId;
      Set<String> enrollmentsByDepartments = new HashSet<>();

      scormContentId = SCORMContentId.valueOf(course.getCourseContentId().getId());

      Set<String> learners = departmentService.getAllLearners(departmentId.getId());

      Set<String> enrolledLearners = courseEnrollmentRepository.listAll(courseId)
          .stream().map(courseEnrollment -> courseEnrollment.getLearnerId().getId())
          .collect(Collectors.toSet());
      for (String enrolledLearner : enrolledLearners)
      {
        boolean userInGroup = learners.stream().anyMatch(user -> user.equals(enrolledLearner));
        if (!userInGroup)
        {
          continue;
        }
        enrollmentsByDepartments.add(enrolledLearner);
      }
      List<RuntimeData> runtimeData = runtimeDataRepository.listRuntimeData(scormContentId);
      sortedByUser = sortByUser(runtimeData, enrollmentsByDepartments);
    }
    return sortedByUser;
  }

  @NotNull
  private Map<String, List<RuntimeData>> getEnrolledLearners(Course course, String userId)
  {
    SCORMContentId scormContentId;
    scormContentId = SCORMContentId.valueOf(course.getCourseContentId().getId());

    List<RuntimeData> runtimeData = runtimeDataRepository.listRuntimeData(scormContentId, userId);
    return getRuntimeDataByUser(runtimeData);
  }

  @NotNull
  private Map<String, List<RuntimeData>> getRuntimeDataByUser(List<RuntimeData> runtimeData)
  {
    Map<String, List<RuntimeData>> sorted = new HashMap<>();

    List<String> users = new ArrayList<>();

    for (RuntimeData datum : runtimeData)
    {
      datum.getData().forEach((key, value) -> {
        if (DataModelConstants.CMI_LEARNER_ID.equals(key.getName()))
        {
          users.add((String) value);
        }
      });
    }

    return getUsersData(runtimeData, sorted, users);
  }

  private Map<String, List<RuntimeData>> getUsersData(List<RuntimeData> runtimeData, Map<String, List<RuntimeData>> sorted, List<String> users)
  {
    for (String user : users)
    {
      List<RuntimeData> data = runtimeData.stream().filter(datum -> {

        for (Map.Entry<DataModel, Serializable> entry : datum.getData().entrySet())
        {
          if (DataModelConstants.CMI_LEARNER_ID.equals(entry.getKey().getName()) && user.equals(entry.getValue()))
          {
            return true;
          }
        }

        return false;
      }).collect(Collectors.toList());

      sorted.put(user, data);
    }
    return sorted;
  }

  private void getAssessmentData(List<LearnerAssessment> learnerAssessments, Map<String, List<RuntimeData>> sortedByUser)
  {
    for (Map.Entry<String, List<RuntimeData>> runtimeDataList : sortedByUser.entrySet())
    {
      LearnerAssessment learnerAssessment = new LearnerAssessment();
      for (RuntimeData data : runtimeDataList.getValue())
      {
        if (data.getSco().getName().equalsIgnoreCase("Үнэлгээний хуудас"))
        {
          for (Map.Entry<DataModel, Serializable> dataValues : data.getData().entrySet())
          {
            if ((dataValues.getKey().getName().equalsIgnoreCase("cmi.suspend_data")) &&
                (dataValues.getValue().toString().contains("singleChoice") ||
                    dataValues.getValue().toString().contains("multiChoice") ||
                    dataValues.getValue().toString().contains("fillInBlank")))
            {
              learnerAssessment.setLearnerId(data.getLearnerId());
              List<String> subStringedAnswers = getAnswers(dataValues.getValue().toString());
              List<LearnerAnswer> answers = seperateAnswers(subStringedAnswers);
              learnerAssessment.setAnswers(answers);
              learnerAssessments.add(learnerAssessment);
            }
          }
        }
      }
    }
  }

  private List<Map<Integer, String>> getAssessmentAnswers(Map<String, List<RuntimeData>> sortedByUser)
  {
    List<Map<Integer, String>> resultList = new ArrayList<>();

    for (Map.Entry<String, List<RuntimeData>> runtimeDataList : sortedByUser.entrySet())
    {
      for (RuntimeData data : runtimeDataList.getValue())
      {
        if (data.getSco().getName().equalsIgnoreCase("Үнэлгээний хуудас"))
        {
          for (Map.Entry<DataModel, Serializable> dataValues : data.getData().entrySet())
          {
            if ((dataValues.getKey().getName().equalsIgnoreCase("cmi.suspend_data")) &&
                (dataValues.getValue().toString().contains("singleChoice") ||
                    dataValues.getValue().toString().contains("multiChoice") ||
                    dataValues.getValue().toString().contains("fillInBlank")))
            {
              List<String> subStringedAnswers = getAnswers(dataValues.getValue().toString());
              List<LearnerAnswer> answers = seperateAnswers(subStringedAnswers);
              Map<Integer, String> result = new HashMap<>();
              for (LearnerAnswer answer : answers)
              {
                result.put(answer.getIndex(), answer.getAnswer());
              }
              resultList.add(result);
            }
          }
        }
      }
    }
    return resultList;
  }

  public List<String> getAnswers(String suspendData)
  {
    //      Â Alt0706
    //      Å Alt0709
    //      Å Alt0704
    List<String> result = new ArrayList<>();
    String singleChoice = suspendData;
    String multiChoice = suspendData;
    String fillInBlank = suspendData;
    if (suspendData != null && !suspendData.equals("unknown"))
    {
      if (singleChoice.contains("singleChoiceÂ") && singleChoice.contains("Å"))
      {
        int singleChoiceCount = 0;
        singleChoice = singleChoice.substring(singleChoice.indexOf("Â") + 1);
        getAnswersByAnswersType(result, singleChoice, singleChoiceCount);
      }
      if (multiChoice.contains("multiChoiceÂ") && multiChoice.contains("Å"))
      {
        int multiChoiceCount = 0;
        multiChoice = multiChoice.substring(multiChoice.indexOf("multiChoiceÂ") + 12);
        getAnswersByAnswersType(result, multiChoice, multiChoiceCount);
      }
      if (fillInBlank.contains("fillInBlankÂ") && fillInBlank.contains("Å"))
      {
        int fillInBlankCount = 0;
        fillInBlank = fillInBlank.substring(fillInBlank.indexOf("fillInBlankÂ") + 12);
        getAnswersByAnswersType(result, fillInBlank, fillInBlankCount);
      }
    }
    return result;
  }

  private void getAnswersByAnswersType(List<String> result, String suspendData, int suspendDataCount)
  {
    if (suspendData.contains("Å"))
    {
      suspendData = suspendData.substring(0, suspendData.indexOf("Å"));
    }
    for (int i = 0; i < suspendData.length(); i++)
    {
      if (suspendData.charAt(i) == 'À')
      {
        suspendDataCount++;
      }
    }
    for (int i = 0; i < suspendDataCount; i++)
    {
      if (suspendData.contains("Â"))
      {
        result.add(suspendData.substring(0, suspendData.indexOf("Â")));
        suspendData = suspendData.substring(suspendData.indexOf("Â") + 1);
      }
      else
      {
        result.add(suspendData);
      }
    }
  }

  @NotNull
  private List<LearnerAnswer> seperateAnswers(List<String> substringedAnswers)
  {
    //      Â Alt0706
    //      Å Alt0709
    //      Å Alt0704
    List<LearnerAnswer> answers = new ArrayList<>();

    for (String substringedAnswer : substringedAnswers)
    {
      LearnerAnswer learnerAnswer = new LearnerAnswer();

      String substringedIndex = StringUtils.substringBefore(substringedAnswer, "À");
      int finallyIndex = parseInt(substringedIndex);
      String finallyAnswer = substringedAnswer.substring(substringedAnswer.lastIndexOf("À") + 1);
      learnerAnswer.setIndex(finallyIndex);
      learnerAnswer.setAnswer(finallyAnswer);
      answers.add(learnerAnswer);
    }
    return answers;
  }

  @NotNull
  private byte[] writeSurveyExcelData(String[] courseAnalyticsSurveyHeaders, String sheetTitle, List<LearnerAssessment> learnerAssessments) throws IOException
  {
    Object[][] excelData = surveyExcelTableDataConverter.convert(learnerAssessments);
    try (ByteArrayOutputStream os = new ByteArrayOutputStream())
    {
      ExcelWriterUtil.write(false, sheetTitle, courseAnalyticsSurveyHeaders, excelData, os);
      return os.toByteArray();
    }
  }

  private CourseAnalytics getCourseAnalytics(CourseId courseId, List<RuntimeData> runtimeData, DepartmentId departmentId,
      LocalDate startDate, LocalDate endDate)
  {
    LocalDateTime receivedCertificateDate = null;
    CourseAnalytics courseAnalytics = new CourseAnalytics(courseId);
    Set<String> learners = departmentService.getAllLearners(departmentId.getId());
    Set<String> enrollmentsByDepartments = new HashSet<>();

    Set<String> enrolledLearners = courseEnrollmentRepository.listAll(courseId)
        .stream().map(courseEnrollment -> courseEnrollment.getLearnerId().getId())
        .collect(Collectors.toSet());

    for (String enrolledLearner : enrolledLearners)
    {
      if (!learners.contains(enrolledLearner))
      {
        continue;
      }
      enrollmentsByDepartments.add(enrolledLearner);
      List<LearnerCertificate> learnerCertificates = learnerCertificateRepository.listAll(LearnerId.valueOf(enrolledLearner));
      for (LearnerCertificate learnerCertificate : learnerCertificates)
      {
        if (learnerCertificate.getCourseId().equals(courseId))
        {
          receivedCertificateDate = learnerCertificate.getReceivedDate();
        }
      }
    }

    Map<String, List<RuntimeData>> sortedByUser = sortByUser(runtimeData, enrollmentsByDepartments);

    for (Map.Entry<String, List<RuntimeData>> entry : sortedByUser.entrySet())
    {
      String userDepartmentId = accessIdentityManagement.getUserDepartmentId(entry.getKey());
      Group group = groupRepository.findById(GroupId.valueOf(userDepartmentId));

      CourseAnalyticData data = getAnalyticData(entry.getValue(), startDate, endDate, group.getName(), receivedCertificateDate);

      if (data != null)
      {
        courseAnalytics.addData(LearnerId.valueOf(entry.getKey()), data);
        enrollmentsByDepartments.remove(entry.getKey());
      }
    }

    for (String enrolledLearner : enrollmentsByDepartments)
    {
      String userDepartmentId = accessIdentityManagement.getUserDepartmentId(enrolledLearner);
      Group group = groupRepository.findById(GroupId.valueOf(userDepartmentId));
      courseAnalytics.addData(LearnerId.valueOf(enrolledLearner), new CourseAnalyticData.Builder().buildDepartmentName(group.getName()));
    }

    return courseAnalytics;
  }

  private Map<String, List<RuntimeData>> sortByUser(List<RuntimeData> runtimeData, Set<String> learners)
  {
    Map<String, List<RuntimeData>> sorted = new HashMap<>();

    List<String> users = new ArrayList<>();

    for (RuntimeData datum : runtimeData)
    {
      for (Map.Entry<DataModel, Serializable> entry : datum.getData().entrySet())
      {
        DataModel key = entry.getKey();
        Serializable value = entry.getValue();
        if(!DataModelConstants.CMI_LEARNER_ID.equals(key.getName()))
        {
          continue;
        }
        String user = (String) value;
        if (learners.contains(user))
        {
          users.add((String) value);
        }
      }
    }

    return getUsersData(runtimeData, sorted, users);
  }

  private CourseAnalyticData getAnalyticData(List<RuntimeData> runtimeData, LocalDate startDate, LocalDate endDate, String userDepartmentName,
      LocalDateTime receivedCertificateDate)
  {
    CourseAnalyticData data = convert(runtimeData, userDepartmentName, receivedCertificateDate);
    LocalDate lastLaunchDate = LocalDate.now();
    if (data.getLastLaunchDate() != null)
    {
      lastLaunchDate = data.getLastLaunchDate().toLocalDate();
    }

    if ((lastLaunchDate.isAfter(startDate) || lastLaunchDate.isEqual(startDate)) &&
        (lastLaunchDate.isBefore(endDate) || lastLaunchDate.isEqual(endDate)))
    {
      return data;
    }
    return null;
  }
}
