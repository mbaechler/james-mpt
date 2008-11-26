package org.apache.james.mpt;

/**
 * A connection to the host.
 */
public interface Session {
    
    /**
     * Reads a line from the session input,
     * blocking until a new line is available.
     * @return not null
     * @throws Exception
     */
    public String readLine() throws Exception;

    /**
     * Writes a line to the session output.
     * @param line not null
     * @throws Exception
     */
    public void writeLine(String line) throws Exception;

    /**
     * Opens the session.
     * 
     * @throws Exception
     */
    public void start() throws Exception;

    /**
     * Closes the session.
     * 
     * @throws Exception
     */
    public void stop() throws Exception;
}