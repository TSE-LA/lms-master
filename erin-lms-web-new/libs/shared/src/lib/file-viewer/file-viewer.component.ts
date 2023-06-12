import {Component, Input, OnChanges, SimpleChanges} from "@angular/core";
import * as PDFJS from 'pdfjs-dist'
import pdfjsWorker from 'pdfjs-dist/build/pdf.worker.entry';
import {ActivatedRoute} from "@angular/router";
import {HttpClient, HttpHeaders, HttpResponse} from "@angular/common/http";
import {FileUtil} from "../utilities/file-util";
import {DialogRef} from "../dialog/dialog-ref";
import {FileDownloadUtil} from "../../../../core/src/lib/common/utilities/file-download-util";
import {SnackbarService} from "../snackbar/snackbar.service";

@Component({
  selector: 'jrs-file-viewer',
  template: `
    <div class="viewer-container">
      <div class="file-actions" *ngIf="!failedToFetch || this.notFound">
        <jrs-button
          [iconName]="'arrow_back_ios'"
          [iconColor]="'secondary'"
          [noOutline]="true"
          [isMaterial]="true"
          [size]="'icon-medium'"
          [bold]="true"
          [textColor]="'text-link'"
          (clicked)="close()">
          БУЦАХ
        </jrs-button>
        <div class="spacer"></div>
        <div class="pagination">
          <jrs-button
            [iconName]="'arrow_back_ios'"
            [iconColor]="'secondary'"
            [noOutline]="true"
            [isMaterial]="true"
            [size]="'small'"
            (clicked)="prev()">
          </jrs-button>
          <div class="pages">
            <span>{{currentPage}}</span>/<span>{{totalPage}}</span>
          </div>
          <jrs-button
            [iconName]="'arrow_forward_ios'"
            [iconColor]="'secondary'"
            [noOutline]="true"
            [isMaterial]="true"
            [size]="'small'"
            (clicked)="next()">
          </jrs-button>
        </div>
        <div class="spacer"></div>
        <jrs-button
          class="margin-top"
          [iconName]="'get_app'"
          [iconColor]="'secondary'"
          [noOutline]="true"
          [isMaterial]="true"
          [size]="'extra-large'"
          [iconSize]="'medium-large'"
          [bold]="true"
          [textColor]="'text-link'"
          (clicked)="download()">
        </jrs-button>
      </div>
      <div class="canvas-container" *ngIf="!failedToFetch || !this.notFound">
        <canvas id="the-canvas" height="100" [class.fullscreen-background]="this.fullscreen" (click)="exitFullScreen()"  >
        </canvas>
      </div>
      <div *ngIf="!failedToFetch || !this.notFound">
        <jrs-button class="float-right"
                    [iconName]="'fullscreen'"
                    [iconColor]="'secondary'"
                    [noOutline]="true"
                    [isMaterial]="true"
                    [size]="'extra-large'"
                    [iconSize]="'medium-large'"
                    [bold]="true"
                    [textColor]="'text-link'"
                    (clicked)="fullScreen()">
        </jrs-button>
      </div>
    </div>
  `,
  styleUrls: ['file-viewer.component.scss']
})
export class FileViewerComponent implements OnChanges {
  @Input() source: string;
  @Input() fileName: string;
  @Input() courseId: string;
  @Input() learnerId: string;
  @Input() errorText = 'Уучлаарай файл татахад алдаа гарлаа!';
  @Input() notFoundText = 'Уучлаарай файл олдсонгүй!';

  downloadable: boolean;
  fileToView: HttpResponse<Blob>;
  failedToFetch = false;
  canvas: any;
  ctx: any;
  currentPdf: any;
  totalPage = 1;
  currentPage = 1;
  pageRendering: boolean;
  pageNumPending = null;
  loading = false;
  fullscreen = false;
  notFound = false;
  delay = 3000;
  errorImgPath = 'assets/images/error.png'

  constructor(private route: ActivatedRoute, private http: HttpClient, public dialog: DialogRef,
    public snackbar: SnackbarService) {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.source.previousValue !== changes.source.currentValue) {
      let headers = new HttpHeaders();
      headers = headers.set('Accept', 'application/pdf');
      this.loading = true;
      return this.http.get(this.source, {
        headers: headers,
        responseType: 'blob',
        observe: 'response',
      }).subscribe(res => {
        this.fileToView = res;
        FileUtil.fileToBase64(res.body).then((result: string) => {
          this.loading = false;
          this.showFile(result.replace(/^data:application\/(pdf);base64,/, ''))
        })
      }, (error) => {
        if (error.status === 404) {
          this.loading = false;
          this.dialog.close();
          this.failedToFetch = true;
          this.snackbar.open(this.notFoundText)
        } else {
          this.loading = false;
          this.notFound = true;
          this.dialog.close();
          this.snackbar.open(this.errorText)
        }
      })
    }
  }

  download(): void {
    FileDownloadUtil.downloadFile(this.fileToView, this.fileName);
  }

  close(): void {
    this.dialog.close();
  }

  fullScreen(): void {
    const element = document.getElementById('the-canvas');
    if (element.requestFullscreen) {
      element.requestFullscreen();
    } 
    element.addEventListener('click', this.canvas.fullscreen, false);
  }
  exitFullScreen(): void {
    if (document.fullscreenElement) {
      // Exit full screen mode
      document.exitFullscreen();
    }
  }
  
 

  prev(): void {
    if (this.currentPage <= 1) {
      return
    }
    this.currentPage--;
    this.queueRenderPage(this.currentPage);
  }

  next(): void {
    if (this.currentPage >= this.currentPdf.numPages) {
      return;
    }
    this.currentPage++;
    this.queueRenderPage(this.currentPage);
  }

  queueRenderPage(num): void {
    if (this.pageRendering) {
      this.pageNumPending = num;
    } else {
      this.renderPage(num);
    }
  }

  private showFile(fileData: any): void {
    this.canvas = document.getElementById('the-canvas');
    this.ctx = this.canvas.getContext('2d');
    const pdfData = atob(fileData);
    PDFJS.GlobalWorkerOptions.workerSrc = pdfjsWorker;
    const loadingTask = PDFJS.getDocument({data: pdfData});
    loadingTask.promise.then((pdf) => {
      this.currentPdf = pdf;
      this.totalPage = pdf.numPages;
      this.renderPage(this.currentPage);
    }, () => {
      this.failedToFetch = true;
    })
  }

  private renderPage(pageNumber: any): void {
    this.pageRendering = true;
    this.currentPdf.getPage(pageNumber).then((page) => {
      const viewport = page.getViewport({scale: 1.5});
      this.canvas.height = viewport.height;
      this.canvas.width = viewport.width;

      const renderContext = {
        canvasContext: this.ctx,
        viewport
      }
      const renderTask = page.render(renderContext);
      renderTask.promise.then(() => {
        this.pageRendering = false;
        if (this.pageNumPending !== null) {
          this.renderPage(this.pageNumPending);
          this.pageNumPending = null;
        }
      })
    })
  }
}
