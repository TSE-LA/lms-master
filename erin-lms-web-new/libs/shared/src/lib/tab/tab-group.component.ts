import {AfterContentInit, Component, ContentChildren, EventEmitter, Input, OnChanges, Output, QueryList, SimpleChanges} from "@angular/core";
import {TabComponent} from "./tab.component";

@Component({
  selector: 'jrs-tab-group',
  template: `
    <ul class="tab-group">
      <li *ngFor="let tab of tabs" class="tabs" [class]="size" (click)="selectTab(tab)" [class.active]="tab.active">
        <a>{{tab.label}}</a>
      </li>
    </ul>
    <div class="tab-content">
      <ng-content></ng-content>
    </div>
  `,
  styleUrls: ['./tab-group.component.scss']
})
export class TabGroupComponent implements AfterContentInit {
  @ContentChildren(TabComponent) tabs: QueryList<TabComponent>;
  @Input() size = 'small';
  @Input() chooseFirst = true;
  @Output() tabSelected: EventEmitter<any> = new EventEmitter();
  defaultChoice: string;

  ngAfterContentInit(): void {
    const activeTabs = this.tabs.filter(tab => tab.active);

    if (activeTabs.length === 0 && this.chooseFirst) {
      this.selectTab(this.tabs.first);
    } else {
      this.tabSelected.emit({label: '', initial: true});
    }
  }

  selectTab(tab: TabComponent): void {

    this.tabs.toArray().forEach(tab => {
      tab.active = false
    });
    tab.active = true;
    this.tabSelected.emit({label: tab.label, initial: false});
  }
}
