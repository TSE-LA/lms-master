package mn.erin.lms.base.domain.usecase.exam.dto.question;

import java.util.Date;
import java.util.List;

import mn.erin.lms.base.domain.model.exam.HistoryOfModification;

/**
 * @author Oyungerel Chuluunsukh
 **/
public class QuestionDetailedDto
{
  private String id;
  private String value;
  private String groupId;
  private String categoryId;
  private int score;
  private String type;
  private String author;
  private Date createdDate;
  private List<AnswerDto> answers;
  private List<HistoryOfModification> history;
  private String fileId;
  private String fileName;
  private boolean hasImage;
  private String modifiedUser;
  private Date modifiedDate;
  private String correctText;
  private String wrongText;

  public QuestionDetailedDto(String id, String value)
  {
    this.id = id;
    this.value = value;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getValue()
  {
    return value;
  }

  public void setValue(String value)
  {
    this.value = value;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String groupId)
  {
    this.groupId = groupId;
  }

  public String getCategoryId()
  {
    return categoryId;
  }

  public void setCategoryId(String categoryId)
  {
    this.categoryId = categoryId;
  }

  public int getScore()
  {
    return score;
  }

  public void setScore(int score)
  {
    this.score = score;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public String getAuthor()
  {
    return author;
  }

  public void setAuthor(String author)
  {
    this.author = author;
  }

  public Date getCreatedDate()
  {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate)
  {
    this.createdDate = createdDate;
  }

  public String getModifiedUser()
  {
    return modifiedUser;
  }

  public void setModifiedUser(String modifiedUser)
  {
    this.modifiedUser = modifiedUser;
  }

  public Date getModifiedDate()
  {
    return modifiedDate;
  }

  public void setModifiedDate(Date modifiedDate)
  {
    this.modifiedDate = modifiedDate;
  }

  public List<AnswerDto> getAnswers()
  {
    return answers;
  }

  public void setAnswers(List<AnswerDto> answers)
  {
    this.answers = answers;
  }

  public List<HistoryOfModification> getHistory()
  {
    return history;
  }

  public void setHistory(List<HistoryOfModification> history)
  {
    this.history = history;
  }

  public String getWrongText()
  {
    return wrongText;
  }

  public void setWrongText(String wrongText)
  {
    this.wrongText = wrongText;
  }

  public String getCorrectText()
  {
    return correctText;
  }

  public void setCorrectText(String correctText)
  {
    this.correctText = correctText;
  }

  public String getFileId()
  {
    return fileId;
  }

  public void setFileId(String fileId)
  {
    this.fileId = fileId;
  }

  public boolean isHasImage()
  {
    return hasImage;
  }

  public void setHasImage(boolean hasImage)
  {
    this.hasImage = hasImage;
  }

  public String getFileName()
  {
    return fileName;
  }

  public void setFileName(String fileName)
  {
    this.fileName = fileName;
  }
}
