/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.aim.rest.shiro.config;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import mn.erin.domain.aim.constant.AimConstants;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.role.Role;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.aim.repository.UserIdentityRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;

/**
 * @author EBazarragchaa
 */
public class AimRealm extends AuthorizingRealm
{
  private final RoleRepository roleRepository;
  private final MembershipRepository membershipRepository;
  private final UserIdentityRepository userIdentityRepository;
  private final PasswordService passwordService;

  public AimRealm(MembershipRepository membershipRepository, RoleRepository roleRepository,
      UserIdentityRepository userIdentityRepository, PasswordService passwordService)
  {
    this.membershipRepository = Objects.requireNonNull(membershipRepository);
    this.roleRepository = Objects.requireNonNull(roleRepository);
    this.userIdentityRepository = Objects.requireNonNull(userIdentityRepository);
    this.passwordService = Objects.requireNonNull(passwordService);
  }

  @Override
  public String getName()
  {
    return "AimRealm";
  }

  @Override
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals)
  {
    String username = (String) getAvailablePrincipal(principals);

    Subject currentUser = SecurityUtils.getSubject();
    String tenantId = (String) currentUser.getSession().getAttribute(AimConstants.SESSION_ATTR_TENANT_ID);

    SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

    try
    {
      if (AimConstants.ADMIN_USERNAME.equals(username))
      {
        authorizationInfo.addStringPermission("*");
      }
      else
      {
        List<Membership> memberships = membershipRepository.listAllByUsername(TenantId.valueOf(tenantId), username);
        Set<String> stringPermissions = new TreeSet<>();

        for (Membership membership : memberships)
        {
          Role role = roleRepository.findById(membership.getRoleId());
          authorizationInfo.addRole(role.getRoleId().getId());
          stringPermissions.addAll(getShiroRolePermissions(role));
        }

        authorizationInfo.addStringPermissions(stringPermissions);
      }

      return authorizationInfo;
    }
    catch (AimRepositoryException e)
    {
      throw new AuthorizationException(e.getMessage(), e);
    }
  }

  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
  {
    AimAuthenticationToken uToken = (AimAuthenticationToken) token;

    if (StringUtils.isBlank(uToken.getTenantId()) || StringUtils.isBlank(uToken.getUsername())
        || StringUtils.isBlank(new String(uToken.getPassword())))
    {
      throw new UnknownAccountException("User authentication data [userId, tenantId, password] is invalid!");
    }

    try
    {
      UserIdentity userIdentity = userIdentityRepository.getUserIdentityByUsername(uToken.getUsername());
      if (userIdentity == null)
      {
        throw new AimRepositoryException("username with [" + uToken.getUsername() + "] not found!");
      }
      String encryptedPassword = userIdentity.getPassword();

      if (passwordService.passwordsMatch(uToken.getPassword(), encryptedPassword))
      {
        return new SimpleAuthenticationInfo(uToken.getUsername(), uToken.getPassword(), getName());
      }
      else
      {
        throw new UnknownAccountException("Wrong credentials!");
      }
    }
    catch (AimRepositoryException e)
    {
      throw new UnknownAccountException("Wrong credentials!", e);
    }
  }

  private Collection<String> getShiroRolePermissions(Role role)
  {
    Set<String> shiroPermissions = new TreeSet<>();
    for (Permission aimPermission : role.getPermissions())
    {
      String shiroPermission = ShiroUtils.toShiroPermission(aimPermission);
      shiroPermissions.add(shiroPermission);
    }

    return shiroPermissions;
  }
}
