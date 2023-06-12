/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

import {BaseScormWrapper} from '../base-scorm-wrapper';
import {Question, QuestionType} from '../question';

export class UnitelQuestionnaireWrapper extends BaseScormWrapper {
  // @ts-ignore
  private answer = " ";

  init(): void {
    this.getApi();
    if (this.API !== undefined) {
      this.initialized = true;
      this.setValue("cmi.exit", "normal");
    }

    this.loadAssetData('questionnaire-data.json', (response) => {
      const data = JSON.parse(response);
      const datum = data["data"][0];
      const question = new Question(datum.question, null, QuestionType.FILL_IN_BLANK, false);
      this.createQuestionFrame(question);
      this.setButtonEnability();
    });

    this.setValue("cmi.progress_measure", "100.0");
  }

  end(): void {
  }

  submit(): void {
    this.setValue("cmi.completion_status", "completed");
    this.setValue("cmi.comments_from_learner.1.comment", this.answer.toString());
  }

  private createQuestionFrame(question: Question): void {
    const section = document.createElement("section");
    section.setAttribute("class", "section--center mdl-grid mdl-grid--no-spacing mdl-shadow--2dp");
    const card = document.createElement("div");
    card.setAttribute("class", "mdl-card mdl-cell mdl-cell--12-col");
    const questionContent = document.createElement("div");
    questionContent.setAttribute("class", "mdl-card__supporting-text mdl-grid mdl-grid--no-spacing");

    const questionTitle = document.createElement("h5");
    questionTitle.setAttribute("class", "mdl-cell mdl-cell--12-col");
    questionTitle.innerText = question.getTitle();

    questionContent.appendChild(questionTitle);

    this.createQuestionnaireBox(questionContent);

    card.appendChild(questionContent);
    section.appendChild(card);
    document.getElementById("testFrame").appendChild(section);
  }

  private createQuestionnaireBox(questionContent: HTMLElement): void {
    const answerContainer = document.createElement("div");
    answerContainer.setAttribute("class", "section__text mdl-cell mdl-cell--10-col-desktop mdl-cell--6-col-tablet mdl-cell--3-col-phone");
    answerContainer.style.marginBottom = '5px';
    const label = document.createElement("label");
    const input = document.createElement("textarea");
    input.setAttribute("type", "text");
    input.setAttribute("class", "with-gap");
    input.setAttribute("maxlength", "170");
    this.getApi();
    if (this.API !== undefined) {
      this.initialized = true;
    }
    const prevValue = this.getValue("cmi.comments_from_learner.1.comment");
    input.value = (prevValue == 'unknown') ? '' : prevValue;
    input.addEventListener("input", (event) => {
      this.answer = (event.target as HTMLTextAreaElement).value;
    });

    const value = document.createElement("span");
    value.style.fontSize = '18px';
    label.appendChild(input);
    label.appendChild(value);
    answerContainer.appendChild(label);
    questionContent.appendChild(answerContainer);
  }

  private setButtonEnability() {
    const status = this.getValue("cmi.completion_status");
    if (status == "completed") {
      (document.getElementById("#submit-button") as HTMLButtonElement).disabled = true;
    }
  }
}
