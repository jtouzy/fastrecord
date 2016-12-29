package com.jtouzy.fastrecord.statements.writers;

import com.jtouzy.fastrecord.statements.context.WritableContext;
import com.jtouzy.fastrecord.statements.processing.DbReadyStatementMetadata;

/**
 * Base writer interface.
 * All the writers must implement this interface.
 *
 * @param <T> Type of writableContext managed in this writer
 */
public interface Writer<T extends WritableContext> {
    /**
     * Initialize or refresh the writer context before a write process.
     *
     * @param context The new context to associate to the writer
     * @param writerCache The writerCache instance for this write process
     */
    void refreshContext(T context, WriterCache writerCache);

    /**
     * Get the writableContext associated to this writer.
     *
     * @return WritableContext associated to this writer
     */
    T getContext();

    /**
     * Write process.
     * Write the SQLString and add parameters if needed to the DbReadyStatementMetadata result.
     * All the writers must override this method to write their own SQL statement.
     */
    void write();

    /**
     * Get the result of the write process.
     *
     * @return StatementMetadata resulting of the write process
     */
    DbReadyStatementMetadata getResult();

    /**
     * Boolean to tell if the Writer is cacheable in WriterCache during write process.
     *
     * @return True if the writer is cacheable, false otherwise
     */
    boolean isCacheable();
}
