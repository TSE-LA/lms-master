import {Observable} from "rxjs";

export declare interface CheckComponent {
  canDeactivate: () => boolean | Observable<boolean>
}
