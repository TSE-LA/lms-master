import {Component, OnInit, ViewChild} from '@angular/core';
import {ExamSandboxService} from "../../services/exam-sandbox.service";
import {ActivatedRoute, NavigationExtras} from "@angular/router";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {ConfirmDialogComponent} from "../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import {LearnerQuestionModel} from "../../model/exam.model";
import {TakeExamComponent} from "../../../../../../shared/src/lib/take-exam/take-exam.component";

@Component({
  selector: 'jrs-take-exam-container',
  template: `
    <jrs-take-exam
      #takeExam
      [questions]="questions"
      [title]="title"
      [loading]="loading"
      [remainingTime]="remainingTime"
      [durationInSeconds]="durationInSeconds"
      (timeOut)="timeOut($event)"
      (updateExam)="update($event)"
      (endExam)="endExam($event)">
    </jrs-take-exam>
    <jrs-page-loader [show]="initiating"></jrs-page-loader>
  `
})
export class TakeExamContainerComponent implements OnInit {
  loading: boolean;
  initiating: boolean;
  title: string;
  examId: string;
  remainingTime: number;
  durationInSeconds: number;
  questions: LearnerQuestionModel[] = [];
  startTime: Date;
  currentQuestions: LearnerQuestionModel[];

  @ViewChild('takeExam') takeExam: TakeExamComponent;

  constructor(private sb: ExamSandboxService, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.startTime = new Date();
    this.route.paramMap.subscribe(params => {
      this.examId = params.get('id');
      this.initiating = true;
      this.sb.startExam(this.examId).subscribe(res => {
        this.title = res.title;
        this.remainingTime = res.remainingTime;
        this.durationInSeconds = res.durationInSeconds;
        this.questions = res.questions;
        this.initiating = false;
      }, () => {
        const config = new DialogConfig();
        config.title = "Алдаатай шалгалт";
        config.data = {info: "Алдаатай шалгалт байна багшаар шалгуулна уу."}
        config.decline = false;
        config.submitButton = "Буцах";
        this.sb.openDialog(ConfirmDialogComponent, config).afterClosed.subscribe(res => {
          if (res) {
            this.navigateToLaunch();
          }
        });
        this.initiating = false;
      });
    });
  }

  endExam(event): void {
    const config = new DialogConfig();
    config.outsideClick = true;
    config.title = "ШАЛГАЛТЫГ ДУУСГАХДАА ИТГЭЛТЭЙ БАЙНА УУ?";
    config.data = {
      info: (`Шалгалтыг дуусгаснаар дахин үргэлжлүүлэх боломжгүй болохыг анхаарна уу.`)
    };

    const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
    dialogRef.afterClosed.subscribe(result => {
      if (result) {
        this.loading = true;
        this.takeExam.clearInterval();
        this.sb.finishExam(this.examId).subscribe(res => {
          this.loading = false;
          const url = '/exam/launch/' + this.examId;
          const data: NavigationExtras = {
            relativeTo: this.route,
            queryParams: {
              from: event
            }
          };
          this.sb.navigate(url, data);
        }, () => {
          this.loading = false;
          const url = '/exam/launch/' + this.examId;
          const data: NavigationExtras = {
            relativeTo: this.route,
            queryParams: {
              from: event
            }
          };
          this.sb.navigate(url, data);
        });
      }
    });
  }

  timeOut(event): void {
    this.loading = true;
    this.sb.finishExam(this.examId).subscribe(res => {
      this.loading = false;
      this.timeOutDialog();
    }, () => {
      this.loading = false;
      this.timeOutDialog();
    });
  }

  timeOutDialog(): void {
    const config = new DialogConfig();
    config.outsideClick = true;
    config.decline = false;
    config.title = "ШАЛГАЛТ ҮРГЭЛЖЛЭХ ХУГАЦАА ДУУССАН БАЙНА.";
    config.data = {
      info: (`Шалгалт үргэлжлэх хугацаа дууссан тул өмнөх хуудасруу буцах гэж байна.`)
    };

    const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
    dialogRef.afterClosed.subscribe(result => {
      if (result) {
        this.navigateToLaunch();
      }
    });
  }

  update(event: LearnerQuestionModel[]): void {
    this.currentQuestions = event;
    const spentTime = (new Date().getTime() - this.startTime.getTime()) / 1000;
    this.sb.updateLearnerExam(this.examId, event, spentTime).subscribe(() => {
    }, () => this.sb.snackbarOpen("Интернет холболтоо шалгана уу !", false));
    this.startTime = new Date();
  }

  private navigateToLaunch(): void {
    const url = '/exam/launch/' + this.examId;
    const data: NavigationExtras = {
      relativeTo: this.route,
      queryParams: {
        from: 'take-exam'
      }
    };
    this.sb.navigate(url, data);
  }
}
