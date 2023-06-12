package mn.erin.domain.aim.service;

/**
 * @author Byambajav
 */
public class RealmTypeServiceImpl implements RealmTypeProvider
{
  private final String realmType;
  public RealmTypeServiceImpl(String realmType){
    this.realmType = realmType;
  }
  @Override
  public String getCurrentRealmType()
  {
    return realmType;
  }
}
