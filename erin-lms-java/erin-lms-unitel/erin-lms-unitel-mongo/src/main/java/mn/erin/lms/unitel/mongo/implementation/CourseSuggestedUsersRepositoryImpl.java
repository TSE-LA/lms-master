package mn.erin.lms.unitel.mongo.implementation;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.lms.base.domain.repository.CourseSuggestedUsersRepository;
import mn.erin.lms.unitel.mongo.document.MongoCourseSuggestedUsers;
import mn.erin.lms.unitel.mongo.repository.MongoCourseSuggestedUsersRepository;

/**
 * @author Erdenetulga
 */
public class CourseSuggestedUsersRepositoryImpl implements CourseSuggestedUsersRepository
{
  private final MongoCourseSuggestedUsersRepository mongoCourseSuggestedUsersRepository;

  public CourseSuggestedUsersRepositoryImpl(MongoCourseSuggestedUsersRepository mongoCourseSuggestedUsersRepository)
  {
    this.mongoCourseSuggestedUsersRepository = mongoCourseSuggestedUsersRepository;
  }

  @Override
  public boolean saveUsers(String courseId, Set<String> suggestedUsers, Set<String> removedUsers)
  {

    MongoCourseSuggestedUsers mongoCourseSuggestedUsers = mongoCourseSuggestedUsersRepository.findByCourseId(courseId);
    if (mongoCourseSuggestedUsers != null)
    {
      mongoCourseSuggestedUsers.removeSuggestedUsers(removedUsers);
      mongoCourseSuggestedUsers.addSuggestedUsers(suggestedUsers);
      Set<String> duplicateSet = new HashSet<>();
      Set<String> removedDuplicates = mongoCourseSuggestedUsers.getSuggestedUsers().stream().filter(duplicateSet::add).collect(Collectors.toSet());
      removedDuplicates = new HashSet<>(removedDuplicates);
      mongoCourseSuggestedUsers.setSuggestedUsers(removedDuplicates);
      mongoCourseSuggestedUsersRepository.deleteByCourseId(courseId);
      mongoCourseSuggestedUsersRepository.save(mongoCourseSuggestedUsers);
    }
    else
    {
      mongoCourseSuggestedUsers = new MongoCourseSuggestedUsers(courseId, suggestedUsers);
      mongoCourseSuggestedUsersRepository.save(mongoCourseSuggestedUsers);
    }

    return true;
  }

  @Override
  public boolean updateUsers(String courseId, Set<String> suggestedUsers)
  {

    MongoCourseSuggestedUsers mongoCourseSuggestedUsers = mongoCourseSuggestedUsersRepository.findByCourseId(courseId);
    if (mongoCourseSuggestedUsers != null)
    {
      mongoCourseSuggestedUsers.setSuggestedUsers(suggestedUsers);
      Set<String> duplicateSet = new HashSet<>();
      Set<String> removedDuplicates = mongoCourseSuggestedUsers.getSuggestedUsers().stream().filter(duplicateSet::add).collect(Collectors.toSet());
      removedDuplicates = new HashSet<>(removedDuplicates);
      mongoCourseSuggestedUsersRepository.deleteByCourseId(courseId);
      mongoCourseSuggestedUsers.setSuggestedUsers(removedDuplicates);
      mongoCourseSuggestedUsersRepository.save(mongoCourseSuggestedUsers);
    }
    else
    {
      mongoCourseSuggestedUsers = new MongoCourseSuggestedUsers(courseId, suggestedUsers);
      mongoCourseSuggestedUsersRepository.save(mongoCourseSuggestedUsers);
    }

    return true;
  }

  @Override
  public boolean saveGroups(String courseId, Set<String> suggestedGroups)
  {
    MongoCourseSuggestedUsers mongoCourseSuggestedUsers = mongoCourseSuggestedUsersRepository.findByCourseId(courseId);
    if (mongoCourseSuggestedUsers != null)
    {
      mongoCourseSuggestedUsers.addSuggestedGroups(suggestedGroups);
      Set<String> duplicateSet = new HashSet<>();
      Set<String> removedDuplicates = mongoCourseSuggestedUsers.getSuggestedGroups().stream().filter(duplicateSet::add).collect(Collectors.toSet());
      removedDuplicates = new HashSet<>(removedDuplicates);
      mongoCourseSuggestedUsers.setSuggestedGroups(removedDuplicates);
      mongoCourseSuggestedUsersRepository.deleteByCourseId(courseId);
      mongoCourseSuggestedUsersRepository.save(mongoCourseSuggestedUsers);
    }
    else
    {
      mongoCourseSuggestedUsers = new MongoCourseSuggestedUsers(courseId, suggestedGroups);
      mongoCourseSuggestedUsersRepository.save(mongoCourseSuggestedUsers);
    }
    return true;
  }

  @Override
  public boolean updateGroups(String courseId, Set<String> suggestedGroups)
  {
    MongoCourseSuggestedUsers mongoCourseSuggestedUsers = mongoCourseSuggestedUsersRepository.findByCourseId(courseId);
    if (mongoCourseSuggestedUsers != null)
    {
      mongoCourseSuggestedUsers.setSuggestedGroups(suggestedGroups);
      Set<String> duplicateSet = new HashSet<>();
      Set<String> removedDuplicates = mongoCourseSuggestedUsers.getSuggestedGroups().stream().filter(duplicateSet::add).collect(Collectors.toSet());
      removedDuplicates = new HashSet<>(removedDuplicates);
      mongoCourseSuggestedUsers.setSuggestedGroups(removedDuplicates);
      mongoCourseSuggestedUsersRepository.deleteByCourseId(courseId);
    }
    else
    {
      mongoCourseSuggestedUsers = new MongoCourseSuggestedUsers(courseId, suggestedGroups);
    }
    mongoCourseSuggestedUsersRepository.save(mongoCourseSuggestedUsers);
    return true;
  }

  @Override
  public Map<String, Set<String>> fetchAll(String courseId)
  {
    Map<String, Set<String>> result = new HashMap<>();
    MongoCourseSuggestedUsers mongoCourseSuggestedUsers = mongoCourseSuggestedUsersRepository.findByCourseId(courseId);
    result.put("users", mongoCourseSuggestedUsers != null ? mongoCourseSuggestedUsers.getSuggestedUsers() : Collections.emptySet());
    result.put("groups", mongoCourseSuggestedUsers != null ? mongoCourseSuggestedUsers.getSuggestedGroups() : Collections.emptySet());
    return result;
  }
  @Override
  public Set<String> getSuggestedUsers(String courseId)
  {
    MongoCourseSuggestedUsers mongoCourseSuggestedUsers = mongoCourseSuggestedUsersRepository.findByCourseId(courseId);
    return new HashSet<>(mongoCourseSuggestedUsers != null ? mongoCourseSuggestedUsers.getSuggestedUsers() : Collections.emptySet());
  }
  @Override
  public boolean deleteSuggestedUser(String courseId)
  {
    mongoCourseSuggestedUsersRepository.deleteByCourseId(courseId);
    return true;
  }
}
