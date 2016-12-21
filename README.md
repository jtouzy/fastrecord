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
```java
/* Basic entity class */

@Entity
public Event {
  @Id
  private String id;
  private String title;
  // /!\ Getters and setters must be implemented but ignored for demo
}

/* Program */

// Retrieve all events (SELECT event.id, event.title FROM event)
List<Event> events = Query.from(Event.class).findAll();
```
