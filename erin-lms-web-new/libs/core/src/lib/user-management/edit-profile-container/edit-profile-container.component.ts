import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, ValidatorFn} from "@angular/forms";
import {ProfileSandboxService} from "../profile-sandbox.service";
import ProfileValidation from "../utils/profile-validation";
import {FormUtil} from "../../../../../shared/src/lib/utilities/form.util";
import {Field, UserProfile} from "../models/profile.model";
import {PROFILE_GENDER} from "../models/profile.constants";
import {
  DatetimeUtil,
  getFieldValue,
  getSelectOptions,
  getTranslatedName
} from "../../util/date-time-util";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'jrs-edit-profile-container',
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
    <form [formGroup]="userProfileFormGroup">

      <jrs-section [width]="containerWidth" [minWidth]="'fit-content'">
        <jrs-skeleton-loader [amount]="6" [load]="loading"></jrs-skeleton-loader>
        <ng-container *ngIf="!loading">
          <jrs-header-text [size]="'medium'">Профайл засварлах</jrs-header-text>
          <div class="row gx-3 margin-left margin-right">
            <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
              <div class="row gx-1">
                <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
                  <jrs-input-field
                    [formGroup]="userProfileFormGroup"
                    [formType]="'lastName'"
                    [label]="'Эцэг /эх/-ийн нэр'"
                    [selectedType]="'text'"
                    [required]="true"
                    [disabled]="true">
                  </jrs-input-field>
                </div>
              </div>
              <div class="row gx-1">
                <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
                  <jrs-input-field
                    [formGroup]="userProfileFormGroup"
                    [formType]="'firstName'"
                    [label]="'Нэр'"
                    [selectedType]="'text'"
                    [required]="true"
                    [disabled]="true">
                  </jrs-input-field>
                </div>
              </div>
              <div class="row gx-1">
                <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6">
                  <jrs-dropdown-view
                    [formGroup]="userProfileFormGroup"
                    [formType]="'gender'"
                    [icon]="'expand_more'"
                    [label]="'Хүйс'"
                    [values]="genderValues"
                    [outlined]="true"
                    [required]="true"
                  ></jrs-dropdown-view>
                </div>
                <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6"
                     [ngStyle]="{ 'margin-top': '19px'}">
                  <jrs-file-field [acceptFileTypes]="avatarTypes"
                                  [acceptRegex]="allowedAvatarExtensions"
                                  [fileName]="fileName"
                                  [tooltip]="'Зураг хавсаргах'"
                                  (selectionChange)="uploadAvatar($event)"
                                  [id]="'avatar-file-upload-field'">
                  </jrs-file-field>
                </div>
              </div>
              <div class="row gx-1" [ngStyle]="{ 'border-bottom':'1px solid','border-color':'#c7c7cc'}">
                <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6">
                  <jrs-date-picker [formGroup]="userProfileFormGroup"
                                   (onDateChange)="changeEvent($event , 'birthday')"
                                   [formType]="'birthday'"
                                   [margin]="false"
                                   [placeholder]="'Төрсөн огноо'"
                                   [datePickerSize]="'standard'"
                                   [width]="'100%'"
                                   [error]="dateError"
                                   [errorText]="'Огноог оруулна уу'"
                                   [required]="true">
                  </jrs-date-picker>
                </div>
              </div>
              <div class="row gx-1">
                <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6"
                     [ngStyle]="{'padding-bottom':'28px'}">
                </div>
              </div>
              <div class="row gx-1">
                <div *ngFor="let field of fields; index as i"
                     class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6">
                  <div *ngIf="field.type === 'INPUT' && field.name !== 'jobYear'">
                    <jrs-input-field
                      [formGroup]="userProfileFormGroup"
                      [formType]="field.name"
                      [placeholder]="getFieldName(field.name)"
                      [movePlaceholder]="true"
                      [selectedType]="'text'"
                      [required]="field.required"
                    ></jrs-input-field>
                  </div>
                  <div *ngIf="field.type === 'INPUT' && field.name === 'jobYear'">
                    <jrs-input-field
                      [formGroup]="userProfileFormGroup"
                      [formType]="field.name"
                      [placeholder]="this.userProfileFormGroup.controls.jobYear.value ? '' : getFieldName(field.name)"
                      [movePlaceholder]="true"
                      [selectedType]="'text'"
                      [required]="field.required"
                    ></jrs-input-field>
                  </div>
                  <div *ngIf="field.type === 'SELECT'">
                    <jrs-dropdown-view
                      [formGroup]="userProfileFormGroup"
                      [formType]="field.name"
                      [icon]="'expand_more'"
                      [placeholder]="getFieldName(field.name)"
                      [values]="getOptions(field.name)"
                      [outlined]="true"
                      [required]="field.required"
                    ></jrs-dropdown-view>
                  </div>
                  <div *ngIf="field.type === 'DATE'" style="margin-top: 18px">
                    <jrs-date-picker [formGroup]="userProfileFormGroup"
                                     (onDateChange)="changeEvent($event, 'appointedDate')"
                                     [formType]="field.name"
                                     [margin]="false"
                                     [label]="'Шүүгчээр томилогдсон он'"
                                     [labelStyleName]="'date-label-style'"
                                     [placeholder]="'DD/MM/YYYY'"
                                     [datePickerSize]="'small'"
                                     [width]="'100%'"
                                     [error]="dateError"
                                     [errorText] = "'Огноог оруулна уу'"
                                     [required]="field.required">
                    </jrs-date-picker>
                  </div>
                </div>
              </div>
              <div class="row gx-1" [ngStyle]="{ 'border-bottom':'1px solid','border-color':'#c7c7cc'}">
                <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6"
                     [ngStyle]="{'padding-bottom':'10px'}">
                  <jrs-input-field
                    [formGroup]="userProfileFormGroup"
                    [formType]="'email'"
                    [label]="'Цахим шуудан'"
                    [selectedType]="'text'"
                    [errorText]="this.validation.getUserEmailErrorMessage(userProfileFormGroup)"
                    [required]="true"
                    [movePlaceholder]="true">
                  </jrs-input-field>
                </div>
                <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6">
                  <jrs-input-field
                    [formGroup]="userProfileFormGroup"
                    [formType]="'phoneNumber'"
                    [label]="'Утас'"
                    [selectedType]="'number'"
                    [required]="true"
                    [errorText]="this.validation.getPhoneNumberErrorMessage(userProfileFormGroup)">
                  </jrs-input-field>
                </div>
              </div>
              <div class="row gx-1">
                <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6"
                     [ngStyle]="{'padding-bottom':'28px'}">
                </div>
              </div>
              <div class="row gx-1">
                <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
                  <jrs-input-field
                    [formGroup]="userProfileFormGroup"
                    [formType]="'username'"
                    [selectedType]="'text'"
                    [errorText]="validation.getUsernameErrorMessage(userProfileFormGroup, false)"
                    [label]="'Нэвтрэх нэр'"
                    [required]="true"
                    [disabled]="true">
                  </jrs-input-field>
                </div>
                <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
                  <jrs-input-field
                    [formGroup]="userProfileFormGroup"
                    [formType]="'newPassword'"
                    [label]="'Нууц үг'"
                    [errorText]="validation.getPasswordErrorMessage(userProfileFormGroup,'newPassword')"
                    [selectedType]="newPassHide? 'password': 'text'"
                    [iconName]="newPassHide ? 'visibility_off' : 'visibility'"
                    [suffixIcon]="true"
                    [isMaterial]="true"
                    (suffixClick)="newPassHide = !newPassHide">
                  </jrs-input-field>
                  <jrs-input-field
                    [formGroup]="userProfileFormGroup"
                    [formType]="'repeatPassword'"
                    [label]="'Нууц үг давтан оруулна уу'"
                    [errorText]="repeatPasswordErrorText"
                    [error]="repeatPasswordError"
                    [selectedType]="passRepeatHide? 'password': 'text'"
                    [iconName]="passRepeatHide ? 'visibility_off' : 'visibility'"
                    [suffixIcon]="true"
                    [isMaterial]="true"
                    (suffixClick)="passRepeatHide = !passRepeatHide">
                  </jrs-input-field>
                </div>
              </div>
            </div>
          </div>
        </ng-container>
      </jrs-section>
      <jrs-action-buttons [declineButton]="'Хаах'" [submitButton]="'Хадгалах'"
                          (submitted)="submit()" (declined)="close()"></jrs-action-buttons>
      <jrs-page-loader [show]="saving || savingAvatar"></jrs-page-loader>
    </form>
  `
})
export class EditProfileContainerComponent implements OnInit {
  userProfileFormGroup: FormGroup;
  userData: any;
  validation: ProfileValidation;
  saving: boolean;
  loading: boolean;
  containerWidth = '30vw';
  repeatPasswordError = false;
  dateError = false;
  repeatPasswordErrorText = 'Ижил нууц үг оруулна уу!';
  genderValues = PROFILE_GENDER;
  newPassHide = true;
  passRepeatHide = true;
  fields: Field[] = [];

  avatarTypes = ".jpeg, .png, .gif, .jpg";
  allowedAvatarExtensions = /(\.jpeg|\.png|\.gif|\.jpg)$/i;
  fileName: string;
  savingAvatar: boolean;
  userName: string;

  constructor(private sb: ProfileSandboxService, private route: ActivatedRoute) {
    this.validation = new ProfileValidation();
    this.route.queryParamMap
      .subscribe((params) => {
        this.userName = params.get('id')
        if (this.userName === null) {
          this.userName = ''
        }
      });
  }

  ngOnInit(): void {
    this.loading = true;
    this.sb.getFields('jarvis').subscribe((res) => {
      this.setForm(res);
      this.fields = res;
      this.sb.getUserProfile(this.userName).subscribe(res => {
        this.userData = res;
        this.assignValues();
        this.loading = false;
      }, () => {
        this.loading = false;
      })
    });

    this.sb.getMediaBreakPointChange().subscribe(res => {
      const currentMode = (res == "media_sm" || res == "media_s" || res == "media_xs")
      if (currentMode) {
        this.containerWidth = '75vw'
      } else if (res == "media_xl" || res == "media_xxl") {
        this.containerWidth = '30vw'
      } else {
        this.containerWidth = '35vw'
      }
    })
  }

  assignValues(): void {
    this.userProfileFormGroup.controls.username.setValue(this.userData.username);
    this.userProfileFormGroup.controls.firstName.setValue(this.userData.firstName);
    this.userProfileFormGroup.controls.lastName.setValue(this.userData.lastName);
    this.userProfileFormGroup.controls.email.setValue(this.userData.email);
    this.userProfileFormGroup.controls.birthday.setValue(this.userData.birthday);
    this.userProfileFormGroup.controls.phoneNumber.setValue(this.userData.phoneNumber);
    this.userProfileFormGroup.controls.gender.setValue(this.genderValues.find(gender => gender.id === this.userData.gender));
    for (const field of this.fields) {
      this.userProfileFormGroup.controls[field.name].setValue(this.setFieldValue(field));
    }
  }

  submit(): void {
    if (this.isValid() && FormUtil.isFormValid(this.userProfileFormGroup)) {
      this.saving = true;
      this.sb.updateProfile(this.getUserProfileInfo()).subscribe(() => {
        this.saving = false
        this.sb.openSnackbar("Амжилттай хадгаллаа.");
        this.navigateBack();
      }, () => {
        this.saving = false
        this.sb.openSnackbar("Хувийн мэдээлэл хадгалахад алдаа гарлаа!", false);
      })
    }
  }

  isValid(): boolean {
    let isSame = false;
    this.dateError = !FormUtil.isFormValid(this.userProfileFormGroup);
    if (this.userProfileFormGroup.controls.newPassword.value === this.userProfileFormGroup.controls.repeatPassword.value) {
      isSame = true
      this.repeatPasswordError = false
    } else {
      this.repeatPasswordError = true
    }
    return isSame;
  }

  goBack(): void {
    this.sb.goBack();
  }

  getFieldName(name: string): string {
    return getTranslatedName(name);
  }

  private setForm(responseFields: Field[]): void {
    this.userProfileFormGroup = new FormGroup({
      username: new FormControl('', ProfileValidation.userValidation('username') as ValidatorFn[]),
      lastName: new FormControl('', []),
      firstName: new FormControl('', []),
      email: new FormControl('', ProfileValidation.userValidation('email') as ValidatorFn[]),
      birthday: new FormControl('', []),
      phoneNumber: new FormControl('', ProfileValidation.userValidation('phoneNumber') as ValidatorFn[]),
      gender: new FormControl(''),
      newPassword: new FormControl('', ProfileValidation.userValidation('newPassword') as ValidatorFn[]),
      repeatPassword: new FormControl(''),
      ...responseFields.reduce((properties, {name}) => ({...properties, [name]: new FormControl('')}), {}),
    })
  }

  private getUserProfileInfo(): UserProfile {
    const formValues = this.userProfileFormGroup.value
    return {
      username: this.userProfileFormGroup.controls.username.value,
      firstName: this.userProfileFormGroup.controls.firstName.value,
      lastName: this.userProfileFormGroup.controls.lastName.value,
      email: this.userProfileFormGroup.controls.email.value,
      phoneNumber: this.userProfileFormGroup.controls.phoneNumber.value,
      birthday: this.userProfileFormGroup.controls.birthday.value,
      gender: this.userProfileFormGroup.controls.gender.value.id,
      newPassword: this.userProfileFormGroup.controls.newPassword.value,
      properties: this.fields.reduce((result, {name, type}) => ({
        ...result,
        [name]: getFieldValue(type, formValues[name])
      }), {}),
    };
  }

  getOptions(name: string): { id: string, name: string }[] {
    return getSelectOptions(name);
  }

  // Avatar the last code bender
  uploadAvatar(file: File) {
    this.savingAvatar = true;
    this.sb.uploadAvatar(file).subscribe(() => {
      this.savingAvatar = false;
    }, () => {
      this.savingAvatar = false;
      this.sb.openSnackbar("Файл ачааллахад алдаа гарлаа!")
    });
  }

  close(): void {
    this.goBack();
  }

  setFieldValue(field: Field): string | { id: string, name: string } {
    switch (field.type) {
      case 'SELECT':
        return getSelectOptions(field.name).find(
          ({id, name}) => id === this.userData.properties[field.name] || name === this.userData.properties[field.name]
        );
      case 'INPUT':
      case 'DATE':
      default:
        return this.userData.properties[field.name];
    }
  }

  navigateBack(): void {
    this.sb.navigateByUrl('/profile/view');
  }

  getDaysFullMonth(day) {
    const d = new Date(day);
    const lastDay = new Date(d.getFullYear(), d.getMonth() + 1, 0);
    return lastDay.getDate();
  }

  changeEvent(event: any, field: string) {
    if(field === 'birthday') {
      this.userProfileFormGroup.controls[field].setValue(event);
    } else {
      this.userProfileFormGroup.controls[field].setValue(event);
      const selectedDate = this.userProfileFormGroup.controls[field].value;
      const date = DatetimeUtil.getDateDifference(selectedDate)
      this.userProfileFormGroup.controls.jobYear.setValue(date?.toString());
    }
  }
}
