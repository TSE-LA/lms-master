package mn.erin.lms.base.domain.usecase.exam.dto;

import java.util.Date;

import mn.erin.lms.base.domain.model.exam.ExamId;

/**
 * @author mLkhagvasuren
 */
public class ExamExpirationInput
{
  public final ExamId examId;
  public final Date examEndDate;

  public ExamExpirationInput(ExamId examId, Date examEndDate)
  {
    this.examId = examId;
    this.examEndDate = examEndDate;
  }
}
