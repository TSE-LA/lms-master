import {Inject, Injectable} from "@angular/core";
import {AuthenticationService} from "./services/authentication/authentication.service";
import {Observable} from "rxjs";
import {map} from "rxjs/operators";
import {TranslateService} from "@ngx-translate/core";
import {SetAuth} from "../common/statemanagement/actions/auth/auth";
import {ApplicationState} from "../common/statemanagement/state/ApplicationState";
import {Store} from "@ngrx/store";
import {SystemSettingsService} from "../settings/services/system-settings.service";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationSandbox {
  constructor(
    private service: AuthenticationService,
    private translateService: TranslateService,
    private store: Store<ApplicationState>,
    private systemSettingsService: SystemSettingsService,
    @Inject('version') private appVersion) {
  }

  login(username: string, password: string): Observable<any> {
    return this.service.login(username, password).pipe(map(res => {
      const action = new SetAuth(res);
      this.store.dispatch(action);
      return res;
    }));
  }

  logout(): void {
    if (this.service.isLoggedIn()) {
      this.service.logout().subscribe();
    }
  }

  getTranslateService(): TranslateService {
    return this.translateService;
  }

  getAppVersion(): string {
    return this.appVersion;
  }

  getBackgroundImageUrl(): Observable<string> {
    return this.systemSettingsService.getSystemImageUrl(false);
  }

  getLogoImageUrl(): Observable<string> {
    return this.systemSettingsService.getSystemImageUrl(true);
  }
}
