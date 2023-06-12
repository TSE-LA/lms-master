import { Observable, Subject } from 'rxjs'

export class DialogRef {
  private readonly afterClosed$ = new Subject<any>()
  afterClosed: Observable<any> = this.afterClosed$.asObservable()
  constructor() {}

  close(result?: any) {
    this.afterClosed$.next(result)
  }
}
