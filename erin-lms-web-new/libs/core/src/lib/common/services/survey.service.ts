import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient, HttpParams} from "@angular/common/http";
import {map} from "rxjs/operators";
import {DateFormatter} from "../../../../../shared/src/lib/utilities/date-formatter.util";
import {Survey, SurveyStatus} from "../common.model";

@Injectable({
  providedIn: 'root'
})
export class SurveyService {

  constructor(private httpClient: HttpClient) {
  }

  getSurveys(onlyActive?: boolean, withQuestions?: boolean): Observable<Survey[]> {
    let params = new HttpParams();
    params = params.append('onlyActive', onlyActive ? 'true' : 'false');
    return this.httpClient.get('/lms/assessments', {params}).pipe(map((res: any) => {
      const sorted = res.entity.sort((firstModifiedDate, secondModifiedDate) => {
        const firstDate = new Date(firstModifiedDate.modifiedDate);
        const secondDate = new Date(secondModifiedDate.modifiedDate);
        return (secondDate as any) - (firstDate as any);
      });
      return this.mapToSurveyModel(sorted, withQuestions);
    }));
  }

  private mapToSurveyModel(items: any, withQuestions: boolean): Survey[] {
    const surveys: Survey[] = [];

    if (withQuestions) {
      items = items.filter(item => item.questionCount > 0);
    }

    for (const item of items) {
      surveys.push({
        id: item.id,
        name: item.name,
        author: item.authorId + ' ' + DateFormatter.toISODateString(item.modifiedDate),
        createdDate: DateFormatter.toISODateString(item.createdDate),
        modifiedDate: DateFormatter.toISODateString(item.modifiedDate),
        description: item.description,
        questionCount: item.questionCount,
        status: item.status === 'ACTIVE' ? SurveyStatus.ACTIVE : SurveyStatus.INACTIVE
      });
    }

    return surveys;
  }
}
