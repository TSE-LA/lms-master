package mn.erin.lms.base.dms;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.common.formats.ImageExtension;
import mn.erin.common.pdf.PdfUtil;
import mn.erin.domain.base.model.Content;
import mn.erin.domain.dms.model.content_model.Property;
import mn.erin.domain.dms.model.document.Document;
import mn.erin.domain.dms.model.document.DocumentId;
import mn.erin.domain.dms.model.folder.Folder;
import mn.erin.domain.dms.model.folder.FolderId;
import mn.erin.domain.dms.model.folder.FolderType;
import mn.erin.domain.dms.repository.DMSRepositoryException;
import mn.erin.domain.dms.repository.DocumentRepository;
import mn.erin.domain.dms.repository.FolderRepository;
import mn.erin.lms.base.domain.model.CourseConstants;
import mn.erin.lms.base.domain.model.certificate.Certificate;
import mn.erin.lms.base.domain.model.certificate.CertificateId;
import mn.erin.lms.base.domain.model.content.CourseContent;
import mn.erin.lms.base.domain.model.content.CourseModule;
import mn.erin.lms.base.domain.model.content.CourseSection;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.exam.question.Question;
import mn.erin.lms.base.domain.repository.CertificateRepository;
import mn.erin.lms.base.domain.repository.CourseContentRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.QuestionRepository;
import mn.erin.lms.base.domain.service.LmsFileSystemService;
import mn.erin.lms.base.domain.service.TemporaryFileApi;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LmsFileSystemServiceImpl implements LmsFileSystemService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(LmsFileSystemServiceImpl.class);

  private static final FolderId ROOT = FolderId.valueOf("-root-");
  private static final String DESCRIPTION = "cm:description";
  private static final String BASE_COURSE_FOLDER = "Courses";
  private static final String BASE_CERTIFICATE_FOLDER = "Certificate";
  private static final String BASE_QUESTION_FOLDER = "Exams";
  private static final String SYSTEM_CONFIG_FOLDER = "System-Config";
  public static final String BASE_LEARNER_CERTIFICATE_FOLDER = "Learner-Certificates";
  private static final String COURSE_CONTENT_FOLDER = "Course Content";
  private static final String COURSE_ATTACHMENT_FOLDER = "Attachment";
  private static final String COURSE_SCORM_FOLDER = "SCORM";

  private DocumentRepository documentRepository;
  private FolderRepository folderRepository;
  private CourseRepository courseRepository;
  private CertificateRepository certificateRepository;
  private CourseContentRepository courseContentRepository;
  private QuestionRepository questionRepository;
  private TemporaryFileApi temporaryFileApi;

  @Inject
  public void setDocumentRepository(DocumentRepository documentRepository)
  {
    this.documentRepository = documentRepository;
  }

  @Inject
  public void setFolderRepository(FolderRepository folderRepository)
  {
    this.folderRepository = folderRepository;
  }

  @Inject
  public void setCourseRepository(CourseRepository courseRepository)
  {
    this.courseRepository = courseRepository;
  }

  @Inject
  public void setCourseContentRepository(CourseContentRepository courseContentRepository)
  {
    this.courseContentRepository = courseContentRepository;
  }

  @Inject
  public void setCertificateRepository(CertificateRepository certificateRepository)
  {
    this.certificateRepository = certificateRepository;
  }

  @Inject
  public void setQuestionRepository(QuestionRepository questionRepository)
  {
    this.questionRepository = questionRepository;
  }

  @Inject
  public void setTemporaryFileApi(TemporaryFileApi temporaryFileApi)
  {
    this.temporaryFileApi = temporaryFileApi;
  }

  @Override
  public String createCertificateFolder(String certificateId, String certificateName)
  {
    try
    {
      Property property = new Property(DESCRIPTION, certificateName);
      FolderType folderType = new FolderType("lms:Certificate", Collections.singleton(property));
      Folder courseFolder = folderRepository.create(getBaseFolderId(BASE_CERTIFICATE_FOLDER), certificateId, folderType);
      return courseFolder.getFolderId().getId();
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
  }

  @Override
  public void createCertificate(String certificateContentFolderId, String name, Map<String, byte[]> file)
  {
    Validate.notBlank(certificateContentFolderId, "Content folder ID cannot be null or blank!");

    for (Map.Entry<String, byte[]> entry : file.entrySet())
    {
      if (entry.getKey() == null)
      {
        continue;
      }

      try
      {
        if (entry.getValue() != null)
        {
          documentRepository.create(FolderId.valueOf(certificateContentFolderId), name, entry.getValue());
        }
      }
      catch (DMSRepositoryException e)
      {
        LOGGER.error(e.getMessage(), e);
      }
    }
  }

  @Override
  public String getCertificateFolderId(String certificateId)
  {
    try
    {
      Certificate certificate = certificateRepository.fetchById(CertificateId.valueOf(certificateId));
      return certificate.getCertificateFolderId();
    }
    catch (LmsRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
  }

  @Override
  public String createCourseFolder(String courseId, String courseName)
  {
    try
    {
      Property property = new Property(DESCRIPTION, courseName);
      FolderType folderType = new FolderType("lms:Course", Collections.singleton(property));
      Folder courseFolder = folderRepository.create(getBaseFolderId(BASE_COURSE_FOLDER), courseId, folderType);
      return courseFolder.getFolderId().getId();
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
  }

  @Override
  public String createQuestionFolder(String questionId, String questionValue)
  {
    try
    {
      Property property = new Property(DESCRIPTION, questionValue);
      FolderType folderType = new FolderType("lms:Exam", Collections.singleton(property));
      Folder questionFolder = folderRepository.create(getBaseFolderId(BASE_QUESTION_FOLDER), questionId, folderType);
      return questionFolder.getFolderId().getId();
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
  }

  @Override
  public Folder createSystemConfigFolder(String organizationId)
  {
    try
    {
      Property property = new Property(DESCRIPTION, organizationId);
      FolderType folderType = new FolderType("lms:System-Config", Collections.singleton(property));
      return folderRepository.create(getBaseFolderId(SYSTEM_CONFIG_FOLDER), organizationId, folderType);
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
  }

  @Override
  public String getQuestionFolderId(String questionId)
  {
    try
    {
      Question question = questionRepository.findById(questionId);
      return question.getDetail().getContentFolderId();
    }
    catch (LmsRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
  }

  @Override
  public String getCourseFolderId(String courseId)
  {
    try
    {
      Course course = courseRepository.fetchById(CourseId.valueOf(courseId));
      return course.getCourseDetail().getProperties().get(CourseConstants.PROPERTY_COURSE_FOLDER_ID);
    }
    catch (LmsRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
  }

  @Override
  public String createCourseContentFolder(String courseFolderId)
  {
    return createFolderInside(courseFolderId, COURSE_CONTENT_FOLDER);
  }

  @Override
  public String createCourseAttachmentFolder(String courseFolderId)
  {
    return createFolderInside(courseFolderId, COURSE_ATTACHMENT_FOLDER);
  }

  @Override
  public String getCourseContentFolderId(String courseFolderId) throws DMSRepositoryException
  {
    Folder courseContentFolder = folderRepository.getFolder(FolderId.valueOf(courseFolderId), COURSE_CONTENT_FOLDER);
    return courseContentFolder.getFolderId().getId();
  }

  @Override
  public String getCourseAttachmentFolderId(String courseFolderId) throws DMSRepositoryException
  {
    Folder courseAttachmentFolder = folderRepository.getFolder(FolderId.valueOf(courseFolderId), COURSE_ATTACHMENT_FOLDER);
    return courseAttachmentFolder.getFolderId().getId();
  }

  @Override
  public Document createFileInFolder(String folderId, File file) throws DMSRepositoryException
  {
    try
    {
      return documentRepository.create(FolderId.valueOf(folderId), file.getName(), FileUtils.readFileToByteArray(file));
    }
    catch (DMSRepositoryException | IOException e)
    {
      throw new DMSRepositoryException(e.getMessage());
    }
  }

  @Override
  public String getSCORMFolderId(String courseFolderId) throws DMSRepositoryException
  {
    Folder courseContentFolder = folderRepository.getFolder(FolderId.valueOf(courseFolderId), COURSE_SCORM_FOLDER);
    return courseContentFolder.getFolderId().getId();
  }

  @Override
  public String getCourseModuleFolderId(String courseFolderId, String folderName)
  {
    try
    {
      String courseContentFolderId = getCourseContentFolderId(courseFolderId);
      Folder courseModuleFolder = folderRepository.getFolder(FolderId.valueOf(courseContentFolderId), folderName);
      return courseModuleFolder.getFolderId().getId();
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error("failed to get [{}] folder.", folderName, e);
      return null;
    }
  }

  @Override
  public String createFolderInside(String courseFolderId, String folderName)
  {
    try
    {
      Folder folder = folderRepository.create(FolderId.valueOf(courseFolderId), folderName);
      return folder.getFolderId().getId();
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
  }

  @Override
  public Map<String, String> createModule(String contentFolderId, List<ModuleDetail> moduleDetails)
  {
    Validate.notBlank(contentFolderId, "Content folder ID cannot be null or blank!");
    Validate.notEmpty(moduleDetails, "Module details cannot be null or empty!");

    Map<String, String> sectionFileIdMap = new HashMap<>();

    for (ModuleDetail moduleDetail : moduleDetails)
    {
      String folderId = createFolderInside(contentFolderId, String.valueOf(moduleDetail.getModuleFolderId()));

      Map<String, String> sectionFiles = createDocuments(folderId, moduleDetail.getSectionFiles());
      sectionFileIdMap.putAll(sectionFiles);
    }

    return sectionFileIdMap;
  }

  @Override
  public Map<String, String> updateModuleFolders(String contentFolderId, List<ModuleDetail> moduleDetails)
  {
    Map<String, String> sectionFileIdMap = new HashMap<>();

    Set<String> existingFolders = new HashSet<>();

    for (ModuleDetail moduleDetail : moduleDetails)
    {
      try
      {
        Folder moduleFolder = folderRepository.getFolder(FolderId.valueOf(contentFolderId), moduleDetail.getModuleFolderId());
        existingFolders.add(moduleFolder.getFolderId().getId());

        Map<String, String> updatedSectionFiles = updateDocuments(moduleFolder.getFolderId().getId(),
            moduleDetail.getSectionFiles());
        sectionFileIdMap.putAll(updatedSectionFiles);
      }

      catch (DMSRepositoryException ex)
      {
        String newModuleFolderId = createFolderInside(contentFolderId, String.valueOf(moduleDetail.getModuleFolderId()));
        Map<String, String> sectionFiles = createDocuments(newModuleFolderId, moduleDetail.getSectionFiles());
        sectionFileIdMap.putAll(sectionFiles);
        existingFolders.add(newModuleFolderId);
      }
    }
    deleteUnusedFolder(contentFolderId, existingFolders);

    return sectionFileIdMap;
  }


  @Override
  public List<ModulePath> getModulePaths(String courseId)
  {
    try
    {
      CourseContent courseContent = courseContentRepository.fetchById(CourseId.valueOf(courseId));
      return getModulePaths(courseId, courseContent.getModules());
    }
    catch (LmsRepositoryException | DMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return Collections.emptyList();
    }
  }

  @Override
  public void clone(String existingCourseId, String courseId) throws DMSRepositoryException
  {
    String existingContentFolderId = getCourseContentFolderId(existingCourseId);
    String clonedCourseFolderId = getCourseFolderId(courseId);

    folderRepository.copy(FolderId.valueOf(clonedCourseFolderId), FolderId.valueOf(existingContentFolderId));
  }

  @Override
  public boolean deleteFolder(String folderId)
  {
    try
    {
      folderRepository.delete(FolderId.valueOf(folderId));
      return true;
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return false;
    }
  }

  @Override
  public boolean createThumbnail(String courseFolderId, String thumbnailName, byte[] thumbnailGIF, byte[] thumbnailJPEG)
  {

    try
    {
      documentRepository.create(FolderId.valueOf(courseFolderId), thumbnailName.concat(".gif"), thumbnailGIF);
      documentRepository.create(FolderId.valueOf(courseFolderId), thumbnailName.concat(".jpeg"), thumbnailJPEG);
      return true;
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error("failed to create thumbnail", e);
      return false;
    }
  }

  @Override
  public boolean createCertificateImage(String certificatePdfId)
  {
    Document result = null;
    try
    {
      Document.Info info = documentRepository.getDocument(DocumentId.valueOf(certificatePdfId));
      Document document = documentRepository.getDocument(FolderId.valueOf(info.getParentId()), info.getName());
      List<File> files = PdfUtil.splitPdfToImage(new ByteArrayInputStream(document.getContent().getContent()), ImageExtension.JPG, 200, 60000);
      File image = files.get(0);
      result = documentRepository.create(FolderId.valueOf(info.getParentId()), info.getName().replace(".PDF", ".JPG"),
          Files.readAllBytes(image.toPath()));
    }
    catch (Exception e)
    {
      LOGGER.error(e.getMessage(), e);
    }
    return result != null;
  }

  @Override
  public boolean createQuestionImage(String questionFolderId, String name, byte[] image) throws DMSRepositoryException
  {
    Document isCreated = documentRepository.create(FolderId.valueOf(questionFolderId), name, image);
    return isCreated != null;
  }

  @Override
  public void uploadAttachment(String courseId, File file)
  {
    if (file != null)
    {
      try
      {
        String folderId = getCourseFolderId(courseId);
        List<Document.Info> infos = documentRepository.listDocuments(FolderId.valueOf(folderId));
        for (Document.Info info : infos)
        {
          documentRepository.delete(DocumentId.valueOf(info.getId()));
        }
        documentRepository.create(FolderId.valueOf(folderId), file.getName(), Files.readAllBytes(file.toPath()));
      }
      catch (Exception e)
      {
        LOGGER.error(e.getMessage(), e);
      }
    }
  }

  @Override
  public void deleteThumbnail(String courseId, String thumbnailName)
  {
    String folderId = getCourseFolderId(courseId);
    if (folderId == null)
    {
      LOGGER.error("Could not find folder ID [{}]", courseId);
      return;
    }
    try
    {
      Document doc = documentRepository.getDocument(FolderId.valueOf(folderId), thumbnailName);
      documentRepository.delete(doc.getDocumentId());
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error("Could not delete or find document", e);
    }
  }

  @Override
  public File findThumbnail(String courseId, String thumbnailName)
  {
    String folderId = getCourseFolderId(courseId);
    if (folderId == null)
    {
      LOGGER.error("Could not find folder ID [{}]", courseId);
      return null;
    }
    Document doc;
    try
    {
      doc = documentRepository.getDocument(FolderId.valueOf(folderId), thumbnailName);
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error("Could not find document", e);
      return null;
    }
    try
    {
      File file = temporaryFileApi.createTempFile(thumbnailName);
      FileUtils.writeByteArrayToFile(file, doc.getContent().getContent());
      return file;
    }
    catch (IOException e)
    {
      LOGGER.error(e.getMessage());
      return null;
    }
  }

  @Override
  public String getLearnerCertificateFolder(String courseId) throws DMSRepositoryException
  {
    Folder baseFolder = folderRepository.getFolder(ROOT, BASE_LEARNER_CERTIFICATE_FOLDER);
    Folder courseFolder = folderRepository.getFolder(baseFolder.getFolderId(), courseId);
    return courseFolder.getFolderId().getId();
  }

  @Override
  public Content getDocumentContent(String folderId, String documentName)
  {
    FolderId scormContentFolderId = FolderId.valueOf(folderId);

    try
    {
      Document document = documentRepository.getDocument(scormContentFolderId, documentName);
      return document.getContent();
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error("failed to get document with name: [{}]", documentName, e);
      return null;
    }
  }

  @Override
  public Content getDocumentContentById(String folderId, String contentId)
  {
    FolderId contentFolderId = FolderId.valueOf(folderId);

    try
    {
      Document.Info info = documentRepository.getDocument(DocumentId.valueOf(contentId));
      return documentRepository.getDocument(contentFolderId, info.getName()).getContent();
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error("failed to get document with ID: [{}]", contentId, e);
      return null;
    }
  }

  private Map<String, String> updateDocuments(String folderId, Map<String, byte[]> files)
  {
    Map<String, String> sectionFileIdMap = new HashMap<>();
    Set<String> existingDocuments = new HashSet<>();

    Set<String> previousDocuments = getAllDocuments(folderId);

    for (Map.Entry<String, byte[]> entry : files.entrySet())
    {
      if (previousDocuments.contains(entry.getKey()))
      {
        existingDocuments.add(entry.getKey());
      }
      else
      {
        if (entry.getValue() != null)
        {
          try
          {
            Document document = documentRepository.create(FolderId.valueOf(folderId), entry.getKey(), entry.getValue());
            sectionFileIdMap.put(entry.getKey(), document.getDocumentId().getId());
            existingDocuments.add(document.getDocumentId().getId());
          }
          catch (DMSRepositoryException e)
          {
            LOGGER.error(e.getMessage(), e);
          }
        }
      }
    }

    removeContents(folderId, existingDocuments);

    return sectionFileIdMap;
  }

  private void deleteUnusedFolder(String parentFolderId, Set<String> existingFolders)
  {
    List<Folder> folders = folderRepository.listFolders(FolderId.valueOf(parentFolderId));
    Set<String> foldersToDelete = folders.stream().filter(folder -> !existingFolders.contains(folder.getFolderId().getId()))
        .map(folder -> folder.getFolderId().getId())
        .collect(Collectors.toSet());

    for (String folderId : foldersToDelete)
    {
      try
      {
        folderRepository.delete(FolderId.valueOf(folderId));
      }
      catch (DMSRepositoryException e)
      {
        LOGGER.error(e.getMessage(), e);
      }
    }
  }

  private Set<String> getAllDocuments(String folderId)
  {
    return documentRepository.listDocuments(FolderId.valueOf(folderId)).stream().map(Document.Info::getId).collect(Collectors.toSet());
  }

  private void removeContents(String folderId, Set<String> existingDocuments)
  {
    Set<String> allDocuments = getAllDocuments(folderId);
    Set<String> toDeleteDocuments = allDocuments.stream()
        .filter(documentId -> !existingDocuments.contains(documentId))
        .collect(Collectors.toSet());

    for (String documentId : toDeleteDocuments)
    {
      try
      {
        documentRepository.delete(DocumentId.valueOf(documentId));
      }
      catch (DMSRepositoryException e)
      {
        LOGGER.error(e.getMessage(), e);
      }
    }
  }

  private List<ModulePath> getModulePaths(String courseId, List<CourseModule> courseModules) throws DMSRepositoryException
  {
    List<ModulePath> modules = new ArrayList<>();
    try
    {
      Folder baseFolder = folderRepository.getFolder(ROOT, BASE_COURSE_FOLDER);
      Folder courseFolder = folderRepository.getFolder(baseFolder.getFolderId(), courseId);
      Folder contentFolder = folderRepository.getFolder(courseFolder.getFolderId(), COURSE_CONTENT_FOLDER);

      for (CourseModule courseModule : courseModules)
      {
        folderRepository.getFolder(contentFolder.getFolderId(), String.valueOf(courseModule.getModuleFolderId()));
        String moduleName = courseModule.getModuleFolderId();
        List<CourseSection> courseSections = courseModule.getSectionList();

        List<SectionPath> sectionPaths = new ArrayList<>();
        for (CourseSection courseSection : courseSections)
        {
          String attachmentId = courseSection.getAttachmentId().getId();
          try
          {
            Document.Info document = documentRepository.getDocument(DocumentId.valueOf(attachmentId));
            SectionPath sectionPath = new SectionPath(getPath(courseId, moduleName, document.getName()), courseSection.getIndex());
            sectionPaths.add(sectionPath);
          }
          catch (DMSRepositoryException e)
          {
            LOGGER.error(e.getMessage(), e);
          }
        }

        modules.add(new ModulePath(sectionPaths, courseModule.getIndex()));
      }
    }
    catch (DMSRepositoryException e)
    {
      for (CourseModule courseModule : courseModules)
      {
        String moduleName = courseModule.getModuleFolderId();
        List<CourseSection> courseSections = courseModule.getSectionList();

        List<SectionPath> sectionPaths = new ArrayList<>();
        for (CourseSection courseSection : courseSections)
        {
          String attachmentId = courseSection.getAttachmentId().getId();

          Document.Info document = documentRepository.getDocument(DocumentId.valueOf(attachmentId));
          SectionPath sectionPath = new SectionPath(getPath(courseId, moduleName, document.getName()), courseSection.getIndex());
          sectionPaths.add(sectionPath);
        }

        modules.add(new ModulePath(sectionPaths, courseModule.getIndex()));
      }
    }
    return modules;
  }

  private Map<String, String> createDocuments(String folderId, Map<String, byte[]> files)
  {
    if (files == null || files.isEmpty())
    {
      return Collections.emptyMap();
    }

    Map<String, String> sectionFileIdMap = new HashMap<>();

    for (Map.Entry<String, byte[]> entry : files.entrySet())
    {
      if (entry.getKey() == null)
      {
        continue;
      }

      try
      {
        if (entry.getValue() != null)
        {
          Document document = documentRepository.create(FolderId.valueOf(folderId), entry.getKey(), entry.getValue());
          sectionFileIdMap.put(entry.getKey(), document.getDocumentId().getId());
        }
      }
      catch (DMSRepositoryException e)
      {
        LOGGER.error(e.getMessage(), e);
      }
    }

    return sectionFileIdMap;
  }

  private String getPath(String courseId, String moduleName, String documentName)
  {
    return "/alfresco/Courses/" + courseId + "/Course Content/" + moduleName + "/" + documentName;
  }

  private FolderId getBaseFolderId(String folderName)
  {
    Folder baseFolder;
    try
    {
      baseFolder = folderRepository.getFolder(ROOT, folderName);
    }
    catch (DMSRepositoryException e)
    {
      try
      {
        baseFolder = folderRepository.create(ROOT, folderName);
      }
      catch (DMSRepositoryException ex)
      {
        LOGGER.error(e.getMessage(), e);
        return null;
      }
    }

    return baseFolder.getFolderId();
  }

  @Override
  public void deleteQuestionImage(String questionId, String folderId, String name)
  {
    try
    {
      Document doc = documentRepository.getDocument(FolderId.valueOf(folderId), name);
      documentRepository.delete(doc.getDocumentId());
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error("Could not delete or find document", e);
    }
  }

  @Override
  public void deleteDocument(DocumentId documentId)
  {
    try
    {
      documentRepository.delete(documentId);
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error("Could not delete document with ID:{}", documentId.getId(), e);
    }
  }

  @Override
  public Folder getFolder(FolderId folderId)
  {
    try
    {
      return folderRepository.getFolder(folderId);
    }
    catch (DMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return null;
    }
  }
}
