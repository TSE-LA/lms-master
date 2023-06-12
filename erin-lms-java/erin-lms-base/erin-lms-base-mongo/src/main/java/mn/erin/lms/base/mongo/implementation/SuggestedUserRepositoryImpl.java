package mn.erin.lms.base.mongo.implementation;

import mn.erin.lms.base.domain.model.exam.SuggestedUser;
import mn.erin.lms.base.domain.repository.SuggestedUserRepository;
import mn.erin.lms.base.mongo.document.exam.MongoSuggestedUser;
import mn.erin.lms.base.mongo.repository.MongoSuggestedUserRepository;

/**
 * @author Galsan Bayart.
 */
public class SuggestedUserRepositoryImpl implements SuggestedUserRepository
{
  private final MongoSuggestedUserRepository mongoSuggestedUserRepository;

  public SuggestedUserRepositoryImpl(MongoSuggestedUserRepository mongoSuggestedUserRepository)
  {
    this.mongoSuggestedUserRepository = mongoSuggestedUserRepository;
  }

  @Override
  public boolean save(SuggestedUser suggestedUser)
  {
    mongoSuggestedUserRepository.save(mapToMongoSuggestedUser(suggestedUser));
    return true;
  }

  private MongoSuggestedUser mapToMongoSuggestedUser(SuggestedUser suggestedUser){
    return new MongoSuggestedUser(suggestedUser.getExamId(), suggestedUser.getSuggestedUsers());
  }
}
