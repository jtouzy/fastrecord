<p align="center">
  <img width="128" alt="frlogo" 
       src="https://cloud.githubusercontent.com/assets/5709133/21409297/c494d8bc-c7d9-11e6-9ee5-b0eb17361158.png">
</p>

# FastRecord
Lightweight Spring-based library for mapping Java POJO to Database without JPA.

## Install

### Maven install
```xml
<dependency>
  <groupId>com.jtouzy</groupId>
  <artifactId>fastrecord</artifactId>
  <version>{$fastrecord.version}</version>
</dependency>
```

## Basic example
Entity class :
```java
@Entity
public Event {
    @Id
    private String id;
    private String title;
    // /!\ Getters and setters must be implemented but ignored for demo
}
```
Usage in program :
```java
@Autowired
Statement statementProcessor;

// Find all events (SELECT event.id, event.title FROM event)
List<Event> events = statementProcessor.queryFrom(Event.class).findAll();
// Insert new event
Event event = new Event();
statementProcessor.insert(event).execute();
// Update event
event = statementProcessor.queryFrom(Event.class).eq("event_id", 1).findOne();
statementProcessor.update(event).execute();
// Delete event
statementProcessor.delete(event).execute();
```

## Repositories example
Repository class :
```java
@Service
public class EventRepository extends BaseSimpleIdRepository<Event,Integer> {
    public EventRepository() {
        super(Event.class);
    }
}
```
Usage in program :
```java
@Autowired
EventRepository eventRepository;

// Find an event with it's ID using Java8 optionals (SELECT event.id, event.title FROM event where event.id = ?)
Optional<Event> optionalEvent = eventRepository.findById(1);
// Find all events (SELECT event.id, event.title FROM event)
List<Event> events = eventRepository.findAll();
```
