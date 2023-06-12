import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditExamGroupDialogComponent } from './edit-exam-group-dialog.component';

describe('EditGroupDialogComponent', () => {
  let component: EditExamGroupDialogComponent;
  let fixture: ComponentFixture<EditExamGroupDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditExamGroupDialogComponent ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditExamGroupDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
