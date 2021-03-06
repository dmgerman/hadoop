begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

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
name|math
operator|.
name|BigInteger
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
DECL|class|TestCpuTimeTracker
specifier|public
class|class
name|TestCpuTimeTracker
block|{
annotation|@
name|Test
DECL|method|test ()
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|CpuTimeTracker
name|tracker
init|=
operator|new
name|CpuTimeTracker
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|tracker
operator|.
name|updateElapsedJiffies
argument_list|(
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|100
argument_list|)
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|float
name|val1
init|=
name|tracker
operator|.
name|getCpuTrackerUsagePercent
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Not invalid CPU usage"
argument_list|,
name|val1
operator|==
operator|-
literal|1.0
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|updateElapsedJiffies
argument_list|(
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|200
argument_list|)
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|float
name|val2
init|=
name|tracker
operator|.
name|getCpuTrackerUsagePercent
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Not positive CPU usage"
argument_list|,
name|val2
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|updateElapsedJiffies
argument_list|(
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|0
argument_list|)
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|float
name|val3
init|=
name|tracker
operator|.
name|getCpuTrackerUsagePercent
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Not positive CPU usage"
argument_list|,
name|val3
operator|==
literal|0.0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

