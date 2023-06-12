//TODO: Fix this test
//package mn.erin.lms.base.domain.usecase.exam;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//import mn.erin.domain.base.usecase.UseCaseException;
//import mn.erin.lms.base.aim.organization.OrganizationId;
//import mn.erin.lms.base.domain.model.exam.ExamGroup;
//import mn.erin.lms.base.domain.model.exam.ExamGroupId;
//import mn.erin.lms.base.domain.repository.ExamGroupRepository;
//import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
//import mn.erin.lms.base.domain.service.LmsServiceRegistry;
//import mn.erin.lms.base.domain.service.OrganizationIdProvider;
//
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
///**
// * @author Temuulen Naranbold
// */
//public class GetAllExamGroupTest
//{
//  private static final String ORGANIZATION_ID = "jarvis";
//  private static final String ID = "id";
//
//  private LmsRepositoryRegistry lmsRepositoryRegistry;
//  private LmsServiceRegistry lmsServiceRegistry;
//  private ExamGroupRepository examGroupRepository;
//  private OrganizationIdProvider organizationIdProvider;
//  private GetAllExamGroup getAllExamGroup;
//
//  @Before
//  public void setUp()
//  {
//    lmsRepositoryRegistry = mock(LmsRepositoryRegistry.class);
//    lmsServiceRegistry = mock(LmsServiceRegistry.class);
//    examGroupRepository = mock(ExamGroupRepository.class);
//    organizationIdProvider = mock(OrganizationIdProvider.class);
//
//    when(lmsRepositoryRegistry.getExamGroupRepository()).thenReturn(examGroupRepository);
//    when(lmsServiceRegistry.getOrganizationIdProvider()).thenReturn(organizationIdProvider);
//    when(organizationIdProvider.getOrganizationId()).thenReturn(ORGANIZATION_ID);
//
//    getAllExamGroup = new GetAllExamGroup(lmsRepositoryRegistry, lmsServiceRegistry);
//  }
//
//  @Test(expected = UseCaseException.class)
//  public void whenOrganizationId_isNull() throws UseCaseException
//  {
//    when(organizationIdProvider.getOrganizationId()).thenReturn(null);
//    getAllExamGroup.executeImpl(null);
//  }
//
//  @Test(expected = UseCaseException.class)
//  public void whenRepository_isNull() throws UseCaseException
//  {
//    when(lmsRepositoryRegistry.getExamGroupRepository()).thenReturn(null);
//    getAllExamGroup.executeImpl(null);
//  }
//
//  @Test
//  public void whenSuccess() throws UseCaseException
//  {
//    ExamGroup group = new ExamGroup(ExamGroupId.valueOf(ID), null, "Test", OrganizationId.valueOf(ORGANIZATION_ID), "");
//    List<ExamGroup> groups = new ArrayList<>();
//    groups.add(group);
//    when(examGroupRepository.findByOrganizationId(ORGANIZATION_ID)).thenReturn(groups);
//    Assert.assertEquals(getAllExamGroup.executeImpl(null), groups);
//  }
//
//  @Test
//  public void whenSuccess_withParentId() throws UseCaseException
//  {
//    ExamGroup group = new ExamGroup(ExamGroupId.valueOf(ID), ID, "Test", OrganizationId.valueOf(ORGANIZATION_ID), "");
//    List<ExamGroup> groups = new ArrayList<>();
//    groups.add(group);
//    when(examGroupRepository.findByParentIdAndOrganizationId(ExamGroupId.valueOf(ID), OrganizationId.valueOf(ORGANIZATION_ID))).thenReturn(groups);
//    Assert.assertEquals(getAllExamGroup.executeImpl(ID), groups);
//  }
//}
