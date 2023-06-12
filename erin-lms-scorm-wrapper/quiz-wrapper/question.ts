/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

import {QuestionType} from '../question';

export interface Answer {
  value: string;
  isCorrect: boolean;
  score: number;
}

export class Question {
  private readonly title: string;
  private readonly answers: Answer[];
  private selectedAnswers: Answer[];
  public type: QuestionType;

  private overallScore = 0;
  private score = 0;

  constructor(title: string, answers: Answer[], questionType: QuestionType) {
    this.title = title;
    this.answers = answers;
    this.type = questionType;
    this.answers.forEach(answer => {
      if (answer.isCorrect) {
        this.overallScore += 1;
      }
    });
  }

  public getTitle(): string {
    return this.title;
  }

  public getAnswers(): Answer[] {
    return this.answers;
  }


}
