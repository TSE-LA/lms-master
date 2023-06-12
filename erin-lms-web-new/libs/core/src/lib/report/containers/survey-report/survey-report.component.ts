import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';
import {ReportSandboxService} from "../../services/report-sandbox.service";
import {Survey} from "../../../survey/model/survey.model";
import {CourseShortModel, QuestionTypes} from "../../../../../../shared/src/lib/shared-model";
import {Subscription} from "rxjs";
import {SurveyReport} from "../../model/report.model";
import {ChartDataModel} from "../../../../../../shared/src/lib/chart/chart.model";
import {ChartType} from "chart.js";

@Component({
  selector: 'jrs-survey-report',
  template: `
    <div class="container">
      <div class="header">
        <div class="flex">
          <jrs-header-text *ngIf="selectedSurvey">{{selectedSurvey.name}}</jrs-header-text>
          <span class="total-responses">{{'Нийт бөглөсөн: ' + totalResponses}}</span>
          <div class="spacer"></div>
          <jrs-dropdown-view
            class="margin-left"
            [label]="'Үнэлгээ'"
            [chooseFirst]="true"
            [outlined]="true"
            [size]="'medium'"
            [icon]="'expand_more'"
            [width]="'180px'"
            [tooltip]="true"
            [values]="surveys"
            [hasSuffix]="true"
            (selectedValue)="surveySelect($event)">
          </jrs-dropdown-view>
          <jrs-dropdown-view
            class="margin-left"
            [label]="'Сургалт'"
            [chooseFirst]="true"
            [outlined]="true"
            [size]="'medium'"
            [icon]="'expand_more'"
            [width]="'180px'"
            [tooltip]="true"
            [values]="courses"
            [hasSuffix]="true"
            (selectedValue)="courseSelect($event)">
          </jrs-dropdown-view>
        </div>
      </div>
      <div *ngFor="let chartData of chartDataSet">
        <div class="question">
          <div *ngIf="chartData.chartType != null">
            <jrs-chart
              [hasLegend]="true"
              [loading]="loading"
              [totalResponses]="totalResponses"
              [suffixText]="suffixText"
              [chartType]="chartData.chartType"
              [dataSet]="chartData">
            </jrs-chart>
          </div>
          <div *ngIf="chartData.chartType == null">
            <jrs-fill-question-result [data]="chartData"></jrs-fill-question-result>
          </div>
        </div>
      </div>
      <jrs-page-loader [show]="loading"></jrs-page-loader>
    </div>
  `,
  styleUrls: ['./survey-report.component.scss']
})
export class SurveyReportComponent implements OnInit, AfterViewInit, OnDestroy {
  surveys: Survey[] = [];
  courses: CourseShortModel[] = [{name: 'БҮГД', id: 'all'}];
  selectedCourse: CourseShortModel;
  selectedSurvey: Survey;
  startDateSubscription: Subscription;
  endDateSubscription: Subscription;
  chartDataSet: ChartDataModel[] = [];
  startDate: string;
  suffixText = '-р асуулт';
  endDate: string;
  loading: boolean;
  questions: SurveyReport[] = [];
  totalResponses = 0;

  constructor(private sb: ReportSandboxService) {
  }

  ngOnInit(): void {
    this.loadPage();
  }

  ngAfterViewInit(): void {
    this.startDateSubscription = this.sb.getStartDate().subscribe(start => {
      this.startDate = start;
      this.getSurveyReportData();
    });
    this.endDateSubscription = this.sb.getEndDate().subscribe(end => {
      this.endDate = end;
      this.getSurveyReportData();
    });
  }

  ngOnDestroy(): void {
    this.startDateSubscription.unsubscribe();
    this.endDateSubscription.unsubscribe();
  }


  getSurveyReportData(): void {
    if (this.selectedSurvey != null && this.startDate != null && this.endDate != null) {
      this.sb.getSurveyReport(this.selectedSurvey.id, this.startDate, this.endDate,
        this.selectedCourse != null ? this.selectedCourse.id : null).subscribe(
        (report: SurveyReport[]) => {
          this.getTotalResponses(report[0]);
          const charts: ChartDataModel[] = [];
          let index = 1;
          report.forEach(data => {
            const labels = [];
            const chartData = [];
            data.answers.forEach(answer => {
              labels.push(answer.value);
              chartData.push(answer.count);
            })
            charts.push({
              labels: labels,
              data: chartData,
              chartType: this.getChartType(data.questionType),
              index: index,
              text: data.question
            });
            index++;
          })
          this.chartDataSet = charts;
          this.loading = false;
        }, () => {
          this.loading = false;
          this.sb.snackbarOpen("Тайлангийн мэдээлэл авахад алдаа гарлаа");
        })
    }
  }

  getTotalResponses(report: SurveyReport): void {
    this.totalResponses = 0;
    if (report.questionType === 'FILL_IN_BLANK') {
      this.totalResponses += report.answers.length;
    } else {
      report.answers.forEach(res => this.totalResponses += res.count);
    }
  }


  getChartType(questionType: string): ChartType {
    switch (questionType) {
      case QuestionTypes.SINGLE_CHOICE:
        return 'pie';
      case QuestionTypes.MULTIPLE_CHOICE:
        return 'bar';
      case QuestionTypes.FILL_IN_BLANK:
        return null
    }
  }

  courseSelect(selectedCourse): void {
    this.loading = true;
    this.selectedCourse = selectedCourse;
    this.getSurveyReportData();
  }

  loadPage(): void {
    this.loading = true;
    const load = new Promise(resolve => {
      this.sb.getSurveys(true).subscribe((res: any) => {
        this.surveys = res;
        this.selectedSurvey = this.surveys[0];
        this.getCourseWithSurvey();
        resolve(true);
      }, () => {
        this.loading = false;
        this.sb.snackbarOpen("Үнэлгээний мэдээлэл авахад алдаа гарлаа");
      })
    })
    load.then(() => this.getSurveyReportData());
  }

  getCurrentSurveyId(): string {
    return this.selectedSurvey ? this.selectedSurvey.id : this.surveys[0].id;
  }

  getCourseWithSurvey(): void {
    this.loading = true;
    this.sb.getCoursesWithSurvey(this.selectedSurvey.id).subscribe((courses: CourseShortModel[]) => {
      this.courses = this.courses.concat(courses);
      this.getSurveyReportData();
    }, () => {
      this.sb.snackbarOpen("Сургалтын мэдээлэл авахад алдаа гарлаа");
      this.loading = false;
    })
  }

  surveySelect(survey: Survey): void {
    this.selectedSurvey = survey;
    this.getCourseWithSurvey();
  }
}

