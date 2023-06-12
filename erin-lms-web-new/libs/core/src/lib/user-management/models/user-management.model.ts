export interface UserDetailedModel extends UserModel {
  firstName: string;
  lastName: string;
  gender: Gender;
  password: string;
  confirmPassword: string;
  status: UserStatus;
  birthday: string;
  dateLastModified: any;
  message?: string;
  deletable?: boolean;
  properties?: Record<string, string>
}

export interface UserModel {
  id?: string;
  username: string;
  email: string;
  phoneNumber: string;
}

export interface UserCreateModel extends UserModel {
  firstName: string;
  lastName: string;
  gender: string;
  birthday: string;
  password: string;
  properties?: Record<string, string>;
}

export enum Gender {
  MALE, FEMALE, NA
}

export enum UserStatus {
  ACTIVE, ARCHIVED
}

export interface ArchiveUserModel {
  userIds: string[];
  archived: boolean;
}

