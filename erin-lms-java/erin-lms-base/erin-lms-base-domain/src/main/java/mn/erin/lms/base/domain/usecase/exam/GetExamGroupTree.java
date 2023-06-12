package mn.erin.lms.base.domain.usecase.exam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.exam.ExamGroup;
import mn.erin.lms.base.domain.model.exam.ExamGroupTree;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.LmsUseCase;

/**
 * @author Temuulen Naranbold
 */
public class GetExamGroupTree extends LmsUseCase<List<ExamGroup>, List<ExamGroupTree>>
{
  public GetExamGroupTree(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  protected List<ExamGroupTree> executeImpl(List<ExamGroup> input) throws UseCaseException, LmsRepositoryException
  {
    List<ExamGroup> rootGroups = input.stream().filter(ExamGroup::isRoot).collect(Collectors.toList());
    List<ExamGroupTree> rootGroupTrees = new ArrayList<>();
    input.removeAll(rootGroups);
    for (ExamGroup rootGroup : rootGroups)
    {
      rootGroupTrees.add(getGroupTree(input, mapper(rootGroup)));
    }
    return rootGroupTrees;
  }

  private ExamGroupTree getGroupTree(List<ExamGroup> input, ExamGroupTree rootGroupTree)
  {
    if (input != null)
    {
      for (ExamGroup examGroup : input)
      {
        if (rootGroupTree.getId().equals(examGroup.getParentId()))
        {
          rootGroupTree.addChild(getGroupTree(input, mapper(examGroup)));
        }
      }

    }
    return rootGroupTree;
  }

  private ExamGroupTree mapper(ExamGroup examGroup)
  {
    return new ExamGroupTree(examGroup.getId().getId(), examGroup.getParentId(), examGroup.getName(), examGroup.getOrganizationId());
  }
}
