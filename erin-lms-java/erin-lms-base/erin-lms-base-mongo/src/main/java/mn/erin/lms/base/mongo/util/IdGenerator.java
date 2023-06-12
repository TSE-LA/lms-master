package mn.erin.lms.base.mongo.util;

import java.util.Date;

import org.bson.types.ObjectId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public final class IdGenerator
{
  public static String generateId()
  {
    return new ObjectId(new Date()).toHexString();
  }
}
