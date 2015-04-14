begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity
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
name|capacity
package|;
end_package

begin_comment
comment|/**  * Scheduling modes, see below for detailed explanations   */
end_comment

begin_enum
DECL|enum|SchedulingMode
specifier|public
enum|enum
name|SchedulingMode
block|{
comment|/**    *<p>    * When a node has partition (say partition=x), only application in the queue    * can access to partition=x AND requires for partition=x resource can get    * chance to allocate on the node.    *</p>    *     *<p>    * When a node has no partition, only application requires non-partitioned    * resource can get chance to allocate on the node.    *</p>    */
DECL|enumConstant|RESPECT_PARTITION_EXCLUSIVITY
name|RESPECT_PARTITION_EXCLUSIVITY
block|,
comment|/**    * Only used when a node has partition AND the partition isn't an exclusive    * partition AND application requires non-partitioned resource.    */
DECL|enumConstant|IGNORE_PARTITION_EXCLUSIVITY
name|IGNORE_PARTITION_EXCLUSIVITY
block|}
end_enum

end_unit

