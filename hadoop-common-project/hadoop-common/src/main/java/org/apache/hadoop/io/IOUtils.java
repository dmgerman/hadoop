begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|WritableByteChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|DirectoryStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|DirectoryIteratorException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|StandardOpenOption
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|PathIOException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|Shell
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|IO_FILE_BUFFER_SIZE_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|IO_FILE_BUFFER_SIZE_KEY
import|;
end_import

begin_comment
comment|/**  * An utility class for I/O related functionality.   */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|IOUtils
specifier|public
class|class
name|IOUtils
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|IOUtils
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Copies from one stream to another.    *    * @param in InputStrem to read from    * @param out OutputStream to write to    * @param buffSize the size of the buffer     * @param close whether or not close the InputStream and     * OutputStream at the end. The streams are closed in the finally clause.      */
DECL|method|copyBytes (InputStream in, OutputStream out, int buffSize, boolean close)
specifier|public
specifier|static
name|void
name|copyBytes
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|OutputStream
name|out
parameter_list|,
name|int
name|buffSize
parameter_list|,
name|boolean
name|close
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|copyBytes
argument_list|(
name|in
argument_list|,
name|out
argument_list|,
name|buffSize
argument_list|)
expr_stmt|;
if|if
condition|(
name|close
condition|)
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|=
literal|null
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|in
operator|=
literal|null
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|close
condition|)
block|{
name|closeStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|closeStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Copies from one stream to another.    *     * @param in InputStrem to read from    * @param out OutputStream to write to    * @param buffSize the size of the buffer     */
DECL|method|copyBytes (InputStream in, OutputStream out, int buffSize)
specifier|public
specifier|static
name|void
name|copyBytes
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|OutputStream
name|out
parameter_list|,
name|int
name|buffSize
parameter_list|)
throws|throws
name|IOException
block|{
name|PrintStream
name|ps
init|=
name|out
operator|instanceof
name|PrintStream
condition|?
operator|(
name|PrintStream
operator|)
name|out
else|:
literal|null
decl_stmt|;
name|byte
name|buf
index|[]
init|=
operator|new
name|byte
index|[
name|buffSize
index|]
decl_stmt|;
name|int
name|bytesRead
init|=
name|in
operator|.
name|read
argument_list|(
name|buf
argument_list|)
decl_stmt|;
while|while
condition|(
name|bytesRead
operator|>=
literal|0
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|bytesRead
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|ps
operator|!=
literal|null
operator|)
operator|&&
name|ps
operator|.
name|checkError
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to write to output stream."
argument_list|)
throw|;
block|}
name|bytesRead
operator|=
name|in
operator|.
name|read
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Copies from one stream to another.<strong>closes the input and output streams     * at the end</strong>.    *    * @param in InputStrem to read from    * @param out OutputStream to write to    * @param conf the Configuration object     */
DECL|method|copyBytes (InputStream in, OutputStream out, Configuration conf)
specifier|public
specifier|static
name|void
name|copyBytes
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|OutputStream
name|out
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|copyBytes
argument_list|(
name|in
argument_list|,
name|out
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
name|IO_FILE_BUFFER_SIZE_KEY
argument_list|,
name|IO_FILE_BUFFER_SIZE_DEFAULT
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Copies from one stream to another.    *    * @param in InputStream to read from    * @param out OutputStream to write to    * @param conf the Configuration object    * @param close whether or not close the InputStream and     * OutputStream at the end. The streams are closed in the finally clause.    */
DECL|method|copyBytes (InputStream in, OutputStream out, Configuration conf, boolean close)
specifier|public
specifier|static
name|void
name|copyBytes
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|OutputStream
name|out
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|boolean
name|close
parameter_list|)
throws|throws
name|IOException
block|{
name|copyBytes
argument_list|(
name|in
argument_list|,
name|out
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
name|IO_FILE_BUFFER_SIZE_KEY
argument_list|,
name|IO_FILE_BUFFER_SIZE_DEFAULT
argument_list|)
argument_list|,
name|close
argument_list|)
expr_stmt|;
block|}
comment|/**    * Copies count bytes from one stream to another.    *    * @param in InputStream to read from    * @param out OutputStream to write to    * @param count number of bytes to copy    * @param close whether to close the streams    * @throws IOException if bytes can not be read or written    */
DECL|method|copyBytes (InputStream in, OutputStream out, long count, boolean close)
specifier|public
specifier|static
name|void
name|copyBytes
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|OutputStream
name|out
parameter_list|,
name|long
name|count
parameter_list|,
name|boolean
name|close
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
name|buf
index|[]
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
name|long
name|bytesRemaining
init|=
name|count
decl_stmt|;
name|int
name|bytesRead
decl_stmt|;
try|try
block|{
while|while
condition|(
name|bytesRemaining
operator|>
literal|0
condition|)
block|{
name|int
name|bytesToRead
init|=
call|(
name|int
call|)
argument_list|(
name|bytesRemaining
operator|<
name|buf
operator|.
name|length
condition|?
name|bytesRemaining
else|:
name|buf
operator|.
name|length
argument_list|)
decl_stmt|;
name|bytesRead
operator|=
name|in
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|bytesToRead
argument_list|)
expr_stmt|;
if|if
condition|(
name|bytesRead
operator|==
operator|-
literal|1
condition|)
break|break;
name|out
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|bytesRead
argument_list|)
expr_stmt|;
name|bytesRemaining
operator|-=
name|bytesRead
expr_stmt|;
block|}
if|if
condition|(
name|close
condition|)
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|=
literal|null
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|in
operator|=
literal|null
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|close
condition|)
block|{
name|closeStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|closeStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Utility wrapper for reading from {@link InputStream}. It catches any errors    * thrown by the underlying stream (either IO or decompression-related), and    * re-throws as an IOException.    *     * @param is - InputStream to be read from    * @param buf - buffer the data is read into    * @param off - offset within buf    * @param len - amount of data to be read    * @return number of bytes read    */
DECL|method|wrappedReadForCompressedData (InputStream is, byte[] buf, int off, int len)
specifier|public
specifier|static
name|int
name|wrappedReadForCompressedData
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|is
operator|.
name|read
argument_list|(
name|buf
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
throw|throw
name|ie
throw|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error while reading compressed data"
argument_list|,
name|t
argument_list|)
throw|;
block|}
block|}
comment|/**    * Reads len bytes in a loop.    *    * @param in InputStream to read from    * @param buf The buffer to fill    * @param off offset from the buffer    * @param len the length of bytes to read    * @throws IOException if it could not read requested number of bytes     * for any reason (including EOF)    */
DECL|method|readFully (InputStream in, byte[] buf, int off, int len)
specifier|public
specifier|static
name|void
name|readFully
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|toRead
init|=
name|len
decl_stmt|;
while|while
condition|(
name|toRead
operator|>
literal|0
condition|)
block|{
name|int
name|ret
init|=
name|in
operator|.
name|read
argument_list|(
name|buf
argument_list|,
name|off
argument_list|,
name|toRead
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Premature EOF from inputStream"
argument_list|)
throw|;
block|}
name|toRead
operator|-=
name|ret
expr_stmt|;
name|off
operator|+=
name|ret
expr_stmt|;
block|}
block|}
comment|/**    * Similar to readFully(). Skips bytes in a loop.    * @param in The InputStream to skip bytes from    * @param len number of bytes to skip.    * @throws IOException if it could not skip requested number of bytes     * for any reason (including EOF)    */
DECL|method|skipFully (InputStream in, long len)
specifier|public
specifier|static
name|void
name|skipFully
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|long
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|amt
init|=
name|len
decl_stmt|;
while|while
condition|(
name|amt
operator|>
literal|0
condition|)
block|{
name|long
name|ret
init|=
name|in
operator|.
name|skip
argument_list|(
name|amt
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|==
literal|0
condition|)
block|{
comment|// skip may return 0 even if we're not at EOF.  Luckily, we can
comment|// use the read() method to figure out if we're at the end.
name|int
name|b
init|=
name|in
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|b
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"Premature EOF from inputStream after "
operator|+
literal|"skipping "
operator|+
operator|(
name|len
operator|-
name|amt
operator|)
operator|+
literal|" byte(s)."
argument_list|)
throw|;
block|}
name|ret
operator|=
literal|1
expr_stmt|;
block|}
name|amt
operator|-=
name|ret
expr_stmt|;
block|}
block|}
comment|/**    * Close the Closeable objects and<b>ignore</b> any {@link Throwable} or    * null pointers. Must only be used for cleanup in exception handlers.    *    * @param log the log to record problems to at debug level. Can be null.    * @param closeables the objects to close    * @deprecated use {@link #cleanupWithLogger(Logger, java.io.Closeable...)}    * instead    */
annotation|@
name|Deprecated
DECL|method|cleanup (Log log, java.io.Closeable... closeables)
specifier|public
specifier|static
name|void
name|cleanup
parameter_list|(
name|Log
name|log
parameter_list|,
name|java
operator|.
name|io
operator|.
name|Closeable
modifier|...
name|closeables
parameter_list|)
block|{
for|for
control|(
name|java
operator|.
name|io
operator|.
name|Closeable
name|c
range|:
name|closeables
control|)
block|{
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
name|log
operator|!=
literal|null
operator|&&
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Exception in closing "
operator|+
name|c
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/**    * Close the Closeable objects and<b>ignore</b> any {@link Throwable} or    * null pointers. Must only be used for cleanup in exception handlers.    *    * @param logger the log to record problems to at debug level. Can be null.    * @param closeables the objects to close    */
DECL|method|cleanupWithLogger (Logger logger, java.io.Closeable... closeables)
specifier|public
specifier|static
name|void
name|cleanupWithLogger
parameter_list|(
name|Logger
name|logger
parameter_list|,
name|java
operator|.
name|io
operator|.
name|Closeable
modifier|...
name|closeables
parameter_list|)
block|{
for|for
control|(
name|java
operator|.
name|io
operator|.
name|Closeable
name|c
range|:
name|closeables
control|)
block|{
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
name|logger
operator|!=
literal|null
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Exception in closing {}"
argument_list|,
name|c
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/**    * Closes the stream ignoring {@link Throwable}.    * Must only be called in cleaning up from exception handlers.    *    * @param stream the Stream to close    */
DECL|method|closeStream (java.io.Closeable stream)
specifier|public
specifier|static
name|void
name|closeStream
parameter_list|(
name|java
operator|.
name|io
operator|.
name|Closeable
name|stream
parameter_list|)
block|{
if|if
condition|(
name|stream
operator|!=
literal|null
condition|)
block|{
name|cleanupWithLogger
argument_list|(
literal|null
argument_list|,
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Closes the streams ignoring {@link Throwable}.    * Must only be called in cleaning up from exception handlers.    *    * @param streams the Streams to close    */
DECL|method|closeStreams (java.io.Closeable... streams)
specifier|public
specifier|static
name|void
name|closeStreams
parameter_list|(
name|java
operator|.
name|io
operator|.
name|Closeable
modifier|...
name|streams
parameter_list|)
block|{
if|if
condition|(
name|streams
operator|!=
literal|null
condition|)
block|{
name|cleanupWithLogger
argument_list|(
literal|null
argument_list|,
name|streams
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Closes the socket ignoring {@link IOException}    *    * @param sock the Socket to close    */
DECL|method|closeSocket (Socket sock)
specifier|public
specifier|static
name|void
name|closeSocket
parameter_list|(
name|Socket
name|sock
parameter_list|)
block|{
if|if
condition|(
name|sock
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|sock
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignored
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Ignoring exception while closing socket"
argument_list|,
name|ignored
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * The /dev/null of OutputStreams.    */
DECL|class|NullOutputStream
specifier|public
specifier|static
class|class
name|NullOutputStream
extends|extends
name|OutputStream
block|{
annotation|@
name|Override
DECL|method|write (byte[] b, int off, int len)
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|write (int b)
specifier|public
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
throws|throws
name|IOException
block|{     }
block|}
comment|/**    * Write a ByteBuffer to a WritableByteChannel, handling short writes.    *     * @param bc               The WritableByteChannel to write to    * @param buf              The input buffer    * @throws IOException     On I/O error    */
DECL|method|writeFully (WritableByteChannel bc, ByteBuffer buf)
specifier|public
specifier|static
name|void
name|writeFully
parameter_list|(
name|WritableByteChannel
name|bc
parameter_list|,
name|ByteBuffer
name|buf
parameter_list|)
throws|throws
name|IOException
block|{
do|do
block|{
name|bc
operator|.
name|write
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|buf
operator|.
name|remaining
argument_list|()
operator|>
literal|0
condition|)
do|;
block|}
comment|/**    * Write a ByteBuffer to a FileChannel at a given offset,     * handling short writes.    *     * @param fc               The FileChannel to write to    * @param buf              The input buffer    * @param offset           The offset in the file to start writing at    * @throws IOException     On I/O error    */
DECL|method|writeFully (FileChannel fc, ByteBuffer buf, long offset)
specifier|public
specifier|static
name|void
name|writeFully
parameter_list|(
name|FileChannel
name|fc
parameter_list|,
name|ByteBuffer
name|buf
parameter_list|,
name|long
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
do|do
block|{
name|offset
operator|+=
name|fc
operator|.
name|write
argument_list|(
name|buf
argument_list|,
name|offset
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|buf
operator|.
name|remaining
argument_list|()
operator|>
literal|0
condition|)
do|;
block|}
comment|/**    * Return the complete list of files in a directory as strings.<p>    *    * This is better than File#listDir because it does not ignore IOExceptions.    *    * @param dir              The directory to list.    * @param filter           If non-null, the filter to use when listing    *                         this directory.    * @return                 The list of files in the directory.    *    * @throws IOException     On I/O error    */
DECL|method|listDirectory (File dir, FilenameFilter filter)
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|listDirectory
parameter_list|(
name|File
name|dir
parameter_list|,
name|FilenameFilter
name|filter
parameter_list|)
throws|throws
name|IOException
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
try|try
init|(
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|stream
init|=
name|Files
operator|.
name|newDirectoryStream
argument_list|(
name|dir
operator|.
name|toPath
argument_list|()
argument_list|)
init|)
block|{
for|for
control|(
name|Path
name|entry
range|:
name|stream
control|)
block|{
name|Path
name|fileName
init|=
name|entry
operator|.
name|getFileName
argument_list|()
decl_stmt|;
if|if
condition|(
name|fileName
operator|!=
literal|null
condition|)
block|{
name|String
name|fileNameStr
init|=
name|fileName
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|filter
operator|==
literal|null
operator|)
operator|||
name|filter
operator|.
name|accept
argument_list|(
name|dir
argument_list|,
name|fileNameStr
argument_list|)
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|fileNameStr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|DirectoryIteratorException
name|e
parameter_list|)
block|{
throw|throw
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
return|return
name|list
return|;
block|}
comment|/**    * Ensure that any writes to the given file is written to the storage device    * that contains it. This method opens channel on given File and closes it    * once the sync is done.<br>    * Borrowed from Uwe Schindler in LUCENE-5588    * @param fileToSync the file to fsync    */
DECL|method|fsync (File fileToSync)
specifier|public
specifier|static
name|void
name|fsync
parameter_list|(
name|File
name|fileToSync
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|fileToSync
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"File/Directory "
operator|+
name|fileToSync
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" does not exist"
argument_list|)
throw|;
block|}
name|boolean
name|isDir
init|=
name|fileToSync
operator|.
name|isDirectory
argument_list|()
decl_stmt|;
comment|// HDFS-13586, FileChannel.open fails with AccessDeniedException
comment|// for any directory, ignore.
if|if
condition|(
name|isDir
operator|&&
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
return|return;
block|}
comment|// If the file is a directory we have to open read-only, for regular files
comment|// we must open r/w for the fsync to have an effect. See
comment|// http://blog.httrack.com/blog/2013/11/15/
comment|// everything-you-always-wanted-to-know-about-fsync/
try|try
init|(
name|FileChannel
name|channel
init|=
name|FileChannel
operator|.
name|open
argument_list|(
name|fileToSync
operator|.
name|toPath
argument_list|()
argument_list|,
name|isDir
condition|?
name|StandardOpenOption
operator|.
name|READ
else|:
name|StandardOpenOption
operator|.
name|WRITE
argument_list|)
init|)
block|{
name|fsync
argument_list|(
name|channel
argument_list|,
name|isDir
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Ensure that any writes to the given file is written to the storage device    * that contains it. This method opens channel on given File and closes it    * once the sync is done.    * Borrowed from Uwe Schindler in LUCENE-5588    * @param channel Channel to sync    * @param isDir if true, the given file is a directory (Channel should be    *          opened for read and ignore IOExceptions, because not all file    *          systems and operating systems allow to fsync on a directory)    * @throws IOException    */
DECL|method|fsync (FileChannel channel, boolean isDir)
specifier|public
specifier|static
name|void
name|fsync
parameter_list|(
name|FileChannel
name|channel
parameter_list|,
name|boolean
name|isDir
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|channel
operator|.
name|force
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
if|if
condition|(
name|isDir
condition|)
block|{
assert|assert
operator|!
operator|(
name|Shell
operator|.
name|LINUX
operator|||
name|Shell
operator|.
name|MAC
operator|)
operator|:
literal|"On Linux and MacOSX fsyncing a directory"
operator|+
literal|" should not throw IOException, we just don't want to rely"
operator|+
literal|" on that in production (undocumented)"
operator|+
literal|". Got: "
operator|+
name|ioe
assert|;
comment|// Ignore exception if it is a directory
return|return;
block|}
comment|// Throw original exception
throw|throw
name|ioe
throw|;
block|}
block|}
comment|/**    * Takes an IOException, file/directory path, and method name and returns an    * IOException with the input exception as the cause and also include the    * file,method details. The new exception provides the stack trace of the    * place where the exception is thrown and some extra diagnostics    * information.    *    * Return instance of same exception if exception class has a public string    * constructor; Otherwise return an PathIOException.    * InterruptedIOException and PathIOException are returned unwrapped.    *    * @param path file/directory path    * @param methodName method name    * @param exception the caught exception.    * @return an exception to throw    */
DECL|method|wrapException (final String path, final String methodName, final IOException exception)
specifier|public
specifier|static
name|IOException
name|wrapException
parameter_list|(
specifier|final
name|String
name|path
parameter_list|,
specifier|final
name|String
name|methodName
parameter_list|,
specifier|final
name|IOException
name|exception
parameter_list|)
block|{
if|if
condition|(
name|exception
operator|instanceof
name|InterruptedIOException
operator|||
name|exception
operator|instanceof
name|PathIOException
condition|)
block|{
return|return
name|exception
return|;
block|}
else|else
block|{
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Failed with %s while processing file/directory :[%s] in "
operator|+
literal|"method:[%s]"
argument_list|,
name|exception
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|path
argument_list|,
name|methodName
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|wrapWithMessage
argument_list|(
name|exception
argument_list|,
name|msg
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
comment|// For subclasses which have no (String) constructor throw IOException
comment|// with wrapped message
return|return
operator|new
name|PathIOException
argument_list|(
name|path
argument_list|,
name|exception
argument_list|)
return|;
block|}
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|wrapWithMessage ( final T exception, final String msg)
specifier|private
specifier|static
parameter_list|<
name|T
extends|extends
name|IOException
parameter_list|>
name|T
name|wrapWithMessage
parameter_list|(
specifier|final
name|T
name|exception
parameter_list|,
specifier|final
name|String
name|msg
parameter_list|)
throws|throws
name|T
block|{
name|Class
argument_list|<
name|?
extends|extends
name|Throwable
argument_list|>
name|clazz
init|=
name|exception
operator|.
name|getClass
argument_list|()
decl_stmt|;
try|try
block|{
name|Constructor
argument_list|<
name|?
extends|extends
name|Throwable
argument_list|>
name|ctor
init|=
name|clazz
operator|.
name|getConstructor
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|Throwable
name|t
init|=
name|ctor
operator|.
name|newInstance
argument_list|(
name|msg
argument_list|)
decl_stmt|;
return|return
call|(
name|T
call|)
argument_list|(
name|t
operator|.
name|initCause
argument_list|(
name|exception
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
name|exception
throw|;
block|}
block|}
comment|/**    * Reads a DataInput until EOF and returns a byte array.  Make sure not to    * pass in an infinite DataInput or this will never return.    *    * @param in A DataInput    * @return a byte array containing the data from the DataInput    * @throws IOException on I/O error, other than EOF    */
DECL|method|readFullyToByteArray (DataInput in)
specifier|public
specifier|static
name|byte
index|[]
name|readFullyToByteArray
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|baos
operator|.
name|write
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|EOFException
name|eof
parameter_list|)
block|{
comment|// finished reading, do nothing
block|}
return|return
name|baos
operator|.
name|toByteArray
argument_list|()
return|;
block|}
block|}
end_class

end_unit

