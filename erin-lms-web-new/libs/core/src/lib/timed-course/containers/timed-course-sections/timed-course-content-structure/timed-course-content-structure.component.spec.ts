import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimedCourseContentStructureComponent } from './timed-course-content-structure.component';

describe('TimedCourseContentStructureComponent', () => {
  let component: TimedCourseContentStructureComponent;
  let fixture: ComponentFixture<TimedCourseContentStructureComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TimedCourseContentStructureComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimedCourseContentStructureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
