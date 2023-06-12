import {
  CourseStructure, FileAttachment,
  StructureModule
} from "../../../../../shared/src/lib/structures/content-structure/content-structure.model";
import {
  AttachmentModel,
  EnrollmentState,
  OnlineCourseModel,
  OnlineCourseStatistic,
  OnlineCourseStatisticBundleModel,
  OnlineCourseTableViewModel,
  TestModel
} from "./online-course.model";
import {DateFormatter} from "../../../../../shared/src/lib/utilities/date-formatter.util";
import {AnswerChoice, CategoryItem, GroupNode, QuestionTypes, SmallDashletModel, TestQuestion} from "../../../../../shared/src/lib/shared-model";
import {SecondsToTimeConverterUtil} from "../../util/secondsToTimeConverter.util";
import {TimeToPercentageConverter} from "../../util/TimeToPercentageConverter";

export class OnlineCourseMapper {


  public static mapStructureToRest(attachments: FileAttachment[], structure: StructureModule[]): any {
    const mapped = {attachmentIdList: [], modules: []};
    attachments.forEach(attachment => {
      mapped.attachmentIdList.push(attachment.id)
    });
    structure.forEach((module, moduleIndex) => {
      mapped.modules.push({
        name: module.updatedName,
        index: moduleIndex + 1,
        sectionList: [],
        fileType: module.uploadFileType,
        moduleFolderId: module.moduleFolderId
      });
      module.sections.forEach((section, sectionIndex) => {
        mapped.modules[moduleIndex].sectionList.push({
          name: section.name,
          index: sectionIndex + 1,
          fileId: section.fileId ? section.fileId : null
        });
      })
    });
    return mapped;
  }

  public static mapAttachment(entity: any): AttachmentModel[] {
    const attachments: AttachmentModel[] = [];

    entity.attachments.forEach((attachment: any) => {
      const attachmentModel: AttachmentModel = {
        attachmentFolderId: attachment.attachmentFolderId,
        fileName: attachment.name,
        attachmentId: attachment.attachmentId.id
      };
      attachments.push(attachmentModel);
    });

    return attachments;
  }

  public static mapStructure(entity: any): CourseStructure {
    const mapped: CourseStructure = {
      attachments: [],
      originalContent: [],
      modules: []
    }
    if (entity.moduleInfoList !== undefined) {
      if (entity.attachments !== undefined) {
        for (const attachment of entity.attachments) {
          mapped.originalContent.push({
            name: attachment.name,
            id: attachment.id,
            type: attachment.type
          })
        }
      }
      if (entity.additionalFiles !== undefined) {
        for (const attachments of entity.additionalFiles) {
          mapped.attachments.push({
            name: attachments.name,
            id: attachments.id,
            type: attachments.type
          })
        }
      }
      const mappedList: StructureModule[] = [];
      const modules = entity.moduleInfoList.sort((a, b) => a.index - b.index);
      for (const module of modules) {
        mappedList.push({
          name: module.name,
          updatedName: module.name,
          sections: [],
          opened: false,
          uploadFileType: module.fileType,
          moduleFolderId: module.moduleFolderId
        });
        const sectionList = module.sectionInfoList.sort((a, b) => a.index - b.index);
        for (const section of sectionList) {
          mappedList[mappedList.length - 1].sections.push({
            name: section.name,
            fileId: section.fileId === 'empty' ? undefined : section.fileId
          });
        }
      }
      mapped.modules = mappedList;
    }
    return mapped;
  }

  public static mapToOnlineCourseModel(res: any): OnlineCourseModel {
    const item = res.entity;
    const properties = item.properties;
    return {
      id: item.id,
      name: item.title,
      categoryId: item.courseCategoryId,
      description: item.description,
      note: properties.note,
      typeId: item.type.toUpperCase(),
      createdDate: item.createdDate,
      contentId: item.courseContentId,
      credit: item.credit,
      testId: item.testId ? item.testId : null,
      hasTest: item.hasQuiz ? item.hasQuiz : false,
      publishStatus: item.publishStatus,
      publishDate: item.publishDate ? new Date(item.publishDate) : null,
      surveyId: item.assessmentId,
      hasSurvey: item.hasAssessment ? item.hasAssessment : false,
      certificateId: item.certificateId ? item.certificateId : null,
      hasCertificate: item.hasCertificate ? item.hasCertificate : false,
      assignedLearners: item.assignedLearners,
      assignedDepartments: item.assignedDepartments,
      assignedDepartmentNames: Object.values(item.assignedDepartmentNames),
      modifiedDate: item.modifiedDate,
      thumbnailUrl: item.thumbnailUrl,
      author: item.authorId ? item.authorId : ''
    };
  }

  public static mapToOnlineCourses(items: any): OnlineCourseModel[] {
    const courses: OnlineCourseModel[] = [];

    for (const onlineCourse of items) {
      courses.push(this.mapToOnlineCourseTableModel(onlineCourse));
    }

    return courses;
  }

  public static getRole(userRole: string): string {
    switch (userRole) {
      case 'LMS_SUPERVISOR':
        return 'SUPERVISOR';
      case 'LMS_MANAGER':
        return 'MANAGER';
      default:
        return 'EMPLOYEE';
    }
  }

  public static mapToCreateRestModel(onlineCourse: OnlineCourseModel): any {
    return {
      title: onlineCourse.name,
      categoryId: onlineCourse.categoryId,
      type: onlineCourse.typeId,
      credit: onlineCourse.credit,
      description: onlineCourse.description,
      assessmentId: onlineCourse.surveyId,
      certificateId: onlineCourse.certificateId,
      hasAssessment: !!onlineCourse.surveyId,
      hasQuiz: false,
      hasFeedBack: false,
      properties: {},
    };
  }

  public static mapToOnlineCourseTableModel(entity: any): OnlineCourseTableViewModel {
    return {
      id: entity.id,
      categoryId: entity.categoryId,
      name: entity.title,
      typeId: entity.type,
      certificateId: entity.certificateId,
      description: entity.description,
      author: entity.authorId,
      thumbnailUrl: entity.thumbnailUrl,
      publishStatus: entity.publishStatus,
      createdDate: DateFormatter.toISODateString(entity.createdDate),
      modifiedDate: DateFormatter.toISODateString(entity.modifiedDate),
      viewersCount: 0,
      chaptersCount: entity.modulesCount,
      progress: entity.progress,
      enrollmentState: entity.enrollmentState === 'NEW' ? EnrollmentState.NEW : entity.enrollmentState === 'COMPLETED' ?
        EnrollmentState.COMPLETED : EnrollmentState.IN_PROGRESS,
      isReadyToPublish: entity.properties.isReadyToPublish === 'true',
      surveyId: entity.assessmentId,
      authorAndDate: entity.authorId + "\xa0\xa0\xa0\xa0" + DateFormatter.toISODateString(entity.createdDate)
    };
  }

  public static mapToDropdownModel(entity: any): CategoryItem {
    return {
      id: entity.categoryId,
      name: entity.categoryName,
      newCount: 0,
      count: 0
    }
  }

  public static mapToUploadedModel(content: File, entity: any, name: string): StructureModule {
    const mapped: StructureModule = {
      name: 'mapped',
      sections: [],
      updatedName: '',
      opened: true,
      attachmentName: [],
      codecSupported: entity.codecSupported != null ? entity.codecSupported : false,
      absolutePath: entity.absolutePath != null ? entity.absolutePath : ''
    };
    entity.restSections.sort((a, b) => a.index - b.index).filter(item => {
      mapped.sections.push({
        name: name + '-' + item.name,
        fileId: item.fileId,
      });
    });
    mapped.attachmentName.push({name: content.name, id: entity.attachmentFileId, type: content.type});
    return mapped;
  }

  public static mapToUpdateRestModel(onlineCourse: OnlineCourseModel, sendNotification: boolean): any {
    return {
      categoryId: onlineCourse.categoryId,
      type: onlineCourse.typeId,
      description: onlineCourse.description,
      title: onlineCourse.name,
      credit: onlineCourse.credit,
      properties: {},
      hasQuiz: onlineCourse.hasTest,
      hasFeedBack: false,
      hasAssessment: onlineCourse.hasSurvey,
      assessmentId: onlineCourse.surveyId,
      certificateId: onlineCourse.certificateId,
      hasCertificate: onlineCourse.hasCertificate,
      emailSubject: 'Нийтэлсэн сургалтад өөрчлөлт орлоо.',
      templateName: sendNotification ? 'online-course-update-email-template.ftl' : null,
      withThumbnail: !!onlineCourse.thumbnailUrl
    };
  }

  public static courseRestMap(course: OnlineCourseModel): any {
    return {
      categoryId: course.categoryId,
      type: course.typeId,
      description: course.description,
      credit: course.credit,
      title: course.name,
      properties: {},
      hasQuiz: course.hasTest,
      hasAssessment: course.hasSurvey,
      assessmentId: course.surveyId,
      certificateId: course.certificateId,
      hasCertificate: course.hasCertificate
    };
  }

  public static mapToTestModel(entity: any): TestModel {
    const test: TestModel = {
      testId: entity.id,
      testName: entity.name,
      attempt: entity.maxAttempts,
      threshold: entity.thresholdScore,
      questions: []
    };

    const questions: TestQuestion[] = [];
    for (const item of entity.questions) {
      const choices: AnswerChoice[] = item.answers;
      const questionType = item.type === 'SINGLE_CHOICE' ? QuestionTypes.SINGLE_CHOICE : QuestionTypes.MULTIPLE_CHOICE;
      questions.push(new TestQuestion(item.title, choices, questionType));
    }
    test.questions = questions;
    return test;
  }

  public static getTestRestBody(tests: TestModel): any {
    const questions = [];
    for (const testQuestion of tests.questions) {
      const answers = [];
      for (const answer of testQuestion.answers) {
        answers.push({value: answer.value, correct: answer.correct, score: answer.weight});
      }
      questions.push({question: testQuestion.question, answers: answers, questionType: testQuestion.type});
    }
    return {
      quizId: tests.testId,
      questions: questions,
      maxAttempts: tests.attempt,
      thresholdScore: tests.threshold
    };

  }


  public static mapToStatisticDownload(onlineCourses: OnlineCourseStatistic[]): any[] {
    const mapped = [];
    for (const course of onlineCourses) {
      mapped.push({
        courseId: null,
        courseName: null,
        reportData: {
          username: course.username,
          group: course.groupName,
          firstViewDate: course.firstLaunchDate,
          lastViewDate: course.lastLaunchDate,
          viewCount: course.views,
          status: Math.floor(course.progress),
          score: course.score,
          spentTime: course.spentTime,
          spentTimeRatio: course.spentTimeRatio.toolTip,
          spentTimeOnTest: course.spentTimeOnTest,
          certification: course.certificateDate,
          survey: course.doneSurvey
        },
      })
    }
    return mapped;
  }


  public static mapToOnlineCourseStatistics(entity: any): OnlineCourseStatisticBundleModel {
    const statistics: OnlineCourseStatistic[] = [];
    const dashlets: SmallDashletModel[] = [];
    const barChartGreen = 'assets/images/bar-chart-green.png';
    const barChartPurple = 'assets/images/bar-chart-purple.png';
    let launchCount = 0;
    let completionCount = 0;
    let totalDuration = 0;
    let hasDurationCount = 0;
    for (const statistic of entity.analytics) {

      if (statistic.status > 0) {
        launchCount++;
      }
      if (statistic.spentTimeInMilliseconds > 0) {
        hasDurationCount++;
        totalDuration += statistic.spentTimeInMilliseconds;
      }
      if (statistic.status >= 90) {
        completionCount++;
      }

      statistics.push({
        username: statistic.learnerId,
        groupName: statistic.groupName,
        doneSurvey: statistic.survey != null,
        firstLaunchDate: statistic.firstViewDate != null ? statistic.firstViewDate : "00:00:00",
        lastLaunchDate: statistic.lastViewDate != null ? statistic.lastViewDate : "00:00:00",
        score: statistic.score,
        views: statistic.views,
        progress: statistic.status.toFixed(1),
        spentTimeRatio: {
          toolTip: statistic.spentTimeRatio,
          name: TimeToPercentageConverter.getPercentage(statistic.spentTimeRatio)
        },
        spentTime: statistic.spentTime,
        certificateDate: statistic.receivedCertificateDate ? statistic.receivedCertificateDate : '',
        spentTimeOnTest: statistic.spentTimeOnTest != null ? statistic.spentTimeOnTest : "00:00:00"
      });
    }
    totalDuration /= 1000;
    dashlets.push({
      title: 'Сургалттай танилцсан',
      info: launchCount,
      imageSrc: barChartPurple,
      hasDropDown: false,
      navigateLink: null
    })
    dashlets.push({
      title: '90% танилцсан',
      info: completionCount,
      imageSrc: barChartPurple,
      hasDropDown: false,
      navigateLink: null
    })
    dashlets.push({
      title: 'Танилцсан дундаж хугацаа',
      info: totalDuration > 0 ? SecondsToTimeConverterUtil.convertWithoutUnit(Math.floor(totalDuration / hasDurationCount)) : '00:00:00',
      imageSrc: barChartGreen,
      hasDropDown: false,
      navigateLink: null
    })
    statistics.sort((a, b) => {
      return b.progress - a.progress;
    })
    return {statistics: statistics, dashlets: dashlets};
  }

  static mapToGroupNode(entity: any[]): GroupNode[] {
    const groupNode = [];
    entity.forEach(group => {
      groupNode.push({
        parent: group.parent,
        id: group.id,
        name: group.name,
        children: this.mapToGroupNode(group.children),
        indeterminate: group.someChildrenSelected,
        checked: group.currentGroupSelected,
        allChildChecked: group.allChildrenSelected,
      })
    })
    return groupNode;
  }
}
