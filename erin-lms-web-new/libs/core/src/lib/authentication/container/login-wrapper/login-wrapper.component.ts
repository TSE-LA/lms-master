import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthenticationSandbox} from "../../authentication-sandbox";
import {finalize} from "rxjs/operators";
import { Subscription} from "rxjs";


@Component({
  selector: 'jrs-login-wrapper',
  template: `
    <div class="login-container">
      <div
        class="background-image"
        [ngStyle]="{'background-image':' url(' + background+ ')'}">
      </div>
      <jrs-login-field
        [loading]="loading"
        [loginLogo]="loginLogo"
        [getLogo]="logo"
        [invalidPassword]="invalidPassword"
        [errorMessage]="errorMessage"
        [version]="sb.getAppVersion()"
        (onLogin)="this.login($event)">
      </jrs-login-field>
    </div>`,
  styleUrls: ['./login-wrapper.component.scss']
})
export class LoginWrapperComponent implements OnInit, OnDestroy {
  invalidPassword = false;
  currentYear: number;
  loading = false;
  errorMessage: string;
  background = '/alfresco/Background/background.png';
  loginLogo = 'assets/images/header-logo.png';
  logo = "";

  backgroundImageSub: Subscription;

  constructor(public sb: AuthenticationSandbox) {
    const date = new Date();
    this.currentYear = date.getFullYear();
  }

  ngOnInit(): void {
    this.loadPage();
    this.sb.logout();
  }

  ngOnDestroy(): void {
    this.backgroundImageSub.unsubscribe();
  }

  login(credentials) {
    this.loading = true;
    this.errorMessage = '';
    this.sb.login(credentials.username, credentials.password).pipe(finalize(() => {
        this.loading = false;
      })
    ).subscribe(() => {
      this.loading = false;
    }, error => {
      this.loading = false;
      this.invalidPassword = true;
      if (error) {
        this.sb.getTranslateService().get(error).subscribe(res => this.errorMessage = res);
      } else {
        this.errorMessage = 'Интернэт холболтоо шалгана уу.';
      }
    });
  }

  private loadPage(): void {
    this.backgroundImageSub = this.sb.getBackgroundImageUrl().subscribe(res => {
      this.background = res;
    });
    this.sb.getLogoImageUrl().subscribe(res => {
      this.logo = res;
    });
  }
}
