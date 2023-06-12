package mn.erin.lms.base.dms;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.model.Content;
import mn.erin.domain.dms.model.document.DocumentContent;
import mn.erin.lms.base.domain.model.content.CourseModule;
import mn.erin.lms.base.domain.model.content.CourseSection;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.service.ImageService;
import mn.erin.lms.base.domain.service.LmsFileSystemService;
import mn.erin.lms.base.domain.service.TemporaryFileApi;
import mn.erin.lms.base.domain.service.ThumbnailService;
import mn.erin.lms.base.domain.util.VideoCutter;
import mn.erin.lms.base.scorm.model.AssetId;
import mn.erin.lms.base.scorm.model.ContentAggregation;
import mn.erin.lms.base.scorm.repository.SCORMContentRepository;
import mn.erin.lms.base.scorm.repository.SCORMRepositoryException;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class DmsThumbnailService implements ThumbnailService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DmsThumbnailService.class);
  private static final String ERROR_GIF = "Could not create thumbnail GIF file.";

  private final ImageService imageService;
  private final LmsFileSystemService lmsFileSystemService;
  private final SCORMContentRepository scormContentRepository;
  private final CourseRepository courseRepository;
  private final TemporaryFileApi temporaryFileApi;

  public DmsThumbnailService(ImageService imageService, LmsFileSystemService lmsFileSystemService,
      SCORMContentRepository scormContentRepository, CourseRepository courseRepository, TemporaryFileApi temporaryFileApi)
  {
    this.imageService = imageService;
    this.lmsFileSystemService = lmsFileSystemService;
    this.scormContentRepository = scormContentRepository;
    this.courseRepository = courseRepository;
    this.temporaryFileApi = temporaryFileApi;
  }

  @Override
  public String getThumbnailUrl(String courseId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getDefaultThumbnailUrl()
  {
    return "/alfresco/default-thumbnail.jpeg";
  }

  @Override
  public Content createThumbnailFromVideo(String videoFile)
  {
    VideoCutter.cut(videoFile);
    Path file = Paths.get(videoFile.replace(".mp4", "") + " - copy.mp4");

    if (Files.exists(file))
    {
      try
      {
        return new DocumentContent(Files.readAllBytes(file));
      }
      catch (IOException e)
      {
        LOGGER.error(e.getMessage(), e);
      }
    }
    return null;
  }

  @Override
  public void updateThumbnailToGIF(Course course, String thumbnailName)
  {
    try
    {
      String courseFolderId = lmsFileSystemService.getCourseFolderId(course.getCourseId().getId());
      File file = lmsFileSystemService.findThumbnail(course.getCourseId().getId(), thumbnailName);
      Content gif = imageService.convertVideoToGIF(file.getAbsolutePath());
      Content jpeg = imageService.convertVideoToJPEG(file.getAbsolutePath());
      String newName = UUID.randomUUID().toString();
      if (gif == null || jpeg == null)
      {
        updateCourseThumbnailUrl(course, course.getCourseId(), getDefaultThumbnailUrl());
        throw new IOException(ERROR_GIF);
      }
      boolean isCreatedThumbnail = lmsFileSystemService.createThumbnail(courseFolderId, newName, gif.getContent(), jpeg.getContent());
      if (isCreatedThumbnail)
      {
        updateCourseThumbnailUrl(course, course.getCourseId(), newName);
        lmsFileSystemService.deleteThumbnail(course.getCourseId().getId(), thumbnailName);
      }
      else
      {
        updateCourseThumbnailUrl(course, course.getCourseId(), getDefaultThumbnailUrl());
      }
    }
    catch (LmsRepositoryException | IOException e)
    {
      LOGGER.error(e.getMessage(), e);
    }
  }

  @Override
  public String updateThumbnailToGIF(String courseId, File file) throws IOException
  {
    Validate.notBlank(courseId);
    if (file == null || !file.exists())
    {
      throw new IOException("Input file doesn't exist.");
    }

    try
    {
      Course course = courseRepository.fetchById(CourseId.valueOf(courseId));
      String[] split = course.getCourseDetail().getThumbnailUrl().split("/");
      String thumbnailName = split[split.length - 1];
      String courseFolderId = lmsFileSystemService.getCourseFolderId(course.getCourseId().getId());
      if (StringUtils.isBlank(courseFolderId))
      {
        throw new IllegalStateException("Cannot found the courseFolderId by " + courseFolderId);
      }
      Content gif = imageService.convertVideoToGIF(file.getAbsolutePath());
      Content jpeg = imageService.convertVideoToJPEG(file.getAbsolutePath());

      if (gif == null || jpeg == null)
      {
        throw new IllegalStateException("Couldn't convert the file to GIF, JPEG from  " + file.getName());
      }

      String newName = UUID.randomUUID().toString();
      boolean isCreatedThumbnail = lmsFileSystemService.createThumbnail(courseFolderId, newName, gif.getContent(), jpeg.getContent());
      if (isCreatedThumbnail)
      {
        if (!course.getCourseDetail().getThumbnailUrl().equals(getDefaultThumbnailUrl()))
        {
          lmsFileSystemService.deleteThumbnail(course.getCourseId().getId(), thumbnailName);
          lmsFileSystemService.deleteThumbnail(course.getCourseId().getId(), thumbnailName.replace(".jpeg", ".gif"));
        }
        updateCourseThumbnailUrl(course, course.getCourseId(), newName);
      }
      return newName;
    }
    catch (LmsRepositoryException | IOException e)
    {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
  }

  @Override
  public void setThumbnailUrlFromCourseContents(List<CourseModule> modules, Course course, String courseFolderId)
  {
    for (CourseModule module : modules)
    {
      for (CourseSection section : module.getSectionList())
      {
        AssetId assetId = AssetId.valueOf(section.getAttachmentId().getId());
        try
        {
          ContentAggregation.Resource resource = scormContentRepository.getResource(assetId);
          String documentName = resource.getAssetName();
          if (documentName.endsWith(".mp4") || documentName.endsWith(".webm"))
          {
            createAndSetThumbnail(module, course, courseFolderId, documentName);
          }
        }
        catch (SCORMRepositoryException | LmsRepositoryException | IOException e)
        {
          LOGGER.error("Failed to get course contents", e);
        }
      }
    }
  }

  private void createAndSetThumbnail(CourseModule module, Course course, String courseFolderId, String documentName) throws IOException, LmsRepositoryException
  {
    String courseContentFolderId = lmsFileSystemService.getCourseModuleFolderId(courseFolderId, module.getModuleFolderId());
    Content content = lmsFileSystemService.getDocumentContent(courseContentFolderId, documentName);
    File tempFile = temporaryFileApi.store(UUID.randomUUID() + ".mp4", content.getContent());

    if (tempFile == null || !tempFile.exists())
    {
      throw new IOException("Could not create file object from " + documentName + " video file.");
    }

    Content gif = imageService.convertVideoToGIF(tempFile.getAbsolutePath());
    Content jpeg = imageService.convertVideoToJPEG(tempFile.getAbsolutePath());
    tempFile.deleteOnExit();
    if (gif == null || jpeg == null)
    {
      throw new IOException(ERROR_GIF);
    }
    String thumbnailName = UUID.randomUUID().toString();
    boolean created = lmsFileSystemService.createThumbnail(courseFolderId, thumbnailName, gif.getContent(), jpeg.getContent());
    if (created)
    {
      updateCourseThumbnailUrl(course, course.getCourseId(), thumbnailName);
    }
  }

  private void updateCourseThumbnailUrl(Course course, CourseId courseId, String thumbnailName) throws LmsRepositoryException
  {
    CourseDetail courseDetail = course.getCourseDetail();
    courseDetail.setThumbnailUrl("/alfresco/Courses/" + courseId.getId() + "/" + thumbnailName + ".jpeg");
    courseRepository.update(courseId, course.getCourseCategoryId(), courseDetail, course.getCourseType(), course.getAssessmentId(), course.getCertificateId());
  }
}
