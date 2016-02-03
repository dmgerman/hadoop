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
name|RSUtil2
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

begin_comment
comment|/**  * A raw erasure encoder in RS code scheme in pure Java in case native one  * isn't available in some environment. Please always use native implementations  * when possible. This new Java coder is about 5X faster than the one originated  * from HDFS-RAID, and also compatible with the native/ISA-L coder.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|RSRawEncoder2
specifier|public
class|class
name|RSRawEncoder2
extends|extends
name|AbstractRawErasureEncoder
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
DECL|method|RSRawEncoder2 (int numDataUnits, int numParityUnits)
specifier|public
name|RSRawEncoder2
parameter_list|(
name|int
name|numDataUnits
parameter_list|,
name|int
name|numParityUnits
parameter_list|)
block|{
name|super
argument_list|(
name|numDataUnits
argument_list|,
name|numParityUnits
argument_list|)
expr_stmt|;
if|if
condition|(
name|numDataUnits
operator|+
name|numParityUnits
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
name|numDataUnits
index|]
expr_stmt|;
name|RSUtil2
operator|.
name|genCauchyMatrix
argument_list|(
name|encodeMatrix
argument_list|,
name|getNumAllUnits
argument_list|()
argument_list|,
name|numDataUnits
argument_list|)
expr_stmt|;
if|if
condition|(
name|isAllowingVerboseDump
argument_list|()
condition|)
block|{
name|DumpUtil
operator|.
name|dumpMatrix
argument_list|(
name|encodeMatrix
argument_list|,
name|numDataUnits
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
name|numDataUnits
operator|*
literal|32
index|]
expr_stmt|;
name|RSUtil2
operator|.
name|initTables
argument_list|(
name|numDataUnits
argument_list|,
name|numParityUnits
argument_list|,
name|encodeMatrix
argument_list|,
name|numDataUnits
operator|*
name|numDataUnits
argument_list|,
name|gfTables
argument_list|)
expr_stmt|;
if|if
condition|(
name|isAllowingVerboseDump
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
DECL|method|doEncode (ByteBuffer[] inputs, ByteBuffer[] outputs)
specifier|protected
name|void
name|doEncode
parameter_list|(
name|ByteBuffer
index|[]
name|inputs
parameter_list|,
name|ByteBuffer
index|[]
name|outputs
parameter_list|)
block|{
name|RSUtil2
operator|.
name|encodeData
argument_list|(
name|gfTables
argument_list|,
name|inputs
argument_list|,
name|outputs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doEncode (byte[][] inputs, int[] inputOffsets, int dataLen, byte[][] outputs, int[] outputOffsets)
specifier|protected
name|void
name|doEncode
parameter_list|(
name|byte
index|[]
index|[]
name|inputs
parameter_list|,
name|int
index|[]
name|inputOffsets
parameter_list|,
name|int
name|dataLen
parameter_list|,
name|byte
index|[]
index|[]
name|outputs
parameter_list|,
name|int
index|[]
name|outputOffsets
parameter_list|)
block|{
name|RSUtil2
operator|.
name|encodeData
argument_list|(
name|gfTables
argument_list|,
name|dataLen
argument_list|,
name|inputs
argument_list|,
name|inputOffsets
argument_list|,
name|outputs
argument_list|,
name|outputOffsets
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

