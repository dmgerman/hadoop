begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.protocol
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
name|protocol
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
comment|/**  * This class is helper class to generate a live usage report by calculating  * the delta betweenââcurrent DataNode usage metrics and the usage metrics  * captured at the time of the last report.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|DataNodeUsageReportUtil
specifier|public
specifier|final
class|class
name|DataNodeUsageReportUtil
block|{
DECL|field|bytesWritten
specifier|private
name|long
name|bytesWritten
decl_stmt|;
DECL|field|bytesRead
specifier|private
name|long
name|bytesRead
decl_stmt|;
DECL|field|writeTime
specifier|private
name|long
name|writeTime
decl_stmt|;
DECL|field|readTime
specifier|private
name|long
name|readTime
decl_stmt|;
DECL|field|blocksWritten
specifier|private
name|long
name|blocksWritten
decl_stmt|;
DECL|field|blocksRead
specifier|private
name|long
name|blocksRead
decl_stmt|;
DECL|field|lastReport
specifier|private
name|DataNodeUsageReport
name|lastReport
decl_stmt|;
DECL|method|getUsageReport (long bWritten, long bRead, long wTime, long rTime, long wBlockOp, long rBlockOp, long timeSinceLastReport)
specifier|public
name|DataNodeUsageReport
name|getUsageReport
parameter_list|(
name|long
name|bWritten
parameter_list|,
name|long
name|bRead
parameter_list|,
name|long
name|wTime
parameter_list|,
name|long
name|rTime
parameter_list|,
name|long
name|wBlockOp
parameter_list|,
name|long
name|rBlockOp
parameter_list|,
name|long
name|timeSinceLastReport
parameter_list|)
block|{
if|if
condition|(
name|timeSinceLastReport
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|lastReport
operator|==
literal|null
condition|)
block|{
name|lastReport
operator|=
name|DataNodeUsageReport
operator|.
name|EMPTY_REPORT
expr_stmt|;
block|}
return|return
name|lastReport
return|;
block|}
name|DataNodeUsageReport
operator|.
name|Builder
name|builder
init|=
operator|new
name|DataNodeUsageReport
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|DataNodeUsageReport
name|report
init|=
name|builder
operator|.
name|setBytesWrittenPerSec
argument_list|(
name|getBytesWrittenPerSec
argument_list|(
name|bWritten
argument_list|,
name|timeSinceLastReport
argument_list|)
argument_list|)
operator|.
name|setBytesReadPerSec
argument_list|(
name|getBytesReadPerSec
argument_list|(
name|bRead
argument_list|,
name|timeSinceLastReport
argument_list|)
argument_list|)
operator|.
name|setWriteTime
argument_list|(
name|getWriteTime
argument_list|(
name|wTime
argument_list|)
argument_list|)
operator|.
name|setReadTime
argument_list|(
name|getReadTime
argument_list|(
name|rTime
argument_list|)
argument_list|)
operator|.
name|setBlocksWrittenPerSec
argument_list|(
name|getWriteBlockOpPerSec
argument_list|(
name|wBlockOp
argument_list|,
name|timeSinceLastReport
argument_list|)
argument_list|)
operator|.
name|setBlocksReadPerSec
argument_list|(
name|getReadBlockOpPerSec
argument_list|(
name|rBlockOp
argument_list|,
name|timeSinceLastReport
argument_list|)
argument_list|)
operator|.
name|setTimestamp
argument_list|(
name|Time
operator|.
name|monotonicNow
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// Save raw metrics
name|this
operator|.
name|bytesRead
operator|=
name|bRead
expr_stmt|;
name|this
operator|.
name|bytesWritten
operator|=
name|bWritten
expr_stmt|;
name|this
operator|.
name|blocksWritten
operator|=
name|wBlockOp
expr_stmt|;
name|this
operator|.
name|blocksRead
operator|=
name|rBlockOp
expr_stmt|;
name|this
operator|.
name|readTime
operator|=
name|rTime
expr_stmt|;
name|this
operator|.
name|writeTime
operator|=
name|wTime
expr_stmt|;
name|lastReport
operator|=
name|report
expr_stmt|;
return|return
name|report
return|;
block|}
DECL|method|getBytesReadPerSec (long bRead, long timeInSec)
specifier|private
name|long
name|getBytesReadPerSec
parameter_list|(
name|long
name|bRead
parameter_list|,
name|long
name|timeInSec
parameter_list|)
block|{
return|return
operator|(
name|bRead
operator|-
name|this
operator|.
name|bytesRead
operator|)
operator|/
name|timeInSec
return|;
block|}
DECL|method|getBytesWrittenPerSec (long bWritten, long timeInSec)
specifier|private
name|long
name|getBytesWrittenPerSec
parameter_list|(
name|long
name|bWritten
parameter_list|,
name|long
name|timeInSec
parameter_list|)
block|{
return|return
operator|(
name|bWritten
operator|-
name|this
operator|.
name|bytesWritten
operator|)
operator|/
name|timeInSec
return|;
block|}
DECL|method|getWriteBlockOpPerSec ( long totalWriteBlocks, long timeInSec)
specifier|private
name|long
name|getWriteBlockOpPerSec
parameter_list|(
name|long
name|totalWriteBlocks
parameter_list|,
name|long
name|timeInSec
parameter_list|)
block|{
return|return
operator|(
name|totalWriteBlocks
operator|-
name|this
operator|.
name|blocksWritten
operator|)
operator|/
name|timeInSec
return|;
block|}
DECL|method|getReadBlockOpPerSec (long totalReadBlockOp, long timeInSec)
specifier|private
name|long
name|getReadBlockOpPerSec
parameter_list|(
name|long
name|totalReadBlockOp
parameter_list|,
name|long
name|timeInSec
parameter_list|)
block|{
return|return
operator|(
name|totalReadBlockOp
operator|-
name|this
operator|.
name|blocksRead
operator|)
operator|/
name|timeInSec
return|;
block|}
DECL|method|getReadTime (long totalReadTime)
specifier|private
name|long
name|getReadTime
parameter_list|(
name|long
name|totalReadTime
parameter_list|)
block|{
return|return
name|totalReadTime
operator|-
name|this
operator|.
name|readTime
return|;
block|}
DECL|method|getWriteTime (long totalWriteTime)
specifier|private
name|long
name|getWriteTime
parameter_list|(
name|long
name|totalWriteTime
parameter_list|)
block|{
return|return
name|totalWriteTime
operator|-
name|this
operator|.
name|writeTime
return|;
block|}
block|}
end_class

end_unit

