package mn.erin.lms.base.analytics.repository.mongo.implementation;

import mn.erin.lms.base.analytics.repository.mongo.constants.MongoCollectionProvider;
import mn.erin.lms.base.analytics.service.UserService;

/**
 * @author Munkh
 */
public class AnalyticRepositoryImpl extends AnalyticRepository
{
  public AnalyticRepositoryImpl(MongoCollectionProvider mongoCollectionProvider,
      UserService userService)
  {
    super(mongoCollectionProvider, userService);
  }
}
