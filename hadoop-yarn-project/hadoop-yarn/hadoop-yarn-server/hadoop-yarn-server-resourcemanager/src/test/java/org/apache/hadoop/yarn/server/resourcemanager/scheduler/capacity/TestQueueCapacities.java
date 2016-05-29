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

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|api
operator|.
name|records
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|TestQueueCapacities
specifier|public
class|class
name|TestQueueCapacities
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestQueueCapacities
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|suffix
specifier|private
name|String
name|suffix
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameters
DECL|method|getParameters ()
specifier|public
specifier|static
name|Collection
argument_list|<
name|String
index|[]
argument_list|>
name|getParameters
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
index|[]
block|{
block|{
literal|"Capacity"
block|}
block|,
block|{
literal|"AbsoluteCapacity"
block|}
block|,
block|{
literal|"UsedCapacity"
block|}
block|,
block|{
literal|"AbsoluteUsedCapacity"
block|}
block|,
block|{
literal|"MaximumCapacity"
block|}
block|,
block|{
literal|"AbsoluteMaximumCapacity"
block|}
block|,
block|{
literal|"MaxAMResourcePercentage"
block|}
block|,
block|{
literal|"ReservedCapacity"
block|}
block|,
block|{
literal|"AbsoluteReservedCapacity"
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|TestQueueCapacities (String suffix)
specifier|public
name|TestQueueCapacities
parameter_list|(
name|String
name|suffix
parameter_list|)
block|{
name|this
operator|.
name|suffix
operator|=
name|suffix
expr_stmt|;
block|}
DECL|method|get (QueueCapacities obj, String suffix, String label)
specifier|private
specifier|static
name|float
name|get
parameter_list|(
name|QueueCapacities
name|obj
parameter_list|,
name|String
name|suffix
parameter_list|,
name|String
name|label
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|executeByName
argument_list|(
name|obj
argument_list|,
literal|"get"
operator|+
name|suffix
argument_list|,
name|label
argument_list|,
operator|-
literal|1f
argument_list|)
return|;
block|}
DECL|method|set (QueueCapacities obj, String suffix, String label, float value)
specifier|private
specifier|static
name|void
name|set
parameter_list|(
name|QueueCapacities
name|obj
parameter_list|,
name|String
name|suffix
parameter_list|,
name|String
name|label
parameter_list|,
name|float
name|value
parameter_list|)
throws|throws
name|Exception
block|{
name|executeByName
argument_list|(
name|obj
argument_list|,
literal|"set"
operator|+
name|suffix
argument_list|,
name|label
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|// Use reflection to avoid too much avoid code
DECL|method|executeByName (QueueCapacities obj, String methodName, String label, float value)
specifier|private
specifier|static
name|float
name|executeByName
parameter_list|(
name|QueueCapacities
name|obj
parameter_list|,
name|String
name|methodName
parameter_list|,
name|String
name|label
parameter_list|,
name|float
name|value
parameter_list|)
throws|throws
name|Exception
block|{
comment|// We have 4 kinds of method
comment|// 1. getXXX() : float
comment|// 2. getXXX(label) : float
comment|// 3. setXXX(float) : void
comment|// 4. setXXX(label, float) : void
if|if
condition|(
name|methodName
operator|.
name|startsWith
argument_list|(
literal|"get"
argument_list|)
condition|)
block|{
name|float
name|result
decl_stmt|;
if|if
condition|(
name|label
operator|==
literal|null
condition|)
block|{
comment|// 1.
name|Method
name|method
init|=
name|QueueCapacities
operator|.
name|class
operator|.
name|getDeclaredMethod
argument_list|(
name|methodName
argument_list|)
decl_stmt|;
name|result
operator|=
operator|(
name|float
operator|)
name|method
operator|.
name|invoke
argument_list|(
name|obj
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// 2.
name|Method
name|method
init|=
name|QueueCapacities
operator|.
name|class
operator|.
name|getDeclaredMethod
argument_list|(
name|methodName
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|result
operator|=
operator|(
name|float
operator|)
name|method
operator|.
name|invoke
argument_list|(
name|obj
argument_list|,
name|label
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
else|else
block|{
if|if
condition|(
name|label
operator|==
literal|null
condition|)
block|{
comment|// 3.
name|Method
name|method
init|=
name|QueueCapacities
operator|.
name|class
operator|.
name|getDeclaredMethod
argument_list|(
name|methodName
argument_list|,
name|Float
operator|.
name|TYPE
argument_list|)
decl_stmt|;
name|method
operator|.
name|invoke
argument_list|(
name|obj
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// 4.
name|Method
name|method
init|=
name|QueueCapacities
operator|.
name|class
operator|.
name|getDeclaredMethod
argument_list|(
name|methodName
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|Float
operator|.
name|TYPE
argument_list|)
decl_stmt|;
name|method
operator|.
name|invoke
argument_list|(
name|obj
argument_list|,
name|label
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
return|return
operator|-
literal|1f
return|;
block|}
block|}
DECL|method|internalTestModifyAndRead (String label)
specifier|private
name|void
name|internalTestModifyAndRead
parameter_list|(
name|String
name|label
parameter_list|)
throws|throws
name|Exception
block|{
name|QueueCapacities
name|qc
init|=
operator|new
name|QueueCapacities
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|// First get returns 0 always
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0f
argument_list|,
name|get
argument_list|(
name|qc
argument_list|,
name|suffix
argument_list|,
name|label
argument_list|)
argument_list|,
literal|1e-8
argument_list|)
expr_stmt|;
comment|// Set to 1, and check
name|set
argument_list|(
name|qc
argument_list|,
name|suffix
argument_list|,
name|label
argument_list|,
literal|1f
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1f
argument_list|,
name|get
argument_list|(
name|qc
argument_list|,
name|suffix
argument_list|,
name|label
argument_list|)
argument_list|,
literal|1e-8
argument_list|)
expr_stmt|;
comment|// Set to 2, and check
name|set
argument_list|(
name|qc
argument_list|,
name|suffix
argument_list|,
name|label
argument_list|,
literal|2f
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2f
argument_list|,
name|get
argument_list|(
name|qc
argument_list|,
name|suffix
argument_list|,
name|label
argument_list|)
argument_list|,
literal|1e-8
argument_list|)
expr_stmt|;
block|}
DECL|method|check (int mem, int cpu, Resource res)
name|void
name|check
parameter_list|(
name|int
name|mem
parameter_list|,
name|int
name|cpu
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|mem
argument_list|,
name|res
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|cpu
argument_list|,
name|res
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testModifyAndRead ()
specifier|public
name|void
name|testModifyAndRead
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Test - "
operator|+
name|suffix
argument_list|)
expr_stmt|;
name|internalTestModifyAndRead
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|internalTestModifyAndRead
argument_list|(
literal|"label"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

