import {Component, Input} from '@angular/core';
import {SmallDashletModel} from "../shared-model";
import {DetailedUserInfo} from "../../../../core/src/lib/common/common.model";

@Component({
  selector: 'jrs-small-dashlets-bundle',
  template: `
    <div class="small-dashlets" [style.grid-template-columns]="getLength()">
      <jrs-small-dashlet
        *ngFor="let dashlet of dashlets"
        [title]="dashlet.title"
        [info]="dashlet.info"
        [loading]="loading"
        [height]="'120px'"
        [hasDropDown]="dashlet.hasDropDown"
        [imageSrc]="dashlet.imageSrc"
        [extraData]="dashlet.extraData"
        [navigateLink]="dashlet.navigateLink"
        [empty]="dashlet.empty"
        [noData]="showEmptyUser">
      </jrs-small-dashlet>
      <jrs-user-info-dashlet
        *ngIf="selectedUserInfo"
        [username]="selectedUserInfo.username"
        [role]="role"
        [loading]="loading"
        [phoneNumber]="selectedUserInfo.phoneNumber"
        [email]="selectedUserInfo.email"
        [totalScore]="totalScore"
        [noData]="showEmptyUser">
      </jrs-user-info-dashlet>
    </div>
  `,
  styleUrls: ['./small-dashlets-bundle.component.scss']
})
export class SmallDashletsBundleComponent {
  @Input() loading: boolean;
  @Input() dashlets: SmallDashletModel[];
  @Input() selectedUserInfo: DetailedUserInfo;
  @Input() totalScore: number;
  @Input() role: string;
  @Input() showEmptyUser: boolean;

  getLength(): string {
    return 'repeat(' + (this.dashlets.length + (this.selectedUserInfo ? 1 : 0)) + ', 1fr)'
  }
}
