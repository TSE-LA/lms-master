package mn.erin.lms.base.analytics.repository.mongo.utils;

import java.util.Collection;
import java.util.Objects;

/**
* @author Munkh
*/
public class ArrayStringConverter {
  private ArrayStringConverter()
  {}

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
    for (int i=0; i<array.length; i++)
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
