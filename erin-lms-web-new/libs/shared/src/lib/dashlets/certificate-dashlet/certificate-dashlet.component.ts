import {Component, Input, OnInit, ViewEncapsulation} from "@angular/core";
import {CertificateSandbox} from "../../../../../core/src/lib/certificate/certificate.sandbox";
import {DialogConfig} from "../../dialog/dialog-config";
import {FileViewDialogComponent} from "../../dialog/file-view-dialog/file-view-dialog.component";
import {ReceivedCertificateModel} from "../../../../../core/src/lib/certificate/model/certificate.model";

@Component({
  selector: 'jrs-certificate-dashlet',
  template: `
    <div *ngIf="this.allCertificates">
      <jrs-header-text class="header-text">МИНИЙ СЕРТИФИКАТ</jrs-header-text>
      <div class="certificates">
        <ng-container *ngFor="let certificate of this.allCertificates">
            <div *ngIf="this.certificate">
              <div class="certificate-dashlet-container" [class.loading]="loading">
                <div class="certificate-dashlet-header">
                  <span class="certificate-type">{{certificate.type}}</span>
                  <div class="spacer"></div>
                  <div *ngIf="!loading" class="certificate-badge">
                    <img src="{{this.certificateBadge}}" [alt]="'Зураг олдсонгүй'" [ngStyle]="{width: this.imageWidth}">
                  </div>
                </div>
                <a class="certificate-name" (click)="this.openCertificate(certificate)">
                  <span class="certificate-name-text" jrsTooltip="{{this.getTooltip(this.certificate.courseName)}}">{{this.certificate.courseName}}</span>
                </a>
                <div class="certificate-date">
                  <span>{{this.certificate.date}}</span>
                </div>
              </div>
            </div>
        </ng-container>
        </div>
    </div>
  `,
  styleUrls: ['./certificate-dashlet.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class CertificateDashletComponent implements OnInit {
  @Input() loading = false;
  @Input() imageWidth = '30px';
  @Input() certificateDashletData: ReceivedCertificateModel[];
  allCertificates: ReceivedCertificateModel[] = [];
  certificateBadge = 'assets/images/badge.png';
  mediaState: number = 3;
  direction: string;

  constructor(private sb: CertificateSandbox) {
  }

  ngOnInit(): void {
    this.loading = true;
    this.getResponsiveSize();
    if(this.certificateDashletData){
      this.allCertificates = this.certificateDashletData;
      this.loading = false;
    } else {
      this.loading = false;
    }
  }

  openCertificate(certificate): void {
    const config = new DialogConfig();
    config.outsideClick = false;
    config.background = false;
    config.blur = true;
    config.width = 'auto'
    config.data = {
      courseName: certificate.courseName,
      source: '/alfresco/' + 'Learner-Certificates/' + certificate.courseId + '/' + certificate.learnerId + '/certificate.PDF',
    };
    this.sb.openDialog(FileViewDialogComponent, config);
  }

  getTooltip(text) {
    if (text && text.length > 40) {
      return text;
    } else {
      return null;
    }
  }

  getResponsiveSize() {
    this.sb.getMediaBreakPointChange().subscribe(res => {
      const onMobileDevice = (res == "media_sm" || res == "media_s" || res == "media_xs");
      if (onMobileDevice) {
        this.mediaState = 1;
      } else {
        if (res === 'media_xxl') {
          this.mediaState = 4;
        } else if (res === 'media_xl') {
          this.mediaState = 4;
        } else if (res === 'media_lg') {
          this.mediaState = 3
        } else if (res === 'media_md') {
          this.mediaState = 3;
        } else if (res === 'media_sm') {
          this.mediaState = 2;
        } else if (res === 'media_s') {
          this.mediaState = 1;
        }
      }
    })
  }
}
