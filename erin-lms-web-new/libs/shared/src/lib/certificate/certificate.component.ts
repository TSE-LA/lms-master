import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {CertificateModel} from "../../../../core/src/lib/certificate/model/certificate.model";

@Component({
  selector: 'jrs-certificate',
  template: `
    <div class="certificate">
      <div class="certificate-image">
        <img [src]="image" [alt]="desc"/>
      </div>
      <div class="certificate-footer">
        <div class="certificate-footer__left">
          <p class="certificate-name">{{this.name}}</p>
          <span class="certificate-author">Үүсгэсэн: {{this.author}}</span>
          <div class="certificate-actions">
            <div (click)="viewClicked()">
              <jrs-icon [mat]="true" [color]="'gray'" [size]="'medium'">visibility</jrs-icon>
            </div>
            <div *ngIf="!isUsed" (click)="deleteClicked()">
              <jrs-icon [mat]="true" [color]="'warn'" [size]="'medium'">delete</jrs-icon>
            </div>
          </div>
        </div>
        <div class="certificate-footer__right">
          <p class="certificate-created-date">{{this.createdDate}}</p>
          <div class="certificate-status" [class.un-used]="!isUsed">
            <span>{{this.isUsed ? 'Идэвхтэй' : 'Идэвхгүй'}}</span>
          </div>
        </div>
      </div>
    </div>`,
  styleUrls: ['./certificate.component.scss']
})
export class CertificateComponent {
  @Input() name: string
  @Input() author: string;
  @Input() id: string
  @Input() createdDate = '';
  @Input() image: string;
  @Input() desc: string;
  @Input() isUsed: boolean;
  @Output() deleteClick = new EventEmitter();
  @Output() viewClick = new EventEmitter();


  viewClicked() {
    const certificate: CertificateModel = {
      authorId: this.author,
      used: this.isUsed,
      name: this.name,
      id: this.id
    }
    this.viewClick.emit(certificate)
  }

  getThumbnailUrl(): string {
    return `alfresco/`;
  }


  deleteClicked() {
    this.deleteClick.emit(this.id)
  }
}
