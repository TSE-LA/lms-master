package mn.erin.lms.base.rest.api;

import io.swagger.annotations.Api;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.domain.model.enrollment.ExamEnrollment;
import mn.erin.lms.base.domain.model.enrollment.ExamEnrollmentId;
import mn.erin.lms.base.domain.repository.ExamEnrollmentRepository;
import mn.erin.lms.base.rest.model.exam.RestExamEnrollment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Galsan Bayart.
 */
@Api
@RestController
@RequestMapping("/examEnrollment")
public class ExamEnrollmentRestApi
{
  private final ExamEnrollmentRepository examEnrollmentRepository;

  public ExamEnrollmentRestApi(ExamEnrollmentRepository examEnrollmentRepository)
  {
    this.examEnrollmentRepository = examEnrollmentRepository;
  }

  @PostMapping("/create")
  public ResponseEntity<RestResult> creatEnrollment(@RequestBody RestExamEnrollment restExamEnrollment){
    return RestResponse.success(examEnrollmentRepository.createEnrollment(restExamEnrollment.getExamId(),
            restExamEnrollment.getLearnerId(), "r"));
  }

  @PostMapping("/update")
  public ResponseEntity<RestResult> updateEnrollment(@RequestBody RestExamEnrollment restExamEnrollment){
    return RestResponse.success(examEnrollmentRepository.update(mapToExamEnrollment(restExamEnrollment)));
  }


  private ExamEnrollment mapToExamEnrollment(RestExamEnrollment restExamEnrollment){
    return new ExamEnrollment(new ExamEnrollmentId(restExamEnrollment.getExamId()), "", restExamEnrollment.getLearnerId(), "");
  }

}
