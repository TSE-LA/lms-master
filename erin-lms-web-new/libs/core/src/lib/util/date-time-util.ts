import {JOB_TITLE} from "../user-management/models/profile.constants";

export class DatetimeUtil {
  public static isLeapYear(year: number) {
    return (year % 400 === 0) || ((year % 4 === 0) && (year % 100 !== 0));
  }

  public static parse(dateStr: string): Date {
    const split = dateStr.split("-");
    return new Date(parseInt(split[0], 0), parseInt(split[1], 0) - 1, parseInt(split[2], 0));
  }

  static getDaysFullMonth(day){
    const d = new Date(day);
    const lastDay = new Date(d.getFullYear(), d.getMonth() + 1, 0);
    return lastDay.getDate();
  }
  public static getDateDifference(selectedDate): string {
    let date: string;
    const currentDate = new Date();
    let yearDiff = currentDate.getFullYear() - new Date(selectedDate).getFullYear();
    let monthDiff = currentDate.getMonth() - new Date(selectedDate).getMonth();
    let dayDiff = currentDate.getDate() - new Date(selectedDate).getDate();
    if(yearDiff < 0) {
      const lastFullMonth = this.getDaysFullMonth(currentDate);
      if(lastFullMonth < new Date(selectedDate).getDate()) {
        dayDiff = lastFullMonth + dayDiff + (new Date(selectedDate).getDate() - lastFullMonth)
      } else {
        dayDiff = lastFullMonth + dayDiff;
      }
      monthDiff--;
    }
    if(monthDiff < 0) {
      monthDiff = 12 + monthDiff;
      yearDiff--;
    }
    if(!isNaN(yearDiff) && !isNaN(monthDiff) && !isNaN(dayDiff)) {
      if(yearDiff !== 0 && monthDiff !== 0) {
        date = (yearDiff + ' жил' + ' ' + monthDiff + ' сар')
      } else if(yearDiff == 0 && monthDiff > 0) {
        date = (monthDiff + ' сар')
      } else if(yearDiff == 0 && monthDiff == 0) {
        date = (dayDiff + ' өдөр')
      } else if(yearDiff !== 0 && monthDiff === 0) {
        date = (yearDiff + ' жил')
      }
      return date;
    }
  }

}

export enum Month {
  JANUARY,
  FEBRUARY,
  MARCH,
  APRIL,
  MAY,
  JUNE,
  JULY,
  AUGUST,
  SEPTEMBER,
  OCTOBER,
  NOVEMBER,
  DECEMBER
}

export function getFieldValue(type: 'INPUT' | 'SELECT' | 'DATE', value?: string | { id: string, name: string }): string {
  switch (type) {
    case 'SELECT':
      return (value as { id: string, name: string })?.name;
    case 'DATE':
    case 'INPUT':
    default:
      return value as string;
  }
}
export function getSelectOptions(name: string): { id: string, name: string }[] {
  if (name === 'jobTitle') {
    return JOB_TITLE;
  }

  return [];
}

export function getTranslatedName(name:string): string {
  switch (name) {
    case 'jurisdictionalCourt':
      return 'Харьяалах шүүх';
    case 'appointedDate':
      return 'Шүүгчээр томилогдсон огноо';
    case 'jobYear':
      return 'Шүүгчээр ажиллаж буй жил';
    case 'jobTitle':
      return 'Албан тушаал';
    case 'other':
      return 'Бусад';
    default:
      return name;
  }
}
