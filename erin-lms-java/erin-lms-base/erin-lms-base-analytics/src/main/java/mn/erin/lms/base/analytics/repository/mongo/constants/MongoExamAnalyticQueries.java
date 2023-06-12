package mn.erin.lms.base.analytics.repository.mongo.constants;

/**
 * @author Byambajav
 */
public class MongoExamAnalyticQueries
{
  private MongoExamAnalyticQueries()
  {
  }

  // Collection - mongoExam
  /**
   * Fetching exams from mongoExam according to the request
   */
  public static final String STAGE_EXAM_MATCH = "{ $match: {"
      + "\"mongoExamConfig.startDate\": { $gte: ISODate(\"__startDate__\"), $lte: ISODate(\"__endDate__\") },"
      + "\"mongoExamConfig.endDate\": { $gte: ISODate(\"__startDate__\"), $lte: ISODate(\"__endDate__\") },"
      + "examCategoryId: { $in: __categories__ },"
      + "examGroupId: { $in: __groups__ },"
      + "examStatus: { $in: __statuses__ }, "
      + "examType: { $in: __types__ }, "
      + "}}";

  // Collection - mongoExamCategory
  /**
   * Fetching exam category for to get category name
   */
  public static final String STAGE_LOOKUP_EXAM_CATEGORY_NAME = "{ $lookup: {"
      + "from: \"mongoExamCategory\", "
      + "let: { category_id: \"$examCategoryId\" }, "
      + "pipeline: ["
      + "{ $match: { $expr: { $eq: [ { \"$toString\": \"$_id\" }, \"$$category_id\" ] } } }"
      + "], "
      + "as: \"examCategory\""
      + "} }";

  // Collection - mongoExamRuntimeData
  /**
   * Fetching runtime data for the exams, and calculating average statistics
   */
  public static final String STAGE_LOOKUP_EXAM_RUNTIME_DATA = "{ $lookup: {"
      + "from: \"mongoExamRuntimeData\", "
      + "let: { exam_id: \"$_id\"},"
      + "pipeline: ["
      + " { $match: { $expr: { $eq: [ \"$examId\", { $toString: \"$$exam_id\"} ] } } },"
      + " { $unwind: {"
      + " path: \"$examInteraction\""
      + " preserveNullAndEmptyArrays: true }},"
      + " { $group: { "
      + " _id: { learner: \"$learnerId\" },"
      + " examId:{ $first: \"$$exam_id\"},"
      + " maxScore: { $max: \"$examInteraction.score\"},"
      + " threshold: { $max: \"$thresholdScore\"},"
      + " maxSpentTime: {$max: \"$examInteraction.spentTime\"}"
      + " }},"
      + " { $group: { "
      + " _id: { examId: \"$examId\" },"
      + " totalRuntime: { $sum: 1},"
      + " passedCount: { $sum: { $cond: [ { $gte: [ \"$maxScore\", \"$threshold\" ] }, 1, 0 ] } }"
      + " averageScore: {$avg: \"$maxScore\"},"
      + " averageSpentTime: { $avg: \"$maxSpentTime\"}"
      + " }} ],"
      + "as: \"examRuntime\" } }";

  /**
   * Final mapping
   */
  public static final String STAGE_EXAM_PROJECT_FINAL = "{ $project: {"
      + "_id: { $toString: \"$_id\"},"
      + "title: \"$name\","
      + "status: \"$examStatus\","
      + "type: \"$examType\","
      + "categoryName: { $first: \"$examCategory.name\" },"
      + "duration: \"$mongoExamConfig.duration\","
      + "questionCount: {$ifNull: [{$toInt: \"$mongoExamConfig.totalQuestion\" }, {$toInt: 0}]},"
      + "enrollmentCount: { $cond: {if: {$isArray: \"$enrolledLearners\"}, then: {$size: \"$enrolledLearners\" }, else: 0} },"
      + "averageScore: {$ifNull: [{$toInt: {$first:  \"$examRuntime.averageScore\"} }, {$toInt: 0}]},"
      + "averageSpentTime:  {$ifNull: [{$toInt: {$first:\"$examRuntime.averageSpentTime\"}}, {$toInt: 0}]},"
      + "passedCount: {$ifNull: [{$toInt: {$first: \"$examRuntime.passedCount\"}}, {$toInt: 0}]},"
      + "totalRuntime:  {$ifNull: [{$toInt: {$first:\"$examRuntime.totalRuntime\"}} , {$toInt: 0}]},"
      + "maxScore: {$ifNull: [{$toInt: \"$mongoExamConfig.maxScore\"}, {$toInt: 0}]},"
      + "}}";
}
