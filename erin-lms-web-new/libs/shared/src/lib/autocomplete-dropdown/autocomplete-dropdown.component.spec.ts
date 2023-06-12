import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AutocompleteDropdownComponent} from './autocomplete-dropdown.component';
import {DropdownModel} from "../dropdown/dropdownModel";
import {Type} from "@angular/core";

describe('AutocompleteDropdownComponent', () => {
  let component: AutocompleteDropdownComponent<DropdownModel>;
  let fixture: ComponentFixture<AutocompleteDropdownComponent<DropdownModel>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AutocompleteDropdownComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AutocompleteDropdownComponent as Type<AutocompleteDropdownComponent<DropdownModel>>);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
