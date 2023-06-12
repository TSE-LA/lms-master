import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimedCourseLaunchPageComponent } from './timed-course-launch-page.component';

describe('TimedCourseLaunchPageComponent', () => {
  let component: TimedCourseLaunchPageComponent;
  let fixture: ComponentFixture<TimedCourseLaunchPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TimedCourseLaunchPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimedCourseLaunchPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
