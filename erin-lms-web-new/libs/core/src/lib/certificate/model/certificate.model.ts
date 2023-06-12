export interface CertificateModel {
  id: string;
  name: string;
  used: boolean;
  type?: string;
  authorId?: string;
}

export interface GenerateCertificateModel {
  url: string;
  documentId: string;
}

export interface ReceivedCertificateModel {
  learnerId: string;
  courseId: string;
  certificateId: string;
  courseName: string;
  date: string;
  score?: number;
  type?: string;
}


