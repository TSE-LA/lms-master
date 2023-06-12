import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupEnrollmentSectionComponent } from './group-enrollment-section.component';

describe('GroupEnrollmentSectionComponent', () => {
  let component: GroupEnrollmentSectionComponent;
  let fixture: ComponentFixture<GroupEnrollmentSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GroupEnrollmentSectionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GroupEnrollmentSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
