import {
  ClassroomCourseAttendanceModel,
  ClassroomCourseModel,
  ClassroomCourseState,
  ClassroomCourseUpdateModel,
  CreateClassroomCourseModel,
  CreateClassroomCourseTableModel,
  DateItem,
  InstructorDropdownModel,
  Invitation
} from "./classroom-course.model";
import {CategoryItem} from "../../../../../shared/src/lib/shared-model";
import {DateFormatter} from "../../../../../shared/src/lib/utilities/date-formatter.util";
import {GetDayValueUtil} from "../../../../../shared/src/lib/utilities/day-value.util";
import {STATE_ITEMS} from "./classroom-course.constants";

export class ClassroomCourseMapper {
  public static mapToUpdateModel(classroomCourse: ClassroomCourseUpdateModel) {
    return {
      categoryId: classroomCourse.categoryId,
      type: classroomCourse.typeId,
      description: classroomCourse.description,
      title: classroomCourse.name,
      properties: {
        address: classroomCourse.address,
        teacher: classroomCourse.instructor,
        teacherNumber: classroomCourse.instructorNumber,
        state: classroomCourse.state,
        maxEnrollmentCount: classroomCourse.maxEnrollmentCount,
        attachedFileId: classroomCourse.attachment.id,
        date: classroomCourse.startDate,
        startTime: classroomCourse.startTime,
        endTime: classroomCourse.endTime,
        attachmentName: classroomCourse.attachment.name,
        attachmentRenderedName: classroomCourse.attachment.renderedName,
        previousState: classroomCourse.previousState ? classroomCourse.previousState : null
      },
      hasAssessment: !!classroomCourse.surveyId,
      credit: classroomCourse.credit,
      assessmentId: classroomCourse.surveyId,
      hasCertificate: !!classroomCourse.certificateId,
      certificateId: classroomCourse.certificateId,
      emailSubject: 'Танхимын сургалтанд өөрчлөлт орлоо.',
      templateName: 'classroom-course-update-email-template.ftl'
    };
  }

  public static mapToCategoryModel(entity: any): CategoryItem {
    return {
      id: entity.categoryId,
      name: entity.categoryName
    }
  }

  public static mapToCreateRestModel(classroomCourse: CreateClassroomCourseModel, dateItem: DateItem, attachmentMimeType: string): any {
    return {
      categoryId: classroomCourse.categoryId,
      type: classroomCourse.type,
      description: classroomCourse.description,
      title: classroomCourse.name,
      credit: classroomCourse.credit,
      properties: {
        address: classroomCourse.address,
        teacher: classroomCourse.instructor,
        teacherNumber: classroomCourse.instructorNumber,
        state: ClassroomCourseState[ClassroomCourseState.NEW],
        maxEnrollmentCount: classroomCourse.maxEnrollmentCount,
        attachedFileId: classroomCourse.attachedFileId,
        date: DateFormatter.toISODateString(dateItem.date),
        startTime: (dateItem.startHour <= 9 ? '0' + dateItem.startHour : dateItem.startHour) + ":" +
          (dateItem.startMinute <= 9 ? '0' + dateItem.startMinute : dateItem.startMinute),
        endTime: (dateItem.endHour <= 9 ? '0' + dateItem.endHour : dateItem.endHour) + ":" +
          (dateItem.endMinute <= 9 ? '0' + dateItem.endMinute : dateItem.endMinute),
        attachmentName: classroomCourse.attachFileName,
        attachmentRenderedName: classroomCourse.attachFileRenderedName,
        attachmentMimeType: attachmentMimeType
      },
      hasAssessment: !!classroomCourse.surveyId,
      assessmentId: classroomCourse.surveyId,
      hasCertificate: !!classroomCourse.certificateId,
      certificateId: classroomCourse.certificateId,
    };
  }

  public static mapToClassroomCourseModel(entity: any): ClassroomCourseModel {
    const date = new Date(DateFormatter.toISODateString(entity.properties.date));
    return {
      id: entity.id,
      categoryId: entity.courseCategoryId,
      name: entity.title,
      typeId: entity.type,
      description: entity.description,
      author: entity.authorId,
      hasCertificate: entity.hasCertificate,
      hasSurvey: entity.hasAssessment,
      credit: entity.credit,
      createdDate: DateFormatter.toISODateString(entity.createdDate),
      address: entity.properties.address ? entity.properties.address : '',
      instructor: entity.properties.teacher,
      instructorNumber: entity.properties.teacherNumber,
      dateAndTime: {
        dateString: DateFormatter.toISODateString(entity.properties.date),
        date: date,
        weekday: GetDayValueUtil.getDay(date),
        startTime: entity.properties.startTime,
        endTime: entity.properties.endTime,
      },
      enrollment: {
        assignedDepartments: entity.assignedDepartments,
        assignedLearners: entity.assignedLearners,
        groups: entity.assignedDepartments,
        maxEnrollmentCount: entity.properties.maxEnrollmentCount,
        departmentNames: Object.values(entity.assignedDepartmentNames).join(' '),
        enrollmentCount: entity.enrollmentCount,
        enrollmentMerged: this.mapEnrollment(entity.properties.maxEnrollmentCount,
          this.getEnrollmentCount(entity, entity.suggestedLearners != null ? entity.suggestedLearners.length : 0)),
        isClassroomFull: entity.enrollmentCount >= entity.properties.maxEnrollmentCount,
        hasEnrolled: false,
      },
      attachment: {
        name: entity.properties.attachmentName,
        renderedName: entity.properties.attachmentRenderedName,
        id: entity.properties.attachedFileId,
        type: entity.properties.attachmentMimeType,
      },
      state: entity.properties.state ? entity.properties.state : entity.publishStatus,
      previousState: entity.properties.previousState,
      surveyId: entity.assessmentId,
      certificateId: entity.certificateId,
      reason: entity.properties.reason,
      categoryName: entity.courseCategoryName,
    }
  }

  public static mapToClassroomCourseActivity(res: any): CreateClassroomCourseTableModel[] {
    const result: CreateClassroomCourseTableModel[] = [];
    for (let data of res) {
      result.push({
        id: data.id,
        categoryName: data.courseCategoryName,
        courseName: data.title,
        status: STATE_ITEMS.filter(item => item.state === data.properties.state.toLowerCase())[0].name,
        maxEnrollmentCount: data.properties.maxEnrollmentCount,
        startDate: DateFormatter.toISODateString(data.properties.date),
        address: data.properties.address,
        published: data.authorId,
        creationDate: DateFormatter.toISODateString(data.createdDate),
      })
    }
    return result;
  }

  public static mapToDepartmentNames(departmentsEntity): string {
    const departments = Object.entries(departmentsEntity);
    if (departments.length > 0) {
      let result = departments[0][1];
      for (let i = 1; i < departments.length; i++) {
        result = result + ',\xa0' + departments[i][1];
      }
      return <string>result;
    }
  }

  public static getEnrollmentCount(entity: any, suggestedUserCount: number): number {
    return entity.properties.state !== 'DONE' ? suggestedUserCount : entity.enrollmentCount;
  }

  public static mapEnrollment(max: number, current: number): string {
    let enrollment;
    if (!current) {
      enrollment = '0';
      if (max > 9) {
        enrollment += '0';
      }
    } else {
      if (max > 9 && current <= 9) {
        enrollment = '0' + current;
      } else {
        enrollment = current.toString();
      }
    }
    enrollment += '/' + max;

    return enrollment;
  }


  public static mapToGradeTable(res): any {
    if (res.grades == null) {
      return null;
    }
    const average = this.getAverage(res);
    return [
      res.grades && res.grades[0] ? res.grades[0] : res.grades[0] == null ? '-' : 0,
      res.grades && res.grades[1] ? res.grades[1] : res.grades[1] == null ? '-' : 0,
      res.grades && res.grades[2] ? res.grades[2] : res.grades[2] == null ? '-' : 0,
      average.toString().substring(0, 4),
      res.present ? 'Ирсэн' : 'Ирээгүй'
    ];
  }

  public static getAverage(data): number {
    let total = 0;
    let divider = 0;
    for (const grade of data.grades) {
      if (grade !== null) {
        total += grade;
        divider++;
      }
    }
    if (total == 0 && divider == 0) {
      return 0;
    }
    return total / divider;
  }

  public static mapToAttendanceModel(entity: any): ClassroomCourseAttendanceModel[] {
    const attendances: ClassroomCourseAttendanceModel[] = [];
    for (const attendance of entity) {
      attendances.push({
        username: attendance.learnerId,
        displayName: "",
        groups: attendance.groupName,
        supervisor: attendance.supervisorId,
        invitation: attendance.invited ? Invitation.SENT : Invitation.UNSENT,
        attendance: attendance.present,
        score1: attendance.grades != null ? attendance.grades[0] : undefined,
        score2: attendance.grades != null ? attendance.grades[1] : undefined,
        score3: attendance.grades != null ? attendance.grades[2] : undefined,
      });
    }
    return attendances;
  }


  public static mapToRestAttendancesModel(attendances: ClassroomCourseAttendanceModel[]): any[] {
    const rest = [];
    for (const attendance of attendances) {
      rest.push({
        employeeName: attendance.displayName,
        department: attendance.groups ? attendance.groups : "",
        supervisor: attendance.supervisor ? attendance.supervisor : "",
        state: Invitation[attendance.invitation],
        present: attendance.attendance,
        grade1: attendance.score1 ? attendance.score1 : null,
        grade2: attendance.score2 ? attendance.score2 : null,
        grade3: attendance.score3 ? attendance.score3 : null,
      });
    }
    return rest;
  }

  public static mapToCloseClassroomModel(attendances: ClassroomCourseAttendanceModel[]) {
    const rest = [];
    for (const attendance of attendances) {
      const grades: number[] = [];
      if (attendance.score1 !== undefined) {
        grades.push(attendance.score1);
      }
      if (attendance.score1 === undefined) {
        grades.push(null);
      }
      if (attendance.score2 !== undefined) {
        grades.push(attendance.score2);
      }
      if (attendance.score2 === undefined) {
        grades.push(null);
      }
      if (attendance.score3 !== undefined) {
        grades.push(attendance.score3);
      }
      if (attendance.score3 === undefined) {
        grades.push(null);
      }
      rest.push({
        learnerId: attendance.username,
        present: attendance.attendance,
        groupName: attendance.groups,
        invited: attendance.invitation == 0,
        supervisorId: attendance.supervisor,
        grades: grades.length !== 0 ? grades : null,
      });
    }
    return rest;
  }

  public static mapToInstructorsModel(entity: any[]): InstructorDropdownModel[] {
    const instructors = [];
    for (const instructor of entity) {
      instructors.push({
        name: instructor.name,
        phoneNumber: instructor.phone,
        email: instructor.email
      })
    }
    return instructors;
  }
}
