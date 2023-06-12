import {Answer} from '../question';

export class AssessmentUtil {
  // @ts-ignore
  public static toSuspendData(singleChoice: Map<number, Answer>, multipleChoice: Map<number, Answer[]>, fillInBlank: Map<number, string>): string {
    let result = "singleChoice";
    //      Â Alt0706 =
    //      Å Alt0709 |
    //      Å Alt0704 -

    for (let [key, value] of singleChoice) {
      result += "Â" + key + "À" + value.value;
    }

    result += "ÅmultiChoice";
    for (let [key, value] of multipleChoice) {
      result += "Â" + key + "À" + value.map(item => item.value).toString();
    }
    result += "ÅfillInBlank";
    for (let [key, value] of fillInBlank) {
      result += "Â" + key + "À" + value;
    }

    return result;
  }

  // @ts-ignore
  public static getSingleChoice(suspendData: string): Map<number, Answer> {
    // @ts-ignore
    let result: Map<number, Answer> = new Map<number, Answer>();
    if (suspendData != null && suspendData !== 'unknown') {
      const split = suspendData.split("Å")[0] !== 'singleChoice' ? suspendData.split("Å")[0].split("Â") : [];

      for (let index = 1; index < split.length; index++) {
        const pair = split[index].split("À");
        const key = parseInt(pair[0]);
        const value = pair[1];
        result.set(key, {value: value});
      }

    }
    return result;
  }

  // @ts-ignore
  public static getMultiChoice(suspendData: string): Map<number, Answer[]> {
    // @ts-ignore
    let result: Map<number, Answer[]> = new Map<number, Answer[]>();
    if (suspendData != null && suspendData !== 'unknown') {
      const split = suspendData.split("Å")[1] !== 'multiChoice' ? suspendData.split("Å")[1].split("Â") : [];

      for (let index = 1; index < split.length; index++) {
        const pair = split[index].split("À");
        const key = parseInt(pair[0]);
        const values = pair[1].split(",");
        if (values) {
          const answers: Answer[] = [];
          for (const value of values) {
            answers.push({value: value});
          }
          result.set(key, answers);
        }
      }
    }
    return result;
  }

  // @ts-ignore
  public static getFillInBlank(suspendData: string): Map<number, string> {
    // @ts-ignore
    let result: Map<number, string> = new Map<number, string>();
    if (suspendData != null && suspendData !== 'unknown') {
      const split = suspendData.split("Å")[2] !== 'fillInBlank' ? suspendData.split("Å")[2].split("Â") : [];

      for (let index = 1; index < split.length; index++) {
        const pair = split[index].split("À");
        const key = parseInt(pair[0]);
        const value = pair[1];
        result.set(key, value);
      }

    }
    return result;
  }
}
