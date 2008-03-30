/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.james.test.functional;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

import org.apache.commons.lang.StringUtils;


public class ScriptBuilder {
    
    public static ScriptBuilder open(String host, int port) throws Exception {
        InetSocketAddress address = new InetSocketAddress(host, port);
        SocketChannel socket = SocketChannel.open(address);
        socket.configureBlocking(false);
        Client client = new Client(socket, socket);
        return new ScriptBuilder(client);
    }
    
    private int tagCount = 0;
    
    private boolean uidSearch = false;
    private boolean peek = false;
    private int messageNumber = 1;
    private String user = "imapuser";
    private String password = "password";
    private String mailbox = "testmailbox";
    private String file = "rfc822.mail";
    private String basedir = "/org/apache/james/test/functional/";
    private boolean createdMailbox = false;
    private final Client client;
    private Fetch fetch = new Fetch();
    private Search search = new Search();
    private String partialFetch = "";
    
    
    public ScriptBuilder(final Client client) {
        super();
        this.client = client;
    }
    
    public final boolean isPeek() {
        return peek;
    }

    public final void setPeek(boolean peek) {
        this.peek = peek;
    }

    public final boolean isUidSearch() {
        return uidSearch;
    }

    public final void setUidSearch(boolean uidSearch) {
        this.uidSearch = uidSearch;
    }

    public final String getBasedir() {
        return basedir;
    }

    public final void setBasedir(String basedir) {
        this.basedir = basedir;
    }

    public final String getFile() {
        return file;
    }

    public final void setFile(String file) {
        this.file = file;
    }
    
    private InputStream openFile() throws Exception {
        InputStream result = this.getClass().getResourceAsStream( basedir + file );
        return new IgnoreHeaderInputStream(result);
    }
    
    public final Fetch getFetch() {
        return fetch;
    }

    public final void setFetch(Fetch fetch) {
        this.fetch = fetch;
    }
    
    public final void resetFetch() {
        this.fetch = new Fetch();
    }
    
    public final int getMessageNumber() {
        return messageNumber;
    }

    public final void setMessageNumber(int messageNumber) {
        this.messageNumber = messageNumber;
    }

    public final String getMailbox() {
        return mailbox;
    }

    public final ScriptBuilder setMailbox(String mailbox) {
        this.mailbox = mailbox;
        return this;
    }

    public final String getPassword() {
        return password;
    }

    public final void setPassword(String password) {
        this.password = password;
    }

    public final String getUser() {
        return user;
    }

    public final void setUser(String user) {
        this.user = user;
    }

    public void login() throws Exception {
        command("LOGIN " + user + " " + password);
    }

    private void command(final String command) throws Exception {
        tag();
        write(command);
        lineEnd();
        response();
    }
    
    public ScriptBuilder rename(String from, String to) throws Exception {
        command("RENAME " + from + " " + to);
        return this;
    }
    
    public ScriptBuilder select() throws Exception {
        command("SELECT " + mailbox);
        return this;
    }
    
    public ScriptBuilder create() throws Exception {
        command("CREATE " + mailbox);
        createdMailbox = true;
        return this;
    }
    
    public ScriptBuilder flagDeleted() throws Exception {
        store(new Flags().deleted().msn(messageNumber));
        return this;
    }
    
    public ScriptBuilder expunge() throws Exception {
        command("EXPUNGE");
        return this;
    }
    
    public void delete() throws Exception {
        if (createdMailbox) {
            command("DELETE " + mailbox);
        }
    }
    
    public void search() throws Exception {
        search.setUidSearch(uidSearch);
        command(search.command());
        search = new Search();
    }
    
    public ScriptBuilder all() {
        search.all();
        return this;
    }
    
    public ScriptBuilder answered() {
        search.answered();
        return this;
    }
    
    public ScriptBuilder bcc(String address) {
        search.bcc(address);
        return this;
    }

    public ScriptBuilder before(int year, int month, int day) {
        search.before(year, month, day);
        return this;
    }
    
    public ScriptBuilder body(String text) {
        search.body(text);
        return this;
    }    

    public ScriptBuilder cc(String address) {
        search.cc(address);
        return this;
    }

    public ScriptBuilder deleted() {
        search.deleted();
        return this;
    }
    
    public ScriptBuilder draft() {
        search.draft();
        return this;
    }

    public ScriptBuilder flagged() {
        search.flagged();
        return this;
    }

    public ScriptBuilder from(String address) {
        search.from(address);
        return this;
    }

    public ScriptBuilder header(String field, String value) {
        search.header(field, value);
        return this; 
    }
    
    public ScriptBuilder keyword(String flag) {
        search.keyword(flag);
        return this;
    }
    
    public ScriptBuilder larger(long size) {
        search.larger(size);
        return this;
    }

    public ScriptBuilder NEW() {
        search.NEW();
        return this;
    }
    
    public ScriptBuilder not() {
        search.not();
        return this;
    }
    
    public ScriptBuilder old() {
        search.old();
        return this;
    }
    
    public ScriptBuilder on(int year, int month, int day) {
        search.on(year, month, day);
        return this;
    }

    public ScriptBuilder or() {
        search.or();
        return this;
    }

    public ScriptBuilder recent() {
        search.recent();
        return this;
    }

    public ScriptBuilder seen() {
        search.seen();
        return this;
    }

    public ScriptBuilder sentbefore(int year, int month, int day) {
        search.sentbefore(year, month, day);
        return this;
    }

    public ScriptBuilder senton(int year, int month, int day) {
        search.senton(year, month, day);
        return this; 
    }

    public ScriptBuilder sentsince(int year, int month, int day) {
        search.sentsince(year, month, day);
        return this;
    }
    
    public ScriptBuilder since(int year, int month, int day) {
        search.since(year, month, day);
        return this; 
    }
    
    public ScriptBuilder smaller(int size) {
        search.smaller(size);
        return this;
    }

    public ScriptBuilder subject(String address) {
        search.subject(address);
        return this;
    }
    
    public ScriptBuilder text(String text) {
        search.text(text);
        return this;
    }
    
    public ScriptBuilder to(String address) {
        search.to(address);
        return this;
    }
    
    public ScriptBuilder uid() {
        search.uid();
        return this;
    }

    public ScriptBuilder unanswered() {
        search.unanswered();
        return this;
    }
    
    public ScriptBuilder undeleted() {
        search.undeleted();
        return this;
    }

    public ScriptBuilder undraft() {
        search.undraft();
        return this;
    }
    
    public ScriptBuilder unflagged() {
        search.unflagged();
        return this;
    }

    public ScriptBuilder unkeyword(String flag) {
        search.unkeyword(flag);
        return this;
    }
    
    public ScriptBuilder unseen() {
        search.unseen();
        return this;
    }
    
    public ScriptBuilder openParen() {
        search.openParen();
        return this;
    }
    
    public ScriptBuilder closeParen() {
        search.closeParen();
        return this;
    }
    
    public ScriptBuilder msn(int low, int high) {
        search.msn(low, high);
        return this;
    }
    
    public ScriptBuilder msnAndUp(int limit) {
        search.msnAndUp(limit);
        return this;
    }
    
    public ScriptBuilder msnAndDown(int limit) {
        search.msnAndDown(limit);
        return this;
    }
    
    public Flags flags() {
        return new Flags();
    }
    
    public void store(Flags flags) throws Exception {
        String command = flags.command();
        command(command);
    }
    
    public Search getSearch() throws Exception {
        return search;
    }
    
    public ScriptBuilder partial(long start, long octets) {
        partialFetch = "<" + start +  "." + octets + ">";
        return this;
    }
    
    public ScriptBuilder fetchSection(String section) throws Exception {
        final String body;
        if (peek) {
            body = " (BODY.PEEK[";
        } else {
            body = " (BODY[";
        }
        final String command = "FETCH " + messageNumber + body + section + "]" + partialFetch + ")";
        command(command);
        return this;
    }
    
    public void fetchAllMessages() throws Exception {
        final String command = fetch.command();
        command(command);
    }
    
    public void fetch() throws Exception {
        final String command = fetch.command(messageNumber);
        command(command);
    }
    
    public void fetchFlags() throws Exception {
        final String command = "FETCH " + messageNumber + " (FLAGS)";
        command(command);
    }
    
    public void append() throws Exception {
        tag();
        write("APPEND " + mailbox);
        write(openFile());
        lineEnd();
        response();
    }

    private void write(InputStream in) throws Exception {
        client.write(in);
    }

    private void response() throws Exception {
        client.readResponse();
    }
    
    private void tag() throws Exception {
        client.lineStart();
        write("A" + ++tagCount + " ");
    }
    
    private void lineEnd() throws Exception {
        client.lineEnd();
    }

    private void write(String phrase) throws Exception {
        client.write(phrase);
    }
     
    public void close() throws Exception {
        client.close();
    }
    
    public void logout() throws Exception {
        command("LOGOUT");
    }
    
    public void quit() throws Exception {
        delete();
        logout();
        close();
    }
    
    public static final class Flags {
        private StringBuffer flags;
        private StringBuffer msn;
        private boolean first;
        private boolean silent;
        private boolean add;
        private boolean subtract;
        
        public Flags() {
            add = false;
            subtract = false;
            silent = false;
            first = true;
            flags = new StringBuffer("(");
            msn = new StringBuffer();
        }
        
        public Flags msn(long number) {
            msn.append(number);
            msn.append(' ');
            return this;
        }
        
        public Flags range(long low, long high) {
            msn.append(low);
            msn.append(':');
            msn.append(high);
            msn.append(' ');
            return this;
        }
        
        public Flags rangeTill(long number) {
            msn.append("*:");
            msn.append(number);
            msn.append(' ');
            return this;
        }
        
        public Flags rangeFrom(long number) {
            msn.append(number);
            msn.append(":* ");
            return this;
        }
        
        public Flags add() {
            add = true;
            subtract = false;
            return this;
        }
        
        public Flags subtract() {
            add = false;
            subtract = true;
            return this;
        }
        
        public Flags silent() {
            silent = true;
            return this;
        }
        
        public Flags deleted() {
            return append("\\DELETED");
        }
        
        public Flags flagged() {
            return append("\\FLAGGED");
        }
        
        public Flags answered() {
            return append("\\ANSWERED");
        }
        
        public Flags seen() {
            return append("\\SEEN");
        }
        
        public Flags draft() {
            return append("\\DRAFT");
        }
        
        public String command() {
            String flags;
            if (add) {
                flags =" +FLAGS " ;
            } else if (subtract) {
                flags =" -FLAGS " ;
            } else {
                flags = " FLAGS ";
            }
            if (silent) {
                flags = flags + ".SILENT";
            }
            return "STORE " + msn + flags + this.flags + ")";
        }
        
        private Flags append(String term) {
            if (first) {
                first = false;
            } else {
                flags.append(' ');
            }
            flags.append(term);
            return this;
        }
    }
    
    public static final class Search {

        private StringBuffer buffer;
        private boolean first;
        private boolean uidSearch = false;
        
        public Search() {
            clear();
        }
        
        public final boolean isUidSearch() {
            return uidSearch;
        }

        public final void setUidSearch(boolean uidSearch) {
            this.uidSearch = uidSearch;
        }

        public String command() {
            if (uidSearch) {
                return "UID SEARCH " + buffer.toString();
            } else {
                return "SEARCH " + buffer.toString();
            }
        }
        
        public void clear() {
            buffer = new StringBuffer();
            first = true;            
        }
        
        private Search append(long term) {
            return append(new Long(term).toString());
        }
        
        private Search append(String term) {
            if (first) {
                first = false;
            } else {
                buffer.append(' ');
            }
            buffer.append(term);
            return this;
        }
        
        private Search date(int year, int month, int day) {
            append(day);
            switch (month) {
                case 1: 
                    buffer.append("-Jan-");
                    break;
                case 2: 
                    buffer.append("-Feb-");
                    break;
                case 3: 
                    buffer.append("-Mar-");
                    break;
                case 4: 
                    buffer.append("-Apr-");
                    break;
                case 5: 
                    buffer.append("-May-");
                    break;
                case 6: 
                    buffer.append("-Jun-");
                    break;
                case 7: 
                    buffer.append("-Jul-");
                    break;
                case 8: 
                    buffer.append("-Aug-");
                    break;
                case 9: 
                    buffer.append("-Sep-");
                    break;
                case 10: 
                    buffer.append("-Oct-");
                    break;
                case 11: 
                    buffer.append("-Nov-");
                    break;
                case 12: 
                    buffer.append("-Dec-");
                    break;
            }
            buffer.append(year);
            return this;
        }
        
        public Search all() {
            return append("ALL");
        }
        
        public Search answered() {
            return append("ANSWERED");
        }
        
        public Search bcc(String address) {
            return append("BCC " + address);
        }

        public Search before(int year, int month, int day) {
            return append("BEFORE").date(year, month, day);
        }
        
        public Search body(String text) {
            return append("BODY").append(text);
        }    

        public Search cc(String address) {
            return append("CC").append(address);
        }

        public Search deleted() {
            return append("DELETED");
        }
        
        public Search draft() {
            return append("DRAFT");
        }

        public Search flagged() {
            return append("FLAGGED");
        }

        public Search from(String address) {
            return append("FROM").append(address);
        }

        public Search header(String field, String value) {
            return append("HEADER").append(field).append(value); 
        }
        
        public Search keyword(String flag) {
            return append("KEYWORD").append(flag);
        }
        
        public Search larger(long size) {
            return append("LARGER").append(size);
        }

        public Search NEW() {
            return append("NEW");
        }
        
        public Search not() {
            return append("NOT");
        }
        
        public Search old() {
            return append("OLD");
        }
        
        public Search on(int year, int month, int day) {
            return append("ON").date(year, month, day);
        }

        public Search or() {
            return append("OR");
        }

        public Search recent() {
            return append("RECENT");
        }

        public Search seen() {
            return append("SEEN");
        }

        public Search sentbefore(int year, int month, int day) {
            return append("SENTBEFORE").date(year, month, day);
        }

        public Search senton(int year, int month, int day) {
            return append("SENTON").date(year, month, day); 
        }

        public Search sentsince(int year, int month, int day) {
            return append("SENTSINCE").date(year, month, day);
        }
        
        public Search since(int year, int month, int day) {
            return append("SINCE").date(year, month, day); 
        }
        
        public Search smaller(int size) {
            return append("SMALLER").append(size);}

        public Search subject(String address) {
            return append("SUBJECT").append(address);
        }
        
        public Search text(String text) {
            return append("TEXT").append(text);
        }
        
        public Search to(String address) {
            return append("TO").append(address);
        }
        
        public Search uid() {
            return append("UID");
        }

        public Search unanswered() {
            return append("UNANSWERED");
        }
        
        public Search undeleted() {
            return append("UNDELETED");
        }

        public Search undraft() {
            return append("UNDRAFT");
        }
        
        public Search unflagged() {
            return append("UNFLAGGED");
        }

        public Search unkeyword(String flag) {
            return append("UNKEYWORD").append(flag);
        }
        
        public Search unseen() {
            return append("UNSEEN");
        }
        
        public Search openParen() {
            return append("(");
        }
        
        public Search closeParen() {
            return append(")");
        }
        
        public Search msn(int low, int high) {
            return append(low + ":" + high);
        }
        
        public Search msnAndUp(int limit) {
            return append(limit + ":*");
        }
        
        public Search msnAndDown(int limit) {
            return append("*:" + limit);
        }
    }
    
    public static final class Fetch {
        
        public static final String[] COMPREHENSIVE_HEADERS = {"DATE", "FROM", "TO", "CC", "SUBJECT", 
            "REFERENCES", "IN-REPLY-TO", "MESSAGE-ID", "MIME-VERSION", "CONTENT-TYPE", 
            "X-MAILING-LIST", "X-LOOP", "LIST-ID", "LIST-POST", "MAILING-LIST", "ORIGINATOR", "X-LIST", 
            "SENDER", "RETURN-PATH", "X-BEENTHERE"};
        
        
        public static final String[] SELECT_HEADERS = {"DATE", "FROM", "TO", "ORIGINATOR", "X-LIST"};
        
        private boolean flagsFetch = false;
        private boolean rfc822Size = false;
        private boolean internalDate = false;
        private boolean uid = false;
        private String bodyPeek = null;
        
        public String command(int messageNumber) {
            return "FETCH " + messageNumber + "(" + fetchData() + ")";
        }
        
        public String command() {
            return "FETCH 1:* (" + fetchData() + ")";
        }
        
        public final boolean isFlagsFetch() {
            return flagsFetch;
        }

        public final Fetch setFlagsFetch(boolean flagsFetch) {
            this.flagsFetch = flagsFetch;
            return this;
        }
        
        public final boolean isUid() {
            return uid;
        }

        public final Fetch setUid(boolean uid) {
            this.uid = uid;
            return this;
        }

        public final boolean isRfc822Size() {
            return rfc822Size;
        }

        public final Fetch setRfc822Size(boolean rfc822Size) {
            this.rfc822Size = rfc822Size;
            return this;
        }
        
        public final boolean isInternalDate() {
            return internalDate;
        }

        public final Fetch setInternalDate(boolean internalDate) {
            this.internalDate = internalDate;
            return this;
        }
        
        public final String getBodyPeek() {
            return bodyPeek;
        }

        public final void setBodyPeek(String bodyPeek) {
            this.bodyPeek = bodyPeek;
        }

        public void bodyPeekCompleteMessage() {
            setBodyPeek(buildBody(true, ""));
        }
        
        public void bodyPeekNotHeaders(String[] fields) {
            setBodyPeek(buildBody(true, buildHeaderFields(fields, true)));
        }
        
        public Fetch bodyPeekHeaders(String[] fields) {
            setBodyPeek(buildBody(true, buildHeaderFields(fields, false)));
            return this;
        }
        
        public String buildBody(boolean peek, String section) {
            String result;
            if (peek) {
                result  = "BODY.PEEK[";
            } else {
                result = "BODY[";
            }
            result = result + section + "]";
            return result;
        }
        
        public String buildHeaderFields(String[] fields, boolean not) {
            String result;
            if (not) {
                result = "HEADER.FIELDS.NOT (";
            } else {
                result = "HEADER.FIELDS (";
            }
            for (int i = 0; i < fields.length; i++) {
                if (i>0) {
                    result = result + " ";
                }
                result = result + fields[i];
            }
            result = result + ")";
            return result;
        }
        
        public String fetchData() {
            final StringBuffer buffer = new StringBuffer();
            boolean first = true;
            if (flagsFetch) {
                first = add(buffer, first, "FLAGS");
            }
            if (rfc822Size) {
                first = add(buffer, first, "RFC822.SIZE");
            }
            if (internalDate) {
                first = add(buffer, first, "INTERNALDATE");
            }
            if (uid) {
                first = add(buffer, first, "UID");
            }
            add(buffer, first, bodyPeek);
            return buffer.toString();
        }

        private boolean add(final StringBuffer buffer, boolean first, String atom) {
            if (atom != null) {
                if (first) {
                    first = false;
                } else {
                    buffer.append(" ");
                }
                buffer.append(atom);
            }
            return first;
        }
    }
    
    public static final class Client {
        
        private static final Charset ASCII = Charset.forName("us-ascii");
        
        private final Out out;
        private final ReadableByteChannel source;
        private final WritableByteChannel sump;
        private ByteBuffer inBuffer = ByteBuffer.allocate(256);
        private final ByteBuffer outBuffer = ByteBuffer.allocate(262144);
        private final ByteBuffer crlf;
        private boolean isLineTagged = false;
        private int continuationBytes = 0;
        
        public Client(ReadableByteChannel source, WritableByteChannel sump) throws Exception {
            super();
            this.source = source;
            this.sump = sump;
            this.out = new Out();
            byte[] crlf = {'\r', '\n'};
            this.crlf = ByteBuffer.wrap(crlf);
            inBuffer.flip();
            readLine();
        }

        public void write(InputStream in) throws Exception {
            outBuffer.clear();
            int next = in.read(); 
            while(next != -1) {
                if (next == '\n') {
                    outBufferNext((byte)'\r');
                    outBufferNext((byte)'\n');
                } else if (next == '\r') {
                    outBufferNext((byte)'\r');
                    outBufferNext((byte)'\n');
                    next = in.read();
                    if (next == '\n') {
                        next = in.read();
                    } else if (next != -1) {
                        outBufferNext((byte) next);
                    }
                } else {
                    outBufferNext((byte) next);
                }
                next = in.read();
            }
            
            writeOutBuffer();
        }
        
        public void outBufferNext(byte next) throws Exception {
            outBuffer.put(next);
        }

        private void writeOutBuffer() throws Exception {
            outBuffer.flip();
            int count = outBuffer.limit();
            String continuation = " {" + count + "+}";
            write(continuation);
            lineEnd();
            out.client();
            while (outBuffer.hasRemaining()) {
                final byte next = outBuffer.get();
                print (next);
                if (next == '\n') {
                    out.client();
                }
            }
            outBuffer.rewind();
            while (outBuffer.hasRemaining()) {
                sump.write(outBuffer);
            }
        }

        public void readResponse() throws Exception {
            isLineTagged = false;
            while (!isLineTagged) {
                readLine();
            }
        }
        
        private byte next() throws Exception {
            byte result;
            if (inBuffer.hasRemaining()) {
                result = inBuffer.get();
                print(result);
            } else {
                inBuffer.compact();
                while (source.read(inBuffer) == 0);
                inBuffer.flip();
                result = next();
            }
            return result;
        }
        
        private void print(char next) {
            out.print(next);
        }
            
        private void print(byte next) {
            print((char) next);
        }
        
        public void lineStart() throws Exception {
            out.client();
        }
        
        public void write(String phrase) throws Exception {
            out.print(phrase);
            final ByteBuffer buffer = ASCII.encode(phrase);
            writeRemaining(buffer);
        }
        
        public void writeLine(String line) throws Exception {
            lineStart();
            write(line);
            lineEnd();
        }

        private void writeRemaining(final ByteBuffer buffer) throws IOException {
            while (buffer.hasRemaining()) {
                sump.write(buffer);
            }
        }
        
        public void lineEnd() throws Exception {
            out.lineEnd();
            crlf.rewind();
            writeRemaining(crlf);
        }
        
        private void readLine() throws Exception {
            out.server();
            
            final byte next = next();
            isLineTagged = next != '*';
            readRestOfLine(next);
        }

        private void readRestOfLine(byte next) throws Exception {
            while (next != '\r') {
                if (next == '{') {
                    startContinuation();
                }
                next = next();
            }
            next();
        }

        private void startContinuation() throws Exception {
            continuationBytes = 0;
            continuation();
        }
        
        private void continuation() throws Exception {
            byte next = next();
            switch(next) {
                case '0':
                    continuationDigit(0);
                    break;
                case '1':
                    continuationDigit(1);
                    break;
                case '2':
                    continuationDigit(2);
                    break;
                case '3':
                    continuationDigit(3);
                    break;
                case '4':
                    continuationDigit(4);
                    break;
                case '5':
                    continuationDigit(5);
                    break;
                case '6':
                    continuationDigit(6);
                    break;
                case '7':
                    continuationDigit(7);
                    break;
                case '8':
                    continuationDigit(8);
                    break;
                case '9':
                    continuationDigit(9);
                    break;
                case '+':
                    next();
                    next();                   
                    readContinuation();
                    break;
                default:
                    next();
                    next();
                    readContinuation();
                    break;
            }
        }
        
        private void readContinuation() throws Exception {
           out.server();
            while (continuationBytes-- > 0) {
                int next = next();
                if (next == '\n') {
                   out.server();
                }
            }
        }
        
        private void continuationDigit(int digit) throws Exception {
            continuationBytes = 10*continuationBytes + digit;
            continuation();
        }
        
        public void close() throws Exception {
            source.close();
            sump.close();
        }
    }
    
    private static final class Out {
        private static final String[] IGNORE_LINES_STARTING_WITH = {  
                                                        "S: \\* OK \\[PERMANENTFLAGS",
                                                        "C: A22 LOGOUT",
                                                        "S: \\* BYE Logging out", 
                                                        "S: \\* OK Dovecot ready\\."};
        private static final String[] IGNORE_LINES_CONTAINING = {
                                        "OK Logout completed.",
                                        "LOGIN imapuser password", 
                                        "OK Logged in",
                                        "LOGOUT"};
        
        private final CharBuffer lineBuffer = CharBuffer.allocate(65536);
        private boolean isClient = false;
        public void client() {
            lineBuffer.put("C: ");
            isClient = true;
        }
        
        public void print(char next) {
            if (!isClient) {
                escape(next);
            }
            lineBuffer.put(next);
        }

        private void escape(char next) {
            if (next == '\\' || next == '*' || next=='.' || next == '[' || next == ']' || next == '+'
                || next == '(' || next == ')' || next == '{' || next == '}' || next == '?') {
                lineBuffer.put('\\');
            }
        }

        public void server() {
            lineBuffer.put("S: ");
            isClient = false;
        }
        
        public void print(String phrase) {
            if (!isClient) {
                phrase = StringUtils.replace(phrase, "\\", "\\\\");
                phrase = StringUtils.replace(phrase, "*", "\\*");
                phrase = StringUtils.replace(phrase, ".", "\\.");
                phrase = StringUtils.replace(phrase, "[", "\\[");
                phrase = StringUtils.replace(phrase, "]", "\\]");
                phrase = StringUtils.replace(phrase, "+", "\\+");
                phrase = StringUtils.replace(phrase, "(", "\\(");
                phrase = StringUtils.replace(phrase, ")", "\\)");
                phrase = StringUtils.replace(phrase, "}", "\\}");
                phrase = StringUtils.replace(phrase, "{", "\\{");
                phrase = StringUtils.replace(phrase, "?", "\\?");
            }
            lineBuffer.put(phrase);
        }
        
        public void lineEnd() {
            lineBuffer.flip();
            final String text = lineBuffer.toString();
            String[] lines = text.split("\r\n");
            for (int i = 0; i < lines.length; i++) {
                String line = StringUtils.chomp(lines[i]);    
                if (!ignoreLine(line)) {
                    line = StringUtils.replace(line, "OK Create completed", "OK CREATE completed");
                    line = StringUtils.replace(line, "OK Fetch completed", "OK FETCH completed");
                    line = StringUtils.replace(line, "OK Append completed", "OK APPEND completed");
                    line = StringUtils.replace(line, "OK Delete completed", "OK DELETE completed");
                    line = StringUtils.replace(line, "Select completed", "SELECT completed");
                    line = StringUtils.replace(line, "\\\\Seen \\\\Draft", "\\\\Draft \\\\Seen");
                    line = StringUtils.replace(line, "\\\\Flagged \\\\Deleted", "\\\\Deleted \\\\Flagged");
                    line = StringUtils.replace(line, "\\\\Flagged \\\\Draft", "\\\\Draft \\\\Flagged");
                    line = StringUtils.replace(line, "\\\\Seen \\\\Recent", "\\\\Recent \\\\Seen");
                    line = StringUtils.replace(line, "\\] First unseen\\.", "\\](.)*");
                    if (line.startsWith("S: \\* OK \\[UIDVALIDITY ")) {
                        line = "S: \\* OK \\[UIDVALIDITY \\d+\\]";
                    } else if (line.startsWith("S: \\* OK \\[UIDNEXT")) {
                        line = "S: \\* OK \\[PERMANENTFLAGS \\(\\\\Answered \\\\Deleted \\\\Draft \\\\Flagged \\\\Seen\\)\\]";
                    }
                    System.out.println(line);
                }
            }
            lineBuffer.clear();
        }

        private boolean ignoreLine(String line) {
            boolean result = false;
            for (int i = 0; i < IGNORE_LINES_CONTAINING.length; i++) {
                if (line.indexOf(IGNORE_LINES_CONTAINING[i]) > 0) {
                    result = true;
                    break;
                }
            }
            for (int i = 0; i < IGNORE_LINES_STARTING_WITH.length && ! result; i++) {
                if (line.startsWith(IGNORE_LINES_STARTING_WITH[i])) {
                    result = true;
                    break;
                }
            }
            return result;
        }
    }
    
    private static final class IgnoreHeaderInputStream extends InputStream {

        private boolean isFinishedHeaders = false;
        private final InputStream delegate;
        
        public IgnoreHeaderInputStream(final InputStream delegate) {
            super();
            this.delegate = delegate;
        }

        public int read() throws IOException {
            final int result;
            final int next = delegate.read();
            if (isFinishedHeaders) {
                result = next;
            } else {
                switch (next) {
                    case -1:
                        isFinishedHeaders = true;
                        result = next;
                        break;
                    case '#':
                        readLine();
                        result = read();
                        break;
                        
                    case '\r':
                    case '\n':
                        result = read();
                        break;
                        
                    default:
                        isFinishedHeaders = true;
                        result = next;
                        break;
                }
            }
            return result;
        }
        private void readLine() throws IOException {
            int next = delegate.read();
            while (next != -1 && next !='\r' && next !='\n') {
                next = delegate.read();
            }
        }
    }
}
