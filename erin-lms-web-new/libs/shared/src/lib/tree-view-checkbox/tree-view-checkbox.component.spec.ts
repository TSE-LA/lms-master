import {ComponentFixture, TestBed} from '@angular/core/testing';
import {TreeViewCheckboxComponent} from './tree-view-checkbox.component';

describe('TreeViewCheckboxComponent', () => {
  let component: TreeViewCheckboxComponent;
  let fixture: ComponentFixture<TreeViewCheckboxComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TreeViewCheckboxComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TreeViewCheckboxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
