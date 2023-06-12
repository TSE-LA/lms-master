package mn.erin.domain.aim.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.base.repository.Repository;

/**
 * @author Zorig
 */
public interface GroupRepository extends Repository<Group>
{
  /**
   * Creates new group on a repository.
   *
   * @param name     the group name
   * @param description   the group number
   * @param tenantId the tenant id
   * @return the generated id or null
   * @throws AimRepositoryException when parent group does not exist
   */
  Group createGroup(String name, String parentId, String description, TenantId tenantId) throws AimRepositoryException;

  /**
   * Returns a group to a given group number and a tenant. Each tenant shall have only one group with the number
   *
   * @param number   the group number
   * @param tenantId the tenant id
   * @return the group or null
   * @throws AimRepositoryException if more than one groups to the number and tenant found
   */
  Group findByNumberAndTenantId(String number, TenantId tenantId) throws AimRepositoryException;

  Group findByName(String name) throws AimRepositoryException;

  boolean doesGroupExist(GroupId groupId);

  boolean doesGroupExistByName(String name) throws AimRepositoryException;

  int getNthSibling(String parentId) throws AimRepositoryException;

  List<Group> getAllRootGroups(TenantId tenantId) throws AimRepositoryException;

  List<Group> getAllGroups(Set<String> groupIds) throws AimRepositoryException;

  Map<String, String> getAllGroupNamesAndId();

  boolean deleteGroup(String number) throws AimRepositoryException;

  Set<GroupId> getAllSubGroups(String number);

  Group moveGroupParent(String groupId, String parentId);

  Group moveGroupSibling(String number, int siblingNumber);

  Group renameGroup(String number, String newName);

  @Deprecated
  Set<String> getParentGroupIds(String groupId);

  Set<String> getSubGroups(String groupId);

  Set<Group> getAllByIds(Set<String> groupIds);
}
