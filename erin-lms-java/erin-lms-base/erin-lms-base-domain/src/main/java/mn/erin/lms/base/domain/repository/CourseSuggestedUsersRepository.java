package mn.erin.lms.base.domain.repository;

import java.util.Map;
import java.util.Set;

/**
 * @author Erdenetulga
 */
public interface CourseSuggestedUsersRepository
{
  boolean saveUsers(String courseId, Set<String> suggestedUsers, Set<String> removedUsers);

  boolean updateUsers(String courseId, Set<String> suggestedUsers);

  boolean saveGroups(String courseId, Set<String> suggestedGroups);

  boolean updateGroups(String courseId, Set<String> suggestedGroups);

  /**
   * Deletes suggessted users of course
   *
   * @param courseId The ID of the course
   * @return true if deleted, otherwise false
   * */
  boolean deleteSuggestedUser(String courseId);

  Set<String> getSuggestedUsers(String courseId);

  Map<String, Set<String>> fetchAll(String courseId);
}
