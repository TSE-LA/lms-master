package mn.erin.lms.base.domain.service;

/**
 * @author Byambajav
 */

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import mn.erin.common.formats.ImageExtension;
import mn.erin.common.pdf.Section;
import mn.erin.lms.base.domain.usecase.content.dto.SectionDto;

public final class PdfSplitService
{

  private PdfSplitService()
  {
  }

  private static final Map<String, Double> PERCENTAGES = new ConcurrentHashMap<>();
  private static final Map<String, Map<String, SectionDto>> COMPLETED_SECTION_DTO = new ConcurrentHashMap<>();
  private static final Map<String, Map<String, Section>> COMPLETED_SECTION = new ConcurrentHashMap<>();

  public static void splitPdfToImage(byte[] imagePayload, ImageExtension imageExtension, String courseId, String courseType,
      TemporaryFileApi temporaryFileApi) throws IOException
  {
    List<File> result = new ArrayList<>();
    PDDocument document = PDDocument.load(imagePayload);
    PDFRenderer renderer = new PDFRenderer(document);
    int pageNumber = document.getNumberOfPages();
    for (int page = 0; page < pageNumber; page++)
    {
      BufferedImage image = renderer.renderImageWithDPI(page, 200);
      File tempImageFile = temporaryFileApi.createTempFile((page + 1) + "_converted" + "." + imageExtension.name());
      ImageIO.write(image, imageExtension.name(), tempImageFile);
      result.add(tempImageFile);
      PERCENTAGES.put(courseId, calculatePercentage(pageNumber, page + 1));
    }

    document.close();
    if (courseType.equals("onlineCourse"))
    {
      toSectionDto(result.stream().sorted(Comparator.comparingInt(image -> Integer.parseInt(image.getName().split("_")[0])))
          .collect(Collectors.toList()), imageExtension.name(), courseId);
    }
    else
    {
      toSection(result.stream().sorted(Comparator.comparingInt(image -> Integer.parseInt(image.getName().split("_")[0])))
          .collect(Collectors.toList()), imageExtension.name(), courseId);
    }
  }

  private static double calculatePercentage(int pageNumber, int currentPage)
  {
    int result;
    result = (currentPage * 100) / pageNumber;
    return result;
  }

  public static double getPercentage(String id)
  {
    if (PERCENTAGES.get(id) != null)
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
    else
    {
      return 0.0;
    }
  }

  public static Map<String, Section> getSection(String courseId)
  {
    return COMPLETED_SECTION.get(courseId);
  }

  public static Map<String, SectionDto> getSectionDto(String courseId)
  {
    return COMPLETED_SECTION_DTO.get(courseId);
  }

  private static void toSectionDto(List<File> images, String imageExtension, String courseId)
  {
    Map<String, SectionDto> output = new HashMap<>();

    for (int index = 0; index < images.size(); index++)
    {
      String uuid = UUID.randomUUID().toString();
      output.put(uuid + "." + imageExtension, new SectionDto(index + 1, images.get(index).toPath()));
    }
    COMPLETED_SECTION_DTO.put(courseId, output);
  }

  private static void toSection(List<File> images, String imageExtension, String courseId)
  {
    Map<String, Section> output = new HashMap<>();

    for (int index = 0; index < images.size(); index++)
    {
      String uuid = UUID.randomUUID().toString();
      int pageNumber = index + 1;
      File image = images.get(index);
      output.put(uuid + "." + imageExtension, new Section(pageNumber, image));
    }
    COMPLETED_SECTION.put(courseId, output);
  }
}
