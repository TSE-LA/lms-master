/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

import {addDecorator} from '@storybook/angular';
import {withKnobs} from '@storybook/addon-knobs';
import {withActions} from '@storybook/addon-actions';
import {themes} from "@storybook/theming";

import '!style-loader!css-loader!sass-loader!../src/lib/theme/styles/index.scss';

addDecorator(withKnobs);
addDecorator(withActions);

export const parameters = {
  docs: {
    theme: themes.light,
  },
}

function addThemeClass() {
  document.body.classList.add("jrs-theme-default");
}

addThemeClass();
