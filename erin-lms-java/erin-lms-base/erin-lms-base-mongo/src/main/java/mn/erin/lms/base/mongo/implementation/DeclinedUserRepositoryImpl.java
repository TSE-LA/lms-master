package mn.erin.lms.base.mongo.implementation;

import java.util.stream.Collectors;

import mn.erin.lms.base.domain.model.exam.DeclinedUser;
import mn.erin.lms.base.domain.model.exam.DeclinedUserInfo;
import mn.erin.lms.base.domain.repository.DeclinedUserRepository;
import mn.erin.lms.base.mongo.document.exam.MongoDeclinedUser;
import mn.erin.lms.base.mongo.document.exam.MongoDeclinedUserInfo;
import mn.erin.lms.base.mongo.repository.MongoDeclinedUserRepository;

/**
 * @author Galsan Bayart.
 */
public class DeclinedUserRepositoryImpl implements DeclinedUserRepository
{
  private final MongoDeclinedUserRepository mongoDeclinedUserRepository;

  public DeclinedUserRepositoryImpl(MongoDeclinedUserRepository mongoDeclinedUserRepository)
  {
    this.mongoDeclinedUserRepository = mongoDeclinedUserRepository;
  }

  @Override
  public boolean save(DeclinedUser declinedUser)
  {
    mongoDeclinedUserRepository.save(mapToMongoDeclinedUser(declinedUser));
    return true;
  }

  private MongoDeclinedUser mapToMongoDeclinedUser(DeclinedUser declinedUser){
    return new MongoDeclinedUser(declinedUser.getExamId(), declinedUser.getDeclinedUserInfos().stream().map(this::mapToMongoDeclinedUserInfo).collect(Collectors.toList()));
  }

  private MongoDeclinedUserInfo mapToMongoDeclinedUserInfo(DeclinedUserInfo declinedUserInfo){
    return new MongoDeclinedUserInfo(declinedUserInfo.getUserId(), declinedUserInfo.getReason());
  }
}
