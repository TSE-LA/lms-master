import {Component, OnInit} from '@angular/core';
import {CertificateModel} from "../model/certificate.model";
import {CertificateSandbox} from "../certificate.sandbox";
import {DialogConfig} from "../../../../../shared/src/lib/dialog/dialog-config";
import {
  ALLOWED_TYPE,
  CREATE_CERTIFICATE_TEXT, DELETE_CERTIFICATE_FAILURE_MSG, DELETE_CERTIFICATE_SUCCESS_MSG,
  UPLOAD_CERTIFICATE_FAIL_MSG,
  UPLOAD_CERTIFICATE_SUCCESS_MSG, WRONG_TYPE, DEFAULT_THUMBNAIL_CERTIFICATE
} from "../model/certificate.constants";
import {FileUploadDialogComponent} from "../../../../../shared/src/lib/dialog/file-upload-dialog/file-upload-dialog.component";
import {ConfirmDialogComponent} from "../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import {FileViewDialogComponent} from "../../../../../shared/src/lib/dialog/file-view-dialog/file-view-dialog.component";


@Component({
  selector: 'jrs-certificate-container',
  template: `
    <div class="flex justify-end margin-bottom">
      <jrs-button
        [title]="'Сертификат үүсгэх'"
        [size]="'medium'"
        (clicked)="createCertificate()">
      </jrs-button>
    </div>
    <div *ngIf="!loading">
      <jrs-grid-container [column]="column" [row]="row">
        <div *ngFor="let certificate of splitCertificates">
          <jrs-certificate
            (deleteClick)="deleteCertificate($event)"
            (viewClick)="viewCertificate($event)"
            [name]="certificate.name"
            [author]="certificate.authorId"
            [isUsed]="certificate.used"
            [id]="certificate.id"
            [image]="DEFAULT_THUMBNAIL"
            [desc]="certificate.name"
          ></jrs-certificate>
        </div>
      </jrs-grid-container>
    </div>
    <div *ngIf="loading">
      <jrs-grid-container>
        <div *ngFor="let cer of [].constructor(row*column)">
          <jrs-certificate-skeleton-loader></jrs-certificate-skeleton-loader>
        </div>
      </jrs-grid-container>
    </div>
    <div class="flex justify-end margin-top">
      <jrs-paginator
        [contents]="certificates"
        [perPageNumber]="row*column"
        (pageClick)="splitCertificate($event)">
      </jrs-paginator>
    </div>
  `,
})
export class CertificateContainerComponent implements OnInit {

  certificates: CertificateModel[] = [];
  splitCertificates: CertificateModel[];
  username: string;
  loading: boolean;
  row = 2;
  column = 4;
  DEFAULT_THUMBNAIL = DEFAULT_THUMBNAIL_CERTIFICATE

  constructor(private sb: CertificateSandbox) {
    this.calculateGrid();
  }

  ngOnInit(): void {
    this.loadPage();
    this.calculateGrid();
  }

  getCertificates(): void {
    this.loading = true;
    this.sb.getCertificates().subscribe(res => {
      this.loading = false;
      this.certificates = res;
      this.calculateGrid();
    })
  }

  splitCertificate(certificates: CertificateModel[]): void {
    this.splitCertificates = certificates;
  }

  createCertificate(): void {
    const config = new DialogConfig();
    config.outsideClick = true;
    config.data = {
      fileTypes: ".docx",
      allowedRegexp: /(\.docx)$/i
    }
    config.title = CREATE_CERTIFICATE_TEXT;
    const dialogRef = this.sb.openDialog(FileUploadDialogComponent, config)
    dialogRef.afterClosed.subscribe(res => {
      if (res.status) {
        if (res.mimetype === ALLOWED_TYPE) {
          this.loading = true;
          this.sb.uploadCertificate(res.file).subscribe(() => {
            this.getCertificates();
            this.sb.openSnackbar(UPLOAD_CERTIFICATE_SUCCESS_MSG, true);
            this.loading = false;
          }, () => {
            this.loading = false;
            this.sb.openSnackbar(UPLOAD_CERTIFICATE_FAIL_MSG, false);
          })
        } else {
          this.sb.openSnackbar(WRONG_TYPE, false);
        }
      }
    })
  }

  deleteCertificate(certificateId: string): void {
    const config = new DialogConfig();
    config.outsideClick = true;
    config.data = {
      info: "Та энэ сертификатыг устгах гэж байна, устгахдаа итгэлтэй байна уу?"
    }
    config.title = "Сертификат устгах"
    const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config)
    dialogRef.afterClosed.subscribe(res => {
      if (res) {
        this.loading = true
        this.sb.deleteCertificate(certificateId).subscribe(res => {
          this.getCertificates();
          this.loading = false;
          this.sb.openSnackbar(DELETE_CERTIFICATE_SUCCESS_MSG, true)
        }, () => {
          this.loading = false
          this.sb.openSnackbar(DELETE_CERTIFICATE_FAILURE_MSG);
        })
      }
    })

  }


  viewCertificate(certificate: CertificateModel): void {
    const courseName = "Тест сертификат";
    const config = new DialogConfig();
    config.outsideClick = false;
    config.background = false;
    config.width = 'auto';
    config.blur =true;
    config.data = {
      courseName: courseName,
      source: `alfresco/Certificate-Test/${this.username}/${certificate.id}.PDF`,
    }
    this.sb.generateCertificate(certificate,
      certificate.authorId,
      courseName,
      `Certificate-Test/${this.username}`,
      certificate.id).subscribe(() => {
      this.sb.openDialog(FileViewDialogComponent, config);
    }, () => {
      this.sb.openDialog(FileViewDialogComponent, config);
    })
  }

  private loadPage(): void {
    this.sb.username$.subscribe(username => this.username = username);
    this.getCertificates();

  }

  calculateGrid(): void {
    this.sb.getMediaBreakPointChange().subscribe(res => {
      switch (res) {
        case 'media_xxl' :
          this.row = this.countRows(4);
          this.column = 4;
          break;
        case 'media_xl' :
          this.row = this.countRows(4);
          this.column = 4;
          break;
        case 'media_lg' :
        case 'media_ml' :
          this.row = this.countRows(3);
          this.column = 3;
          break;
        case  'media_md' :
        case  'media_sm' :
          this.row = this.countRows(2);
          this.column = 2;
          break;
        case  'media_s' :
        case  'media_xs' :
          this.row = this.countRows(1);
          this.column = 1;
          break;
        default:
          break;
      }
    })
  }

  private countRows(column: number): number {
    let row;
    switch (column) {
      case 4 :
        row = this.certificates.length < 5 ? 1 : 2;
        break;
      case 3 :
        row = this.certificates.length < 4 ? 1 : this.certificates.length < 7 ? 2 : 3;
        break;
      case 2 :
        row = this.certificates.length > 4 ? 3 : this.certificates.length < 3 ? 1 : 2;
        break;
      case 1 :
        row = this.certificates.length > 5 ? 6 : this.certificates.length;
        break;
    }
    return row;
  }

}
