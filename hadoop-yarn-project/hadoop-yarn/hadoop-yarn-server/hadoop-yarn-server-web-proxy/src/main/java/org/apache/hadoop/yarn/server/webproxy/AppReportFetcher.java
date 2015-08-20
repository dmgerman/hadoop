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
name|ipc
operator|.
name|RPC
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
name|client
operator|.
name|AHSProxy
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
name|client
operator|.
name|ClientRMProxy
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

begin_comment
comment|/**  * This class abstracts away how ApplicationReports are fetched.  */
end_comment

begin_class
DECL|class|AppReportFetcher
specifier|public
class|class
name|AppReportFetcher
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
name|AppReportFetcher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|applicationsManager
specifier|private
specifier|final
name|ApplicationClientProtocol
name|applicationsManager
decl_stmt|;
DECL|field|historyManager
specifier|private
specifier|final
name|ApplicationHistoryProtocol
name|historyManager
decl_stmt|;
DECL|field|recordFactory
specifier|private
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
DECL|field|isAHSEnabled
specifier|private
name|boolean
name|isAHSEnabled
decl_stmt|;
comment|/**    * Create a new Connection to the RM/Application History Server    * to fetch Application reports.    * @param conf the conf to use to know where the RM is.    */
DECL|method|AppReportFetcher (Configuration conf)
specifier|public
name|AppReportFetcher
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|APPLICATION_HISTORY_ENABLED
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_APPLICATION_HISTORY_ENABLED
argument_list|)
condition|)
block|{
name|isAHSEnabled
operator|=
literal|true
expr_stmt|;
block|}
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
try|try
block|{
name|applicationsManager
operator|=
name|ClientRMProxy
operator|.
name|createRMProxy
argument_list|(
name|conf
argument_list|,
name|ApplicationClientProtocol
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
name|isAHSEnabled
condition|)
block|{
name|historyManager
operator|=
name|getAHSProxy
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|historyManager
operator|=
literal|null
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Create a direct connection to RM instead of a remote connection when    * the proxy is running as part of the RM. Also create a remote connection to    * Application History Server if it is enabled.    * @param conf the configuration to use    * @param applicationsManager what to use to get the RM reports.    */
DECL|method|AppReportFetcher (Configuration conf, ApplicationClientProtocol applicationsManager)
specifier|public
name|AppReportFetcher
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ApplicationClientProtocol
name|applicationsManager
parameter_list|)
block|{
if|if
condition|(
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|APPLICATION_HISTORY_ENABLED
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_APPLICATION_HISTORY_ENABLED
argument_list|)
condition|)
block|{
name|isAHSEnabled
operator|=
literal|true
expr_stmt|;
block|}
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|applicationsManager
operator|=
name|applicationsManager
expr_stmt|;
if|if
condition|(
name|isAHSEnabled
condition|)
block|{
try|try
block|{
name|historyManager
operator|=
name|getAHSProxy
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|this
operator|.
name|historyManager
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|getAHSProxy (Configuration configuration)
specifier|protected
name|ApplicationHistoryProtocol
name|getAHSProxy
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|AHSProxy
operator|.
name|createAHSProxy
argument_list|(
name|configuration
argument_list|,
name|ApplicationHistoryProtocol
operator|.
name|class
argument_list|,
name|configuration
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_SERVICE_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_SERVICE_PORT
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Get an application report for the specified application id from the RM and    * fall back to the Application History Server if not found in RM.    * @param appId id of the application to get.    * @return the ApplicationReport for the appId.    * @throws YarnException on any error.    * @throws IOException    */
DECL|method|getApplicationReport (ApplicationId appId)
specifier|public
name|ApplicationReport
name|getApplicationReport
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|GetApplicationReportRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetApplicationReportRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setApplicationId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|GetApplicationReportResponse
name|response
decl_stmt|;
try|try
block|{
name|response
operator|=
name|applicationsManager
operator|.
name|getApplicationReport
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isAHSEnabled
condition|)
block|{
comment|// Just throw it as usual if historyService is not enabled.
throw|throw
name|e
throw|;
block|}
comment|// Even if history-service is enabled, treat all exceptions still the same
comment|// except the following
if|if
condition|(
operator|!
operator|(
name|e
operator|.
name|getClass
argument_list|()
operator|==
name|ApplicationNotFoundException
operator|.
name|class
operator|)
condition|)
block|{
throw|throw
name|e
throw|;
block|}
name|response
operator|=
name|historyManager
operator|.
name|getApplicationReport
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
return|return
name|response
operator|.
name|getApplicationReport
argument_list|()
return|;
block|}
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|applicationsManager
operator|!=
literal|null
condition|)
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|this
operator|.
name|applicationsManager
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|historyManager
operator|!=
literal|null
condition|)
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|this
operator|.
name|historyManager
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

