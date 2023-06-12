import { Component, OnInit } from "@angular/core";
import {
  ANNOUNCEMENT_DELETE,
  ANNOUNCEMENT_EDIT,
  ANNOUNCEMENT_NOT_FOUND_TEXT,
  ANNOUNCEMENT_STATISTIC,
  ANNOUNCEMENT_VIEW,
  PUBLISHED_ANNOUNCEMENT_ACTIONS,
  TABLE_COLUMNS,
  UNPUBLISHED_ANNOUNCEMENT_ACTIONS,
} from "../../model/announcement.constants";
import { AnnouncementSandboxService } from "../../announcement-sandbox.service";
import {
  Announcement,
  AnnouncementStatistic,
  PublishStatus,
  ViewStatus,
} from "../../model/announcement.model";
import { DialogConfig } from "libs/shared/src/lib/dialog/dialog-config";
import { AnnouncementCreateDialogComponent } from "../announcement-create-dialog/announcement-create-dialog.component";
import { ConfirmDialogComponent } from "libs/shared/src/lib/dialog/confirm-dialog/confirm-dialog.component";
import { AnnouncementViewDialogComponent } from "../announcement-view-dialog/announcement-view-dialog.component";
import { DateFormatter } from "libs/shared/src/lib/utilities/date-formatter.util";
import { DropdownModel } from "../../../../../../shared/src/lib/dropdown/dropdownModel";
import { AnnouncementUpdateDialogComponent } from "../announcement-update-dialog/announcement-update-dialog.component";

@Component({
  selector: "jrs-announcement-page",
  template: `
    <jrs-section
      [overflow]="true"
      [height]="'75vh'"
      [width]="'100%'"
      [background]="'section-background-secondary'"
    >
      <div class="flex justify-end margin-bottom">
        <jrs-header-text margin="false">ЗАРЛАЛ МЭДЭЭЛЭЛ</jrs-header-text>
        <span class="spacer"></span>
        <jrs-date-interval-picker
          [month]="true"
          [startDate]="startDate"
          [endDate]="endDate"
          (startDateChange)="this.startDateChange($event)"
          (endDateChange)="this.endDateChange($event)"
        >
        </jrs-date-interval-picker>
        <div style="margin-top: 21px;" *canAccess="CREATE_BUTTON">
          <jrs-button
            class="margin-left"
            [size]="'long'"
            [title]="'Үүсгэх'"
            (clicked)="openCreateDialog()"
          >
          </jrs-button>
        </div>
      </div>
      <jrs-dynamic-table
        [tableColumns]="tableColumns"
        [dataSource]="announcements"
        [contextActions]="contextActions"
        (selectedAction)="actionTriggered($event)"
        (rowSelected)="rowSelected($event)"
        [loading]="loading"
        [minWidth]="'unset'"
        [maxWidth]="'unset'"
        [tableMinHeight]="'63vh'"
        [notFoundText]="announcementNotFound"
      >
      </jrs-dynamic-table>
      <div class="margin-bottom"></div>
    </jrs-section>
    <jrs-side-drawer [open]="drawer" (closeDrawer)="closeDrawer($event)">
      <jrs-announcement-statistics
        [statistics]="statistics"
        [totalViewPercentage]="totalView"
        [date]="selectedAnnouncement ? selectedAnnouncement.modifiedDate : ''"
        [author]="selectedAnnouncement ? selectedAnnouncement.author : ''"
      ></jrs-announcement-statistics>
    </jrs-side-drawer>
    <jrs-page-loader [show]="statisticsLoading"></jrs-page-loader>
  `,
  styles: [],
})
export class AnnouncementPageComponent implements OnInit {
  CREATE_BUTTON = "user.announcement.create";
  tableColumns = TABLE_COLUMNS;
  announcements: Announcement[] = [];
  contextActions: DropdownModel[] = [];
  announcementNotFound = ANNOUNCEMENT_NOT_FOUND_TEXT;
  loading: boolean;
  selectedAnnouncement: Announcement;
  selectedStartDate: Date;
  selectedEndDate: Date;
  startDate: string;
  endDate: string;
  drawer = false;
  statisticsLoading: boolean;
  statistics: AnnouncementStatistic[] = [];
  totalView: number;

  constructor(private sb: AnnouncementSandboxService) {
    const date = new Date();
    this.selectedStartDate = new Date(date.getFullYear(), date.getMonth(), 1);
    this.startDate = this.selectedStartDate.toISOString();
    this.selectedEndDate = date;
    this.endDate = this.selectedEndDate.toISOString();
  }

  ngOnInit(): void {
    this.updateAnnouncementTable();
  }

  closeDrawer($event: boolean) {
    this.drawer = $event;
  }

  actionTriggered(event): void {
    switch (event.action) {
      case ANNOUNCEMENT_VIEW:
        this.openViewDialog(this.selectedAnnouncement);
        // this.sb.updateNotification(this.selectedCourse.id);
        break;
      case ANNOUNCEMENT_EDIT:
        this.openEditDialog(this.selectedAnnouncement.id);
        break;
      case ANNOUNCEMENT_DELETE:
        this.openDeleteDialog(this.selectedAnnouncement.id);
        break;
      case ANNOUNCEMENT_STATISTIC:
        this.openStatistics(this.selectedAnnouncement.id);
        break;
      default:
        break;
    }
  }

  openCreateDialog(): void {
    const config = new DialogConfig();
    config.title = "ЗАРЛАЛ, МЭДЭЭЛЭЛ ҮҮСГЭХ";
    config.width = "70vw";
    this.sb
      .openDialog(AnnouncementCreateDialogComponent, config)
      .afterClosed.subscribe((update) => {
        if (update) {
          this.updateAnnouncementTable();
        }
      });
  }

  openStatistics(id: string): void {
    this.statisticsLoading = true;
    this.sb.getStatistics(id).subscribe(
      (res) => {
        this.statistics = res.statistics;
        this.totalView = res.totalView;
        this.statisticsLoading = false;
        this.drawer = true;
      },
      () => {
        this.drawer = false;
        this.statisticsLoading = false;
        this.sb.snackbarOpen(
          "Зарлал мэдээллийн статистик ачааллахад гарлаа",
          false
        );
      }
    );
  }

  openViewDialog(selectedAnnouncement: Announcement): void {
    this.sb.getAnnouncementById(selectedAnnouncement.id).subscribe(
      (res) => {
        const config = new DialogConfig();
        config.title = res.title;
        config.data = {
          author: res.author,
          modifiedDate: res.modifiedDate,
          content: res.content,
        };
        this.sb
          .openDialog(AnnouncementViewDialogComponent, config)
          .afterClosed.subscribe(() => {
            if (selectedAnnouncement.viewStatus == ViewStatus.NEW) {
              this.sb
                .viewAnnouncement(selectedAnnouncement.id)
                .subscribe(() => {
                  this.updateAnnouncementTable();
                });
            }
          });
      },
      () => {
        this.sb.snackbarOpen("Зар мэдээ ачааллахад  алдаа гарлаа.", false);
      }
    );
  }

  openEditDialog(id: string): void {
    const config = new DialogConfig();
    config.title = "ЗАРЛАЛ, МЭДЭЭЛЭЛ ЗАСВАРЛАХ";
    config.width = "70vw";
    config.data = { id };
    this.sb
      .openDialog(AnnouncementUpdateDialogComponent, config)
      .afterClosed.subscribe((update) => {
        if (update) {
          this.updateAnnouncementTable();
        }
      });
  }

  openDeleteDialog(id: string): void {
    const config = new DialogConfig();
    config.outsideClick = true;
    config.title = "Зарлал устгах уу?";
    config.data = {
      info: `Та '${this.selectedAnnouncement.title}' нэртэй зарлал устгахдаа итгэлтэй байна уу?\nУстгасан зарлалыг дахин сэргээх боломжгүйг анхаарна уу.`,
    };

    const dialogRef = this.sb.openDialog(ConfirmDialogComponent, config);
    dialogRef.afterClosed.subscribe((result) => {
      if (result) {
        this.loading = true;
        this.sb.deleteAnnouncement(id).subscribe(
          () => {
            this.loading = false;
            this.sb.snackbarOpen("Зарлал амжилттай устгалаа", true);
            this.updateAnnouncementTable();
          },
          () => {
            this.loading = false;
            this.sb.snackbarOpen("Зарлал устгахад алдаа гарлаа", false);
          }
        );
      }
    });
  }

  rowSelected(event: Announcement) {
    this.selectedAnnouncement = event;
    if (this.selectedAnnouncement.publishStatus == PublishStatus.PUBLISHED) {
      this.contextActions = this.sb.filterWithPermission(
        PUBLISHED_ANNOUNCEMENT_ACTIONS
      );
    } else {
      this.contextActions = this.sb.filterWithPermission(
        UNPUBLISHED_ANNOUNCEMENT_ACTIONS
      );
    }
  }

  startDateChange(event): void {
    this.selectedStartDate = event;
    this.updateAnnouncementTable();
  }

  endDateChange(event): void {
    this.selectedEndDate = event;
    this.updateAnnouncementTable();
  }

  private updateAnnouncementTable(): void {
    this.loading = true;
    this.sb.getAnnouncementNotification();
    this.sb
      .getAllAnnouncements(
        DateFormatter.toISODateString(this.selectedStartDate),
        DateFormatter.toISODateString(this.selectedEndDate)
      )
      .subscribe(
        (res) => {
          this.announcements = res;
          this.loading = false;
        },
        () => {
          this.sb.snackbarOpen("Зар мэдээ ачааллахад алдаа гарлаа.", false);
          this.loading = false;
        }
      );
  }
}
