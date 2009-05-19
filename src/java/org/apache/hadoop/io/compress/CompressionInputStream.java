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
block|{
comment|/**    * The input stream to be compressed.     */
DECL|field|in
specifier|protected
specifier|final
name|InputStream
name|in
decl_stmt|;
comment|/**    * Create a compression input stream that reads    * the decompressed bytes from the given stream.    *     * @param in The input stream to be compressed.    */
DECL|method|CompressionInputStream (InputStream in)
specifier|protected
name|CompressionInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|)
block|{
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
block|}
end_class

end_unit

