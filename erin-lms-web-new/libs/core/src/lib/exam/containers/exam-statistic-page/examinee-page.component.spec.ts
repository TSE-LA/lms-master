import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ExamStatisticsComponent} from "./exam-statistics-page.component";

//TODO: remove ignore flag
xdescribe('ExamineePageComponent', () => {
  let component: ExamStatisticsComponent;
  let fixture: ComponentFixture<ExamStatisticsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExamStatisticsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExamStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
