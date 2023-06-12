package mn.erin.lms.base.rest.api;

import java.util.stream.Collectors;

import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.domain.model.exam.ExamResult;
import mn.erin.lms.base.domain.model.exam.ExamResultEntity;
import mn.erin.lms.base.domain.repository.ExamResultRepository;
import mn.erin.lms.base.rest.model.exam.RestExamResult;
import mn.erin.lms.base.rest.model.exam.RestExamResultEntity;

/**
 * @author Galsan Bayart.
 */

@Api
@RestController
@RequestMapping("/exam-result")
public class ExamResultRestApi
{
  private final ExamResultRepository examResultRepository;

  public ExamResultRestApi(ExamResultRepository examResultRepository)
  {
    this.examResultRepository = examResultRepository;
  }

  @PostMapping("/save")
  public ResponseEntity<RestResult> save(@RequestBody RestExamResult restExamResult){
    return RestResponse.success(examResultRepository.save(new ExamResult(restExamResult.getRestExamResultEntities().stream().map(this::mapToExamResultEntity).collect(Collectors.toList()))));
  }

  private ExamResultEntity mapToExamResultEntity(RestExamResultEntity restExamResultEntity){
    return new ExamResultEntity(restExamResultEntity.getQuestionId(), restExamResultEntity.getAnswer());
  }
}
