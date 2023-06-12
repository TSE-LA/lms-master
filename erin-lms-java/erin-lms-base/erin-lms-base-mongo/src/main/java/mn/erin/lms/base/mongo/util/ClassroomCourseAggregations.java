package mn.erin.lms.base.mongo.util;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import mn.erin.lms.base.domain.model.classroom_course.CalendarEvent;

/**
 * @author Galsan Bayart.
 */
public class ClassroomCourseAggregations
{

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

  public static final String STAGE_PROJECT_FOR_INSTRUCTOR = "{$project:{"
      + "date: {$dateFromString:{"
      + "dateString: \"$properties.date\"}}, "
      + "teacher: \"$properties.teacher\", "
      + "state: \"$properties.state\", "
      + "startTime: \"$properties.startTime\", "
      + "title: \"$title\", "
      + "id: \"$_id\""
      + "}}";

  public static final String STAGE_MATCH_FOR_INSTRUCTOR = "{$match:{"
      + "teacher: { $in : %s }, "
      + "date: {$gte: ISODate(\"%s\"), $lte: ISODate(\"%s\") }"
      + "}}";

  public static final  String STAGE_PROJECT_FOR_LEARNER = "{$project:{"
      + "_id: { $toString: \"$_id\" }, "
      + "date: {$dateFromString:{"
      + "dateString: \"$properties.date\"}}, "
      + "teacher: \"$properties.teacher\", "
      + "state: \"$properties.state\", "
      + "startTime: \"$properties.startTime\", "
      + "title: \"$title\", "
      + "id: \"$_id\""
      + "}}";

  public static final String STAGE_MATCH_FOR_LEARNER = "{$match:{"
      + "_id: { $in : %s }, "
      + "date: {$gte: ISODate(\"%s\"), $lte: ISODate(\"%s\") }, "
      + "teacher: { $exists : true }"
      + "}}";

  @NotNull
  public static CalendarEvent mapToCourseCalendar(Document document)
  {
    return new CalendarEvent(document.getString("title"), document.getString("state"), document.get("id").toString(),
        document.getString("startTime"), (Date) document.get("date"), "classroom-course");
  }

  public static String toArrayString(Collection<String> collection)
  {
    if (collection == null)
    {
      throw new NullPointerException("Converting collection cannot be null!");
    }

    collection.removeIf(Objects::isNull);

    if (collection.isEmpty())
    {
      return "[]";
    }

    String[] array = collection.toArray(new String[0]);
    String result = "[\"";
    for (int i = 0; i < array.length; i++)
    {
      if (array[i] == null)
      {
        continue;
      }
      result = result.concat(array[i]);
      result = result.concat("\"");
      // If not end of the array
      if (i != array.length - 1)
      {
        result = result.concat(", \"");
      }
    }
    result = result.concat("]");
    return result;
  }

}
