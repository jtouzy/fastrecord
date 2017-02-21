package com.jtouzy.fastrecord.tests.builders;

import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.tests.metadata.workingEntities.Category;
import com.jtouzy.fastrecord.tests.metadata.workingEntities.Event;
import com.jtouzy.fastrecord.tests.metadata.workingEntities.SimpleEvent;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class InsertBuilderTest extends AbstractBuilderTest {
    @Test
    public void simpleInsertBuilderTest() throws Exception {
        SimpleEvent simpleEvent = new SimpleEvent();
        simpleEvent.setTitle("Test");
        DbReadyStatementMetadata metadata = statementProcessor.insert(simpleEvent).writeMetadata();
        Assert.assertEquals("INSERT INTO simple_event (title) VALUES (?)",
                metadata.getSqlString().toString());
        Assert.assertEquals(1, metadata.getParameters().size());
        Assert.assertEquals("Test", metadata.getParameters().get(0).getValue());
        Assert.assertEquals(Types.VARCHAR, metadata.getParameters().get(0).getType());
    }

    @Test
    public void simpleInsertWithJoinTest() throws Exception {
        Category category = new Category();
        category.setId(1);
        Event event = new Event();
        event.setTitle("Event_title");
        event.setCategory(category);
        DbReadyStatementMetadata metadata = statementProcessor.insert(event).writeMetadata();
        Assert.assertEquals("INSERT INTO event (category, title) VALUES (?, ?)",
                metadata.getSqlString().toString());
        Assert.assertEquals(2, metadata.getParameters().size());
        Assert.assertEquals("1", metadata.getParameters().get(0).getValue());
        Assert.assertEquals(Types.INTEGER, metadata.getParameters().get(0).getType());
        Assert.assertEquals("Event_title", metadata.getParameters().get(1).getValue());
        Assert.assertEquals(Types.VARCHAR, metadata.getParameters().get(1).getType());
    }

    @Test
    public void doubleInsertWithJoinTest() throws Exception {
        Category category = new Category();
        category.setId(1);
        List<Event> events = new ArrayList<>();
        Event event = new Event();
        event.setTitle("Event_title");
        event.setCategory(category);
        events.add(event);
        event = new Event();
        event.setTitle("Event_title_2");
        event.setCategory(category);
        events.add(event);
        DbReadyStatementMetadata metadata = statementProcessor.insert(Event.class, events).writeMetadata();
        Assert.assertEquals("INSERT INTO event (category, title) VALUES (?, ?), (?, ?)",
                metadata.getSqlString().toString());
        Assert.assertEquals(4, metadata.getParameters().size());
        Assert.assertEquals("1", metadata.getParameters().get(0).getValue());
        Assert.assertEquals(Types.INTEGER, metadata.getParameters().get(0).getType());
        Assert.assertEquals("Event_title", metadata.getParameters().get(1).getValue());
        Assert.assertEquals(Types.VARCHAR, metadata.getParameters().get(1).getType());
        Assert.assertEquals("1", metadata.getParameters().get(2).getValue());
        Assert.assertEquals(Types.INTEGER, metadata.getParameters().get(2).getType());
        Assert.assertEquals("Event_title_2", metadata.getParameters().get(3).getValue());
        Assert.assertEquals(Types.VARCHAR, metadata.getParameters().get(3).getType());
    }
}
