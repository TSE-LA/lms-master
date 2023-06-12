package mn.erin.lms.legacy.infrastructure.unitel.notification.sms;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.common.sms.SmsSender;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LegacyUnitelSmsSender implements SmsSender
{
  private static final Logger LOGGER = LoggerFactory.getLogger(LegacyUnitelSmsSender.class);
  private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
  private static final String URI = "http://10.21.64.200:8100/esme/submit";
  private static final String MESSAGE = "message";
  private static final String TO = "to";
  private static final String FROM = "from";

  @Override
  public boolean sendSms(String phoneNumber, String message)
  {
    JSONObject msgBody = new JSONObject();
    msgBody.put(TO, phoneNumber);
    msgBody.put(FROM, "8008");
    msgBody.put(MESSAGE, message);

    RequestBody requestBody = RequestBody.create(msgBody.toString(), JSON);

    Request request = new Request.Builder()
        .url(HttpUrl.parse(URI))
        .post(requestBody)
        .build();

    OkHttpClient client = new OkHttpClient();
    try (Response response = client.newCall(request).execute())
    {
      LOGGER.info("Sending sms to: [{}]", phoneNumber);
      LOGGER.info("Response status: {}", response.isSuccessful());
      return response.isSuccessful();
    }
    catch (IOException e)
    {
      LOGGER.error(e.getMessage());
      return false;
    }
  }
}
