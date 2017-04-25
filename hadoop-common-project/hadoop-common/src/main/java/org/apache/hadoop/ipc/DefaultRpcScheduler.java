begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
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
name|conf
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|/**  * No op default RPC scheduler.  */
end_comment

begin_class
DECL|class|DefaultRpcScheduler
specifier|public
class|class
name|DefaultRpcScheduler
implements|implements
name|RpcScheduler
block|{
annotation|@
name|Override
DECL|method|getPriorityLevel (Schedulable obj)
specifier|public
name|int
name|getPriorityLevel
parameter_list|(
name|Schedulable
name|obj
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|shouldBackOff (Schedulable obj)
specifier|public
name|boolean
name|shouldBackOff
parameter_list|(
name|Schedulable
name|obj
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|addResponseTime (String name, int priorityLevel, int queueTime, int processingTime)
specifier|public
name|void
name|addResponseTime
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|priorityLevel
parameter_list|,
name|int
name|queueTime
parameter_list|,
name|int
name|processingTime
parameter_list|)
block|{   }
DECL|method|DefaultRpcScheduler (int priorityLevels, String namespace, Configuration conf)
specifier|public
name|DefaultRpcScheduler
parameter_list|(
name|int
name|priorityLevels
parameter_list|,
name|String
name|namespace
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{   }
block|}
end_class

end_unit

