package mn.erin.lms.base.rest.api;

import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.domain.model.enrollment.EnrollSupervisor;
import mn.erin.lms.base.domain.repository.EnrollSupervisorRepository;
import mn.erin.lms.base.rest.model.exam.RestEnrollSupervisor;

/**
 * @author Galsan Bayart.
 */
@Api
@RestController
@RequestMapping("/enroll-manager-supervisor")
public class EnrollManagerSupervisorApi
{

  //todo move it into create exam use case after front end finished;
  private final EnrollSupervisorRepository enrollSupervisorRepository;

  public EnrollManagerSupervisorApi(EnrollSupervisorRepository enrollSupervisorRepository)
  {
    this.enrollSupervisorRepository = enrollSupervisorRepository;
  }

  @PostMapping("/save")
  public ResponseEntity<RestResult> save(@RequestBody RestEnrollSupervisor restEnrollSupervisor){
    return RestResponse.success(enrollSupervisorRepository.save(mapToEnrollSupervisor(restEnrollSupervisor)));
  }

  private EnrollSupervisor mapToEnrollSupervisor(RestEnrollSupervisor restEnrollSupervisor){
    return new EnrollSupervisor(restEnrollSupervisor.getExamId(), restEnrollSupervisor.getSupervisorIds(), restEnrollSupervisor.getAdminAddedUser());
  }
}
