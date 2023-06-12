export class DialogConfig<D = any> {
  data?: D;
  width? = '450px';
  minHeight?: string;
  title?: string;
  outsideClick?: boolean;
  decline? = true;
  submitButton?: string;
  declineButton?: string;
  background? = true;
  blur?: boolean;
}
