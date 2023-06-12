package mn.erin.domain.aim.service;

import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;

public interface AimConfigProvider
{
  TenantId getDefaultTenantId();

  RoleId getAdminRoleId();

  String getRootGroupName();

  String getDefaultPassword();
}
