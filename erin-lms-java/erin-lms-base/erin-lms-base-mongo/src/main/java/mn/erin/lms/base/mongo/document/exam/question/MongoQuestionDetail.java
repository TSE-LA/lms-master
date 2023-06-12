package mn.erin.lms.base.mongo.document.exam.question;

import java.util.ArrayList;
import java.util.List;

import mn.erin.lms.base.mongo.document.exam.MongoHistoryOfModification;

/**
 * @author Temuulen Naranbold
 */
public class MongoQuestionDetail
{
  private List<MongoHistoryOfModification> historyOfModifications = new ArrayList<>();
  private String contentFolderId;
  private boolean hasImage;
  private String imageName;
  private String correctAnswerText;
  private String wrongAnswerText;

  public MongoQuestionDetail()
  {
  }

  public MongoQuestionDetail(List<MongoHistoryOfModification> historyOfModifications, String contentFolderId, boolean hasImage, String imageName,
      String correctAnswerText, String wrongAnswerText)
  {
    this.historyOfModifications = historyOfModifications;
    this.contentFolderId = contentFolderId;
    this.hasImage = hasImage;
    this.imageName = imageName;
    this.correctAnswerText = correctAnswerText;
    this.wrongAnswerText = wrongAnswerText;
  }


  public List<MongoHistoryOfModification> getHistoryOfModifications()
  {
    return historyOfModifications;
  }

  public void setHistoryOfModifications(List<MongoHistoryOfModification> historyOfModifications)
  {
    this.historyOfModifications = historyOfModifications;
  }

  public void addModifyInfo(MongoHistoryOfModification historyOfModification)
  {
    this.historyOfModifications.add(historyOfModification);
  }

  public String getContentFolderId()
  {
    return contentFolderId;
  }

  public void setContentFolderId(String contentFolderId)
  {
    this.contentFolderId = contentFolderId;
  }

  public boolean isHasImage()
  {
    return hasImage;
  }

  public void setHasImage(boolean hasImage)
  {
    this.hasImage = hasImage;
  }

  public String getImageName()
  {
    return imageName;
  }

  public void setImageName(String imageName)
  {
    this.imageName = imageName;
  }

  public String getCorrectAnswerText()
  {
    return correctAnswerText;
  }

  public void setCorrectAnswerText(String correctAnswerText)
  {
    this.correctAnswerText = correctAnswerText;
  }

  public String getWrongAnswerText()
  {
    return wrongAnswerText;
  }

  public void setWrongAnswerText(String wrongAnswerText)
  {
    this.wrongAnswerText = wrongAnswerText;
  }
}
