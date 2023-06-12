package mn.erin.lms.base.rest.model.exam;

/**
 * @author Byambajav
 */
public class RestLearnerAnswer
{
  private String value;
  private int index;
  private boolean selected;

  public RestLearnerAnswer()
  {
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
