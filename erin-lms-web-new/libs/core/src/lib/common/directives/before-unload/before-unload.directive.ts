import {Directive, EventEmitter, HostListener, OnInit, Output} from "@angular/core";
import {CommonSandboxService} from "../../common-sandbox.service";
import {UserRoleProperties} from "../../common.model";

@Directive({
  selector: '[browserTab]'
})
export class BeforeUnloadDirective implements OnInit {
  private authRole = this.commonSb.authRole$;
  private role: string;
  constructor(private commonSb: CommonSandboxService) {
    this.authRole.subscribe(res => this.role = res)
  }

  ngOnInit() {
  }

  @Output() checkEvent = new EventEmitter();

  @HostListener('window: beforeunload', ['$event']) unloadHandler(event: Event){
    if(this.role !== UserRoleProperties.adminRole.roleUrl){
      event.returnValue = false;
      this.checkEvent.emit('true');
    }
  }
}
