package mn.erin.lms.base.aim;

import java.util.List;
import java.util.Map;
import java.util.Set;

import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.user.UserAggregate;
import mn.erin.domain.aim.model.user.UserInfo;
import mn.erin.domain.base.model.person.ContactInfo;

/**
 * AIM username is user ID for LMS.
 *
 * @author Bat-Erdene Tsogoo.
 */
public interface AccessIdentityManagement extends UserProvider
{
  String getCurrentUserId();

  String getRole(String username);

  String getCurrentUserDepartmentId();

  String getUserDepartmentId(String username);

  String getDepartmentId(String departmentName);

  Set<String> getSubDepartments(String departmentId);

  Group getDepartment(String departmentId);

  List<Group> getDepartments(Set<String> departments);

  /**
   * get parent departments
   * @deprecated
   * This method is no longer deemed valid.
   * Use getAncestorDepartments instead.
   *
   * @param departmentId
   * @return parents as string list
   */
  @Deprecated
  Set<String> getParentDepartments(String departmentId);

  String getDepartmentName(String departmentId);

  Map<String, String> getDepartmentNamesAndId();

  Set<String> getInstructors(String departmentId);

  Set<String> getLearners(String departmentId);

  Set<String> getAllLearners(String departmentId);

  Set<String> getLearnersByRole(String departmentId, String role);

  /**
   * @param usernames Learners username
   * @return Returns the user aggregates
   */
  List<UserAggregate> getUserAggregatesByUsername(Set<String> usernames);

  Set<String> getParentGroupLearnersByRole(String departmentId, String role);

  ContactInfo getContactInfo(String username);

  UserInfo getUserInfo(String username);

  boolean isArchived(String username);

  Set<String> getAncestorDepartments(String departmentId);
}
