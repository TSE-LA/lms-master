import {Component, Input, OnChanges, SimpleChanges, EventEmitter, Output} from '@angular/core';
import {DropdownModel} from "../dropdown/dropdownModel";
import {OnlineCourseModel} from "../../../../core/src/lib/online-course/models/online-course.model";

@Component({
  selector: 'jrs-card',
  template: `
    <div>
      <div class="container-a" (mouseenter)="hoverIdx = 1" (mouseleave)="hoverIdx = -1">
        <div class="card" [class]="[size]">
          <div class="image" [ngStyle]="{'background-image':
                           ' url(' + course.thumbnailUrl + '), ' +
                            'url(' + this.defaultThumbnailUrl + ')' }">
            <div [ngStyle]="{'background-image':
                           ' url(' + getGif(course.thumbnailUrl) + ')'}"
                 [ngClass]="{ 'thumbnail-gif': true, 'hide': hoverIdx !== 1 }">
            </div>
            <div class="flex footer">
              <div *ngIf="hasNotification && !course.progress" class="state"><span><b>{{state}}</b></span></div>
              <span class="spacer"></span>
              <jrs-menu *ngIf="hasMenu" [contextActions]="contextValues"
                        (toggleMenu)="contextMenuEvent($event)"
                        [context]="false"
                        (selectedAction)="onContextMenuClick($event)">
              </jrs-menu>
            </div>
          </div>
          <div class="container">
            <div class="dark"><span><b>{{course.name}}</b></span></div>
            <div class="flex sub-title"><span>{{course.author}}</span><span class="spacer"></span>
              <span>{{course.createdDate}}</span></div>
          </div>
          <div style="border-top: 1px solid gainsboro"></div>
          <div class="flex footer">
            <a class="card-button" (click)="onClick($event)" [attr.data-test-id]="'course-see-button'">
              {{buttonText}}
            </a>
            <span class="spacer"></span>
            <div class="check-cirlce">
              <jrs-icon *ngIf="course.progress === 100"
                        [size]="'large'"
                        [mat]="true"
                        [color]="'primary'">
                check_circle
              </jrs-icon>
            </div>
            <jrs-progress *ngIf="course.progress < 100 && hasProgress"
                          [progress]="course.progress">
            </jrs-progress>
          </div>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./card.component.scss']
})
export class CardComponent implements OnChanges {
  @Input() contextValues: any[] = []
  @Input() state: string;
  @Input() rates: number;
  @Input() buttonText: string;
  @Input() defaultThumbnailUrl: string;
  @Input() rating = 0;
  @Input() per: string;
  @Input() repeat = 1;
  @Input() hasMenu: boolean;
  @Input() hasProgress: boolean;
  @Input() hasNotification: boolean;
  @Input() size = 'medium';
  @Input() course: OnlineCourseModel;
  @Output() contextMenuClicked = new EventEmitter<DropdownModel>();
  @Output() contextMenuTriggered = new EventEmitter<OnlineCourseModel>()
  @Output() clicked = new EventEmitter<Event>();

  hoverIdx = -1;
  numbers = Array<number>();

  ngOnChanges(changes: SimpleChanges): void {
    this.numbers = Array(this.repeat).fill(4);
  }

  getGif(thumbnailUrl: string): string {
    return thumbnailUrl.split(".")[0].concat(".gif");
  }

  contextMenuEvent(open: boolean): void {
    if (open) {
      this.contextMenuTriggered.emit(this.course)
    }
  }

  onContextMenuClick(action: DropdownModel): void {
    if (action) {
      this.contextMenuClicked.emit(action)
    }
  }

  onClick(e): void{
    this.clicked.emit(e);
  }
}
