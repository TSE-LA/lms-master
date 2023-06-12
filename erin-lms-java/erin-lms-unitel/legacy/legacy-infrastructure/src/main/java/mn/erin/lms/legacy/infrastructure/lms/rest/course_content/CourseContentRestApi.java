/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.rest.course_content;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import mn.erin.common.docx.DocxUtil;
import mn.erin.common.formats.VideoExtension;
import mn.erin.common.pdf.Section;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.dms.repository.DMSRepositoryException;
import mn.erin.domain.dms.usecase.document.create_document.CreateDocument;
import mn.erin.domain.dms.usecase.document.create_document.CreateDocumentInput;
import mn.erin.domain.dms.usecase.document.create_document.CreateDocumentOutput;
import mn.erin.domain.dms.usecase.document.delete_document.DeleteDocumentInput;
import mn.erin.domain.dms.usecase.document.delete_document.DeleteDocumentOutput;
import mn.erin.domain.dms.usecase.document.get_all_document_name.GetDocumentNameInput;
import mn.erin.domain.dms.usecase.document.get_all_document_name.GetDocumentNameOutput;
import mn.erin.domain.dms.usecase.document.get_all_documents.GetAllDocumentInput;
import mn.erin.domain.dms.usecase.document.get_all_documents.GetAllDocumentOutput;
import mn.erin.domain.dms.usecase.document.get_document.GetDocumentInput;
import mn.erin.domain.dms.usecase.document.get_document.GetDocumentOuput;
import mn.erin.domain.dms.usecase.folder.create_folder.CreateFolderInput;
import mn.erin.domain.dms.usecase.folder.create_folder.CreateFolderOutput;
import mn.erin.domain.dms.usecase.folder.delete_folder.DeleteFolderInput;
import mn.erin.domain.dms.usecase.folder.delete_folder.DeleteFolderOutput;
import mn.erin.domain.dms.usecase.folder.get_folder.GetFolderInput;
import mn.erin.domain.dms.usecase.folder.get_folder.GetFolderOuput;
import mn.erin.domain.dms.usecase.folder.update_folder.UpdateFolderInput;
import mn.erin.domain.dms.usecase.folder.update_module_folder.UpdateInfo;
import mn.erin.domain.dms.usecase.folder.update_module_folder.UpdateSubFolderInput;
import mn.erin.domain.dms.usecase.folder.update_module_folder.UpdateSubFolderOutput;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.domain.service.LmsFileSystemService;
import mn.erin.lms.base.domain.service.PdfSplitService;
import mn.erin.lms.base.domain.service.TemporaryFileApi;
import mn.erin.lms.base.domain.usecase.content.SplitPdfToImages;
import mn.erin.lms.base.domain.usecase.content.dto.ImageExtension;
import mn.erin.lms.base.domain.usecase.content.dto.SplitPdfToImagesInput;
import mn.erin.lms.legacy.domain.lms.model.content.Attachment;
import mn.erin.lms.legacy.domain.lms.model.content.CourseContent;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseAssessmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseContentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseGroupRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseTestRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.service.CourseService;
import mn.erin.lms.legacy.domain.lms.usecase.course.create_course.CreateCourse;
import mn.erin.lms.legacy.domain.lms.usecase.course.create_course.CreateCourseInput;
import mn.erin.lms.legacy.domain.lms.usecase.course.create_course.CreateCourseOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course.delete_course.DeleteCourse;
import mn.erin.lms.legacy.domain.lms.usecase.course.delete_course.DeleteCourseInput;
import mn.erin.lms.legacy.domain.lms.usecase.course.delete_course.DeleteCourseOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourse;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseInput;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.ContentModuleInfo;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.ContentSectionInfo;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.create_course_content.CreateCourseContent;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.create_course_content.CreateCourseContentInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.create_course_content.CreateCourseContentOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.delete_all_course_attachment.DeleteAllCourseAttachment;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.delete_all_course_attachment.DeleteAllCourseAttachmentInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.delete_course_attachment.DeleteCourseAttachment;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.delete_course_attachment.DeleteCourseAttachmentInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.delete_course_attachment.DeleteCourseAttachmentOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.delete_course_content.DeleteCourseContent;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.delete_course_content.DeleteCourseContentInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.delete_course_content.DeleteCourseContentOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.get_course_content.GetCourseContent;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.get_course_content.GetCourseContentInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.get_course_content.GetCourseContentOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.get_course_publish_status.GetCoursePublishStatus;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.get_course_publish_status.GetCoursePublishStatusOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.update_course_content.UpdateCourseContent;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.update_course_content.UpdateCourseContentInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_group.CreateCourseGroup;
import mn.erin.lms.legacy.infrastructure.lms.repository.dms.CourseContentPathResolver;
import mn.erin.lms.legacy.infrastructure.lms.rest.BaseCourseRestApi;
import mn.erin.lms.legacy.infrastructure.lms.rest.course.RestCourse;
import mn.erin.lms.legacy.infrastructure.lms.rest.course.RestCourseDetail;
import mn.erin.lms.legacy.infrastructure.lms.rest.course.RestCourseExpanded;
import mn.erin.lms.legacy.infrastructure.lms.rest.course_content.rest_entities.RestAttachment;
import mn.erin.lms.legacy.infrastructure.lms.rest.course_content.rest_entities.RestCourseContent;
import mn.erin.lms.legacy.infrastructure.lms.rest.course_content.rest_entities.RestModule;
import mn.erin.lms.legacy.infrastructure.lms.rest.course_content.rest_entities.RestUpdatedContent;
import mn.erin.lms.legacy.infrastructure.lms.rest.course_content.rest_entities.RestUpdatedModule;

import static mn.erin.common.file.FileUtil.getByteArray;
import static mn.erin.common.pdf.PdfUtil.getUUIdString;
import static mn.erin.lms.legacy.infrastructure.lms.rest.ErrorMessages.ERROR_MSG_COURSE_CONTENT_ID;
import static mn.erin.lms.legacy.infrastructure.lms.rest.ErrorMessages.ERROR_MSG_COURSE_ID;
import static mn.erin.lms.legacy.infrastructure.lms.rest.ErrorMessages.ERR_MSG_COURSE_CONTENT_NOT_FOUND;
import static mn.erin.lms.legacy.infrastructure.lms.rest.course_content.utils.CourseContentUtils.addFileIds;
import static mn.erin.lms.legacy.infrastructure.lms.rest.course_content.utils.CourseContentUtils.createRestSection;
import static mn.erin.lms.legacy.infrastructure.lms.rest.course_content.utils.CourseContentUtils.getModuleDetails;
import static mn.erin.lms.legacy.infrastructure.lms.rest.course_content.utils.CourseContentUtils.getUpdatedModules;
import static mn.erin.lms.legacy.infrastructure.lms.rest.course_content.utils.CourseContentUtils.putDocumentIds;
import static mn.erin.lms.legacy.infrastructure.lms.rest.course_content.utils.CourseContentUtils.setImageFileIds;
import static mn.erin.lms.legacy.infrastructure.lms.rest.course_content.utils.CourseContentUtils.toCreateInput;
import static mn.erin.lms.legacy.infrastructure.lms.rest.course_content.utils.CourseContentUtils.toRestCourseContent;
import static mn.erin.lms.legacy.infrastructure.lms.rest.course_content.utils.CourseContentUtils.toUpdateInfos;
import static mn.erin.lms.legacy.infrastructure.lms.rest.course_content.utils.CourseContentUtils.toUpdateInput;
import static mn.erin.lms.legacy.infrastructure.lms.rest.course_content.utils.CourseContentUtils.validate;

/**
 * Responsible REST API for course content.
 * <p>
 * author Tamir Batmagnai.
 */
// TODO : Refactor all api and move logic to usecases
@Api("CourseContent")
@RequestMapping(value = "/legacy/course-contents", name = "Provides 'ERIN' LMS course content features")
public class CourseContentRestApi extends BaseCourseRestApi
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CourseContentRestApi.class);

  private static final String ATTACH_FILE_NAME = "Attachments";
  private static final String COURSE_CONTENT_FOLDER_NAME = "Course Content";

  private static final String REQUEST_BODY_NULL = "Course expanded request body cannot be null!";
  private static final String COURSE_FOLDER_NOT_EXIST = "Course folder does not exist!";

  private static final String FAILED_TO_CREATE_FOLDER = "Failed to create folder by course id!";
  private static final String FAILED_TO_GET_CREATED_CONTENT = "Failed to get created course content info!";
  private static final String FAILED_TO_CREATE_SUB_FOLDERS = "Failed to create sub folders or folder info could not persisted!";
  public static final String WORD_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
  public static final String FOLDER_DOES_NOT_EXIST_WITH_ID = "Folder does not exist with course id : ";
  public static final String DOCX = ".docx";

  private final CourseCategoryRepository courseCategoryRepository;
  private final CourseEnrollmentRepository courseEnrollmentRepository;
  private final CourseAssessmentRepository courseAssessmentRepository;
  private final CourseTestRepository courseTestRepository;
  private final CourseGroupRepository courseGroupRepository;
  private final CourseAuditRepository courseAuditRepository;

  private final Map<String, Section> files = new HashMap<>();
  private final Map<String, File> attachmentFiles = new HashMap<>();

  @Inject
  private AccessIdentityManagement accessIdentityManagement;

  @Inject
  private LmsFileSystemService lmsFileSystemService;

  @Inject
  private CourseContentPathResolver courseContentPathResolver;

  @Inject
  private TemporaryFileApi temporaryFileApi;

  public CourseContentRestApi(CourseContentRepository contentRepository, CourseRepository courseRepository,
      CourseCategoryRepository courseCategoryRepository, CourseEnrollmentRepository courseEnrollmentRepository,
      CourseAssessmentRepository courseAssessmentRepository, CourseTestRepository courseTestRepository,
      CourseService courseService, CourseGroupRepository courseGroupRepository, CourseAuditRepository courseAuditRepository)
  {
    super(courseRepository, contentRepository, courseService);
    this.courseCategoryRepository = Objects.requireNonNull(courseCategoryRepository, "CourseCategoryRepository cannot be null!");
    this.courseEnrollmentRepository = Objects.requireNonNull(courseEnrollmentRepository, "CourseEnrollmentRepository cannot be null!");
    this.courseAssessmentRepository = Objects.requireNonNull(courseAssessmentRepository, "CourseAssessmentRepository cannot be null!");
    this.courseTestRepository = Objects.requireNonNull(courseTestRepository, "CourseAssessmentRepository cannot be null!");
    this.courseService = Objects.requireNonNull(courseService, "CourseSearchService cannot be null!");
    this.courseGroupRepository = courseGroupRepository;
    this.courseAuditRepository = courseAuditRepository;
  }

  @ApiOperation("Creates course content.")
  @PostMapping
  public ResponseEntity createContent(@RequestBody RestCourseContent restCourseContent)
  {
    validate(restCourseContent);
    List<Module> moduleDetails = getModuleDetails(files, restCourseContent.getModules());

    if (moduleDetails.isEmpty())
    {
      return RestResponse.internalError("Failed to read byte array from file content!");
    }

    GetCourse getCourse = new GetCourse(courseRepository);
    GetCourseOutput course;
    try
    {
      course = getCourse.execute(new GetCourseInput(restCourseContent.getCourseId()));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }

    //Get course folder if doesn't exist create
    String courseFolderId;
    try
    {
      courseFolderId = getCourseFolderId(restCourseContent.getCourseId());
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      courseFolderId = lmsFileSystemService.createCourseFolder(restCourseContent.getCourseId(), course.getTitle());
    }

    if (courseFolderId == null)
    {

      return RestResponse.internalError("Could not create course folder");
    }

    //Check if course content exists delete and create new
    String contentFolderId;
    try
    {
      contentFolderId = lmsFileSystemService.getCourseContentFolderId(courseFolderId);
      lmsFileSystemService.deleteFolder(contentFolderId);
    }
    catch (DMSRepositoryException ignored)
    {
      // Deletes only if exists (no need to log)
    }

    contentFolderId = lmsFileSystemService.createCourseContentFolder(courseFolderId);

    if (contentFolderId == null)
    {
      return RestResponse.internalError("Could not create course content folder");
    }

    try
    {
      createAttachFolder(contentFolderId, restCourseContent.getAttachments(), restCourseContent.getAdditionalFiles());
      Map<String, String> documentIds = createModuleFolders(contentFolderId, moduleDetails);
      executeCreateContent(restCourseContent, documentIds);
    }
    catch (UseCaseException | IOException e)
    {
      LOGGER.error(e.getMessage(), e);
      // Removes folder named by course id.
      deleteFolderById(courseFolderId);
      return RestResponse.internalError(FAILED_TO_CREATE_SUB_FOLDERS);
    }

    try
    {
      GetCourseContentInput input = new GetCourseContentInput(restCourseContent.getCourseId());
      GetCourseContent usecase = new GetCourseContent(courseContentRepository);

      GetCourseContentOutput createdContent = usecase.execute(input);
      return RestResponse.success(toRestCourseContent(createdContent));
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(FAILED_TO_GET_CREATED_CONTENT);
    }
  }

  @ApiOperation("Updates course content.")
  @PutMapping
  public ResponseEntity updateContent(@RequestBody RestUpdatedContent updatedContent)
  {
    validate(updatedContent);
    // Removes leading and trailing spaces from module name
    RestUpdatedContent restContent = trimModuleName(updatedContent);

    List<UpdatedModule> moduleDetails = getUpdatedModules(files, restContent.getModules());

    String courseFolderId;
    String contentFolderId;

    Map<String, String> updatedDocIds;

    try
    {
      courseFolderId = getCourseFolderId(restContent.getCourseId());

      GetFolderInput contentFolderInput = new GetFolderInput(courseFolderId, COURSE_CONTENT_FOLDER_NAME);
      GetFolderOuput contentFolderOutput = getFolder.execute(contentFolderInput);

      contentFolderId = contentFolderOutput.getId();
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(FOLDER_DOES_NOT_EXIST_WITH_ID + restContent.getCourseId());
    }

    try
    {
      updateAttachments(contentFolderId, restContent);
      updatedDocIds = updateModuleFolders(contentFolderId, moduleDetails);
    }
    catch (UseCaseException | IOException e)
    {
      LOGGER.error(e.getMessage(), e);

      updateUndo(contentFolderId, moduleDetails);
      return RestResponse.internalError("Failed to update sub folders and attachments!");
    }

    try
    {
      GetCourseContentOutput output = executeUpdateContent(restContent, updatedDocIds);
      return RestResponse.success(toRestCourseContent(output));
    }
    catch (UseCaseException | LMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);

      updateUndo(courseFolderId, moduleDetails);
      return RestResponse.internalError("Failed to persist updated data!");
    }
  }

  @ApiOperation("Gets course ready to publish status depending on the course id.")
  @GetMapping("/ready-to-publish/{courseId}")
  public ResponseEntity getsReadyToPublishStatus(@PathVariable(value = "courseId") String courseId)
  {
    Validate.notNull(courseId, ERROR_MSG_COURSE_ID);

    GetCourseContentInput input = new GetCourseContentInput(courseId);
    GetCoursePublishStatus usecase = new GetCoursePublishStatus(courseContentRepository, courseRepository, courseAssessmentRepository);

    GetCoursePublishStatusOutput output = null;
    try
    {
      output = usecase.execute(input);
    }
    catch (UseCaseException e)
    {
      e.printStackTrace();
    }
    return (null == output) ? RestResponse.success() : RestResponse.success(output);
  }

  @ApiOperation("Gets course content depending on the course id.")
  @GetMapping("/{courseId}")
  public ResponseEntity getsByCourseId(@PathVariable(value = "courseId") String courseId)
  {
    Validate.notNull(courseId, ERROR_MSG_COURSE_ID);

    GetCourseContentInput input = new GetCourseContentInput(courseId);
    GetCourseContent usecase = new GetCourseContent(courseContentRepository);

    try
    {
      GetCourseContentOutput output = usecase.execute(input);
      return (null == output) ? RestResponse.success() : RestResponse.success(toRestCourseContent(output));
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage());
      return RestResponse.notFound(String.format(ERR_MSG_COURSE_CONTENT_NOT_FOUND, courseId));
    }
  }

  @ApiOperation("Gets course content url depending on the course id.")
  @GetMapping("/{courseId}/file-attachment")
  public ResponseEntity getsContentByCourseId(@PathVariable(value = "courseId") String courseId)
  {
    Validate.notNull(courseId, ERROR_MSG_COURSE_ID);
    try
    {
      GetCourseContentOutput courseContent = getCourseContent(courseId);
      List<ContentModuleInfo> courseModules = courseContent.getCourseModules();
      courseContentPathResolver.setCourseFolderPath(courseId);
      List<ModulePaths> modules = getModulePaths(courseId, courseModules);

      return RestResponse.success(modules);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage());
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Deletes a course content depending on the course id")
  @DeleteMapping("/{courseId}")
  public ResponseEntity deleteByCourseId(@PathVariable String courseId)
  {
    Validate.notNull(courseId, ERROR_MSG_COURSE_CONTENT_ID);

    DeleteCourseContentOutput output = null;

    // Deletes folder content.
    if (deleteFolderById(courseId))
    {
      // Deletes metadata.
      DeleteCourseContentInput input = new DeleteCourseContentInput(courseId);
      DeleteCourseContent usecase = new DeleteCourseContent(courseContentRepository);
      output = usecase.execute(input);
    }
    return RestResponse.success(output);
  }

  @ApiOperation("Uploads a VIDEO file")
  @PostMapping("/upload-video")
  public ResponseEntity uploadVideo(@RequestParam("file") MultipartFile multipartFile)
  {
    String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
    if (VideoExtension.isSupported(extension))
    {
      throw new IllegalArgumentException("Unsupported video format!");
    }

    try
    {
      File file = saveToFile(multipartFile);
      String uuid = getUUIdString();
      attachmentFiles.put(uuid, file);
      Map<String, Section> videoSectionMap = new HashMap<>();
      videoSectionMap.put(uuid + "." + extension, new Section(1, file));
      this.files.putAll(videoSectionMap);
      return RestResponse.success(createRestSection(uuid, videoSectionMap));
    }
    catch (IOException e)
    {
      return RestResponse.internalError("Failed to read the video!");
    }
  }

  @ApiOperation("Uploads a pdf file and splits it into images")
  @PostMapping("/split-pdf-to-images")
  public ResponseEntity<RestResult> uploadPdf(@RequestParam("file") MultipartFile multipartFile,
      @RequestParam("courseId") String courseId,
      @RequestParam("courseType") String courseType)
  {
    SplitPdfToImagesInput input;
    try
    {
      File file = saveToFile(multipartFile);
      attachmentFiles.put(courseId, file);
      input = new SplitPdfToImagesInput(multipartFile.getBytes(), ImageExtension.JPEG, courseId, courseType);
    }
    catch (IOException e)
    {
      return RestResponse.internalError("Failed to read multipart form data");
    }

    SplitPdfToImages splitPdfToImages = new SplitPdfToImages(temporaryFileApi);
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
  @GetMapping("/get-pdf-section")
  public ResponseEntity<RestResult> read(@RequestParam("courseId") String courseId)
  {
    File file = attachmentFiles.get(courseId);
    String uuid = getUUIdString();
    Map<String, Section> result = PdfSplitService.getSection(courseId);
    attachmentFiles.put(uuid, file);
    files.putAll(result);
    return RestResponse.success(createRestSection(uuid, result));
  }

  @ApiOperation("Uploads a additional files")
  @PostMapping("/upload-attachments")
  public ResponseEntity uploadAttachments(@RequestParam("file") MultipartFile multipartFile)
  {
    String uuid = getUUIdString();
    Map<String, File> convertedPDF;

    if (multipartFile.isEmpty())
    {
      return RestResponse.badRequest("File is empty!");
    }
    try
    {
      if (multipartFile.getName().toLowerCase().contains(DOCX) || Objects.equals(multipartFile.getContentType(), WORD_TYPE))
      {
        convertedPDF = DocxUtil.docxConvertToPdf(multipartFile.getInputStream(), uuid);
        attachmentFiles.putAll(convertedPDF);
      }
      else
      {
        File file = saveToFile(multipartFile);
        attachmentFiles.put(uuid, file);
      }
    }
    catch (IOException e)
    {
      LOGGER.error(e.getMessage());
      return RestResponse.internalError("Fail to read multipart form data.\n" + e.getMessage());
    }
    return RestResponse.success(uuid);
  }

  @ApiOperation("Deletes a attachment files")
  @DeleteMapping("/{courseId}/{attachmentId}/{attachmentType}/{isDeleteAll}/delete-attachments")
  public ResponseEntity deleteAttachment(@PathVariable String courseId, @PathVariable String attachmentId, @PathVariable String attachmentType,
      @PathVariable boolean isDeleteAll) throws UseCaseException
  {
    Validate.notNull(courseId, ERROR_MSG_COURSE_CONTENT_ID);

    DeleteCourseAttachmentOutput output = null;

    String contentFolderId;
    String courseFolderId;

    try
    {
      courseFolderId = getCourseFolderId(courseId);
      GetFolderInput contentFolderInput = new GetFolderInput(courseFolderId, COURSE_CONTENT_FOLDER_NAME);
      GetFolderOuput contentFolderOutput = getFolder.execute(contentFolderInput);
      contentFolderId = contentFolderOutput.getId();
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(FOLDER_DOES_NOT_EXIST_WITH_ID + courseId);
    }
    if (isDeleteAll)
    {
      if (removeAllAttachments(courseId, contentFolderId, attachmentId, true))
      {
        DeleteAllCourseAttachmentInput input = new DeleteAllCourseAttachmentInput(courseId);
        DeleteAllCourseAttachment useCase = new DeleteAllCourseAttachment(courseContentRepository);
        output = useCase.execute(input);
      }
    }
    else
    {
      if (removeAttachments(contentFolderId, attachmentId, attachmentType))
      {
        DeleteCourseAttachmentInput input = new DeleteCourseAttachmentInput(courseId, attachmentId);
        DeleteCourseAttachment useCase = new DeleteCourseAttachment(courseContentRepository);
        output = useCase.execute(input);
      }
    }
    return RestResponse.success(output);
  }

  @ApiOperation("Deletes a additional files")
  @DeleteMapping("/{courseId}/{attachmentId}/{attachmentType}/{isDeleteAll}/delete-additional-files")
  public ResponseEntity deleteAdditionalFiles(@PathVariable String courseId, @PathVariable String attachmentId, @PathVariable String attachmentType,
      @PathVariable boolean isDeleteAll) throws UseCaseException
  {
    Validate.notNull(courseId, ERROR_MSG_COURSE_CONTENT_ID);

    DeleteCourseAttachmentOutput output = null;

    String contentFolderId;
    String courseFolderId;

    try
    {
      courseFolderId = getCourseFolderId(courseId);
      GetFolderInput contentFolderInput = new GetFolderInput(courseFolderId, COURSE_CONTENT_FOLDER_NAME);
      GetFolderOuput contentFolderOutput = getFolder.execute(contentFolderInput);
      contentFolderId = contentFolderOutput.getId();
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(FOLDER_DOES_NOT_EXIST_WITH_ID + courseId);
    }
    if (isDeleteAll)
    {
      if (removeAllAttachments(courseId, contentFolderId, attachmentId, false))
      {
        DeleteAllCourseAttachmentInput input = new DeleteAllCourseAttachmentInput(courseId);
        DeleteAllCourseAttachment useCase = new DeleteAllCourseAttachment(courseContentRepository);
        output = useCase.executeAdditionalFiles(input);
      }
    }
    else
    {
      if (removeAttachments(contentFolderId, attachmentId, attachmentType))
      {
        DeleteCourseAttachmentInput input = new DeleteCourseAttachmentInput(courseId, attachmentId);
        DeleteCourseAttachment useCase = new DeleteCourseAttachment(courseContentRepository);
        output = useCase.executeAdditionalFiles(input);
      }
    }
    return RestResponse.success(output);
  }

  @ApiOperation("Creates a course info and course content otherwise combined save.")
  @PostMapping("/expanded")
  public ResponseEntity createExpanded(@RequestBody RestCourseExpanded courseExpanded)
  {
    if (null == courseExpanded)
    {
      LOGGER.warn(REQUEST_BODY_NULL);
      return RestResponse.badRequest(REQUEST_BODY_NULL);
    }

    if (null == courseExpanded.getRestCourse() || null == courseExpanded.getCourseContent())
    {
      return RestResponse.badRequest(REQUEST_BODY_NULL);
    }

    return createCourseExpanded(courseExpanded);
  }

  private ResponseEntity<RestResult> createCourseExpanded(RestCourseExpanded courseExpanded)
  {
    RestCourse restCourse = courseExpanded.getRestCourse();
    RestCourseDetail courseDetail = restCourse.getCourseDetail();

    String categoryId = restCourse.getCategoryId();
    String courseTitle = courseDetail.getTitle();

    RestCourseContent restCourseContent = courseExpanded.getCourseContent();

    String createdCourseId;
    RestCourseContent createdRestContent;

    try
    {
      createdCourseId = createCourse(restCourse);
      createCourseFolder(createdCourseId, courseTitle);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError("Failed to create a course [" + courseTitle + "]");
    }
    catch (NoSuchElementException e)
    {
      return RestResponse.notFound("The course category with the ID: [" + categoryId + "] does not exist!");
    }

    String courseFolderId;
    String contentFolderId;

    try
    {
      courseFolderId = getCourseFolderId(createdCourseId);
    }
    catch (UseCaseException ex)
    {
      return deleteCourseReturnResponse(createdCourseId, createdCourseId, COURSE_FOLDER_NOT_EXIST, ex);
    }

    try
    {
      contentFolderId = createContentFolderReturnId(courseFolderId);
    }
    catch (UseCaseException ex)
    {
      return deleteCourseReturnResponse(createdCourseId, courseFolderId, FAILED_TO_CREATE_FOLDER, ex);
    }

    try
    {
      restCourseContent.setCourseId(createdCourseId);
      createSubFolders(contentFolderId, restCourseContent);
    }
    catch (UseCaseException | IOException ex)
    {
      return deleteCourseReturnResponse(createdCourseId, courseFolderId, FAILED_TO_CREATE_SUB_FOLDERS, ex);
    }

    try
    {
      GetCourseContentOutput createdCourseContent = getCourseContent(createdCourseId);
      createdRestContent = toRestCourseContent(createdCourseContent);
    }
    catch (UseCaseException ex)
    {
      return deleteCourseReturnResponse(createdCourseId, courseFolderId, FAILED_TO_GET_CREATED_CONTENT, ex);
    }

    return RestResponse.success(createRestExpanded(restCourse, createdRestContent));
  }

  private String createCourse(RestCourse restCourse) throws UseCaseException
  {
    String categoryId = restCourse.getCategoryId();

    RestCourseDetail courseDetail = restCourse.getCourseDetail();
    Map<String, Object> properties = courseDetail.getCourseProperties();

    CreateCourseInput input = new CreateCourseInput(courseDetail.getTitle(), categoryId, properties);
    CreateCourse createCourse = new CreateCourse(courseRepository, courseCategoryRepository,
        new CreateCourseGroup(accessIdentityManagement, courseGroupRepository));

    CreateCourseOutput output = createCourse.execute(input);
    return output.getId();
  }

  private void createSubFolders(String contentId, RestCourseContent courseContent) throws UseCaseException, IOException
  {
    List<RestModule> restModules = courseContent.getModules();

    List<Module> moduleDetails = getModuleDetails(files, restModules);

    if (moduleDetails.isEmpty())
    {
      deleteCourse(courseContent.getCourseId());
      throw new UseCaseException("Course content is empty!");
    }

    Map<String, String> documentIds = createModuleFolders(contentId, moduleDetails);
    createAttachFolder(contentId, courseContent.getAttachments(), courseContent.getAdditionalFiles());
    executeCreateContent(courseContent, documentIds);
  }

  private List<ModulePaths> getModulePaths(String courseId, List<ContentModuleInfo> courseModules) throws UseCaseException
  {
    String courseSectionFolderPath;
    List<ModulePaths> modules = new ArrayList<>();

    for (ContentModuleInfo courseModule : courseModules)
    {
      Integer moduleIndex = courseModule.getIndex();
      ModulePaths module = new ModulePaths();
      String moduleName = courseModule.getName();
      List<ContentSectionInfo> sectionInfos = courseModule.getSectionInfos();

      for (ContentSectionInfo sectionInfo : sectionInfos)
      {
        String fileId = sectionInfo.getFileId();

        GetDocumentNameInput input = new GetDocumentNameInput(fileId);

        GetDocumentNameOutput documentNameOutput = getDocumentName.execute(input);
        String documentName = documentNameOutput.getDocumentName();

        courseSectionFolderPath = courseContentPathResolver.getCourseFolderPath(courseId, moduleName, documentName);
        module.addPaths(new Path(courseSectionFolderPath, sectionInfo.getIndex()));
      }

      module.setIndex(moduleIndex);
      modules.add(module);
    }
    return modules;
  }

  private GetCourseContentOutput getCourseContent(String courseId) throws UseCaseException
  {
    GetCourseContentInput getContentInput = new GetCourseContentInput(courseId);
    GetCourseContent useCase = new GetCourseContent(courseContentRepository);

    return useCase.execute(getContentInput);
  }

  private ResponseEntity<RestResult> deleteCourseReturnResponse(String courseId, String folderId, String errorMessage, Throwable stackTrace)
  {
    LOGGER.error(errorMessage, stackTrace);

    deleteBoth(courseId, folderId);

    return RestResponse.internalError(errorMessage);
  }

  private void deleteBoth(String courseId, String folderId)
  {
    deleteCourse(courseId);
    deleteCourseFolder(folderId);
  }

  private void deleteCourse(String courseId)
  {
    DeleteCourseInput input = new DeleteCourseInput(courseId);

    DeleteCourse deleteCourse = new DeleteCourse(courseRepository, courseEnrollmentRepository, courseContentRepository, courseAssessmentRepository,
        courseTestRepository, courseGroupRepository, courseAuditRepository);
    DeleteCourseOutput output;
    try
    {
      output = deleteCourse.execute(input);
      if (output.isDeleted())
      {
        LOGGER.info("Successful deleted course with ID [{}]", courseId);
      }
      else
      {
        LOGGER.info("Could not delete course with ID [{}]", courseId);
      }
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
    }
  }

  private RestCourseExpanded createRestExpanded(RestCourse course, RestCourseContent courseContent)
  {
    RestCourseExpanded created = new RestCourseExpanded();

    created.setRestCourse(course);
    created.setCourseContent(courseContent);

    return created;
  }

  /**
   * Creates course content folder then returns folder id, if folder already exist just return id.
   *
   * @param courseFolderId given course folder id.
   * @return course content folder id.
   * @throws UseCaseException
   */
  private String createContentFolderReturnId(String courseFolderId) throws UseCaseException
  {
    String contentFolderId = null;
    try
    {
      GetFolderInput input = new GetFolderInput(courseFolderId, COURSE_CONTENT_FOLDER_NAME);
      GetFolderOuput output = getFolder.execute(input);

      contentFolderId = output.getId();
    }
    catch (UseCaseException e)
    {
      contentFolderId = createFolderInside(courseFolderId, COURSE_CONTENT_FOLDER_NAME);
    }
    return contentFolderId;
  }

  private RestUpdatedContent trimModuleName(RestUpdatedContent updatedContent)
  {
    for (RestUpdatedModule module : updatedContent.getModules())
    {
      String trimmedName = module.getInitName().trim();
      String trimmedUpdatedName = module.getUpdatedName().trim();

      module.setInitName(trimmedName);
      module.setUpdatedName(trimmedUpdatedName);
    }

    return updatedContent;
  }

  private boolean deleteFolderById(String folderId)
  {
    DeleteFolderInput input = new DeleteFolderInput(folderId);
    DeleteFolderOutput output = deleteFolder.execute(input);

    if (output.isDeleted())
    {
      LOGGER.info("Successful deleted course folder with id = [{}]", folderId);
    }
    else
    {
      LOGGER.info("Could not delete course folder with id = [{}]", folderId);
    }
    return output.isDeleted();
  }

  private CreateCourseContentOutput executeCreateContent(RestCourseContent courseContent, Map<String, String> documentIds)
      throws UseCaseException
  {
    setImageFileIds(courseContent.getModules(), documentIds);
    courseContent.setAttachments(courseContent.getAttachments());
    courseContent.setAdditionalFiles(courseContent.getAdditionalFiles());
    CreateCourseContentInput courseInput = toCreateInput(courseContent);
    CreateCourseContent createCourseContent = new CreateCourseContent(courseContentRepository, courseRepository);

    return createCourseContent.execute(courseInput);
  }

  private void updateUndo(String courseFolderId, List<UpdatedModule> moduleDetails)
  {
    LOGGER.info("############ Update reverting : For reason for that error occurred while update folders!");
    try
    {
      List<UpdateInfo> updateInfos = toUpdateInfos(moduleDetails);

      for (UpdateInfo updateInfo : updateInfos)
      {
        String previousName = updateInfo.getFolderName();
        String updatedName = updateInfo.getUpdatedName();

        GetFolderInput previousInput = new GetFolderInput(courseFolderId, previousName);
        GetFolderOuput moduleFolder = getFolder.execute(previousInput);

        String updatedFolderId = null;

        // Already module folder name updated.
        if (null == moduleFolder)
        {
          GetFolderInput updatedInput = new GetFolderInput(courseFolderId, updatedName);
          GetFolderOuput updatedFolder = getFolder.execute(updatedInput);

          updatedFolderId = updatedFolder.getId();

          // Removes updated files.
          removeDocuments(updatedFolderId, updateInfo.getDocumentFiles());

          // Updates folder name by previous name.
          UpdateFolderInput input = new UpdateFolderInput(updatedFolderId, updateInfo.getFolderName(), Collections.emptyMap());
          updateFolder.execute(input);
        }
        // Folder name non-updated!
        else
        {
          String nonUpdatedFolderId = moduleFolder.getId();
          removeDocuments(nonUpdatedFolderId, updateInfo.getDocumentFiles());
        }
      }
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
    }
  }

  private void removeDocuments(String folderId, Map<String, byte[]> documentFiles) throws UseCaseException
  {
    Set<Map.Entry<String, byte[]>> documentFileEntries = documentFiles.entrySet();

    for (Map.Entry<String, byte[]> documentFile : documentFileEntries)
    {
      String documentName = documentFile.getKey();

      if (documentName.contains(ImageExtension.JPEG.name()))
      {
        GetDocumentInput documentInput = new GetDocumentInput(folderId, documentName);
        GetDocumentOuput documentOutput = getDocument.execute(documentInput);

        if (null != documentOutput)
        {
          String documentId = documentOutput.getDocumentId();
          DeleteDocumentInput deleteDocumentInput = new DeleteDocumentInput(documentId);
          deleteDocument.execute(deleteDocumentInput);
        }
      }
    }
  }

  private List<String> createAttachFolder(String courseFolderId, List<RestAttachment> attachments, List<RestAttachment> additionalFiles)
      throws UseCaseException, IOException
  {
    if (attachments == (null))
    {
      return Collections.emptyList();
    }

    List<String> createdAttachmentIds = new ArrayList<>();

    CreateFolderInput createFolderInput = new CreateFolderInput(courseFolderId, ATTACH_FILE_NAME);
    CreateFolderOutput attachmentsFolder = createFolder.execute(createFolderInput);

    getFileAttachmentsNames(attachments, createdAttachmentIds, attachmentsFolder);
    if (additionalFiles == null)
    {
      return createdAttachmentIds;
    }
    else
    {
      getFileAttachmentsNames(additionalFiles, createdAttachmentIds, attachmentsFolder);
    }

    return createdAttachmentIds;
  }

  private void getFileAttachmentsNames(List<RestAttachment> attachments, List<String> createdAttachmentIds, CreateFolderOutput attachmentsFolder)
      throws IOException, UseCaseException
  {
    for (RestAttachment attachment : attachments)
    {
      CreateDocumentInput createDocumentInput;
      if (attachment.getType().equals(DOCX))
      {
        createDocumentInput = new CreateDocumentInput(attachmentsFolder.getFolderId(), attachment.getId() + ".pdf",
            getByteArray(attachmentFiles, attachment.getId()));
      }
      else
      {
        createDocumentInput = new CreateDocumentInput(attachmentsFolder.getFolderId(), attachment.getId() + attachment.getType(),
            getByteArray(attachmentFiles, attachment.getId()));
      }
      CreateDocumentOutput createdDocument = createDocument.execute(createDocumentInput);
      createdAttachmentIds.add(createdDocument.getDocumentId());
    }
  }

  private Map<String, String> createModuleFolders(String parentFolderId, List<Module> moduleDetails) throws UseCaseException
  {
    Map<String, String> allFileIds = new HashMap<>();

    for (Module module : moduleDetails)
    {

      CreateFolderInput moduleFolderInput = new CreateFolderInput(parentFolderId, module.getName());
      CreateFolderOutput moduleFolder = createFolder.execute(moduleFolderInput);

      String folderId = moduleFolder.getFolderId();

      Map<String, byte[]> sectionFiles = module.getSectionFiles();
      Map<String, String> sectionFileIds = createDocumentsReturnIds(createDocument, folderId, sectionFiles);

      addFileIds(allFileIds, sectionFileIds);
    }

    return allFileIds;
  }

  private Map<String, String> createDocumentsReturnIds(CreateDocument usecase, String folderId, Map<String, byte[]> sectionFiles)
      throws UseCaseException
  {
    if (null == sectionFiles || sectionFiles.isEmpty())
    {
      return Collections.emptyMap();
    }

    Map<String, String> documentIds = new HashMap<>();

    for (Map.Entry<String, byte[]> fileEntry : sectionFiles.entrySet())
    {
      if (null == fileEntry.getKey() || fileEntry.getKey().isEmpty())
      {
        continue;
      }

      CreateDocumentInput docInput = new CreateDocumentInput(folderId, fileEntry.getKey(), fileEntry.getValue());
      CreateDocumentOutput documentOutput = usecase.execute(docInput);

      String documentId = documentOutput.getDocumentId();

      if (null != documentId)
      {
        documentIds.put(fileEntry.getKey(), documentId);
      }
    }
    return documentIds;
  }

  private List<String> updateAttachments(String parentFolderId, RestUpdatedContent restUpdatedContent) throws UseCaseException, IOException
  {
    if (restUpdatedContent.getAttachments() == null || restUpdatedContent.getAdditionalFiles() == null)
    {
      return Collections.emptyList();
    }
    String attachFolderId = getAttachmentFolderId(parentFolderId);
    List<String> allAttachmentId = getAllDocumentId(attachFolderId);

    for (RestAttachment restAttachment : restUpdatedContent.getAttachments())
    {
      allAttachmentId.add(restAttachment.getId());
    }
    for (RestAttachment restAttachment : restUpdatedContent.getAdditionalFiles())
    {
      allAttachmentId.add(restAttachment.getId());
    }
    return createNewAttachments(attachFolderId, restUpdatedContent, attachmentFiles, allAttachmentId);
  }

  private List<String> createNewAttachments(String attachFolderId, RestUpdatedContent restUpdatedContent, Map<String, File> attachmentFiles,
      List<String> existIds) throws UseCaseException, IOException
  {
    getUpdatedAttachmentName(attachFolderId, restUpdatedContent.getAttachments(), attachmentFiles, existIds);
    getUpdatedAttachmentName(attachFolderId, restUpdatedContent.getAdditionalFiles(), attachmentFiles, existIds);
    return existIds;
  }

  private void getUpdatedAttachmentName(String attachFolderId, List<RestAttachment> attachments, Map<String, File> attachmentFiles, List<String> existIds)
      throws IOException, UseCaseException
  {
    if (attachments != null)
    {
      CreateDocumentInput createDocumentInput;
      for (RestAttachment attachment : attachments)
      {
        if (attachment.getType().equals(DOCX))
        {
          createDocumentInput = new CreateDocumentInput(attachFolderId, attachment.getId() + ".pdf", getByteArray(attachmentFiles, attachment.getId()));
        }
        else
        {
          createDocumentInput = new CreateDocumentInput(attachFolderId, attachment.getId() + attachment.getType(),
              getByteArray(attachmentFiles, attachment.getId()));
        }
        CreateDocumentOutput createdDoc = createDocument.execute(createDocumentInput);
        existIds.add(createdDoc.getDocumentId());
      }
    }
  }

  private boolean removeAllAttachments(String courseId, String parentFolderId, String deleteAttachmentId, boolean isAttachment) throws UseCaseException
  {
    DeleteDocumentOutput output = new DeleteDocumentOutput(false);
    List<Attachment> allAttachmentsList;

    if (null != deleteAttachmentId)
    {
      String attachmentFolderId = getAttachmentFolderId(parentFolderId);
      CourseContent courseContent;
      try
      {
        courseContent = courseContentRepository.get(new CourseId(courseId));
        if (isAttachment)
        {
          allAttachmentsList = courseContent.getAttachmentsList();
        }
        else
        {
          allAttachmentsList = courseContent.getAdditionalFileList();
        }
        for (Attachment attachment : allAttachmentsList)
        {
          GetDocumentInput allDocumentInput = new GetDocumentInput(
              attachmentFolderId, attachment.getId().getId() + (attachment.getType().equals(DOCX) ? ".pdf" : attachment.getType()));
          GetDocumentOuput document = getDocument.execute(allDocumentInput);
          DeleteDocumentInput input = new DeleteDocumentInput(document.getDocumentId());
          output = deleteDocument.execute(input);
          if (output.isDeleted())
          {
            LOGGER.info("Successful deleted course attachment with id = [{}]", deleteAttachmentId);
          }
          else
          {
            LOGGER.info("Could not delete course attachment with id = [{}]", deleteAttachmentId);
          }
        }
      }
      catch (LMSRepositoryException e)
      {
        e.printStackTrace();
      }
    }
    return output.isDeleted();
  }

  private boolean removeAttachments(String parentFolderId, String deleteAttachmentId, String attachmentType) throws UseCaseException
  {
    DeleteDocumentOutput output = new DeleteDocumentOutput(false);

    if (null != deleteAttachmentId)
    {
      String attachmentFolderId = getAttachmentFolderId(parentFolderId);
      GetDocumentInput allDocumentInput = new GetDocumentInput(attachmentFolderId,
          deleteAttachmentId + (attachmentType.equals(DOCX) ? ".pdf" : attachmentType));
      GetDocumentOuput document = getDocument.execute(allDocumentInput);
      DeleteDocumentInput input = new DeleteDocumentInput(document.getDocumentId());
      output = deleteDocument.execute(input);
      if (output.isDeleted())
      {
        LOGGER.info("Successful deleted course attachment with id = [{}]", deleteAttachmentId);
      }
      else
      {
        LOGGER.info("Could not delete course attachment with id = [{}]", deleteAttachmentId);
      }
    }
    return output.isDeleted();
  }

  private String getAttachmentFolderId(String parentFolderId) throws UseCaseException
  {
    GetFolderInput input = new GetFolderInput(parentFolderId, ATTACH_FILE_NAME);
    GetFolderOuput attachmentFolder;
    try
    {
      attachmentFolder = getFolder.execute(input);
    }
    catch (UseCaseException e)
    {
      CreateFolderInput createFolderInput = new CreateFolderInput(parentFolderId, ATTACH_FILE_NAME);
      createFolder.execute(createFolderInput);
      attachmentFolder = getFolder.execute(input);
    }
    return attachmentFolder.getId();
  }

  private List<String> getAllDocumentId(String folderId) throws UseCaseException
  {
    GetAllDocumentInput allDocumentInput = new GetAllDocumentInput(folderId);

    GetAllDocumentOutput allDocuments = getAllDocument.execute(allDocumentInput);

    return allDocuments.getDocumentIds();
  }

  private GetCourseContentOutput executeUpdateContent(RestUpdatedContent restUpdatedContent, Map<String, String> updatedDocumentIds)
      throws UseCaseException, LMSRepositoryException
  {

    CourseContent courseContent = courseContentRepository.get(new CourseId(restUpdatedContent.getCourseId()));
    List<RestAttachment> previousAttachmentsList = courseContent.getAttachmentsList()
        .stream()
        .map(restAttachment ->
            new RestAttachment(restAttachment.getId().getId(), restAttachment.getName(), restAttachment.getType())).collect(Collectors.toList());
    List<RestAttachment> previousAdditionalFilesList = courseContent.getAdditionalFileList()
        .stream()
        .map(restAttachment ->
            new RestAttachment(restAttachment.getId().getId(), restAttachment.getName(), restAttachment.getType())).collect(Collectors.toList());
    if (restUpdatedContent.getAttachments() == null)
    {
      previousAttachmentsList = null;
    }
    else
    {
      for (RestAttachment restAttachment : restUpdatedContent.getAttachments())
      {
        previousAttachmentsList.add(restAttachment);
      }
    }
    if (restUpdatedContent.getAttachments() == null)
    {
      previousAttachmentsList = null;
    }
    else
    {
      for (RestAttachment restAttachment : restUpdatedContent.getAdditionalFiles())
      {
        previousAdditionalFilesList.add(restAttachment);
      }
    }
    restUpdatedContent.setAttachments(previousAttachmentsList);
    restUpdatedContent.setAdditionalFiles(previousAdditionalFilesList);
    putDocumentIds(restUpdatedContent.getModules(), updatedDocumentIds);

    UpdateCourseContentInput input = toUpdateInput(restUpdatedContent.getCourseId(), restUpdatedContent);
    UpdateCourseContent updateContent = new UpdateCourseContent(courseContentRepository);

    return updateContent.execute(input);
  }

  private Map<String, String> updateModuleFolders(String courseFolderId, List<UpdatedModule> moduleDetails) throws UseCaseException
  {
    List<UpdateInfo> updateInfos = toUpdateInfos(moduleDetails);

    UpdateSubFolderInput input = new UpdateSubFolderInput(courseFolderId, updateInfos);

    UpdateSubFolderOutput output = updateSubFolder.execute(input);

    return output.getDocumentIds();
  }

  public File saveToFile(MultipartFile file) throws IOException
  {
    if (null == file || file.isEmpty())
    {
      throw new IllegalArgumentException("Attachment file cannot be null or empty!");
    }
    return temporaryFileApi.store(file.getOriginalFilename(), file.getBytes());
  }
}
