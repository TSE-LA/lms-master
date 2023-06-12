package mn.erin.lms.base.analytics.usecase.exam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.analytics.model.Analytic;
import mn.erin.lms.base.analytics.repository.AnalyticsRepositoryRegistry;
import mn.erin.lms.base.analytics.usecase.AnalyticsUseCase;
import mn.erin.lms.base.analytics.usecase.dto.ExamAnalytics;
import mn.erin.lms.base.analytics.usecase.dto.ExamFilter;
import mn.erin.lms.base.domain.model.category.ExamCategory;
import mn.erin.lms.base.domain.model.category.ExamCategoryId;
import mn.erin.lms.base.domain.model.exam.ExamGroup;
import mn.erin.lms.base.domain.model.exam.ExamGroupId;
import mn.erin.lms.base.domain.model.exam.ExamStatus;
import mn.erin.lms.base.domain.model.exam.ExamType;
import mn.erin.lms.base.domain.repository.ExamCategoryRepository;
import mn.erin.lms.base.domain.repository.ExamGroupRepository;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;

/**
 * @author Byambajav
 */
public class GenerateExamAnalytics extends AnalyticsUseCase<ExamFilter, ExamAnalytics>
{
  protected final ExamGroupRepository examGroupRepository;
  protected final ExamCategoryRepository examCategoryRepository;
  protected final LmsServiceRegistry lmsServiceRegistry;

  public GenerateExamAnalytics(AnalyticsRepositoryRegistry analyticsRepositoryRegistry,
      ExamCategoryRepository examCategoryRepository,
      ExamGroupRepository examGroupRepository, LmsServiceRegistry lmsServiceRegistry)
  {
    super(analyticsRepositoryRegistry);
    this.examCategoryRepository = examCategoryRepository;
    this.examGroupRepository = examGroupRepository;
    this.lmsServiceRegistry = lmsServiceRegistry;
  }

  @Override
  public ExamAnalytics execute(ExamFilter input) throws UseCaseException
  {
    Validate.notNull(input);
    Validate.notNull(input.getDateFilter());

    Set<String> categories = setCategories(input.getCategoryId());
    Set<String> groups = setGroups(input.getGroupId());
    Set<String> types = setTypes(input.getType());
    Set<String> statuses = setStatuses(input.getStatus());

    LocalDate startDate = input.getDateFilter().getStartDate();
    LocalDate endDate = input.getDateFilter().getEndDate();

    return new ExamAnalytics(analyticsRepositoryRegistry.getExamAnalyticRepository().getAll(groups, categories, statuses, types, startDate, endDate));
  }

  private Set<String> setCategories(String categoryId)
  {
    Set<String> categories = new HashSet<>();
    if (StringUtils.isBlank(categoryId) || categoryId.equals("all"))
    {
      List<ExamCategory> examCategories = examCategoryRepository.listAllByParentCategoryId(ExamCategoryId.valueOf("exam"));
      for (ExamCategory examCategory : examCategories)
      {
        categories.add(examCategory.getId().getId());
      }
    }
    else
    {
      categories.add(categoryId );
    }
    return categories;
  }

  private Set<String> setGroups(String groupId)
  {
    Set<String> groups = new HashSet<>();
    if (StringUtils.isBlank(groupId) || groupId.equals("all"))
    {
      List<ExamGroup> examGroups = examGroupRepository.findByParentIdAndOrganizationId(ExamGroupId.valueOf("exam"), OrganizationId.valueOf(lmsServiceRegistry.getOrganizationIdProvider().getOrganizationId()));
      for (ExamGroup examGroup : examGroups)
      {
        groups.add(examGroup.getId().getId());
      }
    }
    else
    {
      groups.add(groupId);
    }
    return groups;
  }

  private Set<String> setTypes(String type)
  {
    Set<String> types = new HashSet<>();
    if (StringUtils.isBlank(type) || type.equals("all"))
    {
      types.add(ExamType.OFFICIAL.name());
      types.add(ExamType.SELF_TRAINING.name());
    }
    else
    {
      types.add(type);
    }
    return types;
  }

  private Set<String> setStatuses(String status)
  {
    Set<String> statuses = new HashSet<>();
    if (StringUtils.isBlank(status) || status.equals("all"))
    {
      statuses.add(ExamStatus.NEW.name());
      statuses.add(ExamStatus.STARTED.name());
      statuses.add(ExamStatus.FINISHED.name());
      statuses.add(ExamStatus.PENDING.name());
      statuses.add(ExamStatus.PUBLISHED.name());
    }
    else
    {
      statuses.add(status);
    }
    return statuses;
  }
}
