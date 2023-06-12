import {AnswerChoice, GroupNode, PublishStatus, QuestionTypes, TestQuestion, TimedCourseModel} from "../../../../../shared/src/lib/shared-model";
import {DateFormatter} from "../../../../../shared/src/lib/utilities/date-formatter.util";
import {FileAttachment, StructureModule} from "../../../../../shared/src/lib/structures/content-structure/content-structure.model";
import {ContentModule} from "../../common/common.model";
import {TimedCourseStructure} from "./timed-course.model";
import {TestModel} from "../../online-course/models/online-course.model";

export class TimedCourseMapper {
  static mapToTimedCourseModel(item: any): TimedCourseModel {
    const properties = item.properties;
    return {
      id: item.id,
      name: item.title,
      author: item.authorId,
      categoryId: item.courseCategory,
      contentId: item.courseContentId,
      description: item.description,
      endDate: properties.endDate ? DateFormatter.toISODateString(new Date(properties.endDate)) : null,
      startDate: DateFormatter.toISODateString(new Date(properties.startDate)),
      keyword: properties.keyWord,
      state: properties.state === undefined ? '' : properties.state.toUpperCase(),
      code: properties.code,
      target: properties.target,
      hasTest: properties.hasTest ? properties.hasTest : false,
      hasFeedBack: properties.hasFeedBack ? properties.hasFeedBack : false,
      enrollments: {
        groups: properties.enrolledGroups ? properties.enrolledGroups : item.userGroupOutput.groups,
        users: item.userGroupOutput ? item.userGroupOutput.users : []
      },
      status: item.status,
      publishStatus: item.publishStatus,
      publishDate: properties.publishDate ? new Date(properties.publishDate) : null,
      modifiedDate: item.createdDate,
      note: item.note,
      department: properties.department
    };
  }

  static mapToUploadedModel(file: File, entity: any, name: string): StructureModule {
    const mapped: StructureModule = {
      name: 'mapped',
      sections: [],
      updatedName: '',
      opened: false,
      attachmentName: [],
    };
    entity.restSections.sort((a, b) => a.index - b.index).filter(item => {
      mapped.sections.push({
        name: name + '-' + item.name,
        fileId: item.fileId,
      });
    });
    mapped.attachmentName.push({name: file.name, id: entity.attachFileId, type: file.type});
    return mapped;
  }

  static mapToStructureRest(contents: FileAttachment[], attachments: FileAttachment[], structure: StructureModule[], id: string, initial?: boolean) {
    const mapped = {
      additionalFiles: [],
      attachments: [],
      modules: [],
      courseId: id,
    };
    contents.forEach(attachment => {
      mapped.attachments.push({
        id: attachment.id,
        name: attachment.name,
        type: this.getFileType(attachment)
      });

    });
    attachments.forEach(attachment => {
      mapped.additionalFiles.push({
        id: attachment.id,
        name: attachment.name,
        type: this.getFileType(attachment)
      });
    })
    structure.forEach((module, key) => {
      let mappedModule;
      if (initial) {
        mappedModule = {
          name: module.updatedName,
          index: key + 1,
          sectionList: [],
          fileType: module.uploadFileType
        }
      } else {
        mappedModule = {
          updatedName: module.updatedName,
          initName: module.name ? module.name : module.updatedName,
          index: key + 1,
          sectionList: [],
          fileAttachmentList: [],
          fileType: module.uploadFileType
        };
      }

      module.sections.forEach((section, key) => {
        mappedModule.sectionList.push({
          name: section.name,
          index: key + 1,
          fileId: section.fileId ? section.fileId : null
        });
      })
      mapped.modules.push(mappedModule);
    })
    return mapped;
  }

  static getFileType(fileAttachment: FileAttachment) {
    if (fileAttachment.type !== undefined) {
      switch (fileAttachment.type) {
        case 'application/vnd.openxmlformats-officedocument.wordprocessingml.document':
          return '.docx';
        case 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet':
          return '.xlsx';
        case 'image/jpeg':
          return '.jpeg';
        case 'image/jpg':
          return '.jpg';
        case 'image/png':
          return '.png';
        case 'image/svg':
          return '.svg';
        case 'video/mp4':
          return '.mp4';
        case 'video/webm':
          return '.webm';
        case 'application/pdf':
          return '.pdf';
        default:
          return fileAttachment.type;
      }
    }
  }

  static mapStructure(entity: any): TimedCourseStructure {
    const mapped: TimedCourseStructure = {
      attachments: [],
      originalContent: [],
      modules: []
    }
    if (entity.modules !== undefined) {
      if (entity.attachments !== undefined) {
        for (const attachment of entity.attachments) {
          mapped.originalContent.push({
            name: attachment.name,
            id: attachment.id,
            type: attachment.type
          });
        }
      }
      if (entity.additionalFiles !== undefined) {
        for (const attachments of entity.additionalFiles) {
          mapped.attachments.push({
            name: attachments.name,
            id: attachments.id,
            type: attachments.type
          });
        }
      }
      const mappedList: StructureModule[] = [];
      const modules = entity.modules.sort((a, b) => a.index - b.index);
      for (const module of modules) {
        mappedList.push({
          name: module.name,
          updatedName: module.name,
          sections: [],
          opened: false,
          uploadFileType: module.fileType
        });
        const sectionList = module.sectionList.sort((a, b) => a.index - b.index);
        for (const section of sectionList) {
          mappedList[mappedList.length - 1].sections.push({
            name: section.name,
            fileId: section.fileId === 'empty' ? undefined : section.fileId === 'empty' ? undefined : section.fileId
          });
        }
      }
      mapped.modules = mappedList;
    }
    return mapped;
  }

  static mapToTestModel(entity: any): TestModel {
    const testModel: TestModel = {
      testId: entity.id,
      testName: entity.name,
      attempt: entity.maxAttempts,
      threshold: entity.thresholdScore,
      questions: []
    };

    const questions: TestQuestion[] = [];
    for (const item of entity.questions) {
      const choices: AnswerChoice[] = item.answers;
      const questionType = QuestionTypes.SINGLE_CHOICE;
      questions.push(new TestQuestion(item.title, choices, questionType));
    }
    testModel.questions = questions;
    return testModel;
  }

  static getTestBody(id: string, test: TestModel): any {
    return {
      courseId: id,
      courseTests: test.questions,
      testId: test.testId,
      maxAttempts: test.attempt,
      thresholdScore: test.threshold
    };
  }

  static mapToAttachment(content: any, entity: any): FileAttachment {
    return {name: content.name, id: entity, type: content.type};
  }

  static mapToTableViewModel(data: any, categoryName): TimedCourseModel[] {
    const mapped: TimedCourseModel[] = [];
    for (const item of data) {
      mapped.push({
        id: item.id,
        name: item.title,
        target: item.properties.target,
        keyword: item.properties.keyWord,
        code: item.properties.code,
        startDate: DateFormatter.toISODateString(item.properties.startDate),
        endDate: DateFormatter.toISODateString(item.properties.endDate),
        createdDate: DateFormatter.toISODateString(item.createdDate),
        status: item.enrollmentState,
        new: item.enrollmentState != null && item.properties.state != 'EXPIRED' ? item.enrollmentState == 'NEW' : null,
        publishStatus: item.publishStatus === 'PUBLISHED' ?
          PublishStatus.PUBLISHED : item.publishStatus === 'UNPUBLISHED' ?
            PublishStatus.UNPUBLISHED : PublishStatus.PENDING,
        state: item.properties.state,
        author: item.authorId,
        categoryName: categoryName,
        categoryId: item.courseCategory,
        isReadyToPublish: item.isReadyToPublish,
        description: item.description,
        hasTest: item.properties.hasTest ? item.properties.hasTest : false,
        hasFeedBack: item.properties.hasFeedBack ? item.properties.hasFeedBack : false,
        enrollments: {
          groups: item.userGroupOutput ? item.userGroupOutput.groups : [],
          users: item.userGroupOutput ? item.userGroupOutput.users : []
        },
        publishDate: item.properties.publishDate ? new Date(item.properties.publishDate) : null,
        note: item.note
      });
    }
    return mapped;
  }

  static mapContent(entity: any): ContentModule[] {
    const mappedList: ContentModule[] = [];
    if (entity !== undefined) {
      for (const module of entity) {
        mappedList.push({
          documentPaths: [],
          moduleIndex: module.index
        });
        for (const section of module.paths) {
          mappedList[mappedList.length - 1].documentPaths.push({
            sectionPath: section.path === undefined ? '' : section.path,
            sectionIndex: section.index
          });
        }
      }
    }
    return mappedList;
  }

  static getPublishedDate(publishedDate: any): number {
    if (publishedDate.properties !== undefined && publishedDate.properties.publishedDate !== undefined) {
      return publishedDate.properties.publishedDate;
    }
    return 0;
  }


  static mapToCreateRestModel(course: TimedCourseModel, type: string): any {
    return {
      categoryId: course.categoryId,
      courseDetail: {
        courseProperties: {
          target: course.target,
          keyWord: course.keyword,
          code: course.code,
          state: course.state,
          startDate: course.startDate,
          endDate: course.endDate == '-' ? "" : course.endDate,
          hasTest: course.hasTest,
          hasFeedBack: course.hasFeedBack,
          department: course.department,
        },
        userGroup: course.enrollments,
        description: course.description,
        note: course.note,
        title: type === 'clone' ? '[ХУУЛСАН] ' + course.name : course.name
      }
    };
  }

  static mapToGroupNode(entities: any[]): GroupNode[] {
    return entities.map(entity => ({
      parent: entity.parent,
      id: entity.id,
      name: entity.name,
      children: this.mapToGroupNode(entity.children),
      indeterminate: entity.someChildrenSelected,
      checked: entity.currentGroupSelected,
      allChildChecked: entity.allChildrenSelected,
    } as GroupNode))
  }
}
