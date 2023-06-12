package mn.erin.lms.legacy.domain.unitel.usecase.promotion;

import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class PromotionDetail
{
  private String promotionId;
  private String promotionName;
  private String promotionCategoryId;
  private String promotionState;

  private String type;
  private String code;
  private String keyword;
  private String description;
  private String note;

  private String publishStatus;
  private Date startDate;
  private Date endDate;
  private Date modifiedDate;

  private String authorId;
  private String modifiedUserId;

  public String getPromotionId()
  {
    return promotionId;
  }

  public String getPromotionName()
  {
    return promotionName;
  }

  public String getCode()
  {
    return code;
  }

  public String getKeyword()
  {
    return keyword;
  }

  public String getPromotionCategoryId()
  {
    return promotionCategoryId;
  }

  public String getType()
  {
    return type.toUpperCase();
  }

  public String getPromotionState()
  {
    return promotionState.toUpperCase();
  }

  public String getPublishStatus()
  {
    return publishStatus;
  }

  public String getDescription()
  {
    return description;
  }

  public String getNote()
  {
    return note;
  }

  public Date getStartDate()
  {
    return startDate;
  }

  public Date getEndDate()
  {
    return endDate;
  }

  public Date getModifiedDate()
  {
    return modifiedDate;
  }

  public String getAuthorId()
  {
    return authorId;
  }

  public String getModifiedUserId()
  {
    return modifiedUserId;
  }

  private void validateDate(Date startDate, Date endDate, String promotionState)
  {
    if (promotionState.equalsIgnoreCase("main") && endDate != null)
    {
      throw new IllegalArgumentException("Promotion with \"main\" state cannot have end date.");
    }
    if (!promotionState.equalsIgnoreCase("main") && endDate == null)
    {
      throw new IllegalArgumentException("Promotion with state other than \"main\" must have end date.");
    }
    if (promotionState.equalsIgnoreCase("current") && (endDate.before(new Date())) ||
        promotionState.equalsIgnoreCase("expired") && (endDate.after(new Date())))
    {
      throw new IllegalArgumentException("End date is null or correlation between Promotion state and end date is wrongfully given.");
    }
    if (startDate.getTime() > endDate.getTime())
    {
      throw new IllegalArgumentException("The start date of the promotion cannot be greater than end date.");
    }
  }

  private PromotionDetail(Builder builder)
  {
    this.promotionId = builder.promotionId;
    this.promotionName = Validate.notBlank(builder.promotionName, "Promotion name is required!");
    this.code = Validate.notBlank(builder.code, "Promotion code is required!");
    this.keyword = Validate.notBlank(builder.keyword, "Promotion keyword is required!");
    this.type = Validate.notBlank(builder.directMass, "Direct/Mass is required!");
    this.promotionState = Validate.notBlank(builder.promotionState, "Promotion state is required!");
    this.startDate = Objects.requireNonNull(builder.startDate, "Promotion start date is required!");
    this.endDate = builder.endDate;

    validateDate(startDate, endDate, promotionState);

    this.modifiedDate = Objects.requireNonNull(builder.modifiedDate, "Promotion modified date is required!");
    this.promotionCategoryId = builder.promotionCategoryId;
    this.publishStatus = builder.publishStatus;
    this.description = builder.description;
    this.note = builder.note;
    this.authorId = builder.authorId;
    this.modifiedUserId = builder.modifiedUserId;
  }

  public static class Builder
  {
    private String promotionId;
    private String promotionName;
    private String promotionCategoryId;
    private String promotionState;
    private String code;
    private String keyword;
    private String directMass;
    private String publishStatus;
    private String description;
    private String note;
    private String authorId;
    private String modifiedUserId;
    private Date startDate;
    private Date endDate;
    private Date modifiedDate;

    public Builder()
    {

    }

    public Builder(String promotionId)
    {
      this.promotionId = promotionId;
    }

    public Builder withName(String promotionName)
    {
      this.promotionName = promotionName;
      return this;
    }

    public Builder withCode(String code)
    {
      this.code = code;
      return this;
    }

    public Builder withKeyword(String keyword)
    {
      this.keyword = keyword;
      return this;
    }

    public Builder withCategoryId(String categoryId)
    {
      this.promotionCategoryId = categoryId;
      return this;
    }

    public Builder withType(String directMass)
    {
      this.directMass = directMass;
      return this;
    }

    public Builder withState(String promotionState)
    {
      this.promotionState = promotionState;
      return this;
    }

    public Builder withStatus(String publishStatus)
    {
      this.publishStatus = publishStatus;
      return this;
    }

    public Builder withDescription(String description)
    {
      this.description = description;
      return this;
    }

    public Builder withNote(String note)
    {
      this.note = note;
      return this;
    }

    public Builder modifiedAt(Date modifiedDate)
    {
      this.modifiedDate = modifiedDate;
      return this;
    }

    public Builder starts(Date startDate)
    {
      this.startDate = startDate;
      return this;
    }

    public Builder ends(Date endDate)
    {
      this.endDate = endDate;
      return this;
    }

    public Builder modifiedBy(String modifiedUserId)
    {
      this.modifiedUserId = modifiedUserId;
      return this;
    }

    public Builder createdBy(String authorId)
    {
      this.authorId = authorId;
      return this;
    }

    public PromotionDetail build()
    {
      return new PromotionDetail(this);
    }
  }
}
