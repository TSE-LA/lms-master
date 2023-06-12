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
import { DialogConfig } from "libs/shared/src/lib/dialog/dialog-config";
import { EDITOR_TRANSLATION } from "../../model/editor.translation";

@Component({
  selector: "jrs-announcement-update-dialog",
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
        [title]="'ХАДГАЛАХ'"
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

    <jrs-page-loader
      [show]="saveLoad || publishLoad || initLoad"
    ></jrs-page-loader>
  `,
  styles: [],
})
export class AnnouncementUpdateDialogComponent implements OnInit {
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
  initLoad: boolean;
  publishLoad: boolean;
  content: string;
  id: string;
  private editor: Jodit;

  constructor(
    private dialog: DialogRef,
    private sb: AnnouncementSandboxService,
    private config: DialogConfig
  ) {
    this.id = this.config.data.id;
  }

  ngOnInit(): void {
    this.sb.getAnnouncementById(this.id).subscribe(
      (res) => {
        this.formGroup.controls.title.setValue(res.title);
        this.content = res.content || "";
        this.groups = res.departmentIds || [];

        this.initPage();
      },
      () => {
        this.sb.snackbarOpen("Зар мэдээ ачааллахад  алдаа гарлаа.", false);
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
    const groups = this.groupEnrollment.getEnrolledGroupIds();
    return this.sb
      .updateAnnouncement({
        ...this.formGroup.value,
        departmentIds: groups,
        content: this.editor.getEditorValue(),
        id: this.id,
      })
      .pipe(
        map((res: boolean) => {
          this.saveLoad = false;
          this.sb.snackbarOpen("Зар мэдээ амжилттай хадгаллаа.");
          return this.id;
        }),
        catchError((error: any) => {
          this.saveLoad = false;
          this.sb.snackbarOpen("Зар мэдээ хадгалахад  алдаа гарлаа.", false);
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

  initPage(): void {
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
    this.editor.setEditorValue(this.content);
    this.initLoad = true;
    this.sb.getAnnouncementEnrolledGroups(this.id).subscribe(
      (res) => {
        this.groups = res;
        this.groups[0].showChildren = true;
        this.initLoad = false;
      },
      () => {
        this.initLoad = false;
        this.sb.snackbarOpen("Групп ачаалахад алдаа гарлаа", false);
      }
    );
  }
}
