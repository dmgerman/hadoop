begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.health
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
name|nodemanager
operator|.
name|health
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
import|import static
name|org
operator|.
name|assertj
operator|.
name|core
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertThat
import|;
end_import

begin_comment
comment|/**  * Tests for the {@link ExceptionReporter} class.  */
end_comment

begin_class
DECL|class|TestExceptionReporter
specifier|public
class|class
name|TestExceptionReporter
block|{
annotation|@
name|Test
DECL|method|testUnhealthy ()
specifier|public
name|void
name|testUnhealthy
parameter_list|()
block|{
name|ExceptionReporter
name|reporter
init|=
operator|new
name|ExceptionReporter
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|reporter
operator|.
name|isHealthy
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|reporter
operator|.
name|getLastHealthReportTime
argument_list|()
argument_list|)
operator|.
name|isZero
argument_list|()
expr_stmt|;
name|String
name|message
init|=
literal|"test"
decl_stmt|;
name|Exception
name|exception
init|=
operator|new
name|Exception
argument_list|(
name|message
argument_list|)
decl_stmt|;
name|reporter
operator|.
name|reportException
argument_list|(
name|exception
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|reporter
operator|.
name|isHealthy
argument_list|()
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|reporter
operator|.
name|getHealthReport
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|reporter
operator|.
name|getLastHealthReportTime
argument_list|()
argument_list|)
operator|.
name|isNotEqualTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

