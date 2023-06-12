import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClassroomCourseSuperInvitationComponent } from './classroom-course-super-invitation.component';

describe('ClassroomCourseSuperInvitationComponent', () => {
  let component: ClassroomCourseSuperInvitationComponent;
  let fixture: ComponentFixture<ClassroomCourseSuperInvitationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ClassroomCourseSuperInvitationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ClassroomCourseSuperInvitationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
