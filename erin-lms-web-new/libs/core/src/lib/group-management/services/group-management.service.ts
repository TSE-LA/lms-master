import {Inject, Injectable} from '@angular/core';
import {forkJoin, Observable} from "rxjs";
import {HttpClient, HttpParams} from "@angular/common/http";
import {catchError, map} from "rxjs/operators";
import {GroupNode} from "../../../../../shared/src/lib/shared-model";
import {DetailedUserInfo, Membership, Role, UserRoleProperties, UsersByGroup} from "../../common/common.model";
import {User, UserCourses} from "../model/group.model";
import {UserNameUtil} from "../../../../../shared/src/lib/utilities/user-name.util";
import {GroupUtil} from "../model/group-util";
import {UserManagementService} from "../../user-management/service/user-management.service";

@Injectable({
  providedIn: 'root'
})
export class GroupManagementService {

  flatData: GroupNode[] = [];

  constructor(private um: UserManagementService, private httpClient: HttpClient, @Inject('tenantId') private tenantId) {
  }

  getAllUsersGroups(groupId: string, getFlatData: boolean): Observable<GroupNode[]> {
    return this.httpClient.get('/aim/group/' + groupId).pipe(map((res: any) => {
      const nodes: GroupNode[] = [];
      if (res.entity.length === undefined) {
        GroupUtil.pushNode(nodes, res.entity);
      } else {
        for (const node of res.entity) {
          GroupUtil.pushNode(nodes, node);
        }
      }
      if (getFlatData) {
        this.flatData = [];
        GroupUtil.flatten(nodes, this.flatData);
        return this.flatData;
      } else {
        return nodes;
      }
    }));
  }

  getAllUsersWithMemberships(groupId: string): Observable<Membership[]> {
    return this.httpClient.get('/aim/group/' + groupId + '/memberships').pipe(map((res: any) => {
      const usersWithMembership: Membership[] = [];

      for (const userWithMembership of res.entity) {
        usersWithMembership.push({
          userId: userWithMembership.userId,
          roleId: userWithMembership.roleId,
          groupId: userWithMembership.groupId,
          membershipId: userWithMembership.membershipId,
        });
      }

      return usersWithMembership;
    }));
  }

  getGroupMembers(id: string): Observable<Membership[]> {
    return this.httpClient.get('/aim/group/' + id + '/memberships').pipe(map((res: any) => {
      const usersWithMembership: Membership[] = [];
      for (const userWithMembership of res.entity) {
        usersWithMembership.push({
          userId: userWithMembership.userId,
          roleId: userWithMembership.roleId,
          groupId: userWithMembership.groupId,
          membershipId: userWithMembership.membershipId,
        });
      }
      return usersWithMembership;
    }));
  }

  deleteMembership(membershipId: string): Observable<any> {
    return this.httpClient.delete('/aim/memberships/' + membershipId);
  }

  createMembers(groupId: string, roleId: string, users: User[]): Observable<any> {
    const usernames: string[] = [];
    users.every((value: User) => usernames.push(value.name));
    const body = {
      groupId,
      roleId,
      users: usernames
    };
    return this.httpClient.post('/aim/memberships', body);
  }

  getAllRoles(): Observable<Role[]> {
    let params = new HttpParams();
    params = params.append('tenantId', this.tenantId);
    return this.httpClient.get('/aim/roles', {params}).pipe(map((res: any) => {
      const roles: Role[] = [];

      for (const role of res.entity) {
        roles.push({
          id: role.roleId,
          name: role.roleName
        });
      }
      return roles;
    }));
  }

  createPromotionReadership(groupId: string, userId: string, newGroupId?: string): Observable<any> {
    return this.httpClient.post('/legacy/lms/readerships', {groupId, learnerId: userId, newGroupId: newGroupId});
  }

  deleteGroup(id): Observable<any> {
    return this.httpClient.delete('/aim/group/' + id);
  }

  updateGroupName(id: string, newName: string): Observable<any> {
    return this.httpClient.patch('/aim/group/' + id + '/rename', {name: newName});
  }

  addGroup(parent: string, groupName: string): Observable<any> {
    return this.httpClient.post('/aim/group', {
      tenantId: this.tenantId,
      parentId: parent,
      name: groupName
    });
  }

  getMyUsersByGroupMap(courseId: string, groupId: string): Observable<UsersByGroup> {
    const userMap = this.um.getAllDetailedUserMap();
    const groups = this.getAllUsersGroups(groupId, false);

    return forkJoin([userMap, groups]).pipe(map((res) => {
      const allUsers = res[0];
      const root: GroupNode = res[1][0];
      const allMyUsers = new Map<string, DetailedUserInfo>();
      GroupUtil.setPath(root.children, root.name);
      allUsers.forEach((user: DetailedUserInfo, key: string) => {
        if (user.membership != null && user.membership.groupId != null && user.membership.roleId != UserRoleProperties.adminRole.id) {
          const node: GroupNode = GroupUtil.getNode(root, user.membership.groupId);
          if (node) {
            user.path = node.path;
            user.displayName = UserNameUtil.getCombinedName(user.displayName, user.username);
            allMyUsers.set(key, user);
          }
        }
      });
      return {allUsers, allMyUsers, groups: root}
    }), catchError(error => {
      throw error;
    }))
  }

  getLearnersReaderships(groupId: string, selectedUsers: User[]): Observable<UserCourses[]> {
    let params = new HttpParams();
    params = params.append('groupId', groupId);
    const requests = [];
    const usersCourses: UserCourses[] = [];
    for (const user of selectedUsers) {
      const reqParam = params.append('learnerId', user.name);
      requests.push(this.httpClient.get('/legacy/lms/readerships', {params: reqParam}).pipe(map((res: any) => {
        const courses = [];
        for (const course of res.entity){
          courses.push(course.courseDetail.title);
        }
        usersCourses.push({username: user.name, courses: courses});
      })));
    }
    return forkJoin(requests).pipe(map(() => {
      return usersCourses;
    }));
  }
}
