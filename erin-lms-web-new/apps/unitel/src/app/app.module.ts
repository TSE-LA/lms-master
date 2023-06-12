import {Component, CUSTOM_ELEMENTS_SCHEMA, NgModule, NO_ERRORS_SCHEMA} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {RouterModule} from '@angular/router';
import {SharedModule} from "@erin-lms-web-new/shared";
import {CoreModule} from "@erin-lms-web-new/core";
import {Routing} from "./app-routing";
import {CONSTANTS} from "../assets/constants/constants";
import {environment} from "../environments/environment";
import {StoreModule} from "@ngrx/store";
import {rootReducer} from "../../../../libs/core/src/lib/common/statemanagement/rootReducer";
import {CustomHttpInterceptor} from "../../../../libs/core/src/lib/authentication/services/custom-http-interceptor.service";
import {HTTP_INTERCEPTORS} from "@angular/common/http";



@Component({
  selector: 'erin-lms-web-root',
  template: `
    <div jrsThemeManager [syncThemeWithOperatingSystem]="true">
      <router-outlet></router-outlet>
    </div>
  `
})
export class AppComponent {
  title = 'unitel';
}

@NgModule({
  declarations: [AppComponent],
  imports: [
    Routing,
    BrowserModule,
    SharedModule,
    RouterModule,
    CoreModule,
    StoreModule.forRoot(rootReducer)],
  providers: [
    SharedModule,
    {provide: 'baseUrl', useValue: environment.baseUrl},
    {provide: 'constants', useValue: CONSTANTS},
    {provide: 'version', useValue: environment.appVersion},
    {provide: 'tenantId', useValue: environment.tenantId},
    {provide: HTTP_INTERCEPTORS, useClass: CustomHttpInterceptor, multi: true}],
  bootstrap: [AppComponent],
  schemas: [
    CUSTOM_ELEMENTS_SCHEMA,
    NO_ERRORS_SCHEMA
  ]
})
export class AppModule {
}
