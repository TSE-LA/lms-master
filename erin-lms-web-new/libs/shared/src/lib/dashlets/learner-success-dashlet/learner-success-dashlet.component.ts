import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {LEARNER_SUCCESS_GROUPS, LEARNER_SUCCESS_PERCENTAGES, LearnerSuccessInput, SEMESTERS} from '../dashlet-model';
import {DropdownModel} from "../../dropdown/dropdownModel";
import {LearnerSuccessDashletModel} from "../../../../../core/src/lib/dashboard/dashboard-model/dashboard-model";

@Component({
  selector: 'jrs-learner-success-dashlet',
  template: `
    <div class="container margin-top" [class.loading]="loading" [class.large-dashlet]="large">
      <div class="header">
        <jrs-header-text [margin]="false" class="header-text">{{title}}</jrs-header-text>
        <jrs-percentage-indicator
          *ngIf="!loading"
          [overallPercentage]="data? data.overallPercentage: ''"
          [up]="data? data.up: true"
          [changedPercentage]="data? data.difference: ''">
        </jrs-percentage-indicator>
        <jrs-dropdown-view
          *ngIf="!loading"
          [label]="'Он'"
          [color]="'light'"
          [outlined]="true"
          [tooltip]="false"
          [size]="'medium'"
          [defaultValue]="selectedYear.name"
          [values]="years"
          (selectedValue)="selectYear($event)">
        </jrs-dropdown-view>
        <jrs-dropdown-view
          *ngIf="!loading"
          [label]="'Хагас жил'"
          [color]="'light'"
          [outlined]="true"
          [tooltip]="false"
          [size]="'medium'"
          [defaultValue]="selectedSemester.name"
          [values]="semesters"
          (selectedValue)="selectSemester($event)">
        </jrs-dropdown-view>
        <jrs-dropdown-view
          *ngIf="!loading"
          [label]="'Групп'"
          [color]="'light'"
          [outlined]="true"
          [tooltip]="false"
          [size]="'medium'"
          [defaultValue]="selectedGroup.name"
          [values]="groups"
          (selectedValue)="selectGroup($event)">
        </jrs-dropdown-view>
      </div>
      <div class="dashlet-container">
        <div class="dashlet-header" *ngIf="!loading">
          <div class="indicator">
            <div class="own-success-circle"></div>
            <span>Миний авсан оноо</span>
          </div>
          <div class="indicator">
            <div class="others-success-circle"></div>
            <span>Бусад суралцагчдын дундаж оноо</span>
          </div>
        </div>
        <div class="dashlet-body">
          <div class="percentages">
            <div *ngFor="let percentage of percentages" class="percentage">{{percentage}}</div>
          </div>
          <div class="chart-wrapper">
            <div class="parent">
              <div class="data-line">
                <div *ngFor="let percentage of percentages" class="background"></div>
              </div>
              <div class="charts" *ngIf="!loading">
                <div class="chart" *ngFor="let score of data? data.learnerSuccesses: []">
                  <div class="scores">
                    <div class="learnerScore"
                         [ngStyle]="{'height': score.learnerScore + '%'}"
                         jrsTooltip="{{score.learnerScore + '%'}}"
                         placement="{{placement}}"
                         delay="0">
                    </div>
                    <div class="groupScore"
                         [ngStyle]="{'height': score.groupScore + '%'}"
                         jrsTooltip="{{score.groupScore + '%'}}"
                         placement="{{placement}}"
                         delay="0">
                    </div>
                  </div>
                  <div class="month">{{score.month}}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./learner-success-dashlet.component.scss']
})
export class LearnerSuccessDashletComponent implements OnInit {
  @Input() title: string;
  @Input() data: LearnerSuccessDashletModel = {
    overallPercentage: "",
    difference: "",
    totalScore: 0,
    groupTotalScore: 0,
    up: true
  };
  @Input() loading: boolean;
  @Input() large: boolean;
  @Output() getLearnerSuccessData = new EventEmitter<LearnerSuccessInput>();
  percentages = LEARNER_SUCCESS_PERCENTAGES;
  years: DropdownModel[] = [];
  currentYear = {id: new Date().getFullYear(), name: new Date().getFullYear() + ' он'};
  semesters = SEMESTERS;
  groups = LEARNER_SUCCESS_GROUPS;
  selectedSemester: DropdownModel;
  selectedYear: DropdownModel;
  selectedGroup: DropdownModel;
  placement = 'left';


  ngOnInit(): void {
    this.selectedSemester = this.semesters[0];
    this.selectedGroup = this.groups[0];
    this.getYears();
    this.getData();
  }

  getYears(): void {
    // 2020 is being used as the initial year since there is no data before 2020
    for (let year = 2020; year <= this.currentYear.id; year++) {
      this.years.push({id: year.toString(), name: year + ' он'});
    }
    this.selectedYear = {id: this.currentYear.id.toString(), name: this.currentYear.name};
  }

  getData(): void {
    this.getLearnerSuccessData.emit(
      {
        selectedGroup: this.selectedGroup.id,
        selectedYear: this.selectedYear.id,
        selectedHalfYear: this.selectedSemester.id
      });
  }

  selectYear(event: any): void {
    this.selectedYear = event;
    this.getData();
  }

  selectSemester(event: any): void {
    this.selectedSemester = event;
    this.getData();
  }

  selectGroup(event: any): void {
    this.selectedGroup = event;
    this.getData();
  }
}
