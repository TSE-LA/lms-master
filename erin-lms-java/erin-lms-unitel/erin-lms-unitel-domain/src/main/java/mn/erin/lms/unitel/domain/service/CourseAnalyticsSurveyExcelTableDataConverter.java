package mn.erin.lms.unitel.domain.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import mn.erin.common.excel.ExcelTableDataConverter;
import mn.erin.lms.base.domain.model.report.LearnerAnswer;
import mn.erin.lms.base.domain.model.report.LearnerAssessment;

/**
 * @author Erdenetulga
 */
public class CourseAnalyticsSurveyExcelTableDataConverter implements ExcelTableDataConverter<List<LearnerAssessment>>
{

  @Override
  @SuppressWarnings("unchecked")
  public Object[][] convert(List<LearnerAssessment> analyticsResults)
  {
    String name = "";

    int size = analyticsResults.size();
    Object[][] data = new Object[size][];

    for (int index = 0; index < size; index++)
    {
      LearnerAssessment learnerAssessment = analyticsResults.get(index);

      name = learnerAssessment.getLearnerId();
      List<LearnerAnswer> answers = learnerAssessment.getAnswers();
      answers.sort(Comparator.comparing(LearnerAnswer::getIndex));
      List<Object> rows = new ArrayList<>();
      for (LearnerAnswer answer : answers)
      {
        if (NumberUtils.isParsable(answer.getAnswer()))
        {
          rows.add(Integer.parseInt(answer.getAnswer()));
        }
        else
        {
          rows.add(answer.getAnswer());
        }
      }
      Object[] row = { 0, name };

      Object[] tempArr = new Object[row.length + rows.size()];
      System.arraycopy(row, 0, tempArr, 0, row.length);
      for (int i = 0; i < rows.size(); i++)
        tempArr[row.length + i] = rows.get(i);

      data[index] = tempArr;
    }

    return data;
  }
}
