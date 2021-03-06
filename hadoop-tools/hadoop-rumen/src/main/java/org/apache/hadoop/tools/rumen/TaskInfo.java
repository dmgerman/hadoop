begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
package|;
end_package

begin_class
DECL|class|TaskInfo
specifier|public
class|class
name|TaskInfo
block|{
DECL|field|bytesIn
specifier|private
specifier|final
name|long
name|bytesIn
decl_stmt|;
DECL|field|recsIn
specifier|private
specifier|final
name|int
name|recsIn
decl_stmt|;
DECL|field|bytesOut
specifier|private
specifier|final
name|long
name|bytesOut
decl_stmt|;
DECL|field|recsOut
specifier|private
specifier|final
name|int
name|recsOut
decl_stmt|;
DECL|field|maxMemory
specifier|private
specifier|final
name|long
name|maxMemory
decl_stmt|;
DECL|field|maxVcores
specifier|private
specifier|final
name|long
name|maxVcores
decl_stmt|;
DECL|field|metrics
specifier|private
specifier|final
name|ResourceUsageMetrics
name|metrics
decl_stmt|;
DECL|method|TaskInfo (long bytesIn, int recsIn, long bytesOut, int recsOut, long maxMemory)
specifier|public
name|TaskInfo
parameter_list|(
name|long
name|bytesIn
parameter_list|,
name|int
name|recsIn
parameter_list|,
name|long
name|bytesOut
parameter_list|,
name|int
name|recsOut
parameter_list|,
name|long
name|maxMemory
parameter_list|)
block|{
name|this
argument_list|(
name|bytesIn
argument_list|,
name|recsIn
argument_list|,
name|bytesOut
argument_list|,
name|recsOut
argument_list|,
name|maxMemory
argument_list|,
literal|1
argument_list|,
operator|new
name|ResourceUsageMetrics
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|TaskInfo (long bytesIn, int recsIn, long bytesOut, int recsOut, long maxMemory, ResourceUsageMetrics metrics)
specifier|public
name|TaskInfo
parameter_list|(
name|long
name|bytesIn
parameter_list|,
name|int
name|recsIn
parameter_list|,
name|long
name|bytesOut
parameter_list|,
name|int
name|recsOut
parameter_list|,
name|long
name|maxMemory
parameter_list|,
name|ResourceUsageMetrics
name|metrics
parameter_list|)
block|{
name|this
argument_list|(
name|bytesIn
argument_list|,
name|recsIn
argument_list|,
name|bytesOut
argument_list|,
name|recsOut
argument_list|,
name|maxMemory
argument_list|,
literal|1
argument_list|,
name|metrics
argument_list|)
expr_stmt|;
block|}
DECL|method|TaskInfo (long bytesIn, int recsIn, long bytesOut, int recsOut, long maxMemory, long maxVcores)
specifier|public
name|TaskInfo
parameter_list|(
name|long
name|bytesIn
parameter_list|,
name|int
name|recsIn
parameter_list|,
name|long
name|bytesOut
parameter_list|,
name|int
name|recsOut
parameter_list|,
name|long
name|maxMemory
parameter_list|,
name|long
name|maxVcores
parameter_list|)
block|{
name|this
argument_list|(
name|bytesIn
argument_list|,
name|recsIn
argument_list|,
name|bytesOut
argument_list|,
name|recsOut
argument_list|,
name|maxMemory
argument_list|,
name|maxVcores
argument_list|,
operator|new
name|ResourceUsageMetrics
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|TaskInfo (long bytesIn, int recsIn, long bytesOut, int recsOut, long maxMemory, long maxVcores, ResourceUsageMetrics metrics)
specifier|public
name|TaskInfo
parameter_list|(
name|long
name|bytesIn
parameter_list|,
name|int
name|recsIn
parameter_list|,
name|long
name|bytesOut
parameter_list|,
name|int
name|recsOut
parameter_list|,
name|long
name|maxMemory
parameter_list|,
name|long
name|maxVcores
parameter_list|,
name|ResourceUsageMetrics
name|metrics
parameter_list|)
block|{
name|this
operator|.
name|bytesIn
operator|=
name|bytesIn
expr_stmt|;
name|this
operator|.
name|recsIn
operator|=
name|recsIn
expr_stmt|;
name|this
operator|.
name|bytesOut
operator|=
name|bytesOut
expr_stmt|;
name|this
operator|.
name|recsOut
operator|=
name|recsOut
expr_stmt|;
name|this
operator|.
name|maxMemory
operator|=
name|maxMemory
expr_stmt|;
name|this
operator|.
name|maxVcores
operator|=
name|maxVcores
expr_stmt|;
name|this
operator|.
name|metrics
operator|=
name|metrics
expr_stmt|;
block|}
comment|/**    * @return Raw bytes read from the FileSystem into the task. Note that this    *         may not always match the input bytes to the task.    */
DECL|method|getInputBytes ()
specifier|public
name|long
name|getInputBytes
parameter_list|()
block|{
return|return
name|bytesIn
return|;
block|}
comment|/**    * @return Number of records input to this task.    */
DECL|method|getInputRecords ()
specifier|public
name|int
name|getInputRecords
parameter_list|()
block|{
return|return
name|recsIn
return|;
block|}
comment|/**    * @return Raw bytes written to the destination FileSystem. Note that this may    *         not match output bytes.    */
DECL|method|getOutputBytes ()
specifier|public
name|long
name|getOutputBytes
parameter_list|()
block|{
return|return
name|bytesOut
return|;
block|}
comment|/**    * @return Number of records output from this task.    */
DECL|method|getOutputRecords ()
specifier|public
name|int
name|getOutputRecords
parameter_list|()
block|{
return|return
name|recsOut
return|;
block|}
comment|/**    * @return Memory used by the task leq the heap size.    */
DECL|method|getTaskMemory ()
specifier|public
name|long
name|getTaskMemory
parameter_list|()
block|{
return|return
name|maxMemory
return|;
block|}
comment|/**    * @return Vcores used by the task.    */
DECL|method|getTaskVCores ()
specifier|public
name|long
name|getTaskVCores
parameter_list|()
block|{
return|return
name|maxVcores
return|;
block|}
comment|/**    * @return Resource usage metrics    */
DECL|method|getResourceUsageMetrics ()
specifier|public
name|ResourceUsageMetrics
name|getResourceUsageMetrics
parameter_list|()
block|{
return|return
name|metrics
return|;
block|}
block|}
end_class

end_unit

