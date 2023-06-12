import {AfterViewInit, Component, Input, OnChanges, SimpleChanges, ViewChild} from '@angular/core';
import {Chart, ChartType, registerables} from "chart.js";
import {CHART_COLORS, ChartDataModel} from "./chart.model";
import {LegendModel} from "../legends/legend.model";

@Component({
  selector: 'jrs-chart',
  template: `
    <span *ngIf="dataSet.index">{{dataSet.index + suffixText + ': ' + dataSet.text}}</span>
    <div *ngIf="!loading" class="chart-wrapper" [ngClass]="{'hide': totalResponses < 1}">
      <div [ngClass]="dataSet.chartType == 'pie' ? 'pie-chart' : 'bar-chart'">
        <canvas #chart></canvas>
      </div>
      <div *ngIf="totalResponses == 0" class="not-found">
        <div class="img">
          <jrs-image-viewer [load]="false" [imageSrc]="'assets/images/file-not-found.png'"></jrs-image-viewer>
        </div>
        <div class="not-found-text">
          <span>ҮНЭЛГЭЭГ БӨГЛӨӨГҮЙ БАЙНА</span>
        </div>
      </div>
      <div *ngIf="hasLegend" class="legend">
        <jrs-legends [legends]="legendData"></jrs-legends>
      </div>
    </div>

  `,
  styleUrls: ['./chart.component.scss']
})
export class ChartComponent implements OnChanges, AfterViewInit {
  @ViewChild('chart') chart;
  @Input() dataSet: ChartDataModel;
  @Input() chartType: ChartType;
  @Input() suffixText: string;
  @Input() hasLegend: boolean;
  @Input() totalResponses: number;
  @Input() loading: boolean;
  legendData: LegendModel[] = [];
  public context: any;

  constructor() {
    Chart.register(...registerables);
  }

  ngAfterViewInit(): void {
    this.drawChart();
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (let prop in changes) {
      if (prop == 'dataSet' && this.chart != null) {
        this.drawChart();
      }
    }
  }

  drawChart(): void {
    if (this.hasLegend) {
      this.getLegendData();
    }
    this.context = this.chart.nativeElement.getContext('2d');
    const data = {
      labels: this.dataSet.labels,
      datasets: [{
        data: this.dataSet.data,
        backgroundColor: CHART_COLORS,
        hoverOffset: 4
      }]
    };
    const chart = new Chart(this.context, {
      type: this.dataSet.chartType,
      data: data,
      options: {
        indexAxis: 'y',
        elements: {
          bar: {
            borderWidth: 2,
          }
        },
        responsive: true
      },
    })
    chart.options.plugins.legend.display = false;
    chart.update();
  }

  getLegendData(): void {
    const result: LegendModel[] = [];
    for (let i = 0; i < this.dataSet.data.length; i++) {
      result.push({
        color: CHART_COLORS[i],
        text: this.dataSet.labels[i]
      })
    }
    this.legendData = result;
  }
}
