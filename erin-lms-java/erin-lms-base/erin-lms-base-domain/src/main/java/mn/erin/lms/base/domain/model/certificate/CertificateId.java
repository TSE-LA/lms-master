package mn.erin.lms.base.domain.model.certificate;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Erdenetulga
 */
public class CertificateId  extends EntityId
{
    private CertificateId(String id)
    {
        super(id);
    }

    public static CertificateId valueOf(String id)
    {
        return new CertificateId(id);
    }
}
