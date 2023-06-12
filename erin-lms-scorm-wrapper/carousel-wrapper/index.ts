import {CarouselBasedScormWrapper} from './carousel-based-wrapper';

const wrapper = new CarouselBasedScormWrapper();

window.addEventListener("load", () => {
  wrapper.init();
});

window.addEventListener("unload", () => {
  wrapper.end();
});
