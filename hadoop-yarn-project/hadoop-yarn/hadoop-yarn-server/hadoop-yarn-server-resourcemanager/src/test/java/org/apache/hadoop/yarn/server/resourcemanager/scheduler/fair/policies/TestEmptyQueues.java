begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.policies
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
operator|.
name|policies
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|Schedulable
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|SchedulingPolicy
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
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

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
name|Collection
import|;
end_import

begin_class
DECL|class|TestEmptyQueues
specifier|public
class|class
name|TestEmptyQueues
block|{
DECL|field|schedulables
specifier|private
name|Collection
argument_list|<
name|?
extends|extends
name|Schedulable
argument_list|>
name|schedulables
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|schedulables
operator|=
operator|new
name|ArrayList
argument_list|<
name|Schedulable
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|testComputeShares (SchedulingPolicy policy)
specifier|private
name|void
name|testComputeShares
parameter_list|(
name|SchedulingPolicy
name|policy
parameter_list|)
block|{
name|policy
operator|.
name|computeShares
argument_list|(
name|schedulables
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|testFifoPolicy ()
specifier|public
name|void
name|testFifoPolicy
parameter_list|()
block|{
name|testComputeShares
argument_list|(
name|SchedulingPolicy
operator|.
name|getInstance
argument_list|(
name|FifoPolicy
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|testFairSharePolicy ()
specifier|public
name|void
name|testFairSharePolicy
parameter_list|()
block|{
name|testComputeShares
argument_list|(
name|SchedulingPolicy
operator|.
name|getInstance
argument_list|(
name|FairSharePolicy
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|testDRFPolicy ()
specifier|public
name|void
name|testDRFPolicy
parameter_list|()
block|{
name|testComputeShares
argument_list|(
name|SchedulingPolicy
operator|.
name|getInstance
argument_list|(
name|DominantResourceFairnessPolicy
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

