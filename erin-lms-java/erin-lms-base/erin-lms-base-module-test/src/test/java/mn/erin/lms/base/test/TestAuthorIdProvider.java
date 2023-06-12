package mn.erin.lms.base.test;

import mn.erin.lms.base.aim.AuthorIdProvider;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class TestAuthorIdProvider implements AuthorIdProvider
{
  private static final String AUTHOR = "Edgar Allen Poe";

  @Override
  public String getAuthorId()
  {
    return AUTHOR;
  }
}
