package mn.erin.lms.base.domain.model.content;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class AttachmentId extends EntityId
{
  private AttachmentId(String id)
  {
    super(id);
  }

  public static AttachmentId valueOf(String id)
  {
    return new AttachmentId(id);
  }
}
