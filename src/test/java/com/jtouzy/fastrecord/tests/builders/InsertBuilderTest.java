package com.jtouzy.fastrecord.tests.builders;

import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.tests.metadata.workingEntities.Category;
import com.jtouzy.fastrecord.tests.metadata.workingEntities.Event;
import com.jtouzy.fastrecord.tests.metadata.workingEntities.SimpleEvent;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;

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
}
