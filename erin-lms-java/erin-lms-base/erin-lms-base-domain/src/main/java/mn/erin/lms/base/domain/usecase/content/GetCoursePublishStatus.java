package mn.erin.lms.base.domain.usecase.content;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.content.CourseModule;
import mn.erin.lms.base.domain.model.content.CourseSection;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.domain.repository.CourseAssessmentRepository;
import mn.erin.lms.base.domain.repository.CourseContentRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;

/**
 * @author Erdenetulga
 */
@Authorized(users = {Author.class})
public class GetCoursePublishStatus implements UseCase<String, Boolean> {

    private final LmsRepositoryRegistry lmsRepositoryRegistry;
    private static final String ERROR_MSG_FOR_COURSE_ID = "Course id cannot be null!";

    public GetCoursePublishStatus(LmsRepositoryRegistry lmsRepositoryRegistry) {
        this.lmsRepositoryRegistry = Objects.requireNonNull(lmsRepositoryRegistry);
    }

    @Override
    public Boolean execute(String courseId) throws UseCaseException {
        Validate.notNull(courseId, ERROR_MSG_FOR_COURSE_ID);
        CourseRepository courseRepository = lmsRepositoryRegistry.getCourseRepository();
        CourseContentRepository courseContentRepository = lmsRepositoryRegistry.getCourseContentRepository();
        CourseAssessmentRepository courseAssessmentRepository = lmsRepositoryRegistry.getCourseAssessmentRepository();
        try {
            Course course = courseRepository.fetchById(CourseId.valueOf(courseId));
            if (course.getCourseDetail().hasQuiz()) {
                return courseAssessmentRepository.fetchById(CourseId.valueOf(courseId)).getCourseId().getId() != null;
            }
            for (CourseModule module : courseContentRepository.fetchById(CourseId.valueOf(courseId)).getModules()) {
                for (CourseSection section : module.getSectionList()) {
                    return !section.getAttachmentId().getId().equals("empty");
                }
            }
            return true;
        } catch (LmsRepositoryException e) {
            return false;
        }
    }
}


