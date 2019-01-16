begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|NameNodeRpcServer
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
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
name|lang3
operator|.
name|ClassUtils
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
name|protocolPB
operator|.
name|RouterPolicyProvider
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
name|datanode
operator|.
name|DataNode
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
name|authorize
operator|.
name|Service
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|TestName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
import|;
end_import

begin_comment
comment|/**  * Test suite covering RouterPolicyProvider. We expect that it contains a  * security policy definition for every RPC protocol used in HDFS. The test  * suite works by scanning an RPC server's class to find the protocol interfaces  * it implements, and then comparing that to the protocol interfaces covered in  * RouterPolicyProvider. This is a parameterized test repeated for multiple HDFS  * RPC server classes.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|TestRouterPolicyProvider
specifier|public
class|class
name|TestRouterPolicyProvider
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
name|TestRouterPolicyProvider
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|policyProviderProtocols
specifier|private
specifier|static
name|Set
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|policyProviderProtocols
decl_stmt|;
annotation|@
name|Rule
DECL|field|testName
specifier|public
name|TestName
name|testName
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
DECL|field|rpcServerClass
specifier|private
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|rpcServerClass
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|initialize ()
specifier|public
specifier|static
name|void
name|initialize
parameter_list|()
block|{
name|Service
index|[]
name|services
init|=
operator|new
name|RouterPolicyProvider
argument_list|()
operator|.
name|getServices
argument_list|()
decl_stmt|;
name|policyProviderProtocols
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|services
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|Service
name|service
range|:
name|services
control|)
block|{
name|policyProviderProtocols
operator|.
name|add
argument_list|(
name|service
operator|.
name|getProtocol
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|TestRouterPolicyProvider (Class<?> rpcServerClass)
specifier|public
name|TestRouterPolicyProvider
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|rpcServerClass
parameter_list|)
block|{
name|this
operator|.
name|rpcServerClass
operator|=
name|rpcServerClass
expr_stmt|;
block|}
annotation|@
name|Parameters
argument_list|(
name|name
operator|=
literal|"protocolsForServer-{0}"
argument_list|)
DECL|method|data ()
specifier|public
specifier|static
name|List
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
index|[]
argument_list|>
name|data
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Class
argument_list|<
name|?
argument_list|>
index|[]
index|[]
block|{
block|{
name|RouterRpcServer
operator|.
name|class
block|}
operator|,
block|{
name|NameNodeRpcServer
operator|.
name|class
block|}
operator|,
block|{
name|DataNode
operator|.
name|class
block|}
operator|,
block|{
name|RouterAdminServer
operator|.
name|class
block|}
block|}
block|)
function|;
block|}
end_class

begin_function
annotation|@
name|Test
DECL|method|testPolicyProviderForServer ()
specifier|public
name|void
name|testPolicyProviderForServer
parameter_list|()
block|{
name|List
argument_list|<
name|?
argument_list|>
name|ifaces
init|=
name|ClassUtils
operator|.
name|getAllInterfaces
argument_list|(
name|rpcServerClass
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|serverProtocols
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|ifaces
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|obj
range|:
name|ifaces
control|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|iface
init|=
operator|(
name|Class
argument_list|<
name|?
argument_list|>
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|iface
operator|.
name|getSimpleName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"Protocol"
argument_list|)
condition|)
block|{
name|serverProtocols
operator|.
name|add
argument_list|(
name|iface
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Running test {} for RPC server {}.  Found server protocols {} "
operator|+
literal|"and policy provider protocols {}."
argument_list|,
name|testName
operator|.
name|getMethodName
argument_list|()
argument_list|,
name|rpcServerClass
operator|.
name|getName
argument_list|()
argument_list|,
name|serverProtocols
argument_list|,
name|policyProviderProtocols
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Expected to find at least one protocol in server."
argument_list|,
name|serverProtocols
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Set
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|differenceSet
init|=
name|Sets
operator|.
name|difference
argument_list|(
name|serverProtocols
argument_list|,
name|policyProviderProtocols
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Following protocols for server %s are not defined in "
operator|+
literal|"%s: %s"
argument_list|,
name|rpcServerClass
operator|.
name|getName
argument_list|()
argument_list|,
name|RouterPolicyProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
name|differenceSet
operator|.
name|toArray
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|differenceSet
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

unit|}
end_unit

