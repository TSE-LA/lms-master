package mn.erin.lms.base.mongo.document.enrollment;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Galsan Bayart.
 */
@Document
public class MongoEnrollSupervisor
{
  @Id
  String id;
  String examId;
  Set<String> supervisorIds;
  Set<String> adminAddedUser;

  public MongoEnrollSupervisor()
  {
  }

  public MongoEnrollSupervisor(String examId, Set<String> supervisorIds, Set<String> adminAddedUser)
  {
    this.examId = examId;
    this.supervisorIds = supervisorIds;
    this.adminAddedUser = adminAddedUser;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getExamId()
  {
    return examId;
  }

  public void setExamId(String examId)
  {
    this.examId = examId;
  }

  public Set<String> getSupervisorIds()
  {
    return supervisorIds;
  }

  public void setSupervisorIds(Set<String> supervisorIds)
  {
    this.supervisorIds = supervisorIds;
  }

  public Set<String> getAdminAddedUser()
  {
    return adminAddedUser;
  }

  public void setAdminAddedUser(Set<String> adminAddedUser)
  {
    this.adminAddedUser = adminAddedUser;
  }
}
