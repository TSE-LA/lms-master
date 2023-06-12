import {Component, Input} from "@angular/core";

@Component({
  selector: 'jrs-tab',
  template: `
    <div [hidden]="!active" class="tab">
      <ng-content></ng-content>
    </div>
  `,
})

export class TabComponent {
  @Input() label: string;
  @Input() active = false;

  constructor() {
  }
}
