export class SecondsToTimeConverterUtil {
  public static convert(time): string {
    let hour = Math.floor(time % (3600 * 24) / 3600);
    let min = Math.floor(time % 3600 / 60);
    let sec = Math.floor(time % 60)
    if (hour == 0 && min == 0) {
      return sec.toFixed(0) + 'сек';
    } else if (min == 0 && sec == 0) {
      return hour.toFixed(0) + 'ц'
    } else if (hour == 0 && sec == 0) {
      return min.toFixed(0) + 'мин'
    } else if (hour == 0) {
      return min.toFixed(0) + 'мин ' + sec.toFixed(0) + 'сек'
    } else if (sec == 0) {
      return hour.toFixed(0) + 'ц ' + min.toFixed(0) + 'мин';
    } else {
      return hour.toFixed(0) + 'ц ' + min.toFixed(0) + 'мин ' + sec.toFixed(0) + 'сек';
    }
  }

  public static convertWithoutUnit(seconds): string {
    const hours = '0' + Math.floor(seconds / 3600);
    const remainderMinutes = '0' + Math.floor((seconds % 3600) / 60);
    const remainderSeconds = '0' + seconds % 60;
    return hours.slice(-2) + ':' + remainderMinutes.slice(-2) + ':' + remainderSeconds.slice(-2);
  }
}
