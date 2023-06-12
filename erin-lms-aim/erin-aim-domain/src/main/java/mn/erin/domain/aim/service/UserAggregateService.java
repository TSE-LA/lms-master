package mn.erin.domain.aim.service;

import java.util.Collection;

import mn.erin.domain.aim.model.user.UserAggregate;

public interface UserAggregateService
{
  Collection<UserAggregate> getAllUserAggregates(boolean checkData) throws Exception;

  UserAggregate getUserAggregate(String userId);
}