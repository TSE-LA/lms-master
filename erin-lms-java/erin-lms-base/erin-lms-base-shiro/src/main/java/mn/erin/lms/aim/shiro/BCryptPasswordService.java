package mn.erin.lms.aim.shiro;

import org.apache.shiro.authc.credential.PasswordService;
import org.springframework.security.crypto.bcrypt.BCrypt;

import mn.erin.domain.aim.service.PasswordEncryptService;

/**
 * @author Munkh
 */
public class BCryptPasswordService implements PasswordService, PasswordEncryptService
{
  @Override
  public String encryptPassword(Object plaintextPassword)
  {
    final String str;
    if (plaintextPassword instanceof char[])
    {
      str = new String((char[]) plaintextPassword);
    }
    else if (plaintextPassword instanceof String)
    {
      str = (String) plaintextPassword;
    }
    else
    {
      throw new SecurityException("Unsupported password type: " + plaintextPassword.getClass().getName());
    }
    return BCrypt.hashpw(str, BCrypt.gensalt());
  }

  @Override
  public boolean passwordsMatch(Object submittedPlaintext, String encrypted)
  {
    return BCrypt.checkpw(new String((char[]) submittedPlaintext), encrypted);
  }

  @Override
  public String encrypt(String plainPassword)
  {
    return encryptPassword(plainPassword);
  }
}
