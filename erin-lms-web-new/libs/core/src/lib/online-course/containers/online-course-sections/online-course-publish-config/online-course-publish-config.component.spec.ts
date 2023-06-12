import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OnlineCoursePublishConfigComponent } from './online-course-publish-config.component';

describe('OnlineCoursePublishConfigComponent', () => {
  let component: OnlineCoursePublishConfigComponent;
  let fixture: ComponentFixture<OnlineCoursePublishConfigComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OnlineCoursePublishConfigComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OnlineCoursePublishConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
