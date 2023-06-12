package mn.erin.lms.base.rest.model.exam;

import org.junit.Test;

import mn.erin.infrastucture.rest.common.response.RestEntity;

import static org.junit.Assert.assertNull;

/**
 * @author mLkhagvasuren
 */
public class RestOngoingExamTest
{
  @Test
  public void check_null_for_rest_entity()
  {
    assertNull(RestEntity.of(null).getEntity());
  }
}
