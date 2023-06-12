package mn.erin.domain.aim.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mLkhagvasuren
 */
public class AimTemporaryFileProviderImpl implements AimTemporaryFileProvider
{
  private static final Logger LOG = LoggerFactory.getLogger(AimTemporaryFileProviderImpl.class);

  @Override
  public File create(String name, byte[] content)
  {
    try
    {
      return Files.write(Paths.get(name), content).toFile();
    }
    catch (IOException e)
    {
      LOG.error("Failed to store temporary file", e);
      return null;
    }
  }
}
