package mn.erin.lms.base.domain.usecase.content;

import java.io.IOException;

import mn.erin.lms.base.domain.service.CodecService;

/**
 * @author Byambajav
 */
public class CheckVideoCodec
{
  private final CodecService codecService;

  public CheckVideoCodec(CodecService codecService)
  {
    this.codecService = codecService;
  }

  public boolean execute(String input) throws IOException {
    try
    {
      String videoCodec = codecService.videoProperty(input);
      return videoCodec.equals("h264") || videoCodec.equals("vp8") || videoCodec.equals("vp9");
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return false;
    }
  }
}
