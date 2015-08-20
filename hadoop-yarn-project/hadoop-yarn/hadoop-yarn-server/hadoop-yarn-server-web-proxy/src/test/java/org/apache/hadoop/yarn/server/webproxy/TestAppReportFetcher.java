begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.webproxy
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
name|webproxy
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|ApplicationClientProtocol
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
name|ApplicationHistoryProtocol
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
name|GetApplicationReportRequest
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
name|GetApplicationReportResponse
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
name|exceptions
operator|.
name|ApplicationNotFoundException
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
name|YarnException
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
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_class
DECL|class|TestAppReportFetcher
specifier|public
class|class
name|TestAppReportFetcher
block|{
DECL|field|historyManager
specifier|static
name|ApplicationHistoryProtocol
name|historyManager
decl_stmt|;
DECL|field|conf
specifier|static
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|appManager
specifier|private
specifier|static
name|ApplicationClientProtocol
name|appManager
decl_stmt|;
DECL|field|fetcher
specifier|private
specifier|static
name|AppReportFetcher
name|fetcher
decl_stmt|;
DECL|field|appNotFoundExceptionMsg
specifier|private
specifier|final
name|String
name|appNotFoundExceptionMsg
init|=
literal|"APP NOT FOUND"
decl_stmt|;
annotation|@
name|After
DECL|method|cleanUp ()
specifier|public
name|void
name|cleanUp
parameter_list|()
block|{
name|historyManager
operator|=
literal|null
expr_stmt|;
name|appManager
operator|=
literal|null
expr_stmt|;
name|fetcher
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|testHelper (boolean isAHSEnabled)
specifier|public
name|void
name|testHelper
parameter_list|(
name|boolean
name|isAHSEnabled
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|APPLICATION_HISTORY_ENABLED
argument_list|,
name|isAHSEnabled
argument_list|)
expr_stmt|;
name|appManager
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|ApplicationClientProtocol
operator|.
name|class
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|appManager
operator|.
name|getApplicationReport
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|GetApplicationReportRequest
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenThrow
argument_list|(
operator|new
name|ApplicationNotFoundException
argument_list|(
name|appNotFoundExceptionMsg
argument_list|)
argument_list|)
expr_stmt|;
name|fetcher
operator|=
operator|new
name|AppReportFetcherForTest
argument_list|(
name|conf
argument_list|,
name|appManager
argument_list|)
expr_stmt|;
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|fetcher
operator|.
name|getApplicationReport
argument_list|(
name|appId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFetchReportAHSEnabled ()
specifier|public
name|void
name|testFetchReportAHSEnabled
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|testHelper
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|historyManager
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|getApplicationReport
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|GetApplicationReportRequest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|appManager
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|getApplicationReport
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|GetApplicationReportRequest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFetchReportAHSDisabled ()
specifier|public
name|void
name|testFetchReportAHSDisabled
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
block|{
try|try
block|{
name|testHelper
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ApplicationNotFoundException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|==
name|appNotFoundExceptionMsg
argument_list|)
expr_stmt|;
comment|/* RM will not know of the app and Application History Service is disabled        * So we will not try to get the report from AHS and RM will throw        * ApplicationNotFoundException        */
block|}
name|Mockito
operator|.
name|verify
argument_list|(
name|appManager
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|getApplicationReport
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|GetApplicationReportRequest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|historyManager
operator|!=
literal|null
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"HistoryManager should be null as AHS is disabled"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|AppReportFetcherForTest
specifier|static
class|class
name|AppReportFetcherForTest
extends|extends
name|AppReportFetcher
block|{
DECL|method|AppReportFetcherForTest (Configuration conf, ApplicationClientProtocol acp)
specifier|public
name|AppReportFetcherForTest
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ApplicationClientProtocol
name|acp
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|,
name|acp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAHSProxy (Configuration conf)
specifier|protected
name|ApplicationHistoryProtocol
name|getAHSProxy
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|GetApplicationReportResponse
name|resp
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|GetApplicationReportResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|historyManager
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|ApplicationHistoryProtocol
operator|.
name|class
argument_list|)
expr_stmt|;
try|try
block|{
name|Mockito
operator|.
name|when
argument_list|(
name|historyManager
operator|.
name|getApplicationReport
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|GetApplicationReportRequest
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|resp
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
comment|// TODO Auto-generated catch block
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
return|return
name|historyManager
return|;
block|}
block|}
block|}
end_class

end_unit

