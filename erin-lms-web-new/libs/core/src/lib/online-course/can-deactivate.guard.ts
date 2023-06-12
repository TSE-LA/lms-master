import {Injectable} from "@angular/core";
import {ActivatedRouteSnapshot, CanDeactivate, Router, RouterStateSnapshot} from "@angular/router";
import {DialogConfig} from "../../../../shared/src/lib/dialog/dialog-config";
import {ConfirmDialogComponent} from "../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import {Subject} from "rxjs";
import {CheckComponent} from "./component/check-component";
import {DialogService} from "../../../../shared/src/lib/dialog/dialog.service";

@Injectable({
  providedIn: 'root'
})

export class CanDeactivateGuard implements CanDeactivate<CheckComponent> {
  dialogRef;
  result = new Subject<boolean>();

  constructor(private router: Router, private dialogService: DialogService) {
  }

  CONFIRM_DIALOG_TITLE = 'Анхааруулга';
  CONFIRM_DIALOG_MSG_ON_EXIT = 'ДУУСГАХ товч даралгүй гарахад сорилын оролдлогод тооцно.\nТа гарахдаа итгэлтэй байна уу?';

  canDeactivate(
    component: CheckComponent,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot): Promise<boolean> {
    const splitUrl = nextState.url.split(';')[1];
    if (!component.canDeactivate() && splitUrl !== 'navigate=true') {
      const config = new DialogConfig();
      config.outsideClick = true;
      config.title = this.CONFIRM_DIALOG_TITLE;
      config.data = {
        info: (this.CONFIRM_DIALOG_MSG_ON_EXIT)
      }
      if (!this.dialogRef) {
        this.dialogRef = this.dialogService.open(ConfirmDialogComponent, config);
        this.dialogRef.afterClosed.subscribe(result => {
          if (result) {
            this.router.navigate([nextState.url, {navigate: true}]);
          }
          this.dialogRef = undefined;
        });
      }
      return Promise.resolve(false);
    } else {
      return Promise.resolve(true)
    }
  }
}
