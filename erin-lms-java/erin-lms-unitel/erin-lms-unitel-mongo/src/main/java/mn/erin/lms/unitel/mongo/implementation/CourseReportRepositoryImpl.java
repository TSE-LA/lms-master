package mn.erin.lms.unitel.mongo.implementation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;

import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.domain.model.certificate.LearnerCertificate;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.domain.repository.CourseEnrollmentRepository;
import mn.erin.lms.base.domain.repository.LearnerCertificateRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.mongo.document.course.MongoCourse;
import mn.erin.lms.base.mongo.repository.MongoCourseRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.lms.base.scorm.constants.DataModelConstants;
import mn.erin.lms.base.scorm.model.DataModel;
import mn.erin.lms.base.scorm.model.RuntimeData;
import mn.erin.lms.base.scorm.model.SCORMContentId;
import mn.erin.lms.base.scorm.repository.RuntimeDataRepository;
import mn.erin.lms.base.scorm.repository.SCORMRepositoryException;
import mn.erin.lms.unitel.domain.model.analytics.CourseAnalyticData;
import mn.erin.lms.unitel.domain.model.report.CourseReport;
import mn.erin.lms.unitel.domain.repository.CourseReportRepository;
import mn.erin.lms.unitel.mongo.document.EnrolledGroup;
import mn.erin.lms.unitel.mongo.document.MongoCourseReport;
import mn.erin.lms.unitel.mongo.document.MongoReportData;
import mn.erin.lms.unitel.mongo.repository.MongoCourseReportRepository;

import static mn.erin.lms.unitel.domain.util.CourseAnalyticDataConverter.convert;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseReportRepositoryImpl implements CourseReportRepository
{
  private static final String DECIMAL_FORMAT = "%.1f";
  private static final Logger LOGGER = LoggerFactory.getLogger(CourseReportRepositoryImpl.class);

  private final MongoCourseReportRepository mongoCourseReportRepository;
  private MongoCourseRepository mongoCourseRepository;
  private RuntimeDataRepository runtimeDataRepository;
  private CourseEnrollmentRepository courseEnrollmentRepository;
  private LmsDepartmentService lmsDepartmentService;
  private LearnerCertificateRepository learnerCertificateRepository;

  public CourseReportRepositoryImpl(MongoCourseReportRepository mongoCourseReportRepository)
  {
    this.mongoCourseReportRepository = mongoCourseReportRepository;
  }

  @Inject
  public void setLearnerCertificateRepository(LearnerCertificateRepository learnerCertificateRepository)
  {
    this.learnerCertificateRepository = learnerCertificateRepository;
  }

  @Inject
  public void setCourseEnrollmentRepository(CourseEnrollmentRepository courseEnrollmentRepository)
  {
    this.courseEnrollmentRepository = courseEnrollmentRepository;
  }

  @Inject
  public void setMongoCourseRepository(MongoCourseRepository mongoCourseRepository)
  {
    this.mongoCourseRepository = mongoCourseRepository;
  }

  @Inject
  public void setRuntimeDataRepository(RuntimeDataRepository runtimeDataRepository)
  {
    this.runtimeDataRepository = runtimeDataRepository;
  }

  @Inject
  public void setLmsDepartmentService(LmsDepartmentService lmsDepartmentService)
  {
    this.lmsDepartmentService = lmsDepartmentService;
  }

  @Override
  public void save(CourseReport courseReport)
  {
    //    Map<String, Object> reportData = courseReport.getReportData();
    //    String id = courseReport.getCourseId().getId();
    //    int questionsCount = (int) reportData.get(PromotionConstants.REPORT_FIELD_QUESTIONS_COUNT);
    //    MongoCourseReport mongoCourseReport = new MongoCourseReport(id, questionsCount);
    //    mongoCourseReportRepository.save(mongoCourseReport);
  }

  @Override
  public CourseReport find(CourseId courseId, DepartmentId departmentId) throws LmsRepositoryException
  {
    return mapToCourseReport(courseId, departmentId);
  }

  @Override
  public void update(CourseId courseId, Map<String, Object> data) throws LmsRepositoryException
  {
    //    MongoCourseReport mongoCourseReport = getMongoCourseReport(courseId);
    //    update(mongoCourseReport, data);
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean delete(CourseId courseId)
  {
    mongoCourseReportRepository.deleteById(courseId.getId());
    return true;
  }

  void update(MongoCourseReport mongoCourseReport, Map<String, Object> data)
  {
    throw new UnsupportedOperationException();
  }

  private CourseReport mapToCourseReport(CourseId courseId, DepartmentId departmentId)
  {
    MongoCourseReport mongoCourseReport = new MongoCourseReport(courseId.getId());
    Optional<MongoCourse> mongoCourse = mongoCourseRepository.findById(courseId.getId());
    MongoCourse course = new MongoCourse();
    if (mongoCourse.isPresent())
    {
      course = mongoCourse.get();
    }
    CourseReport courseReport = new CourseReport(courseId);
    if (course.getCourseContentId() != null)
    {
      SCORMContentId scormContentId;

      //Report fields
      float enrollmentCount = 0;
      List<CourseEnrollment> enrollments;
      int viewersCount = 0;
      float status;
      float completedViewersCount = 0.0f;
      float receivedCertificateCount;
      int repeatedViewersCount = 0;
      int survey = 0;
      int analyticDataSampleSize = 0;
      Set<EnrolledGroup> enrolledSubGroups = new HashSet<>();
      scormContentId = SCORMContentId.valueOf(course.getCourseContentId());
      List<LearnerCertificate> learnerCertificates = learnerCertificateRepository.listAll(courseId);

      receivedCertificateCount = learnerCertificates.size();

      for (String subDepartment : course.getAssignedDepartments())
      {
        EnrolledGroup enrolledGroup = new EnrolledGroup();
        int subGroupsEnrollmentCount = 0;
        Set<String> learners = lmsDepartmentService.getLearners(subDepartment);
        for (String learner : learners)
        {
          subGroupsEnrollmentCount += courseEnrollmentRepository.getEnrollmentCountByLearnerId(courseId, learner);
          enrolledGroup.setEnrollmentCount(subGroupsEnrollmentCount);
          enrolledGroup.setEnrolledGroup(subDepartment);
        }
        enrolledSubGroups.add(enrolledGroup);
      }

      Set<String> learners = lmsDepartmentService.getLearners(departmentId.getId());
      for (String learner : learners)
      {
        enrollmentCount += courseEnrollmentRepository.getEnrollmentCountByLearnerId(courseId, learner);
      }

      enrollments = courseEnrollmentRepository.listAll(courseId);
      try
      {
        List<RuntimeData> runtimeData = runtimeDataRepository.listRuntimeData(scormContentId);
        Map<String, List<RuntimeData>> sortedByUser = sortByUser(runtimeData, departmentId);
        for (Map.Entry<String, List<RuntimeData>> entry : sortedByUser.entrySet())
        {
          CourseAnalyticData data = convert(entry.getValue(), "", null);
          repeatedViewersCount = data.getInteractionsCount();

          if (data.getStatus() > 0)
          {
            viewersCount++;
          }

          if (data.getStatus().equals(100.0f))
          {
            completedViewersCount++;
          }

          if (!StringUtils.isBlank(data.getFeedback()))
          {
            survey++;
          }

          analyticDataSampleSize += data.getStatus();
        }
      }
      catch (SCORMRepositoryException e)
      {
        LOGGER.error("Couldn't get scorm contents", e);
        return null;
      }
      status = enrollmentCount == 0.0 ? 0 : Float.parseFloat(String.format(DECIMAL_FORMAT, (analyticDataSampleSize / (float) enrollmentCount)));
      MongoReportData mongoReportData = new MongoReportData(
          course.getTitle(),
          course.getCategoryName(),
          course.getCourseType(),
          course.getAuthorId(),
          course.isHasCertificate(),
          enrollmentCount,
          enrolledSubGroups,
          course.getAssignedDepartments(),
          viewersCount,
          completedViewersCount,
          repeatedViewersCount,
          receivedCertificateCount,
          survey,
          status > 100 ? 100.0f : status
      );
      mongoCourseReport.addData(mongoReportData);
      Map<String, Object> reportData = new HashMap<>();
      for (MongoReportData reportDatum : mongoCourseReport.getReportData())
      {
        reportData.put("title", reportDatum.getTitle());
        reportData.put("category", reportDatum.getCategory());
        reportData.put("state", reportDatum.getState());
        reportData.put("authorId", reportDatum.getAuthorId());
        reportData.put("hasCertificate", reportDatum.isHasCertificate());
        reportData.put("enrollmentCount", reportDatum.getEnrollmentCount());
        reportData.put("enrolledGroups", reportDatum.getEnrolledGroups());
        reportData.put("viewersCount", reportDatum.getViewersCount());
        reportData.put("completedViewersCount", reportDatum.getCompletedViewersCount());
        reportData.put("repeatedViewersCount", reportDatum.getRepeatedViewersCount());
        reportData.put("receivedCertificateCount", reportDatum.getReceivedCertificateCount());
        reportData.put("enrolledGroupIds", reportDatum.getEnrolledGroupId());
        reportData.put("survey", reportDatum.getSurvey());
        reportData.put("status", reportDatum.getStatus());
      }

      for (CourseEnrollment enrollment : enrollments)
      {
        courseReport.addLearner(enrollment.getLearnerId());
      }

      courseReport.setReportData(reportData);
    }
    return courseReport;
  }

  private Map<String, List<RuntimeData>> sortByUser(List<RuntimeData> runtimeData, DepartmentId departmentId)
  {
    Set<String> learners = lmsDepartmentService.getLearners(departmentId.getId());

    Map<String, List<RuntimeData>> sorted = new HashMap<>();

    List<String> users = new ArrayList<>();

    for (RuntimeData datum : runtimeData)
    {
      datum.getData().forEach((key, value) -> {
        if (DataModelConstants.CMI_LEARNER_ID.equals(key.getName()))
        {
          String user = (String) value;
          if (learners.contains(user))
          {
            users.add((String) value);
          }
        }
      });
    }

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
}
