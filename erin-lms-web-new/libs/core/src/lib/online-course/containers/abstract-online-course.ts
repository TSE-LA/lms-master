
import {OnlineCourseSandboxService} from "../online-course-sandbox.service";
import {OnlineCourseModel} from "../models/online-course.model";

export abstract class AbstractOnlineCourse {
  static readonly DEFAULT_ERROR_MESSAGE = 'Couldn\'t load page, try reloading page';

  onlineCourseId: string;
  onlineCourseTitle = '';
  onlineCourseDescription = '';
  errorMessage = '';
  onlineCourseProperties: OnlineCourseModel;

  protected constructor(protected sb: OnlineCourseSandboxService) {
  }

  protected loadPage(): void {
    this.sb.getOnlineCourseById(this.onlineCourseId).subscribe(res => {
      this.onlineCourseProperties = res;
      this.onlineCourseTitle = this.onlineCourseProperties.name;
      this.onlineCourseDescription = this.onlineCourseProperties.description;
      this.propertiesLoaded();
    })
  }

  abstract propertiesLoaded(): void;
}
