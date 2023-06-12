/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

export enum QuestionType {
  MULTI_CHOICE = 'MULTI_CHOICE',
  SINGLE_CHOICE = 'SINGLE_CHOICE',
  FILL_IN_BLANK = 'FILL_IN_BLANK'
}

export interface Answer {
  value: string;
}

export class Question {

  private readonly title: string;
  private readonly type: QuestionType;
  private readonly answers: Answer[];
  isRequired: boolean;

  constructor(title: string, answers: Answer[], questionType: QuestionType, isRequired: boolean) {
    this.title = title;
    this.answers = answers;
    this.type = questionType;
    this.isRequired = isRequired;
  }

  getType(): QuestionType {
    return this.type;
  }

  public getTitle(): string {
    return this.title;
  }

  public getAnswers(): Answer[] {
    return this.answers;
  }
}
