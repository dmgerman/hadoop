begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.applicationsmanager
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
name|applicationsmanager
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
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
name|protocolrecords
operator|.
name|AllocateRequest
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
name|AMResponse
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|ApplicationMasterService
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
name|ClientRMService
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
name|MockAM
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
name|MockNM
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
name|MockRM
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
name|RMContext
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
name|rmapp
operator|.
name|RMApp
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
name|rmapp
operator|.
name|attempt
operator|.
name|RMAppAttempt
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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

begin_class
DECL|class|TestAMRMRPCResponseId
specifier|public
class|class
name|TestAMRMRPCResponseId
block|{
DECL|field|recordFactory
specifier|private
specifier|static
specifier|final
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
DECL|field|rm
specifier|private
name|MockRM
name|rm
decl_stmt|;
DECL|field|amService
name|ApplicationMasterService
name|amService
init|=
literal|null
decl_stmt|;
DECL|field|clientService
specifier|private
name|ClientRMService
name|clientService
decl_stmt|;
DECL|field|context
specifier|private
name|RMContext
name|context
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|this
operator|.
name|rm
operator|=
operator|new
name|MockRM
argument_list|()
expr_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|this
operator|.
name|clientService
operator|=
name|rm
operator|.
name|getClientRMService
argument_list|()
expr_stmt|;
name|amService
operator|=
name|rm
operator|.
name|getApplicationMasterService
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
if|if
condition|(
name|rm
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testARRMResponseId ()
specifier|public
name|void
name|testARRMResponseId
parameter_list|()
throws|throws
name|Exception
block|{
name|MockNM
name|nm1
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"h1:1234"
argument_list|,
literal|5000
argument_list|)
decl_stmt|;
name|RMApp
name|app
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
comment|// Trigger the scheduling so the AM gets 'launched'
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|RMAppAttempt
name|attempt
init|=
name|app
operator|.
name|getCurrentAppAttempt
argument_list|()
decl_stmt|;
name|MockAM
name|am
init|=
name|rm
operator|.
name|sendAMLaunched
argument_list|(
name|attempt
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
decl_stmt|;
name|am
operator|.
name|registerAppAttempt
argument_list|()
expr_stmt|;
name|AllocateRequest
name|allocateRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|AllocateRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|allocateRequest
operator|.
name|setApplicationAttemptId
argument_list|(
name|attempt
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|AMResponse
name|response
init|=
name|amService
operator|.
name|allocate
argument_list|(
name|allocateRequest
argument_list|)
operator|.
name|getAMResponse
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|response
operator|.
name|getResponseId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|response
operator|.
name|getReboot
argument_list|()
argument_list|)
expr_stmt|;
name|allocateRequest
operator|.
name|setResponseId
argument_list|(
name|response
operator|.
name|getResponseId
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|=
name|amService
operator|.
name|allocate
argument_list|(
name|allocateRequest
argument_list|)
operator|.
name|getAMResponse
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|response
operator|.
name|getResponseId
argument_list|()
argument_list|)
expr_stmt|;
comment|/* try resending */
name|response
operator|=
name|amService
operator|.
name|allocate
argument_list|(
name|allocateRequest
argument_list|)
operator|.
name|getAMResponse
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|response
operator|.
name|getResponseId
argument_list|()
argument_list|)
expr_stmt|;
comment|/** try sending old **/
name|allocateRequest
operator|.
name|setResponseId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|response
operator|=
name|amService
operator|.
name|allocate
argument_list|(
name|allocateRequest
argument_list|)
operator|.
name|getAMResponse
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|response
operator|.
name|getReboot
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

