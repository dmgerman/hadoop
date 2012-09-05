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

begin_comment
comment|/** A reusable {@link OutputStream} implementation that writes to an in-memory  * buffer.  *  *<p>This saves memory over creating a new OutputStream and  * ByteArrayOutputStream each time data is written.  *  *<p>Typical usage is something like the following:<pre>  *  * OutputBuffer buffer = new OutputBuffer();  * while (... loop condition ...) {  *   buffer.reset();  *   ... write buffer using OutputStream methods ...  *   byte[] data = buffer.getData();  *   int dataLength = buffer.getLength();  *   ... write data to its ultimate destination ...  * }  *</pre>  * @see DataOutputBuffer  * @see InputBuffer  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|OutputBuffer
specifier|public
class|class
name|OutputBuffer
extends|extends
name|FilterOutputStream
block|{
DECL|class|Buffer
specifier|private
specifier|static
class|class
name|Buffer
extends|extends
name|ByteArrayOutputStream
block|{
DECL|method|getData ()
specifier|public
name|byte
index|[]
name|getData
parameter_list|()
block|{
return|return
name|buf
return|;
block|}
DECL|method|getLength ()
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|count
return|;
block|}
annotation|@
name|Override
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|count
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|write (InputStream in, int len)
specifier|public
name|void
name|write
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|newcount
init|=
name|count
operator|+
name|len
decl_stmt|;
if|if
condition|(
name|newcount
operator|>
name|buf
operator|.
name|length
condition|)
block|{
name|byte
name|newbuf
index|[]
init|=
operator|new
name|byte
index|[
name|Math
operator|.
name|max
argument_list|(
name|buf
operator|.
name|length
operator|<<
literal|1
argument_list|,
name|newcount
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|newbuf
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|buf
operator|=
name|newbuf
expr_stmt|;
block|}
name|IOUtils
operator|.
name|readFully
argument_list|(
name|in
argument_list|,
name|buf
argument_list|,
name|count
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|count
operator|=
name|newcount
expr_stmt|;
block|}
block|}
DECL|field|buffer
specifier|private
name|Buffer
name|buffer
decl_stmt|;
comment|/** Constructs a new empty buffer. */
DECL|method|OutputBuffer ()
specifier|public
name|OutputBuffer
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|Buffer
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|OutputBuffer (Buffer buffer)
specifier|private
name|OutputBuffer
parameter_list|(
name|Buffer
name|buffer
parameter_list|)
block|{
name|super
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|this
operator|.
name|buffer
operator|=
name|buffer
expr_stmt|;
block|}
comment|/** Returns the current contents of the buffer.    *  Data is only valid to {@link #getLength()}.    */
DECL|method|getData ()
specifier|public
name|byte
index|[]
name|getData
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|getData
argument_list|()
return|;
block|}
comment|/** Returns the length of the valid data currently in the buffer. */
DECL|method|getLength ()
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|getLength
argument_list|()
return|;
block|}
comment|/** Resets the buffer to empty. */
DECL|method|reset ()
specifier|public
name|OutputBuffer
name|reset
parameter_list|()
block|{
name|buffer
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Writes bytes from a InputStream directly into the buffer. */
DECL|method|write (InputStream in, int length)
specifier|public
name|void
name|write
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|buffer
operator|.
name|write
argument_list|(
name|in
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

