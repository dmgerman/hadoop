begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.crypto
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|crypto
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

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|Decryptor
specifier|public
interface|interface
name|Decryptor
block|{
comment|/**    * Initialize the decryptor, the internal decryption context will be     * reset.    * @param key decryption key.    * @param iv decryption initialization vector    * @throws IOException if initialization fails    */
DECL|method|init (byte[] key, byte[] iv)
specifier|public
name|void
name|init
parameter_list|(
name|byte
index|[]
name|key
parameter_list|,
name|byte
index|[]
name|iv
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Indicate whether decryption context is reset.    *<p/>    * It's useful for some mode like CTR which requires different IV for     * different parts of data. Usually decryptor can maintain the context     * internally such as calculating IV/counter, then continue a multiple-part     * decryption operation without reinit the decryptor using key and the new     * IV. For mode like CTR, if context is reset after each decryption, the     * decryptor should be reinit before each operation, that's not efficient.     * @return boolean whether context is reset.    */
DECL|method|isContextReset ()
specifier|public
name|boolean
name|isContextReset
parameter_list|()
function_decl|;
comment|/**    * This exposes a direct interface for record decryption with direct byte    * buffers.    *<p/>    * The decrypt() function need not always consume the buffers provided,    * it will need to be called multiple times to decrypt an entire buffer     * and the object will hold the decryption context internally.    *<p/>    * Some implementation may need enough space in the destination buffer to     * decrypt an entire input.    *<p/>    * The end result will move inBuffer.position() by the bytes-read and    * outBuffer.position() by the bytes-written. It should not modify the     * inBuffer.limit() or outBuffer.limit() to maintain consistency of operation.    *<p/>    * @param inBuffer in direct {@link ByteBuffer} for reading from. Requires     * inBuffer != null and inBuffer.remaining()> 0    * @param outBuffer out direct {@link ByteBuffer} for storing the results    * into. Requires outBuffer != null and outBuffer.remaining()> 0    * @throws IOException if decryption fails    */
DECL|method|decrypt (ByteBuffer inBuffer, ByteBuffer outBuffer)
specifier|public
name|void
name|decrypt
parameter_list|(
name|ByteBuffer
name|inBuffer
parameter_list|,
name|ByteBuffer
name|outBuffer
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

