import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'jrs-info-panel',
  template: `
    <div class="wrapper" [style.width]="width">
      <jrs-icon class="icon" [mat]="true" [color]="'secondary'">info</jrs-icon>
      <span>{{text}}</span>
    </div>`,
  styleUrls: ['./info-panel.component.scss']
})
export class InfoPanelComponent implements OnInit {
  @Input() text: string;
  @Input() width = "63vw";

  constructor() {
  }

  ngOnInit(): void {
  }

}
