package mn.erin.lms.base.mongo.document.certificate;

import org.springframework.data.annotation.Id;

/**
 * @author Erdenetulga
 */
public class MongoCourseCertificate
    {
        @Id
        private String id;


  public MongoCourseCertificate()
        {
        }

  public MongoCourseCertificate(String id)
        {
            this.id = id;
        }

        public String getId()
        {
            return id;
        }

    }
