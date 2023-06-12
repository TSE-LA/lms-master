package mn.erin.aim.ldap;

/**
 * Source:
 * https://web.archive.org/web/20180226001713/http://www.developerscrappad.com/1109/windows/active-directory/java-ldap-jndi-2-ways-of-decoding-and-using-the-objectguid-from-windows-active-directory/
 */
public final class ObjectGuids
{
  private ObjectGuids()
  {
    /*ctor*/
  }

  public static String convertToSearchString(byte[] objectGUID)
  {
    StringBuilder result = new StringBuilder();
    for (byte b : objectGUID)
    {
      result.append("\\");
      result.append(prefixZeros((int) b & 0xFF));
    }
    return result.toString();
  }

  public static String convertToUUID(byte[] objectGUID)
  {
    StringBuilder displayStr = new StringBuilder();

    displayStr.append(prefixZeros((int) objectGUID[3] & 0xFF));
    displayStr.append(prefixZeros((int) objectGUID[2] & 0xFF));
    displayStr.append(prefixZeros((int) objectGUID[1] & 0xFF));
    displayStr.append(prefixZeros((int) objectGUID[0] & 0xFF));
    displayStr.append("-");
    displayStr.append(prefixZeros((int) objectGUID[5] & 0xFF));
    displayStr.append(prefixZeros((int) objectGUID[4] & 0xFF));
    displayStr.append("-");
    displayStr.append(prefixZeros((int) objectGUID[7] & 0xFF));
    displayStr.append(prefixZeros((int) objectGUID[6] & 0xFF));
    displayStr.append("-");
    displayStr.append(prefixZeros((int) objectGUID[8] & 0xFF));
    displayStr.append(prefixZeros((int) objectGUID[9] & 0xFF));
    displayStr.append("-");
    displayStr.append(prefixZeros((int) objectGUID[10] & 0xFF));
    displayStr.append(prefixZeros((int) objectGUID[11] & 0xFF));
    displayStr.append(prefixZeros((int) objectGUID[12] & 0xFF));
    displayStr.append(prefixZeros((int) objectGUID[13] & 0xFF));
    displayStr.append(prefixZeros((int) objectGUID[14] & 0xFF));
    displayStr.append(prefixZeros((int) objectGUID[15] & 0xFF));

    return displayStr.toString();
  }

  private static String prefixZeros(int value)
  {
    if (value <= 0xF)
    {
      StringBuilder sb = new StringBuilder("0");
      sb.append(Integer.toHexString(value));

      return sb.toString();
    }
    else
    {
      return Integer.toHexString(value);
    }
  }
}