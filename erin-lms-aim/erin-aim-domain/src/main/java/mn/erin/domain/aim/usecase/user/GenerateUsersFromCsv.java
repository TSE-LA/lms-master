package mn.erin.domain.aim.usecase.user;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import mn.erin.domain.aim.constant.ValidateUtils;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserContact;
import mn.erin.domain.aim.model.user.UserDuplicity;
import mn.erin.domain.aim.model.user.UserGender;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.model.user.UserIdentitySource;
import mn.erin.domain.aim.model.user.UserInfo;
import mn.erin.domain.aim.model.user.UserProfile;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.service.PasswordEncryptService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mn.erin.domain.aim.constant.AimErrorMessageConstant.INVALID_INPUT;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.INVALID_PHONE_NUMBER_PATTERN;

/**
 * @author Munkh
 */
public class GenerateUsersFromCsv implements UseCase<GenerateUsersFromCsvInput, UserDuplicity>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GenerateUsersFromCsv.class);

  private final AimRepositoryRegistry registry;
  private final TenantIdProvider tenantIdProvider;
  private final PasswordEncryptService passwordEncryptService;
  private int registeredUsers = 0;
  private List<String> registeredUsersId = new ArrayList<>();

  public GenerateUsersFromCsv(AimRepositoryRegistry registry, TenantIdProvider tenantIdProvider, PasswordEncryptService passwordEncryptService)
  {
    this.registry = registry;
    this.tenantIdProvider = tenantIdProvider;
    this.passwordEncryptService = passwordEncryptService;
  }

  @Override
  public UserDuplicity execute(GenerateUsersFromCsvInput input) throws UseCaseException
  {
    Validate.notNull(input);
    List<UserImportData> importList;
    List<String> duplicated = new ArrayList<>();
    List<String> usersFailedToCreate = new ArrayList<>();
    List<String> headers;

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(input.inputStream)))
    {
      headers = new ArrayList<>(Arrays.asList(reader.readLine().split(",")));

      importList = reader.lines()
          .map(line -> new UserImportData(line.split(",")))
          .collect(Collectors.toList());
    }
    catch (IOException e)
    {
      throw new UseCaseException("Failed to import users", e);
    }
    for (UserImportData importData : importList)
    {
      try
      {
        if (!ValidateUtils.isEmail(importData.email) ||
            !ValidateUtils.isUsername(importData.username) || !ValidateUtils.isPassword(importData.password))
        {
          throw new UseCaseException(INVALID_INPUT);
        }
        if (!importData.phone.equals("") && !ValidateUtils.isPhoneNumber(importData.phone))
        {
          throw new UseCaseException(INVALID_PHONE_NUMBER_PATTERN);
        }
      }
      catch (UseCaseException e)
      {
        usersFailedToCreate.add(importData.username);
      }
    }
    if (usersFailedToCreate.isEmpty())
    {
      for (UserImportData importData : importList)
      {
        if (!registry.getUserIdentityRepository().existByUsername(importData.username, UserIdentitySource.OWN))
        {
          registerNewUser(importData, headers);
        }
        else
        {
          duplicated.add(importData.username);
        }
      }
    }

    return new UserDuplicity(duplicated, registeredUsers, usersFailedToCreate);
  }

  boolean registerNewUser(UserImportData user, List<String> headers)
  {
    boolean isCreated = true;
    UserId userId = null;
    TenantId tenantId = TenantId.valueOf(tenantIdProvider.getCurrentUserTenantId());
    try
    {

      User newUser = registry.getUserRepository().createUser(tenantId);
      userId = newUser.getUserId();
      UserContact contact = new UserContact(user.email, user.phone);

      Map<String, String> properties = new HashMap<>();
      for (Map.Entry<Integer, String> entry : user.properties.entrySet())
      {
        properties.put(headers.get(entry.getKey()), entry.getValue());
      }

      LocalDateTime birthday = (new SimpleDateFormat("yyyy-MM-dd").parse(user.birthday)).toInstant().atZone(ZoneId.of("Asia/Ulaanbaatar")).toLocalDateTime();

      if (!StringUtils.isBlank(properties.get("appointedDate")))
      {
        LocalDateTime appointedDate = (new SimpleDateFormat("yyyy-MM-dd").parse(properties.get("appointedDate"))).toInstant().atZone(ZoneId.of("Asia/Ulaanbaatar")).toLocalDateTime();
        LocalDateTime currentDate = LocalDateTime.now();
        if (currentDate.isAfter(appointedDate))
        {
          Period interval = Period.between(appointedDate.toLocalDate(), currentDate.toLocalDate());
          int jobYear = interval.getYears();
          int jobMonth = interval.getMonths();
          if (jobMonth != 0)
          {
            properties.put("jobYear", jobYear + " жил " + jobMonth + " сар");
          }
          else
          {
            properties.put("jobYear", jobYear + " жил");
          }
        }
      }

      registry.getUserProfileRepository().create(
          new UserProfile(userId,
              new UserInfo(user.firstName, user.lastName, getGenderInput(user.gender), birthday, user.jobTitle, "", "", properties), contact));
      registry.getUserIdentityRepository()
          .create(new UserIdentity(userId, user.username, passwordEncryptService.encrypt(user.password), UserIdentitySource.OWN));
      this.registeredUsersId.add(userId.getId());
      this.registeredUsers++;
    }
    catch (AimRepositoryException | ParseException e)
    {
      // rollback
      if (userId != null)
      {
        rollback(userId.getId());
      }
      LOGGER.error("Failed to create user [{}]", user.username);
      isCreated = false;
    }
    return isCreated;
  }

  void rollback(String id)
  {
    UserId userId = UserId.valueOf(id);
    registry.getUserProfileRepository().delete(userId);
    registry.getUserIdentityRepository().delete(userId);
    registry.getUserRepository().delete(userId);
  }

  UserGender getGenderInput(String data)
  {
    if (data.equalsIgnoreCase("female") || data.equalsIgnoreCase("male"))
    {
      return UserGender.valueOf(data.toUpperCase());
    }
    else
    {
      return UserGender.NA;
    }
  }

  private static class UserImportData
  {
    private final String username;
    private final String email;
    private final String password;
    private final String phone;
    private final String lastName;
    private final String firstName;
    private final String gender;
    private final String jobTitle;
    private final String birthday;
    private final Map<Integer, String> properties = new HashMap<>();

    public UserImportData(Object[] data)
    {
      this.username = (String) data[0];
      this.email = (String) data[1];
      this.password = (String) data[2];
      this.phone = data.length > 3 ? (String) data[3] : "";
      this.lastName = data.length > 4 ? (String) data[4] : "";
      this.firstName = data.length > 5 ? (String) data[5] : "";
      this.gender = data.length > 6 ? (String) data[6] : "";
      this.jobTitle = data.length > 7 ? (String) data[7] : "";
      this.birthday = data.length > 8 ? (String) data[8] : "";
      for (int i = 9; i < data.length; i++)
      {
        this.properties.put(i, (String) data[i]);
      }
      this.properties.put(7, this.jobTitle);
    }
  }
}
