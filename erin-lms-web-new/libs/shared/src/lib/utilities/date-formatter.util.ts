export class DateFormatter {

  public static dateFormat(date: Date, separator): string {
    return date.getFullYear() + separator + this.zeroDigitPlace(date.getMonth() + 1) + separator + this.zeroDigitPlace(date.getDate());
  }

  public static dateFormatWithTimeISO(date: Date): string {
    return date.getFullYear() + '-' + this.zeroDigitPlace(date.getMonth() + 1) + '-' + this.zeroDigitPlace(date.getDate()) + ' ' + this.timeFormat(date);
  }


  public static dateFormatWithTime(date: Date): string {
    return this.isoToDatePickerString(date) + ' ' + this.zeroDigitPlace(date.getHours()) + ':' + this.zeroDigitPlace(date.getMinutes());
  }

  public static timeFormat(date: Date): string {
    return this.zeroDigitPlace(date.getHours()) + ':' + this.zeroDigitPlace(date.getMinutes()) + ':' + this.zeroDigitPlace(date.getSeconds());
  }

  public static zeroDigitPlace(data: number): string {
    return (data < 10) ? '0' + data : data.toString();
  }

  public static toISODateString(element: any): string {
    if (!element) {
      return '-';
    }
    const date = new Date(element);
    return this.dateFormat(date, '-');
  }

  public static isoToDatePickerString(element: any): string {
    if (!element || element == '-') {
      return element;
    }
    const date = new Date(element);
    return this.dateFormat(date, '.');
  }

  public static isoToDatePickerStringValue(element: any): string {
    if (!element || element == '-') {
      return element;
    }
    const date = new Date(element);
    return this.dateFormat(date, '-');
  }

  public static toISOStringWithTime(element: any): string {
    if (!element) {
      return '-';
    }
    const date = new Date(element);

    return this.dateFormatWithTimeISO(date);
  }
}
