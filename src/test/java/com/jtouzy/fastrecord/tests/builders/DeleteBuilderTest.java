package com.jtouzy.fastrecord.tests.builders;

import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.tests.metadata.workingEntities.SimpleEvent;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;

public class DeleteBuilderTest extends AbstractBuilderTest {
    @Test
    public void simpleDeleteBuilderTest() throws Exception {
        SimpleEvent simpleEvent = new SimpleEvent();
        simpleEvent.setId(1);
        DbReadyStatementMetadata metadata = statementProcessor.delete(simpleEvent).writeMetadata();
        Assert.assertEquals("DELETE FROM simple_event WHERE (simple_event.id = ?)",
                metadata.getSqlString().toString());
        Assert.assertEquals(1, metadata.getParameters().size());
        Assert.assertEquals("1", metadata.getParameters().get(0).getValue());
        Assert.assertEquals(Types.INTEGER, metadata.getParameters().get(0).getType());
    }
}
