begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.router.clientrm
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
name|router
operator|.
name|clientrm
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|server
operator|.
name|MockResourceManagerFacade
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
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|SubClusterId
import|;
end_import

begin_comment
comment|/**  * Extends the FederationClientInterceptor and overrides methods to provide a  * testable implementation of FederationClientInterceptor.  */
end_comment

begin_class
DECL|class|TestableFederationClientInterceptor
specifier|public
class|class
name|TestableFederationClientInterceptor
extends|extends
name|FederationClientInterceptor
block|{
DECL|field|mockRMs
specifier|private
name|ConcurrentHashMap
argument_list|<
name|SubClusterId
argument_list|,
name|MockResourceManagerFacade
argument_list|>
name|mockRMs
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|badSubCluster
specifier|private
name|List
argument_list|<
name|SubClusterId
argument_list|>
name|badSubCluster
init|=
operator|new
name|ArrayList
argument_list|<
name|SubClusterId
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getClientRMProxyForSubCluster ( SubClusterId subClusterId)
specifier|protected
name|ApplicationClientProtocol
name|getClientRMProxyForSubCluster
parameter_list|(
name|SubClusterId
name|subClusterId
parameter_list|)
throws|throws
name|YarnException
block|{
name|MockResourceManagerFacade
name|mockRM
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|mockRMs
operator|.
name|containsKey
argument_list|(
name|subClusterId
argument_list|)
condition|)
block|{
name|mockRM
operator|=
name|mockRMs
operator|.
name|get
argument_list|(
name|subClusterId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mockRM
operator|=
operator|new
name|MockResourceManagerFacade
argument_list|(
name|super
operator|.
name|getConf
argument_list|()
argument_list|,
literal|0
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|subClusterId
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
operator|!
name|badSubCluster
operator|.
name|contains
argument_list|(
name|subClusterId
argument_list|)
argument_list|)
expr_stmt|;
name|mockRMs
operator|.
name|put
argument_list|(
name|subClusterId
argument_list|,
name|mockRM
argument_list|)
expr_stmt|;
block|}
return|return
name|mockRM
return|;
block|}
block|}
comment|/**    * For testing purpose, some subclusters has to be down to simulate particular    * scenarios as RM Failover, network issues. For this reason we keep track of    * these bad subclusters. This method make the subcluster unusable.    *    * @param badSC the subcluster to make unusable    */
DECL|method|registerBadSubCluster (SubClusterId badSC)
specifier|protected
name|void
name|registerBadSubCluster
parameter_list|(
name|SubClusterId
name|badSC
parameter_list|)
block|{
name|badSubCluster
operator|.
name|add
argument_list|(
name|badSC
argument_list|)
expr_stmt|;
if|if
condition|(
name|mockRMs
operator|.
name|contains
argument_list|(
name|badSC
argument_list|)
condition|)
block|{
name|mockRMs
operator|.
name|get
argument_list|(
name|badSC
argument_list|)
operator|.
name|setRunningMode
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

