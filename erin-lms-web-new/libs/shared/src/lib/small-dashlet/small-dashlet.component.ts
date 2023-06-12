import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {DropdownModel} from "../dropdown/dropdownModel";
import {UserRoleProperties} from "../../../../core/src/lib/common/common.model";

@Component({
  selector: 'jrs-small-dashlet',
  template: `
    <div *ngIf="!empty"
         class="container"
         [ngClass]="[backgroundColor]"
         [class.has-dropdown]="hasDropDown"
         [class.loading]="loading"
         [style.height]="height">
      <ng-container *ngIf="!noData">
        <div class="header">
          <span>{{title}}</span>
          <jrs-dropdown-view
            *ngIf="hasDropDown && !loading"
            class="select-role"
            [chooseFirst]="true"
            [values]="DROP_DOWN_VALUES"
            [color]="'light'"
            [outlined]="true"
            (selectedValue)="selectValue($event)"
            [size]="'small'">
          </jrs-dropdown-view>
        </div>
        <div class="info">
          <span class="info-text">{{dataToShow}}</span>
          <div *ngIf="imageSrc && !loading" class="image">
            <img src="{{imageSrc}}" [alt]="'Зураг олдсонгүй'" [ngStyle]="{width: this.imageWidth}">
          </div>
          <jrs-button
            class="navigate"
            *ngIf="!imageSrc && !loading"
            [textColor]="'text-link'"
            [size]="'small-medium'"
            [title]="'Дэлгэрэнгүй'"
            [noOutline]="true"
            (clicked)="navigate()">
          </jrs-button>
        </div>
      </ng-container>
      <jrs-label *ngIf="noData" [text]="EMPTY_TEXT"></jrs-label>
    </div>
  `,
  styleUrls: ['./small-dashlet.component.scss']
})
export class SmallDashletComponent implements OnInit, OnChanges {
  @Input() title: string;
  @Input() imageSrc: string;
  @Input() info: string;
  @Input() backgroundColor = 'default-background';
  @Input() height = '100px';
  @Input() hasDropDown: boolean;
  @Input() navigateLink: string;
  @Input() extraData: any;
  @Input() loading = true;
  @Input() imageWidth = '100%'
  @Input() noData = false;
  @Input() empty = false;
  @Output() navigateToActivityTable = new EventEmitter<boolean>();
  DROP_DOWN_VALUES: DropdownModel[] = [{id: 'all', name: 'Бүгд'}, {id: 'LMS_USER', name: 'Суралцагч'}, {id: 'LMS_SUPERVISOR', name: 'Ахлах/Менежер'}];
  EMPTY_TEXT = 'Мэдээлэл байхгүй';
  dataToShow: any;

  ngOnInit(): void {
    this.dataToShow = this.info;
  }

  ngOnChanges(changes: SimpleChanges) {
    for (let prop in changes) {
      if (prop == 'info') {
        this.dataToShow = this.info;
      }
    }
  }

  selectValue(value: DropdownModel): void {
    if (value.id == UserRoleProperties.employeeRole.id) {
      this.dataToShow = this.extraData.employee;
    } else if (value.id == UserRoleProperties.supervisorRole.id) {
      this.dataToShow = this.extraData.manager;
    } else {
      this.dataToShow = this.info;
    }
  }


  navigate(): void {
    if (this.navigateLink) {
      this.navigateToActivityTable.emit(true);
    }
  }
}
