package mn.erin.lms.base.domain.usecase.system.dto;

import java.io.File;

public class SystemConfigInput
{
  private File file;
  private boolean isLogo;

  public File getFile()
  {
    return file;
  }

  public void setFile(File file)
  {
    this.file = file;
  }

  public boolean isLogo()
  {
    return isLogo;
  }

  public void setLogo(boolean logo)
  {
    isLogo = logo;
  }
}
