package mn.erin.lms.base.domain.usecase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import mn.erin.common.excel.ExcelWriterUtil;
import mn.erin.domain.aim.model.user.UserStatus;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.RestUserResultForDownload;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;

public class DownloadAllUserInformation extends CourseUseCase<List<RestUserResultForDownload>, byte[]>
{
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

  public DownloadAllUserInformation(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  public byte[] execute(List<RestUserResultForDownload> input) throws UseCaseException
  {
    String sheetTitle = "Нийт хэрэглэгчдийн мэдээлэл." + LocalDateTime.now().format(dateFormatter);
    String[] headers = getHeaders().toArray(new String[0]);
    Object[][] data = convert(input);
    try (ByteArrayOutputStream os = new ByteArrayOutputStream())
    {
      ExcelWriterUtil.write(false, sheetTitle, headers, data, os);
      return os.toByteArray();
    }
    catch (IOException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private Object[][] convert(List<RestUserResultForDownload> input)
  {
    int size = input.size();
    Object[][] data = new Object[size][];

    for (int index = 0; index < input.size(); index++)
    {
      RestUserResultForDownload user = input.get(index);
      String username = getValue(user.getUsername());
      String firstname = getValue(user.getFirstname());
      String lastname = getValue(user.getLastname());
      String email = getValue(user.getEmail());
      String phoneNumber = getValue(user.getPhoneNumber());
      String modifiedDate = getValue(user.getModifiedDate());
      String status = getStatus(user.getStatus());

      Object[] row = { 0, username, firstname, lastname, email, phoneNumber, modifiedDate, status };
      data[index] = row;
    }

    return data;
  }

  private List<String> getHeaders()
  {
    List<String> headers = new ArrayList<>();
    headers.add("№");
    headers.add("Хэрэглэгчийн нэр");
    headers.add("Нэр");
    headers.add("Овог");
    headers.add("Имэйл");
    headers.add("Утас");
    headers.add("Өөрчилсөн огноо");
    headers.add("Статус");
    return headers;
  }

  @NotNull
  private String getValue(String user)
  {
    return user != null ? user : "";
  }

  @NotNull
  private String getStatus(UserStatus user)
  {
    switch (user)
    {
    case ACTIVE:
      return "Идэвхтэй";
    case ARCHIVED:
      return "Идэвхгүй";
    case LOCKED:
      return "Түгжигдсэн";
    default:
      return "";
    }
  }
}
