package mn.erin.lms.base.analytics.repository.mongo.constants;

/**
 * @author Munkh
 */
public class MongoStatisticQueries
{
  private MongoStatisticQueries()
  {}

  public static final String STAGE_STATISTICS_MATCH = "{ $match: { "
      + "\"scormContentId._id\": \"__contentId__\", "
      + "\"learnerId._id\": { $in: __enrolledLearners__ }, "
      + "scoName: { $nin: [ \"Үнэлгээний хуудас\", \"ТЕСТ\" ] }, "
      + "$expr: { $and: [ "
      + "{ $gte: [ "
      + "{ $dateFromString: { "
      + "dateString: \"$runtimeData.erin,date,initial_launch.data\","
      + "format: \"%Y-%m-%d %H:%M:%S\","
      + "onError: ISODate(\"__endDate__\") "
      + "} }, "
      + "ISODate(\"__startDate__\") "
      + "] }, "
      + "{ $lte: [ "
      + "{ $dateFromString: { "
      + "dateString: \"$runtimeData.erin,date,initial_launch.data\", "
      + "format: \"%Y-%m-%d %H:%M:%S\", "
      + "onError: ISODate(\"__startDate__\") "
      + "} }, "
      + "ISODate(\"__endDate__\") "
      + "] }, ] }, } }";

  public static final String STAGE_STATISTICS_GROUP = "{ $group: { "
      + "_id: \"$learnerId._id\", "
      + "contentId: { $first: \"$scormContentId._id\" }, "
      + "progress: { $avg: { $convert: { input: \"$runtimeData.cmi,progress_measure.data\", to: \"double\" } } }, "
      + "spentTime: { $push: \"$runtimeData.cmi,total_time.data\" }, "
      + "views: { $max: { $toInt: \"$runtimeData.cmi,interactions,_count.data\" } }, "
      + "firstViewDate: { $min: { $convert: { input: \"$runtimeData.erin,date,initial_launch.data\", to: \"date\", onError: null } } }, "
      + "lastViewDate: { $max: { $convert: { input: \"$runtimeData.erin,date,last_launch.data\", to: \"date\", onError: null } } }, "
      + "} }";

  public static final String STAGE_LOOKUP_STATISTICS_CERTIFICATE = "{ $lookup: { "
      + "from: \"mongoLearnerCertificate\", "
      + "let: { course_id: \"__courseId__\", learner_id: \"$_id\" }, "
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

  public static final String STAGE_LOOKUP_STATISTICS_SURVEY = "{ $lookup: { "
      + "from: \"SCORM\", "
      + "let: { content_id: \"$contentId\", learner_id: \"$_id\" }, "
      + "pipeline: [ { $match: { $and: [ "
      + "{ $expr: { $eq: [ \"$scormContentId._id\", \"$$content_id\" ] } }, "
      + "{ $expr: { $eq: [ \"$learnerId._id\", \"$$learner_id\" ] } }, "
      + "{ scoName: \"Үнэлгээний хуудас\" }, "
      + "{ \"runtimeData.cmi,suspend_data.data\": { $ne: \"unknown\" } } "
      + "] } }, { $project: { "
      + "data: \"$runtimeData.cmi,suspend_data.data\","
      + "spentTime: \"$runtimeData.cmi,total_time.data\" "
      + " } } ], "
      + "as: \"survey\" "
      + "} }";

  public static final String STAGE_LOOKUP_STATISTICS_TEST = "{ $lookup: { "
      + "from: \"SCORM\", "
      + "let: { content_id: \"$contentId\", learner_id: \"$_id\" }, "
      + "pipeline: [ { $match: { $and: [ "
      + "{ $expr: { $eq: [ \"$scormContentId._id\", \"$$content_id\" ] } }, "
      + "{ $expr: { $eq: [ \"$learnerId._id\", \"$$learner_id\" ] } }, "
      + "{ scoName: \"ТЕСТ\" }, "
      + "{ \"runtimeData.cmi,score,max.data\": { $ne: \"unknown\" } }, "
      + "{ \"runtimeData.cmi,score,raw.data\": { $ne: \"unknown\" } } "
      + "] } }, { $project: { "
      + "max: \"$runtimeData.cmi,score,max.data\", "
      + "raw: \"$runtimeData.cmi,score,raw.data\", "
      + "spentTime: \"$runtimeData.cmi,total_time.data\" "
      + " } } ], "
      + "as: \"test\" "
      + "} }";

  public static final String STAGE_STATISTICS_PROJECT = "{ $project: { "
      + "_id: \"$_id\", "
      + "progress: \"$progress\", "
      + "spentTime: \"$spentTime\", "
      + "views: \"$views\", "
      + "firstViewDate: { $dateToString: { format: \"%Y-%m-%d %H:%M:%S\", date: \"$firstViewDate\" } }, "
      + "lastViewDate: { $dateToString: { format: \"%Y-%m-%d %H:%M:%S\", date: \"$lastViewDate\" } }, "
      + "certificate: { $arrayElemAt: [ \"$certificate\", 0 ] }, "
      + "certificate: { $arrayElemAt: [ \"$certificate\", 0 ] }, "
      + "survey: { $arrayElemAt: [ \"$survey\", 0 ] }, "
      + "test: { $arrayElemAt: [ \"$test\", 0 ] } "
      + "} }";

  public static final String STAGE_STATISTICS_PROJECT_FINAL = "{ $project: { "
      + "_id: \"$_id\", "
      + "progress: \"$progress\", "
      + "spentTime: \"$spentTime\", "
      + "views: \"$views\", "
      + "firstViewDate: \"$firstViewDate\", "
      + "lastViewDate: \"$lastViewDate\", "
      + "receivedCertificateDate: { $dateToString: { format: \"%Y-%m-%d %H:%M:%S\", date: \"$certificate.receivedDate\" } }, "
      + "survey: \"$survey.data\","
      + "surveySpentTime: \"$survey.spentTime\", "
      + "maxScore: { $convert: { input: \"$test.max\", to: \"int\", onError: 0 } }, "
      + "score: { $convert: { input: \"$test.raw\", to: \"int\", onError: 0 } }, "
      + "spentTimeOnTest: \"$test.spentTime\" "
      + "} }";
}
