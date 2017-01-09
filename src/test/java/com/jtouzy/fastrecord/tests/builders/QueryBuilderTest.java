package com.jtouzy.fastrecord.tests.builders;

import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;
import com.jtouzy.fastrecord.tests.metadata.workingEntities.Category;
import com.jtouzy.fastrecord.tests.metadata.workingEntities.Event;
import com.jtouzy.fastrecord.tests.metadata.workingEntities.SimpleEvent;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;

public class QueryBuilderTest extends AbstractBuilderTest {
    @Test
    public void singleConditionQueryBuilderTest() throws Exception {
        DbReadyStatementMetadata metadata = statementProcessor.queryFrom(SimpleEvent.class)
                .conditions().eq("title", "Test").endConditions().writeMetadata();
        Assert.assertEquals("SELECT simple_event0.id as simple_event0$$id, " +
                "simple_event0.title as simple_event0$$title " +
                "FROM simple_event simple_event0 " +
                "WHERE ((simple_event0.title = ?))",
                metadata.getSqlString().toString());
        Assert.assertEquals(1, metadata.getParameters().size());
        Assert.assertEquals("Test", metadata.getParameters().get(0).getValue());
        Assert.assertEquals(Types.VARCHAR, metadata.getParameters().get(0).getType());
    }

    @Test
    public void twoConditionsQueryBuilderTest() throws Exception {
        DbReadyStatementMetadata metadata = statementProcessor.queryFrom(SimpleEvent.class)
                .conditions().eq("title", "Test2").andEq("id", 1).endConditions().writeMetadata();
        Assert.assertEquals("SELECT simple_event0.id as simple_event0$$id, " +
                        "simple_event0.title as simple_event0$$title " +
                        "FROM simple_event simple_event0 " +
                        "WHERE ((simple_event0.title = ? AND simple_event0.id = ?))",
                metadata.getSqlString().toString());
        Assert.assertEquals(2, metadata.getParameters().size());
        Assert.assertEquals("Test2", metadata.getParameters().get(0).getValue());
        Assert.assertEquals(Types.VARCHAR, metadata.getParameters().get(0).getType());
        Assert.assertEquals("1", metadata.getParameters().get(1).getValue());
        Assert.assertEquals(Types.INTEGER, metadata.getParameters().get(1).getType());
    }

    @Test
    public void simpleJoinQueryBuilderTest() throws Exception {
        DbReadyStatementMetadata metadata = statementProcessor.queryFrom(Event.class)
                .fill(Category.class).writeMetadata();
        Assert.assertEquals("SELECT event0.id as event0$$id, event0.category as event0$$category, " +
                        "event0.title as event0$$title, category0.title as category0$$title " +
                        "FROM event event0, category category0 " +
                        "WHERE ((event0.category = category0.id))",
                metadata.getSqlString().toString());
        Assert.assertEquals(0, metadata.getParameters().size());
    }

    @Test
    public void simpleOrderedQueryBuilderTest() throws Exception {
        DbReadyStatementMetadata metadata = statementProcessor.queryFrom(Event.class).orderBy("title").writeMetadata();
        Assert.assertEquals("SELECT event0.id as event0$$id, event0.category as event0$$category, " +
                        "event0.title as event0$$title FROM event event0 ORDER BY event0.title",
                metadata.getSqlString().toString());
        Assert.assertEquals(0, metadata.getParameters().size());
    }

    @Test
    public void simpleJoinOrderedQueryBuilderTest() throws Exception {
        DbReadyStatementMetadata metadata = statementProcessor.queryFrom(Event.class)
                .fill(Category.class).orderBy(Category.class, "title").writeMetadata();
        Assert.assertEquals("SELECT event0.id as event0$$id, event0.category as event0$$category, " +
                        "event0.title as event0$$title, category0.title as category0$$title " +
                        "FROM event event0, category category0 " +
                        "WHERE ((event0.category = category0.id)) ORDER BY category0.title",
                metadata.getSqlString().toString());
        Assert.assertEquals(0, metadata.getParameters().size());
    }
}
