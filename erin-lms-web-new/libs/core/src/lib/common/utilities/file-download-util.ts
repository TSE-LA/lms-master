export class FileDownloadUtil {
  public static downloadFile(response: any, nameOfFile?: string) {
    const blob = new Blob([response.body], {type: response.headers.get('content-type')});
    const objUrl = URL.createObjectURL(blob);
    const downloadLink = document.createElement('a');
    downloadLink.href = objUrl;
    const cd = response.headers.get('content-disposition');
    const filename = nameOfFile ? nameOfFile : cd.match('filename="(.+)"');
    downloadLink.download = nameOfFile ? filename : filename[1];
    document.body.appendChild(downloadLink);
    downloadLink.click();
    document.body.removeChild(downloadLink);
    URL.revokeObjectURL(objUrl);
  }

  public static downloadAttachment(response: any, nameOfFile?: string) {
    const extension = nameOfFile ? nameOfFile.split('.').pop() : '';
    if (!['docx', 'xlsx', 'jpeg', 'jpg', 'png', 'svg', 'mp4', 'webm', 'pdf'].includes(extension)) {
      console.error('File extension not supported: ' + extension);
      return;
    }
    const blob = new Blob([response.body], {type: response.headers.get('content-type')});
    const objUrl = URL.createObjectURL(blob);
    const downloadLink = document.createElement('a');
    downloadLink.href = objUrl;
    downloadLink.target = '_blank';
    downloadLink.rel = 'noopener noreferrer';
    downloadLink.setAttribute('download', nameOfFile);
    document.body.appendChild(downloadLink);
    downloadLink.click();
    document.body.removeChild(downloadLink);
    URL.revokeObjectURL(objUrl);
  }
}
