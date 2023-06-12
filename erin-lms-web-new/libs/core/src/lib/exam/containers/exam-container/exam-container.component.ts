import {Component} from '@angular/core';
import {ExamSandboxService} from "../../services/exam-sandbox.service";
import {Router} from "@angular/router";

@Component({
  selector: 'jrs-exam-container',
  template: `
    <jrs-tab-group [chooseFirst]="false" (tabSelected)="changeLocation($event)">
      <jrs-tab *ngFor="let page of examPages" [label]="page.name" [active]="page.active">
      </jrs-tab>
    </jrs-tab-group>
    <router-outlet class="container"></router-outlet>
  `,
  styleUrls: ['./exam-container.component.scss']
})
export class ExamContainerComponent {
  examPages = [
    {name: "Шалгалтын сан", path: "/list", active: false, id: "app.navigation.exam-list"},
    {name: "Асуултын сан", path: "/question-list", active: false, id: "app.navigation.question-list"},
    {name: "Шалгалтын жагсаалт", path: "/examinee-list", active: false, id: "app.navigation.examinee-list"},
    {name: "Шалгалтын статистик", path: "/exam-statistics/", active: false, id: "app.navigation.exam-statistics"}
  ];

  constructor(private sb: ExamSandboxService, private router: Router) {
    this.examPages = [...this.sb.filterPermission(this.examPages)];
  }

  changeLocation(event): void {
    if (event.initial) {
      const foundPage = this.examPages.find(page => this.router.url.includes(page.path));
      if (foundPage != null) {
        foundPage.active = true;
      } else {
        this.sb.navigateByUrl("/exam/container" + this.examPages[0].path);
      }
    } else {
      const page = this.examPages.find(page => page.name == event.label);
      this.sb.navigateByUrl("/exam/container" + page.path);
    }
  }
}
