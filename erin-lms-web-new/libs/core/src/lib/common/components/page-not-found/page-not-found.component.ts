import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'jrs-page-not-found',
  template: `
    <div class="not-found-page">

      <h2>ӨӨ, УУЧЛААРАЙ...</h2>
      <p> Таны хайж буй мэдээлэл энд байхгүй байна. Өмнөх хуудас руу буцаад дахиад оролдох уу? :))</p>
      <jrs-button class="back" [routerLink]="'/'" [color]="'primary'" [size]="'medium'" [float]="'center'">БУЦАХ</jrs-button>
      <div class="meme">
        <img src="https://www.meme-arsenal.com/memes/9a418d60a30443cc088797d2d5124463.jpg">
      </div>
    </div>
  `,
  styles: ['h2 {margin-top: 10%;text-align: center;}p {text-align: center;} img{height: 400px;margin-left: auto; margin-right: auto}' +
  '.meme{width:100%;display: flex;justify-content: center;}']
})
export class PageNotFoundComponent implements OnInit {
  constructor() { }

  ngOnInit(): void {
  }

}
