import {Component, OnInit} from '@angular/core';
import {UserManagementSandboxService} from "../../service/user-management-sandbox.service";
import { UserCreateModel} from "../../models/user-management.model";
import {DialogRef} from "../../../../../../shared/src/lib/dialog/dialog-ref";
import {FormControl, FormGroup, ValidatorFn} from "@angular/forms";
import ProfileValidation from "../../utils/profile-validation";
import {PROFILE_GENDER} from "../../models/profile.constants";
import {debounceTime, distinctUntilChanged} from "rxjs/operators";
import {FormUtil} from "../../../../../../shared/src/lib/utilities/form.util";
import {
  CHECK_USERNAME_FAILURE_MSG,
  CREATE_USER_FAILURE_MSG,
  CREATE_USER_SUCCESS_MSG
} from "../../models/user-management.constant";
import {Field} from "../../models/profile.model";
import {
  DatetimeUtil,
  getFieldValue, getSelectOptions,
  getTranslatedName
} from "../../../util/date-time-util";

@Component({
  selector: 'jrs-user-create-dialog',
  template: `
    <jrs-scroll [height]="'620px'">
      <div style="padding: 15px">
        <div class="row gx-1">
          <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
            <jrs-input-field
              [formGroup]="formGroup"
              [formType]="'lastName'"
              [selectedType]="'text'"
              [placeholder]="'Эцэг /эх/-ийн нэр'"
              [movePlaceholder]="true"
              [required]="true">
            </jrs-input-field>
          </div>
        </div>
        <div class="row gx-1">
          <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
            <jrs-input-field
              [formGroup]="formGroup"
              [formType]="'firstName'"
              [selectedType]="'text'"
              [required]="true"
              [padding]="false"
              [placeholder]="'Нэр'"
              [movePlaceholder]="true">
            </jrs-input-field>
          </div>
        </div>
        <div class="row gx-1" [ngStyle]="{ 'border-bottom':'1px solid','border-color':'#c7c7cc'}">
          <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6"
               style="padding-top: 24px">
            <jrs-dropdown-view [formGroup]="formGroup"
                               [formType]="'gender'"
                               [icon]="'expand_more'"
                               [placeholder]="'Хүйс'"
                               [values]="genderValues"
                               [outlined]="true"
                               [errorText] = "'Хүйс оруулна уу'"
                               [required]="true">
            </jrs-dropdown-view>
          </div>
          <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6" style="margin-top: 8px">
            <jrs-date-picker [formGroup]="formGroup"
                             (onDateChange)="changeEvent($event , 'birthday')"
                             [formType]="'birthday'"
                             [margin]="false"
                             [labelStyleName]="'date-label-style'"
                             [label]="'Төрсөн огноо'"
                             [placeholder]="'DD/MM/YYYY'"
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
            <div *ngIf="field.type === 'INPUT' && field.name !== 'jobYear'" style="margin-top: 18px">
              <jrs-input-field
                [formGroup]="formGroup"
                [formType]="field.name"
                [placeholder]="getFieldName(field.name)"
                [movePlaceholder]="true"
                [padding]="false"
                [selectedType]="'text'"
                [required]="field.required"
              ></jrs-input-field>
            </div>
            <div *ngIf="field.type === 'INPUT' && field.name === 'jobYear'" style="padding-top: 24px">
              <jrs-input-field
                [disabled]="true"
                [formGroup]="formGroup"
                [formType]="field.name"
                [placeholder]="this.formGroup.controls.jobYear.value ? '' : getFieldName(field.name)"
                [movePlaceholder]="true"
                [selectedType]="'text'"
                [required]="field.required"
              ></jrs-input-field>
            </div>
            <div *ngIf="field.type === 'SELECT'" style="margin-top: 18px">
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
            <div *ngIf="field.type === 'DATE'" style="margin-top: 8px">
              <jrs-date-picker [formGroup]="formGroup"
                               (onDateChange)="changeEvent($event, 'appointedDate')"
                               [formType]="field.name"
                               [margin]="false"
                               [labelStyleName]="'date-label-style'"
                               [label]="getFieldName(field.name)"
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
          <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6">
            <jrs-input-field
              [formGroup]="formGroup"
              [formType]="'phoneNumber'"
              [placeholder]="'Утас'"
              [selectedType]="'number'"
              [required]="true"
              [movePlaceholder]="true"
              [errorText]="this.validation.getPhoneNumberErrorMessage(formGroup)">
            </jrs-input-field>
          </div>
          <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6" [ngStyle]="{'padding-bottom':'10px'}">
            <jrs-input-field
              [formGroup]="formGroup"
              [formType]="'email'"
              [placeholder]="'Цахим шуудан'"
              [movePlaceholder]="true"
              [selectedType]="'text'"
              [errorText]="this.validation.getUserEmailErrorMessage(formGroup)"
              [required]="true">
            </jrs-input-field>
          </div>
        </div>
        <div class="row gx-1">
          <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12" style="margin-top: 28px">
            <jrs-input-field
              [formGroup]="formGroup"
              [formType]="'username'"
              [selectedType]="'text'"
              [pattern]="pattern"
              [errorText]="validation.getUsernameErrorMessage(formGroup, usernameExists)"
              [disabled]="loadSearch"
              [required]="true"
              [suffixIcon]="true"
              [loading]="loadSearch"
              [placeholder]="'Нэвтрэх нэр'"
              [movePlaceholder]="true">
            </jrs-input-field>
          </div>
        </div>
        <div class="row gx-1">
          <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
            <jrs-input-field
              [formGroup]="formGroup"
              [formType]="'password'"
              [placeholder]="'Нууц үг'"
              [required]="true"
              [errorText]="validation.getPasswordErrorMessage(formGroup,'password')"
              [selectedType]="passHide? 'password': 'text'"
              [iconName]="passHide ? 'visibility_off' : 'visibility'"
              [suffixIcon]="true"
              [isMaterial]="true"
              (suffixClick)="passHide = !passHide">
            </jrs-input-field>
          </div>
        </div>
        <div class="row gx-1 ">
          <div class="col-media_s-12 col-media_sm-12 col-media_md-12 col-media_xl-12">
            <jrs-input-field
              [formGroup]="formGroup"
              [formType]="'repeatPassword'"
              [placeholder]="'Нууц үг давтан оруулна уу'"
              [selectedType]="repeatPassHide? 'password': 'text'"
              [iconName]="repeatPassHide ? 'visibility_off' : 'visibility'"
              [suffixIcon]="true"
              [isMaterial]="true"
              [errorText]="repeatPasswordErrorText"
              [error]="repeatPasswordError"
              (suffixClick)="repeatPassHide = !repeatPassHide">
            </jrs-input-field>
          </div>
        </div>
        <jrs-action-buttons [declineButton]="'Хаах'" [submitButton]="'Хадгалах'"
                            (submitted)="save()" (declined)="closeDialog()"></jrs-action-buttons>
        <jrs-page-loader [show]="loading"></jrs-page-loader>
      </div>
    </jrs-scroll>


  `,
  styles: []
})
export class UserCreateDialogComponent implements OnInit{
  formGroup: FormGroup;
  validation: ProfileValidation;
  pattern: string;
  genderValues = PROFILE_GENDER;
  loading: boolean;
  saving: boolean;
  loadSearch: boolean;
  passHide = true;
  dateError = false;
  repeatPassHide = true;
  usernameExists: boolean;
  fields: Field[] = [];
  repeatPasswordError: boolean;
  repeatPasswordErrorText = 'Ижил нууц үг оруулна уу!';

  constructor(private dialogRef: DialogRef, private sb: UserManagementSandboxService) {
    this.validation = new ProfileValidation();
  }

  ngOnInit() {
    this.sb.getFields('jarvis').subscribe((res) => {
      this.setFormGroup(res);
      this.fields = res;
    });
  }

  checkUsername(username: string): void {
    this.sb.doesUserExist(username).subscribe(res => {
      this.pattern = res ? '^(?!' + username + '$).*$' : '^\\S*$';
      this.usernameExists = res;
      this.loadSearch = false;
    }, () => {
      this.sb.openSnackbar(CHECK_USERNAME_FAILURE_MSG, false);
      this.loadSearch = false;
    });
  }

  closeDialog(): void {
    this.dialogRef.close();
  }

  save(): void {
    if (this.isValid() && FormUtil.isFormValid(this.formGroup) && !this.usernameExists ) {
      this.saving = true;
      const formValues = this.formGroup.value;
      const newUser: UserCreateModel = {
        username: formValues.username,
        lastName: formValues.lastName,
        firstName: formValues.firstName,
        email: formValues.email,
        phoneNumber: formValues.phoneNumber,
        gender: formValues.gender.id,
        birthday: formValues.birthday,
        password: formValues.password,
        properties: this.fields.reduce((result, {name, type}) => ({...result, [name]: getFieldValue(type, formValues[name])}), {}),
      }
      this.sb.createUser(newUser).subscribe(res => {
        this.saving = false;
        this.dialogRef.close(res);
        this.sb.openSnackbar(CREATE_USER_SUCCESS_MSG);
      }, () => {
        this.saving = false;
        this.sb.openSnackbar(CREATE_USER_FAILURE_MSG, false);
      })
    }
  }

  private setFormGroup(fields: Field[]): void {
    this.formGroup = new FormGroup({
      username: new FormControl('', ProfileValidation.userValidation('username') as ValidatorFn[]),
      firstName: new FormControl(''),
      lastName: new FormControl(''),
      birthday: new FormControl(''),
      email: new FormControl('', ProfileValidation.userValidation('email') as ValidatorFn[]),
      phoneNumber: new FormControl('', ProfileValidation.userValidation('phoneNumber') as ValidatorFn[]),
      gender: new FormControl('',  ProfileValidation.userValidation('gender') as ValidatorFn[]),
      password: new FormControl('', ProfileValidation.userValidation('password') as ValidatorFn[]),
      repeatPassword: new FormControl('', ProfileValidation.userValidation('password') as ValidatorFn[]),
      ...fields.reduce((properties, { name }) => ({ ...properties, [name]: new FormControl('') }), {}),
    });

    // Time out for the username search
    this.formGroup.get('username').valueChanges.pipe(
      debounceTime(1000),
      distinctUntilChanged(),
    ).subscribe(username => {
      if (username) {
        this.loadSearch = true;
        this.checkUsername(username);
      }
    });
  }

  isValid(): boolean {
    let isSame = false;
    this.dateError = !FormUtil.isFormValid(this.formGroup);
    if (this.formGroup.controls.password.value === this.formGroup.controls.repeatPassword.value) {
      isSame = true
      this.repeatPasswordError = false;
    } else {
      this.repeatPasswordError = true;
    }
    return isSame;
  }

  getFieldName(name:string): string {
    return getTranslatedName(name);
  }

  getOptions(name:string): { id: string, name: string }[]{
    return getSelectOptions(name);
  }

  changeEvent(event: any, field: string) {
    if(field === 'birthday') {
      this.formGroup.controls[field].setValue(event);
    } else {
      this.formGroup.controls[field].setValue(event);
      const selectedDate = this.formGroup.controls[field].value;
      const date = DatetimeUtil.getDateDifference(selectedDate)
      this.formGroup.controls.jobYear.enable();
      this.formGroup.controls.jobYear.setValue(date?.toString());
    }
  }

}
