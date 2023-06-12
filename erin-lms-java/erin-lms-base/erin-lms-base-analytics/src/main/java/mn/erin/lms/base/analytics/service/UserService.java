package mn.erin.lms.base.analytics.service;

import java.util.List;

import mn.erin.domain.aim.model.user.UserIdentity;

/**
 * @author Munkh
 */
public interface UserService
{
  void overwriteUsers();
  List<UserIdentity> getIdentities();
  boolean exists(String userId);
  void cleanup();
}
