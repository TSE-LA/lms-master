import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'jrs-image-attach-button',
  template: `
    <input #fileInput hidden
           type="file"
           value=""
           accept="image/jpeg, image/png"
           (change)="inputChanged($event)">
    <div class="flex-center float-left">
      <span class="spacer"></span>
      <jrs-circle-button class="icon-btn margin-right"
                         [iconName]="'image'"
                         [color]="'none'"
                         [iconColor]="'primary'"
                         [isMaterial]="true"
                         [size]="'medium'"
                         [disabled]="load"
                         (click)="$event.preventDefault();fileInput.click()">
      </jrs-circle-button>
      <jrs-label [text]="uploadedFileName"></jrs-label>
      <jrs-loader *ngIf="load" [color]="'primary'"></jrs-loader>
      <jrs-circle-button
        *ngIf="uploadedFileName"
        [size]="'medium'"
        [isMaterial]="true"
        [iconName]="'close'"
        [color]="'none'"
        [iconColor]="'warn'"
        [disabled]="load"
        (click)="clearFile()">
      </jrs-circle-button>
    </div>
  `,
  styleUrls: ['./image-attach-button.component.scss']
})
export class ImageAttachButtonComponent {
  @Input() load: boolean;
  @Input() uploadedFileName: string;
  @Output() selectedFile = new EventEmitter<File>();

  inputChanged(item: any): void {
    this.uploadedFileName = item.target.files[0].name;
    this.selectedFile.emit(item.target.files[0]);
    item.target.value = '';
  }

  clearFile(): void {
    this.uploadedFileName = "";
    this.selectedFile.emit(null);
  }
}
