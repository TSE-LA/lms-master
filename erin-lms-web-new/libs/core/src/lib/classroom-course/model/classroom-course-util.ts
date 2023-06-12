import {ScoModel} from "../../scorm/model/runtime-data.model";
import {CourseProgress} from "../../common/common.model";

export class ClassroomCourseUtil {
  public static collectProgressData(scos: ScoModel[]): CourseProgress[] {
    const result: CourseProgress[] = [];

    for (const sco of scos) {
      result.push({
        moduleName: sco.scoName,
        progress: parseFloat(sco.runtimeData.get('cmi.progress_measure').data)
      });
    }
    return result;
  }

  public static getLaunchUrl(path: string): string {
    return '/alfresco/' + path;
  }
}
