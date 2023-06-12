import {Directive, ElementRef, HostListener, Input, Renderer2} from '@angular/core';

@Directive({
  selector: '[jrsTooltip]'
})
export class TooltipDirective {

  @Input('jrsTooltip') tooltipTitle: string;
  @Input() placement = 'auto';
  @Input() delay = 500;
  tooltip: HTMLElement;
  offset = 10;

  constructor(private el: ElementRef, private renderer: Renderer2) {
  }

  @HostListener('mouseenter') onMouseEnter(): void {
    if (!this.tooltip && this.tooltipTitle) {
      this.show();
    }
  }

  @HostListener('mouseleave') onMouseLeave(): void {
    this.hide();

  }

  @HostListener('click') onClick(): void {
    this.hide();
  }


  show(): void {
    this.create();
    this.setPosition();
    this.renderer.addClass(this.tooltip, 'jrs-tooltip-show');
  }

  hide(): void {
    if (this.tooltip) {
      this.renderer.removeClass(this.tooltip, 'jrs-tooltip-show');
      window.setTimeout(() => {
        this.renderer.removeChild(document.body, this.tooltip);
        this.tooltip = null;
      }, this.delay);
    }
  }

  create(): void {
    this.tooltip = this.renderer.createElement('span');
    this.renderer.appendChild(
      this.tooltip,
      this.renderer.createText(this.tooltipTitle)
    );

    this.renderer.appendChild(document.body, this.tooltip);

    this.renderer.addClass(this.tooltip, 'jrs-tooltip');
    this.renderer.addClass(this.tooltip, `jrs-tooltip-${this.placement}`);


    this.renderer.setStyle(this.tooltip, '-webkit-transition', `opacity ${this.delay}ms`);
    this.renderer.setStyle(this.tooltip, '-moz-transition', `opacity ${this.delay}ms`);
    this.renderer.setStyle(this.tooltip, '-o-transition', `opacity ${this.delay}ms`);
    this.renderer.setStyle(this.tooltip, 'transition', `opacity ${this.delay}ms`);
  }

  setPosition(): void {
    const hostPos = this.el.nativeElement.getBoundingClientRect();
    const tooltipPos = this.tooltip.getBoundingClientRect();

    const scrollPos = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0;

    let top, left;
    const height = hostPos.bottom - 50;
    if (this.placement === 'auto') {
      if (hostPos.left > document.body.clientWidth / 2) {
        this.placement = 'left';
      } else {
        this.placement = 'right';
      }
    }

    if (this.placement === 'top') {
      top = hostPos.top - tooltipPos.height - this.offset;
      left = hostPos.left + (hostPos.width - tooltipPos.width) / 2;
    }

    if (this.placement === 'bottom') {
      top = hostPos.bottom + this.offset;
      left = hostPos.left + (hostPos.width - tooltipPos.width) / 2;
    }

    if (this.placement === 'left') {
      top = hostPos.top + this.offset;
      left = hostPos.left - tooltipPos.width - this.offset;
    }

    if (this.placement === 'right') {
      if (hostPos.width > document.body.clientWidth / 2) {
        top = hostPos.top + hostPos.height;
        left = hostPos.left + 25;
      } else {
        top = hostPos.top + (hostPos.height - tooltipPos.height) / 2;
        left = hostPos.right + this.offset;
      }
    }

    this.renderer.setStyle(this.tooltip, 'top', `${top + scrollPos}px`);
    this.renderer.setStyle(this.tooltip, 'left', `${left}px`);
    this.renderer.setStyle(this.tooltip, 'max-height', `${height}px`);
  }
}
