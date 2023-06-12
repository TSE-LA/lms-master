import {
  AnswerModel,
  AnswerState,
  ExamInteractionModel,
  ExamRuntimeModel,
  LearnerExamModel,
  QuestionAndAnswerModel
} from "../../../../core/src/lib/exam/model/exam.model";
import {ExamDetailKey} from "./model";

export const DUMMY_LEARNER_ANSWER: AnswerModel[] = [
  {id: '1', value: 'halo', weight: 10, state: AnswerState.NEUTRAL},
  {id: '2', value: 'helo', weight: 10, state: AnswerState.NEUTRAL},
  {id: '3', value: 'hqlo', weight: 10, state: AnswerState.NEUTRAL},
]
export const DUMMY_QUESTION_ANSWERS: QuestionAndAnswerModel[] =
  [
    {
      questionId: '55',
      questionIndex: 1,
      questionValue: 'hello',
      totalScore: 10,
      learnerAnswer: DUMMY_LEARNER_ANSWER
    },
    {
      questionId: '55',
      questionIndex: 2,
      questionValue: 'hello',
      totalScore: 10,
      learnerAnswer: DUMMY_LEARNER_ANSWER
    },
    {
      questionId: '55',
      questionIndex: 3,
      questionValue: 'hello',
      totalScore: 10,
      learnerAnswer: DUMMY_LEARNER_ANSWER
    },
    {
      questionId: '55',
      questionIndex: 4,
      questionValue: 'hello',
      totalScore: 10,
      learnerAnswer: DUMMY_LEARNER_ANSWER
    },
    {
      questionId: '55',
      questionIndex: 5,
      questionValue: 'hello',
      totalScore: 10,
      learnerAnswer: DUMMY_LEARNER_ANSWER
    },
    {
      questionId: '55',
      questionIndex: 6,
      questionValue: 'hello',
      totalScore: 10,
      learnerAnswer: DUMMY_LEARNER_ANSWER
    },
    {
      questionId: '55',
      questionIndex: 7,
      questionValue: 'hello',
      totalScore: 10,
      learnerAnswer: DUMMY_LEARNER_ANSWER
    },
    {
      questionId: '55',
      questionIndex: 8,
      questionValue: 'hello',
      totalScore: 10,
      learnerAnswer: DUMMY_LEARNER_ANSWER
    },
    {
      questionId: '55',
      questionIndex: 9,
      questionValue: 'hello',
      totalScore: 10,
      learnerAnswer: DUMMY_LEARNER_ANSWER
    },
    {
      questionId: '55',
      questionIndex: 10,
      questionValue: 'hello',
      totalScore: 10,
      learnerAnswer: DUMMY_LEARNER_ANSWER
    },
    {
      questionId: '55',
      questionIndex: 11,
      questionValue: 'hello',
      totalScore: 10,
      learnerAnswer: DUMMY_LEARNER_ANSWER
    },
    {
      questionId: '55',
      questionIndex: 12,
      questionValue: 'hello',
      totalScore: 10,
      learnerAnswer: DUMMY_LEARNER_ANSWER
    },
    {
      questionId: '55',
      questionIndex: 13,
      questionValue: 'hello',
      totalScore: 10,
      learnerAnswer: DUMMY_LEARNER_ANSWER
    },
    {
      questionId: '55',
      questionIndex: 14,
      questionValue: 'hello',
      totalScore: 10,
      learnerAnswer: DUMMY_LEARNER_ANSWER
    },
    {
      questionId: '55',
      questionIndex: 15,
      questionValue: 'hello',
      totalScore: 10,
      learnerAnswer: DUMMY_LEARNER_ANSWER
    },
    {
      questionId: '55',
      questionIndex: 16,
      questionValue: 'hello',
      totalScore: 10,
      learnerAnswer: DUMMY_LEARNER_ANSWER
    },
    {
      questionId: '55',
      questionIndex: 17,
      questionValue: 'hello',
      totalScore: 10,
      learnerAnswer: DUMMY_LEARNER_ANSWER
    },
    {
      questionId: '55',
      questionIndex: 18,
      questionValue: 'hello',
      totalScore: 10,
      learnerAnswer: DUMMY_LEARNER_ANSWER
    },
    {
      questionId: '55',
      questionIndex: 19,
      questionValue: 'hello',
      totalScore: 10,
      learnerAnswer: DUMMY_LEARNER_ANSWER
    },
    {
      questionId: '55',
      questionIndex: 20,
      questionValue: 'hello',
      totalScore: 10,
      learnerAnswer: DUMMY_LEARNER_ANSWER
    },
    {
      questionId: '55',
      questionIndex: 21,
      questionValue: 'hello',
      totalScore: 10,
      learnerAnswer: DUMMY_LEARNER_ANSWER
    },
    {
      questionId: '55',
      questionIndex: 22,
      questionValue: 'hello',
      totalScore: 10,
      learnerAnswer: DUMMY_LEARNER_ANSWER
    }
  ]

export const DUMMY_EXAM_INTERACTION: ExamInteractionModel = {
  initialLaunch: '2021-12-02',
  lastLaunch: '2021-12-15',
  spentTime: '120 мин',
  attempt: 5,
  score: 115,
  successStatus: 'SUCCESS',
  completionStatus: 'COMPLETED',
  bookmark: 'i dunno',
  questionAndAnswers: DUMMY_QUESTION_ANSWERS
}
export const DUMMY_RUNTIME_DATA: ExamRuntimeModel = {
  maxScore: 150,
  learnerId: '',
  maxAttempt: 15,
  maxDuration: '150 мин',
  examInteraction: DUMMY_EXAM_INTERACTION
}
export const DUMMY_EXAM: LearnerExamModel = {
  id: '123',
  name: 'Улирлын эцсийн шалгалт',
  author: 'Oresama',
  description: 'Тухайн улиралд орсон хичээлүүдийг хэр зэрэг сурсан эсэхийг шалгана',
  thresholdScore: 95,
  startDate: '2021-12-01',
  endDate: '2021-12-31',
  startTime: '2021-12-05',
  examState: 'started',
  certificateId: '144',
  duration: 150,
  runtimeData: DUMMY_RUNTIME_DATA
}
export const EXAM_PROPERTIES_ROW: ExamDetailKey[] = [
  {name: 'Багш'},
  {name: 'Нийт оноо'},
  {name: 'Босго оноо'},
  {name: 'Оролдлогын тоо'},
  {name: 'Үргэлжлэх хугацаа'},
];

export const COMPLETED_EXAM_ROW = [
  {name: 'Авсан оноо', icon: 'emoji_events', color: 'gold'},
  {name: 'Онооны хувь', icon: 'local_fire_department', color: 'burn'},
  {name: 'Үлдсэн оролдлого', icon: 'restore', color: 'primary'},
  {name: 'Зарцуулсан хугацаа', icon: 'hourglass_top', color: 'secondary'},
  {name: 'Сертификат', icon: 'verified', color: 'silver'},
]
