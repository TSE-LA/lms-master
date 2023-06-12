import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimedCourseUpdatePageComponent } from './timed-course-update-page.component';

describe('TimedCourseUpdatePageComponent', () => {
  let component: TimedCourseUpdatePageComponent;
  let fixture: ComponentFixture<TimedCourseUpdatePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TimedCourseUpdatePageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimedCourseUpdatePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
