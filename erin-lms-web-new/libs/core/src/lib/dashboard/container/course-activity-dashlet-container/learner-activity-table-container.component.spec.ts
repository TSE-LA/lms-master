import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LearnerActivityTableContainerComponent } from './learner-activity-table-container.component';

describe('CourseActivityDashletContainerComponent', () => {
  let component: LearnerActivityTableContainerComponent;
  let fixture: ComponentFixture<LearnerActivityTableContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LearnerActivityTableContainerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LearnerActivityTableContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
