package com.jtouzy.fastrecord.tests.builders;

import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.tests.metadata.workingEntities.SimpleEvent;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;

public class UpdateBuilderTest extends AbstractBuilderTest {
    @Test
    public void simpleUpdateBuilderTest() throws Exception {
        SimpleEvent simpleEvent = new SimpleEvent();
        simpleEvent.setId(1);
        simpleEvent.setTitle("Test");
        DbReadyStatementMetadata metadata = statementProcessor.update(simpleEvent).writeMetadata();
        Assert.assertEquals("UPDATE simple_event SET id = ?, title = ? " +
                        "WHERE (simple_event.id = ?)", metadata.getSqlString().toString());
        Assert.assertEquals(3, metadata.getParameters().size());
        Assert.assertEquals("1", metadata.getParameters().get(0).getValue());
        Assert.assertEquals(Types.INTEGER, metadata.getParameters().get(0).getType());
        Assert.assertEquals("Test", metadata.getParameters().get(1).getValue());
        Assert.assertEquals(Types.VARCHAR, metadata.getParameters().get(1).getType());
        Assert.assertEquals("1", metadata.getParameters().get(2).getValue());
        Assert.assertEquals(Types.INTEGER, metadata.getParameters().get(2).getType());
    }
}
