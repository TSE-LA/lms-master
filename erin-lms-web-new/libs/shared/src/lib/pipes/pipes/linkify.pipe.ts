import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';

@Pipe({
  name: 'linkify'
})
export class LinkifyPipe implements PipeTransform {

  constructor(private _domSanitizer: DomSanitizer) {}

  transform(value: any, args?: any): any {
    return this._domSanitizer.bypassSecurityTrustHtml(this.turnIntoLink(value));
  }

  private turnIntoLink(text: string): string {
    let linkedText = '';
    if (text && text.length > 0) {
      for (const word of text.split(" ")) {
        if (word.startsWith("https://") || word.startsWith("http://")){
          linkedText += `<a href="${word}" target="_blank" >${word}</a> `;
        }
        else
          linkedText += word + " ";
      }
      return linkedText;
    }
    else return text;
  }
}
