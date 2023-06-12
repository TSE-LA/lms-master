package mn.erin.lms.base.domain.usecase;


import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.aim.LmsUserService;

/**
 * @author Bat-Erdene Tsogoo.
 */
public abstract class LmsUseCase<I, O> implements UseCase<I, O>
{
  protected final LmsRepositoryRegistry lmsRepositoryRegistry;
  protected final LmsServiceRegistry lmsServiceRegistry;

  protected LmsUseCase(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    this.lmsRepositoryRegistry = lmsRepositoryRegistry;
    this.lmsServiceRegistry = lmsServiceRegistry;
  }

  protected abstract O executeImpl(I input) throws UseCaseException, LmsRepositoryException;

  @Override
  public O execute(I input) throws UseCaseException
  {
    checkAuthorization();
    try
    {
      return executeImpl(input);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private void checkAuthorization()
  {
    Authorized annotation = this.getClass().getAnnotation(Authorized.class);
    Class<?>[] users = annotation.users();
    LmsUserService lmsUserService = lmsServiceRegistry.getLmsUserService();
    LmsUser currentUser = lmsUserService.getCurrentUser();

    boolean isAuthorized = false;
    for (Class<?> user : users)
    {
      if (user.isInstance(currentUser))
      {
        isAuthorized = true;
        break;
      }
    }

    if (!isAuthorized)
    {
      throw new AuthorizationException("Unauthorized access");
    }
  }
}
