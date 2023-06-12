import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TestStructureComponent } from './test-structure.component';

describe('TestStructureComponent', () => {
  let component: TestStructureComponent;
  let fixture: ComponentFixture<TestStructureComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TestStructureComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestStructureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
