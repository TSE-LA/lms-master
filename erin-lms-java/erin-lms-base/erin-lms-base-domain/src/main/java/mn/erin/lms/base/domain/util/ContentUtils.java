package mn.erin.lms.base.domain.util;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.lms.base.domain.model.content.AttachmentId;
import mn.erin.lms.base.domain.model.content.CourseModule;
import mn.erin.lms.base.domain.model.content.CourseSection;
import mn.erin.lms.base.domain.service.LmsFileSystemService;
import mn.erin.lms.base.domain.usecase.certificate.dto.CreateCertificateInput;
import mn.erin.lms.base.domain.usecase.content.dto.ModuleInfo;
import mn.erin.lms.base.domain.usecase.content.dto.SectionDto;
import mn.erin.lms.base.domain.usecase.content.dto.SectionInfo;

/**
 * author Tamir Batmagnai.
 */
public final class ContentUtils
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ContentUtils.class);

  private static final String EMPTY_FILE_ID = "empty";

  private ContentUtils()
  {
  }

  public static List<CourseModule> toCourseModules(List<ModuleInfo> modulesList)
  {
    List<CourseModule> courseModuleList = new ArrayList<>();

    for (ModuleInfo moduleInfo : modulesList)
    {
      CourseModule courseModule = new CourseModule(moduleInfo.getName(), moduleInfo.getIndex(), moduleInfo.getFileType(), moduleInfo.getModuleFolderId());
      List<CourseSection> courseSectionList = toCourseSections(moduleInfo.getSectionInfoList());

      for (CourseSection courseSection : courseSectionList)
      {
        courseModule.addSection(courseSection);
      }

      courseModuleList.add(courseModule);
    }

    return courseModuleList;
  }

  public static List<ModuleInfo> toModuleInfos(List<CourseModule> courseModules)
  {
    return courseModules
        .stream()
        .map(courseModule -> new ModuleInfo(courseModule.getName(), toSectionInfos(courseModule.getSectionList()), courseModule.getIndex(),
            courseModule.getFileType(), courseModule.getModuleFolderId()))
        .collect(Collectors.toList());
  }

  public static List<LmsFileSystemService.ModuleDetail> getModuleDetails(Map<String, SectionDto> sectionFiles, List<ModuleInfo> modules)
  {
    List<LmsFileSystemService.ModuleDetail> moduleDetails = new ArrayList<>();

    for (ModuleInfo module : modules)
    {
      try
      {
        Map<String, byte[]> contentMap = convertSectionFiles(sectionFiles, module.getSectionInfoList());
        String moduleName = module.getName().trim();
        LmsFileSystemService.ModuleDetail moduleDetail = new LmsFileSystemService.ModuleDetail(moduleName, module.getIndex(), contentMap, module.getFileType(),
            module.getModuleFolderId());
        moduleDetails.add(moduleDetail);
      }
      catch (IOException e)
      {
        LOGGER.error(e.getMessage(), e);
      }
    }

    return moduleDetails;
  }

  public static void setSectionFiles(List<ModuleInfo> courseModules, Map<String, String> sectionFileIdMap)
  {
    List<SectionInfo> allSections = new ArrayList<>();

    for (ModuleInfo module : courseModules)
    {
      allSections.addAll(module.getSectionInfoList());
    }

    replaceBySectionFileId(allSections, sectionFileIdMap);
  }

  private static void replaceBySectionFileId(List<SectionInfo> allSections, Map<String, String> sectionFileIdMap)
  {
    for (Map.Entry<String, String> entry : sectionFileIdMap.entrySet())
    {
      String sectionId = entry.getKey();
      String fileId = entry.getValue();

      for (SectionInfo section : allSections)
      {
        String id = section.getFileId();

        if (!StringUtils.isBlank(id) && id.equals(sectionId))
        {
          section.setFileId(fileId);
        }
      }
    }
  }

  private static Map<String, byte[]> convertSectionFiles(Map<String, SectionDto> sectionFiles,
      List<SectionInfo> sections) throws IOException
  {
    Map<String, byte[]> result = new HashMap<>();

    for (SectionInfo sectionInfo : sections)
    {
      if (!StringUtils.isBlank(sectionInfo.getFileId()))
      {
        byte[] content = sectionFiles.get(sectionInfo.getFileId()) != null ?
            Files.readAllBytes(sectionFiles.get(sectionInfo.getFileId()).getFile()) : null;
        result.put(sectionInfo.getFileId(), content);
      }
    }

    return result;
  }

  private static List<SectionInfo> toSectionInfos(List<CourseSection> sectionList)
  {
    return sectionList
        .stream()
        .map(courseSection -> new SectionInfo(courseSection.getName(), courseSection.getAttachmentId().getId(), courseSection.getIndex()))
        .collect(Collectors.toList());
  }

  private static List<CourseSection> toCourseSections(List<SectionInfo> sectionInfoList)
  {
    List<CourseSection> courseSections = new ArrayList<>();

    for (SectionInfo sectionInfo : sectionInfoList)
    {
      AttachmentId attachmentId = AttachmentId.valueOf((null == sectionInfo.getFileId()) ? EMPTY_FILE_ID : sectionInfo.getFileId());
      CourseSection courseSection = new CourseSection(sectionInfo.getName(), attachmentId, sectionInfo.getIndex());
      courseSections.add(courseSection);
    }

    return courseSections;
  }

  public static Map<String, byte[]> convertCertificateFiles(CreateCertificateInput input) throws IOException {
    Map<String, byte[]> certificateFileMap = new HashMap<>();
    byte[] file = Files.readAllBytes(input.getFile().toPath());
    certificateFileMap.put(input.getId(), file);
    return certificateFileMap;
  }
}
