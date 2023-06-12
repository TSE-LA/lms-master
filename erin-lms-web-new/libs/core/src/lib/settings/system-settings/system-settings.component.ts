import {Component} from '@angular/core';
import {SystemSettingsSandboxService} from "../system-settings-sandbox.service";

@Component({
  selector: 'jrs-system-settings',
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
    <div class="container-tree-view">
      <jrs-section [overflow]="true" [height]="'72vh'" [minWidth]="'fit-content'" [width]="'212px'"
                   [background]="'section-background-secondary'">
        <div>
          <jrs-button
            (clicked)="this.showButton1 = true ; this.showButton2=false"
            [noOutline]="true"
            [isMaterial]="true"
            [size]="'small-medium'"
            [textColor]="'text-primary'">
            Нэвтрэх хуудасны зураг
          </jrs-button>
          <jrs-button
            (clicked)="this.showButton2 = true; this.showButton1 = false"
            [noOutline]="true"
            [isMaterial]="true"
            [size]="'small-medium'"
            [textColor]="'text-primary'">
            Лого зураг
          </jrs-button>
        </div>
      </jrs-section>
      <jrs-section [height]="'72vh'" [minWidth]="'fit-content'" [width]="'unset'"
                   [background]="'section-background-secondary'">
        <div *ngIf="showButton1">
          <jrs-header-text [size]="'medium'" [margin]="false">Нүүр хуудасны зураг оруулах</jrs-header-text>
          <div class="row gx-1">
            <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6">
              <jrs-file-field (selectionChange)="uploadFile($event)"></jrs-file-field>
            </div>
          </div>
          <div class="flex-center margin-top margin-bottom-large">
            <jrs-button
              class="side-margin"
              [title]="'ХАДГАЛАХ'"
              [color]="'primary'"
              [size]="'medium'"
              [float]="'center'"
              [load]="saving"
              (clicked)="save()">
            </jrs-button>
          </div>
        </div>
        <div *ngIf="showButton2" >
          <jrs-header-text [size]="'medium'" [margin]="false">Лого зураг оруулах</jrs-header-text>
          <div class="row gx-1">
            <div class="col-media_s-12 col-media_sm-12 col-media_md-6 col-media_xl-6">
              <jrs-file-field (selectionChange)="uploadLogoFile($event)"></jrs-file-field>
            </div>
          </div>
          <div class="flex-center margin-top margin-bottom-large">
            <jrs-button
              class="side-margin"
              [title]="'ХАДГАЛАХ'"
              [color]="'primary'"
              [size]="'medium'"
              [float]="'center'"
              [load]="saving"
              (clicked)="save()">
            </jrs-button>
          </div>
        </div>
        <jrs-page-loader [show]="saving"></jrs-page-loader>
      </jrs-section>

    </div>
  `,
  styles: []
})
export class SystemSettingsComponent {
  saving: boolean;
  file: File;
  isLogo: boolean;
  showButton1: boolean = true;
  showButton2: boolean = false;
  constructor(private sb: SystemSettingsSandboxService) {
  }

  goBack() {
    this.sb.goBack();
  }

  uploadFile(file: File) {
    this.file = file;
    this.isLogo= false;
  }
  uploadLogoFile(file: File) {
    this.file = file;
    this.isLogo = true;
  }

  save(): void {
    this.saving = true;
    this.sb.uploadImage(this.file, this.isLogo).subscribe(() => {
      this.sb.snackbarService.open("Амжилттай шинэчлэгдлээ.", true);
      this.saving = false;
    }, () => {
      this.sb.snackbarService.open("Шинэчлэхэд алдаа гарлаа.");
      this.saving = false;
    });
  }

}
