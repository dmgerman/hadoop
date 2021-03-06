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
name|ApplicationReport
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
name|FinalApplicationStatus
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
name|Priority
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
name|YarnApplicationState
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
name|conf
operator|.
name|YarnConfiguration
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

begin_class
DECL|class|TestApplicatonReport
specifier|public
class|class
name|TestApplicatonReport
block|{
annotation|@
name|Test
DECL|method|testApplicationReport ()
specifier|public
name|void
name|testApplicationReport
parameter_list|()
block|{
name|long
name|timestamp
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|ApplicationReport
name|appReport1
init|=
name|createApplicationReport
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
name|timestamp
argument_list|)
decl_stmt|;
name|ApplicationReport
name|appReport2
init|=
name|createApplicationReport
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
name|timestamp
argument_list|)
decl_stmt|;
name|ApplicationReport
name|appReport3
init|=
name|createApplicationReport
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
name|timestamp
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appReport1
argument_list|,
name|appReport2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appReport2
argument_list|,
name|appReport3
argument_list|)
expr_stmt|;
name|appReport1
operator|.
name|setApplicationId
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|appReport1
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotSame
argument_list|(
name|appReport1
argument_list|,
name|appReport2
argument_list|)
expr_stmt|;
name|appReport2
operator|.
name|setCurrentApplicationAttemptId
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|appReport2
operator|.
name|getCurrentApplicationAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotSame
argument_list|(
name|appReport2
argument_list|,
name|appReport3
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|appReport1
operator|.
name|getAMRMToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createApplicationReport ( int appIdInt, int appAttemptIdInt, long timestamp)
specifier|protected
specifier|static
name|ApplicationReport
name|createApplicationReport
parameter_list|(
name|int
name|appIdInt
parameter_list|,
name|int
name|appAttemptIdInt
parameter_list|,
name|long
name|timestamp
parameter_list|)
block|{
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|timestamp
argument_list|,
name|appIdInt
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
name|appAttemptIdInt
argument_list|)
decl_stmt|;
name|ApplicationReport
name|appReport
init|=
name|ApplicationReport
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
name|appAttemptId
argument_list|,
literal|"user"
argument_list|,
literal|"queue"
argument_list|,
literal|"appname"
argument_list|,
literal|"host"
argument_list|,
literal|124
argument_list|,
literal|null
argument_list|,
name|YarnApplicationState
operator|.
name|FINISHED
argument_list|,
literal|"diagnostics"
argument_list|,
literal|"url"
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|FinalApplicationStatus
operator|.
name|SUCCEEDED
argument_list|,
literal|null
argument_list|,
literal|"N/A"
argument_list|,
literal|0.53789f
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_APPLICATION_TYPE
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
return|return
name|appReport
return|;
block|}
block|}
end_class

end_unit

