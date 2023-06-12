package mn.erin.domain.aim.usecase.authentication;


import mn.erin.domain.aim.service.RealmTypeProvider;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Byambajav
 */
public class GetRealmType implements UseCase<Void, String>
{
  private final RealmTypeProvider realmTypeProvider;

  public GetRealmType(RealmTypeProvider realmTypeProvider)
  {
    this.realmTypeProvider = realmTypeProvider;
  }

  @Override
  public String execute(Void input) throws UseCaseException
  {
    return realmTypeProvider.getCurrentRealmType();
  }
}
