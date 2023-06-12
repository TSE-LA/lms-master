/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_content;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import mn.erin.lms.legacy.domain.lms.model.content.AttachmentId;
import mn.erin.lms.legacy.domain.lms.model.content.CourseModule;
import mn.erin.lms.legacy.domain.lms.model.content.CourseSection;

/**
 * author Tamir Batmagnai.
 */
public class ContentUtils
{
  private static final String EMPTY_FILE_ID = "empty";

  private ContentUtils()
  {
  }

  public static List<CourseModule> toCourseModules(List<ContentModuleInfo> modulesList)
  {
    List<CourseModule> courseModuleList = new ArrayList<>();

    for (ContentModuleInfo moduleInfo : modulesList)
    {
      CourseModule courseModule = new CourseModule(moduleInfo.getName(), moduleInfo.getIndex(), moduleInfo.getFileType());
      List<CourseSection> courseSectionList = toCourseSections(moduleInfo.getSectionInfos());

      for (CourseSection courseSection : courseSectionList)
      {
        courseModule.addSection(courseSection);
      }

      courseModuleList.add(courseModule);
    }

    return courseModuleList;
  }

  public static List<ContentModuleInfo> toModuleInfos(List<CourseModule> courseModules)
  {
    return courseModules
        .stream()
        .map(courseModule -> new ContentModuleInfo(courseModule.getName(), toSectionInfos(courseModule.getSectionList()), courseModule.getIndex(), courseModule.getFileType()))
        .collect(Collectors.toList());
  }

  private static List<ContentSectionInfo> toSectionInfos(List<CourseSection> sectionList)
  {
    return sectionList
        .stream()
        .map(courseSection -> new ContentSectionInfo(courseSection.getName(), courseSection.getAttachmentId().getId(), courseSection.getIndex()))
        .collect(Collectors.toList());
  }

  private static List<CourseSection> toCourseSections(List<ContentSectionInfo> sectionInfoList)
  {
    List<CourseSection> courseSections = new ArrayList<>();

    for (ContentSectionInfo sectionInfo : sectionInfoList)
    {
      AttachmentId attachmentId = new AttachmentId((null == sectionInfo.getFileId()) ? EMPTY_FILE_ID : sectionInfo.getFileId());
      CourseSection courseSection = new CourseSection(sectionInfo.getName(), attachmentId, sectionInfo.getIndex());
      courseSections.add(courseSection);
    }

    return courseSections;
  }
}
