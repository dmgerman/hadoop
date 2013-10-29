begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
package|;
end_package

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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyInt
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|argThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|eq
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verify
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|io
operator|.
name|PrintStream
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
name|ha
operator|.
name|HAServiceProtocol
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
name|ha
operator|.
name|HAServiceStatus
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
name|ha
operator|.
name|HAServiceTarget
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
name|cli
operator|.
name|RMAdminCLI
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
name|server
operator|.
name|api
operator|.
name|ResourceManagerAdministrationProtocol
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
name|protocolrecords
operator|.
name|RefreshAdminAclsRequest
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
name|protocolrecords
operator|.
name|RefreshNodesRequest
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
name|protocolrecords
operator|.
name|RefreshQueuesRequest
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
name|protocolrecords
operator|.
name|RefreshServiceAclsRequest
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
name|protocolrecords
operator|.
name|RefreshSuperUserGroupsConfigurationRequest
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
name|protocolrecords
operator|.
name|RefreshUserToGroupsMappingsRequest
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
name|mockito
operator|.
name|ArgumentMatcher
import|;
end_import

begin_class
DECL|class|TestRMAdminCLI
specifier|public
class|class
name|TestRMAdminCLI
block|{
DECL|field|admin
specifier|private
name|ResourceManagerAdministrationProtocol
name|admin
decl_stmt|;
DECL|field|haadmin
specifier|private
name|HAServiceProtocol
name|haadmin
decl_stmt|;
DECL|field|rmAdminCLI
specifier|private
name|RMAdminCLI
name|rmAdminCLI
decl_stmt|;
annotation|@
name|Before
DECL|method|configure ()
specifier|public
name|void
name|configure
parameter_list|()
throws|throws
name|IOException
block|{
name|admin
operator|=
name|mock
argument_list|(
name|ResourceManagerAdministrationProtocol
operator|.
name|class
argument_list|)
expr_stmt|;
name|haadmin
operator|=
name|mock
argument_list|(
name|HAServiceProtocol
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|haadmin
operator|.
name|getServiceStatus
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|HAServiceStatus
argument_list|(
name|HAServiceProtocol
operator|.
name|HAServiceState
operator|.
name|INITIALIZING
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|HAServiceTarget
name|haServiceTarget
init|=
name|mock
argument_list|(
name|HAServiceTarget
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|haServiceTarget
operator|.
name|getProxy
argument_list|(
name|any
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
argument_list|,
name|anyInt
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|haadmin
argument_list|)
expr_stmt|;
name|rmAdminCLI
operator|=
operator|new
name|RMAdminCLI
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|ResourceManagerAdministrationProtocol
name|createAdminProtocol
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|admin
return|;
block|}
annotation|@
name|Override
specifier|protected
name|HAServiceTarget
name|resolveTarget
parameter_list|(
name|String
name|rmId
parameter_list|)
block|{
return|return
name|haServiceTarget
return|;
block|}
block|}
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|500
argument_list|)
DECL|method|testRefreshQueues ()
specifier|public
name|void
name|testRefreshQueues
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"-refreshQueues"
block|}
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rmAdminCLI
operator|.
name|run
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|admin
argument_list|)
operator|.
name|refreshQueues
argument_list|(
name|any
argument_list|(
name|RefreshQueuesRequest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|500
argument_list|)
DECL|method|testRefreshUserToGroupsMappings ()
specifier|public
name|void
name|testRefreshUserToGroupsMappings
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"-refreshUserToGroupsMappings"
block|}
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rmAdminCLI
operator|.
name|run
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|admin
argument_list|)
operator|.
name|refreshUserToGroupsMappings
argument_list|(
name|any
argument_list|(
name|RefreshUserToGroupsMappingsRequest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|500
argument_list|)
DECL|method|testRefreshSuperUserGroupsConfiguration ()
specifier|public
name|void
name|testRefreshSuperUserGroupsConfiguration
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"-refreshSuperUserGroupsConfiguration"
block|}
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rmAdminCLI
operator|.
name|run
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|admin
argument_list|)
operator|.
name|refreshSuperUserGroupsConfiguration
argument_list|(
name|any
argument_list|(
name|RefreshSuperUserGroupsConfigurationRequest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|500
argument_list|)
DECL|method|testRefreshAdminAcls ()
specifier|public
name|void
name|testRefreshAdminAcls
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"-refreshAdminAcls"
block|}
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rmAdminCLI
operator|.
name|run
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|admin
argument_list|)
operator|.
name|refreshAdminAcls
argument_list|(
name|any
argument_list|(
name|RefreshAdminAclsRequest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|500
argument_list|)
DECL|method|testRefreshServiceAcl ()
specifier|public
name|void
name|testRefreshServiceAcl
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"-refreshServiceAcl"
block|}
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rmAdminCLI
operator|.
name|run
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|admin
argument_list|)
operator|.
name|refreshServiceAcls
argument_list|(
name|any
argument_list|(
name|RefreshServiceAclsRequest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|500
argument_list|)
DECL|method|testRefreshNodes ()
specifier|public
name|void
name|testRefreshNodes
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"-refreshNodes"
block|}
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rmAdminCLI
operator|.
name|run
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|admin
argument_list|)
operator|.
name|refreshNodes
argument_list|(
name|any
argument_list|(
name|RefreshNodesRequest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|500
argument_list|)
DECL|method|testGetGroups ()
specifier|public
name|void
name|testGetGroups
parameter_list|()
throws|throws
name|Exception
block|{
name|when
argument_list|(
name|admin
operator|.
name|getGroupsForUser
argument_list|(
name|eq
argument_list|(
literal|"admin"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"group1"
block|,
literal|"group2"
block|}
argument_list|)
expr_stmt|;
name|PrintStream
name|origOut
init|=
name|System
operator|.
name|out
decl_stmt|;
name|PrintStream
name|out
init|=
name|mock
argument_list|(
name|PrintStream
operator|.
name|class
argument_list|)
decl_stmt|;
name|System
operator|.
name|setOut
argument_list|(
name|out
argument_list|)
expr_stmt|;
try|try
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"-getGroups"
block|,
literal|"admin"
block|}
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rmAdminCLI
operator|.
name|run
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|admin
argument_list|)
operator|.
name|getGroupsForUser
argument_list|(
name|eq
argument_list|(
literal|"admin"
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|out
argument_list|)
operator|.
name|println
argument_list|(
name|argThat
argument_list|(
operator|new
name|ArgumentMatcher
argument_list|<
name|StringBuilder
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|Object
name|argument
parameter_list|)
block|{
return|return
operator|(
literal|""
operator|+
name|argument
operator|)
operator|.
name|equals
argument_list|(
literal|"admin : group1 group2"
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|setOut
argument_list|(
name|origOut
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|500
argument_list|)
DECL|method|testTransitionToActive ()
specifier|public
name|void
name|testTransitionToActive
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"-transitionToActive"
block|,
literal|"rm1"
block|}
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rmAdminCLI
operator|.
name|run
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|haadmin
argument_list|)
operator|.
name|transitionToActive
argument_list|(
name|any
argument_list|(
name|HAServiceProtocol
operator|.
name|StateChangeRequestInfo
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|500
argument_list|)
DECL|method|testTransitionToStandby ()
specifier|public
name|void
name|testTransitionToStandby
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"-transitionToStandby"
block|,
literal|"rm1"
block|}
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rmAdminCLI
operator|.
name|run
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|haadmin
argument_list|)
operator|.
name|transitionToStandby
argument_list|(
name|any
argument_list|(
name|HAServiceProtocol
operator|.
name|StateChangeRequestInfo
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|500
argument_list|)
DECL|method|testGetServiceState ()
specifier|public
name|void
name|testGetServiceState
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"-getServiceState"
block|,
literal|"rm1"
block|}
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rmAdminCLI
operator|.
name|run
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|haadmin
argument_list|)
operator|.
name|getServiceStatus
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|500
argument_list|)
DECL|method|testCheckHealth ()
specifier|public
name|void
name|testCheckHealth
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"-checkHealth"
block|,
literal|"rm1"
block|}
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rmAdminCLI
operator|.
name|run
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|haadmin
argument_list|)
operator|.
name|monitorHealth
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test printing of help messages    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|500
argument_list|)
DECL|method|testHelp ()
specifier|public
name|void
name|testHelp
parameter_list|()
throws|throws
name|Exception
block|{
name|PrintStream
name|oldOutPrintStream
init|=
name|System
operator|.
name|out
decl_stmt|;
name|PrintStream
name|oldErrPrintStream
init|=
name|System
operator|.
name|err
decl_stmt|;
name|ByteArrayOutputStream
name|dataOut
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|ByteArrayOutputStream
name|dataErr
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|System
operator|.
name|setOut
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|dataOut
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|setErr
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|dataErr
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"-help"
block|}
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rmAdminCLI
operator|.
name|run
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|oldOutPrintStream
operator|.
name|println
argument_list|(
name|dataOut
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dataOut
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"rmadmin is the command to execute YARN administrative commands."
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dataOut
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"yarn rmadmin [-refreshQueues] [-refreshNodes] [-refreshSuper"
operator|+
literal|"UserGroupsConfiguration] [-refreshUserToGroupsMappings] "
operator|+
literal|"[-refreshAdminAcls] [-refreshServiceAcl] [-getGroup"
operator|+
literal|" [username]] [-help [cmd]] [-transitionToActive<serviceId>]"
operator|+
literal|" [-transitionToStandby<serviceId>] [-failover [--forcefence] "
operator|+
literal|"[--forceactive]<serviceId><serviceId>] "
operator|+
literal|"[-getServiceState<serviceId>] [-checkHealth<serviceId>]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dataOut
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"-refreshQueues: Reload the queues' acls, states and scheduler "
operator|+
literal|"specific properties."
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dataOut
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"-refreshNodes: Refresh the hosts information at the "
operator|+
literal|"ResourceManager."
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dataOut
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"-refreshUserToGroupsMappings: Refresh user-to-groups mappings"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dataOut
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"-refreshSuperUserGroupsConfiguration: Refresh superuser proxy"
operator|+
literal|" groups mappings"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dataOut
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"-refreshAdminAcls: Refresh acls for administration of "
operator|+
literal|"ResourceManager"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dataOut
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"-refreshServiceAcl: Reload the service-level authorization"
operator|+
literal|" policy file"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dataOut
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"-help [cmd]: Displays help for the given command or all "
operator|+
literal|"commands if none"
argument_list|)
argument_list|)
expr_stmt|;
name|testError
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-help"
block|,
literal|"-refreshQueues"
block|}
argument_list|,
literal|"Usage: java RMAdmin [-refreshQueues]"
argument_list|,
name|dataErr
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|testError
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-help"
block|,
literal|"-refreshNodes"
block|}
argument_list|,
literal|"Usage: java RMAdmin [-refreshNodes]"
argument_list|,
name|dataErr
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|testError
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-help"
block|,
literal|"-refreshUserToGroupsMappings"
block|}
argument_list|,
literal|"Usage: java RMAdmin [-refreshUserToGroupsMappings]"
argument_list|,
name|dataErr
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|testError
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-help"
block|,
literal|"-refreshSuperUserGroupsConfiguration"
block|}
argument_list|,
literal|"Usage: java RMAdmin [-refreshSuperUserGroupsConfiguration]"
argument_list|,
name|dataErr
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|testError
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-help"
block|,
literal|"-refreshAdminAcls"
block|}
argument_list|,
literal|"Usage: java RMAdmin [-refreshAdminAcls]"
argument_list|,
name|dataErr
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|testError
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-help"
block|,
literal|"-refreshServiceAcl"
block|}
argument_list|,
literal|"Usage: java RMAdmin [-refreshServiceAcl]"
argument_list|,
name|dataErr
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|testError
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-help"
block|,
literal|"-getGroups"
block|}
argument_list|,
literal|"Usage: java RMAdmin [-getGroups [username]]"
argument_list|,
name|dataErr
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|testError
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-help"
block|,
literal|"-transitionToActive"
block|}
argument_list|,
literal|"Usage: java RMAdmin [-transitionToActive<serviceId>]"
argument_list|,
name|dataErr
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|testError
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-help"
block|,
literal|"-transitionToStandby"
block|}
argument_list|,
literal|"Usage: java RMAdmin [-transitionToStandby<serviceId>]"
argument_list|,
name|dataErr
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|testError
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-help"
block|,
literal|"-getServiceState"
block|}
argument_list|,
literal|"Usage: java RMAdmin [-getServiceState<serviceId>]"
argument_list|,
name|dataErr
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|testError
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-help"
block|,
literal|"-checkHealth"
block|}
argument_list|,
literal|"Usage: java RMAdmin [-checkHealth<serviceId>]"
argument_list|,
name|dataErr
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|testError
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-help"
block|,
literal|"-failover"
block|}
argument_list|,
literal|"Usage: java RMAdmin "
operator|+
literal|"[-failover [--forcefence] [--forceactive] "
operator|+
literal|"<serviceId><serviceId>]"
argument_list|,
name|dataErr
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|testError
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-help"
block|,
literal|"-badParameter"
block|}
argument_list|,
literal|"Usage: java RMAdmin"
argument_list|,
name|dataErr
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|testError
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-badParameter"
block|}
argument_list|,
literal|"badParameter: Unknown command"
argument_list|,
name|dataErr
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|setOut
argument_list|(
name|oldOutPrintStream
argument_list|)
expr_stmt|;
name|System
operator|.
name|setErr
argument_list|(
name|oldErrPrintStream
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|500
argument_list|)
DECL|method|testException ()
specifier|public
name|void
name|testException
parameter_list|()
throws|throws
name|Exception
block|{
name|PrintStream
name|oldErrPrintStream
init|=
name|System
operator|.
name|err
decl_stmt|;
name|ByteArrayOutputStream
name|dataErr
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|System
operator|.
name|setErr
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|dataErr
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|when
argument_list|(
name|admin
operator|.
name|refreshQueues
argument_list|(
name|any
argument_list|(
name|RefreshQueuesRequest
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenThrow
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"test exception"
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|args
init|=
block|{
literal|"-refreshQueues"
block|}
decl_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|rmAdminCLI
operator|.
name|run
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|admin
argument_list|)
operator|.
name|refreshQueues
argument_list|(
name|any
argument_list|(
name|RefreshQueuesRequest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dataErr
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"refreshQueues: test exception"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|setErr
argument_list|(
name|oldErrPrintStream
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testError (String[] args, String template, ByteArrayOutputStream data, int resultCode)
specifier|private
name|void
name|testError
parameter_list|(
name|String
index|[]
name|args
parameter_list|,
name|String
name|template
parameter_list|,
name|ByteArrayOutputStream
name|data
parameter_list|,
name|int
name|resultCode
parameter_list|)
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|resultCode
argument_list|,
name|rmAdminCLI
operator|.
name|run
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|data
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|template
argument_list|)
argument_list|)
expr_stmt|;
name|data
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

