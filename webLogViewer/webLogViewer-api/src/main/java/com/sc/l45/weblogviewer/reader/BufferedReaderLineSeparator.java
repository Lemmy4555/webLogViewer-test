package com.sc.l45.weblogviewer.reader;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;

public class BufferedReaderLineSeparator extends Reader {
    Reader in;
    char[] buffer;
    /* Index of current read position. Must be >= 0 and <= limit. */
    /*
     * There is a special case where pos may be equal to limit+1; this is used
     * as an indicator that a readLine was done with a '\r' was the very last
     * char in the buffer. Since we don't want to read-ahead and potentially
     * block, we set pos this way to indicate the situation and deal with it
     * later. Doing it this way rather than having a separate boolean field to
     * indicate the condition has the advantage that it is self-clearing on
     * things like mark/reset.
     */
    int pos;
    /* Limit of valid data in buffer. Must be >= pos and <= buffer.length. */
    /* This can be < pos in the one special case described above. */
    int limit;

    /*
     * The value -1 means there is no mark, or the mark has been invalidated.
     * Otherwise, markPos is the index in the buffer of the marked position.
     * Must be >= 0 and <= pos. Note we do not explicitly store the read-limit.
     * The implicit read-limit is (buffer.length - markPos), which is guaranteed
     * to be >= the read-limit requested in the call to mark.
     */
    int markPos = -1;

    // The JCL book specifies the default buffer size as 8K characters.
    // This is package-private because it is used by LineNumberReader.
    static final int DEFAULT_BUFFER_SIZE = 8192;

    /**
     * The line buffer for <code>readLine</code>.
     */
    private StringBuffer sbuf = null;

    /**
     * Create a new <code>BufferedReader</code> that will read from the
     * specified subordinate stream with a default buffer size of 8192 chars.
     *
     * @param in
     *            The subordinate stream to read from
     */
    public BufferedReaderLineSeparator(Reader in) {
        this(in, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Create a new <code>BufferedReader</code> that will read from the
     * specified subordinate stream with a buffer size that is specified by the
     * caller.
     *
     * @param in
     *            The subordinate stream to read from
     * @param size
     *            The buffer size to use
     *
     * @exception IllegalArgumentException
     *                if size &lt;= 0
     */
    public BufferedReaderLineSeparator(Reader in, int size) {
        super(getLock(in));
        if (size <= 0)
            throw new IllegalArgumentException("Illegal buffer size: " + size);
        this.in = in;
        buffer = new char[size];
    }

    /*
     * Read more data into the buffer. Update pos and limit appropriately.
     * Assumes pos==limit initially. May invalidate the mark if read too much.
     * Return number of chars read (never 0), or -1 on eof.
     */
    private int fill() throws IOException {
        checkStatus();
        // Handle the special case of a readLine that has a '\r' at the end of
        // the buffer. In this case, we'll need to skip a '\n' if it is the
        // next char to be read. This special case is indicated by 'pos >
        // limit'.
        boolean retAtEndOfBuffer = false;
        if (pos > limit) {
            retAtEndOfBuffer = true;
            --pos;
        }

        if (markPos >= 0 && limit == buffer.length)
            markPos = -1;
        if (markPos < 0)
            pos = limit = 0;
        int count = in.read(buffer, limit, buffer.length - limit);
        if (count > 0)
            limit += count;

        if (retAtEndOfBuffer && buffer[pos] == '\n') {
            --count;
            // If the mark was set to the location of the \n, then we
            // must change it to fully pretend that the \n does not
            // exist.
            if (markPos == pos)
                ++markPos;
            ++pos;
        }

        return count;
    }

    /*
     * Return the end of the line starting at this.pos and ending at limit. The
     * index returns is *before* any line terminators, or limit if no line
     * terminators were found.
     */
    private int lineEnd(int limit) {
        int i = pos;
        for (; i < limit; i++) {
            char ch = buffer[i];
            if (ch == '\n' || ch == '\r')
                break;
        }
        return i;
    }

    /**
     * This method reads a single line of text from the input stream, returning
     * it as a <code>String</code>. A line is terminated by "\n", a "\r", or an
     * "\r\n" sequence. The system dependent line separator is not used. The
     * line termination characters are not returned in the resulting
     * <code>String</code>.
     * 
     * @return The line of text read, or <code>null</code> if end of stream.
     * 
     * @exception IOException
     *                If an error occurs
     */
    public String readLine() throws IOException {
        checkStatus();
        StringBuilder mustReturnThisEndLine = null;
        // Handle the special case where a previous readLine (with no
        // intervening
        // reads/skips) had a '\r' at the end of the buffer.
        // In this case, we'll need to skip a '\n' if it's the next char to be
        // read.
        // This special case is indicated by 'pos > limit'.
        if (pos > limit) {
            int ch = read();
            if (ch < 0)
                return null;
            if (ch != '\n')
                --pos;
        }
        int i = lineEnd(limit);
        if (i < limit) {
            String str = String.valueOf(buffer, pos, getEndLineOffset(i));
            pos = i + 1;
            // If the last char in the buffer is a '\r', we must remember
            // to check if the next char to be read after the buffer is refilled
            // is a '\n'. If so, skip it. To indicate this condition, we set pos
            // to be limit + 1, which normally is never possible.
            if (buffer[i] == '\r')
                if (pos == limit || buffer[pos] == '\n')
                    pos++;
            return str;
        }
        if (sbuf == null)
            sbuf = new StringBuffer(200);
        else
            sbuf.setLength(0);
        sbuf.append(buffer, pos, i - pos);
        pos = i;
        // We only want to return null when no characters were read before
        // EOF. So we must keep track of this separately. Otherwise we
        // would treat an empty `sbuf' as an EOF condition, which is wrong
        // when there is just a newline.
        boolean eof = false;
        for (;;) {
            // readLine should block. So we must not return until a -1 is
            // reached.
            if (pos >= limit) {
                // here count == 0 isn't sufficient to give a failure.
                int count = fill();
                if (count < 0) {
                    eof = true;
                    break;
                }
                continue;
            }
            int ch = buffer[pos++];
            
            mustReturnThisEndLine = new StringBuilder();
            
            if (ch == '\n' || ch == '\r') {
                // Check here if a '\r' was the last char in the buffer; if so,
                // mark it as in the comment above to indicate future reads
                // should skip a newline that is the next char read after
                // refilling the buffer.
                mustReturnThisEndLine.append((char) ch);
                if (ch == '\r')
                    if (pos == limit || buffer[pos] == '\n') {
                        mustReturnThisEndLine.append(buffer[pos]);
                        pos++;
                    }
                break;
            }
            i = lineEnd(limit);
            sbuf.append(buffer, pos - 1, i - (pos - 1));
            pos = i;
        }
        StringBuffer str = (sbuf.length() == 0 && eof) ? null : sbuf;
        if(sbuf.length() == 0 && str != null && !mustReturnThisEndLine.toString().isEmpty()) {
            return mustReturnThisEndLine.toString();
        }
        return str != null ? str.toString() : null;
    }

    int getEndLineOffset(int i) {
        if (pos == 1 || i == buffer.length - 1) {
            return i - pos;
        }

        int res = 0;

        int a = pos - 1;
        int b = pos - 2;
        if (buffer[a] == '\r' || buffer[a] == '\n') {
            if (buffer[b] == '\r' || buffer[b] == '\n') {
                res = (i - pos) + 2;
            } else {
                res = (i - pos) + 1;
            }
        } else {
            res = i - pos;
        }

        return res;
    }

    String appendEndline(StringBuffer str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(str);
        int a = pos > buffer.length ? buffer.length - 1 : pos - 1;
        int b = pos > buffer.length ? buffer.length - 2 : pos - 2;
        if(a < 0 || b < 0) {
            a = 0;
            b = 0;
        }
        if (buffer[a] == '\r' || buffer[a] == '\n') {
            if (buffer[b] == '\r' || buffer[b] == '\n') {
                sb.append(buffer[b]);
            }
            sb.append(buffer[a]);
        }
        return sb.toString();
    }

    private void checkStatus() throws IOException {
        if (in == null)
            throw new IOException("Stream closed");
    }

    @Override
    public void close() throws IOException {
        synchronized (lock) {
            if (in != null)
                in.close();
            in = null;
            buffer = null;
        }
    }

    /**
     * This method read chars from a stream and stores them into a caller
     * supplied buffer. It starts storing the data at index <code>offset</code>
     * into the buffer and attempts to read <code>len</code> chars. This method
     * can return before reading the number of chars requested. The actual
     * number of chars read is returned as an int. A -1 is returned to indicate
     * the end of the stream.
     * <p>
     * This method will block until some data can be read.
     *
     * @param buf
     *            The array into which the chars read should be stored
     * @param offset
     *            The offset into the array to start storing chars
     * @param count
     *            The requested number of chars to read
     *
     * @return The actual number of chars read, or -1 if end of stream.
     *
     * @exception IOException
     *                If an error occurs.
     * @exception IndexOutOfBoundsException
     *                If offset and count are not valid regarding buf.
     */
    public int read(char[] buf, int offset, int count) throws IOException {
        if (offset < 0 || offset + count > buf.length || count < 0)
            throw new IndexOutOfBoundsException();

        synchronized (lock) {
            checkStatus();
            // Once again, we need to handle the special case of a readLine
            // that has a '\r' at the end of the buffer. In this case, we'll
            // need to skip a '\n' if it is the next char to be read.
            // This special case is indicated by 'pos > limit'.
            boolean retAtEndOfBuffer = false;

            int avail = limit - pos;
            if (count > avail) {
                if (avail > 0)
                    count = avail;
                else
                {
                    if (limit == buffer.length)
                        markPos = -1; // read too far - invalidate the mark.
                    if (pos > limit) {
                        // Set a boolean and make pos == limit to simplify
                        // things.
                        retAtEndOfBuffer = true;
                        --pos;
                    }
                    if (markPos < 0) {
                        // Optimization: can read directly into buf.
                        if (count >= buffer.length && !retAtEndOfBuffer)
                            return in.read(buf, offset, count);
                        pos = limit = 0;
                    }
                    avail = in.read(buffer, limit, buffer.length - limit);
                    if (retAtEndOfBuffer && avail > 0 && buffer[limit] == '\n') {
                        --avail;
                        limit++;
                    }
                    if (avail < count) {
                        if (avail <= 0)
                            return avail;
                        count = avail;
                    }
                    limit += avail;
                }
            }
            System.arraycopy(buffer, pos, buf, offset, count);
            pos += count;
            return count;
        }
    }

    public static Object getLock(Reader in) {
        try {
            Field field = in.getClass().getSuperclass().getDeclaredField("lock");
            field.setAccessible(true);
            return field.get(in);
        } catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
