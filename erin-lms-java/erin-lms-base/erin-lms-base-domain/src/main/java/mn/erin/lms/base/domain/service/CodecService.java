package mn.erin.lms.base.domain.service;

import java.io.IOException;

/**
 * @author Byambajav
 */
public interface CodecService
{
  String videoProperty(String videoFile) throws IOException;
  String  convertToH264(String filePath, String fileName) throws IOException;
  double getPercentage(String id);
}
