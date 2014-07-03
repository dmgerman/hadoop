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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|BadPaddingException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|IllegalBlockSizeException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|NoSuchPaddingException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|ShortBufferException
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
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|util
operator|.
name|NativeCodeLoader
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/**  * OpenSSL cipher using JNI.  * Currently only AES-CTR is supported. It's flexible to add   * other crypto algorithms/modes.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|OpensslCipher
specifier|public
specifier|final
class|class
name|OpensslCipher
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|OpensslCipher
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|ENCRYPT_MODE
specifier|public
specifier|static
specifier|final
name|int
name|ENCRYPT_MODE
init|=
literal|1
decl_stmt|;
DECL|field|DECRYPT_MODE
specifier|public
specifier|static
specifier|final
name|int
name|DECRYPT_MODE
init|=
literal|0
decl_stmt|;
comment|/** Currently only support AES/CTR/NoPadding. */
DECL|field|AES_CTR
specifier|public
specifier|static
specifier|final
name|int
name|AES_CTR
init|=
literal|0
decl_stmt|;
DECL|field|PADDING_NOPADDING
specifier|public
specifier|static
specifier|final
name|int
name|PADDING_NOPADDING
init|=
literal|0
decl_stmt|;
DECL|field|context
specifier|private
name|long
name|context
init|=
literal|0
decl_stmt|;
DECL|field|algorithm
specifier|private
specifier|final
name|int
name|algorithm
decl_stmt|;
DECL|field|padding
specifier|private
specifier|final
name|int
name|padding
decl_stmt|;
DECL|field|nativeCipherLoaded
specifier|private
specifier|static
name|boolean
name|nativeCipherLoaded
init|=
literal|false
decl_stmt|;
static|static
block|{
if|if
condition|(
name|NativeCodeLoader
operator|.
name|isNativeCodeLoaded
argument_list|()
operator|&&
name|NativeCodeLoader
operator|.
name|buildSupportsOpenssl
argument_list|()
condition|)
block|{
try|try
block|{
name|initIDs
argument_list|()
expr_stmt|;
name|nativeCipherLoaded
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to load OpenSSL Cipher."
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|isNativeCodeLoaded ()
specifier|public
specifier|static
name|boolean
name|isNativeCodeLoaded
parameter_list|()
block|{
return|return
name|nativeCipherLoaded
return|;
block|}
DECL|method|OpensslCipher (long context, int algorithm, int padding)
specifier|private
name|OpensslCipher
parameter_list|(
name|long
name|context
parameter_list|,
name|int
name|algorithm
parameter_list|,
name|int
name|padding
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|algorithm
operator|=
name|algorithm
expr_stmt|;
name|this
operator|.
name|padding
operator|=
name|padding
expr_stmt|;
block|}
comment|/**    * Return an<code>OpensslCipher<code> object that implements the specified    * algorithm.    *     * @param algorithm currently only supports {@link #AES_CTR}    * @param padding currently only supports {@link #PADDING_NOPADDING}    * @return OpensslCipher an<code>OpensslCipher<code> object     * @throws NoSuchAlgorithmException    * @throws NoSuchPaddingException    */
DECL|method|getInstance (int algorithm, int padding)
specifier|public
specifier|static
specifier|final
name|OpensslCipher
name|getInstance
parameter_list|(
name|int
name|algorithm
parameter_list|,
name|int
name|padding
parameter_list|)
throws|throws
name|NoSuchAlgorithmException
throws|,
name|NoSuchPaddingException
block|{
name|long
name|context
init|=
name|initContext
argument_list|(
name|algorithm
argument_list|,
name|padding
argument_list|)
decl_stmt|;
return|return
operator|new
name|OpensslCipher
argument_list|(
name|context
argument_list|,
name|algorithm
argument_list|,
name|padding
argument_list|)
return|;
block|}
comment|/**    * Initialize this cipher with a key and IV.    *     * @param mode {@link #ENCRYPT_MODE} or {@link #DECRYPT_MODE}    * @param key crypto key    * @param iv crypto iv    */
DECL|method|init (int mode, byte[] key, byte[] iv)
specifier|public
name|void
name|init
parameter_list|(
name|int
name|mode
parameter_list|,
name|byte
index|[]
name|key
parameter_list|,
name|byte
index|[]
name|iv
parameter_list|)
block|{
name|context
operator|=
name|init
argument_list|(
name|context
argument_list|,
name|mode
argument_list|,
name|algorithm
argument_list|,
name|padding
argument_list|,
name|key
argument_list|,
name|iv
argument_list|)
expr_stmt|;
block|}
comment|/**    * Continues a multiple-part encryption or decryption operation. The data    * is encrypted or decrypted, depending on how this cipher was initialized.    *<p/>    *     * All<code>input.remaining()</code> bytes starting at     *<code>input.position()</code> are processed. The result is stored in    * the output buffer.    *<p/>    *     * Upon return, the input buffer's position will be equal to its limit;    * its limit will not have changed. The output buffer's position will have    * advanced by n, when n is the value returned by this method; the output    * buffer's limit will not have changed.    *<p/>    *     * If<code>output.remaining()</code> bytes are insufficient to hold the    * result, a<code>ShortBufferException</code> is thrown.    *     * @param input the input ByteBuffer    * @param output the output ByteBuffer    * @return int number of bytes stored in<code>output</code>    * @throws ShortBufferException if there is insufficient space in the    * output buffer    */
DECL|method|update (ByteBuffer input, ByteBuffer output)
specifier|public
name|int
name|update
parameter_list|(
name|ByteBuffer
name|input
parameter_list|,
name|ByteBuffer
name|output
parameter_list|)
throws|throws
name|ShortBufferException
block|{
name|checkState
argument_list|()
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|input
operator|.
name|isDirect
argument_list|()
operator|&&
name|output
operator|.
name|isDirect
argument_list|()
argument_list|,
literal|"Direct buffers are required."
argument_list|)
expr_stmt|;
name|int
name|len
init|=
name|update
argument_list|(
name|context
argument_list|,
name|input
argument_list|,
name|input
operator|.
name|position
argument_list|()
argument_list|,
name|input
operator|.
name|remaining
argument_list|()
argument_list|,
name|output
argument_list|,
name|output
operator|.
name|position
argument_list|()
argument_list|,
name|output
operator|.
name|remaining
argument_list|()
argument_list|)
decl_stmt|;
name|input
operator|.
name|position
argument_list|(
name|input
operator|.
name|limit
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|position
argument_list|(
name|output
operator|.
name|position
argument_list|()
operator|+
name|len
argument_list|)
expr_stmt|;
return|return
name|len
return|;
block|}
comment|/**    * Finishes a multiple-part operation. The data is encrypted or decrypted,    * depending on how this cipher was initialized.    *<p/>    *     * The result is stored in the output buffer. Upon return, the output buffer's    * position will have advanced by n, where n is the value returned by this    * method; the output buffer's limit will not have changed.    *<p/>    *     * If<code>output.remaining()</code> bytes are insufficient to hold the result,    * a<code>ShortBufferException</code> is thrown.    *<p/>    *     * Upon finishing, this method resets this cipher object to the state it was    * in when previously initialized. That is, the object is available to encrypt    * or decrypt more data.    *<p/>    *     * If any exception is thrown, this cipher object need to be reset before it     * can be used again.    *     * @param output the output ByteBuffer    * @return int number of bytes stored in<code>output</code>    * @throws ShortBufferException    * @throws IllegalBlockSizeException    * @throws BadPaddingException    */
DECL|method|doFinal (ByteBuffer output)
specifier|public
name|int
name|doFinal
parameter_list|(
name|ByteBuffer
name|output
parameter_list|)
throws|throws
name|ShortBufferException
throws|,
name|IllegalBlockSizeException
throws|,
name|BadPaddingException
block|{
name|checkState
argument_list|()
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|output
operator|.
name|isDirect
argument_list|()
argument_list|,
literal|"Direct buffer is required."
argument_list|)
expr_stmt|;
name|int
name|len
init|=
name|doFinal
argument_list|(
name|context
argument_list|,
name|output
argument_list|,
name|output
operator|.
name|position
argument_list|()
argument_list|,
name|output
operator|.
name|remaining
argument_list|()
argument_list|)
decl_stmt|;
name|output
operator|.
name|position
argument_list|(
name|output
operator|.
name|position
argument_list|()
operator|+
name|len
argument_list|)
expr_stmt|;
return|return
name|len
return|;
block|}
comment|/** Forcibly clean the context. */
DECL|method|clean ()
specifier|public
name|void
name|clean
parameter_list|()
block|{
if|if
condition|(
name|context
operator|!=
literal|0
condition|)
block|{
name|clean
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|context
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|/** Check whether context is initialized. */
DECL|method|checkState ()
specifier|private
name|void
name|checkState
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|context
operator|!=
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finalize ()
specifier|protected
name|void
name|finalize
parameter_list|()
throws|throws
name|Throwable
block|{
name|clean
argument_list|()
expr_stmt|;
block|}
DECL|method|initIDs ()
specifier|private
specifier|native
specifier|static
name|void
name|initIDs
parameter_list|()
function_decl|;
DECL|method|initContext (int alg, int padding)
specifier|private
specifier|native
specifier|static
name|long
name|initContext
parameter_list|(
name|int
name|alg
parameter_list|,
name|int
name|padding
parameter_list|)
function_decl|;
DECL|method|init (long context, int mode, int alg, int padding, byte[] key, byte[] iv)
specifier|private
specifier|native
name|long
name|init
parameter_list|(
name|long
name|context
parameter_list|,
name|int
name|mode
parameter_list|,
name|int
name|alg
parameter_list|,
name|int
name|padding
parameter_list|,
name|byte
index|[]
name|key
parameter_list|,
name|byte
index|[]
name|iv
parameter_list|)
function_decl|;
DECL|method|update (long context, ByteBuffer input, int inputOffset, int inputLength, ByteBuffer output, int outputOffset, int maxOutputLength)
specifier|private
specifier|native
name|int
name|update
parameter_list|(
name|long
name|context
parameter_list|,
name|ByteBuffer
name|input
parameter_list|,
name|int
name|inputOffset
parameter_list|,
name|int
name|inputLength
parameter_list|,
name|ByteBuffer
name|output
parameter_list|,
name|int
name|outputOffset
parameter_list|,
name|int
name|maxOutputLength
parameter_list|)
function_decl|;
DECL|method|doFinal (long context, ByteBuffer output, int offset, int maxOutputLength)
specifier|private
specifier|native
name|int
name|doFinal
parameter_list|(
name|long
name|context
parameter_list|,
name|ByteBuffer
name|output
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|maxOutputLength
parameter_list|)
function_decl|;
DECL|method|clean (long context)
specifier|private
specifier|native
name|void
name|clean
parameter_list|(
name|long
name|context
parameter_list|)
function_decl|;
DECL|method|getLibraryName ()
specifier|public
specifier|native
specifier|static
name|String
name|getLibraryName
parameter_list|()
function_decl|;
block|}
end_class

end_unit

