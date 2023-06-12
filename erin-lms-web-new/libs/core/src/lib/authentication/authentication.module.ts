import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {LoginWrapperComponent} from './container/login-wrapper/login-wrapper.component';
import {LoginFieldComponent} from './component/login-field/login-field.component';
import {FormsModule} from "@angular/forms";
import {AuthenticationSandbox} from "./authentication-sandbox";
import {SharedModule} from "../../../../shared/src";
import {TranslateLoader, TranslateModule} from "@ngx-translate/core";
import {HttpClient} from "@angular/common/http";
import {TranslateHttpLoader} from "@ngx-translate/http-loader";
import {RouteGuardService} from "./services/route-guard/route-guard.service";
import {CustomHttpInterceptor} from "./services/custom-http-interceptor.service";
export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http);
}

@NgModule({
  declarations: [
    LoginWrapperComponent,
    LoginFieldComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    TranslateModule.forRoot({
      defaultLanguage: 'mn',
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    })
  ],
  providers: [
    AuthenticationSandbox,
    LoginWrapperComponent,
    LoginFieldComponent,
    CustomHttpInterceptor,
    RouteGuardService
  ]
})
export class AuthenticationModule {
}
