export class FileUtil {
  private static images = ['image/jpeg', 'image/png'];
  private static videos = ['video/mp4', 'video/webm'];
  private static web = ['text/html'];

  public static getFileType(mimeType: string) {
    if (this.images.indexOf(mimeType) > -1) {
      return FileType.IMAGE;
    } else if (this.videos.indexOf(mimeType) > -1) {
      return FileType.VIDEO;
    } else if (this.web.indexOf(mimeType) > -1) {
      return FileType.HTML;
    } else {
      return FileType.UNSUPPORTED;
    }
  }
  public static fileToBase64 = (file) => {
    return new Promise(resolve => {
      let reader = new FileReader();
      reader.onload = function(event) {
        resolve(event.target.result);
      };

      reader.readAsDataURL(file);
    });
  };
}

export enum FileType {
  IMAGE = 'Image',
  VIDEO = 'Video',
  HTML = 'HTML',
  UNSUPPORTED = 'Unsupported'
}
