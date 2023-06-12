package mn.erin.lms.base.domain.usecase.content;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.Validate;

import mn.erin.common.formats.ImageExtension;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.service.PdfSplitService;
import mn.erin.lms.base.domain.service.TemporaryFileApi;
import mn.erin.lms.base.domain.usecase.content.dto.SplitPdfToImagesInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Author.class })
public class SplitPdfToImages implements UseCase<SplitPdfToImagesInput, Boolean>
{
  private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
  private final TemporaryFileApi temporaryFileApi;

  public SplitPdfToImages(TemporaryFileApi temporaryFileApi)
  {
    this.temporaryFileApi = temporaryFileApi;
  }

  @Override
  public Boolean execute(SplitPdfToImagesInput input) throws UseCaseException
  {
    Validate.notNull(input);
    EXECUTOR_SERVICE.execute(() -> {
      try
      {
        PdfSplitService.splitPdfToImage(input.getImagePayload(), ImageExtension.valueOf(input.getImageExtension().name()), input.getCourseId(), input.getCourseType(), temporaryFileApi);
      }
      catch (IOException ignored)
      {
        return;
      }
    });

    return true;
  }
}
