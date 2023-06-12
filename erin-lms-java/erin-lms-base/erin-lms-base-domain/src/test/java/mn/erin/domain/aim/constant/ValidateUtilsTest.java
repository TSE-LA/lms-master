package mn.erin.domain.aim.constant;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Oyungerel Chuluunsukh
 **/
public class ValidateUtilsTest
{

  @Test
  public void isEmail()
  {
    assertTrue(ValidateUtils.isEmail("oyungerel@erin.systems"));
    assertTrue(ValidateUtils.isEmail("oyu@apple.com.mn"));
    assertTrue(ValidateUtils.isEmail("oyun.gerel@jarvis.lms.mn"));
    assertFalse(ValidateUtils.isEmail("oyun.gerel@"));
  }

  @Test
  public void isUsername()  {
    assertTrue(ValidateUtils.isUsername("oyungerel@erin.systems"));
    assertTrue(ValidateUtils.isUsername("test.a"));
    assertTrue(ValidateUtils.isUsername("oyun-gerel.c"));
    assertTrue(ValidateUtils.isUsername("oyungerel"));
    assertFalse(ValidateUtils.isUsername("oy"));
  }

  @Test
  public void isPhoneNumber()
  {
    assertTrue(ValidateUtils.isPhoneNumber("99887766"));
    assertFalse(ValidateUtils.isPhoneNumber("998877660"));
  }

  @Test
  public void isPassword()
  {
    assertTrue(ValidateUtils.isPassword("Secret123"));
    assertFalse(ValidateUtils.isPassword("secret"));
  }
}