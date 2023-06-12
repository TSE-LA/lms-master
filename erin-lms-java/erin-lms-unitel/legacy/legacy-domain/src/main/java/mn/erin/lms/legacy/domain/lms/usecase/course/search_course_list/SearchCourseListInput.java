package mn.erin.lms.legacy.domain.lms.usecase.course.search_course_list;

/**
 * author Naranbaatar Avir.
 */
public class SearchCourseListInput
{
  private String searchKey;
  private boolean name;
  private boolean description;

  public SearchCourseListInput(String searchKey)
  {
    this.searchKey = searchKey;
  }

  public void searchByName(boolean name)
  {
    this.name = name;
  }

  public void searchByDescription(boolean description)
  {
    this.description = description;
  }

  public boolean isSearchByName()
  {
    return name;
  }

  public boolean isSearchByDescription()
  {
    return description;
  }

  public String getSearchKey()
  {
    return searchKey;
  }

  public void setSearchKey(String searchKey)
  {
    this.searchKey = searchKey;
  }
}
