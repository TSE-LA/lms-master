package mn.erin.lms.aim.shiro.realm;

import org.apache.shiro.realm.Realm;

public interface RealmFactory
{
  Realm getRealm(RealmType realmType);
}
