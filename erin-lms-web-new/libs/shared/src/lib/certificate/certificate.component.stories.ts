import {Meta, moduleMetadata} from "@storybook/angular";
import {CertificateComponent} from "./certificate.component";
import {boolean, text} from "@storybook/addon-knobs";
import {ButtonComponent} from "../button/button.component";
import {IconsComponent} from "../icons/icons.component";



export default {
  title: 'Certificate',
  component: CertificateComponent,
  decorators: [
    moduleMetadata({
      declarations: [ButtonComponent, IconsComponent],
    }),
  ],
} as Meta

export const Default = () => ({
  component: CertificateComponent,
  props: {
    image: text('Image', 'https://wallpaperaccess.com/full/187161.jpg'),
    desc: text('descriptio', "some image"),
    name: text('Name', 'certificate.docx'),
    author: text('Author', "Dashnyam"),
    createdDate: text('Date', "2021-11-04"),
    isUsed : boolean("Used", false)

  },
});


