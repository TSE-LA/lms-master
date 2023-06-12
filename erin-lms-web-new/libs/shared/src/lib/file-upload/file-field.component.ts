import {Component, Input, Output, EventEmitter, OnChanges, SimpleChanges} from "@angular/core";
import {FormControl, FormGroup} from "@angular/forms";
import {SnackbarService} from "../snackbar/snackbar.service";

@Component({
  selector: 'jrs-file-field',
  template: `
    <div *ngIf="!load" class="file-field-container">
      <label *ngIf="label"  class="{{labelStyleName}}">
        {{label}}
        <span *ngIf="required" class="required-star-style">*</span>
      </label>
      <div [formGroup]="formGroup" class="file-field-style">
        <input class="readonly-field" [formControlName]="'inputName'" type="text" readonly>
        <input #fileInput hidden [formControlName]="file"
               [accept]="acceptFileTypes"
               [placeholder]="''"
               type="file" (change)="inputChanged($event)">
        <span class="spacer"></span>
        <jrs-button class="icon-btn margin-right"
                    [jrsTooltip]="tooltip"
                    [color]="'transparent'"
                    [iconName]="iconName"
                    [iconColor]="'gray'"
                    [isMaterial]="true"
                    [size]="'transparent-small'"
                    (click)="$event.preventDefault();fileInput.click()">
        </jrs-button>
      </div>
    </div>

    <div class="loading" *ngIf="load">
      <div class="shimmer"></div>
    </div>
  `,
  styleUrls: ['./file-field.component.scss']
})
export class FileFieldComponent implements OnChanges {
  @Input() formGroup = new FormGroup({
    inputFile: new FormControl(),
    inputName: new FormControl()
  });
  @Input() file = "inputFile";
  @Input() fileName = "inputName";
  @Input() iconName = "cloud_download";
  @Input() load: boolean;
  @Input() acceptFileTypes = "*";
  @Input() acceptRegex: RegExp;
  @Input() tooltip = "Файл хавсаргах";
  @Input() label = "";
  @Input() labelStyleName = "";
  @Input() required: boolean;
  @Output() selectionChange = new EventEmitter<File>();


  constructor(private snackbar: SnackbarService) {

  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const change in changes) {
      if (change === "fileName") {
        this.formGroup.controls.inputName.setValue(this.fileName);
      }
    }
  }

  inputChanged(item: any): void {
    const file = item.target.files[0];
    if (!this.acceptRegex) {
      this.emitUpload(file);
    } else if (this.acceptRegex.exec(file.name)) {
      this.emitUpload(file);
    } else {
      this.snackbar.open(this.acceptFileTypes + " өргөтгөлтэй файл оруулна уу.");
    }
  }

  private emitUpload(file): void {
    this.formGroup.controls.inputName.setValue(file.name);
    this.selectionChange.emit(file);
  }
}
