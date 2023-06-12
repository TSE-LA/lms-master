import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from "@angular/core";
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";
import {Status} from "../constant/status";
import {ErrorCodes} from "../constant/error-codes";
import {ErrorMessages} from "../constant/error-messages";
import {DataModelConstraint} from "../constant/data-model-constraint";
import {RuntimeDataModel} from "../model/runtime-data.model";

@Component({
  selector: 'scorm-runtime',
  template: `
    <iframe id="scormContentFrame" [src]="urlSafe" [class.iframe-course]="!fullPage" [class.iframe-full-page]="fullPage"></iframe>
  `,
  styleUrls: ['./scorm-runtime.component.scss']
})
export class ScormRuntimeComponent implements OnChanges {
  @Input() launchUrl: string;
  @Input() data: Map<string, RuntimeDataModel> = new Map();
  @Input() fullPage = false;
  @Output() terminated = new EventEmitter<Map<string, RuntimeDataModel>>();
  @Output() dataChanged = new EventEmitter<any>();
  @Output() initialized = new EventEmitter();
  @Output() committed = new EventEmitter<Map<string, RuntimeDataModel>>();
  urlSafe: SafeResourceUrl;
  private status: number;
  private lastErrorCode: number;
  private lastDiagnostic: string;

  constructor(public domSanitizer: DomSanitizer) {
    this.status = Status.STARTED;
    this.lastErrorCode = ErrorCodes.NO_ERROR;
    this.lastDiagnostic = '';
    (window as any).API_1484_11 = this;
  }

  ngOnChanges(changes: SimpleChanges) {
    for (let prop in changes) {
      if (prop === 'launchUrl') {
        this.status = Status.STARTED;
        this.urlSafe = this.domSanitizer.bypassSecurityTrustResourceUrl(this.launchUrl);
      }
    }
  }

  Initialize(value: string): boolean {
    switch (this.status) {
      case Status.STARTED:
        this.status = Status.INITIALIZED;
        this.initialized.emit();
        return this.success();
      case Status.INITIALIZED:
        return this.fail(ErrorCodes.ALREADY_INITIALIZED);
      case Status.TERMINATED:
        return this.fail(ErrorCodes.CONTENT_INSTANCE_TERMINATED);
      default:
        return this.fail(ErrorCodes.GENERAL_INITIALIZATION_FAILURE, 'Unknown status [' + this.status + ']');
    }
  }

  Terminate(value: string): boolean {
    switch (this.status) {
      case Status.STARTED:
        return this.fail(ErrorCodes.TERMINATION_BEFORE_INITIALIZATION);
      case Status.INITIALIZED:
        this.status = Status.TERMINATED;
        this.terminated.emit(this.data);
        return this.success();
      case Status.TERMINATED:
        return this.fail(ErrorCodes.TERMINATION_AFTER_TERMINATION);
      default:
        return this.fail(ErrorCodes.GENERAL_TERMINATION_FAILURE, 'Unknown status [' + this.status + ']');
    }
  }

  GetValue(cmiElement: string): string {
    const runtimeData = this.getRuntimeData(cmiElement);

    if (runtimeData.constraint === DataModelConstraint.WRITE_ONLY) {
      this.fail(ErrorCodes.DATA_MODEL_ELEMENT_IS_WRITE_ONLY);
      throw new Error(ErrorMessages.get(ErrorCodes.DATA_MODEL_ELEMENT_IS_WRITE_ONLY));
    }
    return runtimeData.data;
  }

  SetValue(cmiElement: string, value: string): void {
    const runtimeData = this.getRuntimeData(cmiElement);

    if (runtimeData.constraint === DataModelConstraint.READ_ONLY) {
      this.fail(ErrorCodes.DATA_MODEL_ELEMENT_IS_READ_ONLY);
      throw new Error(ErrorMessages.get(ErrorCodes.DATA_MODEL_ELEMENT_IS_READ_ONLY));
    }
    runtimeData.data = value;
    this.dataChanged.emit({cmiElement, value});
  }

  GetLastError(): number {
    return this.lastErrorCode;
  }

  GetErrorString(errorCode: number): string {
    return ErrorMessages.get(this.lastErrorCode);
  }

  GetDiagnostic(errorCode: number): string {
    return this.lastDiagnostic;
  }

  Commit(value: string): boolean {
    this.committed.emit(this.data);
    return true;
  }

  private success(): boolean {
    this.save(ErrorCodes.NO_ERROR);
    return true;
  }

  private fail(errorCode: number, diagnostic?: string): boolean {
    if (diagnostic === undefined) {
      this.save(errorCode, ErrorMessages.get(errorCode));
    }
    this.save(errorCode, diagnostic);
    return false;
  }

  private save(errorCode: number, diagnostic?: string) {
    this.lastErrorCode = errorCode;
    this.lastDiagnostic = diagnostic !== undefined ? diagnostic : '';
  }

  private getRuntimeData(cmiElement: string): RuntimeDataModel {
    const runtimeData = this.data.get(cmiElement);
    if (runtimeData === undefined || runtimeData === null) {
      this.fail(ErrorCodes.UNDEFINED_DATA_MODEL_ELEMENT);
      throw new Error(ErrorMessages.get(ErrorCodes.UNDEFINED_DATA_MODEL_ELEMENT));
    }
    return runtimeData;
  }
}
