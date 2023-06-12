package mn.erin.lms.base.analytics.repository.mongo.constants;

/**
 * @author Munkh
 */
public class MongoLearnerQueries
{
  private MongoLearnerQueries()
  {}

  /**
   * courses array
   * categories array
   */
  public static final String STAGE_COURSE_MATCH = "{ $match: { "
      + "createdDate: { $gte: ISODate(\"__startDate__\"), $lte: ISODate(\"__endDate__\") }, "
      + "$and: [ "
      + "{ $expr: { $in: [ { $toString: \"$_id\" }, __enrolledCourses__ ] } }, "
      + "{ $expr: { $in: [ \"$categoryId\", __subCategories__ ] } } "
      + "] } }";

  public static final String STAGE_COURSE_GROUP = "{ $group: { "
      + "_id: { $toString: \"$_id\" }, "
      + "title: { $first: \"$title\" }, "
      + "categoryId: { $first: \"$categoryId\" }, "
      + "courseType: { $first: \"$courseType\" }, "
      + "courseContentId: { $first: \"$courseContentId\" } "
      + "} }";

  public static final String STAGE_LOOKUP_COURSE_CATEGORY_NAME = "{ $lookup: {"
      + "from: \"mongoCourseCategory\", "
      + "let: { category_id: \"$categoryId\" }, "
      + "pipeline: ["
      + "{ $match: { $expr: { $eq: [ { \"$toString\": \"$_id\" }, \"$$category_id\" ] } } }"
      + "], "
      + "as: \"category\""
      + "} }";

  /**
   * learnerId string
   */
  public static final String STAGE_LEARNER_CERTIFICATE_LOOKUP = "{ $lookup: { "
      + "from: \"mongoLearnerCertificate\", "
      + "let: { course_id: \"$_id\", learner_id: \"__learnerId__\" }, "
      + "pipeline: [ "
      + "{ $match: { "
      + "$and: [ "
      + "{ $expr: { $eq: [ \"$learnerId\", \"$$learner_id\" ] } }, "
      + "{ $expr: { $eq: [ \"$courseId\", \"$$course_id\" ] } } "
      + "] } }, "
      + "{ "
      + "$project: { "
      + "_id: \"$courseId\", "
      + "receivedDate: \"$receivedDate\" "
      + "} } ], "
      + "as: \"certificate\" "
      + "} }";

  /**
   * learnerId string
   */
  public static final String STAGE_RUNTIME_DATA_LOOKUP = "{ $lookup: { "
      + "from: \"SCORM\", "
      + "let: { content_id: \"$courseContentId\", learnerId: \"__learnerId__\" }, "
      + "pipeline: [ "
      + "{ $match: { "
      + "$and : ["
      + "{ $expr: { $eq: [ \"$scormContentId._id\", \"$$content_id\" ] } }, "
      + "{ $expr: {  $eq: [ \"$learnerId._id\", \"$$learnerId\" ] } }, "
      + "{ scoName: { $nin: [ \"Үнэлгээний хуудас\", \"ТЕСТ\" ] } }"
      + "] } }, "
      + "{ $lookup: { "
      + "from: \"SCORM\", "
      + "let: { content_id: \"$scormContentId._id\", learner_id: \"$$learnerId\" }, "
      + "pipeline: [{ "
      + "$match: { "
      + "$and: [ "
      + "{ $expr: { $eq: [ \"$scormContentId._id\", \"$$content_id\" ] } }, "
      + "{ $expr: { $eq: [ \"$learnerId._id\", \"$$learner_id\" ] } }, "
      + "{ scoName: \"Үнэлгээний хуудас\" }, "
      + "{ \"runtimeData.cmi,suspend_data.data\": { $ne: \"unknown\" } } "
      + "] } },"
      + "{ $project: { "
      + "survey: \"$runtimeData.cmi,suspend_data.data\" "
      + "} } ], "
      + "as: \"survey\" "
      + "} }, "
      + "{ $lookup: { "
      + "from: \"SCORM\", "
      + "let: { content_id: \"$scormContentId._id\", learner_id: \"$$learnerId\" }, "
      + "pipeline: [ { $match: { "
      + "$and: [ "
      + "{ $expr: { $eq: [ \"$scormContentId._id\", \"$$content_id\" ] } }, "
      + "{ $expr: { $eq: [ \"$learnerId._id\", \"$$learner_id\" ] } }, "
      + "{ scoName: \"ТЕСТ\" }, "
      + "{ \"runtimeData.cmi,score,max.data\": { $ne: \"unknown\" } }, "
      + "{ \"runtimeData.cmi,score,raw.data\": { $ne: \"unknown\" } } "
      + "] } }, "
      + "{ $project: { "
      + "max: \"$runtimeData.cmi,score,max.data\", "
      + "raw: \"$runtimeData.cmi,score,raw.data\", "
      + "spentTime: \"$runtimeData.cmi,total_time.data\" "
      + "} } ], "
      + "as: \"test\" "
      + "} }, "
      + "{ $group: { "
      + "_id: \"$learnerId._id\", "
      + "progress: { $avg: { $convert: { input: \"$runtimeData.cmi,progress_measure.data\", to: \"double\" } } }, "
      + "spentTime: { $push: \"$runtimeData.cmi,total_time.data\" }, "
      + "views: { $max: { $toInt: \"$runtimeData.cmi,interactions,_count.data\" } }, "
      + "firstViewDate: { $min: { $convert: { input: \"$runtimeData.erin,date,initial_launch.data\", to: \"date\", onError: null } } }, "
      + "lastViewDate: { $max: { $convert: { input: \"$runtimeData.erin,date,last_launch.data\", to: \"date\", onError: null } } }, "
      + "survey: { $first: \"$survey\" }, "
      + "test: { $first: \"$test\" } "
      + "} },"
      + "{ $project: { "
      + "_id: \"$learnerId._id\", "
      + "progress: \"$progress\", "
      + "spentTime: \"$spentTime\", "
      + "views: \"$views\", "
      + "firstViewDate: { $dateToString: { format: \"__dateTimeFormat__\", date: \"$firstViewDate\" } }, "
      + "lastViewDate: { $dateToString: { format: \"__dateTimeFormat__\", date: \"$lastViewDate\" } }, "
      + "survey: { $arrayElemAt: [ \"$survey\", 0 ] }, "
      + "test: { $arrayElemAt: [ \"$test\", 0 ] }"
      + "} } ], "
      + "as: \"runtimeData\" "
      + "} }";

  public static final String STAGE_COURSE_PROJECT = "{ $project: { "
      + "_id: \"$_id\", "
      + "title: \"$title\", "
      + "category: { $arrayElemAt: [ \"$category\", 0 ] }, "
      + "courseType: \"$courseType\", "
      + "certificate: { $arrayElemAt: [ \"$certificate\", 0 ] }, "
      + "runtimeData: { $arrayElemAt: [ \"$runtimeData\", 0 ] } "
      + "} }";

  public static final String STAGE_COURSE_PROJECT_FINAL = "{ $project: { "
      + "_id: \"$_id\", "
      + "title: \"$title\", "
      + "category: \"$category.name\", "
      + "courseType: \"$courseType\", "
      + "receivedCertificateDate: \"$certificate.receivedDate\", "
      + "progress: \"$runtimeData.progress\", "
      + "spentTime: \"$runtimeData.spentTime\", "
      + "views: \"$runtimeData.views\", "
      + "firstViewDate: \"$runtimeData.firstViewDate\", "
      + "lastViewDate: \"$runtimeData.lastViewDate\", "
      + "survey: \"$runtimeData.survey.survey\", "
      + "maxScore: \"$runtimeData.test.max\", "
      + "score: \"$runtimeData.test.raw\", "
      + "spentTimeOnTest: \"$runtimeData.test.spentTime\""
      + "} }";
}
