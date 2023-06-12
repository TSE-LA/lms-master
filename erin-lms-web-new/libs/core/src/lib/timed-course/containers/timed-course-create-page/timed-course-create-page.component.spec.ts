import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimedCourseCreatePageComponent } from './timed-course-create-page.component';

describe('TimedCourseCreatePageComponent', () => {
  let component: TimedCourseCreatePageComponent;
  let fixture: ComponentFixture<TimedCourseCreatePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TimedCourseCreatePageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimedCourseCreatePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
