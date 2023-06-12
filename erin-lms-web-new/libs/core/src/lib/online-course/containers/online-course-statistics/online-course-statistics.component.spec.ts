import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OnlineCourseStatisticsComponent } from './online-course-statistics.component';

describe('OnlineCourseStatisticsComponent', () => {
  let component: OnlineCourseStatisticsComponent;
  let fixture: ComponentFixture<OnlineCourseStatisticsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OnlineCourseStatisticsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OnlineCourseStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
