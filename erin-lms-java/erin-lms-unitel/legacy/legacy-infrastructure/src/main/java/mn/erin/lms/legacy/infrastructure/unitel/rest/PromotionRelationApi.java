package mn.erin.lms.legacy.infrastructure.unitel.rest;

import java.util.List;
import java.util.Set;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.legacy.domain.lms.model.course.CourseGroup;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseGroupRepository;
import mn.erin.lms.legacy.domain.lms.usecase.audit.DeleteCourseAudit;
import mn.erin.lms.legacy.domain.lms.usecase.audit.dto.GetCourseAuditOutput;
import mn.erin.lms.legacy.domain.lms.usecase.audit.GetCourseAudits;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Promotion Relation")
@RequestMapping(value = "/legacy/lms/promotion-relations")
public class PromotionRelationApi
{
  @Inject
  private CourseGroupRepository courseGroupRepository;

  @Inject
  private CourseAuditRepository courseAuditRepository;

  @Inject
  private AccessIdentityManagement accessIdentityManagement;

  @DeleteMapping("/{groupId}")
  public ResponseEntity<RestResult> delete(@PathVariable String groupId)
  {
    Set<String> allGroups = accessIdentityManagement.getSubDepartments(groupId);

    for (String group : allGroups)
    {
      List<CourseGroup> courseGroupList = courseGroupRepository.listGroupCourses(group);

      for (CourseGroup courseGroup : courseGroupList)
      {
        courseGroupRepository.delete(courseGroup.getCourseGroupId());
      }
    }

    Set<String> groupUsers = accessIdentityManagement.getLearners(groupId);

    for (String user : groupUsers)
    {
      GetCourseAudits getCourseAudits = new GetCourseAudits(courseAuditRepository);

      List<GetCourseAuditOutput> courseAudits = getCourseAudits.execute(user);

      DeleteCourseAudit deleteCourseAudit = new DeleteCourseAudit(courseAuditRepository);
      for (GetCourseAuditOutput courseAudit : courseAudits)
      {
        deleteCourseAudit.execute(courseAudit.getId());
      }
    }

    return RestResponse.success();
  }
}
