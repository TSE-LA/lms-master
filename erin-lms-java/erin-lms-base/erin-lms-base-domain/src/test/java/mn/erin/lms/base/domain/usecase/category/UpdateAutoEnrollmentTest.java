package mn.erin.lms.base.domain.usecase.category;

import org.junit.Before;
import org.junit.Test;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.usecase.category.dto.UpdateAutoEnrollmentInput;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Dashnyam Bayarsaikhan
 */
public class UpdateAutoEnrollmentTest
{
  private CourseCategoryRepository courseCategoryRepository;

  private UpdateAutoEnrollment updateAutoEnrollment;

  @Before
  public void setUp()
  {
    courseCategoryRepository = mock(CourseCategoryRepository.class);
    updateAutoEnrollment = new UpdateAutoEnrollment(courseCategoryRepository);
  }

  @Test(expected = NullPointerException.class)
  public void input_is_null_throws_null_pointer_exception_test() throws UseCaseException
  {
    updateAutoEnrollment.execute(null);
  }

  @Test(expected = NullPointerException.class)
  public void input_auto_enroll_is_null_throws_null_pointer_exception_test() throws UseCaseException
  {
    UpdateAutoEnrollmentInput input = new UpdateAutoEnrollmentInput("id", null);
    updateAutoEnrollment.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void input_id_is_null_throws_usecase_exception_test() throws UseCaseException
  {
    UpdateAutoEnrollmentInput input = new UpdateAutoEnrollmentInput(null, true);
    updateAutoEnrollment.execute(input);
  }

  @Test
  public void assert_null_enrollment_update_success() throws UseCaseException
  {
    UpdateAutoEnrollmentInput input = new UpdateAutoEnrollmentInput("id", true);
    assertNull(updateAutoEnrollment.execute(input));
  }
}