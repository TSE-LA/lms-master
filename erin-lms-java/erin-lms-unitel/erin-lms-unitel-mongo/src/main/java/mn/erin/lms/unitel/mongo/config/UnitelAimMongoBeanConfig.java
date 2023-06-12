package mn.erin.lms.unitel.mongo.config;

import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.lms.unitel.mongo.implementation.MongoGroupRepository;
import mn.erin.lms.unitel.mongo.implementation.MongoMembershipRepository;

/**
 * @author Zorig
 */

@Configuration
public class UnitelAimMongoBeanConfig
{
    private final MongoTemplate mongoTemplate;

    UnitelAimMongoBeanConfig(MongoClient mongoClient) {
        mongoTemplate = new MongoTemplate(mongoClient, "AIM");
    }

    @Bean
    public GroupRepository groupRepository() {
        return new MongoGroupRepository(mongoTemplate.getCollection("groups"));
    }

    @Bean
    public MembershipRepository membershipRepository() {
        return new MongoMembershipRepository(mongoTemplate.getCollection("Memberships"));
    }
}
