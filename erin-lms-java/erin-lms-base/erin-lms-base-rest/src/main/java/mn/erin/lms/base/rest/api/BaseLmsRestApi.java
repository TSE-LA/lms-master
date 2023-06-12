package mn.erin.lms.base.rest.api;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;

/**
 * @author Bat-Erdene Tsogoo.
 */
public abstract class BaseLmsRestApi
{
  protected final LmsRepositoryRegistry lmsRepositoryRegistry;
  protected final LmsServiceRegistry lmsServiceRegistry;

  protected BaseLmsRestApi(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    this.lmsRepositoryRegistry = Objects.requireNonNull(lmsRepositoryRegistry);
    this.lmsServiceRegistry = Objects.requireNonNull(lmsServiceRegistry);
  }

  protected File saveFile(MultipartFile file, String uuid) throws IOException
  {
    if (null == file || file.isEmpty())
    {
      throw new IllegalArgumentException("Attachment file cannot be null or empty!");
    }
    String filename = Objects.requireNonNull(file.getOriginalFilename());
    String formattedName = uuid.concat("uuid").concat(FilenameUtils.getBaseName(filename)).concat("uuid.").concat(FilenameUtils.getExtension(filename));
    return lmsServiceRegistry.getTemporaryFileApi().store(formattedName, file.getBytes());
  }
}
