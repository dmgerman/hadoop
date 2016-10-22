begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.task.reduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|task
operator|.
name|reduce
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskAttemptID
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|MapHost
specifier|public
class|class
name|MapHost
block|{
DECL|enum|State
specifier|public
specifier|static
enum|enum
name|State
block|{
DECL|enumConstant|IDLE
name|IDLE
block|,
comment|// No map outputs available
DECL|enumConstant|BUSY
name|BUSY
block|,
comment|// Map outputs are being fetched
DECL|enumConstant|PENDING
name|PENDING
block|,
comment|// Known map outputs which need to be fetched
DECL|enumConstant|PENALIZED
name|PENALIZED
comment|// Host penalized due to shuffle failures
block|}
DECL|field|state
specifier|private
name|State
name|state
init|=
name|State
operator|.
name|IDLE
decl_stmt|;
DECL|field|hostName
specifier|private
specifier|final
name|String
name|hostName
decl_stmt|;
DECL|field|baseUrl
specifier|private
specifier|final
name|String
name|baseUrl
decl_stmt|;
DECL|field|maps
specifier|private
name|List
argument_list|<
name|TaskAttemptID
argument_list|>
name|maps
init|=
operator|new
name|ArrayList
argument_list|<
name|TaskAttemptID
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|MapHost (String hostName, String baseUrl)
specifier|public
name|MapHost
parameter_list|(
name|String
name|hostName
parameter_list|,
name|String
name|baseUrl
parameter_list|)
block|{
name|this
operator|.
name|hostName
operator|=
name|hostName
expr_stmt|;
name|this
operator|.
name|baseUrl
operator|=
name|baseUrl
expr_stmt|;
block|}
DECL|method|getState ()
specifier|public
name|State
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
DECL|method|getHostName ()
specifier|public
name|String
name|getHostName
parameter_list|()
block|{
return|return
name|hostName
return|;
block|}
DECL|method|getBaseUrl ()
specifier|public
name|String
name|getBaseUrl
parameter_list|()
block|{
return|return
name|baseUrl
return|;
block|}
DECL|method|addKnownMap (TaskAttemptID mapId)
specifier|public
specifier|synchronized
name|void
name|addKnownMap
parameter_list|(
name|TaskAttemptID
name|mapId
parameter_list|)
block|{
name|maps
operator|.
name|add
argument_list|(
name|mapId
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|==
name|State
operator|.
name|IDLE
condition|)
block|{
name|state
operator|=
name|State
operator|.
name|PENDING
expr_stmt|;
block|}
block|}
DECL|method|getAndClearKnownMaps ()
specifier|public
specifier|synchronized
name|List
argument_list|<
name|TaskAttemptID
argument_list|>
name|getAndClearKnownMaps
parameter_list|()
block|{
name|List
argument_list|<
name|TaskAttemptID
argument_list|>
name|currentKnownMaps
init|=
name|maps
decl_stmt|;
name|maps
operator|=
operator|new
name|ArrayList
argument_list|<
name|TaskAttemptID
argument_list|>
argument_list|()
expr_stmt|;
return|return
name|currentKnownMaps
return|;
block|}
DECL|method|markBusy ()
specifier|public
specifier|synchronized
name|void
name|markBusy
parameter_list|()
block|{
name|state
operator|=
name|State
operator|.
name|BUSY
expr_stmt|;
block|}
DECL|method|getNumKnownMapOutputs ()
specifier|public
specifier|synchronized
name|int
name|getNumKnownMapOutputs
parameter_list|()
block|{
return|return
name|maps
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Called when the node is done with its penalty or done copying.    * @return the host's new state    */
DECL|method|markAvailable ()
specifier|public
specifier|synchronized
name|State
name|markAvailable
parameter_list|()
block|{
if|if
condition|(
name|maps
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|state
operator|=
name|State
operator|.
name|IDLE
expr_stmt|;
block|}
else|else
block|{
name|state
operator|=
name|State
operator|.
name|PENDING
expr_stmt|;
block|}
return|return
name|state
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|hostName
return|;
block|}
comment|/**    * Mark the host as penalized    */
DECL|method|penalize ()
specifier|public
specifier|synchronized
name|void
name|penalize
parameter_list|()
block|{
name|state
operator|=
name|State
operator|.
name|PENALIZED
expr_stmt|;
block|}
block|}
end_class

end_unit

