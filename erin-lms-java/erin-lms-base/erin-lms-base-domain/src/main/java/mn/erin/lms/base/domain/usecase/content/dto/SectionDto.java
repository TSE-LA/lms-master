package mn.erin.lms.base.domain.usecase.content.dto;

import java.nio.file.Path;
import java.util.Objects;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class SectionDto
{
  private final Integer pageNumber;
  private final Path file;

  public SectionDto(Integer pageNumber, Path file)
  {
    this.pageNumber = Objects.requireNonNull(pageNumber);
    this.file = Objects.requireNonNull(file);
  }

  public Integer getPageNumber()
  {
    return pageNumber;
  }

  public Path getFile()
  {
    return file;
  }
}
