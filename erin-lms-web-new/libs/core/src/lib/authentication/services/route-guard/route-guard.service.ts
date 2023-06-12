import { Injectable } from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from "@angular/router";
import {AuthenticationService} from "../authentication/authentication.service";
import {SetAuth} from "../../../common/statemanagement/actions/auth/auth";
import {Store} from "@ngrx/store";
import {ApplicationState} from "../../../common/statemanagement/state/ApplicationState";

@Injectable({
  providedIn: 'root'
})
export class RouteGuardService implements CanActivate {
  path: ActivatedRouteSnapshot[];
  role: string;
  readonly route: ActivatedRouteSnapshot;
  // protected authRole = this.commonSB.authRole$;


  constructor(
    public router: Router,
    public auth: AuthenticationService,
    private store: Store<ApplicationState>,) {
    // this.authRole.subscribe(res => this.role = res);
  }

  canActivateChild(route: ActivatedRouteSnapshot, state: any): Promise<boolean> {
    return this.canActivate(route, state);
  }

  async canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {
    const isLoggedIn = this.auth.isLoggedIn();

    if (!isLoggedIn) {
      return await this.auth.validateSession().toPromise().then((res) => {
        const action = new SetAuth(res);
        this.store.dispatch(action);
        this.auth.redirectUrl = state.url;
        this.auth.navigateToRedirectUrl();
        return true;
      }).catch(() => {
        this.router.navigate(['/login']);
        return false;
      });
    }

    if (this.role && route.data.role && typeof route.data.role === 'object' && (route.data.role as string[]).includes(this.role)) {
      return true;
    }

    if (this.role && route.data.role && route.data.role !== this.role) {
      return false;
    }

    if (state.url === '/') {
      // this.navigateToUrl(state.url, this.role);
      return false;
    }

    return isLoggedIn;
  }

  // protected navigateToUrl(url: string, role: string): void {
  //   if (url === '/') {
  //     switch (role) {
  //       case 'LMS_SUPERVISOR':
  //         this.auth.redirectUrl = UserRoleProperties.supervisorRole.roleUrl;
  //         break;
  //       case 'LMS_ADMIN':
  //         this.auth.redirectUrl = UserRoleProperties.adminRole.roleUrl;
  //         break;
  //       case 'LMS_MANAGER':
  //         this.auth.redirectUrl = UserRoleProperties.managerRole.roleUrl;
  //         break;
  //       default:
  //         this.auth.redirectUrl = UserRoleProperties.employeeRole.roleUrl;
  //     }
  //   }
  //   this.auth.navigateToRedirectUrl();
  // }
}
