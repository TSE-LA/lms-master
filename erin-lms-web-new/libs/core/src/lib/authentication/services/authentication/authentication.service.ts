import {Inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {Observable, throwError} from "rxjs";
import {catchError, map} from "rxjs/operators";
import {AuthModel} from "../../authentication-model";
import {ScormRuntimeService} from "../../../scorm/service/scorm-runtime.service";
import {UserRoleProperties} from "../../../common/common.model";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private loginStatus = false;
  private userRole = UserRoleProperties.employeeRole.id;
  public redirectUrl: string;

  constructor(
    private scormRuntime: ScormRuntimeService,
    private http: HttpClient,
    private router: Router,
    @Inject('tenantId') private tenantId) {
  }

  public navigateToRedirectUrl() {
    this.router.navigateByUrl(this.redirectUrl);
    this.redirectUrl = null;
  }

  public navigateToUserDefaultPage() {

    this.router.navigateByUrl('');
    // switch (this.userRole) {
    //   case UserRoleProperties.supervisorRole.role:
    //     this.router.navigateByUrl(UserRoleProperties.supervisorRole.roleUrl);
    //     break;
    //   case UserRoleProperties.managerRole.role:
    //     this.router.navigateByUrl(UserRoleProperties.managerRole.roleUrl);
    //     break;
    //   case UserRoleProperties.adminRole.role:
    //     this.router.navigateByUrl(UserRoleProperties.adminRole.roleUrl);
    //     break;
    //   default:
    //     this.router.navigateByUrl(UserRoleProperties.employeeRole.roleUrl);
    //     break;
    // }
  }

  login(username: string, password: string): Observable<any> {
    const body = {username: username, password, tenantId: this.tenantId};
    return this.http.post('/aim/login', body).pipe(
      map((res) => {

        this.loginStatus = true;
        const mappedModel = this.mapToAuthModel(res);

        if (this.redirectUrl && this.redirectUrl !== '/') {
          this.navigateToRedirectUrl();
        } else {
          this.navigateToUserDefaultPage();
        }

        return mappedModel;
      }),
      catchError(error => {
        this.loginStatus = false;
        if (error.status < 400 && error.status >= 500) {
        }
        return throwError(error.error.message);
      }));
  }

  validateSession(): Observable<AuthModel> {
    return this.http.get('/aim/validate-session').pipe(map((res: any) => {
      this.loginStatus = true;
      return this.mapToAuthModel(res);
    }), catchError(error => {
      this.loginStatus = false;
      return throwError(error);
    }));
  }

  removeSession(): Observable<any> {
    this.loginStatus = false;
    return this.http.get('/aim/logout');
  }

  isLoggedIn(): boolean {
    return this.loginStatus;
  }

  logout(): Observable<any> {
    const url = this.router.url;
    if (url.includes('/online-course/launch') || url.includes('/timed-course/launch')) {
      this.scormRuntime.onSaveDataCalled();
      this.loginStatus = false;
      return this.http.get('/aim/logout');
    } else {
      this.loginStatus = false;
      return this.http.get('/aim/logout');
    }
  }

  private mapToAuthModel(res: any): AuthModel {
    const response = res.entity;
    this.userRole = response.role;
    return {
      userName: response.username,
      userGroups: response.departmentId,
      role: response.role,
      token: response.token,
      permissions: response.permissions
    };
  }


}
