import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddExamGroupDialogComponent } from './add-exam-group-dailog.component';

describe('AddGroupDialogComponent', () => {
  let component: AddExamGroupDialogComponent;
  let fixture: ComponentFixture<AddExamGroupDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddExamGroupDialogComponent ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddExamGroupDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
