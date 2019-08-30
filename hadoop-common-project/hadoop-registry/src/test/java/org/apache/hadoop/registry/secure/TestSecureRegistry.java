begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.secure
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|registry
operator|.
name|secure
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
name|service
operator|.
name|ServiceOperations
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
name|registry
operator|.
name|client
operator|.
name|impl
operator|.
name|zk
operator|.
name|ZKPathDumper
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
name|registry
operator|.
name|client
operator|.
name|impl
operator|.
name|zk
operator|.
name|CuratorService
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
name|registry
operator|.
name|client
operator|.
name|impl
operator|.
name|zk
operator|.
name|RegistrySecurity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|CreateMode
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
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginContext
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
name|registry
operator|.
name|client
operator|.
name|api
operator|.
name|RegistryConstants
operator|.
name|*
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
name|registry
operator|.
name|client
operator|.
name|impl
operator|.
name|zk
operator|.
name|ZookeeperConfigOptions
operator|.
name|PROP_ZK_SASL_CLIENT_CONTEXT
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
name|registry
operator|.
name|client
operator|.
name|impl
operator|.
name|zk
operator|.
name|ZookeeperConfigOptions
operator|.
name|PROP_ZK_SASL_CLIENT_USERNAME
import|;
end_import

begin_comment
comment|/**  * Verify that the Mini ZK service can be started up securely  */
end_comment

begin_class
DECL|class|TestSecureRegistry
specifier|public
class|class
name|TestSecureRegistry
extends|extends
name|AbstractSecureRegistryTest
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
name|TestSecureRegistry
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|beforeTestSecureZKService ()
specifier|public
name|void
name|beforeTestSecureZKService
parameter_list|()
throws|throws
name|Throwable
block|{
name|enableKerberosDebugging
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|afterTestSecureZKService ()
specifier|public
name|void
name|afterTestSecureZKService
parameter_list|()
throws|throws
name|Throwable
block|{
name|disableKerberosDebugging
argument_list|()
expr_stmt|;
name|RegistrySecurity
operator|.
name|clearZKSaslClientProperties
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateSecureZK ()
specifier|public
name|void
name|testCreateSecureZK
parameter_list|()
throws|throws
name|Throwable
block|{
name|startSecureZK
argument_list|()
expr_stmt|;
name|secureZK
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInsecureClientToZK ()
specifier|public
name|void
name|testInsecureClientToZK
parameter_list|()
throws|throws
name|Throwable
block|{
name|startSecureZK
argument_list|()
expr_stmt|;
name|userZookeeperToCreateRoot
argument_list|()
expr_stmt|;
name|RegistrySecurity
operator|.
name|clearZKSaslClientProperties
argument_list|()
expr_stmt|;
name|CuratorService
name|curatorService
init|=
name|startCuratorServiceInstance
argument_list|(
literal|"insecure client"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|curatorService
operator|.
name|zkList
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|curatorService
operator|.
name|zkMkPath
argument_list|(
literal|""
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|false
argument_list|,
name|RegistrySecurity
operator|.
name|WorldReadWriteACL
argument_list|)
expr_stmt|;
block|}
comment|/**    * test that ZK can write as itself    * @throws Throwable    */
annotation|@
name|Test
DECL|method|testZookeeperCanWrite ()
specifier|public
name|void
name|testZookeeperCanWrite
parameter_list|()
throws|throws
name|Throwable
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"curator-log-events"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|startSecureZK
argument_list|()
expr_stmt|;
name|CuratorService
name|curator
init|=
literal|null
decl_stmt|;
name|LoginContext
name|login
init|=
name|login
argument_list|(
name|ZOOKEEPER_LOCALHOST
argument_list|,
name|ZOOKEEPER_CLIENT_CONTEXT
argument_list|,
name|keytab_zk
argument_list|)
decl_stmt|;
try|try
block|{
name|logLoginDetails
argument_list|(
name|ZOOKEEPER
argument_list|,
name|login
argument_list|)
expr_stmt|;
name|RegistrySecurity
operator|.
name|setZKSaslClientProperties
argument_list|(
name|ZOOKEEPER
argument_list|,
name|ZOOKEEPER_CLIENT_CONTEXT
argument_list|)
expr_stmt|;
name|curator
operator|=
name|startCuratorServiceInstance
argument_list|(
literal|"ZK"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|curator
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|addToTeardown
argument_list|(
name|curator
argument_list|)
expr_stmt|;
name|curator
operator|.
name|zkMkPath
argument_list|(
literal|"/"
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|false
argument_list|,
name|RegistrySecurity
operator|.
name|WorldReadWriteACL
argument_list|)
expr_stmt|;
name|curator
operator|.
name|zkList
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|curator
operator|.
name|zkMkPath
argument_list|(
literal|"/zookeeper"
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|false
argument_list|,
name|RegistrySecurity
operator|.
name|WorldReadWriteACL
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|logout
argument_list|(
name|login
argument_list|)
expr_stmt|;
name|ServiceOperations
operator|.
name|stop
argument_list|(
name|curator
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSystemPropertyOverwrite ()
specifier|public
name|void
name|testSystemPropertyOverwrite
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|PROP_ZK_SASL_CLIENT_USERNAME
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|PROP_ZK_SASL_CLIENT_CONTEXT
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|RegistrySecurity
operator|.
name|setZKSaslClientProperties
argument_list|(
name|ZOOKEEPER
argument_list|,
name|ZOOKEEPER_CLIENT_CONTEXT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ZOOKEEPER
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
name|PROP_ZK_SASL_CLIENT_USERNAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ZOOKEEPER_CLIENT_CONTEXT
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
name|PROP_ZK_SASL_CLIENT_CONTEXT
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|userName
init|=
literal|"user1"
decl_stmt|;
name|String
name|context
init|=
literal|"context1"
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|PROP_ZK_SASL_CLIENT_USERNAME
argument_list|,
name|userName
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|PROP_ZK_SASL_CLIENT_CONTEXT
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|RegistrySecurity
operator|.
name|setZKSaslClientProperties
argument_list|(
name|ZOOKEEPER
argument_list|,
name|ZOOKEEPER_CLIENT_CONTEXT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|userName
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
name|PROP_ZK_SASL_CLIENT_USERNAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|context
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
name|PROP_ZK_SASL_CLIENT_CONTEXT
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Start a curator service instance    * @param name name    * @param secure flag to indicate the cluster is secure    * @return an inited and started curator service    */
DECL|method|startCuratorServiceInstance (String name, boolean secure)
specifier|protected
name|CuratorService
name|startCuratorServiceInstance
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|secure
parameter_list|)
block|{
name|Configuration
name|clientConf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|clientConf
operator|.
name|set
argument_list|(
name|KEY_REGISTRY_ZK_ROOT
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
name|clientConf
operator|.
name|setBoolean
argument_list|(
name|KEY_REGISTRY_SECURE
argument_list|,
name|secure
argument_list|)
expr_stmt|;
name|describe
argument_list|(
name|LOG
argument_list|,
literal|"Starting Curator service"
argument_list|)
expr_stmt|;
name|CuratorService
name|curatorService
init|=
operator|new
name|CuratorService
argument_list|(
name|name
argument_list|,
name|secureZK
argument_list|)
decl_stmt|;
name|curatorService
operator|.
name|init
argument_list|(
name|clientConf
argument_list|)
expr_stmt|;
name|curatorService
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Curator Binding {}"
argument_list|,
name|curatorService
operator|.
name|bindingDiagnosticDetails
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|curatorService
return|;
block|}
comment|/**    * have the ZK user create the root dir.    * This logs out the ZK user after and stops its curator instance,    * to avoid contamination    * @throws Throwable    */
DECL|method|userZookeeperToCreateRoot ()
specifier|public
name|void
name|userZookeeperToCreateRoot
parameter_list|()
throws|throws
name|Throwable
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"curator-log-events"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|CuratorService
name|curator
init|=
literal|null
decl_stmt|;
name|LoginContext
name|login
init|=
name|login
argument_list|(
name|ZOOKEEPER_LOCALHOST
argument_list|,
name|ZOOKEEPER_CLIENT_CONTEXT
argument_list|,
name|keytab_zk
argument_list|)
decl_stmt|;
try|try
block|{
name|logLoginDetails
argument_list|(
name|ZOOKEEPER
argument_list|,
name|login
argument_list|)
expr_stmt|;
name|RegistrySecurity
operator|.
name|setZKSaslClientProperties
argument_list|(
name|ZOOKEEPER
argument_list|,
name|ZOOKEEPER_CLIENT_CONTEXT
argument_list|)
expr_stmt|;
name|curator
operator|=
name|startCuratorServiceInstance
argument_list|(
literal|"ZK"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|curator
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|addToTeardown
argument_list|(
name|curator
argument_list|)
expr_stmt|;
name|curator
operator|.
name|zkMkPath
argument_list|(
literal|"/"
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|false
argument_list|,
name|RegistrySecurity
operator|.
name|WorldReadWriteACL
argument_list|)
expr_stmt|;
name|ZKPathDumper
name|pathDumper
init|=
name|curator
operator|.
name|dumpPath
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|pathDumper
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|logout
argument_list|(
name|login
argument_list|)
expr_stmt|;
name|ServiceOperations
operator|.
name|stop
argument_list|(
name|curator
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

