package mn.erin.lms.base.analytics.repository.mongo.constants;

/**
 * @author Munkh
 */
public class MongoCourseQueries
{
  private MongoCourseQueries()
  {}

  /**
   * publishStatus - String
   * startDate - LocalDateTime
   * endDate - LocalDateTime
   * categories - String array
   * courseDepartments
   * departmentId
   */
  public static final String STAGE_COURSE_MATCH = "{ $match: { "
      + "publishStatus: \"__publishStatus__\", "
      + "createdDate: { $gte: ISODate(\"__startDate__\"), $lte: ISODate(\"__endDate__\") }, "
      + "categoryId: { $in: __subCategories__ }, "
      + "$or: [ "
      + "{ courseDepartment: { $in: __subGroups__ } }, "
      + "{ assignedDepartments: { $in: __allGroups__ } } "
      + "] } }";

  /**
   * publishStatus
   * startDate
   * endDate
   * categories
   * courseType
   * courseDepartments
   * departmentId
   */
  public static final String STAGE_COURSE_MATCH_BY_TYPE = "{ $match: { "
      + "publishStatus: \"__publishStatus__\", "
      + "createdDate: { $gte: ISODate(\"__startDate__\"), $lte: ISODate(\"__endDate__\") }, "
      + "categoryId: { $in: __subCategories__ }, "
      + "courseType: \"__courseType__\""
      + "$or: [ "
      + "{ courseDepartment: { $in: __subGroups__ } }, "
      + "{ assignedDepartments: { $in: __allGroups__ } } "
      + "] } }";

  public static final String STAGE_COURSE_PROJECT = "{ $project: { "
      + "_id: { $toString: \"$_id\" }, "
      + "title: \"$title\", "
      + "categoryId: \"$categoryId\", "
      + "courseType: \"$courseType\", "
      + "courseContentId: \"$courseContentId\", "
      + "hasCertificate: \"$hasCertificate\" "
      + "} }";

  public static final String STAGE_LOOKUP_COURSE_CATEGORY_NAME = "{ $lookup: {"
      + "from: \"mongoCourseCategory\", "
      + "let: { category_id: \"$categoryId\" }, "
      + "pipeline: ["
      + "{ $match: { $expr: { $eq: [ { \"$toString\": \"$_id\" }, \"$$category_id\" ] } } }"
      + "], "
      + "as: \"courseCategory\""
      + "} }";

  public static final String STAGE_UNWIND_COURSE_CATEGORY_NAME = "{ $unwind: { "
      + "path: \"$courseCategory\", "
      + "preserveNullAndEmptyArrays: true "
      + "} }";

  // Collection - mongoCourseCategory
  /**
   * learners - array
   */
  public static final String STAGE_LOOKUP_COURSE_ENROLLMENT = "{ $lookup: { "
      + "from: \"mongoCourseEnrollment\", "
      + "let: { course_id: \"$_id\" }, "
      + "pipeline: [ "
      + "{ $match: { $and: [ "
      + "{$expr: { $eq: [ \"$courseId\", \"$$course_id\" ] } }, "
      + "{$expr: { $in: [\"$learnerId\", __learners__ ] } } ] } }, "
      + "{ $project: { \"_id\": false, \"learnerId\": true } } "
      + "], "
      + "as: \"enrolledUsers\""
      + "} }";

  public static final String STAGE_UNWIND_COURSE_ENROLLMENT = "{ $unwind: { "
      + "path: \"$enrolledUsers\", "
      + "preserveNullAndEmptyArrays: true "
      + "} }";

  public static final String STAGE_COURSE_AND_COURSE_ENROLLMENT_GROUP = "{ $group: { "
      + "_id: \"$_id\", "
      + "title: { $first: \"$title\" }, "
      + "categoryName: { $first: \"$courseCategory.name\" }, "
      + "courseType: { $first: \"$courseType\" }, "
      + "courseContentId: { $first: \"$courseContentId\" }, "
      + "hasCertificate: { $first: \"$hasCertificate\" }, "
      + "learners: { $addToSet: \"$enrolledUsers.learnerId\" } "
      + "} }";

  public static final String STAGE_LOOKUP_RUNTIME_DATA = "{ $lookup: { "
      + "from: \"SCORM\", "
      + "let: { content_id: \"$courseContentId\", learners: \"$learners\" }, "
      + "pipeline: [ { $match: { $expr: { $and: [ "
      + "{ $eq: [ \"$scormContentId._id\", \"$$content_id\" ] }, "
      + "{ $in: [ \"$learnerId._id\", \"$$learners\" ] }, "
      + "{ $not: [ { $in: [ \"$scoName\", [ \"Үнэлгээний хуудас\", \"ТЕСТ\" ] ] } ] } "
      + "] } } }, "
      + "{ $group: { "
      + "_id: \"$learnerId._id\", "
      + "progress: { $avg: { $convert: { input: \"$runtimeData.cmi,progress_measure.data\", to: \"double\" } } }, "
      + "spentTime: { $push: \"$runtimeData.cmi,total_time.data\" }, "
      + "views: { $max: { $toInt: \"$runtimeData.cmi,interactions,_count.data\" } }, "
      + "firstViewDate: { $min: { $convert: { input: \"$runtimeData.erin,date,initial_launch.data\", to: \"date\", onError: null } } }, "
      + "lastViewDate: { $max: { $convert: { input: \"$runtimeData.erin,date,last_launch.data\", to: \"date\", onError: null } } }"
      + "} } ], as: \"runtimeData\" } }";

  public static final String STAGE_LOOKUP_SPENT_TIME_ON_TEST = "{ $lookup: { "
      + "from: \"SCORM\", "
      + "let: { content_id: \"$courseContentId\", learners: \"$learners\" }, "
      + "pipeline: [ { $match: { "
      + "$and: [ "
      + "{ $expr: { $eq: [ \"$scormContentId._id\", \"$$content_id\" ] } }, "
      + "{ $expr: { $in: [ \"$learnerId._id\", \"$$learners\" ] } }, "
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
      + "} }";

  public static final String STAGE_LOOKUP_COURSE_CERTIFICATE = "{ $lookup: { "
      + "from: \"mongoLearnerCertificate\", "
      + "let: { course_id: \"$_id\", learners: \"$learners\" }, "
      + "pipeline: [ "
      + "{ $match: { $expr: { $and: [ "
      + "{ $eq: [ \"$courseId\", \"$$course_id\" ] }, "
      + "{ $in: [ \"$learnerId\", \"$$learners\" ] } "
      + "] } } }, "
      + "{ $count: \"count\" "
      + "} ], "
      + "as: \"certificate\" "
      + "} }";

  public static final String STAGE_COURSE_PROJECT_FINAL = "{ $project: { "
      + "_id: { $toString: \"$_id\" }, "
      + "title: \"$title\", "
      + "categoryName: \"$categoryName\", "
      + "courseType: \"$courseType\", "
      + "courseContentId: \"$courseContentId\", "
      + "hasCertificate: \"$hasCertificate\", "
      + "learners: \"$learners\", "
      + "receivedCertificateCount: { $arrayElemAt: [ \"$certificate\", 0 ] }, "
      + "spentTimeOnTest: \"$test.spentTime\", "
      + "maxScore: \"$test.max\","
      + "score: \"$test.raw\""
      + "runtimeData: { $reduce: { "
      + "input: \"$runtimeData\", "
      + "initialValue: { \"totalProgress\": 0.0, \"viewersCount\": 0, \"repeatedViewersCount\": 0, \"completedViewersCount\": 0 }, "
      + "in: { "
      + "totalProgress: { $add: [ \"$$value.totalProgress\", \"$$this.progress\" ] }, "
      + "completedViewersCount: { $add: [ \"$$value.completedViewersCount\", { $cond: [ { $eq: [ \"$$this.progress\", 100 ] }, 1, 0 ] } ] }"
      + "viewersCount: { $add: [ \"$$value.viewersCount\", { $cond: [ { $gt: [ \"$$this.views\", 0 ] }, 1, 0 ] } ] }, "
      + "repeatedViewersCount: { $add: [ \"$$value.repeatedViewersCount\", { $cond: [ { $gt: [ \"$$this.views\", 1 ] }, 1, 0 ] } "
      + "] }, } } } } }";
}
