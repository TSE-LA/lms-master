export class CourseTypeByRoleUtil{
  public static getRole(userRole: string) {
    switch (userRole) {
      case 'LMS_SUPERVISOR':
        return 'SUPERVISOR';
      case 'LMS_MANAGER':
        return 'MANAGER';
      default:
        return 'EMPLOYEE';
    }
  }
}
