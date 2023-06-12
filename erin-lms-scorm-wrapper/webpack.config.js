/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */
const path = require('path');

const config = {
  resolve: {
    extensions: ['.ts']
  },
  module: {
    rules: [
      {
        test: /\.ts$/,
        use: 'ts-loader'
      }
    ]
  }
};

const quizUnitelConfig = Object.assign({}, config, {
  entry: './quiz-wrapper/unitel/index.ts',
  output: {
    filename: 'unitel-wrapper.js',
    path: path.resolve(__dirname, './quiz-wrapper')
  }
});

const quizJarviaConfig = Object.assign({}, config, {
  entry: './quiz-wrapper/jarvis/index.ts',
  output: {
    filename: 'jarvis-wrapper.js',
    path: path.resolve(__dirname, './quiz-wrapper')
  }
});

const questionnaireConfig = Object.assign({}, config, {
  entry: './questionnaire-wrapper/index.ts',
  output: {
    filename: 'questionnaire-wrapper.js',
    path: path.resolve(__dirname, './questionnaire-wrapper')
  }
});

const mainWrapperConfig = Object.assign({}, config, {
  entry: './carousel-wrapper/index.ts',
  output: {
    filename: 'wrapper.js',
    path: path.resolve(__dirname, './carousel-wrapper')
  }
});

const mainWrapperConfigWithoutStream = Object.assign({}, config, {
  entry: './carousel-wrapper/streamless/index.ts',
  output: {
    filename: 'wrapper-without-stream.js',
    path: path.resolve(__dirname, './carousel-wrapper')
  }
});

const surveyConfig = Object.assign({}, config, {
  entry: './survey-wrapper/index.ts',
  output: {
    filename: 'wrapper.js',
    path: path.resolve(__dirname, './survey-wrapper')
  }
});

module.exports = [mainWrapperConfig, mainWrapperConfigWithoutStream, quizUnitelConfig, questionnaireConfig, surveyConfig, quizJarviaConfig];
