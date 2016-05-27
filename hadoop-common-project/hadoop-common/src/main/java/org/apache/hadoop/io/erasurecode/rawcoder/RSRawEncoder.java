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
name|io
operator|.
name|erasurecode
operator|.
name|ErasureCoderOptions
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
name|erasurecode
operator|.
name|rawcoder
operator|.
name|util
operator|.
name|DumpUtil
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
name|erasurecode
operator|.
name|rawcoder
operator|.
name|util
operator|.
name|RSUtil
import|;
end_import

begin_comment
comment|/**  * A raw erasure encoder in RS code scheme in pure Java in case native one  * isn't available in some environment. Please always use native implementations  * when possible. This new Java coder is about 5X faster than the one originated  * from HDFS-RAID, and also compatible with the native/ISA-L coder.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|RSRawEncoder
specifier|public
class|class
name|RSRawEncoder
extends|extends
name|RawErasureEncoder
block|{
comment|// relevant to schema and won't change during encode calls.
DECL|field|encodeMatrix
specifier|private
name|byte
index|[]
name|encodeMatrix
decl_stmt|;
comment|/**    * Array of input tables generated from coding coefficients previously.    * Must be of size 32*k*rows    */
DECL|field|gfTables
specifier|private
name|byte
index|[]
name|gfTables
decl_stmt|;
DECL|method|RSRawEncoder (ErasureCoderOptions coderOptions)
specifier|public
name|RSRawEncoder
parameter_list|(
name|ErasureCoderOptions
name|coderOptions
parameter_list|)
block|{
name|super
argument_list|(
name|coderOptions
argument_list|)
expr_stmt|;
if|if
condition|(
name|getNumAllUnits
argument_list|()
operator|>=
name|RSUtil
operator|.
name|GF
operator|.
name|getFieldSize
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Invalid numDataUnits and numParityUnits"
argument_list|)
throw|;
block|}
name|encodeMatrix
operator|=
operator|new
name|byte
index|[
name|getNumAllUnits
argument_list|()
operator|*
name|getNumDataUnits
argument_list|()
index|]
expr_stmt|;
name|RSUtil
operator|.
name|genCauchyMatrix
argument_list|(
name|encodeMatrix
argument_list|,
name|getNumAllUnits
argument_list|()
argument_list|,
name|getNumDataUnits
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|allowVerboseDump
argument_list|()
condition|)
block|{
name|DumpUtil
operator|.
name|dumpMatrix
argument_list|(
name|encodeMatrix
argument_list|,
name|getNumDataUnits
argument_list|()
argument_list|,
name|getNumAllUnits
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|gfTables
operator|=
operator|new
name|byte
index|[
name|getNumAllUnits
argument_list|()
operator|*
name|getNumDataUnits
argument_list|()
operator|*
literal|32
index|]
expr_stmt|;
name|RSUtil
operator|.
name|initTables
argument_list|(
name|getNumDataUnits
argument_list|()
argument_list|,
name|getNumParityUnits
argument_list|()
argument_list|,
name|encodeMatrix
argument_list|,
name|getNumDataUnits
argument_list|()
operator|*
name|getNumDataUnits
argument_list|()
argument_list|,
name|gfTables
argument_list|)
expr_stmt|;
if|if
condition|(
name|allowVerboseDump
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|DumpUtil
operator|.
name|bytesToHex
argument_list|(
name|gfTables
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doEncode (ByteBufferEncodingState encodingState)
specifier|protected
name|void
name|doEncode
parameter_list|(
name|ByteBufferEncodingState
name|encodingState
parameter_list|)
block|{
name|CoderUtil
operator|.
name|resetOutputBuffers
argument_list|(
name|encodingState
operator|.
name|outputs
argument_list|,
name|encodingState
operator|.
name|encodeLength
argument_list|)
expr_stmt|;
name|RSUtil
operator|.
name|encodeData
argument_list|(
name|gfTables
argument_list|,
name|encodingState
operator|.
name|inputs
argument_list|,
name|encodingState
operator|.
name|outputs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doEncode (ByteArrayEncodingState encodingState)
specifier|protected
name|void
name|doEncode
parameter_list|(
name|ByteArrayEncodingState
name|encodingState
parameter_list|)
block|{
name|CoderUtil
operator|.
name|resetOutputBuffers
argument_list|(
name|encodingState
operator|.
name|outputs
argument_list|,
name|encodingState
operator|.
name|outputOffsets
argument_list|,
name|encodingState
operator|.
name|encodeLength
argument_list|)
expr_stmt|;
name|RSUtil
operator|.
name|encodeData
argument_list|(
name|gfTables
argument_list|,
name|encodingState
operator|.
name|encodeLength
argument_list|,
name|encodingState
operator|.
name|inputs
argument_list|,
name|encodingState
operator|.
name|inputOffsets
argument_list|,
name|encodingState
operator|.
name|outputs
argument_list|,
name|encodingState
operator|.
name|outputOffsets
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

