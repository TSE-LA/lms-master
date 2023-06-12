import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OnlineCourseInfoComponent } from './online-course-info.component';

describe('OnlineCourseInfoComponent', () => {
  let component: OnlineCourseInfoComponent;
  let fixture: ComponentFixture<OnlineCourseInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OnlineCourseInfoComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OnlineCourseInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
