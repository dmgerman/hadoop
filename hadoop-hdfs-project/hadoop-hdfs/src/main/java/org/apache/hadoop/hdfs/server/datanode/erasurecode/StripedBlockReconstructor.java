begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.erasurecode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|erasurecode
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|metrics
operator|.
name|DataNodeMetrics
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
name|Time
import|;
end_import

begin_comment
comment|/**  * StripedBlockReconstructor reconstruct one or more missed striped block in  * the striped block group, the minimum number of live striped blocks should  * be no less than data block number.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|StripedBlockReconstructor
class|class
name|StripedBlockReconstructor
extends|extends
name|StripedReconstructor
implements|implements
name|Runnable
block|{
DECL|field|stripedWriter
specifier|private
name|StripedWriter
name|stripedWriter
decl_stmt|;
DECL|method|StripedBlockReconstructor (ErasureCodingWorker worker, StripedReconstructionInfo stripedReconInfo)
name|StripedBlockReconstructor
parameter_list|(
name|ErasureCodingWorker
name|worker
parameter_list|,
name|StripedReconstructionInfo
name|stripedReconInfo
parameter_list|)
block|{
name|super
argument_list|(
name|worker
argument_list|,
name|stripedReconInfo
argument_list|)
expr_stmt|;
name|stripedWriter
operator|=
operator|new
name|StripedWriter
argument_list|(
name|this
argument_list|,
name|getDatanode
argument_list|()
argument_list|,
name|getConf
argument_list|()
argument_list|,
name|stripedReconInfo
argument_list|)
expr_stmt|;
block|}
DECL|method|hasValidTargets ()
name|boolean
name|hasValidTargets
parameter_list|()
block|{
return|return
name|stripedWriter
operator|.
name|hasValidTargets
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|initDecoderIfNecessary
argument_list|()
expr_stmt|;
name|getStripedReader
argument_list|()
operator|.
name|init
argument_list|()
expr_stmt|;
name|stripedWriter
operator|.
name|init
argument_list|()
expr_stmt|;
name|reconstruct
argument_list|()
expr_stmt|;
name|stripedWriter
operator|.
name|endTargetBlocks
argument_list|()
expr_stmt|;
comment|// Currently we don't check the acks for packets, this is similar as
comment|// block replication.
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to reconstruct striped block: {}"
argument_list|,
name|getBlockGroup
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|getDatanode
argument_list|()
operator|.
name|getMetrics
argument_list|()
operator|.
name|incrECFailedReconstructionTasks
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|getDatanode
argument_list|()
operator|.
name|decrementXmitsInProgress
argument_list|(
name|getXmits
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|DataNodeMetrics
name|metrics
init|=
name|getDatanode
argument_list|()
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
name|metrics
operator|.
name|incrECReconstructionTasks
argument_list|()
expr_stmt|;
name|metrics
operator|.
name|incrECReconstructionBytesRead
argument_list|(
name|getBytesRead
argument_list|()
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|incrECReconstructionRemoteBytesRead
argument_list|(
name|getRemoteBytesRead
argument_list|()
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|incrECReconstructionBytesWritten
argument_list|(
name|getBytesWritten
argument_list|()
argument_list|)
expr_stmt|;
name|getStripedReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|stripedWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|cleanup
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|reconstruct ()
name|void
name|reconstruct
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
name|getPositionInBlock
argument_list|()
operator|<
name|getMaxTargetLength
argument_list|()
condition|)
block|{
name|long
name|remaining
init|=
name|getMaxTargetLength
argument_list|()
operator|-
name|getPositionInBlock
argument_list|()
decl_stmt|;
specifier|final
name|int
name|toReconstructLen
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|getStripedReader
argument_list|()
operator|.
name|getBufferSize
argument_list|()
argument_list|,
name|remaining
argument_list|)
decl_stmt|;
name|long
name|start
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
comment|// step1: read from minimum source DNs required for reconstruction.
comment|// The returned success list is the source DNs we do real read from
name|getStripedReader
argument_list|()
operator|.
name|readMinimumSources
argument_list|(
name|toReconstructLen
argument_list|)
expr_stmt|;
name|long
name|readEnd
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
comment|// step2: decode to reconstruct targets
name|reconstructTargets
argument_list|(
name|toReconstructLen
argument_list|)
expr_stmt|;
name|long
name|decodeEnd
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
comment|// step3: transfer data
if|if
condition|(
name|stripedWriter
operator|.
name|transferData2Targets
argument_list|()
operator|==
literal|0
condition|)
block|{
name|String
name|error
init|=
literal|"Transfer failed for all targets."
decl_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|error
argument_list|)
throw|;
block|}
name|long
name|writeEnd
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
comment|// Only the succeed reconstructions are recorded.
specifier|final
name|DataNodeMetrics
name|metrics
init|=
name|getDatanode
argument_list|()
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
name|metrics
operator|.
name|incrECReconstructionReadTime
argument_list|(
name|readEnd
operator|-
name|start
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|incrECReconstructionDecodingTime
argument_list|(
name|decodeEnd
operator|-
name|readEnd
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|incrECReconstructionWriteTime
argument_list|(
name|writeEnd
operator|-
name|decodeEnd
argument_list|)
expr_stmt|;
name|updatePositionInBlock
argument_list|(
name|toReconstructLen
argument_list|)
expr_stmt|;
name|clearBuffers
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|reconstructTargets (int toReconstructLen)
specifier|private
name|void
name|reconstructTargets
parameter_list|(
name|int
name|toReconstructLen
parameter_list|)
block|{
name|ByteBuffer
index|[]
name|inputs
init|=
name|getStripedReader
argument_list|()
operator|.
name|getInputBuffers
argument_list|(
name|toReconstructLen
argument_list|)
decl_stmt|;
name|int
index|[]
name|erasedIndices
init|=
name|stripedWriter
operator|.
name|getRealTargetIndices
argument_list|()
decl_stmt|;
name|ByteBuffer
index|[]
name|outputs
init|=
name|stripedWriter
operator|.
name|getRealTargetBuffers
argument_list|(
name|toReconstructLen
argument_list|)
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|getDecoder
argument_list|()
operator|.
name|decode
argument_list|(
name|inputs
argument_list|,
name|erasedIndices
argument_list|,
name|outputs
argument_list|)
expr_stmt|;
name|long
name|end
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|this
operator|.
name|getDatanode
argument_list|()
operator|.
name|getMetrics
argument_list|()
operator|.
name|incrECDecodingTime
argument_list|(
name|end
operator|-
name|start
argument_list|)
expr_stmt|;
name|stripedWriter
operator|.
name|updateRealTargetBuffers
argument_list|(
name|toReconstructLen
argument_list|)
expr_stmt|;
block|}
comment|/**    * Clear all associated buffers.    */
DECL|method|clearBuffers ()
specifier|private
name|void
name|clearBuffers
parameter_list|()
block|{
name|getStripedReader
argument_list|()
operator|.
name|clearBuffers
argument_list|()
expr_stmt|;
name|stripedWriter
operator|.
name|clearBuffers
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

