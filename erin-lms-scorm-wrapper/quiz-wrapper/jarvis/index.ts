/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */
import {JarvisQuizWrapper} from "./jarvis-quiz-wrapper";

const testWrapper = new JarvisQuizWrapper();

window.addEventListener("load", () => {
  testWrapper.init();
  const submitButton = document.getElementById("submit-button");
  if (submitButton != null) {
    document.getElementById("submit-button").addEventListener("click", () => {
      testWrapper.submit();
    });
  }
});
