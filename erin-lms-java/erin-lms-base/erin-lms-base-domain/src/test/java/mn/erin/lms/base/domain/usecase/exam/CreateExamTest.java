//package mn.erin.lms.base.domain.usecase.exam;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import mn.erin.lms.base.domain.model.exam.*;
//import mn.erin.lms.base.domain.repository.ExamEnrollmentRepository;
//
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Mockito;
//
//import mn.erin.domain.base.usecase.UseCaseException;
//import mn.erin.lms.base.aim.AccessIdentityManagement;
//import mn.erin.lms.base.domain.model.category.ExamCategoryId;
//import mn.erin.lms.base.domain.repository.ExamRepository;
//import mn.erin.lms.base.domain.repository.LmsRepositoryException;
//import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
//import mn.erin.lms.base.domain.service.LmsServiceRegistry;
//import mn.erin.lms.base.domain.usecase.exam.dto.ExamInput;
//import mn.erin.lms.base.domain.usecase.exam.dto.CreateExamOutput;
//
///**
// * @author Temuulen Naranbold
// */
//public class CreateExamTest
//{
//  private LmsRepositoryRegistry lmsRepositoryRegistry;
//  private LmsServiceRegistry lmsServiceRegistry;
//  private AccessIdentityManagement accessIdentityManagement;
//  private ExamRepository examRepository;
//  private ExamEnrollmentRepository examEnrollmentRepository;
//  private ExamInput input;
//  private CreateExam createExam;
//
//  @Before
//  public void setUp() throws LmsRepositoryException
//  {
//    lmsRepositoryRegistry = Mockito.mock(LmsRepositoryRegistry.class);
//    lmsServiceRegistry = Mockito.mock(LmsServiceRegistry.class);
//    accessIdentityManagement = Mockito.mock(AccessIdentityManagement.class);
//    examRepository = Mockito.mock(ExamRepository.class);
//    examEnrollmentRepository = Mockito.mock(ExamEnrollmentRepository.class);
//    input = ExamTestUtils.createInput();
//
//    Mockito.when(lmsRepositoryRegistry.getExamRepository()).thenReturn(examRepository);
//    Mockito.when(lmsRepositoryRegistry.getExamEnrollmentRepository()).thenReturn(examEnrollmentRepository);
//    Mockito.when(lmsServiceRegistry.getAccessIdentityManagement()).thenReturn(accessIdentityManagement);
//    Mockito.when(accessIdentityManagement.getCurrentUsername()).thenReturn(ExamTestUtils.PERSON_ID);
//    Mockito.when(lmsServiceRegistry.getAccessIdentityManagement()).thenReturn(accessIdentityManagement);
//
//    createExam = new CreateExam(lmsRepositoryRegistry, lmsServiceRegistry);
//  }
//
//  @Test(expected = UseCaseException.class)
//  public void whenInput_isNull() throws UseCaseException
//  {
//    createExam.executeImpl(null);
//  }
//
//  @Test(expected = UseCaseException.class)
//  public void whenName_isNull() throws UseCaseException
//  {
//    input.setName(null);
//    createExam.executeImpl(input);
//  }
//
//  @Test(expected = UseCaseException.class)
//  public void whenCategoryId_isNull() throws UseCaseException
//  {
//    input.setExamCategoryId(null);
//    createExam.executeImpl(input);
//  }
//
//  @Test
//  public void whenSuccess() throws UseCaseException, LmsRepositoryException
//  {
//    input = ExamTestUtils.createInput();
//    HistoryOfModification historyOfModification = new HistoryOfModification(ExamTestUtils.PERSON_ID, ExamTestUtils.CURRENT_DATE);
//    List<HistoryOfModification> historyOfModifications = new ArrayList<>();
//    historyOfModifications.add(historyOfModification);
//    Mockito.when(examEnrollmentRepository.createEnrollment(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(null);
//
//    Exam exam = new Exam.Builder(ExamId.valueOf(ExamTestUtils.EXAM_ID), ExamTestUtils.PERSON_ID, ExamTestUtils.CURRENT_DATE, input.getName())
//        .withDescription(input.getDescription())
//        .withExamCategoryId(ExamCategoryId.valueOf(input.getExamCategoryId()))
//        .withExamType(ExamType.valueOf(input.getExamType()))
//        .withModifiedDate(ExamTestUtils.CURRENT_DATE)
//        .withExamStatus(ExamStatus.NEW)
//        .withModifiedUser(ExamTestUtils.PERSON_ID)
//        .withHistoryOfModifications(historyOfModifications)
//        .withPublishConfig(createExam.mapToExamPublishConfigure(input))
//        .withExamConfig(createExam.mapToExamConfigure(input)).build();
//
//    Mockito.when(examRepository.create(
//        Mockito.anyString(),
//        Mockito.anyString(),
//        Mockito.any(ExamCategoryId.class),
//        Mockito.any(ExamType.class),
//        Mockito.anyList(),
//        Mockito.any(ExamPublishConfig.class),
//        Mockito.any(ExamConfig.class)
//    )).thenReturn(exam);
//    CreateExamOutput output = createExam.executeImpl(input);
//    Assert.assertEquals(ExamTestUtils.EXAM_ID, output.getExamId());
//  }
//}