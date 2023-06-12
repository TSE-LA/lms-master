export interface Survey {
  id: string;
  name: string;
  questionCount: number;
  contentId: string;
  status: SurveyStatus;
  createdDate: string;
  modifiedDate: string;
  admin: string;
  summary: string;
}

export interface CreateSurveyModel {
  name: string,
  summary: string,
}

export interface Quiz {
  quizId: string;
  questions: QuizQuestion[];
}

export class QuizQuestion {
  question: string;
  questionType: QuestionType;
  required?: boolean;
  answers: Answer[] = [
    {value: '', isCorrect: false, score: 1},
    {value: '', isCorrect: false, score: 1}
  ];

  constructor(question: string, questionType: QuestionType, answers?: Answer[], required?: boolean) {
    this.question = question;
    this.questionType = questionType;
    this.required = required;
    if (this.questionType === QuestionType.OPEN) {
      this.answers = [{value: '', isCorrect: true, score: 0}];
    } else {
      if (answers && answers.length > 1) {
        this.answers = answers;
      }
    }
  }

  hasCorrectAnswer(): boolean {
    if (this.questionType === QuestionType.MULTIPLE_CHOICE) {
      return true;
    }

    for (const choice of this.answers) {
      if (choice.isCorrect) {
        return true;
      }
    }
    return false;
  }

  hasValidAnswers(): boolean {
    if (this.questionType === QuestionType.OPEN) {
      return true;
    }

    for (const answer of this.answers) {
      if (answer.value.length < 1) {
        return false;
      }
    }
    return true;
  }

  hasDuplicatedAnswers(): boolean {
    if (this.questionType === QuestionType.OPEN) {
      return false;
    }

    const uniqueAnswers = this.answers.reduce((acc, curr) => {
      if (!acc.find(answer => answer.value === curr.value)) {
        acc.push(curr);
      }
      return acc;
    }, []);

    return this.answers.length !== uniqueAnswers.length;
  }
}

export interface Answer {
  value: string;
  isCorrect: boolean;
  score: number;
}

export interface CreateAssessmentModel {
  name: string;
  description: string;
}

export enum SurveyStatus {
  ACTIVE = "Идэвхтэй",
  INACTIVE = 'Идэвхгүй',
}

export enum QuestionType {
  OPEN,
  CLOSED,
  MULTIPLE_CHOICE
}

