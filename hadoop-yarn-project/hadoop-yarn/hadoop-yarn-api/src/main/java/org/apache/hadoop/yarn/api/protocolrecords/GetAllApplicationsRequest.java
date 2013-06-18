begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.protocolrecords
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
operator|.
name|protocolrecords
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Public
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Stable
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
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  *<p>The request from clients to get a report of all Applications  * in the cluster from the<code>ResourceManager</code>.</p>  *  *<p>Currently, this is empty.</p>  *   * @see ApplicationClientProtocol#getAllApplications(GetAllApplicationsRequest)  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|GetAllApplicationsRequest
specifier|public
specifier|abstract
class|class
name|GetAllApplicationsRequest
block|{
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|newInstance ()
specifier|public
specifier|static
name|GetAllApplicationsRequest
name|newInstance
parameter_list|()
block|{
name|GetAllApplicationsRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|GetAllApplicationsRequest
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|request
return|;
block|}
block|}
end_class

end_unit

