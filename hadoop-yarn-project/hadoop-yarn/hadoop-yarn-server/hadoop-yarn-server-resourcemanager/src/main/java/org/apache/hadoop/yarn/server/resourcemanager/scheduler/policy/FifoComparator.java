begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.policy
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|policy
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A Comparator which orders SchedulableEntities by input order  */
end_comment

begin_class
DECL|class|FifoComparator
specifier|public
class|class
name|FifoComparator
implements|implements
name|Comparator
argument_list|<
name|SchedulableEntity
argument_list|>
block|{
annotation|@
name|Override
DECL|method|compare (SchedulableEntity r1, SchedulableEntity r2)
specifier|public
name|int
name|compare
parameter_list|(
name|SchedulableEntity
name|r1
parameter_list|,
name|SchedulableEntity
name|r2
parameter_list|)
block|{
name|int
name|res
init|=
name|r1
operator|.
name|compareInputOrderTo
argument_list|(
name|r2
argument_list|)
decl_stmt|;
return|return
name|res
return|;
block|}
block|}
end_class

end_unit

