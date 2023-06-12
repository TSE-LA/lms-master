import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, ValidatorFn} from "@angular/forms";
import ProfileValidation from "../../utils/profile-validation";
import {GENDER_FEMALE, GENDER_MALE, GENDER_OTHER, PROFILE_GENDER} from "../../models/profile.constants";
import {DialogRef} from "../../../../../../shared/src/lib/dialog/dialog-ref";
import {UserManagementSandboxService} from "../../service/user-management-sandbox.service";
import {FormUtil} from "../../../../../../shared/src/lib/utilities/form.util";
import {Gender, UserCreateModel, UserDetailedModel} from "../../models/user-management.model";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {UPDATE_USER_FAILURE_MSG, UPDATE_USER_SUCCESS_MSG} from '../../models/user-management.constant';
import {Field} from "../../models/profile.model";
import {
  DatetimeUtil,
  getFieldValue,
  getSelectOptions,
  getTranslatedName
} from "../../../util/date-time-util";

@Component({
  selector: 'jrs-user-update-dialog',
  template: `
    <jrs-scroll [height]="'650px'">
      <div style="padding: 0 15px">
        <div class="row gx-1">
          <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
            <jrs-input-field
              [formGroup]="formGroup"
              [formType]="'username'"
              [selectedType]="'text'"
              [pattern]="pattern"
              [label]="'Нэвтрэх нэр'"
              [required]="true" [disabled]="true">
            </jrs-input-field>
          </div>
        </div>
        <div class="row gx-1">
          <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
            <jrs-input-field
              [formGroup]="formGroup"
              [formType]="'lastName'"
              [label]="'Эцэг /эх/-ийн нэр'"
              [selectedType]="'text'">
            </jrs-input-field>
          </div>
        </div>
        <div class="row gx-1">
          <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
            <jrs-input-field
              [formGroup]="formGroup"
              [formType]="'firstName'"
              [label]="'Хэрэглэгчийн нэр'"
              [selectedType]="'text'">
            </jrs-input-field>
          </div>
        </div>
        <jrs-input-field
          [formGroup]="formGroup"
          [formType]="'email'"
          [label]="'Цахим шуудан'"
          [padding]="false"
          [selectedType]="'text'"
          [errorText]="this.validation.getUserEmailErrorMessage(formGroup)"
          [required]="true">
        </jrs-input-field>
        <div class="row gx-1" style="margin-top: 9px">
          <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6" >
            <jrs-dropdown-view [formGroup]="formGroup"
                               [formType]="'gender'"
                               [label]="'Хүйс'"
                               [icon]="'expand_more'"
                               [placeholder]="'Сонгох'"
                               [values]="genderValues"
                               [outlined]="true"
                               [required]="true">

            </jrs-dropdown-view>
          </div>
          <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6">
            <jrs-date-picker [formGroup]="formGroup"
                             [label]="'Төрсөн огноо'"
                             [placeholder]="'DD/MM/YYYY'"
                             [formType]="'birthday'"
                             (onDateChange)="changeEvent($event, 'birthday')"
                             [datePickerSize]="'standard'"
                             [width]="'100%'"
                             [error]="dateError"
                             [errorText] = "'Огноог оруулна уу'"
                             [required]="true">
            </jrs-date-picker>
          </div>
        </div>
        <div class="row gx-1">
          <div *ngFor="let field of this.fields; index as i" class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6">
            <div *ngIf="field.type === 'INPUT' && field.name !== 'jobYear'">
              <jrs-input-field
                [formGroup]="formGroup"
                [formType]="field.name"
                [label]="getFieldName(field.name)"
                [movePlaceholder]="false"
                [selectedType]="'text'"
                [required]="field.required"
              ></jrs-input-field>
            </div>
            <div *ngIf="field.type === 'INPUT' && field.name === 'jobYear'">
              <jrs-input-field
                [formGroup]="formGroup"
                [formType]="field.name"
                [label]="getFieldName(field.name)"
                [movePlaceholder]="true"
                [selectedType]="'text'"
                [required]="field.required"
              ></jrs-input-field>
            </div>
            <div *ngIf="field.type === 'SELECT'" style="margin-top: 19px">
              <jrs-dropdown-view
                [formGroup]="formGroup"
                [formType]="field.name"
                [icon]="'expand_more'"
                [placeholder]="getFieldName(field.name)"
                [values]="getOptions(field.name)"
                [outlined]="true"
                [required]="field.required"
              ></jrs-dropdown-view>
            </div>
            <div *ngIf="field.type === 'DATE'">
              <jrs-date-picker [formGroup]="formGroup"
                               (onDateChange)="changeEvent($event, 'appointedDate')"
                               [formType]="field.name"
                               [label]="'Шүүгчээр томилогдсон он'"
                               [placeholder]="'DD/MM/YYYY'"
                               [datePickerSize]="'standard'"
                               [width]="'100%'"
                               [error]="dateError"
                               [errorText] = "'Огноог оруулна уу'"
                               [required]="field.required">
              </jrs-date-picker>
            </div>

          </div>
        </div>
        <div class="row gx-1">
          <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6">
            <jrs-input-field
              [formGroup]="formGroup"
              [formType]="'phoneNumber'"
              [label]="'Утасны дугаар'"
              [selectedType]="'number'"
              [errorText]="this.validation.getPhoneNumberErrorMessage(formGroup)">
            </jrs-input-field>
          </div>
          <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6">
            <jrs-input-field
              [formGroup]="formGroup"
              [formType]="'updatePassword'"
              [label]="'Нууц үг'"
              [errorText]="validation.getPasswordErrorMessage(formGroup,'updatePassword')"
              [selectedType]="passHide? 'password': 'text'"
              [iconName]="passHide ? 'visibility_off' : 'visibility'"
              [suffixIcon]="true"
              [isMaterial]="true"
              (suffixClick)="passHide = !passHide">
            </jrs-input-field>
          </div>
        </div>
        <jrs-action-buttons [submitButton]="'Шинэчлэх'" [declineButton]="'Болих'"
                            (submitted)="save()" (declined)="closeDialog()"></jrs-action-buttons>
        <jrs-page-loader [show]="loading"></jrs-page-loader>
      </div>
    </jrs-scroll>
  `
})
export class UserUpdateDialogComponent implements OnInit{
  formGroup: FormGroup;
  validation: ProfileValidation;
  pattern: string;
  userData: any;
  genderValues = PROFILE_GENDER;
  loading: boolean;
  saving: boolean;
  passHide = true;
  dateError = false;
  fields: Field[] = [];

  private currentUser: UserDetailedModel;

  constructor(private config: DialogConfig, private dialogRef: DialogRef, private sb: UserManagementSandboxService) {
    this.userData = config.data.user;
    this.validation = new ProfileValidation();
  }

  ngOnInit(): void {
    this.loading = true;
    this.sb.getFields('jarvis').subscribe((res) => {
      this.setForm(res);
      this.fields = res;
      this.setFormGroup(this.userData);
      this.loading = false;
    });
  }

  closeDialog(): void {
    this.dialogRef.close();
  }

  save(): void {
    this.dateError = !FormUtil.isFormValid(this.formGroup);
    if (!FormUtil.isFormValid(this.formGroup)) {
      return;
    }
    this.saving = true;
    const formValues = this.formGroup.value
    const updateUser: UserCreateModel = {
      id: this.currentUser.id,
      username: formValues.username,
      lastName: formValues.lastName,
      firstName: formValues.firstName,
      email: formValues.email,
      birthday: formValues.birthday,
      phoneNumber: formValues.phoneNumber,
      gender: formValues.gender.id,
      password: formValues.updatePassword,
      properties: this.fields.reduce((result, {name, type}) => ({
        ...result,
        [name]: getFieldValue(type, formValues[name])
      }), {}),
    }
    this.sb.updateUser(updateUser).subscribe(res => {
      this.saving = false;
      if (res) {
        this.dialogRef.close(updateUser);
        this.sb.openSnackbar(UPDATE_USER_SUCCESS_MSG);
      } else {
        this.sb.openSnackbar(UPDATE_USER_FAILURE_MSG, false);
      }
    }, () => {
      this.saving = false;
      this.sb.openSnackbar(UPDATE_USER_FAILURE_MSG, false);
    })
  }

  getFieldName(name:string): string {
   return getTranslatedName(name);
  }

  getOptions(name:string): { id: string, name: string }[]{
    return getSelectOptions(name);
  }

  setFieldValue(field: Field): string | { id: string, name: string } {
    switch (field.type) {
      case 'SELECT':
        return getSelectOptions(field.name).find(({name}) => name === this.userData.properties[field.name]);
      case 'INPUT':
      case 'DATE':
      default:
        return this.userData.properties[field.name];
    }
  }

  private setForm(responseFields: Field[]): void {
    this.formGroup = new FormGroup({
      username: new FormControl('', ProfileValidation.userValidation('username') as ValidatorFn[]),
      lastName: new FormControl('', []),
      firstName: new FormControl('', []),
      email: new FormControl('', ProfileValidation.userValidation('email') as ValidatorFn[]),
      birthday: new FormControl('', []),
      phoneNumber: new FormControl('', ProfileValidation.userValidation('phoneNumber') as ValidatorFn[]),
      gender: new FormControl(''),
      updatePassword: new FormControl('', ProfileValidation.userValidation('updatePassword') as ValidatorFn[]),
      ...responseFields.reduce((properties, {name}) => ({...properties, [name]: new FormControl('')}), {}),
    })
  }
  private setFormGroup(user: UserDetailedModel): void {
    this.currentUser = user;
    this.formGroup.controls.username.setValue(user.username);
    this.formGroup.controls.username.disable();
    this.formGroup.controls.firstName.setValue(user.firstName);
    this.formGroup.controls.lastName.setValue(user.lastName);
    this.formGroup.controls.birthday.setValue(user.birthday);
    this.formGroup.controls.email.setValue(user.email);
    this.formGroup.controls.phoneNumber.setValue(user.phoneNumber);
    this.formGroup.controls.updatePassword.setValue(user.password, ProfileValidation.userValidation('updatePassword') as ValidatorFn[])
    this.formGroup.controls.gender.setValue(this.getGender(user.gender))
    for(const field of this.fields) {
      this.formGroup.controls[field.name].setValue(this.setFieldValue(field))
    }
  }

  getDaysFullMonth(day) {
    const d = new Date(day);
    const lastDay = new Date(d.getFullYear(), d.getMonth() + 1, 0);
    return lastDay.getDate();
  }

  getGender(gender: any) {
    switch(gender) {
      case Gender[Gender.FEMALE]:
        return GENDER_FEMALE
        break
      case Gender[Gender.NA]:
        return GENDER_OTHER
        break
      case Gender[Gender.MALE]:
        return GENDER_MALE
        break
    }
  }

  changeEvent(event: any, field: string) {
    if(field === 'birthday') {
      this.formGroup.controls[field].setValue(event);
    } else {
      this.formGroup.controls[field].setValue(event);
      const selectedDate = this.formGroup.controls[field].value;
      const date = DatetimeUtil.getDateDifference(selectedDate)
      this.formGroup.controls.jobYear.setValue(date?.toString());
    }
  }
}
