package mn.erin.lms.base.domain.usecase.content;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.service.PdfSplitService;

/**
 * @author Byambajav
 */
public class GetPdfSplitPercentage implements UseCase<String, SseEmitter>
{
  @Override
  public SseEmitter execute(String input) throws UseCaseException
  {
    ExecutorService nonBlockingService = Executors
        .newCachedThreadPool();
    SseEmitter emitter = new SseEmitter(15000L);
    nonBlockingService.execute(() -> {
      try
      {
        double percentage = PdfSplitService.getPercentage(input);
        emitter.send(percentage);
        emitter.complete();
        if (Math.round(percentage) == 100.0)
        {
          nonBlockingService.shutdownNow();
        }
        Thread.sleep(3000);
      }
      catch (Exception ex)
      {
        emitter.completeWithError(ex);
      }
    });

    return emitter;
  }
}
