import {Component, Input, Output, EventEmitter} from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'jrs-login-field',
  template: `
    <div class="login-form" [ngClass]="{'loading': loading}">
      <form>
        <img *ngIf="!getLogo" class="logo" src="{{loginLogo}}" alt="header-logo">
        <img *ngIf="getLogo" class="logo" [src]="getLogo" (error)="getLogo = 'assets/images/header-logo.png'">
        <input [(ngModel)]="username" type="email" placeholder="Хэрэглэгчийн нэр" required/>
        <div class="password">
          <input [(ngModel)]="password" [type]="hide ? 'password' : 'text'" placeholder="Нууц үг" (keydown.enter)="login()" required/>
          <jrs-icon class="hide-icon" [mat]="true" (click)="hide = !hide">{{hide ? 'visibility_off' : 'visibility'}}</jrs-icon>
        </div>
        <span *ngIf="invalidPassword" class="error">{{errorMessage}}</span>
        <span *ngIf="!invalidPassword" class="error"></span>
        <jrs-button
          class="previous-month"
          [load]="loading"
          [title]="LOGIN"
          [color]="'primary'"
          [size]="'medium-large'"
          (clicked)="login()"></jrs-button>
        <div class="footer">JARVIS LMS {{version}} Powered by <a class="link" (click)="redirect(redirectUrl)">ERIN Systems LLC</a> {{currentYear}}</div>
      </form>
    </div>`,
  styleUrls: ['./login-field.component.scss']
})
export class LoginFieldComponent {
  @Input() invalidPassword: boolean;
  @Input() loginLogo: string;
  @Input() getLogo: string;
  @Input() version: string;
  @Input() errorMessage: string;
  @Input() loading: boolean;
  invalidInput = false;
  username: string;
  password: string;
  background: string;
  currentYear: number;
  redirectUrl = 'https://www.erin.systems';
  LOGIN = 'НЭВТРЭХ';
  hide = true;

  @Output() onLogin = new EventEmitter();

  constructor(private router: Router) {
    const date = new Date();
    this.currentYear = date.getFullYear();
  }

  login() {
    if (this.username == null) {
      this.errorMessage = 'Нэвтрэх нэр оруулна уу.'
      this.invalidPassword = true;
    } else if (this.password == null) {
      this.errorMessage = 'Нэвтрэх нууц үг оруулна уу.'
      this.invalidPassword = true;
    } else if (this.username.trim() == '') {
      this.errorMessage = 'Нэвтрэх нэр буруу байна.'
      this.invalidPassword = true;
    } else if (this.password.trim() == '') {
      this.errorMessage = 'Нэвтрэх нууц үг буруу байна.'
      this.invalidPassword = true;
    } else {
      this.onLogin.emit({username: this.username, password: this.password});
    }
  }

  redirect(url) {
    window.open(url);
  }
}
