export interface UserProfile {
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  gender?: string;
  birthday: string;
  newPassword: string;
  repeatPassword?: string;
  properties?: Record<string, string>;
}

export interface CourseInfo {
  name: string;
  type: string;
  credit?: string;
}

export interface Field {
  id: string;
  name: string;
  required: boolean;
  type: 'INPUT' | 'SELECT' | 'DATE';
}
