/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.dms.usecase.document.create_document.CreateDocument;
import mn.erin.domain.dms.usecase.document.delete_document.DeleteDocument;
import mn.erin.domain.dms.usecase.document.get_all_document_name.GetDocumentName;
import mn.erin.domain.dms.usecase.document.get_all_documents.GetAllDocument;
import mn.erin.domain.dms.usecase.document.get_document.GetDocument;
import mn.erin.domain.dms.usecase.folder.FolderTypeInput;
import mn.erin.domain.dms.usecase.folder.copy_folder.CopyFolder;
import mn.erin.domain.dms.usecase.folder.create_folder.CreateFolder;
import mn.erin.domain.dms.usecase.folder.create_folder.CreateFolderInput;
import mn.erin.domain.dms.usecase.folder.create_folder.CreateFolderOutput;
import mn.erin.domain.dms.usecase.folder.delete_folder.DeleteFolder;
import mn.erin.domain.dms.usecase.folder.delete_folder.DeleteFolderInput;
import mn.erin.domain.dms.usecase.folder.delete_folder.DeleteFolderOutput;
import mn.erin.domain.dms.usecase.folder.get_folder.GetFolder;
import mn.erin.domain.dms.usecase.folder.get_folder.GetFolderInput;
import mn.erin.domain.dms.usecase.folder.get_folder.GetFolderOuput;
import mn.erin.domain.dms.usecase.folder.get_root_folder.GetRootFolder;
import mn.erin.domain.dms.usecase.folder.get_root_folder.GetRootFolderInput;
import mn.erin.domain.dms.usecase.folder.get_root_folder.GetRootFolderOutput;
import mn.erin.domain.dms.usecase.folder.update_folder.UpdateFolder;
import mn.erin.domain.dms.usecase.folder.update_module_folder.UpdateSubFolder;
import mn.erin.lms.legacy.domain.lms.repository.CourseContentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.service.CourseService;

/**
 * @author Bat-Erdene Tsogoo.
 */
public abstract class BaseCourseRestApi
{
  private static final String BASE_COURSE_FOLDER_NAME = "Courses";

  protected static final String DESCRIPTION = "cm:description";
  protected final CourseRepository courseRepository;
  protected final CourseContentRepository courseContentRepository;
  protected CourseService courseService;

  @Inject
  protected CreateFolder createFolder;

  @Inject
  protected GetRootFolder getRootFolder;

  @Inject
  protected GetFolder getFolder;

  @Inject
  protected DeleteFolder deleteFolder;

  @Inject
  protected UpdateFolder updateFolder;

  @Inject
  protected UpdateSubFolder updateSubFolder;

  @Inject
  protected CreateDocument createDocument;

  @Inject
  protected GetAllDocument getAllDocument;

  @Inject
  protected GetDocumentName getDocumentName;

  @Inject
  protected DeleteDocument deleteDocument;

  @Inject
  protected GetDocument getDocument;

  @Inject
  protected CopyFolder copyFolder;

  public BaseCourseRestApi(CourseRepository courseRepository, CourseContentRepository courseContentRepository,
      CourseService courseService)
  {
    this.courseRepository = Objects.requireNonNull(courseRepository, "CourseRepository cannot be null!");
    this.courseContentRepository = Objects.requireNonNull(courseContentRepository, "CourseContentRepository cannot be null!");
    this.courseService = Objects.requireNonNull(courseService, "CourseSearchService cannot be null!");
  }

  protected String createFolderInside(String courseFolderId, String folderName) throws UseCaseException
  {
    CreateFolderInput input = new CreateFolderInput(courseFolderId, folderName);
    CreateFolderOutput outputCourseIdFolder = createFolder.execute(input);
    return outputCourseIdFolder.getFolderId();
  }

  protected String createCourseFolder(String courseId, String courseName)
  {
    String rootFolderId;

    try
    {
      rootFolderId = getRootFolderId();
    }
    catch (UseCaseException e)
    {
      CreateFolderInput createFolderInput = new CreateFolderInput("-root-", BASE_COURSE_FOLDER_NAME);

      try
      {
        CreateFolderOutput createFolderOutput = createFolder.execute(createFolderInput);
        rootFolderId = createFolderOutput.getFolderId();
      }
      catch (UseCaseException ex)
      {
        throw new IllegalStateException("Unable to create base course folder", ex);
      }
    }

    Map<String, Object> properties = new HashMap<>();
    properties.put(DESCRIPTION, courseName);
    FolderTypeInput folderTypeInput = new FolderTypeInput("lms:Course", properties);
    CreateFolderInput input = new CreateFolderInput(rootFolderId, courseId, folderTypeInput);

    try
    {
      CreateFolderOutput output = createFolder.execute(input);
      return output.getFolderId();
    }
    catch (UseCaseException e)
    {
      throw new IllegalStateException("Unable to create a folder for the course [ID: " + courseId + ", name: " + courseName + "]");
    }
  }

  protected String getCourseFolderId(String courseId) throws UseCaseException
  {
    String rootFolderId = getRootFolderId();

    GetFolderInput folderInput = new GetFolderInput(rootFolderId, courseId);
    GetFolderOuput courseFolder = getFolder.execute(folderInput);

    return courseFolder.getId();
  }

  protected boolean deleteCourseFolder(String courseId)
  {
    try
    {
      String courseFolderId = getCourseFolderId(courseId);
      DeleteFolderInput input = new DeleteFolderInput(courseFolderId);
      DeleteFolderOutput output = deleteFolder.execute(input);
      return output.isDeleted();
    }
    catch (UseCaseException e)
    {
      return false;
    }
  }

  protected boolean isExistFolder(String parentFolderId, String folderName)
  {
    GetFolderInput getFolderInput = new GetFolderInput(parentFolderId, folderName);
    try
    {
      GetFolderOuput existFolder = getFolder.execute(getFolderInput);
      if (null != existFolder)
      {
        return true;
      }
    }
    catch (UseCaseException e)
    {
      return false;
    }
    return false;
  }

  protected String getContentFolderId(String courseFolderId) throws UseCaseException
  {
    GetFolderInput contentFolderInput = new GetFolderInput(courseFolderId, "Course Content");
    GetFolderOuput contentFolderOutput = getFolder.execute(contentFolderInput);

    return contentFolderOutput.getId();
  }

  private String getRootFolderId() throws UseCaseException
  {
    GetRootFolderInput rootFolderInput = new GetRootFolderInput(BASE_COURSE_FOLDER_NAME);
    GetRootFolderOutput rootFolderOutput = getRootFolder.execute(rootFolderInput);
    return rootFolderOutput.getFolderId();
  }
}
