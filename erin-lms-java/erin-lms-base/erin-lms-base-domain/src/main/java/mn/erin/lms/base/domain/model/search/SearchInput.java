package mn.erin.lms.base.domain.model.search;

import java.util.List;

import org.apache.commons.lang3.Validate;

/**
 * @author Galsan Bayart.
 */
public class SearchInput
{
  private String searchKey;
  private List<String> selectedUsers;

  public SearchInput(String searchKey, List<String> selectedUsers)
  {
    this.searchKey = Validate.notNull(searchKey);
    this.selectedUsers = Validate.notNull(selectedUsers);
  }

  public String getSearchKey()
  {
    return searchKey;
  }

  public void setSearchKey(String searchKey)
  {
    this.searchKey = searchKey;
  }

  public List<String> getSelectedUsers()
  {
    return selectedUsers;
  }

  public void setSelectedUsers(List<String> selectedUsers)
  {
    this.selectedUsers = selectedUsers;
  }
}
