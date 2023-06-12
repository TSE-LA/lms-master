import {Component, Input, OnChanges, ViewChild} from "@angular/core";
import { MatDrawer } from "@angular/material/sidenav";
import {CourseProperties} from "../../../../../core/src/lib/classroom-course/model/classroom-course.model";

@Component({
  selector: 'jrs-calendar-sidenav',
  template: `
    <mat-drawer-container *ngIf="data"
                          [class.drawer-opened]="drawer.opened"
                          [class.overlay-style]="drawer.opened"
                          class="example-container">
      <mat-drawer #drawer class="drawer" mode="overlay"
                  opened="true">
        <div class="close">
          <mat-icon class="close"
                    (click)="drawer.close()">
            close
          </mat-icon>
        </div>
        <div class="header">
          <div *ngIf="data.courseStateColor" class="state" [ngStyle]="{'background': data.courseStateColor}"></div>
          <div class="title width-full">
            <div class="flex">
              <div class="mb-10 capitalize-text">{{data.courseState ? data.courseState : ''}}</div>
              <div class="spacer"></div>
              <div class="course-color" [ngStyle]="{'background': data.courseTypeColor}"></div>
              <div>{{data.courseType && (data.courseType === 'online-course') ? 'Цахим сургалт' : 'Танхимын сургалт'}}</div>
            </div>
            <jrs-header-text [margin]="false"
                             [size]="'small'">
              {{data.courseName ? data.courseName : ''}}
            </jrs-header-text>
          </div>
        </div>
        <jrs-divider></jrs-divider>
        <div class="flex">
          <div class="date flex">
            <mat-icon class="icon">event_available</mat-icon>
            <div class="lh-1">{{data.date ? data.date : ''}}</div>
          </div>
          <div class="spacer"></div>
          <div *ngIf="data.courseType !== 'online-course'" class="time flex">
            <mat-icon class="icon">schedule</mat-icon>
            <div class="lh-1">{{data.time ? data.time : ''}}</div>
          </div>
        </div>
        <div *ngIf="data.courseType === 'classroom-course'" class="flex mt-20">
          <mat-icon class="icon">location_on</mat-icon>
          <div class="lh-1">{{data.location ? data.location : ''}}</div>
        </div>

        <div class="label mt-30 mb-10">
          Зохион байгуулагч:
        </div>
        <jrs-header-text [margin]="false"
                         [size]="'small'">
          {{data.organizerName}}
        </jrs-header-text>

        <div class="label mt-30 mb-10">
          Сургалтын ангилал:
        </div>
        <jrs-header-text [margin]="false"
                         [size]="'small'">
          {{data.courseCategory}}
        </jrs-header-text>

        <div class="flex mt-30 mb-10">
          <div class="label">Багш:</div>
          <div class="spacer"></div>
          <div *ngIf="data.courseType !== 'online-course'" class="label">
          Суралцагчийн тоо:</div>
        </div>

        <div class="flex">
          <jrs-header-text [margin]="false"
                           [size]="'small'">
            {{data.moderator}}
          </jrs-header-text>
          <div class="spacer"></div>
          <jrs-header-text *ngIf="data.courseType !== 'online-course'"
            [margin]="false"
            [size]="'small'"
            class="mr-10">
            {{data.count}}
          </jrs-header-text>
        </div>
<!--        <div *ngIf="data.courseType == 'classroom-course'">-->
<!--          <jrs-divider></jrs-divider>-->
<!--          <jrs-header-text [margin]="false"-->
<!--                           [size]="'small'">-->
<!--            Хойшлуулсан шалтгаан:-->
<!--          </jrs-header-text>-->
<!--          <div class="label mt-10">Сургалт болох газрын цагийн-->
<!--            хуваарь өөрчлөгдсөнтэй холбоотой сургалтыг 3 хоногоор хойшлууллаа.-->
<!--          </div>-->
<!--        </div>-->

<!--        <div *ngIf="data.attachment">-->
<!--          <jrs-divider></jrs-divider>-->
<!--          <jrs-header-text [margin]="false"-->
<!--                           [size]="'small'">-->
<!--            Хавсралт:-->
<!--          </jrs-header-text>-->
<!--          <div class="link mt-10">-->
<!--            Хавсралтаар оруулсан файл.pdf-->
<!--          </div>-->
<!--        </div>-->

<!--        <div class="btn-style">-->
<!--          <div>-->
<!--            <jrs-button [color]="'primary'" [size]="'medium'">-->
<!--              Үнэлгээний хуудас-->
<!--            </jrs-button>-->
<!--          </div>-->
<!--        </div>-->
      </mat-drawer>
    </mat-drawer-container>
  `,
  styleUrls: ['./calendar-sidenav.component.scss']
})
export class CalendarSidenavComponent implements OnChanges {
  @ViewChild('drawer') public drawer: MatDrawer;
  @Input() drawerToggle: boolean;
  @Input() data: CourseProperties;

  ngOnChanges() {
    if (this.drawerToggle) {
      this.drawer.toggle();
    }
  }
}
