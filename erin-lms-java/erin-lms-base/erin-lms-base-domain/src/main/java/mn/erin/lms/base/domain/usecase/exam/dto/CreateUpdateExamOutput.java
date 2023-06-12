package mn.erin.lms.base.domain.usecase.exam.dto;

import mn.erin.lms.base.domain.model.exam.Exam;

/**
 * @author Temuulen Naranbold
 */
public class CreateUpdateExamOutput
{
  public final Exam exam;

  public CreateUpdateExamOutput(Exam exam)
  {
    this.exam = exam;
  }
}
