package mn.erin.lms.base.dms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.lms.base.domain.service.CodecService;

/**
 * @author Byambajav
 */
public class DmsCodecConverterService implements CodecService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DmsCodecConverterService.class);
  private static final String FFMPEG_FRAMEWORK = "ffmpeg";
  private static final String OVERWRITE = "-y";
  private static final String CONVERT_VIDEO = "-c:v";
  private static final String ENCODING_SPEED = "-preset";
  private static final String VERY_FAST = "veryfast";
  private static final String FFPROBE = "ffprobe";
  private static final String INPUT = "-i";
  private static final String H264_LIBRARY = "libx264";
  private static final String CONSTANT_RATE_FACTOR = "-crf";
  private static final String DEFAULT_RATE = "30";

  private static final Pattern DURATION_PATTERN = Pattern.compile("(?<=Duration: )[^,]*");
  private static final Pattern TIME_PATTERN = Pattern.compile("(?<=time=)[\\d:.]*");

  private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
  private static final Map<String, Double> PERCENTAGES = new ConcurrentHashMap<>();

  @Override
  public String videoProperty(String videoFile) throws IOException
  {
    List<String> command = new ArrayList<>();
    command.add(FFPROBE);
    command.add("-v");
    command.add("error");
    command.add("-select_streams");
    command.add("v:0");
    command.add("-show_entries");
    command.add("stream=codec_name");
    command.add("-of");
    command.add("default=noprint_wrappers=1:nokey=1");
    command.add(videoFile);
    Process process = new ProcessBuilder()
        .command(command)
        .redirectErrorStream(true)
        .start();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())))
    {
      return reader.readLine();
    }
    catch (InterruptedIOException e)
    {
      LOGGER.error("Error occurred detecting video codec. {}", e.getMessage());
      return "Interrupted";
    }
  }

  @Override
  public String convertToH264(String filePath, String fileId) throws IOException
  {
    return DmsCodecConverterService.converter(filePath, fileId);
  }

  public static String converter(String filePath, String fileId) throws IOException
  {
    String id = UUID.randomUUID().toString();
    String newFileName = id + ".mp4";
    List<String> command = new ArrayList<>();
    command.add(FFMPEG_FRAMEWORK);
    command.add(OVERWRITE);
    command.add(INPUT);
    command.add(filePath);
    command.add(CONVERT_VIDEO);
    command.add(H264_LIBRARY);
    command.add(ENCODING_SPEED);
    command.add(VERY_FAST);
    command.add(CONSTANT_RATE_FACTOR);
    command.add(DEFAULT_RATE);
    command.add(newFileName);
    PERCENTAGES.put(fileId, 0.0);
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    final Process process = processBuilder.start();

    EXECUTOR_SERVICE.execute(() -> {

      InputStream stream = process.getErrorStream();

      try (Scanner scanner = new Scanner(stream))
      {
        String durationOfVideo = scanner.findWithinHorizon(DURATION_PATTERN, 0);
        double totalDuration = getTimeInMillisecond(durationOfVideo);
        double match = getTimeInMillisecond(scanner.findWithinHorizon(TIME_PATTERN, 0));

        while (match <= totalDuration)
        {
          double percentage = match / totalDuration * 100;
          PERCENTAGES.put(fileId, percentage);
          match = getTimeInMillisecond(scanner.findWithinHorizon(TIME_PATTERN, 0));
          if (!process.isAlive())
          {
            PERCENTAGES.put(fileId, 100.0);
          }
          if (Math.round(percentage) == 100.0)
          {
            return;
          }
        }
      }
    });
    return newFileName;
  }

  @Override
  public double getPercentage(String id)
  {
    long percentage = Math.round(PERCENTAGES.get(id));
    if (percentage == 100.0)
    {
      PERCENTAGES.remove(id);
      return 100.0;
    }
    else
    {
      return percentage;
    }
  }

  private static double getTimeInMillisecond(String time)
  {
    if (time != null && !time.isEmpty())
    {
      String[] hourMinuteSecond = time.split(":");
      return Integer.parseInt(hourMinuteSecond[0]) * 3600
          + Integer.parseInt(hourMinuteSecond[1]) * 60
          + Double.parseDouble(hourMinuteSecond[2]);
    }
    else
    {
      return 0;
    }
  }
}
