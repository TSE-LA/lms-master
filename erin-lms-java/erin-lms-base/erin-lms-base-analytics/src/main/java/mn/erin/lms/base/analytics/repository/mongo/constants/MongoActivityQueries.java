/*
 * (C)opyright, 2021, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.analytics.repository.mongo.constants;

/**
 * @author Munkh
 */
public class MongoActivityQueries
{
  private MongoActivityQueries()
  {}

  public static final String STAGE_MATCH_ACTIVITY = "{ $match: { "
      + "\"learnerId._id\": { $in: __learners__ }, "
      + "\"scormContentId._id\": { $in: __contents__ }, "
      + "scoName: { $nin: [ \"Үнэлгээний хуудас\", \"ТЕСТ\" ] }"
      + "} }";

  public static final String STAGE_GROUP_ACTIVITY = "{ $group: { "
      + "_id: { learnerId: \"$learnerId._id\", contentId: \"$scormContentId._id\" }, "
      + "progress: { $avg: { $convert: { input: \"$runtimeData.cmi,progress_measure.data\", to: \"double\" } } }, "
      + "spentTime: { $push: \"$runtimeData.cmi,total_time.data\" } "
      + "} }";

  public static final String STAGE_GROUP_BY_LEARNER_ACTIVITY = "{ $group: { "
      + "_id: \"$_id.learnerId\", "
      + "progress: { $avg: \"$progress\" }, "
      + "spentTime: { $push: \"$spentTime\" } "
      + "} }";

  public static final String STAGE_LOOKUP_SPENT_TIME_ACTIVITY = "{ $lookup: { "
      + ""
      + "} }";
}
