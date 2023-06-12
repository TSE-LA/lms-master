export interface Properties {
  disable: boolean;
  visible: boolean;
}

export class AuthModel {
  userName: string;
  token: string;
  role: string;
  userGroups: string;
  permissions: PermissionItem[];
}

export interface PermissionItem {
  id: string;
  properties: Properties;
}

