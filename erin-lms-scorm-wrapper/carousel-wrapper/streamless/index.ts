import {CarouselBasedScormWrapper} from '../carousel-based-wrapper';
import {CarouselBasedStreamlessScormWrapper} from "./carousel-based-wrapper-without-stream";

const wrapper = new CarouselBasedStreamlessScormWrapper();

window.addEventListener("load", () => {
  wrapper.init();
});

window.addEventListener("unload", () => {
  wrapper.end();
});
