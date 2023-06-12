package mn.erin.aim.repository.mongo.user;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;

import mn.erin.aim.repository.mongo.user.crud_template.MongoUserProfileRepositoryTemplate;
import mn.erin.aim.repository.mongo.user.document.MongoUserContact;
import mn.erin.aim.repository.mongo.user.document.MongoUserInfo;
import mn.erin.aim.repository.mongo.user.document.MongoUserProfile;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.user.UserContact;
import mn.erin.domain.aim.model.user.UserGender;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserInfo;
import mn.erin.domain.aim.model.user.UserProfile;
import mn.erin.domain.aim.model.user.UserProfileId;
import mn.erin.domain.aim.repository.UserProfileRepository;
import mn.erin.domain.base.model.EntityId;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import static mn.erin.domain.aim.constant.AimErrorMessageConstant.USER_PROFILE_EXISTS;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.USER_PROFILE_NOT_FOUND;

/**
 * @author Munkh
 */
@Repository("userProfileRepository")
public class MongoUserProfileRepository implements UserProfileRepository
{
  private final MongoUserProfileRepositoryTemplate template;

  @Inject
  public MongoUserProfileRepository(MongoUserProfileRepositoryTemplate template)
  {
    this.template = template;
  }

  @Override
  public void create(UserProfile newProfile) throws AimRepositoryException
  {
    if (find(newProfile.getUserId()).isPresent())
    {
      throw new AimRepositoryException(USER_PROFILE_EXISTS);
    }

    MongoUserInfo userInfo = new MongoUserInfo()
        .setFirstName(newProfile.getUserInfo().getFirstName())
        .setLastName(newProfile.getUserInfo().getLastName())
        .setGender(newProfile.getUserInfo().getGender().name())
        .setBirthday(newProfile.getUserInfo().getBirthday())
        .setJobTitle(newProfile.getUserInfo().getJobTitle())
        .setProperties(newProfile.getUserInfo().getProperties())
        .setImageId(newProfile.getUserInfo().getImageId())
        .setFolderName(newProfile.getUserInfo().getFolderName());
    MongoUserContact userContact = new MongoUserContact()
      .setEmail(newProfile.getUserContact().getEmail())
      .setPhoneNumber(newProfile.getUserContact().getPhoneNumber());

    template.save(
      new MongoUserProfile()
          .setId(new ObjectId())
          .setUserId(new ObjectId(newProfile.getUserId().getId()))
        .setInfo(userInfo)
        .setContact(userContact));
  }

  @Override
  public void update(UserProfile updatingProfile) throws AimRepositoryException
  {
    Optional<MongoUserProfile> profileDocument = find(updatingProfile.getUserId());
    if (!profileDocument.isPresent())
    {
      throw new AimRepositoryException(USER_PROFILE_NOT_FOUND);
    }

    MongoUserInfo userInfo = new MongoUserInfo()
        .setFirstName(updatingProfile.getUserInfo().getFirstName())
        .setLastName(updatingProfile.getUserInfo().getLastName())
        .setGender(updatingProfile.getUserInfo().getGender().name())
        .setBirthday(updatingProfile.getUserInfo().getBirthday())
        .setJobTitle(updatingProfile.getUserInfo().getJobTitle())
        .setImageId(updatingProfile.getUserInfo().getImageId())
        .setFolderName(updatingProfile.getUserInfo().getFolderName())
        .setProperties(updatingProfile.getUserInfo().getProperties());
    MongoUserContact userContact = new MongoUserContact()
      .setEmail(updatingProfile.getUserContact().getEmail())
      .setPhoneNumber(updatingProfile.getUserContact().getPhoneNumber());
    profileDocument.get()
      .setInfo(userInfo)
      .setContact(userContact);
    template.save(profileDocument.get());
  }

  @Override
  public boolean delete(UserId userId)
  {
    template.deleteByUserId(new ObjectId(userId.getId()));
    return !find(userId).isPresent();
  }

  @Override
  public UserProfile findByUserId(UserId userId)
  {
    return find(userId).map(this::mapToUserProfile).orElse(null);
  }

  @Override
  public UserProfile findByPhoneNumber(String phoneNumber)
  {
    return template.findByContact_PhoneNumber(phoneNumber).map(this::mapToUserProfile).orElse(null);
  }

  @Override
  public UserProfile findById(EntityId entityId)
  {
    return template.findById(new ObjectId(entityId.getId()))
        .map(this::mapToUserProfile)
        .orElse(null);
  }

  @Override
  public Collection<UserProfile> findAll()
  {
    return template.findAll().stream().map(this::mapToUserProfile).collect(Collectors.toList());
  }

  private Optional<MongoUserProfile> find(UserId userId)
  {
    return template.findByUserId(new ObjectId(userId.getId()));
  }

  private UserProfile mapToUserProfile(MongoUserProfile mongoUserProfile)
  {
    MongoUserInfo info = mongoUserProfile.getInfo();
    MongoUserContact contact = mongoUserProfile.getContact();

    return new UserProfile(
      new UserProfileId(mongoUserProfile.getId().toString()),
      UserId.valueOf(mongoUserProfile.getUserId().toString()),
      new UserInfo(
          info != null ? info.getFirstName() : null,
          info != null ? info.getLastName() : null,
          info != null && info.getGender() != null ? UserGender.valueOf(info.getGender()) : null,
          info != null ? info.getBirthday() : null,
          info != null ? info.getJobTitle() : null,
          info != null ? info.getImageId() : null,
          info != null ? info.getFolderName() : null,
          info != null ? info.getProperties() : null
      ),
      new UserContact(
        contact != null ? contact.getEmail() : null,
        contact != null ? contact.getPhoneNumber() : null)
    );
  }
}
