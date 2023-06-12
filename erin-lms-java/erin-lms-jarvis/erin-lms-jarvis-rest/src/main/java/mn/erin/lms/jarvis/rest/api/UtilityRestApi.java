package mn.erin.lms.jarvis.rest.api;

import java.io.Serializable;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import mn.erin.aim.ldap.config.LdapConfig;
import mn.erin.aim.ldap.config.LdapConstants;
import mn.erin.alfresco.client.ApiException;
import mn.erin.alfresco.client.api.NodesApi;
import mn.erin.alfresco.client.model.NodeBodyUpdate;
import mn.erin.alfresco.client.model.NodeEntry;
import mn.erin.alfresco.connector.AlfrescoClient;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AimConfigProvider;
import mn.erin.domain.dms.model.folder.Folder;
import mn.erin.domain.dms.model.folder.FolderId;
import mn.erin.domain.dms.repository.DMSRepositoryException;
import mn.erin.domain.dms.repository.FolderRepository;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.domain.model.classroom_course.ClassroomCourseAttendance;
import mn.erin.lms.base.domain.repository.ClassroomCourseAttendanceRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.scorm.constants.DataModelConstants;
import mn.erin.lms.base.scorm.model.DataModel;
import mn.erin.lms.base.scorm.model.RuntimeData;
import mn.erin.lms.base.scorm.repository.RuntimeDataRepository;
import mn.erin.lms.jarvis.domain.SetupLms;
import mn.erin.lms.jarvis.domain.report.model.LearnerSuccess;
import mn.erin.lms.jarvis.domain.report.repository.LearnerSuccessRepository;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Utility REST API")
@RequestMapping(value = "/utility")
@RestController
public class UtilityRestApi
{
  private static final Logger LOGGER = LoggerFactory.getLogger(UtilityRestApi.class);
  private final GroupRepository groupRepository;
  private final LmsRepositoryRegistry lmsRepositoryRegistry;
  private final LmsServiceRegistry lmsServiceRegistry;
  private final FolderRepository folderRepository;
  private final LmsDepartmentService lmsDepartmentService;
  private final RuntimeDataRepository runtimeDataRepository;
  private final LearnerSuccessRepository learnerSuccessRepository;
  private final AimConfigProvider aimConfigProvider;
  private final ClassroomCourseAttendanceRepository attendanceRepository;

  @Inject
  private LdapConfig ldapConfig;

  public UtilityRestApi(GroupRepository groupRepository, LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry,
      FolderRepository folderRepository, LmsDepartmentService lmsDepartmentService, RuntimeDataRepository runtimeDataRepository,
      LearnerSuccessRepository learnerSuccessRepository, AimConfigProvider aimConfigProvider, ClassroomCourseAttendanceRepository attendanceRepository)
  {
    this.groupRepository = groupRepository;
    this.lmsRepositoryRegistry = lmsRepositoryRegistry;
    this.lmsServiceRegistry = lmsServiceRegistry;
    this.folderRepository = folderRepository;
    this.lmsDepartmentService = lmsDepartmentService;
    this.runtimeDataRepository = runtimeDataRepository;
    this.learnerSuccessRepository = learnerSuccessRepository;
    this.aimConfigProvider = aimConfigProvider;
    this.attendanceRepository = attendanceRepository;
  }

  @ApiOperation("Set up LMS")
  @PostMapping("/setup")
  public ResponseEntity<RestResult> setup()
  {
    SetupLms setupLms = new SetupLms(lmsRepositoryRegistry, lmsServiceRegistry);
    setupLms.execute();
    return RestResponse.success();
  }

  @ApiOperation("Sends an email")
  @GetMapping("/email")
  public ResponseEntity<RestResult> sendEmail(
      @RequestParam String subject,
      @RequestParam String recipient,
      @RequestParam String body
  )
  {
    Map<String, Object> templateData = new HashMap<>();
    templateData.put("body", body);
    lmsServiceRegistry.getNotificationService().sendEmail(recipient, subject, "test-template.ftl", templateData);
    return RestResponse.success();
  }

  @ApiOperation("Converts PDF to JPG")
  @PostMapping("/convert/{nodeId}")
  public ResponseEntity<RestResult> convert(@PathVariable String nodeId)
  {
    return RestResponse.success();
  }

  @ApiOperation("Renames all certificate files to \"certificate\"")
  @GetMapping("/rename-certificates")
  public ResponseEntity<RestResult> renameCertificates() throws DMSRepositoryException
  {
    Folder learnerCertificateRoot = folderRepository.getFolder(FolderId.valueOf("-root-"), "Learner-Certificates");
    Map<String, String> allCertificates = new HashMap<>();
    for (Folder courseCertificateFolder : folderRepository.listFolders(learnerCertificateRoot.getFolderId()))
    {
      for (Folder courseCertificate : folderRepository.listFolders(courseCertificateFolder.getFolderId()))
      {
        for (Folder certificateFile : folderRepository.listFolders(courseCertificate.getFolderId()))
        {
          allCertificates.put(certificateFile.getFolderId().getId(), certificateFile.getFolderName())/*NODE ID*/;
        }
      }
    }
    NodesApi nodesApi = new AlfrescoClient().getNodesApi();
    for (String key : allCertificates.keySet())
    {

      try
      {
        if (allCertificates.get(key).contains("JPG"))
        {
          nodesApi.updateNode(key, new NodeBodyUpdate().name("certificate.JPG"), Collections.emptyList(), Collections.emptyList());
        }
        else if (allCertificates.get(key).contains("PDF"))
        {
          nodesApi.updateNode(key, new NodeBodyUpdate().name("certificate.PDF"), Collections.emptyList(), Collections.emptyList());
        }
      }
      catch (ApiException e)
      {
        LOGGER.info("Certificate doesn't contain PDF or JPG");
      }
    }
    return RestResponse.success();
  }

  @ApiOperation("Checks alfresco connectivity")
  @GetMapping("/check-alfresco")
  public ResponseEntity<String> checkAlfresco() throws ApiException
  {
    NodesApi nodesApi = new AlfrescoClient().getNodesApi();
    NodeEntry rootNode = nodesApi.getNode("-root-", null, null, null);
    if (rootNode != null && rootNode.getEntry() != null)
    {
      return ResponseEntity.ok("Alfresco OK, root node: " + rootNode.getEntry().getId());
    }
    return ResponseEntity.status(500).body("Failed to get Alfresco root node");
  }

  @ApiOperation("Checks LDAP connectivity")
  @GetMapping("/check-ldap")
  public ResponseEntity<String> checkLdapSsl()
  {
    Properties connectionProperties = new Properties();
    connectionProperties.put(Context.INITIAL_CONTEXT_FACTORY, LdapConstants.JNDI_LDAP_CTX_FACTORY);
    connectionProperties.put(Context.PROVIDER_URL, ldapConfig.getUrl());
    connectionProperties.put(Context.SECURITY_PRINCIPAL, ldapConfig.getUsername());
    connectionProperties.put(Context.SECURITY_CREDENTIALS, ldapConfig.getPassword());
    connectionProperties.put(Context.SECURITY_AUTHENTICATION, "simple");

    Exception nonSecureConnectionException = ldapConnect(connectionProperties);
    connectionProperties.put(Context.SECURITY_PROTOCOL, "ssl");
    Exception secureConnectionException = ldapConnect(connectionProperties);

    if (secureConnectionException != null && nonSecureConnectionException != null)
    {
      return new ResponseEntity<>("LDAP server connection fails, with properties ["
          + "url=" + ldapConfig.getUrl() + ","
          + "baseDn=" + ldapConfig.getBaseDn() + ","
          + "username=" + ldapConfig.getUsername()
          + "]:\n"
          + "{\n"
          + secureConnectionException.getMessage() + ",\n"
          + nonSecureConnectionException.getMessage()
          + "\n}"
          , HttpStatus.INTERNAL_SERVER_ERROR);
    }
    else if (secureConnectionException != null)
    {
      return new ResponseEntity<>("LDAP non-secure connection is OK, but secure connection fails: " + secureConnectionException.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    else if (nonSecureConnectionException != null)
    {
      return new ResponseEntity<>("LDAP secure connection is OK, but non-secure connection fails: " + nonSecureConnectionException.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return ResponseEntity.ok("LDAP connection is OK");
  }

  private Exception ldapConnect(Properties connectionProperties)
  {
    try
    {
      DirContext dirContext = new InitialDirContext(connectionProperties);
      String filter = MessageFormat.format("(objectClass={0})", ldapConfig.getObjectClass());
      SearchControls controls = new SearchControls();
      controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      NamingEnumeration<SearchResult> result = dirContext.search(ldapConfig.getBaseDn(), filter, controls);
      if (result.hasMore())
      {
        result.next();
      }
      result.close();
    }
    catch (NamingException e)
    {
      return e;
    }
    return null;
  }

  @ApiOperation("Generate learner success")
  @GetMapping("/generate-learner-success")
  public ResponseEntity<RestResult> generateLearnerSuccess()
  {
    Date now = convertToDate(LocalDate.now());

    List<Group> rootGroups;
    try
    {
      rootGroups = groupRepository.getAllRootGroups(aimConfigProvider.getDefaultTenantId());
    }
    catch (AimRepositoryException e)
    {
      return RestResponse.internalError(e.getMessage());
    }

    if (rootGroups == null || rootGroups.isEmpty())
    {
      return RestResponse.badRequest("Root group couldn't found");
    }

    Set<String> allLearners = new HashSet<>();

    System.out.println(rootGroups.size());

    for (Group group : rootGroups)
    {
      allLearners.addAll(lmsDepartmentService.getAllLearners(group.getId().getId()));
    }

    Date startDate = convertToDate(LocalDate.of(2020, 4, 1));
    Date endDate = convertToDate(LocalDate.of(2020, 5, 1));

    List<LearnerSuccess> groupLearnerSuccess = new ArrayList<>();

    while (endDate.before(now))
    {
      for (String learner : allLearners)
      {
        double performance = 0;
        LearnerScore score = getTotalAndMaxScore(
            runtimeDataRepository.listRunTimeDataTestCompleted(learner, convertToLocalDate(startDate), convertToLocalDate(endDate)));

        List<Double> progresses = runtimeDataRepository.listRunTimeDataProgress(learner, convertToLocalDate(startDate), convertToLocalDate(endDate));
        if (!progresses.isEmpty())
        {
          performance = progresses.stream().reduce(Double::sum).orElse(0.0) / progresses.size();
        }

        groupLearnerSuccess.add(new LearnerSuccess(learner, "online-course",score.getScore(), score.getMaxScore(), performance, convertToLocalDate(startDate).getYear(),
            convertToLocalDate(startDate).getMonthValue()));
      }
      startDate = convertToDate(convertToLocalDate(startDate).plusMonths(1));
      endDate = convertToDate(convertToLocalDate(endDate).plusMonths(1));
    }
    learnerSuccessRepository.saveAll(groupLearnerSuccess);
    return RestResponse.success();
  }

  @ApiOperation("Adds old classroom course invitation sent state")
  @GetMapping("/classroom-invitation")
  public ResponseEntity<RestResult> fixClassroom()
  {
    List<ClassroomCourseAttendance> classroomCourseAttendances;
    classroomCourseAttendances = attendanceRepository.fetchAll();
    for ( ClassroomCourseAttendance classroomCourseAttendance : classroomCourseAttendances){
      classroomCourseAttendance.getAttendances().forEach(attendance -> attendance.setInvited(true));
      attendanceRepository.save(classroomCourseAttendance);
    }
    return RestResponse.success("Successfully fixed " + classroomCourseAttendances.size() + " classroom courses");
  }

  public LocalDate convertToLocalDate(Date dateToConvert)
  {
    return dateToConvert.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate();
  }

  public Date convertToDate(LocalDate date)
  {
    return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  private LearnerScore getTotalAndMaxScore(List<RuntimeData> runtimeDataList)
  {
    Validate.notNull(runtimeDataList);
    if (runtimeDataList.isEmpty())
    {
      return new LearnerScore();
    }
    int totalScore = 0;
    int maxScore = 0;

    for (RuntimeData runtimeData : runtimeDataList)
    {
      Map<DataModel, Serializable> data = runtimeData.getData();

      for (Map.Entry<DataModel, Serializable> dataEntry : data.entrySet())
      {
        if (dataEntry.getKey().getName().equals(DataModelConstants.CMI_SCORE_RAW))
        {
          totalScore += Integer.parseInt((String) dataEntry.getValue());
        }
        else if (dataEntry.getKey().getName().equals(DataModelConstants.CMI_SCORE_MAX))
        {
          maxScore += Integer.parseInt((String) dataEntry.getValue());
        }
      }
    }

    return new LearnerScore(totalScore, maxScore);
  }

  private static class LearnerScore
  {
    private final int score;
    private final int maxScore;

    public LearnerScore()
    {
      this.score = 0;
      this.maxScore = 0;
    }

    public LearnerScore(int score, int maxScore)
    {
      this.score = score;
      this.maxScore = maxScore;
    }

    public int getScore()
    {
      return score;
    }

    public int getMaxScore()
    {
      return maxScore;
    }
  }
}
