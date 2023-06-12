import {Injectable, Type} from "@angular/core";
import {Observable} from "rxjs";
import {CertificateModel, GenerateCertificateModel, ReceivedCertificateModel} from "./model/certificate.model";
import {CertificateService} from "./service/certificate.service";
import {BreakPointObserverService} from "../../../../shared/src/lib/theme/services/break-point-observer.service";
import {DialogConfig} from "../../../../shared/src/lib/dialog/dialog-config";
import {DialogRef} from "../../../../shared/src/lib/dialog/dialog-ref";
import {DialogService} from "../../../../shared/src/lib/dialog/dialog.service";
import {SnackbarService} from "../../../../shared/src/lib/snackbar/snackbar.service";
import {Store} from "@ngrx/store";
import {ApplicationState} from "../common/statemanagement/state/ApplicationState";

@Injectable({
  providedIn: 'root'
})
export class CertificateSandbox {
  username: string;
  username$ = this.store.select(state => state.auth.userName);

  constructor(private service: CertificateService,
    private store: Store<ApplicationState>,
    private breakPointService: BreakPointObserverService,
    private dialogService: DialogService,
    private snackbarService: SnackbarService,
  ) {
    this.username$.subscribe(res => this.username = res);
  }

  getCertificates(): Observable<CertificateModel[]> {
    return this.service.getCertificates();
  }

  getReceivedCertificates(learnerId: string): Observable<ReceivedCertificateModel[]> {
    return this.service.getReceivedCertificates(learnerId);
  }

  openDialog(component: Type<any>, config: DialogConfig): DialogRef {
    return this.dialogService.open(component, config)
  }

  getMediaBreakPointChange(): Observable<any> {
    return this.breakPointService.getMediaBreakPointChange();
  }

  openSnackbar(text: string, success?: boolean): void {
    this.snackbarService.open(text, success)
  }

  uploadCertificate(file: any): Observable<any> {
    return this.service.uploadCertificate(file);
  }

  deleteCertificate(certificateId: string) {
    return this.service.deleteCertificate(certificateId)
  }

  public generateCertificate(certificate: CertificateModel, recipient: string, courseName: string, target: string, fileName: string, courseId?: string): Observable<GenerateCertificateModel> {
    return this.service.generateCertificate(certificate, recipient, courseName, target, fileName, courseId);
  }
}
