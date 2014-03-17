begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
package|;
end_package

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
name|ApplicationAttemptId
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
name|ApplicationId
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
name|ContainerId
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

begin_class
DECL|class|TestContainerId
specifier|public
class|class
name|TestContainerId
block|{
annotation|@
name|Test
DECL|method|testContainerId ()
specifier|public
name|void
name|testContainerId
parameter_list|()
block|{
name|ContainerId
name|c1
init|=
name|newContainerId
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|10l
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ContainerId
name|c2
init|=
name|newContainerId
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|10l
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|ContainerId
name|c3
init|=
name|newContainerId
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|10l
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ContainerId
name|c4
init|=
name|newContainerId
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|,
literal|10l
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ContainerId
name|c5
init|=
name|newContainerId
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|,
literal|8l
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|c1
operator|.
name|equals
argument_list|(
name|c3
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|c1
operator|.
name|equals
argument_list|(
name|c2
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|c1
operator|.
name|equals
argument_list|(
name|c4
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|c1
operator|.
name|equals
argument_list|(
name|c5
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|c1
operator|.
name|compareTo
argument_list|(
name|c3
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|c1
operator|.
name|compareTo
argument_list|(
name|c2
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|c1
operator|.
name|compareTo
argument_list|(
name|c4
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|c1
operator|.
name|compareTo
argument_list|(
name|c5
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|c1
operator|.
name|hashCode
argument_list|()
operator|==
name|c3
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|c1
operator|.
name|hashCode
argument_list|()
operator|==
name|c2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|c1
operator|.
name|hashCode
argument_list|()
operator|==
name|c4
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|c1
operator|.
name|hashCode
argument_list|()
operator|==
name|c5
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|ts
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|ContainerId
name|c6
init|=
name|newContainerId
argument_list|(
literal|36473
argument_list|,
literal|4365472
argument_list|,
name|ts
argument_list|,
literal|25645811
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"container_10_0001_01_000001"
argument_list|,
name|c1
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"container_"
operator|+
name|ts
operator|+
literal|"_36473_4365472_25645811"
argument_list|,
name|c6
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|newContainerId (int appId, int appAttemptId, long timestamp, int containerId)
specifier|public
specifier|static
name|ContainerId
name|newContainerId
parameter_list|(
name|int
name|appId
parameter_list|,
name|int
name|appAttemptId
parameter_list|,
name|long
name|timestamp
parameter_list|,
name|int
name|containerId
parameter_list|)
block|{
name|ApplicationId
name|applicationId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|timestamp
argument_list|,
name|appId
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|applicationAttemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|applicationId
argument_list|,
name|appAttemptId
argument_list|)
decl_stmt|;
return|return
name|ContainerId
operator|.
name|newInstance
argument_list|(
name|applicationAttemptId
argument_list|,
name|containerId
argument_list|)
return|;
block|}
block|}
end_class

end_unit

