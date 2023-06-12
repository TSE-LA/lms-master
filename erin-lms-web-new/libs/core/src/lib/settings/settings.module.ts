import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SystemSettingsComponent } from './system-settings/system-settings.component';
import {SharedModule} from "../../../../shared/src";
import {RouterModule} from "@angular/router";
import {JarvisCommonModule} from "../common/common.module";



@NgModule({
  declarations: [
    SystemSettingsComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    RouterModule,
    JarvisCommonModule,
  ]
})
export class SettingsModule { }
