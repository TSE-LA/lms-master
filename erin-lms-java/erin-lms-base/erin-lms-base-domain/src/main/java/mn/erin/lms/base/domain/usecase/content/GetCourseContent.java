package mn.erin.lms.base.domain.usecase.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.content.Attachment;
import mn.erin.lms.base.domain.model.content.CourseContent;
import mn.erin.lms.base.domain.model.content.CourseModule;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.domain.repository.CourseContentRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.usecase.content.dto.CourseContentDto;
import mn.erin.lms.base.domain.usecase.content.dto.ModuleInfo;
import mn.erin.lms.base.domain.util.ContentUtils;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Author.class })
public class GetCourseContent implements UseCase<String, CourseContentDto>
{
  private final CourseContentRepository courseContentRepository;

  public GetCourseContent(CourseContentRepository courseContentRepository)
  {
    this.courseContentRepository = Objects.requireNonNull(courseContentRepository);
  }

  @Override
  public CourseContentDto execute(String input) throws UseCaseException
  {
    CourseId courseId = CourseId.valueOf(input);

    try
    {
      CourseContent courseContent = courseContentRepository.fetchById(courseId);
      return toOutput(courseContent);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private CourseContentDto toOutput(CourseContent courseContent)
  {
    CourseId courseId = courseContent.getCourseId();

    List<CourseModule> courseModules = courseContent.getModules();
    List<ModuleInfo> moduleInfos = ContentUtils.toModuleInfos(courseModules);
    List<Attachment> attachments = new ArrayList<>();
    for (Attachment attachment : courseContent.getAttachments())
    {
      String fileName = attachment.getName().contains("uuid") ? attachment.getName().split("uuid")[1]
          .concat("." + FilenameUtils.getExtension(attachment.getName())) : attachment.getName();
      attachment.setName(fileName);
      attachments.add(attachment);
    }

    return new CourseContentDto(courseId.getId(), attachments, moduleInfos);
  }
}
