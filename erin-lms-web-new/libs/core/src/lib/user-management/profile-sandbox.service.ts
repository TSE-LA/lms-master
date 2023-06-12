import {Injectable, Type} from '@angular/core';
import {ProfileService} from "./service/profile.service";
import {Observable} from "rxjs";
import {UserProfile} from "./models/profile.model";
import {SnackbarService} from "../../../../shared/src/lib/snackbar/snackbar.service";
import {BreakPointObserverService} from "../../../../shared/src/lib/theme/services/break-point-observer.service";
import {Location} from "@angular/common";
import {Router} from "@angular/router";
import {HistoryService} from "./service/history.service";
import {DialogConfig} from "../../../../shared/src/lib/dialog/dialog-config";
import {DialogRef} from "../../../../shared/src/lib/dialog/dialog-ref";
import {DialogService} from "../../../../shared/src/lib/dialog/dialog.service";
import { ApplicationState } from '../common/statemanagement/state/ApplicationState';
import { select, Store } from '@ngrx/store';

@Injectable({
  providedIn: 'root'
})
export class ProfileSandboxService {
  role: string;
  authRole$ = this.store.pipe(select(state => {
    return state.auth.role;
  }));
  constructor(
    private profileService: ProfileService,
    private historyService: HistoryService,
    private dialogService: DialogService,
    private snackbar: SnackbarService,
    private location: Location,
    private store: Store<ApplicationState>,
    private breakPointService: BreakPointObserverService,
    private router: Router
  ) {
    this.authRole$.subscribe(res => this.role = res);
  }

  async navigateByUrl(url: string): Promise<void> {
    await this.router.navigateByUrl(url);
  }

  getFields(organizationId: string): Observable<any> {
    return this.profileService.getFields(organizationId);
  }

  getUserProfile(userName: string): Observable<any> {
    return this.profileService.getUserProfile(userName);
  }

  getHistory(name: string, year: string): Observable<any> {
    return this.historyService.getHistory(name, year);
  }
  getHistoryCredits(name: string, year: string): Observable<any> {
    return this.historyService.getHistoryCredits(name, year);
  }

  updateProfile(profile: UserProfile): Observable<any> {
    return this.profileService.updateUserProfile(profile);
  }

  openSnackbar(text: string, success = true): void {
    this.snackbar.open(text, success);
  }

  openDialog(component: Type<any>, config: DialogConfig): DialogRef {
    return this.dialogService.open(component, config);
  }

  getMediaBreakPointChange(): Observable<any> {
    return this.breakPointService.getMediaBreakPointChange();
  }

  goBack(): void {
    this.location.back();
  }

  navigate(url: string, queryParams): void {
    this.router.navigate([url], {queryParams});
  }


  uploadAvatar(file: File): Observable<any> {
    return this.profileService.uploadAvatar(file);
  }

  getAvatarUrl(userName:string):Observable<string> {
    return this.profileService.getAvatarUrl(userName);
  }
}
