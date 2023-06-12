package mn.erin.lms.base.domain.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import mn.erin.domain.base.model.Content;
import mn.erin.domain.dms.model.document.DocumentId;
import mn.erin.domain.dms.model.folder.Folder;
import mn.erin.domain.dms.model.folder.FolderId;
import mn.erin.domain.dms.repository.DMSRepositoryException;
import mn.erin.domain.dms.model.document.Document;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface LmsFileSystemService
{
  String createCourseFolder(String courseId, String courseName);

  String getCourseFolderId(String courseId);

  String createCourseContentFolder(String courseFolderId);

  String createCourseAttachmentFolder(String courseFolderId);

  String getCourseContentFolderId(String courseFolderId) throws DMSRepositoryException;

  String getCourseAttachmentFolderId(String courseFolderId) throws DMSRepositoryException;

  String getSCORMFolderId(String courseFolderId) throws DMSRepositoryException;

  String getCourseModuleFolderId(String moduleFolderId, String moduleName);

  Content getDocumentContent(String courseContentFolderId, String contentName);

  Content getDocumentContentById(String courseContentFolderId, String contentId);

  String createFolderInside(String courseFolderId, String folderName);

  Map<String, String> createModule(String contentFolderId, List<ModuleDetail> moduleDetails);

  Map<String, String> updateModuleFolders(String contentFolderId, List<ModuleDetail> moduleDetails);

  List<ModulePath> getModulePaths(String courseId);

  String createCertificateFolder(String certificateId, String certificateName);

  void createCertificate(String certificateContentFolderId, String name, Map<String, byte[]> file);

  String getCertificateFolderId(String certificateId);

  String createQuestionFolder(String questionId, String questionValue);

  Folder createSystemConfigFolder(String organizationId);

  Document createFileInFolder(String folderId, File file) throws DMSRepositoryException;

  String getQuestionFolderId(String questionId);

  boolean deleteFolder(String folderId);

  void clone(String existingCourseId, String courseId) throws DMSRepositoryException;

  boolean createThumbnail(String courseFolderId, String thumbnailName, byte[] thumbnailGIF, byte[] thumbnailJPEG);

  boolean createCertificateImage(String certificatePdfId);

  boolean createQuestionImage(String questionFolderId, String name, byte[] image) throws DMSRepositoryException;

  void uploadAttachment(String courseId, File file);

  void deleteThumbnail(String courseId, String thumbnailName);

  File findThumbnail(String courseId, String thumbnailName);

  String getLearnerCertificateFolder(String courseId) throws DMSRepositoryException;

  void deleteQuestionImage(String questionId, String folderId, String name);

  void deleteDocument(DocumentId documentId);

  Folder getFolder(FolderId folderId);

  class ModuleDetail
  {
    private final String name;
    private final int index;
    private final Map<String, byte[]> sectionFiles;
    private final String fileType;
    private String moduleFolderId;

    public ModuleDetail(String name, int index, Map<String, byte[]> sectionFiles, String fileType, String moduleFolderId)
    {
      this.name = name;
      this.index = index;
      this.sectionFiles = sectionFiles;
      this.fileType = fileType;
      this.moduleFolderId = moduleFolderId;
    }

    public String getName()
    {
      return name;
    }

    public String getModuleFolderId()
    {
      return moduleFolderId;
    }

    public void setModuleFolderId(String moduleFolderId) {
      this.moduleFolderId = moduleFolderId;
    }
    public int getIndex()
    {
      return index;
    }

    public Map<String, byte[]> getSectionFiles()
    {
      return sectionFiles;
    }
  }

  class ModulePath
  {
    private final List<SectionPath> sectionPaths;
    private final Integer index;

    public ModulePath(List<SectionPath> sectionPaths, Integer index)
    {
      this.sectionPaths = sectionPaths;
      this.index = index;
    }

    public List<SectionPath> getSectionPaths()
    {
      return sectionPaths;
    }

    public Integer getIndex()
    {
      return index;
    }
  }

  class SectionPath
  {
    private final String path;
    private final Integer index;

    public SectionPath(String path, Integer index)
    {
      this.path = path;
      this.index = index;
    }

    public String getPath()
    {
      return path;
    }

    public Integer getIndex()
    {
      return index;
    }
  }
}
