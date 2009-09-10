begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.compress
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|compress
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
name|fs
operator|.
name|PositionedReadable
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
name|Seekable
import|;
end_import

begin_comment
comment|/**  * A compression input stream.  *  *<p>Implementations are assumed to be buffered.  This permits clients to  * reposition the underlying input stream then call {@link #resetState()},  * without having to also synchronize client buffers.  */
end_comment

begin_class
DECL|class|CompressionInputStream
specifier|public
specifier|abstract
class|class
name|CompressionInputStream
extends|extends
name|InputStream
implements|implements
name|Seekable
block|{
comment|/**    * The input stream to be compressed.     */
DECL|field|in
specifier|protected
specifier|final
name|InputStream
name|in
decl_stmt|;
DECL|field|maxAvailableData
specifier|protected
name|long
name|maxAvailableData
init|=
literal|0L
decl_stmt|;
comment|/**    * Create a compression input stream that reads    * the decompressed bytes from the given stream.    *     * @param in The input stream to be compressed.    * @throws IOException    */
DECL|method|CompressionInputStream (InputStream in)
specifier|protected
name|CompressionInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
operator|(
name|in
operator|instanceof
name|Seekable
operator|)
operator|||
operator|!
operator|(
name|in
operator|instanceof
name|PositionedReadable
operator|)
condition|)
block|{
name|this
operator|.
name|maxAvailableData
operator|=
name|in
operator|.
name|available
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
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
comment|/**    * Read bytes from the stream.    * Made abstract to prevent leakage to underlying stream.    */
DECL|method|read (byte[] b, int off, int len)
specifier|public
specifier|abstract
name|int
name|read
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
function_decl|;
comment|/**    * Reset the decompressor to its initial state and discard any buffered data,    * as the underlying stream may have been repositioned.    */
DECL|method|resetState ()
specifier|public
specifier|abstract
name|void
name|resetState
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * This method returns the current position in the stream.    *    * @return Current position in stream as a long    */
DECL|method|getPos ()
specifier|public
name|long
name|getPos
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
operator|(
name|in
operator|instanceof
name|Seekable
operator|)
operator|||
operator|!
operator|(
name|in
operator|instanceof
name|PositionedReadable
operator|)
condition|)
block|{
comment|//This way of getting the current position will not work for file
comment|//size which can be fit in an int and hence can not be returned by
comment|//available method.
return|return
operator|(
name|this
operator|.
name|maxAvailableData
operator|-
name|this
operator|.
name|in
operator|.
name|available
argument_list|()
operator|)
return|;
block|}
else|else
block|{
return|return
operator|(
operator|(
name|Seekable
operator|)
name|this
operator|.
name|in
operator|)
operator|.
name|getPos
argument_list|()
return|;
block|}
block|}
comment|/**    * This method is current not supported.    *    * @throws UnsupportedOperationException    */
DECL|method|seek (long pos)
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|UnsupportedOperationException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**    * This method is current not supported.    *    * @throws UnsupportedOperationException    */
DECL|method|seekToNewSource (long targetPos)
specifier|public
name|boolean
name|seekToNewSource
parameter_list|(
name|long
name|targetPos
parameter_list|)
throws|throws
name|UnsupportedOperationException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

