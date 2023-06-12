package mn.erin.lms.base.rest.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import mn.erin.common.formats.VideoExtension;
import mn.erin.domain.base.model.Content;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.model.category.CourseCategory;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.CodecService;
import mn.erin.lms.base.domain.service.LmsFileSystemService;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.PdfSplitService;
import mn.erin.lms.base.domain.usecase.content.CheckVideoCodec;
import mn.erin.lms.base.domain.usecase.content.CreateCourseContent;
import mn.erin.lms.base.domain.usecase.content.DeleteCourseAttachment;
import mn.erin.lms.base.domain.usecase.content.GetCourseContent;
import mn.erin.lms.base.domain.usecase.content.GetModulePaths;
import mn.erin.lms.base.domain.usecase.content.GetPdfSplitPercentage;
import mn.erin.lms.base.domain.usecase.content.SplitPdfToImages;
import mn.erin.lms.base.domain.usecase.content.UpdateCourseAttachment;
import mn.erin.lms.base.domain.usecase.content.UpdateCourseContent;
import mn.erin.lms.base.domain.usecase.content.dto.CourseContentDto;
import mn.erin.lms.base.domain.usecase.content.dto.CourseContentInput;
import mn.erin.lms.base.domain.usecase.content.dto.DeleteCourseAttachmentInput;
import mn.erin.lms.base.domain.usecase.content.dto.ImageExtension;
import mn.erin.lms.base.domain.usecase.content.dto.ModuleInfo;
import mn.erin.lms.base.domain.usecase.content.dto.ModulePathDto;
import mn.erin.lms.base.domain.usecase.content.dto.SectionDto;
import mn.erin.lms.base.domain.usecase.content.dto.SectionInfo;
import mn.erin.lms.base.domain.usecase.content.dto.SplitPdfToImagesInput;
import mn.erin.lms.base.domain.usecase.content.dto.UpdateCourseAttachmentInput;
import mn.erin.lms.base.rest.model.RestCourseContent;
import mn.erin.lms.base.rest.model.RestFile;
import mn.erin.lms.base.rest.model.RestFileSection;
import mn.erin.lms.base.rest.model.RestPersistAttachment;
import mn.erin.lms.base.rest.model.RestSection;
import mn.erin.lms.base.rest.model.RestUpdateAttachment;

import static mn.erin.common.pdf.PdfUtil.getUUIdString;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Course Content REST API")
@RequestMapping(value = "/lms/courses", name = "Provides base LMS course content features")
@RestController
public class CourseContentRestApi extends BaseLmsRestApi
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CourseContentRestApi.class);

  private final Map<String, SectionDto> sectionFiles = new HashMap<>();
  private final Map<String, File> attachmentFiles = new HashMap<>();

  public CourseContentRestApi(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Inject
  private CodecService codecService;

  @ApiOperation("Creates a course content")
  @PostMapping("/{courseId}/course-content")
  public ResponseEntity<RestResult> create(@PathVariable String courseId,
      @RequestBody RestCourseContent body)
  {
    CreateCourseContent createCourseContent = new CreateCourseContent(lmsRepositoryRegistry, lmsServiceRegistry);
    CourseContentInput input = toInput(courseId, body);

    try
    {
      CourseContentDto output = createCourseContent.execute(input);
      return RestResponse.success(output);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Fetches a course content")
  @GetMapping("/{courseId}/course-content")
  public ResponseEntity<RestResult> readById(@PathVariable String courseId)
  {
    GetCourseContent getCourseContent = new GetCourseContent(lmsRepositoryRegistry.getCourseContentRepository());

    try
    {
      CourseContentDto output = getCourseContent.execute(courseId);
      return RestResponse.success(output);
    }
    catch (UseCaseException e)
    {
      return RestResponse.success();
    }
  }

  @ApiOperation("Updates a course content")
  @PutMapping("/{courseId}/course-content")
  public ResponseEntity<RestResult> update(@PathVariable String courseId,
      @RequestBody RestCourseContent body)
  {
    UpdateCourseContent updateCourseContent = new UpdateCourseContent(lmsRepositoryRegistry, lmsServiceRegistry);
    CourseContentInput input = toInput(courseId, body);

    try
    {
      CourseContentDto output = updateCourseContent.execute(input);
      return RestResponse.success(output);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Fetches course content file urls")
  @GetMapping("/{courseId}/course-content/files")
  public ResponseEntity<RestResult> fetchFiles(@PathVariable String courseId)
  {
    GetModulePaths getModulePaths = new GetModulePaths(lmsServiceRegistry.getLmsFileSystemService());
    List<ModulePathDto> result = getModulePaths.execute(courseId);
    return RestResponse.success(result);
  }

  @ApiOperation("Uploads a pdf file and splits it into images")
  @PostMapping("/course-content/split-pdf-to-images")
  public ResponseEntity<RestResult> uploadPdf(@RequestParam("file") MultipartFile multipartFile,
      @RequestParam("courseId") String courseId,
      @RequestParam("courseType") String courseType)
  {
    SplitPdfToImagesInput input;
    try
    {
      input = new SplitPdfToImagesInput(multipartFile.getBytes(), ImageExtension.JPEG, courseId, courseType);
    }
    catch (IOException e)
    {
      return RestResponse.internalError("Failed to read multipart form data");
    }

    SplitPdfToImages splitPdfToImages = new SplitPdfToImages(lmsServiceRegistry.getTemporaryFileApi());
    try
    {
      splitPdfToImages.execute(input);
      return RestResponse.success(true);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
    catch (IllegalArgumentException e1)
    {
      return RestResponse.badRequest(e1.getMessage());
    }
  }

  @ApiOperation("Get pdf split result")
  @GetMapping("/course-content/get-pdf-section")
  public ResponseEntity<RestResult> read(@RequestParam("courseId") String courseId)
  {
    String uuid = getUUIdString();
    Map<String, SectionDto> result = PdfSplitService.getSectionDto(courseId);
    sectionFiles.putAll(result);
    return RestResponse.success(createRestSection(uuid, result));
  }

  @ApiOperation("Uploads a video")
  @PostMapping("/course-content/video")
  public ResponseEntity<RestResult> uploadVideo(@RequestParam("file") MultipartFile multipartFile)
  {
    String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
    if (VideoExtension.isSupported(extension))
    {
      throw new IllegalArgumentException("Unsupported video format!");
    }

    try
    {
      CheckVideoCodec checkVideoCodec = new CheckVideoCodec(codecService);
      String uuid = getUUIdString();
      File file = saveFile(multipartFile, uuid);
      boolean codecSupported = checkVideoCodec.execute(file.getAbsolutePath());
      Map<String, SectionDto> videoSectionMap = new HashMap<>();
      videoSectionMap.put(uuid + "." + extension, new SectionDto(1, file.toPath()));
      if (codecSupported)
      {
        this.sectionFiles.putAll(videoSectionMap);
      }
      return RestResponse.success(createRestSection(uuid, videoSectionMap, file.getAbsolutePath(), codecSupported));
    }
    catch (IOException e)
    {
      return RestResponse.internalError("Failed to read the video!");
    }
  }

  @ApiOperation("Converts a video")
  @GetMapping("/course-content/convert")
  public ResponseEntity<RestResult> convertVideo(
      @RequestParam String filePath,
      @RequestParam String fileId)
  {
    try
    {
      String result = codecService.convertToH264(filePath, fileId);
      Path file = Paths.get(result);
      Map<String, SectionDto> videoSectionMap = new HashMap<>();
      videoSectionMap.put(result, new SectionDto(1, file));
      this.sectionFiles.putAll(videoSectionMap);
      return RestResponse.success(createRestSection(result, videoSectionMap));
    }
    catch (IOException e)
    {
      return RestResponse.internalError("Failed to read the video!");
    }
  }

  @ApiOperation("Gets converting percentage")
  @GetMapping("/course-content/get-percentage/{id}")
  public SseEmitter handleSse(@PathVariable String id)
  {
    ExecutorService nonBlockingService = Executors
        .newCachedThreadPool();
    SseEmitter emitter = new SseEmitter();
    nonBlockingService.execute(() -> {
      try
      {
        double percentage = codecService.getPercentage(id);
        emitter.send(percentage);
        emitter.complete();

        Thread.sleep(6000);

        if (Math.round(percentage) == 100.0)
        {
          nonBlockingService.shutdownNow();
        }
      }
      catch (Exception ex)
      {
        emitter.completeWithError(ex);
        nonBlockingService.shutdownNow();
        Thread.currentThread().getThreadGroup().interrupt();
      }
    });

    return emitter;
  }

  @ApiOperation("Gets pdf split percentage")
  @GetMapping("/course-content/get-pdf-percentage/{id}")
  public SseEmitter pdfSplitSse(@PathVariable String id)
  {
    GetPdfSplitPercentage getPdfSplitPercentage = new GetPdfSplitPercentage();
    SseEmitter emitter = new SseEmitter(15000L);
    try
    {
      emitter = getPdfSplitPercentage.execute(id);
    }
    catch (UseCaseException e)
    {
      e.printStackTrace();
    }
    return emitter;
  }

  @ApiOperation("Persists attachment")
  @PostMapping("/course-content/persist-attachment")
  public ResponseEntity<RestResult> persistAttachment(@RequestBody RestPersistAttachment body)
  {
    File file = this.attachmentFiles.get(body.getAttachmentId());
    lmsServiceRegistry.getLmsFileSystemService().uploadAttachment(body.getCourseId(), file);
    this.attachmentFiles.remove(body.getAttachmentId());
    return RestResponse.success();
  }

  @ApiOperation("Persists thumbnail")
  @PostMapping("/course-content/persist-thumbnail")
  public ResponseEntity<RestResult> persistThumbnail(@RequestBody RestPersistAttachment body)
  {
    File file = this.attachmentFiles.get(body.getAttachmentId());
    try
    {
      String newThumbnail = lmsServiceRegistry.getThumbnailService().updateThumbnailToGIF(body.getCourseId(), file);
      this.attachmentFiles.remove(body.getAttachmentId());
      return RestResponse.success(newThumbnail);
    }
    catch (IOException | IllegalArgumentException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Update all mp4 thumbnails to gif")
  @PutMapping("/course-content/update-old-thumbnails")
  public ResponseEntity<RestResult> updateOldThumbnails(@RequestParam String tenantId)
  {
    Collection<CourseCategory> courseCategories = lmsRepositoryRegistry.getCourseCategoryRepository()
        .listAll(OrganizationId.valueOf(tenantId), CourseCategoryId.valueOf("online-course"));
    List<Course> courses;
    int totalFixedCourses = 0;
    int totalCourses = 0;
    for (CourseCategory category : courseCategories)
    {
      courses = lmsRepositoryRegistry.getCourseRepository().listAll(category.getCourseCategoryId());
      for (Course course : courses)
      {
        if (course.getCourseDetail() != null &&
            course.getCourseDetail().getThumbnailUrl() != null &&
            course.getCourseDetail().getThumbnailUrl().endsWith(".mp4"))
        {
          File file = lmsServiceRegistry.getLmsFileSystemService().findThumbnail(course.getCourseId().getId(), "Thumbnail.mp4");
          if (file != null)
          {
            lmsServiceRegistry.getThumbnailService().updateThumbnailToGIF(course, "Thumbnail.mp4");
            totalFixedCourses++;
          }
          else
          {
            LOGGER.warn("Could not find Thumbnail.mp4 of [{}]", course.getCourseId().getId());
          }
        }
        totalCourses++;
      }
    }
    return RestResponse.success("Total courses " + totalCourses + ", total fixed courses " + totalFixedCourses);
  }

  @ApiOperation("Download attachment")
  @GetMapping("/course-content/download-attachment/{attachmentFolderId}")
  public ResponseEntity downloadAttachment(@PathVariable String attachmentFolderId, @RequestParam String attachmentId, @RequestParam String fileName)
  {
    LmsFileSystemService lmsFileSystemService = lmsServiceRegistry.getLmsFileSystemService();
    Content content = lmsFileSystemService.getDocumentContentById(attachmentFolderId, attachmentId);

    ByteArrayResource resource = new ByteArrayResource(content.getContent());
    return ResponseEntity.ok()
        .contentLength(resource.contentLength())
        .header("Content-Disposition", "attachment; filename=" + fileName)
        .body(resource);
  }

  @ApiOperation("Update attachments")
  @PutMapping("/update-attachments")
  public ResponseEntity<RestResult> updateAttachments(@RequestBody RestUpdateAttachment body)
  {
    try
    {
      List<File> attachments = getAttachments(body.getAttachmentIds());
      UpdateCourseAttachmentInput input = new UpdateCourseAttachmentInput(body.getCourseId(), attachments);
      UpdateCourseAttachment updateCourseAttachment = new UpdateCourseAttachment(lmsRepositoryRegistry, lmsServiceRegistry);
      return RestResponse.success(updateCourseAttachment.execute(input));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Uploads a image")
  @PostMapping("/course-content/image")
  public ResponseEntity<RestResult> uploadImage(@RequestParam("file") MultipartFile multipartFile)
  {
    String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
    if (!ImageExtension.isSupported(extension))
    {
      throw new IllegalArgumentException("Unsupported image format!");
    }

    try
    {
      String uuid = getUUIdString();
      File file = saveFile(multipartFile, uuid);

      Map<String, SectionDto> imageSectionMap = new HashMap<>();
      imageSectionMap.put(uuid + "." + extension, new SectionDto(1, file.toPath()));
      this.sectionFiles.putAll(imageSectionMap);
      return RestResponse.success(createRestSection(uuid, imageSectionMap));
    }
    catch (IOException e)
    {
      return RestResponse.internalError("Failed to read the image!");
    }
  }

  @ApiOperation("Uploads an attachment")
  @PostMapping("/course-content/attachment")
  public ResponseEntity<RestResult> uploadAttachment(@RequestParam("file") MultipartFile multipartFile)
  {
    String uuid = UUID.randomUUID().toString();
    if (multipartFile.isEmpty())
    {
      return RestResponse.badRequest("File is empty!");
    }

    try
    {
      File file = saveFile(multipartFile, uuid);
      this.attachmentFiles.put(uuid, file);
      RestFile result = new RestFile(multipartFile.getOriginalFilename(), file.getPath(), uuid);
      return RestResponse.success(result);
    }
    catch (IOException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Deletes an attachment")
  @DeleteMapping("/course-content/delete-attachment")
  public ResponseEntity<RestResult> deleteAttachment(@RequestParam String attachmentId, @RequestParam(required = false) String courseId)
  {
    if (StringUtils.isBlank(attachmentId))
    {
      return RestResponse.badRequest("Attachment ID can not be null or blank!");
    }

    if (this.attachmentFiles.get(attachmentId) != null)
    {
      this.attachmentFiles.remove(attachmentId);
      return RestResponse.success();
    }

    try
    {
      if (!StringUtils.isBlank(courseId))
      {
        DeleteCourseAttachment deleteCourseAttachment = new DeleteCourseAttachment(lmsRepositoryRegistry, lmsServiceRegistry);
        DeleteCourseAttachmentInput input = new DeleteCourseAttachmentInput();
        input.setCourseId(courseId);
        input.setAttachmentId(attachmentId);
        return RestResponse.success(deleteCourseAttachment.execute(input));
      }
      return RestResponse.badRequest("Attachment not found with ID " + attachmentId);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  private RestFileSection createRestSection(String attachmentFileId, Map<String, SectionDto> files)
  {
    List<RestSection> restSections = new ArrayList<>();

    for (Map.Entry<String, SectionDto> fileEntry : files.entrySet())
    {
      Integer pageNumber = fileEntry.getValue().getPageNumber();

      RestSection restSection = new RestSection();
      restSection.setFileId(fileEntry.getKey());
      restSection.setName(pageNumber.toString());
      restSection.setIndex(pageNumber);

      restSections.add(restSection);
    }

    return new RestFileSection(attachmentFileId, restSections);
  }

  private RestFileSection createRestSection(String attachmentFileId, Map<String, SectionDto> files, String absolutePath, boolean codecSupported)
  {
    List<RestSection> restSections = new ArrayList<>();

    for (Map.Entry<String, SectionDto> fileEntry : files.entrySet())
    {
      Integer pageNumber = fileEntry.getValue().getPageNumber();

      RestSection restSection = new RestSection();
      restSection.setFileId(fileEntry.getKey());
      restSection.setName(pageNumber.toString());
      restSection.setIndex(pageNumber);

      restSections.add(restSection);
    }

    return new RestFileSection(attachmentFileId, restSections, absolutePath, codecSupported);
  }

  private CourseContentInput toInput(String courseId, RestCourseContent restCourseContent)
  {
    List<ModuleInfo> moduleInfoList = restCourseContent.getModules()
        .stream()
        .map(restModule -> new ModuleInfo(
            restModule.getName(),
            toSectionInfo(restModule.getSectionList()),
            restModule.getIndex(),
            restModule.getFileType(),
            restModule.getModuleFolderId() == null ? getUUIdString() : restModule.getModuleFolderId()
        ))
        .collect(Collectors.toList());
    List<File> attachments = getAttachments(restCourseContent.getAttachmentIdList());
    return new CourseContentInput(courseId, moduleInfoList, this.sectionFiles, attachments);
  }

  private List<SectionInfo> toSectionInfo(List<RestSection> sections)
  {
    return sections.stream().map(restSection -> new SectionInfo(
            restSection.getName(),
            restSection.getFileId(),
            restSection.getIndex()
        ))
        .collect(Collectors.toList());
  }

  private List<File> getAttachments(List<String> attachmentIds)
  {
    List<File> attachments = new ArrayList<>();
    for (String attachmentId : attachmentIds)
    {
      if (this.attachmentFiles.get(attachmentId) != null)
      {
        attachments.add(this.attachmentFiles.get(attachmentId));
        this.attachmentFiles.remove(attachmentId);
      }
    }
    return attachments;
  }
}
