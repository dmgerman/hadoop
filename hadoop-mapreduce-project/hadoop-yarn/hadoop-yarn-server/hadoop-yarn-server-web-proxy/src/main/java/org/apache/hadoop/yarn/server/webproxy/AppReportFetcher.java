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
name|net
operator|.
name|InetSocketAddress
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
name|net
operator|.
name|NetUtils
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
name|ClientRMProtocol
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
name|YarnRemoteException
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
name|ipc
operator|.
name|YarnRPC
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
name|ClientRMProtocol
name|applicationsManager
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
comment|/**    * Create a new Connection to the RM to fetch Application reports.    * @param conf the conf to use to know where the RM is.    */
DECL|method|AppReportFetcher (Configuration conf)
specifier|public
name|AppReportFetcher
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|this
operator|.
name|conf
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|rmAddress
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|this
operator|.
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_ADDRESS
argument_list|)
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Connecting to ResourceManager at "
operator|+
name|rmAddress
argument_list|)
expr_stmt|;
name|applicationsManager
operator|=
operator|(
name|ClientRMProtocol
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|ClientRMProtocol
operator|.
name|class
argument_list|,
name|rmAddress
argument_list|,
name|this
operator|.
name|conf
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Connected to ResourceManager at "
operator|+
name|rmAddress
argument_list|)
expr_stmt|;
block|}
comment|/**    * Just call directly into the applicationsManager given instead of creating    * a remote connection to it.  This is mostly for when the Proxy is running    * as part of the RM already.    * @param conf the configuration to use    * @param applicationsManager what to use to get the RM reports.    */
DECL|method|AppReportFetcher (Configuration conf, ClientRMProtocol applicationsManager)
specifier|public
name|AppReportFetcher
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ClientRMProtocol
name|applicationsManager
parameter_list|)
block|{
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
block|}
comment|/**    * Get a report for the specified app.    * @param appId the id of the application to get.     * @return the ApplicationReport for that app.    * @throws YarnRemoteException on any error.    */
DECL|method|getApplicationReport (ApplicationId appId)
specifier|public
name|ApplicationReport
name|getApplicationReport
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|YarnRemoteException
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
init|=
name|applicationsManager
operator|.
name|getApplicationReport
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
name|response
operator|.
name|getApplicationReport
argument_list|()
return|;
block|}
block|}
end_class

end_unit

