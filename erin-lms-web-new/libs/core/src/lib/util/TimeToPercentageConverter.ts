export class TimeToPercentageConverter {
  public static getPercentage(progress: string): number {
    return this.calculatePercentage(this.splitString(progress))
  }

  private static calculatePercentage(data: number[]): number {
    return data[1] < data[0] ? 100 : data[0] !== 0 ? parseFloat((data[0] / data[1] * 100).toFixed(1)) : 0;
  }

  private static splitString(progress: string) {
    const time = progress.split("/");
    const minutes = [];
    time.forEach(el => {
      const splitted = el.split(":");
      minutes.push((parseInt(splitted[0]) * 60 * 60 + parseInt(splitted[1]) * 60 + parseInt(splitted[2])));
    })
    return minutes
  }
}
