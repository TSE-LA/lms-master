package mn.erin.lms.base.domain.usecase.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.domain.service.LmsFileSystemService;
import mn.erin.lms.base.domain.usecase.content.dto.ModulePathDto;
import mn.erin.lms.base.domain.usecase.content.dto.PathDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Author.class })
public class GetModulePaths implements UseCase<String, List<ModulePathDto>>
{
  private final LmsFileSystemService lmsFileSystemService;

  public GetModulePaths(LmsFileSystemService lmsFileSystemService)
  {
    this.lmsFileSystemService = Objects.requireNonNull(lmsFileSystemService);
  }

  @Override
  public List<ModulePathDto> execute(String courseId)
  {
    List<LmsFileSystemService.ModulePath> modulePaths = lmsFileSystemService.getModulePaths(courseId);
    List<ModulePathDto> result = new ArrayList<>();

    for (LmsFileSystemService.ModulePath modulePath : modulePaths)
    {
      List<PathDto> paths = modulePath.getSectionPaths().stream().map(sectionPath -> new PathDto(sectionPath.getPath(), sectionPath.getIndex()))
          .collect(Collectors.toList());
      result.add(new ModulePathDto(paths, modulePath.getIndex()));
    }

    return result;
  }
}
