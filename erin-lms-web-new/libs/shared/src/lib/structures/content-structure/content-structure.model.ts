export interface StructureModule {
  name: string;
  updatedName: string;
  sections: StructureSection[];
  opened?: boolean;
  attachmentName?: FileAttachment[];
  additionalFile?: FileAttachment[];
  uploadFileType?: ModuleFileType;
  codecSupported?: boolean;
  absolutePath?: string;
  moduleFolderId?: string;
}

export interface StructureSection {
  name: string;
  fileId: string;
}

export enum ModuleFileType {
  PDF = 'PDF',
  VIDEO = 'VIDEO'
}

export interface FileAttachment {
  id: string;
  name: string;
  type?: string;
  renderedName?: string;
}

export interface CourseStructure {
  attachments: FileAttachment[];
  originalContent: FileAttachment[];
  modules: StructureModule[];
}
