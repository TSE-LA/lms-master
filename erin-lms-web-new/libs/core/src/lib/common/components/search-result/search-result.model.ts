export interface TimedCourseSearchResultModel {
  id: string;
  title: string;
  category: string;
  state: string;
  target: string;
  keyword: string;
  code: string;
  date: string;
  author: string;
  published: boolean;
  enrollmentState: string;
  createdDate: string;
}

export interface CourseSearchResultModel {
  id: string;
  title: string;
  category: string;
  type: string;
  date: string;
  author: string;
  enrollmentState: string;
  published: boolean;
  createdDate: string;
  categoryId: string;
}
