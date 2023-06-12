import {Component, OnInit} from '@angular/core';
import {ExamSandboxService} from "../../services/exam-sandbox.service";
import {EXAM_REPORT_TABLE_COLUMN} from "../../model/exam.constants";
import {ExamBank, ExamReportModel} from "../../model/exam.model";
import {finalize} from "rxjs/operators";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'jrs-exam-statistics',
  template: `
    <jrs-section [maxWidth]="'72vw'" [width]="'100%'" [height]="'72vh'" [background]="'section-background-secondary'">
      <div class="inline-flex">
        <jrs-dropdown-view
          [defaultValue]="selectedExamName"
          [values]="allExams"
          (selectedValue)="changeCurrentExam($event)"
          [label]="'Шалгалтууд'"
          [color]="'light'"
          [width]="'400px'"
          [outlined]="true"
          [load]="loading"
          [tooltip]="true"
          [size]="'medium'">
        </jrs-dropdown-view>
      </div>
      <jrs-dynamic-table
        [loading]="loading"
        [dataSource]="data"
        [tableColumns]="columns"
        [tableMinHeight]="'61vh'"
        [notFoundText]="notFountText"
        (rowAction)="downloadAnswers($event)">
      </jrs-dynamic-table>
    </jrs-section>
    <jrs-page-loader [show]="pageLoading"></jrs-page-loader>
  `
})
export class ExamStatisticsComponent implements OnInit {
  loading: boolean;
  data: ExamReportModel[] = [];
  columns = EXAM_REPORT_TABLE_COLUMN;
  notFountText = 'Шалгалтын статистик тайлан олдсонгүй';
  allExams: ExamBank[] = [];
  selectedExamName: string;
  selectedExam: ExamBank;
  filter = new Map();
  pageLoading: boolean;
  examId: string;


  constructor(private sb: ExamSandboxService, private route: ActivatedRoute) {
  }


  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.examId = id;
      }
    })
    this.loadPage();
  }

  // TODO: Use here getExamsForBank instead of getAllExamsForBank
  loadPage(): void {
    this.loading = true;
    this.sb.getExamsForBank().subscribe(res => {
      this.allExams = res;
      if (this.examId) {
        this.selectedExam = this.allExams.find(exam => exam.id === this.examId) ?
          this.allExams.find(exam => exam.id === this.examId) : this.allExams[0];
      } else {
        this.selectedExam = this.allExams[0];
      }
      this.selectedExamName = (this.selectedExam != null) ? this.selectedExam.name : null;
      this.changeCurrentExam(this.selectedExam);
    }, () => {
      this.loading = false;
    });
  }

  changeCurrentExam(exam: ExamBank): void {
    this.selectedExam = exam;
    if (exam) {
      this.sb.getExamStatistics(exam.id).subscribe((res) => {
        if (res) {
          this.data = res.map<ExamReportModel>(item => {
              if (item.learnerPassStatus != null) {
                return ({
                  learnerName: item.learnerName,
                  score: this.validateAndFormat(item.learnerFinalScore, item.examMaxScore), //learnerScore/examMaxScore
                  examThresholdScore: item.examThresholdScore,
                  learnerSpentTime: item.learnerSpentTime,
                  attemptCount: this.validateAndFormat(item.learnerAttemptCount, item.examMaxAttemptCount), //learnerAttemptCount/examMaxAttemptCount
                  learnerGradeInPercentage: this.getValidPercentage(item.learnerGradeInPercentage) + "%",
                  learnerPassStatus: this.getStatusInText(item.learnerPassStatus)
                });
              } else { // no exam attempt has been made
                return ({
                  learnerName: item.learnerName,
                  score: `0/${item.examMaxScore}`, //learnerScore/examMaxScore
                  examThresholdScore: item.examThresholdScore,
                  learnerSpentTime: item.learnerSpentTime,
                  attemptCount: `0/${item.examMaxAttemptCount}`, //learnerAttemptCount/examMaxAttemptCount
                  learnerGradeInPercentage: "0%",
                  learnerPassStatus: this.getStatusInText(item.learnerPassStatus)
                });
              }
            }
          );
        }
        this.loading = false;
      }, () => {
        this.loading = false;
      });
    } else {
      this.loading = false;
    }
  }

  downloadAnswers(value: any): void {
    this.pageLoading = true;
    this.sb.downloadLearnerExamAnswer(this.selectedExam.id, value.data.learnerName).pipe(finalize(() => this.pageLoading = false)).subscribe(() => {
    }, error => {
      this.sb.snackbarOpen("Шалгалт өгөөгүй тул тайлан татах боломжгүй байна!")
    });
  }

  getValidPercentage(value: number): number {
    if (!value || value < 0) {
      return 0;
    }
    if (value > 100) {
      return 100;
    }
    return value;
  }

  getStatusInText(status: boolean): string {
    return status ? "Тэнцсэн" : "Тэнцээгүй";
  }

  private validateAndFormat(value: number, maxValue: number): string {
    let finalValue = this.toValidNumber(value);
    finalValue = (finalValue > maxValue) ? maxValue : finalValue;
    const finalMaxValue = this.toValidNumber(maxValue);
    return `${this.toValidNumber(finalValue)}/${this.toValidNumber(finalMaxValue)}`
  }

  private toValidNumber(value: number): number {
    return (!value || value < 0) ? 0 : value;
  }
}
