begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.logaggregation
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
name|containermanager
operator|.
name|logaggregation
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
name|security
operator|.
name|Credentials
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
name|security
operator|.
name|UserGroupInformation
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
name|api
operator|.
name|ContainerLogContext
import|;
end_import

begin_interface
DECL|interface|AppLogAggregator
specifier|public
interface|interface
name|AppLogAggregator
extends|extends
name|Runnable
block|{
DECL|method|startContainerLogAggregation (ContainerLogContext logContext)
name|void
name|startContainerLogAggregation
parameter_list|(
name|ContainerLogContext
name|logContext
parameter_list|)
function_decl|;
DECL|method|abortLogAggregation ()
name|void
name|abortLogAggregation
parameter_list|()
function_decl|;
DECL|method|finishLogAggregation ()
name|void
name|finishLogAggregation
parameter_list|()
function_decl|;
DECL|method|disableLogAggregation ()
name|void
name|disableLogAggregation
parameter_list|()
function_decl|;
DECL|method|enableLogAggregation ()
name|void
name|enableLogAggregation
parameter_list|()
function_decl|;
DECL|method|isAggregationEnabled ()
name|boolean
name|isAggregationEnabled
parameter_list|()
function_decl|;
DECL|method|updateCredentials (Credentials cred)
name|UserGroupInformation
name|updateCredentials
parameter_list|(
name|Credentials
name|cred
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

