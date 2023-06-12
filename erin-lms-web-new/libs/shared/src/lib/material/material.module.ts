/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

import {NgModule} from "@angular/core";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatMenuModule} from "@angular/material/menu";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {MatNativeDateModule, MatOptionModule} from "@angular/material/core";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {MatAutocompleteModule} from "@angular/material/autocomplete";

@NgModule({
  imports: [
    MatDatepickerModule,
    MatMenuModule,
    MatAutocompleteModule
  ],
  providers: [],
  declarations: [],
  entryComponents: [],
  exports: [
    MatFormFieldModule,
    MatDatepickerModule,
    MatInputModule,
    MatNativeDateModule,
    MatButtonModule,
    MatIconModule,
    MatAutocompleteModule,
    MatOptionModule,
    MatMenuModule]
})
export class MaterialModule {
}
