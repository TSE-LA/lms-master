package mn.erin.lms.base.domain.service;

/**
 * @author mLkhagvasuren
 */
public final class TaskCancellationResult
{
  private boolean complete;
  private boolean cancelled;

  public boolean isComplete()
  {
    return complete;
  }

  public boolean isCancelled()
  {
    return cancelled;
  }

  public void complete(boolean complete)
  {
    this.complete = complete;
  }

  public void cancelled(boolean cancelled)
  {
    this.cancelled = cancelled;
  }
}
