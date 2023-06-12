import {RandomQuestion} from "../../../../../shared/src/lib/shared-model";

export enum ExamType {
  OFFICIAL = "OFFICIAL",
  SELF_TRAINING = "SELF_TRAINING"
}

export enum ExamState {
  NEW = 'NEW',
  PUBLISHED = 'PUBLISHED',
  STARTED = 'STARTED',
  FINISHED = 'FINISHED',
  PENDING = 'PENDING'
}

export interface RandomQuestionModel {
  groupId: string;
  categoryId: string;
  difficultyLevel: number;
  amount: number;
}

export enum AnswerResultType {
  SHOW_NOTHING = "SHOW_NOTHING",
  SHOW_WRONG = "SHOW_WRONG",
  SHOW_CORRECT = "SHOW_CORRECT",
  AFTER_EXAM = "AFTER_EXAM"
}

export interface ExamConfigModel {
  questionIds: Set<string>;
  randomQuestions: RandomQuestion[];
  showAnswerResult: AnswerResultType;
  shuffleQuestion: boolean;
  shuffleAnswer: boolean;
  questionsPerPage: boolean;
  lockSystem?: boolean;
  threshold: number;
  maxScore?: number;
  score?: number;
  maxAttempt?: number;
  spentTime?: number;
  scorePercentage?: string;
  attempt: number;
  certificateId: string;
  startDate: string;
  endDate: string;
  startTime: string;
  endTime: string;
  duration: number;
  autoStart: boolean;
}

export interface ExamModel {
  id: string;
  name: string;
  author?: string,
  description: string;
  categoryId: string;
  groupId: string;
  type: ExamType;
  config: ExamConfigModel;
  notPublished: boolean;
  isEditable: boolean;
  publishDate: string;
  publishTime: string;
  sendEmail: boolean;
  sendSms: boolean;
  mailText: string;
  smsText: string;
  enrolledLearners: string[];
  enrolledGroups: string[];
  examState?: string;
  publishStatus?: string;
  firstLaunch?: boolean;
}

export interface ExamBank {
  id: string;
  name: string;
  duration: number;
  start: string;
  startDate: string;
  startTime: string;
  end: string;
  endDate: string;
  endTime: string;
  questionCount: string;
  passingScore: number;
  enrolled: number;
  maxScore: number;
  modified: string;
  examStatus: string;
  notPublished: boolean;
}

export interface LearnerExamModel {
  id: string;
  name: string;
  description: string;
  thresholdScore: number;
  startDate: string;
  endDate: string;
  startTime: string;
  duration: number;
  author: string;
  examState: string;
  certificateId: string;
  runtimeData: ExamRuntimeModel;
}

export interface ExamLaunchModel {
  title: string;
  description: string;
  status: string,
  author: string;
  maxScore: string;
  maxAttempt: number;
  duration: string;
  score: number;
  scorePercentage: string;
  remainingAttempt: number;
  spentTime: string;
  certificateId: string;
  thresholdScore: number;
  sawConfetti?: boolean;
  ongoing: boolean;
}

export interface ExamRuntimeModel {
  learnerId: string;
  maxScore: number;
  maxAttempt: number;
  maxDuration: string;
  examInteraction: ExamInteractionModel;
}

export interface ExamInteractionModel {
  initialLaunch: string;
  lastLaunch: string;
  spentTime: string;
  attempt: number;
  score: number;
  successStatus: string;
  completionStatus: string;
  bookmark: string;
  questionAndAnswers: QuestionAndAnswerModel[];
}

export interface QuestionAndAnswerModel {
  questionId: string;
  questionValue: string;
  questionIndex: number;
  totalScore: number;
  learnerAnswer: AnswerModel[];
}

export interface AnswerModel {
  id: string;
  value: string;
  weight: number;
  state: AnswerState;
  selected?: boolean;
}

export enum AnswerState {
  NEUTRAL = 'transparent',
  SELECTED = '#3B86FF',
  INCORRECT = '#FF6565',
  CORRECT = '#1ABC9C',
}

export interface StartExamModel {
  title: string;
  remainingTime: number;
  durationInSeconds: number;
  questions: LearnerQuestionModel[];
}

export interface ExamReportModel {
  learnerName: string;
  score: string; //learnerScore/examMaxScore
  examThresholdScore: number;
  learnerSpentTime: string;
  attemptCount: string; //learnerAttemptCount/examMaxAttemptCount
  learnerGradeInPercentage: string;
  learnerPassStatus: string
}

export interface ExamReportRestModel {
  learnerName: string;
  examMaxScore: number;
  learnerFinalScore: number;
  examThresholdScore: number;
  learnerSpentTime: string;
  examMaxAttemptCount: number;
  learnerAttemptCount: number;
  learnerGradeInPercentage: number;
  learnerPassStatus: boolean;
}

export interface LearnerExamListModel {
  id: string;
  name: string;
  categoryId: string;
  author: string;
  startDate: string;
  hasCertificate: string;
  status: string;
  startTime: string;
  endTime: string;
  endDate: string;
  thresholdScore: number;
  upcoming: boolean;
}

export interface LearnerQuestionModel {
  id: string;
  index: number;
  value: string;
  imagePath: string;
  type: string;
  selected?: boolean;
  selectedAnswers?: number[];
  answers: LearnerAnswerModel[];
}

export interface LearnerAnswerModel {
  index: number;
  value: string;
  selected?: boolean;
}
