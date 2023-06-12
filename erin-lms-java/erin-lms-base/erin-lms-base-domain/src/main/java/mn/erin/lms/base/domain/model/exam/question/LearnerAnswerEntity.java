package mn.erin.lms.base.domain.model.exam.question;

/**
 * @author Byambajav
 */
public class LearnerAnswerEntity
{
  private final String value;
  private int index;
  private boolean selected;

  public LearnerAnswerEntity(String value, int index, boolean selected)
  {
    this.value = value;
    this.index = index;
    this.selected = selected;
  }

  public boolean isSelected()
  {
    return selected;
  }

  public void setSelected(boolean selected)
  {
    this.selected = selected;
  }

  public String getValue()
  {
    return value;
  }

  public int getIndex()
  {
    return index;
  }

  public void setIndex(int index)
  {
    this.index = index;
  }
}
