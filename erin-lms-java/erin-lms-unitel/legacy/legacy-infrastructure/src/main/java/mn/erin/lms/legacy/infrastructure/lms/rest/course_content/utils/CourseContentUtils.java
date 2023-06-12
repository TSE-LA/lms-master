/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.rest.course_content.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.common.pdf.Section;
import mn.erin.domain.dms.usecase.folder.update_module_folder.UpdateInfo;
import mn.erin.lms.legacy.domain.lms.model.content.Attachment;
import mn.erin.lms.legacy.domain.lms.model.content.AttachmentId;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.ContentModuleInfo;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.ContentSectionInfo;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.create_course_content.CreateCourseContentInput;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.get_course_content.GetCourseContentOutput;
import mn.erin.lms.legacy.domain.lms.usecase.course_content.update_course_content.UpdateCourseContentInput;
import mn.erin.lms.legacy.infrastructure.lms.rest.course_content.Module;
import mn.erin.lms.legacy.infrastructure.lms.rest.course_content.UpdatedModule;
import mn.erin.lms.legacy.infrastructure.lms.rest.course_content.rest_entities.RestAttachment;
import mn.erin.lms.legacy.infrastructure.lms.rest.course_content.rest_entities.RestCourseContent;
import mn.erin.lms.legacy.infrastructure.lms.rest.course_content.rest_entities.RestFileSection;
import mn.erin.lms.legacy.infrastructure.lms.rest.course_content.rest_entities.RestModule;
import mn.erin.lms.legacy.infrastructure.lms.rest.course_content.rest_entities.RestSection;
import mn.erin.lms.legacy.infrastructure.lms.rest.course_content.rest_entities.RestUpdatedContent;
import mn.erin.lms.legacy.infrastructure.lms.rest.course_content.rest_entities.RestUpdatedModule;

import static mn.erin.lms.legacy.infrastructure.lms.rest.ErrorMessages.ERROR_MSG_COURSE_CONTENT;
import static mn.erin.lms.legacy.infrastructure.lms.rest.ErrorMessages.ERROR_MSG_COURSE_ID;
import static mn.erin.lms.legacy.infrastructure.lms.rest.ErrorMessages.ERROR_MSG_COURSE_MODULE;

/**
 * author Tamir Batmagnai.
 */
public class CourseContentUtils
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CourseContentUtils.class);

  private static final String EMPTY_FILE_ID = "empty";

  private CourseContentUtils()
  {
  }

  public static void validate(RestCourseContent courseContent)
  {
    Validate.notNull(courseContent, ERROR_MSG_COURSE_CONTENT);
    Validate.notNull(courseContent.getCourseId(), ERROR_MSG_COURSE_ID);
    Validate.notNull(courseContent.getModules(), ERROR_MSG_COURSE_MODULE);
  }

  public static void validate(RestUpdatedContent updatedContent)
  {
    Validate.notNull(updatedContent, ERROR_MSG_COURSE_CONTENT);
    Validate.notNull(updatedContent.getCourseId(), ERROR_MSG_COURSE_ID);
    Validate.notNull(updatedContent.getModules(), ERROR_MSG_COURSE_MODULE);
  }

  public static RestCourseContent toRestCourseContent(GetCourseContentOutput output)
  {
    RestCourseContent courseContent = new RestCourseContent();

    courseContent.setCourseId(output.getCourseId());
    if (null != output.getCourseModules())
    {
      courseContent.setModules(toRestModules(output.getCourseModules()));
    }
    if (null != output.getAttachments())
    {
      courseContent.setAttachments(toRestAttachment(output.getAttachments()));
    }
    if (null != output.getAdditionalFiles())
    {
      courseContent.setAdditionalFiles(toRestAdditionalFile(output.getAdditionalFiles()));
    }

    return courseContent;
  }

  public static List<UpdateInfo> toUpdateInfos(List<UpdatedModule> moduleDetails)
  {
    List<UpdateInfo> folderInfos = new ArrayList<>();

    for (UpdatedModule moduleDetail : moduleDetails)
    {
      String initName = moduleDetail.getName();
      String updatedName = moduleDetail.getUpdatedName();

      Map<String, byte[]> sectionFiles = moduleDetail.getSectionFiles();

      UpdateInfo folderInfo = new UpdateInfo(initName, updatedName, sectionFiles);
      folderInfos.add(folderInfo);
    }
    return folderInfos;
  }

  public static List<String> getDuplicates(List<String> first, List<String> second)
  {
    List<String> uniqueValues = new ArrayList<>();

    uniqueValues.addAll(first.stream()
        .filter(second::contains)
        .collect(Collectors.toList()));

    return uniqueValues;
  }

  public static CreateCourseContentInput toCreateInput(RestCourseContent courseContent)
  {
    List<ContentModuleInfo> moduleInfos = courseContent.getModules()
        .stream()
        .map(restContentModule -> new ContentModuleInfo(restContentModule.getName(),
            toSectionInfo(restContentModule.getSectionList()),
            restContentModule.getIndex(), restContentModule.getFileType()))
        .collect(Collectors.toList());

    List<Attachment> attachmentList = courseContent.getAttachments().stream()
            .map(restAttachment -> new Attachment(new AttachmentId(restAttachment.getId()), restAttachment.getName(), restAttachment.getType())).collect(Collectors.toList());

    List<Attachment> additionalFilesList = courseContent.getAdditionalFiles().stream()
            .map(restAttachment -> new Attachment(new AttachmentId(restAttachment.getId()), restAttachment.getName(), restAttachment.getType())).collect(Collectors.toList());

    return new CreateCourseContentInput(courseContent.getCourseId(), moduleInfos, attachmentList, additionalFilesList);
  }

  public static UpdateCourseContentInput toUpdateInput(String courseId, RestUpdatedContent courseContent)
  {
    List<ContentModuleInfo> moduleInfos = courseContent.getModules()
        .stream()
        .map(restContentModule -> new ContentModuleInfo(restContentModule.getUpdatedName(),
            toSectionInfo(restContentModule.getSectionList()),
            restContentModule.getIndex(), restContentModule.getFileType()))
        .collect(Collectors.toList());
    List<Attachment> attachmentList = courseContent.getAttachments().stream()
            .map(restAttachment -> new Attachment(new AttachmentId(restAttachment.getId()), restAttachment.getName(), restAttachment.getType())).collect(Collectors.toList());

    List<Attachment> additionalFilesList = courseContent.getAdditionalFiles().stream()
            .map(restAttachment -> new Attachment(new AttachmentId(restAttachment.getId()), restAttachment.getName(), restAttachment.getType())).collect(Collectors.toList());

    return new UpdateCourseContentInput(courseId, moduleInfos, attachmentList, additionalFilesList);
  }

  public static void addFileIds(Map<String, String> allFileId, Map<String, String> sectionFileIds)
  {
    for (Map.Entry<String, String> fileIdEntry : sectionFileIds.entrySet())
    {
      String memoryFileId = fileIdEntry.getKey();
      String documentId = fileIdEntry.getValue();

      allFileId.put(memoryFileId, documentId);
    }
  }

  public static void setImageFileIds(List<RestModule> restModules, Map<String, String> documentIds)
  {
    List<RestSection> allSections = new ArrayList<>();

    for (RestModule module : restModules)
    {
      allSections.addAll(module.getSectionList());
    }
    replaceByDocumentId(allSections, documentIds);
  }

  public static void putDocumentIds(List<RestUpdatedModule> restModules, Map<String, String> documentIds)
  {
    List<RestSection> allSections = new ArrayList<>();

    for (RestUpdatedModule module : restModules)
    {
      allSections.addAll(module.getSectionList());
    }
    replaceByDocumentId(allSections, documentIds);
  }

  public static Map<String, byte[]> toFileContentsMap(Map<String, Section> fileIds, List<RestSection> sectionList) throws IOException
  {
    Map<String, byte[]> filesMap = new HashMap<>();

    for (RestSection restSection : sectionList)
    {
      if (null == restSection.getFileId())
      {
        continue;
      }

      Section section = fileIds.get(restSection.getFileId());

      if (null == section)
      {
        filesMap.put(restSection.getFileId(), null);
        continue;
      }
      File file = section.getFile();

      byte[] content = Files.readAllBytes(file.toPath());
      filesMap.put(restSection.getFileId(), content);
    }
    return filesMap;
  }

  public static List<Module> getModuleDetails(Map<String, Section> filesIds, List<RestModule> restModules)
  {
    List<Module> moduleDetails = new ArrayList<>();

    for (RestModule restModule : restModules)
    {
      try
      {
        Map<String, byte[]> sectionFiles = toFileContentsMap(filesIds, restModule.getSectionList());

        // Removes leading and trailing spaces.
        String moduleName = restModule.getName().trim();

        Module detail = new Module(moduleName, sectionFiles);
        moduleDetails.add(detail);
      }
      catch (IOException e)
      {
        return Collections.emptyList();
      }
    }
    return moduleDetails;
  }

  public static List<UpdatedModule> getUpdatedModules(Map<String, Section> fileIds, List<RestUpdatedModule> updateModules)
  {
    List<UpdatedModule> moduleDetails = new ArrayList<>();

    for (RestUpdatedModule updated : updateModules)
    {
      try
      {
        Map<String, byte[]> sectionFiles = toFileContentsMap(fileIds, updated.getSectionList());

        UpdatedModule detail = new UpdatedModule(updated.getInitName(), updated.getUpdatedName(), sectionFiles, updated.getFileType());
        moduleDetails.add(detail);
      }
      catch (IOException e)
      {
        return Collections.emptyList();
      }
    }
    return moduleDetails;
  }

  public static RestFileSection createRestSection(String attachFileId, Map<String, Section> files)
  {
    List<RestSection> restSections = new ArrayList<>();

    for (Map.Entry<String, Section> fileEntry : files.entrySet())
    {

      Integer pageNumber = fileEntry.getValue().getPageNumber();

      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(pageNumber);

      RestSection restSection = new RestSection();
      restSection.setFileId(fileEntry.getKey());
      restSection.setName(stringBuilder.toString());

      restSection.setIndex(pageNumber);

      restSections.add(restSection);
    }
    return new RestFileSection(attachFileId, restSections);
  }

  private static void replaceByDocumentId(List<RestSection> allSections, Map<String, String> contentIdMap)
  {
    for (Map.Entry<String, String> ids : contentIdMap.entrySet())
    {
      String memoryId = ids.getKey();
      String documentId = ids.getValue();

      for (RestSection restSection : allSections)
      {
        String fileId = restSection.getFileId();

        if (null != fileId && fileId.equalsIgnoreCase(memoryId))
        {
          restSection.updateFileId(documentId);
        }
      }
    }
  }

  private static List<RestModule> toRestModules(List<ContentModuleInfo> courseModules)
  {
    return courseModules.stream()
        .map(
            contentModuleInfo -> new RestModule(contentModuleInfo.getName(), contentModuleInfo.getIndex(), toRestSections(contentModuleInfo.getSectionInfos()),
                contentModuleInfo.getFileType()))
        .collect(Collectors.toList());
  }
  private static List<RestAttachment> toRestAttachment(List<Attachment> courseAttachments)
  {
    return courseAttachments.stream()
        .map(attachment -> new RestAttachment(attachment.getId().getId(), attachment.getName(), attachment.getType()))
        .collect(Collectors.toList());
  }

  private static List<RestAttachment> toRestAdditionalFile(List<Attachment> courseAttachments)
  {
    return courseAttachments.stream()
        .map(attachment -> new RestAttachment(attachment.getId().getId(), attachment.getName(), attachment.getType()))
        .collect(Collectors.toList());
  }

  private static List<RestSection> toRestSections(List<ContentSectionInfo> sectionInfos)
  {
    return sectionInfos.stream()
        .map(contentSectionInfo -> new RestSection(contentSectionInfo.getName(), contentSectionInfo.getIndex(), contentSectionInfo.getFileId()))
        .collect(Collectors.toList());
  }

  private static List<ContentSectionInfo> toSectionInfo(List<RestSection> sectionList)
  {
    return sectionList
        .stream()
        .map(restSection -> new ContentSectionInfo(restSection.getName(),
            (null == restSection.getFileId()) ? EMPTY_FILE_ID : restSection.getFileId(),
            restSection.getIndex()))
        .collect(Collectors.toList());
  }
}
