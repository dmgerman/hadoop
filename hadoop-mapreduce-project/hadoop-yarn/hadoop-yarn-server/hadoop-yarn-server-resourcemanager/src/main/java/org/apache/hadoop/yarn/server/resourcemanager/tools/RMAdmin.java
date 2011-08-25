begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.tools
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
name|resourcemanager
operator|.
name|tools
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
name|java
operator|.
name|security
operator|.
name|PrivilegedAction
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
name|conf
operator|.
name|Configured
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
name|fs
operator|.
name|CommonConfigurationKeys
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
name|RemoteException
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
name|security
operator|.
name|SecurityInfo
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
name|util
operator|.
name|Tool
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
name|ToolRunner
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
name|security
operator|.
name|admin
operator|.
name|AdminSecurityInfo
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
name|resourcemanager
operator|.
name|RMConfig
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
name|resourcemanager
operator|.
name|api
operator|.
name|RMAdminProtocol
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
name|resourcemanager
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
name|resourcemanager
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
name|resourcemanager
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
name|resourcemanager
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
name|resourcemanager
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|RefreshUserToGroupsMappingsRequest
import|;
end_import

begin_class
DECL|class|RMAdmin
specifier|public
class|class
name|RMAdmin
extends|extends
name|Configured
implements|implements
name|Tool
block|{
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
DECL|method|RMAdmin ()
specifier|public
name|RMAdmin
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|RMAdmin (Configuration conf)
specifier|public
name|RMAdmin
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|printHelp (String cmd)
specifier|private
specifier|static
name|void
name|printHelp
parameter_list|(
name|String
name|cmd
parameter_list|)
block|{
name|String
name|summary
init|=
literal|"rmadmin is the command to execute Map-Reduce administrative commands.\n"
operator|+
literal|"The full syntax is: \n\n"
operator|+
literal|"hadoop rmadmin"
operator|+
literal|" [-refreshQueues]"
operator|+
literal|" [-refreshNodes]"
operator|+
literal|" [-refreshSuperUserGroupsConfiguration]"
operator|+
literal|" [-refreshUserToGroupsMappings]"
operator|+
literal|" [-refreshAdminAcls]"
operator|+
literal|" [-help [cmd]]\n"
decl_stmt|;
name|String
name|refreshQueues
init|=
literal|"-refreshQueues: Reload the queues' acls, states and "
operator|+
literal|"scheduler specific properties.\n"
operator|+
literal|"\t\tResourceManager will reload the mapred-queues configuration file.\n"
decl_stmt|;
name|String
name|refreshNodes
init|=
literal|"-refreshNodes: Refresh the hosts information at the ResourceManager.\n"
decl_stmt|;
name|String
name|refreshUserToGroupsMappings
init|=
literal|"-refreshUserToGroupsMappings: Refresh user-to-groups mappings\n"
decl_stmt|;
name|String
name|refreshSuperUserGroupsConfiguration
init|=
literal|"-refreshSuperUserGroupsConfiguration: Refresh superuser proxy groups mappings\n"
decl_stmt|;
name|String
name|refreshAdminAcls
init|=
literal|"-refreshAdminAcls: Refresh acls for administration of ResourceManager\n"
decl_stmt|;
name|String
name|help
init|=
literal|"-help [cmd]: \tDisplays help for the given command or all commands if none\n"
operator|+
literal|"\t\tis specified.\n"
decl_stmt|;
if|if
condition|(
literal|"refreshQueues"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|refreshQueues
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"refreshNodes"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|refreshNodes
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"refreshUserToGroupsMappings"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|refreshUserToGroupsMappings
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"refreshSuperUserGroupsConfiguration"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|refreshSuperUserGroupsConfiguration
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"refreshAdminAcls"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|refreshAdminAcls
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"help"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|help
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|summary
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|refreshQueues
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|help
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|ToolRunner
operator|.
name|printGenericCommandUsage
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Displays format of commands.    * @param cmd The command that is being executed.    */
DECL|method|printUsage (String cmd)
specifier|private
specifier|static
name|void
name|printUsage
parameter_list|(
name|String
name|cmd
parameter_list|)
block|{
if|if
condition|(
literal|"-refreshQueues"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: java RMAdmin"
operator|+
literal|" [-refreshQueues]"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-refreshNodes"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: java RMAdmin"
operator|+
literal|" [-refreshNodes]"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-refreshUserToGroupsMappings"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: java RMAdmin"
operator|+
literal|" [-refreshUserToGroupsMappings]"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-refreshSuperUserGroupsConfiguration"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: java RMAdmin"
operator|+
literal|" [-refreshSuperUserGroupsConfiguration]"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-refreshAdminAcls"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: java RMAdmin"
operator|+
literal|" [-refreshAdminAcls]"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: java RMAdmin"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"           [-refreshQueues]"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"           [-refreshNodes]"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"           [-refreshUserToGroupsMappings]"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"           [-refreshSuperUserGroupsConfiguration]"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"           [-refreshAdminAcls]"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"           [-help [cmd]]"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|()
expr_stmt|;
name|ToolRunner
operator|.
name|printGenericCommandUsage
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getUGI (Configuration conf )
specifier|private
specifier|static
name|UserGroupInformation
name|getUGI
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
return|;
block|}
DECL|method|createAdminProtocol ()
specifier|private
name|RMAdminProtocol
name|createAdminProtocol
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Get the current configuration
specifier|final
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|(
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
comment|// Create the client
specifier|final
name|String
name|adminAddress
init|=
name|conf
operator|.
name|get
argument_list|(
name|RMConfig
operator|.
name|ADMIN_ADDRESS
argument_list|,
name|RMConfig
operator|.
name|DEFAULT_ADMIN_BIND_ADDRESS
argument_list|)
decl_stmt|;
specifier|final
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|conf
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_SECURITY_INFO
argument_list|,
name|AdminSecurityInfo
operator|.
name|class
argument_list|,
name|SecurityInfo
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|RMAdminProtocol
name|adminProtocol
init|=
name|getUGI
argument_list|(
name|conf
argument_list|)
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|RMAdminProtocol
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RMAdminProtocol
name|run
parameter_list|()
block|{
return|return
operator|(
name|RMAdminProtocol
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|RMAdminProtocol
operator|.
name|class
argument_list|,
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|adminAddress
argument_list|)
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|adminProtocol
return|;
block|}
DECL|method|refreshQueues ()
specifier|private
name|int
name|refreshQueues
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Refresh the queue properties
name|RMAdminProtocol
name|adminProtocol
init|=
name|createAdminProtocol
argument_list|()
decl_stmt|;
name|RefreshQueuesRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|RefreshQueuesRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|adminProtocol
operator|.
name|refreshQueues
argument_list|(
name|request
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
DECL|method|refreshNodes ()
specifier|private
name|int
name|refreshNodes
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Refresh the nodes
name|RMAdminProtocol
name|adminProtocol
init|=
name|createAdminProtocol
argument_list|()
decl_stmt|;
name|RefreshNodesRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|RefreshNodesRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|adminProtocol
operator|.
name|refreshNodes
argument_list|(
name|request
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
DECL|method|refreshUserToGroupsMappings ()
specifier|private
name|int
name|refreshUserToGroupsMappings
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Refresh the user-to-groups mappings
name|RMAdminProtocol
name|adminProtocol
init|=
name|createAdminProtocol
argument_list|()
decl_stmt|;
name|RefreshUserToGroupsMappingsRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|RefreshUserToGroupsMappingsRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|adminProtocol
operator|.
name|refreshUserToGroupsMappings
argument_list|(
name|request
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
DECL|method|refreshSuperUserGroupsConfiguration ()
specifier|private
name|int
name|refreshSuperUserGroupsConfiguration
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Refresh the super-user groups
name|RMAdminProtocol
name|adminProtocol
init|=
name|createAdminProtocol
argument_list|()
decl_stmt|;
name|RefreshSuperUserGroupsConfigurationRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|RefreshSuperUserGroupsConfigurationRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|adminProtocol
operator|.
name|refreshSuperUserGroupsConfiguration
argument_list|(
name|request
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
DECL|method|refreshAdminAcls ()
specifier|private
name|int
name|refreshAdminAcls
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Refresh the admin acls
name|RMAdminProtocol
name|adminProtocol
init|=
name|createAdminProtocol
argument_list|()
decl_stmt|;
name|RefreshAdminAclsRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|RefreshAdminAclsRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|adminProtocol
operator|.
name|refreshAdminAcls
argument_list|(
name|request
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|run (String[] args)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|1
condition|)
block|{
name|printUsage
argument_list|(
literal|""
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|int
name|exitCode
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
name|String
name|cmd
init|=
name|args
index|[
name|i
operator|++
index|]
decl_stmt|;
comment|//
comment|// verify that we have enough command line parameters
comment|//
if|if
condition|(
literal|"-refreshAdminAcls"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
operator|||
literal|"-refreshQueues"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
operator|||
literal|"-refreshNodes"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
operator|||
literal|"-refreshUserToGroupsMappings"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
operator|||
literal|"-refreshSuperUserGroupsConfiguration"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|!=
literal|1
condition|)
block|{
name|printUsage
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
return|return
name|exitCode
return|;
block|}
block|}
name|exitCode
operator|=
literal|0
expr_stmt|;
try|try
block|{
if|if
condition|(
literal|"-refreshQueues"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
name|exitCode
operator|=
name|refreshQueues
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-refreshNodes"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
name|exitCode
operator|=
name|refreshNodes
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-refreshUserToGroupsMappings"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
name|exitCode
operator|=
name|refreshUserToGroupsMappings
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-refreshSuperUserGroupsConfiguration"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
name|exitCode
operator|=
name|refreshSuperUserGroupsConfiguration
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-refreshAdminAcls"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
name|exitCode
operator|=
name|refreshAdminAcls
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-help"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
if|if
condition|(
name|i
operator|<
name|args
operator|.
name|length
condition|)
block|{
name|printUsage
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|printHelp
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|exitCode
operator|=
operator|-
literal|1
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|cmd
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
operator|+
literal|": Unknown command"
argument_list|)
expr_stmt|;
name|printUsage
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|printUsage
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|arge
parameter_list|)
block|{
name|exitCode
operator|=
operator|-
literal|1
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|cmd
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
operator|+
literal|": "
operator|+
name|arge
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
expr_stmt|;
name|printUsage
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|e
parameter_list|)
block|{
comment|//
comment|// This is a error returned by hadoop server. Print
comment|// out the first line of the error mesage, ignore the stack trace.
name|exitCode
operator|=
operator|-
literal|1
expr_stmt|;
try|try
block|{
name|String
index|[]
name|content
decl_stmt|;
name|content
operator|=
name|e
operator|.
name|getLocalizedMessage
argument_list|()
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|cmd
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
operator|+
literal|": "
operator|+
name|content
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|cmd
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
operator|+
literal|": "
operator|+
name|ex
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|exitCode
operator|=
operator|-
literal|1
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|cmd
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
operator|+
literal|": "
operator|+
name|e
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|exitCode
return|;
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|result
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|RMAdmin
argument_list|()
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

