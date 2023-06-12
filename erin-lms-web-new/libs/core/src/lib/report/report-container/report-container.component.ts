import {Component, OnInit} from '@angular/core';
import {ReportSandboxService} from "../services/report-sandbox.service";
import {GroupNode} from "../../../../../shared/src/lib/shared-model";
import {UserRoleProperties} from "../../common/common.model";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'jrs-report-container',
  template: `
    <div class="report-header">
      <jrs-tab-group class="margin-right" [chooseFirst]="true" (tabSelected)="navigateToTab($event)">
        <jrs-tab *ngFor="let report of reports" [label]="report.name" [active]="report.active"></jrs-tab>
      </jrs-tab-group>
      <div class="dropdowns">
        <jrs-date-interval-picker
          [month]="true"
          [startDate]="startDate"
          [endDate]="endDate"
          (startDateChange)="this.startDateChange($event)"
          (endDateChange)="this.endDateChange($event)">
        </jrs-date-interval-picker>
        <jrs-drop-down-tree-view
          class="margin-left"
          [selectedGroupName]="selectedGroupName"
          [allGroups]="groups"
          [width]="'150px'"
          [label]="'Алба, хэлтэс'"
          (selectedNode)="groupChange($event)">
        </jrs-drop-down-tree-view>
      </div>
    </div>
    <router-outlet class="container">
    </router-outlet>
  `,
  styleUrls: ['./report-container.component.scss']
})
export class ReportContainerComponent implements OnInit {
  startDate: string;
  endDate: string;
  selectedGroupId: string;
  selectedGroupName: string;
  selectedStartDate: string;
  selectedEndDate: string;
  reports = this.sb.constants.REPORT_CATEGORIES;
  groups: GroupNode[] = []

  constructor(private route: ActivatedRoute, private sb: ReportSandboxService) {
    this.startDate = route.snapshot.queryParamMap.get("startDate");
    this.endDate = route.snapshot.queryParamMap.get("endDate");

    if (this.sb.role != UserRoleProperties.adminRole.id) {
      this.reports = this.reports.filter(report => report.name !== 'Үнэлгээний хуудас')
    }
  }

  ngOnInit(): void {
    this.getGroups();
  }

  navigateToTab(event): void {
    let selectedReport;
    if (event.initial) {
      this.reports[0].active = true;
    }

    this.reports.forEach(report => {
      if (report.name == event.label) {
        report.active = true;
        selectedReport = report;
      } else {
        report.active = false;
      }
    });

    this.sb.navigateByUrl('/report' + selectedReport.path);
  }

  getGroups(): void {
    this.sb.getAllGroups(false).subscribe((groups) => {
      this.selectedGroupName = groups[0].name;
      this.selectedGroupId = groups[0].id;
      this.groups = groups;
      this.groupChange(this.groups[0]);
    });
  }

  startDateChange(event): void {
    this.selectedStartDate = event;
    this.sb.setStartDate(event);
  }

  endDateChange(event): void {
    this.selectedEndDate = event;
    this.sb.setEndDate(event);
  }

  groupChange(value: GroupNode): void {
    this.selectedGroupName = value.name;
    this.selectedGroupId = value.id;
    this.sb.setSelectedGroupId(value.id);
  }
}
