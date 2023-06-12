import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { CertificateContainerComponent } from "./certificate-container/certificate-container.component";
import { CertificateService } from "./service/certificate.service";
import { CertificateSandbox } from "./certificate.sandbox";
import { SharedModule } from "../../../../shared/src";

@NgModule({
  declarations: [CertificateContainerComponent],
  imports: [CommonModule, SharedModule],
  exports: [CertificateContainerComponent],
  providers: [CertificateService, CertificateSandbox],
})
export class CertificateModule {}
