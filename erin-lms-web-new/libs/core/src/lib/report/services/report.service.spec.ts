import {ReportService} from './report.service';
import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {TimedCourseReport} from '../model/report.model';

describe('ReportService', () => {
  let service: ReportService;
  let httpMock;
  const snackbarSpyObj = jasmine.createSpyObj('snackbar', ['success', 'error']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [{ useValue: snackbarSpyObj},
        {provide: "baseUrl", useValue: {}}, {provide: "constants", useValue: {}}]
    });
    service = TestBed.get(ReportService);
    httpMock = TestBed.get(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should map to promotion report model', () => {
    const testResponse = [
      {
        courseId: '-id-',
        courseName: 'Hello, World',
        authorName: 'Arthur Conan Doyle',
        courseCreatedDate: '2019-02-20T03:17:03.700Z',
        courseProperties: {
          state: 'a',
          code: '123',
          hasTest: true
        },
        reportData: {
          feedback: 2,
          score: 1,
          questionsCount: 2,
          views: 3,
          status: 100,
          totalEnrollment: 1
        }
      }
    ];

    const expected: TimedCourseReport[] = [
      {
        id: '-id-',
        code: '123',
        name: 'Hello, World',
        createdDate: '2019-02-20',
        author: 'Arthur Conan Doyle',
        progress: 100,
        totalEnrollment: 1,
        category: undefined,
        state: 'a',
        views: 3,
        hasTest: true,
        questions: 2,
        score: 1,
        feedbackCount: 2
      },
    ];

    service.getTimedCourseReport(new Date(), new Date(), 'Мобайл', 'a').subscribe(res => {
      expect(res).toEqual(expected);
    });

    const httpRequest = httpMock.expectOne({method: 'GET'});
    httpRequest.flush({entity: testResponse});
  });
});
