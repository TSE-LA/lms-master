package mn.erin.domain.aim.repository;

import mn.erin.domain.aim.service.UserAggregateService;

/**
 * @author Munkh
 */
public interface AimRepositoryRegistry
{
  UserRepository getUserRepository();

  UserIdentityRepository getUserIdentityRepository();

  UserProfileRepository getUserProfileRepository();

  GroupRepository getGroupRepository();

  MembershipRepository getMembershipRepository();

  RoleRepository getRoleRepository();

  AimFileSystem getAimFileSystem();

  UserAggregateService getUserAggregateService();
}
