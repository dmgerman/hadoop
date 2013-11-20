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
name|nio
operator|.
name|ByteBuffer
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
comment|/**  * Specification of a direct ByteBuffer 'de-compressor'.   */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|DirectDecompressor
specifier|public
interface|interface
name|DirectDecompressor
block|{
comment|/*    * This exposes a direct interface for record decompression with direct byte    * buffers.    *     * The decompress() function need not always consume the buffers provided,    * it will need to be called multiple times to decompress an entire buffer     * and the object will hold the compression context internally.    *     * Codecs such as {@link SnappyCodec} may or may not support partial    * decompression of buffers and will need enough space in the destination    * buffer to decompress an entire block.    *     * The operation is modelled around dst.put(src);    *     * The end result will move src.position() by the bytes-read and    * dst.position() by the bytes-written. It should not modify the src.limit()    * or dst.limit() to maintain consistency of operation between codecs.    *     * @param src Source direct {@link ByteBuffer} for reading from. Requires src    * != null and src.remaining()> 0    *     * @param dst Destination direct {@link ByteBuffer} for storing the results    * into. Requires dst != null and dst.remaining() to be> 0    *     * @throws IOException if compression fails    */
DECL|method|decompress (ByteBuffer src, ByteBuffer dst)
specifier|public
name|void
name|decompress
parameter_list|(
name|ByteBuffer
name|src
parameter_list|,
name|ByteBuffer
name|dst
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

