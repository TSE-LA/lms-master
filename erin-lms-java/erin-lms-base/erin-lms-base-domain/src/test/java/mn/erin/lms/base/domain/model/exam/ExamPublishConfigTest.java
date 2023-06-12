package mn.erin.lms.base.domain.model.exam;

import java.time.LocalTime;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author mLkhagvasuren
 */
public class ExamPublishConfigTest
{
  @Test
  public void verify_time_parsing()
  {
    LocalTime time = LocalTime.parse("22:55");
    assertEquals(LocalTime.of(22, 55), time);
    assertEquals(82500000, time.toSecondOfDay() * 1000L);
  }
}
