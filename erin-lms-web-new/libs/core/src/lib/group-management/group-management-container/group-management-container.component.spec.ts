import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupManagementContainerComponent } from './group-management-container.component';

describe('GroupManagementContainerComponent', () => {
  let component: GroupManagementContainerComponent;
  let fixture: ComponentFixture<GroupManagementContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GroupManagementContainerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GroupManagementContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
