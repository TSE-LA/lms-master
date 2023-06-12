import { Component, OnInit, SecurityContext, ViewChild } from "@angular/core";
import { DialogRef } from "libs/shared/src/lib/dialog/dialog-ref";
import { FormControl, FormGroup } from "@angular/forms";
import { FormUtil } from "libs/shared/src/lib/utilities/form.util";
import { Jodit } from "jodit";
import { GroupNode, LearnerInfo } from "libs/shared/src/lib/shared-model";
import { AnnouncementSandboxService } from "../../announcement-sandbox.service";
import { TreeViewCheckboxComponent } from "libs/shared/src/lib/tree-view-checkbox/tree-view-checkbox.component";
import { Observable, of, throwError } from "rxjs";
import { catchError, map } from "rxjs/operators";
import { EDITOR_TRANSLATION } from "../../model/editor.translation";

@Component({
  selector: "jrs-announcement-create-dialog",
  template: `
    <div class="margin-top">
      <jrs-input-field
        [placeholder]="'Зарлал, мэдээллийн гарчиг'"
        [formGroup]="formGroup"
        [formType]="'title'"
        [movePlaceholder]="true"
        [errorText]="'Заавал бөглөнө үү'"
        [selectedType]="'text'"
        [required]="true"
      >
      </jrs-input-field>
    </div>
    <jrs-scroll [height]="'30vh'">
      <textarea id="editor" name="editor"></textarea>
    </jrs-scroll>
    <div class="margin-top">
      <jrs-scroll [height]="'100px'">
        <jrs-tree-view-checkbox
          #groupEnrollment
          [nodes]="groups"
          [showSelectedItemCount]="true"
          (groupSelected)="groupSelected($event)"
          (groupChecked)="groupSelected($event)"
        ></jrs-tree-view-checkbox>
      </jrs-scroll>
    </div>

    <div class="flex-center">
       <jrs-button
        [title]="'ҮҮСГЭХ'"
        [color]="'primary'"
        [size]="'icon-medium'"
        [float]="'center'"
        [load]="saveLoad || publishLoad"
        (clicked)="saveAnnouncement()"
      >
      </jrs-button>

      <jrs-button
      class="margin-left"
        [title]="'НИЙТЛЭХ'"
        [color]="'primary'"
        [size]="'icon-medium'"
        [float]="'center'"
        [load]="saveLoad || publishLoad"
        (clicked)="publish()"
      >
      </jrs-button>

      <jrs-button
        class="margin-left"
        [title]="'ЦУЦЛАХ'"
        [color]="'warn'"
        [size]="'icon-medium'"
        [float]="'center'"
        [load]="saveLoad || publishLoad"
        (clicked)="close()"
      >
      </jrs-button>
    </div>

    <jrs-page-loader [show]="saveLoad || publishLoad || load"></jrs-page-loader>
  `,
  styles: [],
})
export class AnnouncementCreateDialogComponent implements OnInit {
  @ViewChild("groupEnrollment")
  groupEnrollment: TreeViewCheckboxComponent;
  byAllGroup = true;
  formGroup = new FormGroup({
    title: new FormControl(""),
  });
  groups: GroupNode[] = [];
  allLearners: LearnerInfo[] = [];
  selectedGroup: GroupNode;
  saveLoad: boolean;
  publishLoad: boolean;
  load: boolean;
  private editor: Jodit;

  constructor(
    private dialog: DialogRef,
    private sb: AnnouncementSandboxService
  ) {}

  ngOnInit(): void {
    this.editor = Jodit.make("#editor", {
      language: "en",
      showXPathInStatusbar: false,
      showCharsCounter: false,
      showWordsCounter: false,
      activeButtonsInReadOnly: [],
      i18n: {
        en: EDITOR_TRANSLATION,
      },
    });
    this.editor.setEditorValue("<p></p>");
    this.load = true;
    this.sb.getAllGroups(false).subscribe(
      (res) => {
        this.load = false;
        this.groups = [...this.groups.concat(res)];
        this.groups[0].showChildren = true;
      },
      () => {
        this.load = false;
        this.sb.snackbarOpen("Групп ачааллахад алдаа гарлаа.");
      }
    );
  }

  close(): void {
    this.dialog.close();
  }

  saveAnnouncement(): void {
    if (FormUtil.isFormValid(this.formGroup)) {
      this.save().subscribe(() => {
        this.dialog.close(true);
      });
    }
  }

  save(): Observable<string> {
    this.saveLoad = true;
    const groups = this.groupEnrollment
      ? this.groupEnrollment.getEnrolledGroupIds()
      : [];
    return this.sb
      .createAnnouncement({
        ...this.formGroup.value,
        departmentIds: groups,
        content: this.editor.getEditorValue(),
        id: "",
      })
      .pipe(
        map((res: string) => {
          this.saveLoad = false;
          this.sb.snackbarOpen("Зар мэдээ амжилттай үүсгэлээ.");
          return res;
        }),
        catchError((error: any) => {
          this.saveLoad = false;
          this.sb.snackbarOpen("Зар мэдээ нийтлэхэд  алдаа гарлаа.", false);
          return throwError(error);
        })
      );
  }

  publish(): void {
    if (FormUtil.isFormValid(this.formGroup)) {
      this.publishLoad = true;
      this.save().subscribe((res: string) => {
        this.sb.publishAnnouncement(res).subscribe(
          () => {
            this.publishLoad = false;
            this.sb.snackbarOpen("Зар мэдээ амжилттай нийтлэгдлээ.");
            this.dialog.close(true);
          },
          () => {
            this.publishLoad = false;
            this.dialog.close(true);
            this.sb.snackbarOpen("Зар мэдээ нийтлэхэд  алдаа гарлаа.", false);
          }
        );
      });
    }
  }

  selectedAllGroup(): void {
    this.byAllGroup = true;
  }

  selectedByGroup(): void {
    this.byAllGroup = false;
  }

  groupSelected(group: GroupNode): void {
    this.selectedGroup = group;
  }
}
