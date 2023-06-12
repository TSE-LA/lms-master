import {Component} from '@angular/core';
import {SnackbarService} from "./snackbar.service";

@Component({
  selector: 'jrs-snackbar',
  template: `
    <div class="snackbar" [class]="[size]">
      <div class="snackbar-status-icon">
        <jrs-icon [mat]="true" [size]="'large'"
                  [color]="status === 'success' ? 'primary': 'warn'">{{icon[this.status]}}</jrs-icon>
      </div>
      <div class="snackbar-text">{{snackbarText}}</div>
      <div class="snackbar-close-button">
        <jrs-button (clicked)="this.close()" [size]="'medium'" [noOutline]="true" [iconName]="'clear'"
                    [iconColor]="'dark'" [color]="'primary'"></jrs-button>
      </div>
    </div>
  `,
  styleUrls: ['./snackbar.component.scss']
})
export class SnackbarComponent {
  status = 'success';
  snackbarText: string;
  size: string;
  icon = {
    success: 'check_circle',
    error: 'cancel'
  }
  id: number;

  private delay = 5000;
  private timer;

  constructor(private snackbarService: SnackbarService) {
  }

  ngOnDestroy(): void {
    this.snackbarService.remove(this.id);
  }

  open(id: number, text: string, success?: boolean, size?: string): void {
    this.id = id;
    this.status = success ? 'success' : 'error';
    this.size = size ? size : 'medium';
    this.snackbarText = text;

    this.timer = setTimeout(() => {
      this.close()
    }, this.delay)
  }

  close(): void {
    clearTimeout(this.timer);
    this.snackbarService.remove(this.id);
  }

  getId(): number {
    return this.id;
  }

  setId(id: number): void {
    this.id = id;
  }
}
