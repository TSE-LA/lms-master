package mn.erin.lms.legacy.infrastructure.unitel.report;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.common.datetime.DateTimeUtils;
import mn.erin.lms.legacy.domain.lms.model.course.CourseNote;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseOutput;
import mn.erin.lms.legacy.domain.unitel.PromotionConstants;
import mn.erin.lms.legacy.domain.unitel.model.PromotionState;
import mn.erin.lms.legacy.domain.unitel.service.PromoExcelReportGenerator;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class PromoReportExcelGeneratorImpl implements PromoExcelReportGenerator
{
  private static final Logger LOGGER = LoggerFactory.getLogger(PromoReportExcelGeneratorImpl.class);

  private static final String MAIN_TITLE = "ПРОМО ТАЙЛАН";
  private static final String CURRENT_PROMO_TITLE = "Одоо хэрэгжиж байгаа урамшуулал";
  private static final String MAIN_PROMO_TITLE = "Үндсэн үйлчилгээний урамшуулал";
  private static final String EXPIRED_PROMO_TITLE = "Хугацаа нь дууссан урамшуулал";
  private static final String NEW_ADDED_PROMO_TITLE = "Шинээр нэмэгдсэн урамшуулал";
  private static final String MODIFIED_PROMO_TITLE = "Өөрчлөлт орсон урамшуулал";

  private final SimpleDateFormat formatter = new SimpleDateFormat(DateTimeUtils.FULL_ISO_DATE_FORMAT);
  private final DateFormat shortDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
  private SXSSFSheet sheet;
  private SXSSFRow row;
  private int rowNum;
  private int lastCol;
  private XSSFCellStyle promoTypeStyle;

  @Override
  @SuppressWarnings("unchecked")
  public byte[] generatePromoExcelReport(Object data, String[] headers, Date startingDateRange, Date endingDateRange)
  {
    Map<String, List<GetCourseOutput>> categoryPromoRelation = (Map) data;

    try (SXSSFWorkbook workbook = new SXSSFWorkbook())
    {
      workbook.setCompressTempFiles(true);
      sheet = workbook.createSheet("Promo Report");
      sheet.setRandomAccessWindowSize(10000);

      // Set print setup
      PrintSetup printSetup = sheet.getPrintSetup();
      printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);
      printSetup.setLandscape(true);
      printSetup.setFitWidth((short) 1);
      printSetup.setFitHeight((short) 0);
      sheet.setFitToPage(true);

      lastCol = headers.length - 1;

      sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, lastCol));
      sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));
      sheet.addMergedRegion(new CellRangeAddress(1, 1, 4, lastCol));
      sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, lastCol));

      rowNum = 1;

      row = sheet.createRow(rowNum);

      Font boldWhiteFont = workbook.createFont();
      boldWhiteFont.setColor(IndexedColors.WHITE.getIndex());
      boldWhiteFont.setBold(true);
      // Promo report title
      SXSSFCell promoReportTitle = row.createCell(0);
      promoReportTitle.setCellValue(MAIN_TITLE);
      CellStyle promoReportTitleStyle = workbook.createCellStyle();
      promoReportTitleStyle.setAlignment(HorizontalAlignment.CENTER);
      promoReportTitleStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
      promoReportTitleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      promoReportTitleStyle.setFont(boldWhiteFont);
      promoReportTitle.setCellStyle(promoReportTitleStyle);

      // Promo report date on the first row
      SXSSFCell date = row.createCell(4);
      date.setCellValue(DateTimeUtils.getCurrentLocalDateTime("yyyy-MM-dd"));
      CellStyle dateCellStyle = workbook.createCellStyle();
      dateCellStyle.setAlignment(HorizontalAlignment.RIGHT);
      dateCellStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
      dateCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      dateCellStyle.setFont(boldWhiteFont);
      date.setCellStyle(dateCellStyle);

      row = sheet.createRow(++rowNum);
      SXSSFCell dateInterval = row.createCell(0);
      String startingDateRangeStr = DateTimeUtils.toIsoString(startingDateRange, DateTimeUtils.SHORT_ISO_DATE_FORMAT);
      String endingDateRangeStr = DateTimeUtils.toIsoString(endingDateRange, DateTimeUtils.SHORT_ISO_DATE_FORMAT);
      dateInterval.setCellValue("Хугацааны интервал: " + startingDateRangeStr + " - " + endingDateRangeStr);

      XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
      headerStyle.setAlignment(HorizontalAlignment.CENTER);
      headerStyle.setFillForegroundColor(new XSSFColor(new Color(72, 146, 166)));
      headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      Font whiteFont = workbook.createFont();
      whiteFont.setColor(IndexedColors.WHITE.getIndex());
      headerStyle.setFont(whiteFont);

      // Headers
      row = sheet.createRow(++rowNum);
      for (int colNum = 0; colNum < headers.length; colNum++)
      {
        sheet.trackColumnForAutoSizing(colNum);
        SXSSFCell cell = row.createCell(colNum);
        cell.setCellStyle(headerStyle);
        cell.setCellValue(headers[colNum]);
        sheet.autoSizeColumn(colNum);
      }

      promoTypeStyle = (XSSFCellStyle) workbook.createCellStyle();
      promoTypeStyle.setAlignment(HorizontalAlignment.CENTER);
      promoTypeStyle.setFillForegroundColor(new XSSFColor(new Color(127, 184, 199)));
      promoTypeStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

      createEmptySpace();
      createPromoHeader();

      Map<String, List<GetCourseOutput>> filteredPromotionsByStartDate = filterPromotionsByStartDate(startingDateRange, categoryPromoRelation);

      long startTime = System.currentTimeMillis();
      // Current promo report table
      SXSSFCell currentPromo = row.createCell(0);
      currentPromo.setCellValue(CURRENT_PROMO_TITLE);
      currentPromo.setCellStyle(promoTypeStyle);
      createReportTable(workbook, PromotionState.CURRENT.name().toUpperCase(), filteredPromotionsByStartDate);

      createEmptySpace();
      createPromoHeader();

      LOGGER.warn("Current promo report table: {}", System.currentTimeMillis() - startTime);

      startTime = System.currentTimeMillis();
      // Main promo report table
      SXSSFCell mainPromo = row.createCell(0);
      mainPromo.setCellValue(MAIN_PROMO_TITLE);
      mainPromo.setCellStyle(promoTypeStyle);
      createReportTable(workbook, PromotionState.MAIN.name().toUpperCase(), filteredPromotionsByStartDate);

      createEmptySpace();
      createPromoHeader();

      LOGGER.warn("Main promo report table: {}", System.currentTimeMillis() - startTime);

      startTime = System.currentTimeMillis();
      // Expired promo report table
      SXSSFCell expiredPromo = row.createCell(0);
      expiredPromo.setCellValue(EXPIRED_PROMO_TITLE);
      expiredPromo.setCellStyle(promoTypeStyle);
      createReportTable(workbook, PromotionState.EXPIRED.name().toUpperCase(), filteredPromotionsByStartDate);

      createEmptySpace();
      createPromoHeader();

      LOGGER.warn("Expired promo report table: {}", System.currentTimeMillis() - startTime);

      Map<String, List<GetCourseOutput>> filteredPromotionsByCreatedDate = filterPromotionsByCreatedDate(startingDateRange, endingDateRange,
          categoryPromoRelation);

      startTime = System.currentTimeMillis();
      // Newly added promo report table
      SXSSFCell newlyAdded = row.createCell(0);
      newlyAdded.setCellValue(NEW_ADDED_PROMO_TITLE);
      newlyAdded.setCellStyle(promoTypeStyle);
      createReportTable(workbook, filteredPromotionsByCreatedDate);

      createEmptySpace();
      createPromoHeader();

      LOGGER.warn("Newly added promo report table: {}", System.currentTimeMillis() - startTime);

      startTime = System.currentTimeMillis();
      // Modified promo report table
      SXSSFCell modifiedPromo = row.createCell(0);
      modifiedPromo.setCellValue(MODIFIED_PROMO_TITLE);
      modifiedPromo.setCellStyle(promoTypeStyle);
      createReportTable(workbook, filterModifiedPromoRelation(filteredPromotionsByCreatedDate));

      createEmptySpace();

      LOGGER.warn("Modified promo report table: {}", System.currentTimeMillis() - startTime);

      try (ByteArrayOutputStream os = new ByteArrayOutputStream())
      {
        workbook.write(os);
        return os.toByteArray();
      }
    }
    catch (IOException | ParseException e)
    {
      return new byte[0];
    }
  }

  private void createReportTable(SXSSFWorkbook xssfWorkbook, String state, Map<String, List<GetCourseOutput>> categoryPromoRelation) throws ParseException
  {
    Map<String, List<GetCourseOutput>> currentPromoCategoryPromoRelation = getFilteredCategoryPromoRelation(state,
        categoryPromoRelation);
    createReportTable(xssfWorkbook, currentPromoCategoryPromoRelation);
  }

  private void createReportTable(SXSSFWorkbook xssfWorkbook, Map<String, List<GetCourseOutput>> categoryPromoRelation) throws ParseException
  {
    SXSSFCell currentPromoCount = row.createCell(4);
    currentPromoCount.setCellValue(getCategoryPromoRelationCount(categoryPromoRelation));
    currentPromoCount.setCellStyle(promoTypeStyle);

    XSSFCellStyle categoryStyle = (XSSFCellStyle) xssfWorkbook.createCellStyle();
    categoryStyle.setFillForegroundColor(new XSSFColor(new Color(191, 219, 227)));
    categoryStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    categoryStyle.setAlignment(HorizontalAlignment.CENTER);

    XSSFCellStyle promotionStyle = (XSSFCellStyle) xssfWorkbook.createCellStyle();
    promotionStyle.setFillForegroundColor(new XSSFColor(new Color(218, 227, 243)));
    promotionStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    promotionStyle.setWrapText(true);

    for (Map.Entry<String, List<GetCourseOutput>> entry : categoryPromoRelation.entrySet())
    {
      row = sheet.createRow(++rowNum);
      SXSSFCell cell = row.createCell(0);
      cell.setCellValue(entry.getValue().size());
      cell.setCellStyle(categoryStyle);

      cell = row.createCell(1);
      cell.setCellValue(entry.getKey());
      cell.setCellStyle(categoryStyle);

      cell = row.createCell(2);
      cell.setCellStyle(categoryStyle);


      int initialRowForCategory = rowNum;
      for (int index = 0; index < entry.getValue().size(); index++)
      {
        row = sheet.createRow(++rowNum);
        GetCourseOutput promotion = entry.getValue().get(index);
        Map<String, Object> promotionProperties = promotion.getProperties();

        cell = row.createCell(0);
        cell.setCellStyle(promotionStyle);

        // Set promotion title
        cell = row.createCell(2);
        cell.setCellStyle(promotionStyle);
        cell.setCellValue(promotion.getTitle());

        // Set promotion code
        cell = row.createCell(3);
        cell.setCellStyle(promotionStyle);
        cell.setCellValue((String) promotionProperties.get(PromotionConstants.PROPERTY_CODE));

        // Set promotion start date
        cell = row.createCell(4);
        cell.setCellStyle(promotionStyle);
        Date startDate;
        Object startDateBeforeParse = promotionProperties.get(PromotionConstants.PROPERTY_START_DATE);
        Object endDateBeforeParse = promotionProperties.get(PromotionConstants.PROPERTY_END_DATE);
        try
        {
          if (startDateBeforeParse.toString().length() > 11)
          {
            startDate = formatter.parse((String) promotionProperties.get(PromotionConstants.PROPERTY_START_DATE));
          }
          else if (startDateBeforeParse.toString().length() > 5)
          {
            startDate = shortDateFormatter.parse((String) promotionProperties.get(PromotionConstants.PROPERTY_START_DATE));
          }
          else
          {
            throw new ParseException("startDate could not be parsed!", 0);
          }
          cell.setCellValue(DateTimeUtils.toIsoString(startDate, DateTimeUtils.SHORT_ISO_DATE_FORMAT));
        }
        catch (ParseException e)
        {
          cell.setCellValue("-");
        }

        // Set promotion end date, if any
        cell = row.createCell(10);

        if (promotionProperties.containsKey(PromotionConstants.PROPERTY_END_DATE))
        {
          Date endDate;
          try
          {
            if (endDateBeforeParse.toString().length() > 11)
            {
              endDate = formatter.parse((String) promotionProperties.get(PromotionConstants.PROPERTY_END_DATE));
            }
            else if (endDateBeforeParse.toString().length() > 5)
            {
              endDate = shortDateFormatter.parse((String) promotionProperties.get(PromotionConstants.PROPERTY_END_DATE));
            }
            else
            {
              throw new ParseException("endDate could not be parsed!", 0);
            }
            cell.setCellValue(DateTimeUtils.toIsoString(endDate, DateTimeUtils.SHORT_ISO_DATE_FORMAT));
          }
          catch (ParseException e)
          {
            cell.setCellValue("-");
          }
        }
        else
        {
          cell.setCellValue("-");
        }
        cell.setCellStyle(promotionStyle);

        // Set promotion target
        cell = row.createCell(6);
        cell.setCellStyle(promotionStyle);
        cell.setCellValue((String) promotion.getProperties().get(PromotionConstants.PROPERTY_TARGET));

        // Set author
        cell = row.createCell(7);
        cell.setCellStyle(promotionStyle);
        cell.setCellValue(promotion.getAuthorId());

        // Set promotion description
        cell = row.createCell(8);
        cell.setCellStyle(promotionStyle);
        cell.setCellValue(promotion.getDescription());

        cell = row.createCell(9);
        cell.setCellStyle(promotionStyle);

        List<CourseNote> notes = promotion.getCourseNotes();

        StringBuilder builder = new StringBuilder();

        for (CourseNote note : notes)
        {
          builder.append(DateTimeFormatter.ISO_LOCAL_DATE.format(note.getDate()));
          builder.append(" : ");
          builder.append(note.getNote());
          builder.append("\n");
        }

        cell.setCellValue(builder.toString());

      }

      sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 2, lastCol));
      sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 1));
      sheet.setColumnWidth(8, 22222);
      sheet.setColumnWidth(9, 22222);
      if (initialRowForCategory != rowNum)
      {
        sheet.groupRow(initialRowForCategory + 1, rowNum);
        sheet.setRowGroupCollapsed(initialRowForCategory + 1, true);
      }
    }
  }

  private Map<String, List<GetCourseOutput>> filterPromotionsByCreatedDate(Date startDate, Date endDate,
      Map<String, List<GetCourseOutput>> categoryPromoRelation)
  {
    Map<String, List<GetCourseOutput>> filtered = new HashMap<>();

    for (Map.Entry<String, List<GetCourseOutput>> entry : categoryPromoRelation.entrySet())
    {
      List<GetCourseOutput> courses = entry.getValue().stream()
          .filter(course -> DateTimeUtils.compare(startDate, course.getCreatedDate()) <= 0 && DateTimeUtils.compare(endDate, course.getCreatedDate()) >= 0)
          .collect(Collectors.toList());
      filtered.put(entry.getKey(), courses);
    }

    return filtered;
  }

  private Map<String, List<GetCourseOutput>> filterPromotionsByStartDate(Date startDate, Map<String, List<GetCourseOutput>> categoryPromoRelation)
  {

    Map<String, List<GetCourseOutput>> filtered = new HashMap<>();

    for (Map.Entry<String, List<GetCourseOutput>> entry : categoryPromoRelation.entrySet())
    {
      List<GetCourseOutput> courses = entry.getValue().stream()
          .filter(course -> DateTimeUtils.compare(startDate, course.getCreatedDate()) > 0)
          .collect(Collectors.toList());
      filtered.put(entry.getKey(), courses);
    }

    return filtered;
  }

  private Map<String, List<GetCourseOutput>> filterModifiedPromoRelation(Map<String, List<GetCourseOutput>> categoryPromoRelation)
  {
    Map<String, List<GetCourseOutput>> filtered = new HashMap<>();

    for (Map.Entry<String, List<GetCourseOutput>> entry : categoryPromoRelation.entrySet())
    {
      List<GetCourseOutput> courses = entry.getValue().stream()
          .filter(course -> DateTimeUtils.compareWithPrecision(course.getModifiedDate(), course.getCreatedDate()) > 0)
          .collect(Collectors.toList());

      filtered.put(entry.getKey(), courses);
    }

    return filtered;
  }

  private Map<String, List<GetCourseOutput>> getFilteredCategoryPromoRelation(String state, Map<String, List<GetCourseOutput>> categoryPromoRelation)
  {
    Map<String, List<GetCourseOutput>> filtered = new HashMap<>();
    for (Map.Entry<String, List<GetCourseOutput>> entry : categoryPromoRelation.entrySet())
    {
      List<GetCourseOutput> courses = entry.getValue().stream()
          .filter(course -> course.getProperties().containsKey(PromotionConstants.PROPERTY_CODE) &&
              state.equals(course.getProperties().get(PromotionConstants.PROPERTY_STATE)))
          .collect(Collectors.toList());

      filtered.put(entry.getKey(), courses);
    }

    return filtered;
  }

  private int getCategoryPromoRelationCount(Map<String, List<GetCourseOutput>> categoryPromoRelation)
  {
    int count = 0;

    for (Map.Entry<String, List<GetCourseOutput>> entry : categoryPromoRelation.entrySet())
    {
      count += entry.getValue().size();
    }

    return count;
  }

  private void createPromoHeader()
  {
    row = sheet.createRow(++rowNum);
    sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 3));
    sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 4, lastCol));
  }

  private void createEmptySpace()
  {
    ++rowNum;
    sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, lastCol));
  }
}
