export class ExamStateValueUtil {
  public static getExamStateDisplayName(examState: string): string {
    switch (examState) {
      case 'NEW':
        return 'ШИНЭ';

      case 'PUBLISHED':
        return 'НИЙТЛЭГДСЭН';

      case 'STARTED':
        return 'ЭХЭЛСЭН';

      case 'FINISHED':
        return 'ДУУССАН';
      case 'PENDING':
        return 'ХҮЛЭЭГДЭЖ БУЙ';
    }
  }
}
