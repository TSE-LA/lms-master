package mn.erin.lms.base.dms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.model.Content;
import mn.erin.domain.dms.model.document.DocumentContent;
import mn.erin.lms.base.domain.service.ImageService;
import mn.erin.lms.base.domain.util.GifUtil;

/**
 * @author Temuulen Naranbold
 */
public class DmsImageConverterService implements ImageService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DmsImageConverterService.class);
  private static final String FFMPEG_FRAMEWORK = "ffmpeg";
  private static final String OVERWRITE_YES = "-y";
  private static final String LOGGING_LEVEL = "-v";
  private static final String IGNORE_LOGGING = "fatal";
  private static final String START_TIME = "-ss";
  private static final String DURATION = "-t";
  private static final String INPUT_FILE = "-i";
  private static final String FRAME_PER_SECOND = "-r";
  private static final String FILTER_GRAPH = "-vf";
  private static final String QUANTITY_OF_OUTPUT_FRAME = "-vframes";
  private static final String COMPLEX_FILTER_GRAPH = "-filter_complex";
  private static final String RESOLUTION = "split [a][b];[a] palettegen [p];[b][p] paletteuse";
  private static final String BLACK_DETECTION = "blackframe=0,metadata=select:key=lavfi.blackframe.pblack:value=98:function=less";

  @Override
  public Content convertVideoToGIF(String fileName) throws IOException
  {
    Validate.notBlank(fileName);
    GifUtil property = new GifUtil(fileName);
    try
    {
      property.setOutput(convertFileExtension(fileName, ConversionType.GIF));
    }
    catch (IllegalArgumentException e)
    {
      throw new IllegalArgumentException(e.getMessage());
    }

    String fileExtension = FilenameUtils.getExtension(fileName);
    convert(commandBuilder(property, fileExtension, ConversionType.GIF));
    byte[] content = Files.readAllBytes(Paths.get(property.getOutput()));
    return new DocumentContent(content);
  }

  @Override
  public Content convertVideoToJPEG(String fileName) throws IOException
  {
    Validate.notBlank(fileName);
    GifUtil property = new GifUtil(fileName, 1024, 576);
    try
    {
      property.setOutput(convertFileExtension(fileName, ConversionType.JPEG));
    }
    catch (IllegalArgumentException e)
    {
      throw new IllegalArgumentException(e.getMessage());
    }

    String fileExtension = FilenameUtils.getExtension(fileName);
    convert(commandBuilder(property, fileExtension, ConversionType.JPEG));

    Path outputFile = Paths.get(property.getOutput());
    if (!Files.exists(outputFile))
    {
      convert(commandBuilder(property, fileExtension, ConversionType.JPEG_BLACK));
    }

    byte[] content = Files.readAllBytes(outputFile);
    return new DocumentContent(content);
  }

  private List<String> commandBuilder(GifUtil property, String extension, ConversionType type)
  {
    List<String> command = new ArrayList<>();

    command.add(FFMPEG_FRAMEWORK);
    command.add(OVERWRITE_YES);
    command.add(LOGGING_LEVEL);
    command.add(IGNORE_LOGGING);

    switch (type)
    {
    case GIF:
      if (!ImageExtension.isImageExtension(extension.toUpperCase()))
      {
        command.add(START_TIME);
        command.add(property.getStartTime());
        command.add(DURATION);
        command.add(property.getDuration());
      }
      command.add(INPUT_FILE);
      command.add(property.getName());
      command.add(FRAME_PER_SECOND);
      command.add(Integer.toString(property.getFps()));
      command.add(COMPLEX_FILTER_GRAPH);
      command.add(scaleBuilder(property) + "," + RESOLUTION);
      break;
    case JPEG:
      if (!ImageExtension.isImageExtension(extension.toUpperCase()))
      {
        command.add(START_TIME);
        command.add(property.getStartTime());
      }
      command.add(INPUT_FILE);
      command.add(property.getName());
      command.add(QUANTITY_OF_OUTPUT_FRAME);
      command.add(String.valueOf(property.getFrameQuantity()));
      command.add(FILTER_GRAPH);
      command.add(BLACK_DETECTION + "," + scaleBuilder(property));
      break;
    case JPEG_BLACK:
      if (!ImageExtension.isImageExtension(extension.toUpperCase()))
      {
        command.add(START_TIME);
        command.add(property.getStartTime());
      }
      command.add(INPUT_FILE);
      command.add(property.getName());
      command.add(QUANTITY_OF_OUTPUT_FRAME);
      command.add(String.valueOf(property.getFrameQuantity()));
      command.add(FILTER_GRAPH);
      command.add(scaleBuilder(property));
      break;
    default:
      throw new UnsupportedOperationException("Given type is not supported.");
    }

    command.add(property.getOutput());

    return command;
  }

  private void convert(List<String> command)
  {
    try
    {
      Process process = new ProcessBuilder()
          .command(command)
          .redirectErrorStream(true)
          .redirectOutput(ProcessBuilder.Redirect.INHERIT)
          .start();

      process.waitFor();
    }
    catch (InterruptedException | IOException e)
    {
      LOGGER.error("Error occurred during conversion. {}", e.getMessage());
      Thread.currentThread().interrupt();
    }
  }

  private String scaleBuilder(GifUtil property)
  {
    return "scale=w=" + property.getWidth()
        + ":h=" + property.getHeight()
        + ":force_original_aspect_ratio=decrease,pad="
        + property.getWidth() + ":"
        + property.getHeight()
        + ":(ow-iw)/2:(oh-ih)/2";
  }

  private String convertFileExtension(String fileName, ConversionType extension)
  {
    String convertedFile;
    try
    {
      convertedFile = Objects.requireNonNull(FilenameUtils.getBaseName(fileName).concat("." + extension.toString().toLowerCase()));
      return convertedFile;
    }
    catch (IllegalArgumentException e)
    {
      throw new IllegalArgumentException("Failed to update the file extension", e);
    }
  }
}
