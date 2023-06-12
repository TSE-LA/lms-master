//package mn.erin.lms.base.domain.usecase.exam;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import mn.erin.domain.base.model.person.PersonId;
//import mn.erin.lms.base.aim.LmsUserService;
//import mn.erin.lms.base.aim.user.LmsAdmin;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Mockito;
//
//import mn.erin.domain.base.usecase.UseCaseException;
//import mn.erin.lms.base.aim.AccessIdentityManagement;
//import mn.erin.lms.base.domain.repository.ExamEnrollmentRepository;
//import mn.erin.lms.base.domain.repository.ExamRepository;
//import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
//import mn.erin.lms.base.domain.service.LmsServiceRegistry;
//import mn.erin.lms.base.domain.usecase.exam.dto.ExamLearnerDto;
//
///**
// * @author Galsan Bayart.
// */
//public class GetLearnerExamListTest {
//    private GetLearnerExamList getLearnerExamList;
//
//    @Before
//    public void setUp() throws Exception {
//        LmsRepositoryRegistry lmsRepositoryRegistry = Mockito.mock(LmsRepositoryRegistry.class);
//        LmsServiceRegistry lmsServiceRegistry = Mockito.mock(LmsServiceRegistry.class);
//        AccessIdentityManagement accessIdentityManagement = Mockito.mock(AccessIdentityManagement.class);
//        ExamEnrollmentRepository examEnrollmentRepository = Mockito.mock(ExamEnrollmentRepository.class);
//        ExamRepository examRepository = Mockito.mock(ExamRepository.class);
//        LmsUserService lmsUserService = Mockito.mock(LmsUserService.class);
//        Mockito.when(lmsUserService.getCurrentUser()).thenReturn(new LmsAdmin(PersonId.valueOf("author")));
//        Mockito.when(lmsServiceRegistry.getLmsUserService()).thenReturn(lmsUserService);
//        Mockito.when(lmsServiceRegistry.getAccessIdentityManagement()).thenReturn(accessIdentityManagement);
//        Mockito.when(lmsRepositoryRegistry.getExamEnrollmentRepository()).thenReturn(examEnrollmentRepository);
//        Mockito.when(lmsRepositoryRegistry.getExamRepository()).thenReturn(examRepository);
//        Mockito.when(examEnrollmentRepository.getAllReadByUserId(Mockito.anyString())).thenReturn(ExamTestUtils.getExamEnrollments());
//        Mockito.when(examRepository.getAll(Mockito.anySet())).thenReturn(ExamTestUtils.getExams());
//        Mockito.when(accessIdentityManagement.getCurrentUsername()).thenReturn(ExamTestUtils.PERSON_ID);
//        getLearnerExamList = new GetLearnerExamList(lmsRepositoryRegistry, lmsServiceRegistry);
//    }
//
//    @Test
//    public void whenGetExamForRead_success() throws UseCaseException {
//        Assert.assertTrue(isEqual(getExpected(), getLearnerExamList.execute(null)));
//    }
//
//    private boolean isEqual(List<ExamLearnerDto> expected, List<ExamLearnerDto> execute) {
//        if (expected.size() != execute.size()) {
//            return false;
//        }
//
//        for (ExamLearnerDto examLearnerDto : expected) {
//            List<ExamLearnerDto> resultDto = execute.stream().filter(f -> f.getId().equals(examLearnerDto.getId())).collect(Collectors.toList());
//            if (resultDto.size() != 1) {
//                return false;
//            }
//            if (!isDtoEqual(resultDto.get(0), examLearnerDto)) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    private boolean isDtoEqual(ExamLearnerDto executedConfigure, ExamLearnerDto expectedConfigure) {
//        if (!executedConfigure.getId().equals(expectedConfigure.getId())) {
//            return false;
//        }
//        if (executedConfigure.getThresholdScore() != expectedConfigure.getThresholdScore()) {
//            return false;
//        }
//
//        return executedConfigure.getExamState().equals(expectedConfigure.getExamState());
//    }
//
//
//    private List<ExamLearnerDto> getExpected() {
//        ExamLearnerDto examLearnerDto1 = new ExamLearnerDto("examId1", 1, "NEW");
//        ExamLearnerDto examLearnerDto2 = new ExamLearnerDto("examId2", 1, "NEW");
//        return Arrays.asList(examLearnerDto1, examLearnerDto2);
//    }
//}
