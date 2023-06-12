import {Component, Input} from '@angular/core';

@Component({
  selector: 'jrs-user-info-dashlet',
  template: `
    <div class="wrapper">
      <ng-container *ngIf="!noData && !loading">
        <div class="user-info margin-right">
          <span class="info-text" [jrsTooltip]="username">Нэр: {{username}}</span>
          <span class="info-text" [jrsTooltip]="role">Эрх: {{role}}</span>
          <span class="info-text" [jrsTooltip]="phoneNumber">Утас: {{phoneNumber}}</span>
        </div>
        <div class="total-score">
          <div class="image">
            <img src="{{badge}}" [ngStyle]="{width: '45px'}" [alt]="'Зураг олдсонгүй'">
          </div>
          <span>{{totalScore}}</span>
        </div>
      </ng-container>
      <jrs-label *ngIf="noData &&!loading " [text]="EMPTY_TEXT"></jrs-label>
      <ng-container *ngIf="loading">
        <jrs-skeleton-loader [load]="loading" [amount]="3"></jrs-skeleton-loader>
        <jrs-skeleton-loader class="margin-left margin-right" [load]="loading" [amount]="3"></jrs-skeleton-loader>
      </ng-container>

    </div>
  `,
  styleUrls: ['./user-info-dashlet.component.scss']
})
export class UserInfoDashletComponent {
  @Input() username: string;
  @Input() role: string;
  @Input() phoneNumber: string;
  @Input() email: string;
  @Input() totalScore: number;
  @Input() noData = true;
  @Input() loading = false;
  EMPTY_TEXT = 'Суралцагч сонгоно уу';
  badge = 'assets/images/badge.png';

}
