package mn.erin.domain.aim.model;

import mn.erin.domain.aim.model.user.UserGender;
import mn.erin.domain.aim.model.user.UserInfo;
import org.junit.Assert;
import org.junit.Test;

public class UserInfoTest
{

  @Test
  public void getUserNameReturnsFirstNameWhenLastNameNull()
  {
    UserInfo info = new UserInfo("Лхагва", null, UserGender.MALE, null, null, null, null, null);
    Assert.assertEquals("Лхагва", info.getUserName());
  }

  @Test
  public void getUserNameReturnsFirstNameWhenLastNameEmpty()
  {
    UserInfo info = new UserInfo("Лхагва", "", UserGender.MALE, null, null, null, null, null);
    Assert.assertEquals("Лхагва", info.getUserName());
  }

  @Test
  public void getUserNameReturnsCorrectNameWhenLastNameNotBlank()
  {
    UserInfo info = new UserInfo("Лхагва", "Амар", UserGender.MALE, null, null, null, null, null);
    Assert.assertEquals("А.Лхагва", info.getUserName());
  }

  @Test
  public void getUserNameReturnsCorrectNameWhenFirstNameNull()
  {
    UserInfo info = new UserInfo(null, "Амар", UserGender.MALE, null, null, null, null, null);
    Assert.assertEquals("(empty name)", info.getUserName());
  }

  @Test
  public void getUserNameReturnsCorrectNameWhenFirstNameEmpty()
  {
    UserInfo info = new UserInfo("", "Амар", UserGender.MALE, null, null, null, null, null);
    Assert.assertEquals("(empty name)", info.getUserName());
  }

  @Test
  public void getUserNameReturnsCorrectNameWhenFirstNameNotBlank()
  {
    UserInfo info = new UserInfo("Лхагва", "Амар", UserGender.MALE, null, null, null, null, null);
    Assert.assertEquals("А.Лхагва", info.getUserName());
  }

  @Test
  public void getUserNameReturnsCorrectNameWhenNull()
  {
    UserInfo info = new UserInfo(null, null, UserGender.MALE, null, null, null, null, null);
    Assert.assertEquals("(empty name)", info.getUserName());
  }

  @Test
  public void getUserNameReturnsCorrectNameWhenEmpty()
  {
    UserInfo info = new UserInfo("", "", UserGender.MALE, null, null, null, null, null);
    Assert.assertEquals("(empty name)", info.getUserName());
  }
}
