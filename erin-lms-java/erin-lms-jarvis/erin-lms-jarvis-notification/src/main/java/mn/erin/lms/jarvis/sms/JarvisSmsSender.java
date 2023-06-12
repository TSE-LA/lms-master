package mn.erin.lms.jarvis.sms;

import mn.erin.common.sms.SmsSender;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class JarvisSmsSender implements SmsSender
{
  @Override
  public boolean sendSms(String phoneNumber, String message)
  {
    return false;
  }
}
