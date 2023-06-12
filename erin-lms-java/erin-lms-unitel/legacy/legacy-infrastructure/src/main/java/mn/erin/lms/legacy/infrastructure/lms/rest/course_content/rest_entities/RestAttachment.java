package mn.erin.lms.legacy.infrastructure.lms.rest.course_content.rest_entities;/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

import java.util.Objects;

import org.apache.commons.lang3.Validate;

/**
 * @author Erdenetulga.
 */
public class RestAttachment
{
    private String id;
    private String name;
    private String type;

    public RestAttachment(String id, String name, String type)
    {
        this.id = Objects.requireNonNull(id, "Attachment Id cannot be null!");
        this.name = Validate.notBlank(name, "Attachment name cannot be blank!");
        this.type = type;
    }

    public RestAttachment() {
    }

    public String getId()
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
}
