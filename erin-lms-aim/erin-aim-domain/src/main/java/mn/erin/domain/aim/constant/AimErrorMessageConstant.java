/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.aim.constant;

/**
 * @author Tamir
 */
public class AimErrorMessageConstant
{
  public static final String AUTHENTICATION_FAILED_USER_DATA_INVALID = "Authentication failed: user data is invalid";

  public static final String INPUT_CANNOT_BE_NULL = "Input cannot be null!";
  // User
  public static final String USER_CANNOT_BE_NULL = "User cannot be null!";
  public static final String USER_ID_CANNOT_BE_NULL = "UserID cannot be null!";
  public static final String TENANT_ID_CANNOT_BE_NULL = "TenantID cannot be null!";
  public static final String IDENTITY_ID_CANNOT_BE_NULL = "Identity ID cannot be null!";
  public static final String USERNAME_CANNOT_BE_NULL = "Username cannot be null!";
  public static final String PASSWORD_CANNOT_BE_NULL = "Password cannot be null!";
  public static final String USER_CONTACT_CANNOT_BE_NULL = "UserContact cannot be null!";
  public static final String USER_EMAIL_CANNOT_BE_NULL = "User email cannot be null!";
  public static final String SOURCE_CANNOT_BE_NULL = "Source cannot be null!";
  public static final String STATUS_CANNOT_BE_NULL = "Status cannot be null!";
  public static final String LAST_MODIFIED_DATE_CANNOT_BE_NULL = "Last modified date cannot be null!";
  public static final String FILE_CANNOT_BE_NULL = "File is empty";

  public static final String REPOSITORY_REGISTRY_CANNOT_BE_NULL = "RepositoryRegistry cannot be null!";
  public static final String TENANT_ID_PROVIDER_CANNOT_BE_NULL = "TenantID provider cannot be null!";
  public static final String USER_PROFILE_REPOSITORY_CANNOT_BE_NULL = "UserProfileRepository cannot be null!";
  public static final String PASSWORD_ENCRYPT_SERVICE_CANNOT_BE_NULL = "PasswordEncryptService cannot be null!";

  // Input constants
  public static final String INVALID_INPUT = "Invalid input!";
  public static final String INVALID_ROLE_ID_INPUT = "Invalid Role ID input!";
  public static final String INVALID_USERNAME_INPUT = "Invalid username input!";
  public static final String INVALID_PASSWORD_INPUT = "Invalid password pattern!";
  public static final String INVALID_USER_ID_INPUT = "Invalid User ID input!";
  public static final String INVALID_GENDER = "Invalid gender!";
  public static final String INVALID_EMAIL_PATTERN = "Invalid email pattern!";
  public static final String INVALID_PHONE_NUMBER_PATTERN = "Invalid phone number pattern!";

  public static final String USER_NOT_FOUND = "User not found!";
  public static final String USER_DOES_NOT_MATCH = "User doesn't match!";
  public static final String TENANT_ID_DOES_NOT_MATCH = "Tenant ID doesn't match!";

  public static final String USERNAME_EXISTS = "Same username of same source exists.";
  public static final String USER_IDENTITY_NOT_FOUND = "User identity not found!";
  public static final String USER_IDENTITY_NOT_CORRECT = "Current user identity not equal requested user identity!";

  public static final String USER_PROFILE_EXISTS = "User profile already exists!";
  public static final String USER_PROFILE_NOT_FOUND = "User profile not found!";

  public static final String USER_STATE_EXISTS = "User state already exists!";
  public static final String USER_STATE_NOT_FOUND = "User state not found!";

  // Rollback constants
  public static final String USER_PROFILE_IS_NOT_CREATED = "User profile is not created!";
  public static final String USER_PROFILE_IS_NOT_UPDATED = "User profile is not updated!";

  private AimErrorMessageConstant()
  {

  }
}
