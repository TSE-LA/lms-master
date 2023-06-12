package mn.erin.lms.base.domain.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bat-Erdene Tsogoo.
 */
public final class VideoCutter
{
  private static final Logger LOGGER = LoggerFactory.getLogger(VideoCutter.class);
  private static final int SKIP_SECONDS = 10;

  private VideoCutter()
  {
  }

  public static void cut(String videoFile)
  {
    List<String> argumentList = generateArgumentList(videoFile);
    executeVideoCutCommand(argumentList);
  }

  private static List<String> generateArgumentList( String videoFile)
  {
    List<String> argumentList = new ArrayList<>();

    argumentList.add("ffmpeg");
    argumentList.add("-ss");
    argumentList.add("00:00:00");
    argumentList.add("-i");
    argumentList.add(videoFile);
    argumentList.add("-t");
    argumentList.add("00:00:" + SKIP_SECONDS);
    argumentList.add("-c");
    argumentList.add("copy");
    argumentList.add(videoFile.replace(".mp4", "") + " - copy.mp4");

    return argumentList;
  }

  private static void executeVideoCutCommand(List<String> argumentList)
  {

    Process process = null;

    try
    {

      process = new ProcessBuilder()
          .command(argumentList)
          .redirectErrorStream(true)
          .redirectOutput(ProcessBuilder.Redirect.INHERIT)
          .start();

      process.waitFor();
    }
    catch (IOException | InterruptedException e)
    {
      LOGGER.error(e.getMessage(), e);
    }
  }
}
