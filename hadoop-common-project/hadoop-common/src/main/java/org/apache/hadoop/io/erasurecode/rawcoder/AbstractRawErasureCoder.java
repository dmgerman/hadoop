begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.erasurecode.rawcoder
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|erasurecode
operator|.
name|rawcoder
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|HadoopIllegalArgumentException
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
name|Configured
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
name|util
operator|.
name|Arrays
import|;
end_import

begin_comment
comment|/**  * A common class of basic facilities to be shared by encoder and decoder  *  * It implements the {@link RawErasureCoder} interface.  */
end_comment

begin_class
DECL|class|AbstractRawErasureCoder
specifier|public
specifier|abstract
class|class
name|AbstractRawErasureCoder
extends|extends
name|Configured
implements|implements
name|RawErasureCoder
block|{
DECL|field|numDataUnits
specifier|private
specifier|final
name|int
name|numDataUnits
decl_stmt|;
DECL|field|numParityUnits
specifier|private
specifier|final
name|int
name|numParityUnits
decl_stmt|;
DECL|method|AbstractRawErasureCoder (int numDataUnits, int numParityUnits)
specifier|public
name|AbstractRawErasureCoder
parameter_list|(
name|int
name|numDataUnits
parameter_list|,
name|int
name|numParityUnits
parameter_list|)
block|{
name|this
operator|.
name|numDataUnits
operator|=
name|numDataUnits
expr_stmt|;
name|this
operator|.
name|numParityUnits
operator|=
name|numParityUnits
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumDataUnits ()
specifier|public
name|int
name|getNumDataUnits
parameter_list|()
block|{
return|return
name|numDataUnits
return|;
block|}
annotation|@
name|Override
DECL|method|getNumParityUnits ()
specifier|public
name|int
name|getNumParityUnits
parameter_list|()
block|{
return|return
name|numParityUnits
return|;
block|}
annotation|@
name|Override
DECL|method|preferDirectBuffer ()
specifier|public
name|boolean
name|preferDirectBuffer
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|release ()
specifier|public
name|void
name|release
parameter_list|()
block|{
comment|// Nothing to do by default
block|}
comment|/**    * Ensure a buffer filled with ZERO bytes from current readable/writable    * position.    * @param buffer a buffer ready to read / write certain size bytes    * @return the buffer itself, with ZERO bytes written, the position and limit    *         are not changed after the call    */
DECL|method|resetBuffer (ByteBuffer buffer)
specifier|protected
name|ByteBuffer
name|resetBuffer
parameter_list|(
name|ByteBuffer
name|buffer
parameter_list|)
block|{
name|int
name|pos
init|=
name|buffer
operator|.
name|position
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|pos
init|;
name|i
operator|<
name|buffer
operator|.
name|limit
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|buffer
operator|.
name|put
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|position
argument_list|(
name|pos
argument_list|)
expr_stmt|;
return|return
name|buffer
return|;
block|}
comment|/**    * Ensure the buffer (either input or output) ready to read or write with ZERO    * bytes fully in specified length of len.    * @param buffer bytes array buffer    * @return the buffer itself    */
DECL|method|resetBuffer (byte[] buffer, int offset, int len)
specifier|protected
name|byte
index|[]
name|resetBuffer
parameter_list|(
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|len
condition|;
operator|++
name|i
control|)
block|{
name|buffer
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
literal|0
expr_stmt|;
block|}
return|return
name|buffer
return|;
block|}
comment|/**    * Check and ensure the buffers are of the length specified by dataLen.    * @param buffers    * @param allowNull    * @param dataLen    */
DECL|method|ensureLength (ByteBuffer[] buffers, boolean allowNull, int dataLen)
specifier|protected
name|void
name|ensureLength
parameter_list|(
name|ByteBuffer
index|[]
name|buffers
parameter_list|,
name|boolean
name|allowNull
parameter_list|,
name|int
name|dataLen
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|buffers
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|buffers
index|[
name|i
index|]
operator|==
literal|null
operator|&&
operator|!
name|allowNull
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Invalid buffer found, not allowing null"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|buffers
index|[
name|i
index|]
operator|!=
literal|null
operator|&&
name|buffers
index|[
name|i
index|]
operator|.
name|remaining
argument_list|()
operator|!=
name|dataLen
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Invalid buffer, not of length "
operator|+
name|dataLen
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Check and ensure the buffers are of the length specified by dataLen.    * @param buffers    * @param allowNull    * @param dataLen    */
DECL|method|ensureLength (byte[][] buffers, boolean allowNull, int dataLen)
specifier|protected
name|void
name|ensureLength
parameter_list|(
name|byte
index|[]
index|[]
name|buffers
parameter_list|,
name|boolean
name|allowNull
parameter_list|,
name|int
name|dataLen
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|buffers
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|buffers
index|[
name|i
index|]
operator|==
literal|null
operator|&&
operator|!
name|allowNull
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Invalid buffer found, not allowing null"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|buffers
index|[
name|i
index|]
operator|!=
literal|null
operator|&&
name|buffers
index|[
name|i
index|]
operator|.
name|length
operator|!=
name|dataLen
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Invalid buffer not of length "
operator|+
name|dataLen
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

