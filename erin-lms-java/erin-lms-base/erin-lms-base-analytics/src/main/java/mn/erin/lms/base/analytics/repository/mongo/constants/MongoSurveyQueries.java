package mn.erin.lms.base.analytics.repository.mongo.constants;

/**
 * @author Munkh
 */
public class MongoSurveyQueries
{
  private MongoSurveyQueries()
  {
  }

  public static final String STAGE_PROJECT = "{ $project: { "
      + "_id: \"$_id\", "
      + "scoName: \"$scoName\", "
      + "contentId: \"$scormContentId._id\", "
      + "launchDate: { $dateFromString: { dateString: \"$runtimeData.erin,date,last_launch.data\", onError: null } }, "
      + "answer: \"$runtimeData.cmi,suspend_data.data\" "
      + "} }";

  public static final String STAGE_MATCH = "{ $match: { "
      + "scoName: \"Үнэлгээний хуудас\", "
      + "contentId: { $in: %s }, "
      + "launchDate: { $gte: ISODate(\"%s\"), $lte: ISODate(\"%s\") }"
      + "} }";

  public static final String STAGE_MATCH_WITH_CONTENT_ID = "{ $match: { "
      + "scoName: \"Үнэлгээний хуудас\", "
      + "contentId: \"%s\", "
      + "launchDate: { $gte: ISODate(\"%s\"), $lte: ISODate(\"%s\") }"
      + "} }";
}
