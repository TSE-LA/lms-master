import {Component, Input, OnInit, SimpleChanges} from '@angular/core';
import {CourseProgress} from "../../common.model";
import {FileAttachment} from "../../../../../../shared/src/lib/structures/content-structure/content-structure.model";

@Component({
  selector: 'jrs-course-progress',
  template: `
    <button *ngIf="progressData" class="chapters inline-flex" [class.clicked]='selectedChapter' [class.disabled]="!this.unLocked"
            [class.padding]="progressData.progress == 100 || unLocked || !unLocked">
      <jrs-header-text class="chapters-title" [size]="'small'" [margin]="false">
        <div class="inline-flex" [class.disabled]="!this.unLocked">
          <jrs-icon class="icon material-icons"
                    *ngIf="!noProgress && showCheckMark || progressData.progress == 100 && !this.isQuestionnaire(progressData.moduleName)"
                    [size]="'chapters-medium'"
                    [mat]="true"
                    [color]="'primary'">
            check_circle
          </jrs-icon>
          <jrs-icon class="icon material-icons"
                    *ngIf="noProgress && !showCheckMark && !unLocked"
                    [size]="'chapters-medium'"
                    [mat]="true"
                    [color]="'warn'">
            lock
          </jrs-icon>
          <jrs-icon class="icon material-icons"
                    *ngIf="unLocked && noProgress"
                    [size]="'chapters-medium'"
                    [mat]="true"
                    [color]="'primary'">
            lock_open
          </jrs-icon>
          <span id="progress" *ngIf="!noProgress && progressData.progress != 100">
            {{progressData.progress | number: '1.'}}%
          </span>
          <div class="text-style">
            {{(this.chapterIndex + 1 + '.')}}&nbsp;
            {{progressData.moduleName}}
          </div>
        </div>

      </jrs-header-text>
      <span class="spacer"></span>
      <jrs-icon class="icon material-icons icon-padding"
                *ngIf="progressData.sections ? progressData.sections.length>0 : false"
                (click)="showOptions = !showOptions"
                [class.open]="showOptions"
                [size]="'medium-large'"
                [color]="'purple-gray'"
                [mat]="true">
        expand_more
      </jrs-icon>
    </button>
    <ul *ngIf="showOptions" class="progress-content">
      <li class="dropdown-values" *ngFor="let value of progressData.sections; let i = index">
        <span>{{(this.chapterIndex + 1 + '.') + (i + 1 + '. ')}}{{value.name}}</span>
      </li>
    </ul>

    <div *ngIf="this.currentFile" class="file-title">
      <a>{{this.chapterIndex + 1 + '.'}}{{this.currentFile.name}}</a>
    </div>

    <div *ngIf="this.additionalFile" class="file-title">
      <a>{{this.chapterIndex + 1 + '.'}}{{this.additionalFile.name}}</a>
    </div>
  `,
  styleUrls: ['./course-progress.component.scss']
})
export class CourseProgressComponent implements OnInit {

  @Input() context: boolean = false;
  @Input() progressData: CourseProgress;
  @Input() chapterIndex: number;
  @Input() noProgress: boolean;
  @Input() showCheckMark: boolean;
  @Input() unLocked: boolean;
  @Input() selectedChapter: boolean;
  @Input() isPermission: boolean;
  @Input() currentFile: FileAttachment;
  @Input() additionalFile: FileAttachment;
  showOptions: boolean;

  offsets = {offsetWidth: 0, offsetHeight: 0};
  transform = {x: '0', y: '0', clX: '0', clY: '0'};
  selectedElement: any;

  constructor() {
  }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges): void {
  }

  isQuestionnaire(moduleName: string): boolean {
    return moduleName === 'АСУУЛГА';
  }

}
