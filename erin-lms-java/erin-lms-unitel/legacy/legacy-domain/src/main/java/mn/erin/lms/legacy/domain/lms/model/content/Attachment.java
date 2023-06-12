/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.model.content;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.Entity;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class Attachment implements Entity<Attachment>
{
  private final AttachmentId id;
  private String name;
  private String type;

  public Attachment(AttachmentId id, String name, String type)
  {
    this.id = Objects.requireNonNull(id, "Attachment Id cannot be null!");
    this.name = Validate.notBlank(name, "Attachment name cannot be blank!");
    this.type = type;
  }

  public AttachmentId getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  @Override
  public boolean sameIdentityAs(Attachment other)
  {
    return other != null && (this.id.equals(other.id));
  }
}
