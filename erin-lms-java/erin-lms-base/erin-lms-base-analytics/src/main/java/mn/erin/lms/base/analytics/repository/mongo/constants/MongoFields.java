package mn.erin.lms.base.analytics.repository.mongo.constants;

/**
 * @author Munkh
 */
public class MongoFields
{
  private MongoFields()
  {
  }

  public static final String FIELD_ID = "_id";

  // Course
  public static final String FIELD_COURSE_PUBLISH_STATUS = "publishStatus";
  public static final String FIELD_COURSE_CATEGORY_ID = "categoryId";

  // region Groups
  public static final String FIELD_GROUP_CHILDREN = "children";
  public static final String FIELD_GROUP_PARENT_ID = "parentId";
  public static final String FIELD_GROUP_TENANT_ID = "tenantId";
  public static final String FIELD_GROUP_NAME = "name";
  public static final String FIELD_GROUP_NTH_SIBLING = "nthSibling";
  public static final String FIELD_GROUP_DESCRIPTION = "description";
  // endregion Groups

  // region Category
  public static final String FIELD_CATEGORY_PARENT_CATEGORY_ID = "parentCategoryId";
  public static final String FIELD_CATEGORY_ORGANIZATION_ID = "organizationId";

  public static final String FIELD_CATEGORY_NAME = "categoryName";
  // region Category

  // region Memberships
  public static final String FIELD_MEMBERSHIP_GROUP_ID = "groupId";
  public static final String FIELD_MEMBERSHIP_USER_ID = "userId";
  public static final String FIELD_MEMBERSHIP_ROLE_ID = "roleId";
  // endregion Memberships

  // region Course after final projection
  public static final String FIELD_COURSE_TITLE = "title";
  public static final String FIELD_COURSE_CATEGORY_NAME = "categoryName";
  public static final String FIELD_COURSE_COURSE_TYPE = "courseType";
  public static final String FIELD_COURSE_COURSE_CONTENT_ID = "courseContentId";
  public static final String FIELD_COURSE_HAS_CERTIFICATE = "hasCertificate";
  public static final String FIELD_COURSE_RECEIVED_CERTIFICATE_COUNT = "receivedCertificateCount";
  public static final String FIELD_COURSE_CERTIFICATE_COUNT = "count";
  public static final String FIELD_COURSE_LEARNERS = "learners";
  public static final String FIELD_COURSE_RUNTIME_DATA = "runtimeData";
  public static final String FIELD_COURSE_TOTAL_PROGRESS = "totalProgress";
  public static final String FIELD_COURSE_VIEWERS_COUNT = "viewersCount";
  public static final String FIELD_COURSE_REPEATED_VIEWERS_COUNT = "repeatedViewersCount";
  public static final String FIELD_COURSE_COMPLETED_VIEWERS_COUNT = "completedViewersCount";
  public static final String FIELD_COURSE_ENROLLMENT_COUNT = "enrollmentCount";
  // endregion Course after final projection

  // region Enrollments
  public static final String FIELD_ENROLLMENT_LEARNER_ID = "learnerId";
  public static final String FIELD_ENROLLMENT_COURSE_ID = "courseId";
  public static final String FIELD_ENROLLMENT_COUNT = "count";
  public static final String FIELD_ENROLLMENTS = "enrollments";
  // endregion Enrollments

  // region Learner
  public static final String FIELD_LEARNER_COURSE_TITLE = "title";
  public static final String FIELD_LEANER_COURSE_CATEGORY = "category";
  public static final String FIELD_LEARNER_COURSE_COURSE_TYPE = "courseType";
  public static final String FIELD_LEARNER_RECEIVED_CERTIFICATE_DATE = "receivedCertificateDate";
  public static final String FIELD_LEARNER_PROGRESS = "progress";
  public static final String FIELD_LEARNER_SPENT_TIME = "spentTime";
  public static final String FIELD_LEARNER_VIEWS = "views";
  public static final String FIELD_LEARNER_FIRST_VIEW_DATE = "firstViewDate";
  public static final String FIELD_LEARNER_LAST_VIEW_DATE = "lastViewDate";
  public static final String FIELD_LEARNER_SURVEY = "survey";
  public static final String FIELD_LEARNER_MAX_SCORE = "maxScore";
  public static final String FIELD_LEARNER_SCORE = "score";
  public static final String FIELD_LEARNER_SURVEY_SPENT_TIME = "surveySpentTime";
  public static final String FIELD_LEARNER_TEST_SPENT_TIME = "spentTimeOnTest";
  // endregion Learner

  // region Promotion
  public static final String FIELD_COURSE_ID = "courseId";
  public static final String FIELD_GROUP_ID = "groupId";

  public static final String FIELD_CONTENT_ID = "contentId";
  public static final String FIELD_COURSES = "courses";
  public static final String FIELD_RUNTIME_DATA = "runtimeData";
  public static final String FIELD_PROGRESS = "progress";
  public static final String FIELD_SPENT_TIME = "spentTime";
  public static final String FIELD_LEARNER_ID = "learnerId";

  public static final String FIELD_CODE = "code";
  public static final String FIELD_AUTHOR = "author";
  public static final String FIELD_NAME = "name";
  public static final String FIELD_STATE = "state";
  public static final String FIELD_CREATED_DATE = "createdDate";
  public static final String FIELD_TOTAL_ENROLLMENT = "totalEnrollment";
  public static final String FIELD_QUESTIONS = "questions";
  public static final String FIELD_STATUS = "status";
  public static final String FIELD_HAS_TEST = "hasTest";
  public static final String FIELD_HAS_FEEDBACK = "hasFeedback";

  public static final String FIELD_FEEDBACK = "feedback";
  public static final String FIELD_SCORE = "score";
  public static final String FIELD_VIEWS = "views";

  public static final String FIELD_SCORM_FEEDBACK_COUNT = "count";
  // endregion Promotion

  // region Survey
  public static final String FIELD_SURVEY_ANSWER = "answer";
  public static final String FIELD_LAST_LAUNCH_DATE = "runtimeData.erin,date,last_launch.data";
  public static final String FIELD_SURVEY_ID = "assessmentId";
  public static final String FIELD_SURVEY_QUIZ_ID = "quizId";
  // endregion Survey

  // region Quiz
  public static final String FIELD_QUIZ_QUESTIONS = "questions";
  // endregion Quiz

  // region Question
  public static final String FIELD_QUESTION_TYPE = "type";
  public static final String FIELD_QUESTION_TITLE = "title";
  public static final String FIELD_QUESTION_REQUIRED = "required";
  public static final String FIELD_QUESTION_ANSWERS = "answers";
  public static final String FIELD_QUESTION_COUNT = "count";
  // endregion Question

  // region Answer
  public static final String FIELD_ANSWER_VALUE = "value";
  public static final String FIELD_ANSWER_CORRECT = "correct";
  public static final String FIELD_ANSWER_SCORE = "score";
  // endregion Answer

  // region Assessment
  public static final String FIELD_ASSESSMENT_TEST_ID = "testId";
  // endregion Assessment

  // region Exam
  public static final String FIELD_EXAM_TITLE = "title";
  public static final String FIELD_EXAM_CATEGORY_NAME = "categoryName";
  public static final String FIELD_EXAM_STATUS = "status";
  public static final String FIELD_EXAM_DURATION = "duration";
  public static final String FIELD_EXAM_PASSED_COUNT = "passedCount";
  public static final String FIELD_EXAM_TOTAL_RUNTIME = "totalRuntime";
  public static final String FIELD_EXAM_AVERAGE_SCORE = "averageScore";
  public static final String FIELD_EXAM_AVERAGE_SPENT_TIME = "averageSpentTime";
  public static final String FIELD_EXAM_MAX_SCORE = "maxScore";
  public static final String FIELD_EXAM_QUESTION_COUNT = "questionCount";
  public static final String FIELD_EXAM_ENROLLMENT_COUNT = "enrollmentCount";

  //region SCORM
  public static final String FIELD_SCORM_CONTENT_ID = "scormContentId";
  public static final String FIELD_SCORM_CONTENT_NAME = "scormContentName";
  public static final String FIELD_SCORM_SCO_NAME = "scoName";
  public static final String FIELD_SCORM_MAX_SCORE = "maxScore";
  public static final String FIELD_COURSE_TYPE = "courseType";
  public static final String FIELD_PERFORMANCE = "performance";
  public static final String FIELD_YEAR = "year";
  public static final String FIELD_MONTH = "month";
}
