package mn.erin.lms.base.domain.usecase.assessment;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.assessment.CourseAssessment;
import mn.erin.lms.base.domain.model.assessment.QuizId;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.domain.repository.CourseAssessmentRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.base.domain.usecase.assessment.dto.UpdateCourseAssessmentInput;

/**
 * @author Erdenetulga
 */
@Authorized(users = {Author.class})
public class UpdateCourseAssessment extends CourseUseCase<UpdateCourseAssessmentInput, Boolean> {
    private final CourseAssessmentRepository courseAssessmentRepository;

    public UpdateCourseAssessment(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry) {
        super(lmsRepositoryRegistry, lmsServiceRegistry);
        this.courseAssessmentRepository = lmsRepositoryRegistry.getCourseAssessmentRepository();
    }

    @Override
    public Boolean execute(UpdateCourseAssessmentInput input) throws UseCaseException
    {
        Validate.notNull(input);
        CourseId courseId = CourseId.valueOf(input.getCourseId());
        validateCourse(courseId);

        try
        {
            CourseAssessment courseAssessment = courseAssessmentRepository.update(courseId, getQuizIds(input.getQuizIds()));
            return courseAssessment != null;
        }
        catch (LmsRepositoryException e)
        {
            throw new UseCaseException(e.getMessage(), e);
        }
    }
    private List<QuizId> getQuizIds(Set<String> quizIds)
    {
        return quizIds.stream().map(QuizId::valueOf).collect(Collectors.toList());
    }
}
