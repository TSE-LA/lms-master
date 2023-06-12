import {Component, OnInit} from '@angular/core';
import {SurveySandboxService} from "../../services/survey-sandbox.service";
import {Survey, SurveyStatus} from "../../model/survey.model";
import {
  SURVEY_ACTIONS, SURVEY_CLONE,
  SURVEY_DELETE,
  SURVEY_EDIT,
  SURVEY_NOT_FOUND_TEXT,
  TABLE_COLUMNS
} from "../../model/survey.constants";
import {DialogConfig} from "../../../../../../shared/src/lib/dialog/dialog-config";
import {ConfirmDialogComponent} from "../../../../../../shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";

@Component({
  selector: 'jrs-survey-container',
  template: `
    <jrs-section [height]="'78vh'" [maxWidth]="'76vw'" [width]="'100%'" [background]="'section-background-secondary'">
      <div class="flex justify-end margin-bottom">
        <jrs-button
          [size]="'long'"
          [title]="'Үнэлгээ нэмэх'"
          (clicked)="navigateToCreate()">
        </jrs-button>
      </div>
      <jrs-dynamic-table [tableColumns]="tableColumns"
                         [dataSource]="surveys"
                         [contextActions]="contextActions"
                         (selectedAction)="actionTriggered($event)"
                         (rowSelected)="surveySelected($event)"
                         [loading]="loading"
                         [noCircle]="true"
                         [minWidth]="'unset'"
                         [maxWidth]="'unset'"
                         [tableMinHeight]="'69vh'"
                         [notFoundText]="surveyNotFound">
      </jrs-dynamic-table>
    </jrs-section>
  `,
  styles: []
})
export class SurveyContainerComponent implements OnInit {
  tableColumns = TABLE_COLUMNS;
  surveys: Survey[] = [];
  contextActions = SURVEY_ACTIONS;
  surveyNotFound = SURVEY_NOT_FOUND_TEXT;
  loading: boolean;
  private selectedSurvey: Survey;

  constructor(private sb: SurveySandboxService) {
  }

  ngOnInit(): void {
    this.getSurveys();
  }

  getSurveys(): void {
    this.loading = true;
    this.sb.getAssessments().subscribe((surveys: Survey[]) => {
      this.surveys = surveys;
      this.loading = false;
    }, () => {
      this.loading = false;
    })
  }

  actionTriggered(event): void {
    const action = event.action;
    switch (action) {
      case SURVEY_DELETE:
        this.deleteSurvey(event.id, event.status);
        break;
      case SURVEY_EDIT:
        this.sb.navigateByUrl('/survey/update/' + event.id);
        break;
      case SURVEY_CLONE:
        this.openCloneDialog();
        break;
      default:
        break;
    }
  }

  navigateToCreate(): void {
    this.sb.navigateByUrl('/survey/create');
  }

  surveySelected($event: Survey): void {
    this.selectedSurvey = $event;
  }

  private deleteSurvey(surveyId: string, status: string): void {
    if (status !== SurveyStatus.INACTIVE) {
      this.sb.openSnackbar('Үнэлгээ идэвхтэй байгаа учир устгах боломжгүй байна!', false)
      return;
    }
    const config = new DialogConfig();
    config.outsideClick = true;
    config.data = {
      info: "Та энэ үнэлгээг устгах гэж байна, устгахдаа итгэлтэй байна уу?"
    }
    config.title = "Үнэлгээ устгах"
    const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config)
    dialogRef.afterClosed.subscribe(res => {
      if (res) {
        this.loading = true
        this.sb.deleteSurvey(surveyId).subscribe(() => {
          this.loading = false;
          this.sb.openSnackbar('Үнэлгээг амжилттай устгалаа', true);
          this.getSurveys();
        }, () => {
          this.loading = false
          this.sb.openSnackbar("Үнэлгээ устгахад алдаа гарлаа");
        })
      }
    })
  }

  private openCloneDialog(): void {
    const config = new DialogConfig();
    config.outsideClick = true;
    config.title = "Үнэлгээний хуудас хуулах уу?";
    config.data = {
      info: (`Та '${this.selectedSurvey.name}' нэртэй үнэлгээний хуудас хуулах бол ТИЙМ гэж дарна уу.`)
    };

    const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
    dialogRef.afterClosed.subscribe(result => {
      if (result) {
        this.loading = true;
        this.sb.cloneSurvey(this.selectedSurvey.id, this.selectedSurvey).subscribe(() => {
          this.loading = false;
          this.sb.openSnackbar("Үнэлгээний хуудас амжилттай хууллаа.", true);
          this.getSurveys();
        }, () => {
          this.loading = false;
          this.sb.openSnackbar("Үнэлгээний хуудас хуулахад алдаа гарлаа!");
        });
      }
    });
  }
}
