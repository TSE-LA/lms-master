package mn.erin.lms.legacy.domain.lms.usecase.course_group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.legacy.domain.lms.model.course.CourseGroup;
import mn.erin.lms.legacy.domain.lms.repository.CourseGroupRepository;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetGroupCourses implements UseCase<GetGroupCourseInput, List<String>>
{
  private static final Logger LOG = LoggerFactory.getLogger(GetGroupCourses.class);

  private final AccessIdentityManagement accessIdentityManagement;
  private final CourseGroupRepository courseGroupRepository;
  private final GroupRepository groupRepository;
  private final MembershipRepository membershipRepository;

  public GetGroupCourses(AccessIdentityManagement accessIdentityManagement, CourseGroupRepository courseGroupRepository,
      GroupRepository groupRepository, MembershipRepository membershipRepository)
  {
    this.accessIdentityManagement = accessIdentityManagement;
    this.courseGroupRepository = courseGroupRepository;
    this.groupRepository = groupRepository;
    this.membershipRepository = membershipRepository;
  }

  @Override
  public List<String> execute(GetGroupCourseInput input)
  {
    Membership membership;
    try
    {
      membership = membershipRepository.findByUsername(accessIdentityManagement.getCurrentUsername());
    }
    catch (AimRepositoryException e)
    {
      LOG.error("Failed to get user membership for current user", e);
      return Collections.emptyList();
    }
    Group group;
    if (input.getGroupId() == null)
    {
      group = groupRepository.findById(membership.getGroupId());
    }
    else
    {
      group = groupRepository.findById(GroupId.valueOf(input.getGroupId()));
    }
    List<CourseGroup> courseGroups = new ArrayList<>(courseGroupRepository.listGroupCourses(group.getId().getId()));
    this.getAllGroupTreeCourses(group.getChildren(), courseGroups);
    return courseGroups.stream().map(courseGroup -> courseGroup.getCourseId().getId()).collect(Collectors.toList());
  }

  private void getAllGroupTreeCourses(List<GroupId> currentUserGroup,List<CourseGroup> courseGroups)
  {
    if (!currentUserGroup.isEmpty()) {
      for (GroupId groupId : currentUserGroup){
        courseGroups.addAll(courseGroupRepository.listGroupCourses(groupId.getId()));
        Group group = groupRepository.findById(groupId);
        getAllGroupTreeCourses(group.getChildren(), courseGroups);
      }
    }
  }
}
