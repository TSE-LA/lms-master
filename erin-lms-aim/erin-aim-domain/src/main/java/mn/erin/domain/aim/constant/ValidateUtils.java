/*
 * (C)opyright, 2021, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.aim.constant;

import java.util.regex.Pattern;
/**
 * @author Temuulen 2021
 */
public class ValidateUtils
{
  private ValidateUtils(){
  }
  public static boolean isEmail(String email)
  {
    Pattern emailRegex = Pattern.compile("^[a-zA-Z0-9\\\\d_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z\\\\d.-]+$");
    return emailRegex.matcher(email).matches();
  }
  public static boolean isUsernameUnicode (String username){
    Pattern phoneNumberRegex = Pattern.compile("[\\p{L}\\w$#+-_!@]{4,}", Pattern.UNICODE_CHARACTER_CLASS);
    return phoneNumberRegex.matcher(username).matches();
  }
  public static boolean isPhoneNumber(String phoneNumber)
  {
    Pattern phoneNumberRegex = Pattern.compile("[0-9]{8}");
    return phoneNumberRegex.matcher(phoneNumber).matches();
  }
  public static boolean isPassword(String password)
  {
    Pattern passwordRegex = Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,})");
    return passwordRegex.matcher(password).matches();
  }
  public static boolean isUsername(String username){
    Pattern usernameRegex = Pattern.compile("^[a-zA-Z0-9]([@._\\-](?![@._\\-])|[a-zA-Z0-9]){4,}$");
    return usernameRegex.matcher(username).matches();
  }
}
