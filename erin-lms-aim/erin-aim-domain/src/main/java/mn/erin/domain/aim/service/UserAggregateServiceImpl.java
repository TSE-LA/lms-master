package mn.erin.domain.aim.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserAggregate;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.model.user.UserIdentitySource;
import mn.erin.domain.aim.model.user.UserProfile;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;

public class UserAggregateServiceImpl implements UserAggregateService
{
  private final AimRepositoryRegistry aimRepositoryRegistry;
  private final TenantIdProvider tenantIdProvider;
  private final AimApplicationDataChecker dataChecker;

  public UserAggregateServiceImpl(AimRepositoryRegistry aimRepositoryRegistry, TenantIdProvider tenantIdProvider, AimApplicationDataChecker dataChecker)
  {
    this.aimRepositoryRegistry = aimRepositoryRegistry;
    this.tenantIdProvider = tenantIdProvider;
    this.dataChecker = dataChecker;
  }

  @Override
  public Collection<UserAggregate> getAllUserAggregates(boolean checkData)
  {
    List<UserAggregate> userAggregates = new ArrayList<>();
    List<User> users = aimRepositoryRegistry.getUserRepository().getAllUsers(TenantId.valueOf(tenantIdProvider.getCurrentUserTenantId()));
    Map<UserId, UserProfile> profiles = aimRepositoryRegistry.getUserProfileRepository().asMap();
    Map<UserId, UserIdentity> identities = aimRepositoryRegistry.getUserIdentityRepository().asMap(UserIdentitySource.OWN);

    Map<String, UserDataResult> allUsersCourseData = new HashMap<>();

    if (checkData)
    {
      allUsersCourseData = dataChecker.getUserCourseData();
    }

    for (User user : users)
    {
      UserAggregate aggregate = new UserAggregate(user, identities.get(user.getUserId()), profiles.get(user.getUserId()));
      UserDataResult userDataResult = allUsersCourseData.get(user.getUserId().getId());

      if (userDataResult != null)
      {
        aggregate.setDataResult(userDataResult);
      }

      userAggregates.add(aggregate);
    }

    return userAggregates;
  }

  @Override
  public UserAggregate getUserAggregate(String userId)
  {
    User user = aimRepositoryRegistry.getUserRepository().findById(UserId.valueOf(userId));
    UserIdentity identity = aimRepositoryRegistry.getUserIdentityRepository().getIdentity(user.getUserId(), UserIdentitySource.OWN);
    UserProfile profile = aimRepositoryRegistry.getUserProfileRepository().findByUserId(user.getUserId());
    return new UserAggregate(user, identity, profile);
  }
}
