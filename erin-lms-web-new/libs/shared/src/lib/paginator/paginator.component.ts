import {Component, Input, OnChanges, Output, SimpleChanges, EventEmitter} from '@angular/core';

@Component({
  selector: 'jrs-paginator',
  template: `
    <div class="paginator" [class.hide]="this.contents.length <= 0">
      <jrs-button
        (clicked)="previousButton()"
        [fixedResolution]="fixedResolution"
        [color]="'light'"
        [outline]="true"
        [iconName]="'arrow_back_ios'"
        [size]="'icon-medium'"
        [iconColor]="'dark'"
        [textColor]=""></jrs-button>
      <div *ngIf="total <= 4">
        <div *ngFor="let item of [].constructor(total); let i = index">
          <jrs-button
            (clicked)="changeFirstNumber($event)"
            [color]="i+1 !== activePage ? 'light' : 'primary'"
            [outline]="i+1 !== activePage"
            [size]="'medium'"
            [title]="i+1"
            [textColor]="i+1 !== activePage ? 'text-dark': 'text-light'"
            [fixedResolution]="fixedResolution"></jrs-button>
        </div>
      </div>
      <div *ngIf="total > 4">
        <div *ngFor="let item of pages">
          <jrs-button (clicked)="changeFirstNumber($event)"
                      [color]="item !== activePage && item !== '•••' ? 'light':'primary'"
                      [noOutline]="item !== activePage && item == '•••'"
                      [outline]="item !== activePage && item != '•••'"
                      [noPointer]="item == '•••'"
                      [size]="'medium'"
                      [title]="item"
                      [textColor]="item !== activePage ? 'text-dark': 'text-light'"
                      [fixedResolution]="fixedResolution">
          </jrs-button>
        </div>
      </div>
      <jrs-button
        (clicked)="nextButton()"
        [fixedResolution]="fixedResolution"
        [iconName]="'arrow_forward_ios'"
        [outline]="true"
        [size]="'icon-medium'"
        [color]="'light'"
        [iconColor]="'dark'">
      </jrs-button>
    </div>`,
  styleUrls: ['./paginator.component.scss']
})
export class PaginatorComponent implements OnChanges {

  @Input() contents: any[];
  @Input() perPageNumber = 8;

  @Output() pageClick = new EventEmitter();
  @Output() pageNumberChange = new EventEmitter<number>();

  total: number;
  pages: any[] = [];
  firstToShowPage = 1;
  activePage = 1;
  fixedResolution = true;
  splittedContents: any[] = []

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.contents) {
      this.firstToShowPage = 1;
      this.activePage = 1;
    }
    this.total = Math.ceil(this.contents.length / this.perPageNumber);
    this.calcPage();
  }

  calcPage(): void {
    if (this.total - this.activePage >= 2 && this.firstToShowPage < this.total) {
      let firstPage = this.firstToShowPage;
      this.clearPages()
      if (this.total - this.activePage === 1) {
        firstPage = this.firstToShowPage - 1;
      }

      this.pages.push(firstPage);
      this.pages.push(firstPage + 1);
      this.pages.push('•••');
      this.pages.push(this.total);
    } else {
      this.clearPages()
      this.pages.push(1);
      this.pages.push('•••');
      this.pages.push(this.total - 1);
      this.pages.push(this.total);
    }
    this.sliceContent();
  }

  changeFirstNumber(e): void {
    const returnedPageNumber: number = +e.target.innerText
    this.activePage = returnedPageNumber;
    if (returnedPageNumber <= this.total) {
      this.firstToShowPage = returnedPageNumber;
      this.calcPage()
    }
  }

  previousButton(): void {
    if (this.firstToShowPage > 1) {
      this.firstToShowPage = this.firstToShowPage - 1;
      this.activePage--;
      this.calcPage();
    }
  }

  nextButton(): void {
    if (this.firstToShowPage < this.total) {
      this.firstToShowPage = this.firstToShowPage + 1;
      this.activePage++;
      this.calcPage();
    }
  }

  clearPages(): void {
    this.pages.splice(0, this.pages.length)
  }

  sliceContent(): void {
    const startTrimIndex = (this.activePage - 1) * this.perPageNumber;
    const endTrimIndex = startTrimIndex + this.perPageNumber;
    this.splittedContents = this.contents.slice(startTrimIndex, endTrimIndex);
    this.pageClick.emit(this.splittedContents);
    this.pageNumberChange.emit(this.activePage)
  }
}
