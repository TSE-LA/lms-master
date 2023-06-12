package mn.erin.lms.base.domain.service;

import java.io.IOException;

import mn.erin.domain.base.model.Content;

/**
 * @author Temuulen Naranbold
 */
public interface ImageService
{
  Content convertVideoToGIF(String fileName) throws IOException;
  Content convertVideoToJPEG(String fileName) throws IOException;
}
