import {ChangeDetectorRef, Component, EventEmitter, HostListener, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {DashletModel} from "../dashlet-model";
import {PublishedCourse, PublishedCourseCountData} from "../../../../../core/src/lib/dashboard/dashboard-model/dashboard-model";
import {DropdownModel} from "../../dropdown/dropdownModel";

@Component({
  selector: 'jrs-course-count-dashlet',
  template: `
    <div class="dashlet" [ngClass]="{'loading': dashletInfo.loading}">
      <div class="header">
        <div class="title">{{dashletInfo.name}}</div>
        <jrs-dropdown-view
          class="select-role"
          [chooseFirst]="true"
          [values]="types"
          [width]="'160px'"
          [color]="'light'"
          [outlined]="true"
          (selectedValue)="selectValue($event)"
          [size]="'small'">
        </jrs-dropdown-view>
        <!--        <div class="button" (click)="deleteDashlet(dashletInfo.id)"></div>-->
      </div>
      <div class="total-count">НИЙТ: {{totalCount}}</div>
      <div [ngClass]="{'hide-dashlet': this.maxValue <= 0}" class="bottom">
        <div class="chart">
          <div *ngFor="let data of chartData"
               jrsTooltip="{{data.categoryName + ': '+ data.count}}"
               placement="{{placement}}"
               delay="0"
               class="chart-item">
            <div class="category-name">{{data.categoryName}}</div>
            <div id="vertical-bar" class="category-count">
              <div id="{{data.categoryId}}" class="count-meter"></div>
              <div id="{{data.categoryId + 'back'}}" class="background-line"></div>
            </div>
          </div>
        </div>
        <div class="count-range">
          <div *ngFor="let number of countNumbers" class="count-number">{{number}}</div>
          <div class="count-number">{{this.maxValue}}</div>
        </div>
      </div>
      <div *ngIf="this.maxValue <= 0" class="not-found">
        <div class="img">
          <jrs-image-viewer [load]="false" [imageSrc]="'assets/images/file-not-found.png'"></jrs-image-viewer>
        </div>
        <div class="not-found-text">
          <span>МЭДЭЭЛЭЛ ОЛДСОНГҮЙ</span>
        </div>
      </div>
    </div>`,
  styleUrls: ['./course-count-dashlet.component.scss']
})
export class CourseCountDashletComponent implements OnChanges {
  @Input() chartDataBundle: PublishedCourseCountData[] = [];
  @Input() loading: boolean;
  @Input() dashletInfo: DashletModel;
  @Input() types: DropdownModel[] = [];
  @Output() delete = new EventEmitter<string>();
  backgroundLineWidth: any;
  maxValue: number;
  placement = 'left';
  chartData: PublishedCourse[] = [];
  selectedType: string;
  totalCount: number;
  countNumbers = [];

  constructor(private cdRef: ChangeDetectorRef) {
  }

  @HostListener('window:resize') onResize(): void {
    this.renderCharts();
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop == 'chartDataBundle') {
        if (this.selectedType == null) {
          this.selectedType = this.types[0].id;
        }
        this.setChartData();
      }
    }
  }


  setChartData(): void {
    this.countNumbers = [];
    const setChart = new Promise(resolve => {
      this.chartDataBundle.forEach(data => {
        if (data.type == this.selectedType) {
          this.chartData = data.courseCountByCategory;
          this.totalCount = data.totalCount;
          this.cdRef.detectChanges();
        }
      })
      if (this.chartData != null) {
        resolve(true);
      } else {
        resolve(false);
      }
    });
    setChart.then(success => {
      if (success) {
        this.calculateChartData();
      }
    })
    setChart.then(success => {
      if (success) {
        this.renderCharts();
      }
    })
  }

  calculateChartData(): void {
    const counts = [];
    this.chartData.forEach(data => counts.push(data.count));
    this.maxValue = Math.max(...counts);
    this.maxValue = Math.ceil(this.maxValue / 10) * 10;
    let countNumbers = this.maxValue;
    for (let i = 1; i <= 10; i++) {
      if (countNumbers < 1) {
        this.countNumbers.push(i - 1);
      } else {
        countNumbers = countNumbers - (this.maxValue * 0.1);
        this.countNumbers.push(countNumbers);
      }
    }
    this.countNumbers.sort((a, b) => a - b);
  }

  selectValue(event: DropdownModel): void {
    this.selectedType = event.id;
    this.setChartData();
    this.calculateCategoryCountWidth();
  }

  renderCharts(): void {
    if (document.getElementById('vertical-bar') != null) {
      this.backgroundLineWidth = document.getElementById('vertical-bar').getBoundingClientRect().width;
      this.calculateCategoryCountWidth();
    }
  }

  calculateCategoryCountWidth(): void {
    let width, chartBar, backLine;
    for (let data of this.chartData) {
      backLine = document.getElementById(data.categoryId + 'back');
      chartBar = document.getElementById(data.categoryId);
      if (this.maxValue > 0) {
        width = (data.count / this.maxValue) * 100;
        chartBar.style.width = width + '%';
        backLine.style.width = (100 - width) + '%';
      } else {
        chartBar.style.width = '0%';
        backLine.style.width = '100%'
      }
    }
  }

  deleteDashlet(dashletId): void {
    this.delete.emit(dashletId);
  }
}
