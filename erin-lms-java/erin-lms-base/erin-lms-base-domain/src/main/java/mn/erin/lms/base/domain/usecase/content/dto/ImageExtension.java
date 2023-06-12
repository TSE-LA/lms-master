package mn.erin.lms.base.domain.usecase.content.dto;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public enum ImageExtension
{
  JPEG,
  JPG,
  PNG;
  public static boolean isSupported(String extension)
  {
    Validate.notBlank(extension, "Extension is required!");

    for (ImageExtension e : ImageExtension.values())
    {
      if (extension.equalsIgnoreCase(e.name()))
      {
        return true;
      }
    }
    return false;
  }
}
