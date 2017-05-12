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
name|OutputStream
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
comment|/**  * A compression output stream.  */
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
DECL|class|CompressionOutputStream
specifier|public
specifier|abstract
class|class
name|CompressionOutputStream
extends|extends
name|OutputStream
block|{
comment|/**    * The output stream to be compressed.     */
DECL|field|out
specifier|protected
specifier|final
name|OutputStream
name|out
decl_stmt|;
comment|/**    * If non-null, this is the Compressor object that we should call    * CodecPool#returnCompressor on when this stream is closed.    */
DECL|field|trackedCompressor
specifier|private
name|Compressor
name|trackedCompressor
decl_stmt|;
comment|/**    * Create a compression output stream that writes    * the compressed bytes to the given stream.    * @param out    */
DECL|method|CompressionOutputStream (OutputStream out)
specifier|protected
name|CompressionOutputStream
parameter_list|(
name|OutputStream
name|out
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
block|}
DECL|method|setTrackedCompressor (Compressor compressor)
name|void
name|setTrackedCompressor
parameter_list|(
name|Compressor
name|compressor
parameter_list|)
block|{
name|trackedCompressor
operator|=
name|compressor
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|finish
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|trackedCompressor
operator|!=
literal|null
condition|)
block|{
name|CodecPool
operator|.
name|returnCompressor
argument_list|(
name|trackedCompressor
argument_list|)
expr_stmt|;
name|trackedCompressor
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/**    * Write compressed bytes to the stream.    * Made abstract to prevent leakage to underlying stream.    */
annotation|@
name|Override
DECL|method|write (byte[] b, int off, int len)
specifier|public
specifier|abstract
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
function_decl|;
comment|/**    * Finishes writing compressed data to the output stream     * without closing the underlying stream.    */
DECL|method|finish ()
specifier|public
specifier|abstract
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Reset the compression to the initial state.     * Does not reset the underlying stream.    */
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

