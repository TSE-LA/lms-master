package mn.erin.lms.base.analytics.repository.mongo.utils;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

/**
 * @author Munkh
 */
public class QueryBuilder
{
  private static final String PLACE_HOLDER = "__key__";

  private QueryBuilder()
  {}

  private static String buildKey(String key)
  {
    return StringUtils.replace(PLACE_HOLDER, "key", key);
  }

  public static Document buildStage(String stage, Map<String, String> replacements)
  {
    for (Map.Entry<String, String> entry: replacements.entrySet())
    {
      stage = StringUtils.replace(stage, buildKey(entry.getKey()), entry.getValue());
    }

    return parseStage(stage);
  }

  public static Document parseStage(String stage)
  {
    return Document.parse(stage);
  }
}
