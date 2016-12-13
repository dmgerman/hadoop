begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
package|;
end_package

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonInclude
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectReader
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Keeps track of how much work has finished.  */
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
annotation|@
name|JsonInclude
argument_list|(
name|JsonInclude
operator|.
name|Include
operator|.
name|NON_DEFAULT
argument_list|)
DECL|class|DiskBalancerWorkItem
specifier|public
class|class
name|DiskBalancerWorkItem
block|{
DECL|field|MAPPER
specifier|private
specifier|static
specifier|final
name|ObjectMapper
name|MAPPER
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
DECL|field|READER
specifier|private
specifier|static
specifier|final
name|ObjectReader
name|READER
init|=
operator|new
name|ObjectMapper
argument_list|()
operator|.
name|readerFor
argument_list|(
name|DiskBalancerWorkItem
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|startTime
specifier|private
name|long
name|startTime
decl_stmt|;
DECL|field|secondsElapsed
specifier|private
name|long
name|secondsElapsed
decl_stmt|;
DECL|field|bytesToCopy
specifier|private
name|long
name|bytesToCopy
decl_stmt|;
DECL|field|bytesCopied
specifier|private
name|long
name|bytesCopied
decl_stmt|;
DECL|field|errorCount
specifier|private
name|long
name|errorCount
decl_stmt|;
DECL|field|errMsg
specifier|private
name|String
name|errMsg
decl_stmt|;
DECL|field|blocksCopied
specifier|private
name|long
name|blocksCopied
decl_stmt|;
DECL|field|maxDiskErrors
specifier|private
name|long
name|maxDiskErrors
decl_stmt|;
DECL|field|tolerancePercent
specifier|private
name|long
name|tolerancePercent
decl_stmt|;
DECL|field|bandwidth
specifier|private
name|long
name|bandwidth
decl_stmt|;
comment|/**    * Empty constructor for Json serialization.    */
DECL|method|DiskBalancerWorkItem ()
specifier|public
name|DiskBalancerWorkItem
parameter_list|()
block|{    }
comment|/**    * Constructs a DiskBalancerWorkItem.    *    * @param bytesToCopy - Total bytes to copy from a disk    * @param bytesCopied - Copied So far.    */
DECL|method|DiskBalancerWorkItem (long bytesToCopy, long bytesCopied)
specifier|public
name|DiskBalancerWorkItem
parameter_list|(
name|long
name|bytesToCopy
parameter_list|,
name|long
name|bytesCopied
parameter_list|)
block|{
name|this
operator|.
name|bytesToCopy
operator|=
name|bytesToCopy
expr_stmt|;
name|this
operator|.
name|bytesCopied
operator|=
name|bytesCopied
expr_stmt|;
block|}
comment|/**    * Reads a DiskBalancerWorkItem Object from a Json String.    *    * @param json - Json String.    * @return DiskBalancerWorkItem Object    * @throws IOException    */
DECL|method|parseJson (String json)
specifier|public
specifier|static
name|DiskBalancerWorkItem
name|parseJson
parameter_list|(
name|String
name|json
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|json
argument_list|)
expr_stmt|;
return|return
name|READER
operator|.
name|readValue
argument_list|(
name|json
argument_list|)
return|;
block|}
comment|/**    * Gets the error message.    */
DECL|method|getErrMsg ()
specifier|public
name|String
name|getErrMsg
parameter_list|()
block|{
return|return
name|errMsg
return|;
block|}
comment|/**    * Sets the error message.    *    * @param errMsg - Msg.    */
DECL|method|setErrMsg (String errMsg)
specifier|public
name|void
name|setErrMsg
parameter_list|(
name|String
name|errMsg
parameter_list|)
block|{
name|this
operator|.
name|errMsg
operator|=
name|errMsg
expr_stmt|;
block|}
comment|/**    * Returns the number of errors encountered.    *    * @return long    */
DECL|method|getErrorCount ()
specifier|public
name|long
name|getErrorCount
parameter_list|()
block|{
return|return
name|errorCount
return|;
block|}
comment|/**    * Incs Error Count.    */
DECL|method|incErrorCount ()
specifier|public
name|void
name|incErrorCount
parameter_list|()
block|{
name|this
operator|.
name|errorCount
operator|++
expr_stmt|;
block|}
comment|/**    * Returns bytes copied so far.    *    * @return long    */
DECL|method|getBytesCopied ()
specifier|public
name|long
name|getBytesCopied
parameter_list|()
block|{
return|return
name|bytesCopied
return|;
block|}
comment|/**    * Sets bytes copied so far.    *    * @param bytesCopied - long    */
DECL|method|setBytesCopied (long bytesCopied)
specifier|public
name|void
name|setBytesCopied
parameter_list|(
name|long
name|bytesCopied
parameter_list|)
block|{
name|this
operator|.
name|bytesCopied
operator|=
name|bytesCopied
expr_stmt|;
block|}
comment|/**    * Increments bytesCopied by delta.    *    * @param delta - long    */
DECL|method|incCopiedSoFar (long delta)
specifier|public
name|void
name|incCopiedSoFar
parameter_list|(
name|long
name|delta
parameter_list|)
block|{
name|this
operator|.
name|bytesCopied
operator|+=
name|delta
expr_stmt|;
block|}
comment|/**    * Returns bytes to copy.    *    * @return - long    */
DECL|method|getBytesToCopy ()
specifier|public
name|long
name|getBytesToCopy
parameter_list|()
block|{
return|return
name|bytesToCopy
return|;
block|}
comment|/**    * Returns number of blocks copied for this DiskBalancerWorkItem.    *    * @return long count of blocks.    */
DECL|method|getBlocksCopied ()
specifier|public
name|long
name|getBlocksCopied
parameter_list|()
block|{
return|return
name|blocksCopied
return|;
block|}
comment|/**    * increments the number of blocks copied.    */
DECL|method|incBlocksCopied ()
specifier|public
name|void
name|incBlocksCopied
parameter_list|()
block|{
name|blocksCopied
operator|++
expr_stmt|;
block|}
comment|/**    * returns a serialized json string.    *    * @return String - json    * @throws IOException    */
DECL|method|toJson ()
specifier|public
name|String
name|toJson
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|MAPPER
operator|.
name|writeValueAsString
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Sets the Error counts for this step.    *    * @param errorCount long.    */
DECL|method|setErrorCount (long errorCount)
specifier|public
name|void
name|setErrorCount
parameter_list|(
name|long
name|errorCount
parameter_list|)
block|{
name|this
operator|.
name|errorCount
operator|=
name|errorCount
expr_stmt|;
block|}
comment|/**    * Number of blocks copied so far.    *    * @param blocksCopied Blocks copied.    */
DECL|method|setBlocksCopied (long blocksCopied)
specifier|public
name|void
name|setBlocksCopied
parameter_list|(
name|long
name|blocksCopied
parameter_list|)
block|{
name|this
operator|.
name|blocksCopied
operator|=
name|blocksCopied
expr_stmt|;
block|}
comment|/**    * Gets maximum disk errors to tolerate before we fail this copy step.    *    * @return long.    */
DECL|method|getMaxDiskErrors ()
specifier|public
name|long
name|getMaxDiskErrors
parameter_list|()
block|{
return|return
name|maxDiskErrors
return|;
block|}
comment|/**    * Sets maximum disk errors to tolerate before we fail this copy step.    *    * @param maxDiskErrors long    */
DECL|method|setMaxDiskErrors (long maxDiskErrors)
specifier|public
name|void
name|setMaxDiskErrors
parameter_list|(
name|long
name|maxDiskErrors
parameter_list|)
block|{
name|this
operator|.
name|maxDiskErrors
operator|=
name|maxDiskErrors
expr_stmt|;
block|}
comment|/**    * Allowed deviation from ideal storage in percentage.    *    * @return long    */
DECL|method|getTolerancePercent ()
specifier|public
name|long
name|getTolerancePercent
parameter_list|()
block|{
return|return
name|tolerancePercent
return|;
block|}
comment|/**    * Sets the tolerance percentage.    *    * @param tolerancePercent - tolerance.    */
DECL|method|setTolerancePercent (long tolerancePercent)
specifier|public
name|void
name|setTolerancePercent
parameter_list|(
name|long
name|tolerancePercent
parameter_list|)
block|{
name|this
operator|.
name|tolerancePercent
operator|=
name|tolerancePercent
expr_stmt|;
block|}
comment|/**    * Max disk bandwidth to use. MB per second.    *    * @return - long.    */
DECL|method|getBandwidth ()
specifier|public
name|long
name|getBandwidth
parameter_list|()
block|{
return|return
name|bandwidth
return|;
block|}
comment|/**    * Sets max disk bandwidth to use, in MBs per second.    *    * @param bandwidth - long.    */
DECL|method|setBandwidth (long bandwidth)
specifier|public
name|void
name|setBandwidth
parameter_list|(
name|long
name|bandwidth
parameter_list|)
block|{
name|this
operator|.
name|bandwidth
operator|=
name|bandwidth
expr_stmt|;
block|}
comment|/**    * Records the Start time of execution.    * @return startTime    */
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|startTime
return|;
block|}
comment|/**    * Sets the Start time.    * @param startTime  - Time stamp for start of execution.    */
DECL|method|setStartTime (long startTime)
specifier|public
name|void
name|setStartTime
parameter_list|(
name|long
name|startTime
parameter_list|)
block|{
name|this
operator|.
name|startTime
operator|=
name|startTime
expr_stmt|;
block|}
comment|/**    * Gets the number of seconds elapsed from the start time.    *    * The reason why we have this is of time skews. The client's current time    * may not match with the server time stamp, hence the elapsed second    * cannot be computed from only startTime.    *    * @return seconds elapsed from start time.    */
DECL|method|getSecondsElapsed ()
specifier|public
name|long
name|getSecondsElapsed
parameter_list|()
block|{
return|return
name|secondsElapsed
return|;
block|}
comment|/**    * Sets number of seconds elapsed.    *    * This is updated whenever we update the other counters.    * @param secondsElapsed  - seconds elapsed.    */
DECL|method|setSecondsElapsed (long secondsElapsed)
specifier|public
name|void
name|setSecondsElapsed
parameter_list|(
name|long
name|secondsElapsed
parameter_list|)
block|{
name|this
operator|.
name|secondsElapsed
operator|=
name|secondsElapsed
expr_stmt|;
block|}
block|}
end_class

end_unit

