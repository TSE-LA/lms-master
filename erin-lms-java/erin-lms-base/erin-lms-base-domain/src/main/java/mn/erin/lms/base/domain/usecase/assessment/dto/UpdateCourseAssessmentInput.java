package mn.erin.lms.base.domain.usecase.assessment.dto;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Erdenetulga
 */

public class UpdateCourseAssessmentInput {
    private final String courseId;
    private Set<String> quizIds = new HashSet<>();
    public UpdateCourseAssessmentInput(String courseId) {
            this.courseId = courseId;
    }

    public void addQuiz(String test)
    {
        this.quizIds.add(test);
    }

    public String getCourseId()
    {
        return courseId;
    }

    public Set<String> getQuizIds()
    {
        return quizIds;
    }
}
