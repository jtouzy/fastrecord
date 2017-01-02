package com.jtouzy.fastrecord.tests.builders;

import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
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
}
