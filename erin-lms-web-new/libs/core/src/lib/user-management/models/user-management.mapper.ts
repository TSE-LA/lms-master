import {LearnerInfo} from "../../../../../shared/src/lib/shared-model";
import {RoleValueUtil} from "../../../../../shared/src/lib/utilities/role-value.util";
import {User} from "../../group-management/model/group.model";
import {DetailedUserInfo} from "../../common/common.model";
import {UserNameUtil} from "../../../../../shared/src/lib/utilities/user-name.util";
import {HttpParams} from "@angular/common/http";
import { UserDetailedModel, UserStatus} from "./user-management.model";

export class UserManagementMapper {
  public static mapToLearnerInfo(res: any): LearnerInfo[] {
    const users: LearnerInfo[] = [];
    const usersWithMemberships = res.entity.filter((user: any) => user.membership);
    for (const user of usersWithMemberships) {
      const userInfo = {
        firstname: user.firstName,
        lastname: user.lastName,
        name: user.username,
        role: RoleValueUtil.getRoleDisplayName(user.membership.roleId),
        selected: false,
        groupPath: user.groupPath,
        groupId: user.membership.groupId
      };
      users.push(userInfo);
    }
    return users;
  }

  public static mapToUserMap(res: any): Map<string, User> {
    const users = new Map<string, User>();
    for (const user of res.entity) {
      const userInfo: User = {
        name: user.username,
        firstname: user.firstName ? user.firstName : "-",
        lastname: user.lastName ? user.lastName : "-",
        role: user.membership ? RoleValueUtil.getRoleDisplayName(user.membership.roleId) : "-",
        groupId: user.membership ? user.membership.groupId : null,
        membershipId: user.membership ? user.membership.id : null,
        notInGroup: true,
        selected: false
      };
      users.set(user.username, userInfo);
    }
    return users;
  }


  public static mapToDetailedUserInfo(res): DetailedUserInfo[] {
    const users: DetailedUserInfo[] = [];
    for (const user of res.entity) {
      const userInfo = this.getDetailedUserInfo(user);

      users.push(userInfo);
    }
    return users;
  }

  public static getDetailedUserInfo(user: any): DetailedUserInfo {
    const userInfo: DetailedUserInfo = {
      userId: user.id,
      displayName: UserNameUtil.getDisplayName(user),
      username: user.username,
      lastName: user.lastName,
      firstName: user.firstName,
      email: user.email,
      phoneNumber: user.phoneNumber,
      membership: user.membership
    };
    if (user.membership !== null) {
      userInfo.membership = {
        userId: user.id,
        groupId: user.membership.groupId,
        roleId: user.membership.roleId,
        membershipId: user.membership.membershipId
      }
    }
    return userInfo;
  }

  public static getIncludeMeParam(includeMe: boolean) {
    const params = new HttpParams();
    return params.append('includeMe', includeMe ? "true" : "false");
  }

  public static mapDetailedUserInfoAsMap(res: any): Map<string, DetailedUserInfo> {
    const users = new Map<string, DetailedUserInfo>();
    for (const user of res.entity) {
      const userInfo = UserManagementMapper.getDetailedUserInfo(user);
      users.set(user.username, userInfo);
    }
    return users;
  }

  public static mapToUserModel(res: any): UserDetailedModel {
    return {
      id: res.userId,
      username: res.username,
      firstName: res.firstName,
      lastName: res.lastName,
      email: res.email,
      phoneNumber: res.phoneNumber,
      birthday: res.birthday,
      gender: res.gender,
      password: res.password,
      confirmPassword: res.confirmPassword,
      status: UserStatus[res.status as keyof typeof UserStatus],
      dateLastModified: res.dateLastModified,
      properties: res.properties,
      deletable: res.deletable !== null ? res.deletable : true,
    };
  }


  public static mapToUserModels(res: any[]): UserDetailedModel[] {
    const users = [];
    res.forEach(entity => users.push(this.mapToUserModel(entity)));
    return users;
  }

  public static mapToDuplicatedUsersModel(res: any): string[] {
    const result: string[] = [];
    for (const user of res) {
      result.push(" " + user);
    }
    return result;
  }
}
