import {Meta, moduleMetadata} from "@storybook/angular";
import {text} from "@storybook/addon-knobs";
import {CommonModule} from "@angular/common";
import {LoginComponent} from "./login.component";
import {RouterModule} from "@angular/router";

export default {
  title: 'Login',
  component: LoginComponent,
  parameters: {actions: {argTypesRegex: '^on.*'}},
  argTypes: {onClick: {action: 'clicked'}},
  excludeStories: /.*Data$/,
  decorators: [
    moduleMetadata({
      imports: [CommonModule, RouterModule]
    }),
  ],
} as Meta

export const Regular = () => ({
  component: LoginComponent,
  props: {
    background: text('background', 'assets/images/login-logo-unitel.png')
  }
});
