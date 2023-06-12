import {Meta, moduleMetadata} from "@storybook/angular";
import {array, number, object, select, text} from "@storybook/addon-knobs";
import {GridContainerComponent} from "./grid-container.component";
import {ButtonComponent} from "../button/button.component";
import {IconsComponent} from "../icons/icons.component";
import {CertificateComponent} from "../certificate/certificate.component";


export default {
  title: 'GridContainer',
  component: GridContainerComponent,
  decorators: [
    moduleMetadata({
      declarations: [ButtonComponent, IconsComponent, CertificateComponent, ButtonComponent],
    }),
  ]
} as Meta

export const Default = () => {
  return {
    component: GridContainerComponent,
    props: {
      row: number('Rows', 1, {min: 1, max: 6}),
      column: number('Columns', 4, {min: 1, max: 6}),
      gap: number('Gap', 10, {min: 5, max: 30}),
      data: object('Data', [{
        "id": "5f477c9318e3992fd18b5cbe",
        "name": "printesr.docx",
        "authorId": "munkhgerel.ba",
        "used": true
      },
        {
          "id": "5f47860018e3992fd18b5d48",
          "name": "1.docx",
          "authorId": "bayartsetseg.m",
          "used": true
        },
        {
          "id": "5f4c5c9518e39972bd409024",
          "name": "certificate-34.docx",
          "authorId": "bayartsetseg.m",
          "used": true
        },
        {
          "id": "5f645a16d2908d3c534e8eb4",
          "name": "certificate-34.docx",
          "authorId": "bayartsetseg.m",
          "used": true
        },
        {
          "id": "5f64603bd2908d3c534e8ef8",
          "name": "certificate-34.docx",
          "authorId": "bayartsetseg.m",
          "used": true
        },
        {
          "id": "5f6467d9d2908d3c534e8fe3",
          "name": "certificate-36.docx",
          "authorId": "bayartsetseg.m",
          "used": true
        },
        {
          "id": "5f646c51d2908d3c534e9003",
          "name": "certificate-36.docx",
          "authorId": "bayartsetseg.m",
          "used": true
        },
        {
          "id": "5f64719bd2908d3c534e9026",
          "name": "certificate-36.docx",
          "authorId": "bayartsetseg.m",
          "used": true
        },
        {
          "id": "5f6473e4d2908d3c534e9029",
          "name": "certificate-36.docx",
          "authorId": "bayartsetseg.m",
          "used": false
        },
        {
          "id": "5f6ab56cd2908d6ef393faf8",
          "name": "certificate.pdf",
          "authorId": "dejidmaa.g",
          "used": false
        },
        {
          "id": "5f6ab580d2908d6ef393faf9",
          "name": "1.docx",
          "authorId": "dejidmaa.g",
          "used": true
        },
        {
          "id": "5fbc7a1a5c172b0a0d491978",
          "name": "certificate-1.docx",
          "authorId": "dejidmaa.g",
          "used": true
        },
        {
          "id": "5fc8c236a6079c773dc3a2cb",
          "name": "certificate.docx",
          "authorId": "erin1",
          "used": true
        }])

    }
  };
}
