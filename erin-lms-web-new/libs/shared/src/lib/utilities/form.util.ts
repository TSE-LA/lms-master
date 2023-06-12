import {FormGroup} from "@angular/forms";

export class FormUtil {
  public static isFormValid(formGroup: FormGroup): boolean {
    let invalids = 0;

    for (const [key] of Object.entries(formGroup.controls)) {
      if (formGroup.controls[key].invalid) {
        invalids++;
        formGroup.controls[key].markAsDirty();
      }
    }
    return invalids == 0;
  }

  public static generateId(): string {
    return '_' + Math.random().toString(36).substr(2, 9);
  }
}
