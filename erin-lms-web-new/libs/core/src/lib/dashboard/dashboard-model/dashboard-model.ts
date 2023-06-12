export interface PublishedCourse {
  categoryName: string;
  categoryId: string;
  count: number;
}

export enum CourseType {
  MANAGER = 'MANAGER',
  EMPLOYEE = 'EMPLOYEE',
  SUPERVISOR = 'SUPERVISOR'
}

export interface PublishedCourseCountData {
  type: CourseType;
  courseCountByCategory: PublishedCourse[];
  totalCount: number;
}


export interface ActivityDashletData {
  learnerCount: number,
  completedLearnerCount: number,
  averageSpentTime: string,
  averageViewPercentage?: string,
  completedEmployeeCount?: number,
  completedManagerCount?: number
}

export interface DetailedLearnerActivity {
  username: string,
  groupPath: string,
  role: string,
  progress: number,
  userHistory:string,
}

export interface LearnerSuccessDashletModel {
  overallPercentage: string;
  difference: string;
  totalScore: number;
  groupTotalScore: number;
  up: boolean;
  learnerSuccesses?: LearnerSuccessesMonthlyData[];
}

export interface LearnerSuccessesMonthlyData {
  learnerScore: number;
  groupScore: number;
  month: string;
}
