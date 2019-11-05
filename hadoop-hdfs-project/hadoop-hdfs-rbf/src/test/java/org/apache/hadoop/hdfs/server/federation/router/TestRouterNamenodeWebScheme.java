begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.router
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|router
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|hdfs
operator|.
name|HdfsConfiguration
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|MockNamenode
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|RouterConfigBuilder
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|ActiveNamenodeResolver
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|FederationNamenodeContext
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|FileSubclusterResolver
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|MembershipNamenodeResolver
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|MountTableResolver
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
name|http
operator|.
name|HttpConfig
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
name|util
operator|.
name|StringUtils
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|FederationStateStoreTestUtils
operator|.
name|getStateStoreConfiguration
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

begin_comment
comment|/**  * Test the scheme of Http address of Namenodes displayed in Router.  * This feature is managed by {@link DFSConfigKeys#DFS_HTTP_POLICY_KEY}  */
end_comment

begin_class
DECL|class|TestRouterNamenodeWebScheme
specifier|public
class|class
name|TestRouterNamenodeWebScheme
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestRouterNamenodeWebScheme
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Router for the test. */
DECL|field|router
specifier|private
name|Router
name|router
decl_stmt|;
comment|/** Namenodes in the cluster. */
DECL|field|nns
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|MockNamenode
argument_list|>
argument_list|>
name|nns
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** Nameservices in the federated cluster. */
DECL|field|nsIds
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|nsIds
init|=
name|asList
argument_list|(
literal|"ns0"
argument_list|,
literal|"ns1"
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Initialize the Mock Namenodes to monitor"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|nsId
range|:
name|nsIds
control|)
block|{
name|nns
operator|.
name|put
argument_list|(
name|nsId
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|nnId
range|:
name|asList
argument_list|(
literal|"nn0"
argument_list|,
literal|"nn1"
argument_list|)
control|)
block|{
name|nns
operator|.
name|get
argument_list|(
name|nsId
argument_list|)
operator|.
name|put
argument_list|(
name|nnId
argument_list|,
operator|new
name|MockNamenode
argument_list|(
name|nsId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Set nn0 to active for all nameservices"
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|MockNamenode
argument_list|>
name|nnNS
range|:
name|nns
operator|.
name|values
argument_list|()
control|)
block|{
name|nnNS
operator|.
name|get
argument_list|(
literal|"nn0"
argument_list|)
operator|.
name|transitionToActive
argument_list|()
expr_stmt|;
name|nnNS
operator|.
name|get
argument_list|(
literal|"nn1"
argument_list|)
operator|.
name|transitionToStandby
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|After
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|MockNamenode
argument_list|>
name|nnNS
range|:
name|nns
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|MockNamenode
name|nn
range|:
name|nnNS
operator|.
name|values
argument_list|()
control|)
block|{
name|nn
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
name|nns
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|router
operator|!=
literal|null
condition|)
block|{
name|router
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Get the configuration of the cluster which contains all the Namenodes and    * their addresses.    * @return Configuration containing all the Namenodes.    */
DECL|method|getNamenodesConfig ()
specifier|private
name|Configuration
name|getNamenodesConfig
parameter_list|()
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMESERVICES
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|nns
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|nsId
range|:
name|nns
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|nnIds
init|=
name|nns
operator|.
name|get
argument_list|(
name|nsId
argument_list|)
operator|.
name|keySet
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_NAMENODES_KEY_PREFIX
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
operator|.
name|append
argument_list|(
name|nsId
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|nnIds
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|nnId
range|:
name|nnIds
control|)
block|{
specifier|final
name|MockNamenode
name|nn
init|=
name|nns
operator|.
name|get
argument_list|(
name|nsId
argument_list|)
operator|.
name|get
argument_list|(
name|nnId
argument_list|)
decl_stmt|;
name|sb
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_RPC_ADDRESS_KEY
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
operator|.
name|append
argument_list|(
name|nsId
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
operator|.
name|append
argument_list|(
name|nnId
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
literal|"localhost:"
operator|+
name|nn
operator|.
name|getRPCPort
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTP_ADDRESS_KEY
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
operator|.
name|append
argument_list|(
name|nsId
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
operator|.
name|append
argument_list|(
name|nnId
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
literal|"localhost:"
operator|+
name|nn
operator|.
name|getHTTPPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|conf
return|;
block|}
annotation|@
name|Test
DECL|method|testWebSchemeHttp ()
specifier|public
name|void
name|testWebSchemeHttp
parameter_list|()
throws|throws
name|IOException
block|{
name|testWebScheme
argument_list|(
name|HttpConfig
operator|.
name|Policy
operator|.
name|HTTP_ONLY
argument_list|,
literal|"http"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWebSchemeHttps ()
specifier|public
name|void
name|testWebSchemeHttps
parameter_list|()
throws|throws
name|IOException
block|{
name|testWebScheme
argument_list|(
name|HttpConfig
operator|.
name|Policy
operator|.
name|HTTPS_ONLY
argument_list|,
literal|"https"
argument_list|)
expr_stmt|;
block|}
DECL|method|testWebScheme (HttpConfig.Policy httpPolicy, String expectedScheme)
specifier|private
name|void
name|testWebScheme
parameter_list|(
name|HttpConfig
operator|.
name|Policy
name|httpPolicy
parameter_list|,
name|String
name|expectedScheme
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|nsConf
init|=
name|getNamenodesConfig
argument_list|()
decl_stmt|;
comment|// Setup the State Store for the Router to use
name|Configuration
name|stateStoreConfig
init|=
name|getStateStoreConfiguration
argument_list|()
decl_stmt|;
name|stateStoreConfig
operator|.
name|setClass
argument_list|(
name|RBFConfigKeys
operator|.
name|FEDERATION_NAMENODE_RESOLVER_CLIENT_CLASS
argument_list|,
name|MembershipNamenodeResolver
operator|.
name|class
argument_list|,
name|ActiveNamenodeResolver
operator|.
name|class
argument_list|)
expr_stmt|;
name|stateStoreConfig
operator|.
name|setClass
argument_list|(
name|RBFConfigKeys
operator|.
name|FEDERATION_FILE_RESOLVER_CLIENT_CLASS
argument_list|,
name|MountTableResolver
operator|.
name|class
argument_list|,
name|FileSubclusterResolver
operator|.
name|class
argument_list|)
expr_stmt|;
name|Configuration
name|routerConf
init|=
operator|new
name|RouterConfigBuilder
argument_list|(
name|nsConf
argument_list|)
operator|.
name|enableLocalHeartbeat
argument_list|(
literal|true
argument_list|)
operator|.
name|heartbeat
argument_list|()
operator|.
name|stateStore
argument_list|()
operator|.
name|rpc
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// set "dfs.http.policy" to "HTTPS_ONLY"
name|routerConf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HTTP_POLICY_KEY
argument_list|,
name|httpPolicy
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
comment|// Specify namenodes (ns1.nn0,ns1.nn1) to monitor
name|routerConf
operator|.
name|set
argument_list|(
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_RPC_ADDRESS_KEY
argument_list|,
literal|"0.0.0.0:0"
argument_list|)
expr_stmt|;
name|routerConf
operator|.
name|set
argument_list|(
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_MONITOR_NAMENODE
argument_list|,
literal|"ns1.nn0,ns1.nn1"
argument_list|)
expr_stmt|;
name|routerConf
operator|.
name|addResource
argument_list|(
name|stateStoreConfig
argument_list|)
expr_stmt|;
comment|// Specify local node (ns0.nn1) to monitor
name|routerConf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMESERVICE_ID
argument_list|,
literal|"ns0"
argument_list|)
expr_stmt|;
name|routerConf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_NAMENODE_ID_KEY
argument_list|,
literal|"nn1"
argument_list|)
expr_stmt|;
comment|// Start the Router with the namenodes to monitor
name|router
operator|=
operator|new
name|Router
argument_list|()
expr_stmt|;
name|router
operator|.
name|init
argument_list|(
name|routerConf
argument_list|)
expr_stmt|;
name|router
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Manually trigger the heartbeat and update the values
name|Collection
argument_list|<
name|NamenodeHeartbeatService
argument_list|>
name|heartbeatServices
init|=
name|router
operator|.
name|getNamenodeHeartbeatServices
argument_list|()
decl_stmt|;
for|for
control|(
name|NamenodeHeartbeatService
name|service
range|:
name|heartbeatServices
control|)
block|{
name|service
operator|.
name|periodicInvoke
argument_list|()
expr_stmt|;
block|}
name|MembershipNamenodeResolver
name|resolver
init|=
operator|(
name|MembershipNamenodeResolver
operator|)
name|router
operator|.
name|getNamenodeResolver
argument_list|()
decl_stmt|;
name|resolver
operator|.
name|loadCache
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Check that the webSchemes are "https"
specifier|final
name|List
argument_list|<
name|FederationNamenodeContext
argument_list|>
name|namespaceInfo
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|nsId
range|:
name|nns
operator|.
name|keySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|?
extends|extends
name|FederationNamenodeContext
argument_list|>
name|nnReports
init|=
name|resolver
operator|.
name|getNamenodesForNameserviceId
argument_list|(
name|nsId
argument_list|)
decl_stmt|;
name|namespaceInfo
operator|.
name|addAll
argument_list|(
name|nnReports
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|FederationNamenodeContext
name|nnInfo
range|:
name|namespaceInfo
control|)
block|{
name|assertEquals
argument_list|(
literal|"Unexpected scheme for Policy: "
operator|+
name|httpPolicy
operator|.
name|name
argument_list|()
argument_list|,
name|nnInfo
operator|.
name|getWebScheme
argument_list|()
argument_list|,
name|expectedScheme
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

