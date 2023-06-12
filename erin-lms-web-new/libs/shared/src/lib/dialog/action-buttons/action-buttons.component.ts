import { Component, EventEmitter, Input, Output } from "@angular/core";

@Component({
  selector: "jrs-action-buttons",
  template: `
    <div
      class="jrs-dialog-buttons"
      [ngStyle]="{ justifyContent: justifyContent }"
    >
      <jrs-button
        *ngIf="submit"
        (clicked)="submitButtonClick()"
        [size]="'medium'"
        [title]="submitButton"
        [color]="'primary'"
      ></jrs-button>
      <jrs-button
        *ngIf="decline"
        (clicked)="declineButtonClick()"
        [size]="'medium'"
        [title]="declineButton"
        [color]="'warn'"
      ></jrs-button>
    </div>
  `,
  styleUrls: ["./action-buttons.component.scss"],
})
export class ActionButtonsComponent {
  @Input() submitButton = "Тийм";
  @Input() declineButton = "Үгүй";
  @Input() decline = true;
  @Input() submit = true;
  @Input() justifyContent = "center";
  @Output() submitted = new EventEmitter();
  @Output() declined = new EventEmitter();

  submitButtonClick(): void {
    this.submitted.emit();
  }

  declineButtonClick(): void {
    this.declined.emit();
  }
}
