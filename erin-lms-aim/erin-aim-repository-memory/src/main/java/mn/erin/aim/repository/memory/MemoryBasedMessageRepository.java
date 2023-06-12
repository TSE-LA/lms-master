package mn.erin.aim.repository.memory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.base.model.message.Message;
import mn.erin.domain.base.repository.message.MessageRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Repository;

@Repository
public class MemoryBasedMessageRepository implements MessageRepository
{
  private final Map<Pair<String, Locale>, Message> messages = new HashMap<>();

  @Override
  public Message findById(EntityId entityId)
  {
    return messages.values()
        .stream()
        .filter(message -> entityId.equals(message.getId()))
        .findFirst()
        .orElse(null);
  }

  @Override
  public Collection<Message> findAll()
  {
    return messages.values();
  }

  @Override
  public void save(Message message)
  {

  }

  @Override
  public Message find(String messageKey, Locale locale)
  {
    return messages.get(Pair.of(messageKey, locale));
  }
}
