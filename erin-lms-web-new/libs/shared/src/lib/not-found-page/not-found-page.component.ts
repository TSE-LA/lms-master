import {Component, Input} from '@angular/core';

@Component({
  selector: 'jrs-not-found-page',
  template: `
    <div class="not-found" *ngIf="show" [ngClass]="size">
      <div class="not-found-container">
        <div>
          <img alt="not-found"  src="{{imageName}}">
        </div>
        <div class="margin-top">
          <span class="not-found-text margin-top">{{text}}</span>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./not-found-page.component.scss']
})
export class NotFoundPageComponent{
  @Input() imageName = 'assets/images/file-not-found.png';
  @Input() show = false;
  @Input() text:string;
  @Input() size = 'medium'
}
