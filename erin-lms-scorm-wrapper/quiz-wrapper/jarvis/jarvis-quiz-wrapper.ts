/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */


import {BaseScormWrapper} from "../../base-scorm-wrapper";
import {Answer, Question} from "../question";
import {QuestionType} from "../../question";

export class JarvisQuizWrapper extends BaseScormWrapper {
  private overallScore = 0;
  private thresholdScore: number;
  private remainingAttempts: number;
  private currentScore: number;
  private noAttemptsLeft: boolean;
  // @ts-ignore
  private selectedAnswers: Map<number, Answer> = new Map<number, Answer>();
  // @ts-ignore
  private selectedMultipleChoiceAnswers: Map<number, Answer[]> = new Map<number, Answer[]>();
  private passed: boolean;

  init(): void {
    this.getApi();
    if (this.API !== undefined) {
      this.initialized = true;
      this.setValue("cmi.exit", "normal");
    } else {
      this.fail("Could not initialize communication with the LMS");
      return;
    }

    this.loadAssetData('quiz-data.json', (response) => {
      //@ts-ignore
      const loader = new ldBar('#ld');
      const data = JSON.parse(response);
      const interactionsCount = parseInt(this.getValue('cmi.interactions._count'), 10);
      const currentScoreValue = this.getValue('cmi.score.raw') === 'unknown' ? '0' : this.getValue('cmi.score.raw');
      this.currentScore = parseInt(currentScoreValue, 10);
      this.remainingAttempts = data["maxAttempts"] - interactionsCount;
      this.thresholdScore = data["thresholdScore"];
      this.passed = this.currentScore >= this.thresholdScore;
      this.setValue('cmi.completion_threshold', this.remainingAttempts < 0 ? '0' : this.remainingAttempts.toString());

      let remainingAttemptsValue;
      if (this.remainingAttempts <= 0) {
        this.noAttemptsLeft = true;
        remainingAttemptsValue = 0;
        const submitButton = document.getElementById('submit-button');
        submitButton.parentNode.removeChild(submitButton);
      } else {
        remainingAttemptsValue = this.remainingAttempts;
      }

      document.getElementById('current-score-value').innerText = this.currentScore.toString();
      document.getElementById('attempts-value').innerText = remainingAttemptsValue;
      document.getElementById('threshold-score-value').innerText = this.thresholdScore.toString();

      if (this.noAttemptsLeft) {
        this.createNoAttemptsPage();
      } else {
        for (let index = 0; index < data["data"].length; index++) {
          const datum = data["data"][index];
          const answers: Answer[] = [];
          datum.answers.forEach(answer => {
            answers.push({
              value: answer.value,
              isCorrect: answer.isCorrect,
              score: answer.score
            });
            if (answer.isCorrect) {
              this.overallScore += answer.score;
            }
          });

          const question = new Question(datum.question, answers, datum.questionType);
          this.createQuestionFrame(question, index + 1);
          const loaderProgress = (index + 1) / (data["data"].length) * 100;
          loader.set(loaderProgress);
        }
      }
      const loaderElem = document.getElementById('loader');
      loaderElem.parentNode.removeChild(loaderElem);
    });
  }

  end(): void {
  }

  submit(): void {
    let score = 0;
    this.selectedAnswers.forEach((value: Answer, key: number) => {
      if (value.isCorrect) {
        score += value.score;
      }
    });

    this.selectedMultipleChoiceAnswers.forEach((answers: Answer[], key: number) => {
      let multipleChoiceScore = 0;
      for (const answer of answers) {
        if (answer.isCorrect) {
          multipleChoiceScore += answer.score;
        } else {
          multipleChoiceScore -= answer.score;
        }
      }
      score += multipleChoiceScore <= 0 ? 0 : multipleChoiceScore;
    });

    this.currentScore = score > this.currentScore ? score : this.currentScore;

    if (this.remainingAttempts === 1 && this.currentScore < this.thresholdScore) {
      this.setValue("cmi.success_status", "failed");
    } else if (score >= this.thresholdScore) {
      this.setValue("cmi.success_status", "passed");
    }
    this.setValue('cmi.suspend_data', score.toString());
    this.setValue('cmi.score.max', this.overallScore.toString());
    this.setValue("cmi.score.raw", this.currentScore.toString());
    this.setValue("cmi.progress_measure", "100.0");
    this.setValue("cmi.completion_status", "completed");
  }

  private createQuestionFrame(question: Question, questionNumber: number): void {
    const section = document.createElement("section");
    section.setAttribute("class", "section--center mdl-grid mdl-grid--no-spacing mdl-shadow--2dp");
    const card = document.createElement("div");
    card.setAttribute("class", "mdl-card mdl-cell mdl-cell--12-col");
    const questionContent = document.createElement("div");
    questionContent.setAttribute("class", "mdl-card__supporting-text mdl-grid mdl-grid--no-spacing");

    const questionTitle = document.createElement("h5");
    questionTitle.setAttribute("class", "mdl-cell mdl-cell--12-col title-size");
    questionTitle.innerText = questionNumber + '. ' + question.getTitle();

    questionContent.appendChild(questionTitle);

    const answers = question.getAnswers();

    if (question.type === QuestionType.SINGLE_CHOICE) {
      for (let index = 0; index < answers.length; index++) {
        this.createAnswerRadioButton(answers[index], questionContent, questionNumber);
      }
    } else {
      for (let index = 0; index < answers.length; index++) {
        this.createAnswerCheckbox(answers[index], questionContent, questionNumber);
      }
    }
    card.appendChild(questionContent);
    section.appendChild(card);
    document.getElementById("quizFrame").appendChild(section);
  }

  private createAnswerRadioButton(answer: Answer, questionContent: HTMLElement, questionNumber: number): void {
    const answerContainer = document.createElement("div");
    answerContainer.setAttribute("class", "section__text mdl-cell mdl-cell--10-col-desktop mdl-cell--6-col-tablet mdl-cell--3-col-phone flex");
    answerContainer.style.marginBottom = '5px';
    const label = document.createElement("label");
    const input = document.createElement("input");
    input.setAttribute("type", "radio");
    input.setAttribute("class", "with-gap");
    input.setAttribute("name", questionNumber.toString());

    input.checked = this.noAttemptsLeft && answer.isCorrect;
    input.disabled = this.noAttemptsLeft && !answer.isCorrect;

    input.addEventListener("change", () => {
      this.selectedAnswers.set(questionNumber, answer);
    });

    const value = document.createElement("span");
    value.innerText = answer.value;
    value.style.fontSize = '16px';
    label.appendChild(input);
    label.appendChild(value);
    answerContainer.appendChild(label);

    if (this.noAttemptsLeft && answer.isCorrect) {
      const correctAnswerContainer = document.createElement("div");
      correctAnswerContainer.setAttribute("class", "correct-answer");
      const icon = document.createElement("i");
      icon.setAttribute("class", "material-icons correct");
      icon.innerText = 'checked';
      correctAnswerContainer.appendChild(icon);
      answerContainer.appendChild(correctAnswerContainer);
    }

    questionContent.appendChild(answerContainer);
  }

  private createAnswerCheckbox(answer: Answer, questionContent: HTMLElement, questionNumber: number): void {
    const answerContainer = document.createElement("div");
    answerContainer.setAttribute("class", "section__text mdl-cell mdl-cell--10-col-desktop mdl-cell--6-col-tablet mdl-cell--3-col-phone flex");
    answerContainer.style.marginBottom = '5px';
    const label = document.createElement("label");
    const input = document.createElement("input");
    input.setAttribute("type", "checkbox");
    input.setAttribute("class", "with-gap");
    input.setAttribute("name", questionNumber.toString());

    if (this.selectedMultipleChoiceAnswers.get(questionNumber) != null &&
      this.selectedMultipleChoiceAnswers.get(questionNumber).find(selected => selected.value === answer.value) != null) {
      input.checked = true;
    }

    input.addEventListener("change", e => {
      let isSelected: boolean = false;
      // @ts-ignore
      if (e.target.checked) {
        // @ts-ignore
        const selectedAnswers = this.selectedMultipleChoiceAnswers.get(questionNumber);
        if (selectedAnswers == null || selectedAnswers === []) {
          this.selectedMultipleChoiceAnswers.set(questionNumber, []);
          this.selectedMultipleChoiceAnswers.get(questionNumber).push(answer);
        } else {
          for (const selectedAnswer of selectedAnswers) {
            if (selectedAnswer && selectedAnswer.value === answer.value) {
              isSelected = true;
              break;
            }
          }

          if (isSelected === false) {
            this.selectedMultipleChoiceAnswers.get(questionNumber).push(answer);
          }
        }
      } else {
        const selectedAnswers = this.selectedMultipleChoiceAnswers.get(questionNumber);
        if (selectedAnswers != null) {
          for (let index = 0; index < selectedAnswers.length; index++) {
            if (selectedAnswers[index].value === answer.value) {
              this.selectedMultipleChoiceAnswers.get(questionNumber).splice(index, 1);
            }
          }
        }
      }
    });
    const value = document.createElement("span");
    value.innerText = answer.value;
    value.style.fontSize = '18px';
    label.appendChild(input);
    label.appendChild(value);
    answerContainer.appendChild(label);

    if (this.noAttemptsLeft && answer.isCorrect) {
      const correctAnswerContainer = document.createElement("div");
      correctAnswerContainer.setAttribute("class", "correct-answer");
      const icon = document.createElement("i");
      icon.setAttribute("class", "material-icons correct");
      icon.innerText = 'checked';
      correctAnswerContainer.appendChild(icon);
      answerContainer.appendChild(correctAnswerContainer);
    }

    questionContent.appendChild(answerContainer);
  }

  private createNoAttemptsPage() {
    const section = document.createElement("section");
    section.setAttribute("class", "section--center mdl-grid mdl-grid--no-spacing mdl-shadow--2dp");
    const card = document.createElement("div");
    card.setAttribute("class", "mdl-card mdl-cell mdl-cell--12-col");
    const noAttemptsLeftFrame = document.createElement("div");
    noAttemptsLeftFrame.setAttribute("class", "mdl-card__supporting-text mdl-grid mdl-grid--no-spacing");
    noAttemptsLeftFrame.setAttribute("style", "justify-content: center;");
    let imageSrc;
    let passValue;
    if (this.passed) {
      imageSrc = "check-mark.png";
      passValue = "ТЭНЦСЭН";
    } else {
      imageSrc = "x-mark.png";
      passValue = "ТЭНЦЭЭГҮЙ";
    }

    const image = document.createElement("img");
    image.setAttribute("src", imageSrc);
    image.setAttribute("style", "width: 100px; margin-bottom: 16px;");
    noAttemptsLeftFrame.appendChild(image);

    const passText = document.createElement("h4");
    passText.setAttribute("class", "mdl-cell mdl-cell--12-col title-size");
    passText.innerText = passValue;
    noAttemptsLeftFrame.appendChild(passText);

    const noAttemptsTitle = document.createElement("h5");
    noAttemptsTitle.setAttribute("class", "mdl-cell mdl-cell--12-col title-size");
    noAttemptsTitle.innerText = "Тестийн оролдлого дууссан байна, та дараагийн тестэндээ идэвхтэй оролцоно уу! Баярлалаа";
    noAttemptsLeftFrame.appendChild(noAttemptsTitle);
    card.appendChild(noAttemptsLeftFrame);
    section.appendChild(card);
    document.getElementById("quizFrame").appendChild(section);
  }
}
