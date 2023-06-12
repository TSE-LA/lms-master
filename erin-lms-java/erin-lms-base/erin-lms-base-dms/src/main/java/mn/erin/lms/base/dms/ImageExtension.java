package mn.erin.lms.base.dms;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Temuulen Naranbold
 */
public enum ImageExtension
{
  GIF,
  JPEG,
  JPG,
  PNG;

  public static boolean isImageExtension(String extensionName)
  {
    for (ImageExtension extension : values())
    {
      if (StringUtils.equals(extension.name(), extensionName))
      {
        return true;
      }
    }
    return false;
  }
}
