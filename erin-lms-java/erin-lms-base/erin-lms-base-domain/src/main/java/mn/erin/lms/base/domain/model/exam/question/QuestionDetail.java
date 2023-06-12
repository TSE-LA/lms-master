package mn.erin.lms.base.domain.model.exam.question;

import java.util.List;

import mn.erin.domain.base.model.ValueObject;
import mn.erin.lms.base.domain.model.category.QuestionCategoryId;
import mn.erin.lms.base.domain.model.exam.HistoryOfModification;

/**
 * @author Temuulen Naranbold
 */
public class QuestionDetail implements ValueObject<QuestionDetail>
{
  private QuestionCategoryId categoryId;
  private QuestionGroupId groupId;
  private List<HistoryOfModification> historyOfModifications;
  private String contentFolderId;
  private boolean hasImage;
  private String imageName;
  private String correctAnswerText;
  private String wrongAnswerText;

  public QuestionDetail(QuestionCategoryId categoryId, QuestionGroupId groupId,
      List<HistoryOfModification> historyOfModifications, String contentFolderId,
      boolean hasImage, String imageName, String correctAnswerText, String wrongAnswerText)
  {
    this.categoryId = categoryId;
    this.groupId = groupId;
    this.historyOfModifications = historyOfModifications;
    this.contentFolderId = contentFolderId;
    this.hasImage = hasImage;
    this.imageName = imageName;
    this.correctAnswerText = correctAnswerText;
    this.wrongAnswerText = wrongAnswerText;
  }

  public QuestionCategoryId getCategoryId()
  {
    return categoryId;
  }

  public void setCategoryId(QuestionCategoryId categoryId)
  {
    this.categoryId = categoryId;
  }

  public QuestionGroupId getGroupId()
  {
    return groupId;
  }

  public void setGroupId(QuestionGroupId groupId)
  {
    this.groupId = groupId;
  }

  public List<HistoryOfModification> getHistoryOfModifications()
  {
    return historyOfModifications;
  }

  public void setHistoryOfModifications(List<HistoryOfModification> historyOfModifications)
  {
    this.historyOfModifications = historyOfModifications;
  }

  public void addModifyInfo(HistoryOfModification modification)
  {
    this.historyOfModifications.add(modification);
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

  @Override
  public boolean sameValueAs(QuestionDetail other)
  {
    return false;
  }
}
