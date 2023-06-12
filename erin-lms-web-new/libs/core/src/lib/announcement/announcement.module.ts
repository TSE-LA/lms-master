import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { AnnouncementPageComponent } from "./container/announcement-page/announcement-page.component";
import { AnnouncementCreateDialogComponent } from "./container/announcement-create-dialog/announcement-create-dialog.component";
import { SharedModule } from "../../../../shared/src";
import { AnnouncementViewDialogComponent } from "./container/announcement-view-dialog/announcement-view-dialog.component";
import { AnnouncementStatisticsComponent } from "./container/announcement-statistics/announcement-statistics.component";
import { AnnouncementUpdateDialogComponent } from "./container/announcement-update-dialog/announcement-update-dialog.component";
import { JarvisCommonModule } from "../common/common.module";

@NgModule({
  declarations: [
    AnnouncementPageComponent,
    AnnouncementCreateDialogComponent,
    AnnouncementViewDialogComponent,
    AnnouncementStatisticsComponent,
    AnnouncementUpdateDialogComponent,
  ],
  imports: [CommonModule, SharedModule, JarvisCommonModule],
})
export class AnnouncementModule {}
