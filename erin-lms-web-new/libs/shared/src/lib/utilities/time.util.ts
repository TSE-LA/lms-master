export class TimeUtil {

  public static getTimePickerHour(time: string): string {
    return time.substring(0, 2);
  }

  public static getTimePickerMinute(time: string): string {
    return time.substring(5, 7);
  }

  public static getHour(time: string): string {
    return time.substring(0, 2);
  }

  public static getMinute(time: string): string {
    return time.substring(3, 5);
  }

  public static getTimePickerString(hour: string, minute: string): string {
    return hour + " : " + minute;
  }
}
