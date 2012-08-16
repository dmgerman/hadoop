begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|io
operator|.
name|Text
import|;
end_import

begin_comment
comment|/**  * A class that provides a line reader from an input stream.  * Depending on the constructor used, lines will either be terminated by:  *<ul>  *<li>one of the following: '\n' (LF) , '\r' (CR),  * or '\r\n' (CR+LF).</li>  *<li><em>or</em>, a custom byte sequence delimiter</li>  *</ul>  * In both cases, EOF also terminates an otherwise unterminated  * line.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|LineReader
specifier|public
class|class
name|LineReader
block|{
DECL|field|DEFAULT_BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_BUFFER_SIZE
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
DECL|field|bufferSize
specifier|private
name|int
name|bufferSize
init|=
name|DEFAULT_BUFFER_SIZE
decl_stmt|;
DECL|field|in
specifier|private
name|InputStream
name|in
decl_stmt|;
DECL|field|buffer
specifier|private
name|byte
index|[]
name|buffer
decl_stmt|;
comment|// the number of bytes of real data in the buffer
DECL|field|bufferLength
specifier|private
name|int
name|bufferLength
init|=
literal|0
decl_stmt|;
comment|// the current position in the buffer
DECL|field|bufferPosn
specifier|private
name|int
name|bufferPosn
init|=
literal|0
decl_stmt|;
DECL|field|CR
specifier|private
specifier|static
specifier|final
name|byte
name|CR
init|=
literal|'\r'
decl_stmt|;
DECL|field|LF
specifier|private
specifier|static
specifier|final
name|byte
name|LF
init|=
literal|'\n'
decl_stmt|;
comment|// The line delimiter
DECL|field|recordDelimiterBytes
specifier|private
specifier|final
name|byte
index|[]
name|recordDelimiterBytes
decl_stmt|;
comment|/**    * Create a line reader that reads from the given stream using the    * default buffer-size (64k).    * @param in The input stream    * @throws IOException    */
DECL|method|LineReader (InputStream in)
specifier|public
name|LineReader
parameter_list|(
name|InputStream
name|in
parameter_list|)
block|{
name|this
argument_list|(
name|in
argument_list|,
name|DEFAULT_BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a line reader that reads from the given stream using the     * given buffer-size.    * @param in The input stream    * @param bufferSize Size of the read buffer    * @throws IOException    */
DECL|method|LineReader (InputStream in, int bufferSize)
specifier|public
name|LineReader
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|int
name|bufferSize
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|bufferSize
operator|=
name|bufferSize
expr_stmt|;
name|this
operator|.
name|buffer
operator|=
operator|new
name|byte
index|[
name|this
operator|.
name|bufferSize
index|]
expr_stmt|;
name|this
operator|.
name|recordDelimiterBytes
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Create a line reader that reads from the given stream using the    *<code>io.file.buffer.size</code> specified in the given    *<code>Configuration</code>.    * @param in input stream    * @param conf configuration    * @throws IOException    */
DECL|method|LineReader (InputStream in, Configuration conf)
specifier|public
name|LineReader
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|in
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
literal|"io.file.buffer.size"
argument_list|,
name|DEFAULT_BUFFER_SIZE
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a line reader that reads from the given stream using the    * default buffer-size, and using a custom delimiter of array of    * bytes.    * @param in The input stream    * @param recordDelimiterBytes The delimiter    */
DECL|method|LineReader (InputStream in, byte[] recordDelimiterBytes)
specifier|public
name|LineReader
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|byte
index|[]
name|recordDelimiterBytes
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|bufferSize
operator|=
name|DEFAULT_BUFFER_SIZE
expr_stmt|;
name|this
operator|.
name|buffer
operator|=
operator|new
name|byte
index|[
name|this
operator|.
name|bufferSize
index|]
expr_stmt|;
name|this
operator|.
name|recordDelimiterBytes
operator|=
name|recordDelimiterBytes
expr_stmt|;
block|}
comment|/**    * Create a line reader that reads from the given stream using the    * given buffer-size, and using a custom delimiter of array of    * bytes.    * @param in The input stream    * @param bufferSize Size of the read buffer    * @param recordDelimiterBytes The delimiter    * @throws IOException    */
DECL|method|LineReader (InputStream in, int bufferSize, byte[] recordDelimiterBytes)
specifier|public
name|LineReader
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|byte
index|[]
name|recordDelimiterBytes
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|bufferSize
operator|=
name|bufferSize
expr_stmt|;
name|this
operator|.
name|buffer
operator|=
operator|new
name|byte
index|[
name|this
operator|.
name|bufferSize
index|]
expr_stmt|;
name|this
operator|.
name|recordDelimiterBytes
operator|=
name|recordDelimiterBytes
expr_stmt|;
block|}
comment|/**    * Create a line reader that reads from the given stream using the    *<code>io.file.buffer.size</code> specified in the given    *<code>Configuration</code>, and using a custom delimiter of array of    * bytes.    * @param in input stream    * @param conf configuration    * @param recordDelimiterBytes The delimiter    * @throws IOException    */
DECL|method|LineReader (InputStream in, Configuration conf, byte[] recordDelimiterBytes)
specifier|public
name|LineReader
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|byte
index|[]
name|recordDelimiterBytes
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|bufferSize
operator|=
name|conf
operator|.
name|getInt
argument_list|(
literal|"io.file.buffer.size"
argument_list|,
name|DEFAULT_BUFFER_SIZE
argument_list|)
expr_stmt|;
name|this
operator|.
name|buffer
operator|=
operator|new
name|byte
index|[
name|this
operator|.
name|bufferSize
index|]
expr_stmt|;
name|this
operator|.
name|recordDelimiterBytes
operator|=
name|recordDelimiterBytes
expr_stmt|;
block|}
comment|/**    * Close the underlying stream.    * @throws IOException    */
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Read one line from the InputStream into the given Text.    *    * @param str the object to store the given line (without newline)    * @param maxLineLength the maximum number of bytes to store into str;    *  the rest of the line is silently discarded.    * @param maxBytesToConsume the maximum number of bytes to consume    *  in this call.  This is only a hint, because if the line cross    *  this threshold, we allow it to happen.  It can overshoot    *  potentially by as much as one buffer length.    *    * @return the number of bytes read including the (longest) newline    * found.    *    * @throws IOException if the underlying stream throws    */
DECL|method|readLine (Text str, int maxLineLength, int maxBytesToConsume)
specifier|public
name|int
name|readLine
parameter_list|(
name|Text
name|str
parameter_list|,
name|int
name|maxLineLength
parameter_list|,
name|int
name|maxBytesToConsume
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|recordDelimiterBytes
operator|!=
literal|null
condition|)
block|{
return|return
name|readCustomLine
argument_list|(
name|str
argument_list|,
name|maxLineLength
argument_list|,
name|maxBytesToConsume
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|readDefaultLine
argument_list|(
name|str
argument_list|,
name|maxLineLength
argument_list|,
name|maxBytesToConsume
argument_list|)
return|;
block|}
block|}
comment|/**    * Read a line terminated by one of CR, LF, or CRLF.    */
DECL|method|readDefaultLine (Text str, int maxLineLength, int maxBytesToConsume)
specifier|private
name|int
name|readDefaultLine
parameter_list|(
name|Text
name|str
parameter_list|,
name|int
name|maxLineLength
parameter_list|,
name|int
name|maxBytesToConsume
parameter_list|)
throws|throws
name|IOException
block|{
comment|/* We're reading data from in, but the head of the stream may be      * already buffered in buffer, so we have several cases:      * 1. No newline characters are in the buffer, so we need to copy      *    everything and read another buffer from the stream.      * 2. An unambiguously terminated line is in buffer, so we just      *    copy to str.      * 3. Ambiguously terminated line is in buffer, i.e. buffer ends      *    in CR.  In this case we copy everything up to CR to str, but      *    we also need to see what follows CR: if it's LF, then we      *    need consume LF as well, so next call to readLine will read      *    from after that.      * We use a flag prevCharCR to signal if previous character was CR      * and, if it happens to be at the end of the buffer, delay      * consuming it until we have a chance to look at the char that      * follows.      */
name|str
operator|.
name|clear
argument_list|()
expr_stmt|;
name|int
name|txtLength
init|=
literal|0
decl_stmt|;
comment|//tracks str.getLength(), as an optimization
name|int
name|newlineLength
init|=
literal|0
decl_stmt|;
comment|//length of terminating newline
name|boolean
name|prevCharCR
init|=
literal|false
decl_stmt|;
comment|//true of prev char was CR
name|long
name|bytesConsumed
init|=
literal|0
decl_stmt|;
do|do
block|{
name|int
name|startPosn
init|=
name|bufferPosn
decl_stmt|;
comment|//starting from where we left off the last time
if|if
condition|(
name|bufferPosn
operator|>=
name|bufferLength
condition|)
block|{
name|startPosn
operator|=
name|bufferPosn
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|prevCharCR
condition|)
operator|++
name|bytesConsumed
expr_stmt|;
comment|//account for CR from previous read
name|bufferLength
operator|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
if|if
condition|(
name|bufferLength
operator|<=
literal|0
condition|)
break|break;
comment|// EOF
block|}
for|for
control|(
init|;
name|bufferPosn
operator|<
name|bufferLength
condition|;
operator|++
name|bufferPosn
control|)
block|{
comment|//search for newline
if|if
condition|(
name|buffer
index|[
name|bufferPosn
index|]
operator|==
name|LF
condition|)
block|{
name|newlineLength
operator|=
operator|(
name|prevCharCR
operator|)
condition|?
literal|2
else|:
literal|1
expr_stmt|;
operator|++
name|bufferPosn
expr_stmt|;
comment|// at next invocation proceed from following byte
break|break;
block|}
if|if
condition|(
name|prevCharCR
condition|)
block|{
comment|//CR + notLF, we are at notLF
name|newlineLength
operator|=
literal|1
expr_stmt|;
break|break;
block|}
name|prevCharCR
operator|=
operator|(
name|buffer
index|[
name|bufferPosn
index|]
operator|==
name|CR
operator|)
expr_stmt|;
block|}
name|int
name|readLength
init|=
name|bufferPosn
operator|-
name|startPosn
decl_stmt|;
if|if
condition|(
name|prevCharCR
operator|&&
name|newlineLength
operator|==
literal|0
condition|)
operator|--
name|readLength
expr_stmt|;
comment|//CR at the end of the buffer
name|bytesConsumed
operator|+=
name|readLength
expr_stmt|;
name|int
name|appendLength
init|=
name|readLength
operator|-
name|newlineLength
decl_stmt|;
if|if
condition|(
name|appendLength
operator|>
name|maxLineLength
operator|-
name|txtLength
condition|)
block|{
name|appendLength
operator|=
name|maxLineLength
operator|-
name|txtLength
expr_stmt|;
block|}
if|if
condition|(
name|appendLength
operator|>
literal|0
condition|)
block|{
name|str
operator|.
name|append
argument_list|(
name|buffer
argument_list|,
name|startPosn
argument_list|,
name|appendLength
argument_list|)
expr_stmt|;
name|txtLength
operator|+=
name|appendLength
expr_stmt|;
block|}
block|}
do|while
condition|(
name|newlineLength
operator|==
literal|0
operator|&&
name|bytesConsumed
operator|<
name|maxBytesToConsume
condition|)
do|;
if|if
condition|(
name|bytesConsumed
operator|>
operator|(
name|long
operator|)
name|Integer
operator|.
name|MAX_VALUE
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Too many bytes before newline: "
operator|+
name|bytesConsumed
argument_list|)
throw|;
return|return
operator|(
name|int
operator|)
name|bytesConsumed
return|;
block|}
comment|/**    * Read a line terminated by a custom delimiter.    */
DECL|method|readCustomLine (Text str, int maxLineLength, int maxBytesToConsume)
specifier|private
name|int
name|readCustomLine
parameter_list|(
name|Text
name|str
parameter_list|,
name|int
name|maxLineLength
parameter_list|,
name|int
name|maxBytesToConsume
parameter_list|)
throws|throws
name|IOException
block|{
name|str
operator|.
name|clear
argument_list|()
expr_stmt|;
name|int
name|txtLength
init|=
literal|0
decl_stmt|;
comment|// tracks str.getLength(), as an optimization
name|long
name|bytesConsumed
init|=
literal|0
decl_stmt|;
name|int
name|delPosn
init|=
literal|0
decl_stmt|;
do|do
block|{
name|int
name|startPosn
init|=
name|bufferPosn
decl_stmt|;
comment|// starting from where we left off the last
comment|// time
if|if
condition|(
name|bufferPosn
operator|>=
name|bufferLength
condition|)
block|{
name|startPosn
operator|=
name|bufferPosn
operator|=
literal|0
expr_stmt|;
name|bufferLength
operator|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
if|if
condition|(
name|bufferLength
operator|<=
literal|0
condition|)
break|break;
comment|// EOF
block|}
for|for
control|(
init|;
name|bufferPosn
operator|<
name|bufferLength
condition|;
operator|++
name|bufferPosn
control|)
block|{
if|if
condition|(
name|buffer
index|[
name|bufferPosn
index|]
operator|==
name|recordDelimiterBytes
index|[
name|delPosn
index|]
condition|)
block|{
name|delPosn
operator|++
expr_stmt|;
if|if
condition|(
name|delPosn
operator|>=
name|recordDelimiterBytes
operator|.
name|length
condition|)
block|{
name|bufferPosn
operator|++
expr_stmt|;
break|break;
block|}
block|}
elseif|else
if|if
condition|(
name|delPosn
operator|!=
literal|0
condition|)
block|{
name|bufferPosn
operator|--
expr_stmt|;
comment|// recheck if bufferPosn matches start of delimiter
name|delPosn
operator|=
literal|0
expr_stmt|;
block|}
block|}
name|int
name|readLength
init|=
name|bufferPosn
operator|-
name|startPosn
decl_stmt|;
name|bytesConsumed
operator|+=
name|readLength
expr_stmt|;
name|int
name|appendLength
init|=
name|readLength
operator|-
name|delPosn
decl_stmt|;
if|if
condition|(
name|appendLength
operator|>
name|maxLineLength
operator|-
name|txtLength
condition|)
block|{
name|appendLength
operator|=
name|maxLineLength
operator|-
name|txtLength
expr_stmt|;
block|}
if|if
condition|(
name|appendLength
operator|>
literal|0
condition|)
block|{
name|str
operator|.
name|append
argument_list|(
name|buffer
argument_list|,
name|startPosn
argument_list|,
name|appendLength
argument_list|)
expr_stmt|;
name|txtLength
operator|+=
name|appendLength
expr_stmt|;
block|}
block|}
do|while
condition|(
name|delPosn
operator|<
name|recordDelimiterBytes
operator|.
name|length
operator|&&
name|bytesConsumed
operator|<
name|maxBytesToConsume
condition|)
do|;
if|if
condition|(
name|bytesConsumed
operator|>
operator|(
name|long
operator|)
name|Integer
operator|.
name|MAX_VALUE
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Too many bytes before delimiter: "
operator|+
name|bytesConsumed
argument_list|)
throw|;
return|return
operator|(
name|int
operator|)
name|bytesConsumed
return|;
block|}
comment|/**    * Read from the InputStream into the given Text.    * @param str the object to store the given line    * @param maxLineLength the maximum number of bytes to store into str.    * @return the number of bytes read including the newline    * @throws IOException if the underlying stream throws    */
DECL|method|readLine (Text str, int maxLineLength)
specifier|public
name|int
name|readLine
parameter_list|(
name|Text
name|str
parameter_list|,
name|int
name|maxLineLength
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readLine
argument_list|(
name|str
argument_list|,
name|maxLineLength
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
comment|/**    * Read from the InputStream into the given Text.    * @param str the object to store the given line    * @return the number of bytes read including the newline    * @throws IOException if the underlying stream throws    */
DECL|method|readLine (Text str)
specifier|public
name|int
name|readLine
parameter_list|(
name|Text
name|str
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readLine
argument_list|(
name|str
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
block|}
end_class

end_unit

