import {Component, OnInit} from "@angular/core";
import {DialogConfig} from "../dialog-config";
import {DialogRef} from "../dialog-ref";

@Component({
  selector: 'file-view-dialog',
  template: `
  <div>
    <jrs-file-viewer
      [source]="this.source"
      [fileName]="this.courseName"
      [courseId]="this.courseId"
      [learnerId]="this.learnerId">
    </jrs-file-viewer>
  </div>`,
})
export class FileViewDialogComponent implements OnInit {
  source: string;
  courseName: string;
  courseId: string;
  learnerId: string;
  errorText : string;

  constructor(public config: DialogConfig, public dialog: DialogRef) {
    this.source = config.data.source;
    this.courseName = config.data.courseName;
    this.courseId = config.data.courseId;
    this.learnerId = config.data.learnerId;
    this.errorText = config.data.errorText;
  }
  ngOnInit() {
  }
}
