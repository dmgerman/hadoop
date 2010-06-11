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
name|net
operator|.
name|Socket
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
comment|/**    * Copies from one stream to another.    * @param in InputStrem to read from    * @param out OutputStream to write to    * @param buffSize the size of the buffer     * @param close whether or not close the InputStream and     * OutputStream at the end. The streams are closed in the finally clause.      */
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
block|}
finally|finally
block|{
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
name|in
operator|.
name|close
argument_list|()
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
comment|/**    * Copies from one stream to another.<strong>closes the input and output streams     * at the end</strong>.    * @param in InputStrem to read from    * @param out OutputStream to write to    * @param conf the Configuration object     */
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
literal|"io.file.buffer.size"
argument_list|,
literal|4096
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Copies from one stream to another.    * @param in InputStrem to read from    * @param out OutputStream to write to    * @param conf the Configuration object    * @param close whether or not close the InputStream and     * OutputStream at the end. The streams are closed in the finally clause.    */
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
literal|"io.file.buffer.size"
argument_list|,
literal|4096
argument_list|)
argument_list|,
name|close
argument_list|)
expr_stmt|;
block|}
comment|/** Reads len bytes in a loop.    * @param in The InputStream to read from    * @param buf The buffer to fill    * @param off offset from the buffer    * @param len the length of bytes to read    * @throws IOException if it could not read requested number of bytes     * for any reason (including EOF)    */
DECL|method|readFully ( InputStream in, byte buf[], int off, int len )
specifier|public
specifier|static
name|void
name|readFully
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|byte
name|buf
index|[]
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
literal|"Premeture EOF from inputStream"
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
comment|/** Similar to readFully(). Skips bytes in a loop.    * @param in The InputStream to skip bytes from    * @param len number of bytes to skip.    * @throws IOException if it could not skip requested number of bytes     * for any reason (including EOF)    */
DECL|method|skipFully ( InputStream in, long len )
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
while|while
condition|(
name|len
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
name|len
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
literal|"Premeture EOF from inputStream"
argument_list|)
throw|;
block|}
name|len
operator|-=
name|ret
expr_stmt|;
block|}
block|}
comment|/**    * Close the Closeable objects and<b>ignore</b> any {@link IOException} or     * null pointers. Must only be used for cleanup in exception handlers.    * @param log the log to record problems to at debug level. Can be null.    * @param closeables the objects to close    */
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
name|IOException
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
comment|/**    * Closes the stream ignoring {@link IOException}.    * Must only be called in cleaning up from exception handlers.    * @param stream the Stream to close    */
DECL|method|closeStream ( java.io.Closeable stream )
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
name|cleanup
argument_list|(
literal|null
argument_list|,
name|stream
argument_list|)
expr_stmt|;
block|}
comment|/**    * Closes the socket ignoring {@link IOException}     * @param sock the Socket to close    */
DECL|method|closeSocket ( Socket sock )
specifier|public
specifier|static
name|void
name|closeSocket
parameter_list|(
name|Socket
name|sock
parameter_list|)
block|{
comment|// avoids try { close() } dance
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
block|{       }
block|}
block|}
comment|/** /dev/null of OutputStreams.    */
DECL|class|NullOutputStream
specifier|public
specifier|static
class|class
name|NullOutputStream
extends|extends
name|OutputStream
block|{
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
block|}
end_class

end_unit

