import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OnlineCourseContainerComponent } from './online-course-container.component';

describe('OnlineCourseContainerComponent', () => {
  let component: OnlineCourseContainerComponent;
  let fixture: ComponentFixture<OnlineCourseContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OnlineCourseContainerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OnlineCourseContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
