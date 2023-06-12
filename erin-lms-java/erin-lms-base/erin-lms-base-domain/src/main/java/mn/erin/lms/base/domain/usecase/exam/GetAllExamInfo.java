package mn.erin.lms.base.domain.usecase.exam;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.category.ExamCategoryId;
import mn.erin.lms.base.domain.model.exam.Exam;
import mn.erin.lms.base.domain.model.exam.ExamGroupId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.exam.dto.ExamDto;
import mn.erin.lms.base.domain.usecase.exam.dto.GetExamInput;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

@Authorized(users = { Author.class, Instructor.class, })
public class GetAllExamInfo extends ExamUseCase<GetExamInput, List<ExamDto>>
{
  public GetAllExamInfo(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  protected List<ExamDto> executeImpl(GetExamInput input) throws UseCaseException, LmsRepositoryException
  {
    Validate.notNull(input, "GetExamInput cannot be null!");

    if (StringUtils.isBlank(input.getGroupId()) && StringUtils.isBlank(input.getCategoryId()))
    {
      return mapToExamInfoOutput(examRepository.getAllExam());
    }
    else if (!StringUtils.isBlank(input.getCategoryId()) && StringUtils.isBlank(input.getGroupId()))
    {
      return mapToExamInfoOutput(examRepository.listAllByCategory(ExamCategoryId.valueOf(input.getCategoryId())));
    }
    else if (!StringUtils.isBlank(input.getGroupId()) && StringUtils.isBlank(input.getCategoryId()))
    {
      return mapToExamInfoOutput(examRepository.listAllByGroup(ExamGroupId.valueOf(input.getGroupId())));
    }
    else
    {
      return mapToExamInfoOutput(examRepository.listAllByCategoryAndGroup(ExamCategoryId.valueOf(input.getCategoryId()), ExamGroupId.valueOf(input.getGroupId())));
    }
  }

  private List<ExamDto> mapToExamInfoOutput(List<Exam> examList)
  {
    return examList.stream().map(this::toDetailedExamDto).collect(Collectors.toList());
  }
}

