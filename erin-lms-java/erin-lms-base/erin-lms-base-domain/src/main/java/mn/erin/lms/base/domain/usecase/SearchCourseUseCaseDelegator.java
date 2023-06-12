package mn.erin.lms.base.domain.usecase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class SearchCourseUseCaseDelegator implements UseCaseDelegator<Map<String, Object>, List<CourseDto>> {
    private Map<String, UseCase<Map<String, Object>, List<CourseDto>>> registry;

    public SearchCourseUseCaseDelegator() {
        this.registry = new HashMap<>();
    }

    @Override
    public <T extends LmsUser> void register(Class<T> user, UseCase<Map<String, Object>, List<CourseDto>> useCase) {
        this.registry.put(user.getName(), useCase);
    }

    @Override
    public List<CourseDto> execute(LmsUser user, Map<String, Object> input) throws UseCaseException {
        UseCase<Map<String, Object>, List<CourseDto>> usecase = registry.get(user.getClass().getName());
        input.put("currentUser", user.getId().getId());
        return usecase.execute(input);
    }
}
