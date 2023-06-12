package mn.erin.lms.base.analytics.repository.mongo.constants;

/**
 * @author Munkh
 */
public class MongoPromotionQueries
{
  private MongoPromotionQueries()
  {}

  public static final String STAGE_PROMOTION_PROJECT = "{ $project: { "
      + "_id: { $toString: \"$_id\" }, "
      + "contentId: \"$courseContentId._id\", "
      + "publishStatus: \"$courseDetail.publishStatus\", "
      + "createdDate: \"$courseDetail.createdDate\", "
      + "courseCategoryId: \"$courseCategoryId._id\", "
      + "hasFeedback: \"$courseDetail.properties.hasFeedBack\", "
      + "hasTest: \"$courseDetail.properties.hasTest\", "
      + "state: \"$courseDetail.properties.state\", "
      + "code:\t\"$courseDetail.properties.code\", "
      + "name: \"$courseDetail.title\", "
      + "author: \"$authorId._id\" "
      + "} }";

  public static final String STAGE_PROMOTION_MATCH = "{ $match: { "
      + "_id: { $in: __courses__ }, "
      + "publishStatus: \"__publishStatus__\", "
      + "createdDate: { $gte: ISODate(\"__startDate__\"), $lte: ISODate(\"__endDate__\") } "
      + "} }";

  public static final String STAGE_PROMOTION_MATCH_BY_CATEGORY_ID = "{ $match: { "
      + "_id: { $in: __courses__ }, "
      + "publishStatus: \"__publishStatus__\", "
      + "createdDate: { $gte: ISODate(\"__startDate__\"), $lte: ISODate(\"__endDate__\") }, "
      + "courseCategoryId: \"__categoryId__\", "
      + "} }";

  public static final String STAGE_PROMOTION_MATCH_BY_STATE = "{ $match: { "
      + "_id: { $in: __courses__ }, "
      + "publishStatus: \"__publishStatus__\", "
      + "createdDate: { $gte: ISODate(\"__startDate__\"), $lte: ISODate(\"__endDate__\") }, "
      + "state: \"__courseType__\""
      + "} }";

  public static final String STAGE_PROMOTION_MATCH_FULL = "{ $match: { "
      + "\"_id\": { $in: __courses__ }, "
      + "\"publishStatus\": \"__publishStatus__\", "
      + "\"createdDate\": { $gte: ISODate(\"__startDate__\"), $lte: ISODate(\"__endDate__\") }, "
      + "\"courseCategoryId\": \"__categoryId__\", "
      + "\"state\": \"__courseType__\""
      + "} }";

  public static final String STAGE_MATCH_ENROLLMENTS = "{ $match: { "
      + "\"courseId._id\": { $in: __courses__ }, "
      + "\"learnerId._id\": { $in: __learners__ } "
      + "} }";

  public static final String STAGE_COUNT_ENROLLMENTS = "{ $group: { "
      + "_id: \"$courseId._id\", "
      + "count: { $sum: 1 } "
      + "} } ";

  public static final String STAGE_GROUP_ENROLLMENTS = "{ $group: { "
      + "_id: \"$courseId._id\", "
      + "enrollments: { $push: \"$learnerId._id\" } "
      + "} } ";

  public static final String STAGE_LOOKUP_PROMOTION_ENROLLMENT = "{ $lookup: { "
      + "from: \"CourseEnrollment\", "
      + "let: { course_id: \"$_id\" }, "
      + "pipeline: [ { $match: { $expr: { $and: [ "
      + "{ $eq: [ \"$courseId._id\", \"$$course_id\" ] },"
      + "{ $in: [ \"$learnerId._id\", __learners__ ] } ] "
      + "} } }, { $group: { "
      + "_id: \"$courseId._id\", "
      + "count: { $sum: 1 } "
      + "} } ], "
      + "as: \"enrolledUsers\" "
      + "} }";

  public static final String STAGE_UNWIND_PROMOTION_ENROLLMENT = "{ $unwind: { "
      + "path: \"$enrolledUsers\", "
      + "preserveNullAndEmptyArrays: true "
      + "} }";

  public static final String STAGE_MATCH_TESTS = "{ $match: { "
      + "\"courseId._id\": { $in: __courses__ }, "
      + "} } ";

  public static final String STAGE_OPEN_UP_TEST_IDS_OF_TESTS = "{ $project: { "
      + "_id: \"$courseId._id\", "
      // TODO: Fix this projection after course multi-test feature
      + "testId: { $arrayElemAt: [ \"$testIds\", 0 ] } "
      + "} }";

  public static final String STAGE_PROJECT_TESTS = "{ $project: { "
      + "_id: \"$_id\", "
      + "testId: \"$testId._id\" "
      + "} } ";

  public static final String STAGE_PROJECT_QUESTIONS = "{ $project: { "
      + "_id: { $toString: \"$_id\" },"
      + "count: { $size: \"$questions\" } "
      + "} }";

  public static final String STAGE_MATCH_QUESTIONS = "{ $match: { "
      + "_id: { $in: __tests__ } "
      + "} }";


  public static final String STAGE_LOOKUP_PROMOTION_QUESTIONS = "{ $lookup: { "
      + "from: \"CourseAssessments\", "
      + "let: { course_id: \"$_id\" }, "
      + "pipeline: [ { $match: { "
      + "$expr: { $eq: [ \"$courseId._id\", \"$$course_id\" ] } "
      + "} },"
      // TODO: Fix this projection after course multi-test feature
      + "{ $project: { "
      + "_id: \"$courseId._id\", "
      + "testId: { $arrayElemAt: [ \"$testIds\", 0 ] } "
      + "} },"
      + "{ $project: { "
      + "_id: \"$courseId._id\", "
      + "testId: \"$testId._id\" "
      + "} },"
      + "{ $lookup: { "
      + "from: \"CourseTests\","
      + "let: { test_id: \"$testId\" },"
      + "pipeline: [ { $match: { "
      + "$expr: { $eq: [ { $toString: \"$_id\" }, \"$$test_id\" ] } "
      + "} },"
      + "{ $project: { "
      + "count: { $size: \"$questions\" } "
      + "} } ], "
      + "as: \"questions\" "
      + "} }, { $project: { "
      + "count: \"$questions.count\" "
      + "} }, { $project: { "
      + "count: { $arrayElemAt: [ \"$count\", 0 ] } "
      + "} } ],"
      + "as: \"questions\" "
      + "} }";

  public static final String STAGE_UNWIND_PROMOTION_QUESTIONS = "{ $unwind: { "
      + "path: \"$questions\", "
      + "preserveNullAndEmptyArrays: true "
      + "} }";

  public static final String STAGE_PROMOTION_PROJECT_FINAL = "{ $project: { "
      + "_id: \"$_id\", "
      + "contentId: \"$contentId\", "
      + "createdDate: \"$createdDate\", "
      + "courseCategoryId: \"$courseCategoryId\", "
      + "hasFeedback: \"$hasFeedback\", "
      + "hasTest: \"$hasTest\", "
      + "state: \"$state\", "
      + "code: \"$code\", "
      + "name: \"$name\", "
      + "author: \"$author\" "
      + "} }";

  public static final String STAGE_PROMOTION_SCORM_GROUP = "{ $group: { "
      + "_id: {"
      + "learnerId: \"$learnerId._id\", "
      + "contentId: \"$scormContentId._id\" "
      + "}, "
      + "contentId: { $addToSet: \"$scormContentId._id\" }, "
      + "progress: { $avg: { $convert: { input: \"$runtimeData.cmi,progress_measure.value\", to: \"double\" } } },"
      + "views: { $push: { $toInt: \"$runtimeData.cmi,interactions,_count.value\" } } "
      + "} }";

  public static final String STAGE_PROMOTION_MATCH_BY_LEARNER = "{ $match: { "
      + "\"_id.learnerId\": { $in: __enrollments__ }, "
      + "} }";

  public static final String STAGE_UNWIND_PROMOTION_SCORM_GROUP = "{ $unwind: { "
      + "path: \"$contentId\", "
      + "preserveNullAndEmptyArrays: true "
      + "} }";

  public static final String STAGE_PROMOTION_SCORM_GROUP_BY_CONTENT = "{ $group: { "
      + "_id: \"$contentId\", "
      + "progress: { $sum: \"$progress\" }, "
      + "views: { $sum: { $cond: [ { $gt: [ { $sum: \"$views\" }, 0 ] }, 1, 0 ] } } "
      + "} }";

  public static final String STAGE_PROMOTION_SCORM_FEEDBACK_MATCH = "{ $match: { "
      + "\"scormContentId._id\": { $in: __contents__ }, "
      + "\"learnerId._id\": { $in: __learners__ }, "
      + "\"runtimeData.cmi,progress_measure.value\": \"100.0\", "
      + "scoName: \"АСУУЛГА\", "
      + "} }";

  public static final String STAGE_PROMOTION_SCORM_FEEDBACK_PROJECT = "{ $project: { "
      + "contentId: \"$scormContentId._id\", "
      + "learnerId: \"$learnerId._id\" "
      + "} }";

  public static final String STAGE_PROMOTION_SCORM_FEEDBACK_GROUP = "{ $group: { "
      + "_id: \"$scormContentId._id\", "
      + "count: { $sum: 1 } "
      + "} }";

  public static final String STAGE_LOOKUP_PROMOTION_SCORM_FEEDBACK = "{ $lookup: { "
      + "from: \"Run-Time Data\", "
      + "let: { \"content_id\": \"$_id\" }, "
      + "pipeline: [ { $match: { $expr: { $and: ["
      + "{ $eq: [ \"$scormContentId._id\", \"$$content_id\" ] },"
      + "{ $in: [ \"$learnerId._id\", __learners__ ] },"
      + "{ $eq: [ \"$scoName\", \"АСУУЛГА\" ] },"
      + "{ $eq: [ \"$runtimeData.cmi,progress_measure.value\", \"100.0\" ] } "
      + "] } } }, "
      + "{ $group: { "
      + "_id: \"$scormContentId._id\", "
      + "count: { $sum: 1 } "
      + "} } ], "
      + "as: \"feedback\" "
      + "} }";

  public static final String STAGE_UNWIND_PROMOTION_SCORM_FEEDBACK = "{ $unwind: { "
      + "path: \"$feedback\", "
      + "preserveNullAndEmptyArrays: true "
      + "} }";

  public static final String STAGE_PROMOTION_SCORM_TEST_MATCH = "{ $match: { "
      + "\"scormContentId._id\": { $in: __contents__ }, "
      + "\"learnerId._id\": { $in: __learners__ }, "
      + "\"runtimeData.cmi,score,max.value\": { $ne: \"unknown\" }, "
      + "\"runtimeData.cmi,score,raw.value\": { $ne: \"unknown\" }, "
      + "scoName: \"ТЕСТ\", "
      + "} }";

  public static final String STAGE_PROMOTION_SCORM_TEST_PROJECT = "{ $project: { "
      + "contentId: \"$scormContentId._id\", "
      + "learnerId: \"$learnerId._id\", "
      + "score: { $convert: { input: \"$runtimeData.cmi,score,raw.value\", to: \"int\" } } "
      + "} }";

  public static final String STAGE_PROMOTION_SCORM_TEST_GROUP = "{ $group: { "
      + "_id: \"$scormContentId._id\", "
      + "score: { $sum: { $convert: { input: \"$runtimeData.cmi,score,raw.value\", to: \"int\" } } } "
      + "} }";

  public static final String STAGE_LOOKUP_PROMOTION_SCORM_TEST = "{ $lookup: { "
      + "from: \"Run-Time Data\", "
      + "let: { \"content_id\": \"$_id\" }, "
      + "pipeline: [ { $match: { $expr: { $and: ["
      + "{ $eq: [ \"$scormContentId._id\", \"$$content_id\" ] },"
      + "{ $in: [ \"$learnerId._id\", __learners__ ] },"
      + "{ $eq: [ \"$scoName\", \"ТЕСТ\" ] },"
      + "{ $ne: [ \"$runtimeData.cmi,score,max.value\", \"unknown\" ] }, "
      + "{ $ne: [ \"$runtimeData.cmi,score,raw.value\", \"unknown\" ] } "
      + "] } } }, "
      + "{ $group: { "
      + "_id: \"$scormContentId._id\", "
      + "score: { $sum: { $convert: { input: \"$runtimeData.cmi,score,raw.value\", to: \"int\" } } } "
      + "} } ], "
      + "as: \"test\" "
      + "} }";

  public static final String STAGE_UNWIND_PROMOTION_SCORM_TEST = "{ $unwind: { "
      + "path: \"$test\", "
      + "preserveNullAndEmptyArrays: true "
      + "} }";

  public static final String STAGE_PROMOTION_SCORM_PROJECT_FINAL = "{ $project: { "
      + "_id: \"$_id\", "
      + "progress: \"$progress\", "
      + "views: \"$views\", "
//      + "feedback: { $ifNull: [ \"$feedback.count\", 0 ] }, "
//      + "score: { $ifNull: [ \"$test.score\", 0 ] } "
      + "} }";

  // Activity
  public static final String STAGE_PROMOTION_COURSE_MATCH = "{ $group: { _id: { $toString: \"$_id\" }, contentId: { $first: \"$courseContentId._id\" } } }";

  public static final String STAGE_PROMOTION_SCORM_MATCH = "{ $match: { "
      + "\"learnerId._id\": { $in: __learners__ }, "
      + "\"scormContentId._id\": { $in: __contents__ },"
      + "scoName: { $nin: [ \"АСУУЛГА\", \"ТЕСТ\" ] } "
      + "} }";

  public static final String STAGE_PROMOTION_SCORM_GROUP_BY_LEARNER = "{ $group: { "
      + "_id: \"$learnerId._id\", "
      + "runtimeData: { $push: { "
      + "contentId: \"$scormContentId._id\", "
      + "progress: { $convert: { input: \"$runtimeData.cmi,progress_measure.value\", to: \"double\" } }, "
      + "spentTime: \"$runtimeData.cmi,total_time.value\" } }"
      + "} }";


  public static final String STAGE_PROMOTION_ENROLLMENT_MATCH = "{ $match: { "
      + "\"learnerId._id\": { $in: %s }, "
      + "\"courseId._id\": { $in: %s } "
      + "} }";

  public static final String STAGE_PROMOTION_ENROLLMENT_GROUP = "{ $group: { "
      + "_id: \"$learnerId._id\", "
      + "courses: { $push: \"$courseId._id\" }"
      + "} }";
}
