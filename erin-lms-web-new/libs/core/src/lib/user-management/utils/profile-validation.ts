import {FormGroup, Validators} from "@angular/forms";

export default class ProfileValidation {
  static userValidation(data: string) {
    switch (data) {
      case 'username' : {
        return [Validators.required, Validators.minLength(4)];
      }
      case 'email' : {
        return [Validators.email, Validators.required, Validators.pattern('^(([^<>()[\\]\\\\.,;:\\s@"]+(\\.[^<>()[\\]\\\\.,;:\\s@"]+)*)|(".+"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$')]
      }
      case 'phoneNumber' : {
        return [Validators.pattern('(\\d{8})')];
      }
      case 'gender' : {
        return [Validators.required];
      }
      case 'password' : {
        return [
          Validators.minLength(8),
          Validators.pattern('((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,})')
        ];
      }
      case 'updatePassword' : {
        return [
          Validators.minLength(8),
          Validators.pattern('((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,})')
        ];
      }

      case 'newPassword' : {
        return [
          Validators.minLength(8),
          Validators.pattern('((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,})')
        ];
      }
      default :
        return [''];
    }
  }

  getUsernameErrorMessage(userForm: FormGroup, isUsernameAlreadyExists: boolean) {
    if (userForm.get('username').hasError('required')) {
      return 'Нэвтрэх нэр заавал оруулна уу.';
    } else if (userForm.get('username').hasError('minlength')) {
      return '4-аас дээш урттай нэр оруулна уу.';
    } else if (userForm.get('username').hasError('pattern')) {
      if (isUsernameAlreadyExists) {
        return 'Нэр давхцаж байна!';
      }
      return 'Хоосон зай агуулсан байна!';
    } else {
      return 'true';
    }
  }

  getPhoneNumberErrorMessage(userForm: FormGroup) {
    if (userForm.get('phoneNumber').hasError('pattern')) {
      return '8 оронтой тоо оруулна уу.';
    } else {
      return '';
    }
  }

  getPasswordErrorMessage(userForm: FormGroup, type: string) {
    if (userForm.get(type).hasError('required')) {
      return 'Нууц үг заавал оруулна уу.';
    } else if (userForm.get(type).hasError('minlength')) {
      return 'Нууц үг богино байна!';
    } else if (userForm.get(type).hasError('pattern')) {
      return 'Нэг тоо эсвэл нэг том тэмдэгт агуулаагүй байна!';
    } else {
      return '';
    }
  }

  getUserEmailErrorMessage(userForm: FormGroup): string {
    if (userForm.controls.email.hasError('required')) {
      return 'Цахим шуудан заавал оруулна уу.';
    } else if (userForm.controls.email.hasError('pattern')) {
      return 'Цахим шуудан буруу форматтай байна. Жишээ: user@gmail.mn';
    } else {
      return '';
    }
  }
}
