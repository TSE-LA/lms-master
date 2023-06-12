import {Component, OnInit} from "@angular/core";
import {ProfileSandboxService} from "../profile-sandbox.service";
import {CourseInfo, UserProfile} from "../models/profile.model";
import {JOB_TITLE, PROFILE_COURSE_INFO_TABLE_COLUMNS, PROFILE_GENDER} from "../models/profile.constants";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {DropdownModel} from "../../../../../shared/src/lib/dropdown/dropdownModel";
import {ActivatedRoute} from "@angular/router";
import { UserRoleProperties } from "../../common/common.model";
import { DialogConfig } from "../../../../../shared/src/lib/dialog/dialog-config";
import {UserModel} from "../models/user-management.model";
import {UserUpdateDialogComponent} from "../user-management-container/user-update-dialog/user-update-dialog.component";
@Component({
  selector: 'jrs-view-profile-container',
  template: `
    <jrs-button class="create-page-return-btn"
                [iconName]="'arrow_back_ios'"
                [iconColor]="'secondary'"
                [noOutline]="true"
                [isMaterial]="true"
                [size]="'medium'"
                [bold]="true"
                [textColor]="'text-link'"
                (clicked)="goBack()">БУЦАХ
    </jrs-button>

    <jrs-section class="margin-bottom-large" [maxWidth]="'80vw'" [width]="'100%'">
      <ng-container>
        <div class="flex margin-left">
          <jrs-header-text [size]="'medium'">Үндсэн мэдээлэл</jrs-header-text>
          <span class="spacer"></span>
          <jrs-circle-button
            [size]="'medium'"
            [isMaterial]="true"
            [iconName]="'edit'"
            [color]="'none'"
            [iconColor]="'primary'"
            [attr.data-test-id]="'profile-edit-button'"
            (clicked)="navigateToEditPage()">
          </jrs-circle-button>
        </div>
        <div class="responsive">
          <div class="margin-left">
            <jrs-image-view [src]="avatar"></jrs-image-view>
          </div>
          <div style="flex-grow: 1" class="margin-left">
            <div class="row g-5 margin-all">
              <div class="responsive-grid">
                <jrs-label [text]="'Эцэг /эх/-ийн нэр'"></jrs-label>
                <jrs-label [text]="userData?.lastName" [isBold]="true"></jrs-label>
                <jrs-label [text]="'Нэр'"></jrs-label>
                <jrs-label [text]="userData?.firstName" [isBold]="true"></jrs-label>
                <jrs-label [text]="'Хүйс'"></jrs-label>
                <jrs-label [text]="genderValue" [isBold]="true"></jrs-label>
                <jrs-label [text]="'Төрсөн огноо'"></jrs-label>
                <jrs-label [text]="userData?.birthday" [isBold]="true"></jrs-label>
              </div>
            </div>

            <jrs-divider></jrs-divider>

            <div class="row g-5 margin-all ">
              <div class="responsive-grid2">
                <jrs-label [text]="'Цахим шуудан'"></jrs-label>
                <jrs-label [text]="userData?.email" [isBold]="true"></jrs-label>
                <jrs-label [text]="'Утас'"></jrs-label>
                <jrs-label [text]="userData?.phoneNumber" [isBold]="true"></jrs-label>
                <ng-container *ngFor="let index of indexes">
                  <jrs-label [text]="getLabel(keys[index])"></jrs-label>
                  <jrs-label [text]="getTranslatedValue(userData?.properties[keys[index]])" [isBold]="true"></jrs-label>
                </ng-container>
              </div>
            </div>
          </div>
        </div>
      </ng-container>

      <ng-container *ngIf="!isAdmin() || userName !== null">
        <div class="flex">
          <jrs-header-text [size]="'medium'">Сургалтад хамрагдсан байдал</jrs-header-text>
          <span class="spacer"></span>
          <div style="margin-top: 15px">
            <jrs-dropdown-view
              [formGroup]="formGroup"
              [formType]="'year'"
              [outlined]="true"
              [size]="'small'"
              [load]="loadYears"
              [icon]="'expand_more'"
              [width]="'100px'"
              [values]="years"
              [defaultValue]="currentYear.toString()"
              (selectedValue)="yearSelect($event)"
            ></jrs-dropdown-view>
          </div>
        </div>

        <div class="flex margin-left">
          <div class="margin-left">
            <jrs-label [size]="'small'" [fontStyle]="'italic'"
                       [text]="'Нийт '"></jrs-label>
            <jrs-label [size]="'small'"  [fontStyle]="'italic'"
                       [text]="totalCredit ? totalCredit.toString() : '0'"></jrs-label>
            <jrs-label [size]="'small'" [fontStyle]="'italic'"
                       [text]="'бц'"></jrs-label>
            <jrs-label [size]="'small'" [fontStyle]="'italic'"
                       [text]="'цуглуулснаас'"></jrs-label>
            <jrs-label [size]="'small'" [fontStyle]="'italic'"
                       [text]="selectedYear"></jrs-label>
            <jrs-label [size]="'small'" [fontStyle]="'italic'"
                       [text]="' онд '"></jrs-label>
            <jrs-label [size]="'small'" [fontStyle]="'italic'"
                       [text]="yearCredit ? yearCredit.toString() : '0'"></jrs-label>
            <jrs-label [size]="'small'" [fontStyle]="'italic'"
                       [text]="' бц'"></jrs-label>
            <jrs-label [size]="'small'" [fontStyle]="'italic'"
                       [text]="'байна'"></jrs-label>
          </div>
        </div>

        <div class="margin-top">
          <jrs-dynamic-table
            [notFoundText]="notFoundText"
            [minWidth]="'unset'"
            [maxWidth]="'unset'"
            [tableMinHeight]="'50vh'"
            [dataPerPage]="10"
            [freezeFirstColumn]="true"
            [dataSource]="dataSource"
            [loading]="historyLoad"
            [tableColumns]="tableColumns"
          ></jrs-dynamic-table>
        </div>
      </ng-container>
    </jrs-section>
  `,
  styleUrls: ['./view-profile-container.component.scss']
})

export class ViewProfileContainerComponent implements OnInit {
  userData: UserProfile;
  formGroup: FormGroup;
  loading: boolean;
  currentYear: number;
  loadYears = false;
  notFoundText = 'Мэдээлэл олдсонгүй';
  tableColumns = PROFILE_COURSE_INFO_TABLE_COLUMNS;
  dataSource: CourseInfo[] = [];
  genderValue: string;
  isDisabledJurisdictionalCourt = false;
  isDisabledJobTitle = false;
  keys: string[];
  indexes: number[];
  years: DropdownModel[] = [];
  totalCredit;
  yearCredit;
  historyLoad: boolean;
  selectedYear = '0000';
  avatar: string;
  userName: string | null;

  constructor(private sb: ProfileSandboxService, private route: ActivatedRoute) {
    this.currentYear = new Date().getFullYear();
    this.selectedYear = this.currentYear.toString();
    this.generateYears();
  }

  ngOnInit() {
    this.setFormGroup();
    this.sb.getFields('jarvis').subscribe((fieldsResponse) => {
      this.isDisabledJurisdictionalCourt = fieldsResponse?.find(field => field.name === "jurisdictionalCourt");
      this.isDisabledJobTitle = fieldsResponse?.find(field => field.name === "jobTitle");
      this.loading = true;
      this.route.queryParamMap
        .subscribe((params) => {
            this.userName = params.get('id');
            this.getUserProfile(this.userName || '');
            this.loadPage(this.userName || '');
            this.sb.getAvatarUrl(this.userName||'').subscribe(res => {
              this.avatar = res;
            })
          }
        );
    });
  }

  private getCourseHistory(name: string, year: string): void {
    this.historyLoad = true;
    this.sb.getHistory(name, year).subscribe((res) => {
      this.dataSource = res ? res.map((history) => {
        const hasCredit = history.credit !== 0;
        return {
          ...history,
          credit: hasCredit ? String(history.credit) : '- ',
          creditInfo: hasCredit ? 'Заавал судлах' : 'Сонгон судлах',
          type: this.getCourseType(history.type),
        }
      }) : [];
      this.historyLoad = false;
    }, () => {
      this.sb.openSnackbar("Сургалтын түүх ачааллахад алдаа гарлаа!");
      this.historyLoad = false;
    });
  }

  setFormGroup(): void {
    this.formGroup = new FormGroup({
      year: new FormControl({id: null, name: this.currentYear}, [Validators.required]),
    });
  }

  loadPage(name: string) {
    this.getCourseHistory(name, this.currentYear.toString());
    this.getCredits(name, this.currentYear.toString())
  }

  navigateToEditPage():void {
    if(this.userName) {
      this.openUpdateUserDialog(this.userData);
    } else {
      this.sb.navigateByUrl('/profile/edit');
    }

  }

  openUpdateUserDialog(updatingUser: UserModel): void {
    const config = new DialogConfig();
    config.title = "Хэрэглэгчийн мэдээлэл засах";
    config.data = {user: updatingUser};
    this.sb.openDialog(UserUpdateDialogComponent, config).afterClosed.subscribe(res => {
      this.getUserProfile(this.userName);
    });
  }

  isAdmin(): boolean {
    return this.sb.role === UserRoleProperties.adminRole.id;
  }

  yearSelect(value: DropdownModel): void {
    this.formGroup.controls.year.setValue(value);
    this.selectedYear = value.name.toString();
    this.getCourseHistory(this.userName||'', this.selectedYear);
    this.getCredits(this.userName||'', this.selectedYear);
  }

  getLabel(fieldName: string): string {
    switch (fieldName) {
      case 'jurisdictionalCourt':
        return 'Харьяалах шүүх';
      case 'appointedDate':
        return 'Шүүгчээр томилогдсон огноо';
      case 'jobYear':
        return 'Шүүгчээр ажиллаж буй жил';
      case 'jobTitle':
        return 'Албан тушаал';
      case 'other':
        return 'Бусад';
      default:
        return fieldName;
    }
  }

  getCourseType(type: string): string {
    switch (type) {
      case 'online-course':
        return 'Цахим сургалт';
      case 'classroom-course':
        return 'Танхимын сургалт';
      default:
        return type;
    }
  }

  async goBack(): Promise<void> {
    this.sb.goBack();
  }

  private generateYears(): void {
    for (let year = 2019; year <= this.currentYear; year++) {
      this.years.push({id: year.toString(), name: year.toString()})
    }
  }

  private getCredits(name: string, year: string): void {
    this.sb.getHistoryCredits(name, year).subscribe(res => {
      this.totalCredit = res.totalCredit;
      this.yearCredit = res.yearCredit;
      this.loading = false;
    }, () => {
      this.sb.openSnackbar("Багц цаг ачааллахад алдаа гарлаа!");
    })
  }

  getTranslatedValue(value: string): string {
    return JOB_TITLE.find(({id}) => id === value)?.name || value;
  }

  getUserProfile(username: string): void {
    this.sb.getUserProfile(username).subscribe(res => {
      this.userData = res;
      this.genderValue = PROFILE_GENDER.find(gender => gender.id === this.userData.gender)?.name || '';
      this.keys = Object.keys(this.userData.properties);
      this.indexes = this.keys.map((_, index) => index);
      this.loading = false;
    }, () => {
      this.loading = false;
    });
  }
}
