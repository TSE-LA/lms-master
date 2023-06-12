package mn.erin.lms.base.domain.service;

import java.io.File;

/**
 * @author mLkhagvasuren
 */
public interface TemporaryFileApi
{
  File createTempFile(String name);

  File store(String name, byte[] content);
}
