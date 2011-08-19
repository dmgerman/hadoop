begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
package|;
end_package

begin_comment
comment|/**  * Configuration Options used in the priority schedulers  * -all in one place for ease of referencing in code.  */
end_comment

begin_class
DECL|class|PrioritySchedulerOptions
specifier|public
class|class
name|PrioritySchedulerOptions
block|{
comment|/** {@value} */
DECL|field|DYNAMIC_SCHEDULER_BUDGET_FILE
specifier|public
specifier|static
specifier|final
name|String
name|DYNAMIC_SCHEDULER_BUDGET_FILE
init|=
literal|"mapred.dynamic-scheduler.budget-file"
decl_stmt|;
comment|/** {@value} */
DECL|field|DYNAMIC_SCHEDULER_STORE
specifier|public
specifier|static
specifier|final
name|String
name|DYNAMIC_SCHEDULER_STORE
init|=
literal|"mapred.dynamic-scheduler.store"
decl_stmt|;
comment|/** {@value} */
DECL|field|MAPRED_QUEUE_NAMES
specifier|public
specifier|static
specifier|final
name|String
name|MAPRED_QUEUE_NAMES
init|=
literal|"mapred.queue.names"
decl_stmt|;
comment|/** {@value} */
DECL|field|DYNAMIC_SCHEDULER_SCHEDULER
specifier|public
specifier|static
specifier|final
name|String
name|DYNAMIC_SCHEDULER_SCHEDULER
init|=
literal|"mapred.dynamic-scheduler.scheduler"
decl_stmt|;
comment|/** {@value} */
DECL|field|DYNAMIC_SCHEDULER_ALLOC_INTERVAL
specifier|public
specifier|static
specifier|final
name|String
name|DYNAMIC_SCHEDULER_ALLOC_INTERVAL
init|=
literal|"mapred.dynamic-scheduler.alloc-interval"
decl_stmt|;
block|}
end_class

end_unit

