import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable, throwError} from "rxjs";
import {LearnerInfo} from "../../../../../shared/src/lib/shared-model";
import {catchError, map} from "rxjs/operators";
import {User} from "../../group-management/model/group.model";
import {DetailedUserInfo} from "../../common/common.model";
import {UserManagementMapper} from "../models/user-management.mapper";
import {ArchiveUserModel, UserCreateModel, UserDetailedModel} from "../models/user-management.model";
import {FileDownloadUtil} from "../../common/utilities/file-download-util";
import {Field} from "../models/profile.model";

@Injectable({
  providedIn: 'root'
})
export class UserManagementService {
  private readonly AIM_URL = '/aim/users';
  private readonly LMS_USER_URL = '/lms/users';

  constructor(private httpClient: HttpClient) {
  }

  getAllUsers(includeMe?: boolean): Observable<DetailedUserInfo[]> {
    const params = UserManagementMapper.getIncludeMeParam(includeMe);
    return this.httpClient.get(this.LMS_USER_URL, {params}).pipe(map((res: any) => UserManagementMapper.mapToDetailedUserInfo(res)));
  }

  getAllUsersWithRole(includeMe: boolean): Observable<LearnerInfo[]> {
    const params = UserManagementMapper.getIncludeMeParam(includeMe);
    return this.httpClient.get(this.LMS_USER_URL + '/all-with-role', {params}).pipe(map((res: any) => UserManagementMapper.mapToLearnerInfo(res)));
  }

  getAllUsersMap(includeMe?: boolean): Observable<Map<string, User>> {
    const params = UserManagementMapper.getIncludeMeParam(includeMe);
    return this.httpClient.get(this.LMS_USER_URL, {params}).pipe(map((res: any) => UserManagementMapper.mapToUserMap(res)));
  }

  getAllDetailedUserMap(includeMe?: boolean): Observable<Map<string, DetailedUserInfo>> {
    const params = UserManagementMapper.getIncludeMeParam(includeMe);
    return this.httpClient.get(this.LMS_USER_URL, {params}).pipe(map((res: any) => UserManagementMapper.mapDetailedUserInfoAsMap(res)));
  }

  getAllSupervisorEmployees(): Observable<LearnerInfo[]> {
    return this.httpClient.get(this.LMS_USER_URL + '/supersEmployees').pipe(map((res: any) => UserManagementMapper.mapToLearnerInfo(res)));
  }

  getRealmType(): Observable<any> {
    return this.httpClient.get(this.AIM_URL + '/realm-type')
      .pipe(map((res: any) => {
        return res.entity;
      }), catchError((error: any) => {
        return throwError(error);
      }));
  }

  createUser(userModel: UserCreateModel): Observable<UserDetailedModel> {
    return this.httpClient.post(this.AIM_URL, userModel)
      .pipe(map((res: any) => {
        return UserManagementMapper.mapToUserModel(res.entity);
      }), catchError((error: any) => {
        return throwError(error);
      }));
  }

  doesUserExist(username: string) {
    return this.httpClient.post(this.AIM_URL + `/check`, username)
      .pipe(map((res: { entity: boolean }) => {
        return res.entity;
      }), catchError((error: any) => {
        return throwError(error);
      }));
  }

  deleteUser(userId: string): Observable<boolean> {
    return this.httpClient.delete(`${this.AIM_URL}/${userId}`)
      .pipe(map((res: any) => {
        return res.entity;
      }), catchError((error: any) => {
        return throwError(error);
      }));
  }

  deleteUsers(userIds: string[]): Observable<boolean> {
    return this.httpClient.post(this.AIM_URL + '/delete', userIds)
      .pipe(map((res: any) => {
        return res.entity;
      }), catchError((error: any) => {
        return throwError(error);
      }));
  }

  archiveUser(userId: string, archived: boolean): Observable<boolean> {
    return this.httpClient.put(`${this.AIM_URL}/archive/${userId}`, {archived})
      .pipe(map((res: any) => {
        return res.entity;
      }), catchError((error: any) => {
        return throwError(error);
      }));
  }

  archiveUsers(users: ArchiveUserModel): Observable<boolean> {
    return this.httpClient.put(this.AIM_URL + '/archive', users)
      .pipe(map((res: any) => {
        return res.entity;
      }), catchError((error: any) => {
        return throwError(error);
      }));
  }

  getUsers(): Observable<UserDetailedModel[]> {
    return this.httpClient.get(this.AIM_URL)
      .pipe(map((res: any) => {
        res.entity.sort((a, b) => a.username.toLowerCase() > b.username.toLowerCase() ? 1 : a.username.toLowerCase() < b.username.toLowerCase() ? -1 : 0);
        return UserManagementMapper.mapToUserModels(res.entity);
      }), catchError((error: any) => {
        return throwError(error);
      }));
  }

  importUsers(file: any): Observable<any> {
    const headers = new HttpHeaders();
    headers.set('Accept', 'multipart/form-data');
    const formData = new FormData();
    formData.append('file', file);

    return this.httpClient.post(this.AIM_URL + '/import', formData, {headers}).pipe(map((res: any) => {
      return res.entity;
    }), catchError((error: any) => {
      return throwError(error);
    }));
  }

  exportUsers(): Observable<any> {
    return this.httpClient.get(this.LMS_USER_URL + "/export", {
      responseType: 'blob',
      observe: 'response'
    }).pipe(map(res => {
      return FileDownloadUtil.downloadFile(res);
    }), catchError(err => {
      return throwError(err);
    }));
  }

  updateUser(user: UserCreateModel): Observable<boolean> {
    return this.httpClient.put(this.AIM_URL + "/" + user.id, user)
      .pipe(map((res: any) => {
        return res.entity;
      }))
  }

  getFields(organizationId: string): Observable<Field[]> {
    return this.httpClient.get('/lms/field/get-fields?organizationId=' + organizationId)
      .pipe(map((res: any) => res.entity as Field[]));
  }
}
