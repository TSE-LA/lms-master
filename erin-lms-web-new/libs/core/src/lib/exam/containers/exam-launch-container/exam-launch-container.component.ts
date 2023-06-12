import {Component, OnInit} from '@angular/core';
import {COMPLETED_EXAM_ROW, EXAM_PROPERTIES_ROW} from "../../../../../../shared/src/lib/exam-launch/constants";
import {ExamDetail} from "../../../../../../shared/src/lib/exam-launch/model";
import {ExamLaunchModel} from "../../model/exam.model";
import {ExamSandboxService} from "../../services/exam-sandbox.service";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {ConfirmDialogComponent} from "../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import {ActivatedRoute, ActivationEnd, Router} from "@angular/router";

@Component({
  selector: 'jrs-exam-launch-container',
  template: `
    <jrs-button
      [iconName]="'arrow_back_ios'"
      [iconColor]="'secondary'"
      [noOutline]="true"
      [isMaterial]="true"
      [size]="'medium'"
      [bold]="true"
      [textColor]="'text-link'"
      (clicked)="back()">БУЦАХ
    </jrs-button>
    <jrs-exam-launch
      [status]="status"
      [title]="title"
      [celebrate]="celebrate"
      [description]="description"
      [examDetail]="examDetail"
      [certificateId]="certificateId"
      [isOngoing]="isOngoing"
      [loading]="loading"
      [maxAttempt]="maxAttempt"
      [remainingAttempt]="remainingAttempt"
      [completedExamDetail]="completedExamDetail"
      (start)="startExam($event)">
    </jrs-exam-launch>
  `,
  styleUrls: ['./exam-launch-container.component.scss']
})
export class ExamLaunchContainerComponent implements OnInit {
  examId: string;
  title: string;
  description: string;
  status: string;
  isOngoing: boolean;
  exam: ExamLaunchModel;
  celebrate: boolean;
  loading: boolean;
  remainingAttempt: number;
  maxAttempt: number;
  examDetail: ExamDetail = {
    keys: EXAM_PROPERTIES_ROW,
    values: [],
  };
  completedExamDetail: ExamDetail = {
    keys: COMPLETED_EXAM_ROW,
    values: [],
  };
  certificateId: string;

  constructor(private sb: ExamSandboxService, private route: ActivatedRoute, private router: Router) {
    this.route.paramMap.subscribe(params => {
      this.examId = params.get('id');
    })
    this.router.events.subscribe((event: ActivationEnd) => {
      if (event.snapshot) {
        this.celebrate = event.snapshot.queryParams.from != null;
      }
    });
  }

  ngOnInit(): void {
    this.loadPage();
  }

  loadPage(): void {
    this.loading = true;
    this.sb.getExamLaunchData(this.examId).subscribe(res => {
      this.exam = res;
      this.title = res.title;
      this.description = res.description;
      this.status = res.status;
      this.isOngoing = res.ongoing;
      this.maxAttempt = res.maxAttempt;
      this.remainingAttempt = res.remainingAttempt;
      this.examDetail.values = [
        this.exam.author,
        this.exam.maxScore,
        this.exam.thresholdScore,
        this.exam.maxAttempt,
        this.exam.duration
      ];
      this.completedExamDetail.values = [
        this.exam.score,
        this.exam.scorePercentage,
        this.exam.remainingAttempt,
        this.exam.spentTime];
      this.certificateId = this.exam.certificateId;
      this.loading = false;
    }, () => this.loading = false);
  }

  back(): void {
    this.sb.navigateByUrl('/exam/container/examinee-list');
  }

  startExam(ongoing): void {
    this.sb.checkActiveExam().subscribe(res => {
      if (res != null && !ongoing) {
        const config = new DialogConfig();
        config.outsideClick = true;
        config.title = '"' + res.examName + '" - ' + " нэртэй шалгалт үргэлжилж байна !";
        config.decline = false;
        config.data = {
          info: ('Үргэлжилж буй шалгалтыг дуусгасны дараа шинээр шалгалт эхлэх боломжтой.')
        };
        const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
        dialogRef.afterClosed.subscribe(result => {
          if (result) {
            return;
          }
        });
      } else {
        this.continueStart(ongoing);
      }
    }, () => {
      return;
    })

  }

  continueStart(ongoing): void {
    if (ongoing) {
      this.sb.navigateByUrl('/exam/take/' + this.examId);
    } else {
      const config = new DialogConfig();
      config.outsideClick = true;
      config.title = "ТА ШАЛГАЛТ ЭХЛҮҮЛЭХДЭЭ ИТГЭЛТЭЙ БАЙНА УУ?";
      config.data = {
        info: (`Шалгалт эхэлсэн тохиолдолд хугацаа явж эхлэх ба буцаах боломжгүйг анхаарна уу!`)
      };

      const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
      dialogRef.afterClosed.subscribe(result => {
        if (result) {
          this.sb.navigateByUrl('/exam/take/' + this.examId);
        }
      });
    }
  }
}
