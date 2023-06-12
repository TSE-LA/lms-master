import { Component, OnInit, SecurityContext } from "@angular/core";
import { DomSanitizer } from "@angular/platform-browser";
import { DialogConfig } from "libs/shared/src/lib/dialog/dialog-config";
import { DialogRef } from "libs/shared/src/lib/dialog/dialog-ref";
import { Jodit } from "jodit";
import { EDITOR_TRANSLATION } from "../../model/editor.translation";

@Component({
  selector: "jrs-announcement-create-dialog",
  template: `
    <jrs-label
      [size]="'small'"
      [text]="modifiedDate + ' | ' + author"
    ></jrs-label>
    <div class="margin-top">
      <jrs-scroll [height]="'50vh'">
        <div [innerHTML]="sanitizedHTMLString"></div>
        <textarea id="editor" name="editor"></textarea>
      </jrs-scroll>
    </div>

    <jrs-action-buttons
      [declineButton]="'ХААХ'"
      [submit]="false"
      (declined)="close()"
    ></jrs-action-buttons>
    <!-- </jrs-scroll> -->
  `,
  styles: [],
})
export class AnnouncementViewDialogComponent implements OnInit {
  modifiedDate: string;
  author: string;
  sanitizedHTMLString: string;
  editor: Jodit;

  constructor(private dialog: DialogRef, private config: DialogConfig) {
    this.author = this.config.data.author;
    this.modifiedDate = this.config.data.modifiedDate;
  }

  ngOnInit(): void {
    this.editor = Jodit.make("#editor", {
      toolbar: false,
      toolbarSticky: false,
      showXPathInStatusbar: false,
      language: "en",
      showCharsCounter: false,
      showWordsCounter: false,
      activeButtonsInReadOnly: [],
      i18n: {
        en: EDITOR_TRANSLATION,
      },
    });
    this.editor.setEditorValue(this.config.data.content);
    this.editor.setReadOnly(true);
  }

  close(): void {
    this.dialog.close();
  }
}
