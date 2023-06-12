import {STATE_ITEMS} from "../../../../core/src/lib/classroom-course/model/classroom-course.constants";
import {ClassroomCourseModel, EventAction, CourseProperties} from "../../../../core/src/lib/classroom-course/model/classroom-course.model";
import {CalendarDayProperties} from "../shared-model";

function getCourseType(events: CalendarDayProperties[], id: string): string {
  let courseType: string;
  for(const event of events){
    event.events.map(type => {
      if(type.courseId === id) {
        courseType = type.eventType;
      }
    })
  }
  return courseType;
}

export class CoursePropertyUtil {
  public static mapToCourseProperties(response: ClassroomCourseModel, events?: CalendarDayProperties[]): CourseProperties {
    return {
      courseName: response.name,
      date: response.createdDate ? response.createdDate : response.dateAndTime.dateString + ',' + '\xa0\xa0' + response.dateAndTime.weekday,
      organizerName: 'Сургалт хариуцсан нэгж',
      location: response.address,
      courseCategory: response.categoryName,
      count: response.enrollment.enrollmentCount.toString(),
      type: response.typeId,
      moderator: response.instructor ? response.instructor : response.author,
      time: response.dateAndTime.startTime + "-" + response.dateAndTime.endTime,
      courseState: STATE_ITEMS.filter(event => event.state.toLowerCase() === response.state.toLowerCase())[0].name,
      courseStateColor: STATE_ITEMS.filter(event => event.state.toLowerCase() === response.state.toLowerCase())[0].color,
      attachment: response.attachment,
      courseType: events ? getCourseType(events, response.id) : null,
      courseTypeColor: events && (getCourseType(events, response.id) === 'online-course') ? '#288BDA' : '#2BB18B',
      actions: []
    }
  }

  static getEventActions(state, role: string): EventAction[] {
    const filteredEvents = STATE_ITEMS.filter(event => event.role.toLowerCase() === role.toLowerCase() && event.state.toLowerCase() === state.toLowerCase())
    return filteredEvents[0]? filteredEvents[0].actions: [];
  }

  static getEmptyEvent(): CourseProperties{
    return null;
  }
}
