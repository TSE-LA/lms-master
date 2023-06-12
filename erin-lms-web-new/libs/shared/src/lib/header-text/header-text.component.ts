import {Component, Input} from "@angular/core";

@Component({
  selector: 'jrs-header-text',
  template: `
    <div class="header-text" [class]="size" [class.bold]="bold" *ngIf="!load" [class.margin]="margin">
      <ng-content></ng-content>
    </div>
    <div class="loader" *ngIf="load"></div>
  `,
  styleUrls: ['./header-text.component.scss']
})
export class HeaderTextComponent {
  @Input() load: boolean;
  @Input() size = 'medium';
  @Input() bold = true;
  @Input() margin = true;
}
