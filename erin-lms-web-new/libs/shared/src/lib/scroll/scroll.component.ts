import {Component, Input, OnInit} from "@angular/core";

@Component({
  selector: 'jrs-scroll',
  template: `
    <div class="scroll-style" [ngClass]="[size, color]" [ngStyle]="{height: height}" [class.scroll-x]="horizontalScrollEnabled">
      <ng-content>
      </ng-content>
    </div>
  `,
  styleUrls: ['./scroll.component.scss']
})
export class ScrollComponent implements OnInit {
  @Input() color = 'secondary';
  @Input() size = 'medium'
  @Input() height = '100px';
  @Input() horizontalScrollEnabled = false;
  constructor() {
  }
  ngOnInit() {
  }
}
