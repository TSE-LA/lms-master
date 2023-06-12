package mn.erin.domain.aim.service;

import java.io.File;

/**
 * @author mLkhagvasuren
 */
public interface AimTemporaryFileProvider
{
  File create(String name, byte[] content);
}
