package mn.erin.domain.aim.usecase.user;

import java.io.InputStream;

public class GenerateUsersFromCsvInput
{
  final InputStream inputStream;

  public GenerateUsersFromCsvInput(InputStream inputStream)
  {
    this.inputStream = inputStream;
  }
}
