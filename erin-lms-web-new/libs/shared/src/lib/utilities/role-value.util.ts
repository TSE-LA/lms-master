import {UserRoleProperties} from "../../../../core/src/lib/common/common.model";

export class RoleValueUtil {
  public static getRoleDisplayName(roleId: string): string {
    switch (roleId) {
      case UserRoleProperties.adminRole.id:
        return UserRoleProperties.adminRole.name;

      case UserRoleProperties.managerRole.id:
        return UserRoleProperties.managerRole.name;

      case UserRoleProperties.supervisorRole.id:
        return UserRoleProperties.supervisorRole.name;

      default:
        return UserRoleProperties.employeeRole.name;
    }
  }

  static amISuperUser(roleId: string): boolean {
    switch (roleId) {
      case UserRoleProperties.managerRole.id:
        return true;

      case UserRoleProperties.supervisorRole.id:
        return true;
      default:
        return false;
    }
  }
}
