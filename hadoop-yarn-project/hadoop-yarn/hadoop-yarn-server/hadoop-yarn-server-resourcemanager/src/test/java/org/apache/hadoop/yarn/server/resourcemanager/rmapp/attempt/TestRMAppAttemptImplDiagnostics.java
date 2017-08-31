begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt
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
name|rmapp
operator|.
name|attempt
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|RandomStringUtils
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
name|conf
operator|.
name|Configuration
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
name|conf
operator|.
name|YarnConfiguration
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
name|event
operator|.
name|Dispatcher
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
name|exceptions
operator|.
name|YarnRuntimeException
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
name|util
operator|.
name|BoundedAppender
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|rules
operator|.
name|ExpectedException
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
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_comment
comment|/**  * Testing {@link RMAppAttemptImpl#diagnostics} scenarios.  */
end_comment

begin_class
DECL|class|TestRMAppAttemptImplDiagnostics
specifier|public
class|class
name|TestRMAppAttemptImplDiagnostics
block|{
annotation|@
name|Rule
DECL|field|expectedException
specifier|public
name|ExpectedException
name|expectedException
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|whenCreatedWithDefaultConfigurationSuccess ()
specifier|public
name|void
name|whenCreatedWithDefaultConfigurationSuccess
parameter_list|()
block|{
specifier|final
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|configuration
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|APP_ATTEMPT_DIAGNOSTICS_LIMIT_KC
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_APP_ATTEMPT_DIAGNOSTICS_LIMIT_KC
argument_list|)
expr_stmt|;
name|createRMAppAttemptImpl
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|whenCreatedWithWrongConfigurationError ()
specifier|public
name|void
name|whenCreatedWithWrongConfigurationError
parameter_list|()
block|{
specifier|final
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|configuration
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|APP_ATTEMPT_DIAGNOSTICS_LIMIT_KC
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|expectedException
operator|.
name|expect
argument_list|(
name|YarnRuntimeException
operator|.
name|class
argument_list|)
expr_stmt|;
name|createRMAppAttemptImpl
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|whenAppendedWithinLimitMessagesArePreserved ()
specifier|public
name|void
name|whenAppendedWithinLimitMessagesArePreserved
parameter_list|()
block|{
specifier|final
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|configuration
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|APP_ATTEMPT_DIAGNOSTICS_LIMIT_KC
argument_list|,
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|RMAppAttemptImpl
name|appAttempt
init|=
name|createRMAppAttemptImpl
argument_list|(
name|configuration
argument_list|)
decl_stmt|;
specifier|final
name|String
name|withinLimit
init|=
name|RandomStringUtils
operator|.
name|random
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
name|appAttempt
operator|.
name|appendDiagnostics
argument_list|(
name|withinLimit
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"messages within limit should be preserved"
argument_list|,
name|withinLimit
argument_list|,
name|appAttempt
operator|.
name|getDiagnostics
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|whenAppendedBeyondLimitMessagesAreTruncated ()
specifier|public
name|void
name|whenAppendedBeyondLimitMessagesAreTruncated
parameter_list|()
block|{
specifier|final
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|configuration
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|APP_ATTEMPT_DIAGNOSTICS_LIMIT_KC
argument_list|,
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|RMAppAttemptImpl
name|appAttempt
init|=
name|createRMAppAttemptImpl
argument_list|(
name|configuration
argument_list|)
decl_stmt|;
specifier|final
name|String
name|beyondLimit
init|=
name|RandomStringUtils
operator|.
name|random
argument_list|(
literal|1025
argument_list|)
decl_stmt|;
name|appAttempt
operator|.
name|appendDiagnostics
argument_list|(
name|beyondLimit
argument_list|)
expr_stmt|;
specifier|final
name|String
name|truncated
init|=
name|String
operator|.
name|format
argument_list|(
name|BoundedAppender
operator|.
name|TRUNCATED_MESSAGES_TEMPLATE
argument_list|,
literal|1024
argument_list|,
literal|1025
argument_list|,
name|beyondLimit
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"messages beyond limit should be truncated"
argument_list|,
name|truncated
argument_list|,
name|appAttempt
operator|.
name|getDiagnostics
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createRMAppAttemptImpl ( final Configuration configuration)
specifier|private
name|RMAppAttemptImpl
name|createRMAppAttemptImpl
parameter_list|(
specifier|final
name|Configuration
name|configuration
parameter_list|)
block|{
specifier|final
name|ApplicationAttemptId
name|mockApplicationAttemptId
init|=
name|mock
argument_list|(
name|ApplicationAttemptId
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|ApplicationId
name|mockApplicationId
init|=
name|mock
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockApplicationAttemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockApplicationId
argument_list|)
expr_stmt|;
specifier|final
name|RMContext
name|mockRMContext
init|=
name|mock
argument_list|(
name|RMContext
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|Dispatcher
name|mockDispatcher
init|=
name|mock
argument_list|(
name|Dispatcher
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockRMContext
operator|.
name|getDispatcher
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockDispatcher
argument_list|)
expr_stmt|;
return|return
operator|new
name|RMAppAttemptImpl
argument_list|(
name|mockApplicationAttemptId
argument_list|,
name|mockRMContext
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|configuration
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

