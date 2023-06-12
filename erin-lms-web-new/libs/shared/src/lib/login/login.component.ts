import {Component, Output, EventEmitter, Input} from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'jrs-login',
  template: `
    <div class="login-container">
      <div class="background-image" [ngStyle]="{'background-image':' url(' + background+ ')'}"></div>
      <div class="login-form">
        <form #loginForm="ngForm" (ngSubmit)="login()">
          <div class="logo" [ngStyle]="{'background-image':' url(' + loginLogo+ ')'}"></div>
          <input type="email" placeholder="Хэрэглэгчийн нэр" required/>
          <input type="password" placeholder="Нууц үг" (keydown.enter)="login()" required/>
          <span *ngIf="invalidPassword" class="error">Нууц үг буруу байна.</span>
          <span *ngIf="!invalidPassword" class="error"></span>
          <jrs-button [color]="'primary'" [size]="'medium'">{{LOGIN}}</jrs-button>
          <div class="footer">Powered by <a class="link" (click)="redirect('https://www.erin.systems')">ERIN Systems LLC</a> {{currentYear}}</div>
        </form>
      </div>
    </div>`,
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  @Input() invalidPassword = false;
  background: string;
  loginLogo: string;
  LOGIN = 'НЭВТРЭХ';
  username: string;
  password: string;
  currentYear: number;
  @Output() onLogin = new EventEmitter();

  constructor(private router: Router) {
    this.background = '/alfresco/Organization/jarvis/background.png';
    this.loginLogo = '/alfresco/Background/login-logo.png';
    const date = new Date();
    this.currentYear = date.getFullYear();
  }

  login() {
    this.router.navigateByUrl('home');
    this.onLogin.emit({username: this.username, password: this.password});
  }

  redirect(url) {
    window.open(url);
  }
}
