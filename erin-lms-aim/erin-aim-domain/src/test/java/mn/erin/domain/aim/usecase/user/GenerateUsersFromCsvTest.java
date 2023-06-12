package mn.erin.domain.aim.usecase.user;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserDuplicity;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserIdentitySource;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.repository.UserIdentityRepository;
import mn.erin.domain.aim.repository.UserProfileRepository;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.aim.service.PasswordEncryptService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.base.usecase.UseCaseException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Dashnyam Bayarsaikhan
 */
@Ignore
public class GenerateUsersFromCsvTest
{
  private GenerateUsersFromCsv generateUsersFromCsv;
  private TenantIdProvider tenantIdProvider;
  private AimRepositoryRegistry registry;
  private PasswordEncryptService passwordEncryptService;
  private GenerateUsersFromCsvInput generateUsersFromCsvInputRight;
  private GenerateUsersFromCsvInput generateUsersFromCsvInputWrong;
  private InputStream inputStream;
  private UserIdentityRepository getUserIdentityRepository;
  private UserProfileRepository userProfileRepository;
  private UserRepository userRepository;
  private User user;
  private UserId userId;

  @Before
  public void setUp() throws Exception
  {
    this.registry = mock(AimRepositoryRegistry.class);
    this.tenantIdProvider = mock(TenantIdProvider.class);
    this.passwordEncryptService = mock(PasswordEncryptService.class);
    this.getUserIdentityRepository = mock(UserIdentityRepository.class);
    this.userProfileRepository = mock(UserProfileRepository.class);
    this.userRepository = mock(UserRepository.class);
    this.user = mock(User.class);
    userId = mock(UserId.class);
    when(registry.getUserIdentityRepository()).thenReturn(this.getUserIdentityRepository);
    when(registry.getUserProfileRepository()).thenReturn(this.userProfileRepository);
    when(registry.getUserRepository()).thenReturn(this.userRepository);
    when(user.getUserId()).thenReturn(userId);
    when(tenantIdProvider.getCurrentUserTenantId()).thenReturn("tenantId");
    String currentDir = System.getProperty("user.dir");
    File initialFileRight = Paths.get(currentDir,"\\src\\test\\java\\mn\\erin\\domain\\aim\\usecase\\user\\csvUserFullRight.csv").toFile();
    File initialFileWrong = Paths.get(currentDir, "\\src\\test\\java\\mn\\erin\\domain\\aim\\usecase\\user\\csvUserFullWrong.csv").toFile();
    InputStream inputStreamRight = new FileInputStream(initialFileRight);
    InputStream inputStreamWrong = new FileInputStream(initialFileWrong);
    this.generateUsersFromCsvInputRight = new GenerateUsersFromCsvInput(inputStreamRight);
    this.generateUsersFromCsvInputWrong = new GenerateUsersFromCsvInput(inputStreamWrong);
    generateUsersFromCsv = new GenerateUsersFromCsv(this.registry, this.tenantIdProvider, this.passwordEncryptService);
  }

  @Test(expected = NullPointerException.class)
  public void whenInputIsNull() throws UseCaseException
  {
    generateUsersFromCsv.execute(null);
  }

  @Test(expected = NullPointerException.class)
  public void whenInputStreamIsNull() throws UseCaseException
  {
    generateUsersFromCsv.execute(new GenerateUsersFromCsvInput(null));
  }

  @Test
  public void registerNewUsersWithDuplications() throws UseCaseException
  {
    when(this.registry.getUserIdentityRepository().existByUsername(anyString(),any(UserIdentitySource.class)))
        .thenReturn(true);
    UserDuplicity userDuplicity = generateUsersFromCsv.execute(this.generateUsersFromCsvInputRight);
    assertEquals(10,userDuplicity.getDuplicatedUsers().size() );
  }
  @Test
  public void registerUsersWithWrongFormat() throws UseCaseException
  {
    when(registry.getUserIdentityRepository().existByUsername(anyString(), any(UserIdentitySource.class)))
        .thenReturn(false);
    User userTest = new User(UserId.valueOf("userId"), TenantId.valueOf("tenantId"));

    when(tenantIdProvider.getCurrentUserTenantId()).thenReturn("Dashnyam");
    TenantId tenantId = mock(TenantId.class);
    when(registry.getUserRepository()).thenReturn(userRepository);
    when(userRepository.createUser(TenantId.valueOf("Dashnyam"))).thenReturn(userTest);
    when(user.getUserId()).thenReturn(userId);
    UserDuplicity userRegistered = generateUsersFromCsv.execute(this.generateUsersFromCsvInputWrong);
    assertEquals(0,userRegistered.getRegisteredUserCount().intValue());
  }

  @Test
  public void registerUsersWithWrongEmailFormat() throws UseCaseException
  {
    when(registry.getUserIdentityRepository().existByUsername(anyString(), any(UserIdentitySource.class)))
        .thenReturn(false);
    User userTest = new User(UserId.valueOf("userId"), TenantId.valueOf("tenantId"));
    when(tenantIdProvider.getCurrentUserTenantId()).thenReturn("Dashnyam");
    TenantId tenantId = mock(TenantId.class);
    when(registry.getUserRepository()).thenReturn(userRepository);
    when(userRepository.createUser(TenantId.valueOf("Dashnyam"))).thenReturn(userTest);

    UserDuplicity userRegistered = generateUsersFromCsv.execute(this.generateUsersFromCsvInputWrong);
    assertEquals(0,userRegistered.getRegisteredUserCount().intValue());
  }

  @Test
  public void registerUsersWithWrongPasswordFormat() throws UseCaseException
  {
    when(registry.getUserIdentityRepository().existByUsername(anyString(), any(UserIdentitySource.class)))
        .thenReturn(false);

    User userTest = new User(UserId.valueOf("userId"), TenantId.valueOf("tenantId"));
    when(tenantIdProvider.getCurrentUserTenantId()).thenReturn("Dashnyam");
    TenantId tenantId = mock(TenantId.class);
    when(registry.getUserRepository()).thenReturn(userRepository);
    when(userRepository.createUser(TenantId.valueOf("Dashnyam"))).thenReturn(userTest);
    UserDuplicity userRegistered = generateUsersFromCsv.execute(this.generateUsersFromCsvInputWrong);
    assertEquals(0,userRegistered.getRegisteredUserCount().intValue());
  }

  @Test
  public void registerUsersWithWrongUsernameFormat() throws UseCaseException
  {
    when(registry.getUserIdentityRepository().existByUsername(anyString(), any(UserIdentitySource.class)))
        .thenReturn(false);

    User userTest = new User(UserId.valueOf("userId"), TenantId.valueOf("tenantId"));
    when(tenantIdProvider.getCurrentUserTenantId()).thenReturn("Dashnyam");
    TenantId tenantId = mock(TenantId.class);
    when(registry.getUserRepository()).thenReturn(userRepository);
    when(userRepository.createUser(TenantId.valueOf("Dashnyam"))).thenReturn(userTest);

    UserDuplicity userRegistered = generateUsersFromCsv.execute(this.generateUsersFromCsvInputWrong);
    assertEquals(0,userRegistered.getRegisteredUserCount().intValue());
  }
  @Test

  public void registerUsersWithWrongPhoneNumberFormat() throws UseCaseException
  {
    when(registry.getUserIdentityRepository().existByUsername(anyString(), any(UserIdentitySource.class)))
        .thenReturn(false);

    User userTest = new User(UserId.valueOf("userId"), TenantId.valueOf("tenantId"));
    when(tenantIdProvider.getCurrentUserTenantId()).thenReturn("Dashnyam");
    TenantId tenantId = mock(TenantId.class);
    when(registry.getUserRepository()).thenReturn(userRepository);
    when(userRepository.createUser(TenantId.valueOf("Dashnyam"))).thenReturn(userTest);

    UserDuplicity userRegistered = generateUsersFromCsv.execute(this.generateUsersFromCsvInputWrong);
    assertEquals(0,userRegistered.getRegisteredUserCount().intValue());
  }

//  @Test
//  public void registerUsersWithCorrectFormat() throws UseCaseException
//  {
//    when(this.registry.getUserIdentityRepository().existByUsername(anyString(), any(UserIdentitySource.class))).thenReturn(false);
//    User userTest = new User(UserId.valueOf("userId"), TenantId.valueOf("tenantId"));
//    when(tenantIdProvider.getCurrentUserTenantId()).thenReturn("Dashnyam");
//    when(registry.getUserRepository()).thenReturn(userRepository);
//    when(userRepository.createUser(TenantId.valueOf("Dashnyam"))).thenReturn(userTest);
//    when(registry.getUserRepository().createUser(any(TenantId.class))).thenReturn(user);
//    UserDuplicity userRegistered = generateUsersFromCsv.execute(this.generateUsersFromCsvInputRight);
//    assertEquals(10,userRegistered.getRegisteredUserCount().intValue());
//  }
}
