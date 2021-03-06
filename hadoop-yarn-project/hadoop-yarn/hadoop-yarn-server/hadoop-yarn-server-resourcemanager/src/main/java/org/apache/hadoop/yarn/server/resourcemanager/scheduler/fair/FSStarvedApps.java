begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair
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
name|fair
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
name|yarn
operator|.
name|util
operator|.
name|resource
operator|.
name|Resources
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|PriorityBlockingQueue
import|;
end_import

begin_comment
comment|/**  * Helper class to track starved applications.  *  * Initially, this uses a blocking queue. We could use other data structures  * in the future. This class also has some methods to simplify testing.  */
end_comment

begin_class
DECL|class|FSStarvedApps
class|class
name|FSStarvedApps
block|{
comment|// List of apps to be processed by the preemption thread.
DECL|field|appsToProcess
specifier|private
name|PriorityBlockingQueue
argument_list|<
name|FSAppAttempt
argument_list|>
name|appsToProcess
decl_stmt|;
comment|// App being currently processed. This assumes a single reader.
DECL|field|appBeingProcessed
specifier|private
name|FSAppAttempt
name|appBeingProcessed
decl_stmt|;
DECL|method|FSStarvedApps ()
name|FSStarvedApps
parameter_list|()
block|{
name|appsToProcess
operator|=
operator|new
name|PriorityBlockingQueue
argument_list|<>
argument_list|(
literal|10
argument_list|,
operator|new
name|StarvationComparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a starved application if it is not already added.    * @param app application to add    */
DECL|method|addStarvedApp (FSAppAttempt app)
name|void
name|addStarvedApp
parameter_list|(
name|FSAppAttempt
name|app
parameter_list|)
block|{
if|if
condition|(
operator|!
name|app
operator|.
name|equals
argument_list|(
name|appBeingProcessed
argument_list|)
operator|&&
operator|!
name|appsToProcess
operator|.
name|contains
argument_list|(
name|app
argument_list|)
condition|)
block|{
name|appsToProcess
operator|.
name|add
argument_list|(
name|app
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Blocking call to fetch the next app to process. The returned app is    * tracked until the next call to this method. This tracking assumes a    * single reader.    *    * @return starved application to process    * @throws InterruptedException if interrupted while waiting    */
DECL|method|take ()
name|FSAppAttempt
name|take
parameter_list|()
throws|throws
name|InterruptedException
block|{
comment|// Reset appBeingProcessed before the blocking call
name|appBeingProcessed
operator|=
literal|null
expr_stmt|;
comment|// Blocking call to fetch the next starved application
name|FSAppAttempt
name|app
init|=
name|appsToProcess
operator|.
name|take
argument_list|()
decl_stmt|;
name|appBeingProcessed
operator|=
name|app
expr_stmt|;
return|return
name|app
return|;
block|}
DECL|class|StarvationComparator
specifier|private
specifier|static
class|class
name|StarvationComparator
implements|implements
name|Comparator
argument_list|<
name|FSAppAttempt
argument_list|>
implements|,
name|Serializable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1
decl_stmt|;
annotation|@
name|Override
DECL|method|compare (FSAppAttempt app1, FSAppAttempt app2)
specifier|public
name|int
name|compare
parameter_list|(
name|FSAppAttempt
name|app1
parameter_list|,
name|FSAppAttempt
name|app2
parameter_list|)
block|{
name|int
name|ret
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|Resources
operator|.
name|fitsIn
argument_list|(
name|app1
operator|.
name|getStarvation
argument_list|()
argument_list|,
name|app2
operator|.
name|getStarvation
argument_list|()
argument_list|)
condition|)
block|{
name|ret
operator|=
operator|-
literal|1
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
block|}
block|}
end_class

end_unit

