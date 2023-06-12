/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.rest;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.context.annotation.SessionScope;

import mn.erin.domain.dms.repository.DocumentRepository;
import mn.erin.domain.dms.repository.FolderRepository;
import mn.erin.domain.dms.usecase.document.create_document.CreateDocument;
import mn.erin.domain.dms.usecase.document.delete_document.DeleteDocument;
import mn.erin.domain.dms.usecase.document.get_all_document_name.GetDocumentName;
import mn.erin.domain.dms.usecase.document.get_all_documents.GetAllDocument;
import mn.erin.domain.dms.usecase.document.get_document.GetDocument;
import mn.erin.domain.dms.usecase.folder.copy_folder.CopyFolder;
import mn.erin.domain.dms.usecase.folder.create_folder.CreateFolder;
import mn.erin.domain.dms.usecase.folder.delete_folder.DeleteFolder;
import mn.erin.domain.dms.usecase.folder.get_folder.GetFolder;
import mn.erin.domain.dms.usecase.folder.get_root_folder.GetRootFolder;
import mn.erin.domain.dms.usecase.folder.update_folder.UpdateFolder;
import mn.erin.domain.dms.usecase.folder.update_module_folder.UpdateSubFolder;
import mn.erin.lms.legacy.domain.lms.repository.CourseAssessmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseContentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseGroupRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseQuestionnaireRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseTestRepository;
import mn.erin.lms.legacy.domain.lms.service.CourseService;
import mn.erin.lms.legacy.infrastructure.lms.repository.ErinLmsRepositoryBeanConfig;
import mn.erin.lms.legacy.infrastructure.lms.repository.dms.CourseContentPathResolver;
import mn.erin.lms.legacy.infrastructure.lms.repository.dms.CoursePathResolver;
import mn.erin.lms.legacy.infrastructure.lms.repository.dms.CoursePathResolverImpl;
import mn.erin.lms.legacy.infrastructure.lms.repository.dms.LmsCourseContentPackagingService;
import mn.erin.lms.legacy.infrastructure.lms.repository.dms.LmsSCORMPackagingService;
import mn.erin.lms.legacy.infrastructure.lms.rest.course.CourseRestApi;
import mn.erin.lms.legacy.infrastructure.lms.rest.course_assessment.CourseAssessmentRestApi;
import mn.erin.lms.legacy.infrastructure.lms.rest.course_content.CourseContentRestApi;
import mn.erin.lms.legacy.infrastructure.lms.rest.report.CourseActivityReportRestApi;
import mn.erin.lms.legacy.infrastructure.lms.rest.report.CourseReportRestApi;
import mn.erin.lms.legacy.infrastructure.scorm.base.service.SCORMPackagingService;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
@Import({ ErinLmsRepositoryBeanConfig.class })
public class ErinLmsRestBeanConfig
{
  @Inject
  private FolderRepository folderRepository;

  @Inject
  private DocumentRepository documentRepository;

  @Bean(name = "legacySCORMPackagingService")
  @SessionScope
  public SCORMPackagingService scormPackagingService()
  {
    return new LmsSCORMPackagingService(coursePathResolver());
  }

  @Bean(name = "legacyCoursePathResolver")
  @SessionScope
  public CoursePathResolver coursePathResolver()
  {
    return new CoursePathResolverImpl();
  }

  @Bean(name = "legacyCourseContentPathResolver")
  @SessionScope
  public CourseContentPathResolver courseContentPackagingService()
  {
    return new LmsCourseContentPackagingService();
  }

  @Bean(name = "legacyCourseRestApi")
  @SessionScope
  CourseRestApi courseRestApi(CourseRepository courseRepository, CourseCategoryRepository courseCategoryRepository,
      CourseContentRepository courseContentRepository, CourseEnrollmentRepository courseEnrollmentRepository,
      CourseAssessmentRepository courseAssessmentRepository, CourseTestRepository courseTestRepository,
      CourseQuestionnaireRepository courseQuestionnaireRepository, CourseService courseService,
      CourseGroupRepository courseGroupRepository)
  {
    return new CourseRestApi(courseRepository, courseCategoryRepository,
        courseContentRepository, courseEnrollmentRepository, courseAssessmentRepository, courseTestRepository, courseQuestionnaireRepository,
        courseService, courseGroupRepository);
  }

  @Bean(name = "legacyCourseContentRestApi")
  @SessionScope
  CourseContentRestApi courseContentRestApi(CourseContentRepository courseContentRepository, CourseRepository courseRepository,
      CourseCategoryRepository categoryRepository, CourseEnrollmentRepository courseEnrollmentRepository,
      CourseAssessmentRepository courseAssessmentRepository, CourseTestRepository courseTestRepository,
      CourseService courseService, CourseGroupRepository courseGroupRepository, CourseAuditRepository courseAuditRepository)
  {
    return new CourseContentRestApi(courseContentRepository, courseRepository, categoryRepository, courseEnrollmentRepository, courseAssessmentRepository,
        courseTestRepository, courseService, courseGroupRepository, courseAuditRepository);
  }

  @Bean(name = "legacyCourseAssessmentRestApi")
  CourseAssessmentRestApi courseAssessmentRestApi(CourseAssessmentRepository courseAssessmentRepository,
      CourseTestRepository courseTestRepository, CourseRepository courseRepository)
  {
    return new CourseAssessmentRestApi(courseAssessmentRepository, courseTestRepository, courseRepository);
  }

  @Bean(name = "legacyCourseReportRestApi")
  CourseReportRestApi courseReportRestApi()
  {
    return new CourseReportRestApi();
  }

  @Bean(name = "legacyCourseActivityReportRestApi")
  CourseActivityReportRestApi courseActivityReportRestApi()
  {
    return new CourseActivityReportRestApi();
  }

  @Bean(name = "legacyCreateFolder")
  public CreateFolder createFolder()
  {
    return new CreateFolder(folderRepository);
  }

  @Bean(name = "legacyGetFolder")
  public GetFolder getFolder()
  {
    return new GetFolder(folderRepository);
  }

  @Bean(name = "legacyGetRootFolder")
  public GetRootFolder getRootFolder()
  {
    return new GetRootFolder(folderRepository);
  }

  @Bean(name = "legacyUpdateFolder")
  public UpdateFolder updateFolder()
  {
    return new UpdateFolder(folderRepository);
  }

  @Bean(name = "legacyDeleteFolder")
  public DeleteFolder deleteFolder()
  {
    return new DeleteFolder(folderRepository);
  }

  @Bean(name = "legacyUpdateSubFolder")
  public UpdateSubFolder updateModuleFolder()
  {
    return new UpdateSubFolder(folderRepository, documentRepository);
  }

  @Bean(name = "legacyCreateDocument")
  public CreateDocument createDocument()
  {
    return new CreateDocument(documentRepository);
  }

  @Bean(name = "legacyGetDocument")
  public GetDocument getDocument()
  {
    return new GetDocument(documentRepository);
  }

  @Bean(name = "legacyGetAllDocument")
  public GetAllDocument getAllDocument()
  {
    return new GetAllDocument(documentRepository);
  }

  @Bean(name = "legacyGetDocumentName")
  public GetDocumentName getAllDocumentName()
  {
    return new GetDocumentName(documentRepository);
  }

  @Bean(name = "legacyDeleteDocument")
  public DeleteDocument deleteDocument()
  {
    return new DeleteDocument(documentRepository);
  }

  @Bean(name = "legacyCopyFolder")
  public CopyFolder copyFolder()
  {
    return new CopyFolder(folderRepository);
  }
}
