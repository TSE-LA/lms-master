export class GetDayValueUtil {

  public static getDay(date) {
    switch (date.getDay()) {
      case 1:
        return DayType.MONDAY;
      case 2:
        return DayType.TUESDAY;
      case 3:
        return DayType.WEDNESDAY;
      case 4:
        return DayType.THURSDAY;
      case 5:
        return DayType.FRIDAY;
      case 6:
        return DayType.SATURDAY;
      case 0:
        return DayType.SUNDAY;
    }
  }
}

export enum DayType {
  MONDAY = 'Даваа',
  TUESDAY = 'Мягмар',
  WEDNESDAY = 'Лхагва',
  THURSDAY = 'Пүрэв',
  FRIDAY = 'Баасан',
  SATURDAY = 'Бямба',
  SUNDAY = 'Ням'
}
