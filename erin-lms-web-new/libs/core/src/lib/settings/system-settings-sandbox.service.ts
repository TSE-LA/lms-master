import { Injectable } from '@angular/core';
import {SnackbarService} from "../../../../shared/src/lib/snackbar/snackbar.service";
import {Location} from "@angular/common";
import {SystemSettingsService} from "./services/system-settings.service";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class SystemSettingsSandboxService {

  constructor(
    public snackbarService: SnackbarService,
    private location: Location,
    private service: SystemSettingsService) { }

  uploadImage(file: File, isLogo: boolean): Observable<any> {
    return this.service.uploadImage(file, isLogo);
  }


  goBack(): void {
    return this.location.back();
  }
}
