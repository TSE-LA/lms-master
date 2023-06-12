import {BaseScormWrapper} from '../base-scorm-wrapper';
import {Answer, Question, QuestionType} from '../question';
import {AssessmentUtil} from '../util/assessment-util';

export class SurveyWrapper extends BaseScormWrapper {
  // @ts-ignore
  private selectedSingleChoiceAnswers: Map<number, Answer> = new Map<number, Answer>();
  // @ts-ignore
  private selectedMultipleChoiceAnswers: Map<number, Answer[]> = new Map<number, Answer[]>();
  // @ts-ignore
  private selectedFillInTheBlankAnswers: Map<number, string> = new Map<number, Answer[]>();

  private requiredQuestionNumbers: number[] = [];

  private firstTime = true;

  init(): void {
    this.getApi();
    if (this.API !== undefined) {
      this.initialized = true;
      this.setValue("cmi.exit", "normal");
    } else {
      this.fail("Could not initialize communication with the LMS");
      return;
    }

    const suspendData = this.getValue("cmi.suspend_data");
    this.selectedSingleChoiceAnswers = AssessmentUtil.getSingleChoice(suspendData);
    this.selectedMultipleChoiceAnswers = AssessmentUtil.getMultiChoice(suspendData);
    this.selectedFillInTheBlankAnswers = AssessmentUtil.getFillInBlank(suspendData);

    this.loadAssetData("survey-data.json", (response) => {
      // @ts-ignore
      const loader = new ldBar('#ld');
      const data = JSON.parse(response);

      if (this.getValue("cmi.suspend_data") != null && this.getValue("cmi.suspend_data") != 'unknown') {
        this.firstTime = false;
      }

      for (let index = 0; index < data["data"].length; index++) {
        const datum = data["data"][index];
        const answers: Answer[] = [];
        datum.answers.forEach(answer => {
          answers.push({value: answer.value});
        });
        const question = new Question(datum.question, answers, datum.questionType, datum.isRequired);
        this.createQuestionFrame(question, index + 1);
        const loaderProgress = (index + 1) / (data["data"].length) * 100;
        loader.set(loaderProgress);
        this.checkSubmitButtonStatus();
      }
      if (this.getValue("cmi.suspend_data") != null && this.getValue("cmi.suspend_data") != 'unknown') {
        const submitButton = document.getElementById('submit-button');
        submitButton.parentNode.removeChild(submitButton);
      }

      const loaderElem = document.getElementById('loader');
      loaderElem.parentNode.removeChild(loaderElem);
    });
  }

  end(): void {
  }

  submit(): void {
    this.setValue("cmi.suspend_data", AssessmentUtil.toSuspendData(this.selectedSingleChoiceAnswers, this.selectedMultipleChoiceAnswers,
      this.selectedFillInTheBlankAnswers));
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
    questionTitle.innerText = (questionNumber) + '. ' + question.getTitle();

    if (question.isRequired) {
      const span = document.createElement('span');
      span.setAttribute("class", "required");
      span.innerText = "*";
      questionTitle.appendChild(span);
      this.requiredQuestionNumbers.push(questionNumber);
    }

    questionContent.appendChild(questionTitle);

    if (question.getType() === QuestionType.FILL_IN_BLANK) {
      this.createAnswerFillInTheBlank(questionContent, questionNumber);
    } else if (question.getType() === QuestionType.MULTI_CHOICE) {
      this.createAnswersCheckbox(question.getAnswers(), questionContent, questionNumber);
    } else {
      this.createAnswersRadioButton(question.getAnswers(), questionContent, questionNumber);
    }

    card.appendChild(questionContent);
    section.appendChild(card);
    document.getElementById('assessmentFrame').appendChild(section);
  }

  private createAnswersRadioButton(answers: Answer[], questionContent: HTMLElement, questionNumber): void {
    for (const answer of answers) {
      this.createAnswerRadioButton(answer, questionContent, questionNumber);
    }
  }

  private createAnswersCheckbox(answers: Answer[], questionContent: HTMLElement, questionNumber: number): void {
    for (const answer of answers) {
      const answerContainer = document.createElement("div");
      answerContainer.setAttribute("class", "section__text mdl-cell mdl-cell--10-col-desktop mdl-cell--6-col-tablet mdl-cell--3-col-phone flex");
      answerContainer.style.marginBottom = '5px';
      const label = document.createElement("label");
      const input = document.createElement("input");
      input.setAttribute("type", "checkbox");
      input.setAttribute("class", "with-gap");
      input.setAttribute("name", questionNumber.toString());

      if(!this.firstTime) {
        input.onclick = () => {
          return false;
        }
      }

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
        this.checkSubmitButtonStatus();
      });
      const value = document.createElement("span");
      value.innerText = answer.value;
      value.style.fontSize = '16px';
      label.appendChild(input);
      label.appendChild(value);
      answerContainer.appendChild(label);
      questionContent.appendChild(answerContainer);
    }
  }

  private createAnswerFillInTheBlank(questionContent: HTMLElement, questionNumber: number): void {
    const answerContainer = document.createElement('div');
    answerContainer.setAttribute("class", "section__text mdl-cell mdl-cell--10-col-desktop mdl-cell--6-col-tablet mdl-cell--3-col-phone");
    answerContainer.style.marginBottom = '5px';
    const label = document.createElement('label');
    const input = document.createElement('textarea');
    input.setAttribute("type", "text");
    input.setAttribute("class", "with-gap");
    input.setAttribute("maxlength", "170");
    if(!this.firstTime) input.readOnly = true;
    if (this.selectedFillInTheBlankAnswers.get(questionNumber) != null) {
      input.value = this.selectedFillInTheBlankAnswers.get(questionNumber);
    }
    input.addEventListener("change", (event) => {
      const answer = (event.target as HTMLTextAreaElement).value;
      if (answer && answer.trim() !== '') {
        this.selectedFillInTheBlankAnswers.set(questionNumber, answer);
      } else {
        this.selectedFillInTheBlankAnswers.delete(questionNumber);
      }
      this.checkSubmitButtonStatus();
    });
    const value = document.createElement("span");
    value.style.fontSize = '18px';
    label.appendChild(input);
    label.appendChild(value);
    answerContainer.appendChild(label);
    questionContent.appendChild(answerContainer);
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

    if (this.selectedSingleChoiceAnswers.get(questionNumber) != null &&
      this.selectedSingleChoiceAnswers.get(questionNumber).value === answer.value) {
      input.checked = true;
    } else if(!this.firstTime) {
      input.disabled = true;
    }

    input.addEventListener("change", () => {
      this.selectedSingleChoiceAnswers.set(questionNumber, answer);
      this.checkSubmitButtonStatus();
    });
    const value = document.createElement("span");
    value.innerText = answer.value;
    value.style.fontSize = '18px';
    label.appendChild(input);
    label.appendChild(value);
    answerContainer.appendChild(label);
    questionContent.appendChild(answerContainer);
  }

  private checkSubmitButtonStatus(): void {
    if (this.shouldActivateSubmitButton()) {
      const submitButton = (document.getElementById('submit-button') as HTMLButtonElement);
      submitButton.disabled = false;
      document.getElementsByTagName('button')[0].style.backgroundColor = '#00877A';
    } else {
      const submitButton = (document.getElementById('submit-button') as HTMLButtonElement);
      submitButton.disabled = true;
      document.getElementsByTagName('button')[0].style.backgroundColor = '#bdbdbd';
    }
  }

  private shouldActivateSubmitButton(): boolean {
    for (const requiredQuestionNumber of this.requiredQuestionNumbers) {
      if (this.selectedSingleChoiceAnswers.get(requiredQuestionNumber) == null &&
        (this.selectedMultipleChoiceAnswers.get(requiredQuestionNumber) == null ||
          this.selectedMultipleChoiceAnswers.get(requiredQuestionNumber).length === 0) &&
        this.selectedFillInTheBlankAnswers.get(requiredQuestionNumber) == null) {
        return false;
      }
    }

    return true;
  }
}
