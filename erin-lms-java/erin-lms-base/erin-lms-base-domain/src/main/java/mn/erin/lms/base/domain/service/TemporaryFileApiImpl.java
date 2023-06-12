package mn.erin.lms.base.domain.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import de.thetaphi.forbiddenapis.SuppressForbidden;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import sun.security.action.GetPropertyAction;

/**
 * @author mLkhagvasuren
 */
@SuppressForbidden
public class TemporaryFileApiImpl implements TemporaryFileApi
{
  private static final Logger LOG = LoggerFactory.getLogger(TemporaryFileApiImpl.class);

  private static final Path TEMP_DIR_ROOT =
      Paths.get(GetPropertyAction.privilegedGetProperty("java.io.tmpdir"));

  @Override
  public File createTempFile(String name)
  {
    try
    {
      return Files.createTempFile(name, null).toFile();
    }
    catch (IOException e)
    {
      LOG.error("Failed to create temporary file", e);
      return null;
    }
  }

  @Override
  public File store(String name, byte[] content)
  {
    try
    {
      String prefix = FilenameUtils.getBaseName(name);
      String suffix = "." + FilenameUtils.getExtension(name);
      Path tempFile = Files.createTempFile(prefix, suffix);
      return Files.write(tempFile, content).toFile();
    }
    catch (IOException e)
    {
      LOG.error("Failed to store temporary file", e);
      return null;
    }
  }

  @Scheduled(cron = "0 0 3 * * *"/* every day at 03:15 */, zone = "Asia/Ulaanbaatar")
  public void cleanTemporaryDir()
  {
    try
    {
      FileUtils.cleanDirectory(TEMP_DIR_ROOT.toFile());
    }
    catch (IOException e)
    {
      LOG.warn("Failed to clean temporary dir", e);
    }
  }
}
