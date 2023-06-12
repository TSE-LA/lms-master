//package mn.erin.lms.base.domain.usecase.exam;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//import mn.erin.domain.base.model.person.PersonId;
//import mn.erin.lms.base.aim.LmsUserService;
//import mn.erin.lms.base.aim.user.LmsAdmin;
//import mn.erin.lms.base.domain.usecase.exam.dto.*;
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
//
///**
// * @author Galsan Bayart.
// */
//public class GetDetailedExamsTest {
//    private GetDetailedExams getDetailedExams;
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
//        Mockito.when(examRepository.listAll(Mockito.anyString())).thenReturn(ExamTestUtils.getExams());
//        Mockito.when(accessIdentityManagement.getCurrentUsername()).thenReturn(ExamTestUtils.PERSON_ID);
//        getDetailedExams = new GetDetailedExams(lmsRepositoryRegistry, lmsServiceRegistry);
//    }
//
//    @Test
//    public void whenGetDetailedExam_success() throws UseCaseException {
//        Assert.assertTrue(isEqual(getExpected(), getDetailedExams.execute(null)));
//    }
//
//    private boolean isEqual(List<ExamDto> expected, List<ExamDto> actual) {
//        if (expected.size() != actual.size()) {
//            return false;
//        }
//
//        for (ExamDto examDto : expected) {
//            List<ExamDto> matchingDtos = actual.stream().filter(dto -> dto.getId().equals(examDto.getId())).collect(Collectors.toList());
//            if (matchingDtos.size() != 1) {
//                return false;
//            }
//            if (!isDtoEqual(matchingDtos.get(0), examDto)) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    private boolean isDtoEqual(ExamDto actual, ExamDto expected) {
//        if (actual.getDuration() != expected.getDuration()) {
//            return false;
//        }
//        if (actual.getThresholdScore() != expected.getThresholdScore()) {
//            return false;
//        }
//        return actual.getName().equals(expected.getName());
//    }
//
//    private List<ExamDto> getExpected() {
//        ExamDto examDto_first = new ExamDto("examId1", "exam1", 1, "NEW", "author", new Date());
//        ExamDto examDto_second = new ExamDto("examId2", "exam2", 1, "NEW", "author", new Date());
//        return Arrays.asList(examDto_first, examDto_second);
//    }
//
//}