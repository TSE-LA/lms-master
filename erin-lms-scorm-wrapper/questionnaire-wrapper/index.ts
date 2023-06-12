/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

import {UnitelQuestionnaireWrapper} from './unitel-questionnaire-wrapper';

const testWrapper = new UnitelQuestionnaireWrapper();

window.addEventListener("load", () => {
  testWrapper.init();
  document.getElementById("submit-button").addEventListener("click", () => {
    testWrapper.submit();
  });
});
