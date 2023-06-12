package mn.erin.lms.legacy.infrastructure.unitel.rest.report;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mn.erin.common.datetime.DateTimeUtils;
import mn.erin.common.excel.ExcelTableDataConverter;
import mn.erin.common.excel.ExcelWriterUtil;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseGroupRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.service.CourseReportService;
import mn.erin.lms.legacy.domain.lms.usecase.report.CourseReportResult;
import mn.erin.lms.legacy.domain.unitel.service.PromoExcelReportGenerator;
import mn.erin.lms.legacy.domain.unitel.service.PromotionReportExcelTableDataConverter;
import mn.erin.lms.legacy.domain.unitel.usecase.DateFilter;
import mn.erin.lms.legacy.domain.unitel.usecase.report.GeneratePromoExcelReport;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Promo Report")
@RequestMapping(value = "/legacy/lms/promo-report")
public class PromotionReportRestApi
{
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
  private static final Logger LOGGER = LoggerFactory.getLogger(PromotionReportRestApi.class);
  private static final String DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss";

  private static final String[] PROMOTION_REPORT_HEADERS = {
      "№", "Код", "Гарчиг", "Үүсгэсэн огноо", "Үүсгэсэн админ", "Үзсэн хувь", "Үзсэн тоо", "Сорилтой эсэх", "Асуултын тоо", "Дундаж оноо", "Нийт элсэлт",
      "Асуулгын тоо"
  };

  @Inject
  private CourseCategoryRepository courseCategoryRepository;

  @Inject
  private CourseRepository courseRepository;

  @Inject
  private CourseReportService courseReportService;

  @Inject
  private PromoExcelReportGenerator promoExcelReportGenerator;

  @Inject
  private AccessIdentityManagement accessIdentityManagement;

  @Inject
  private CourseGroupRepository courseGroupRepository;

  @Inject
  private GroupRepository groupRepository;

  @Inject
  private MembershipRepository membershipRepository;

  private final ExcelTableDataConverter<List<CourseReportResult>> excelTableDataConverter = new PromotionReportExcelTableDataConverter();

  @ApiOperation("Download Promo Excel Report")
  @GetMapping("/promo-excel")
  public ResponseEntity read(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate)
  {
    Date startDateFilter = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    Date endDateFilter = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

    DateFilter dateFilter = new DateFilter(startDateFilter, endDateFilter);
    GeneratePromoExcelReport generatePromoExcelReport = new GeneratePromoExcelReport(promoExcelReportGenerator, courseCategoryRepository,
        courseRepository, accessIdentityManagement, courseGroupRepository, groupRepository, membershipRepository);

    try
    {
      byte[] excel = generatePromoExcelReport.execute(dateFilter);
      ByteArrayResource resource = new ByteArrayResource(excel);
      return ResponseEntity.ok()
          .contentLength(resource.contentLength())
          .contentType(MediaType.parseMediaType(ExcelWriterUtil.EXCEL_MEDIA_TYPE))
          .header("Content-Disposition", "attachment; filename=\"Promo-Report_" +
              DateTimeUtils.getCurrentLocalDateTime(DATE_FORMAT) + ".xlsx\"")
          .body(resource);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Download Promotion Report Table Excel")
  @PostMapping("/download-promotion-report")
  public ResponseEntity download(
      @RequestBody List<CourseReportResult> body
  )
  {
    LocalDateTime dateTime = LocalDateTime.now();
    String currentFormattedDate = DateTimeUtils.getCurrentLocalDateTime(DateTimeUtils.LONG_ISO_DATE_FORMAT);
    String sheetName = ("Татаж авсан: " + dateTime.format(dateFormatter));
    String sheetTitle = ("Урамшууллын тайлан" + "\n" + sheetName);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    Object[][] excelData = excelTableDataConverter.convert(body);
    ExcelWriterUtil.write(false, sheetTitle, PROMOTION_REPORT_HEADERS, excelData, os);

    ByteArrayResource resource = new ByteArrayResource(os.toByteArray());
    return ResponseEntity.ok()
        .contentLength(resource.contentLength())
        .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .header("Content-Disposition", "attachment; filename=\"Promotion-Report-" + currentFormattedDate + ".xlsx\"")
        .body(resource);
  }
}
