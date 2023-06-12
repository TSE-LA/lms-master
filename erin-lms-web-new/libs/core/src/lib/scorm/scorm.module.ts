import {NgModule} from "@angular/core";
import {ScormRuntimeComponent} from "./component/scorm-runtime.component";
import {SharedModule} from "../../../../shared/src";

@NgModule({
  declarations: [ScormRuntimeComponent],
  imports: [
    SharedModule
  ],
  exports: [ScormRuntimeComponent]
})
export class ScormModule {

}
