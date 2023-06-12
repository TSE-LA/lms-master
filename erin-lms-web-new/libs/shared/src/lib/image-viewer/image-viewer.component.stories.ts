import { moduleMetadata, Story, Meta } from '@storybook/angular';
import { ImageViewerComponent } from './image-viewer.component';
import {text} from "@storybook/addon-knobs";

export default {
  title: 'Image viewer',
  component: ImageViewerComponent,
  decorators: [
    moduleMetadata({
      imports: [],
    })
  ],
} as Meta<ImageViewerComponent>;

const Template: Story<ImageViewerComponent> = (args: ImageViewerComponent) => ({
  component: ImageViewerComponent,
  props: args,
});


export const Primary = Template.bind({});
Primary.args = {
  imageSrc: text("Image source", "http://www.placecage.com/c/200/300")
}
