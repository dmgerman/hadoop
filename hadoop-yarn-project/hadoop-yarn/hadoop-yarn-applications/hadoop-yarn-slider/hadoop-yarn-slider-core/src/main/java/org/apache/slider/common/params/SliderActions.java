begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|common
operator|.
name|params
package|;
end_package

begin_comment
comment|/**  * Actions.  * Only some of these are supported by specific Slider Services; they  * are listed here to ensure the names are consistent  */
end_comment

begin_interface
DECL|interface|SliderActions
specifier|public
interface|interface
name|SliderActions
block|{
DECL|field|ACTION_AM_SUICIDE
name|String
name|ACTION_AM_SUICIDE
init|=
literal|"am-suicide"
decl_stmt|;
DECL|field|ACTION_BUILD
name|String
name|ACTION_BUILD
init|=
literal|"build"
decl_stmt|;
DECL|field|ACTION_CLIENT
name|String
name|ACTION_CLIENT
init|=
literal|"client"
decl_stmt|;
DECL|field|ACTION_CREATE
name|String
name|ACTION_CREATE
init|=
literal|"create"
decl_stmt|;
DECL|field|ACTION_DIAGNOSTICS
name|String
name|ACTION_DIAGNOSTICS
init|=
literal|"diagnostics"
decl_stmt|;
DECL|field|ACTION_DEPENDENCY
name|String
name|ACTION_DEPENDENCY
init|=
literal|"dependency"
decl_stmt|;
DECL|field|ACTION_UPDATE
name|String
name|ACTION_UPDATE
init|=
literal|"update"
decl_stmt|;
DECL|field|ACTION_UPGRADE
name|String
name|ACTION_UPGRADE
init|=
literal|"upgrade"
decl_stmt|;
DECL|field|ACTION_DESTROY
name|String
name|ACTION_DESTROY
init|=
literal|"destroy"
decl_stmt|;
DECL|field|ACTION_ECHO
name|String
name|ACTION_ECHO
init|=
literal|"echo"
decl_stmt|;
DECL|field|ACTION_EXISTS
name|String
name|ACTION_EXISTS
init|=
literal|"exists"
decl_stmt|;
DECL|field|ACTION_FLEX
name|String
name|ACTION_FLEX
init|=
literal|"flex"
decl_stmt|;
DECL|field|ACTION_STOP
name|String
name|ACTION_STOP
init|=
literal|"stop"
decl_stmt|;
DECL|field|ACTION_HELP
name|String
name|ACTION_HELP
init|=
literal|"help"
decl_stmt|;
DECL|field|ACTION_INSTALL_KEYTAB
name|String
name|ACTION_INSTALL_KEYTAB
init|=
literal|"install-keytab"
decl_stmt|;
DECL|field|ACTION_INSTALL_PACKAGE
name|String
name|ACTION_INSTALL_PACKAGE
init|=
literal|"install-package"
decl_stmt|;
DECL|field|ACTION_KDIAG
name|String
name|ACTION_KDIAG
init|=
literal|"kdiag"
decl_stmt|;
DECL|field|ACTION_KEYTAB
name|String
name|ACTION_KEYTAB
init|=
literal|"keytab"
decl_stmt|;
DECL|field|ACTION_KILL_CONTAINER
name|String
name|ACTION_KILL_CONTAINER
init|=
literal|"kill-container"
decl_stmt|;
DECL|field|ACTION_LIST
name|String
name|ACTION_LIST
init|=
literal|"list"
decl_stmt|;
DECL|field|ACTION_LOOKUP
name|String
name|ACTION_LOOKUP
init|=
literal|"lookup"
decl_stmt|;
DECL|field|ACTION_NODES
name|String
name|ACTION_NODES
init|=
literal|"nodes"
decl_stmt|;
DECL|field|ACTION_PACKAGE
name|String
name|ACTION_PACKAGE
init|=
literal|"package"
decl_stmt|;
DECL|field|ACTION_PREFLIGHT
name|String
name|ACTION_PREFLIGHT
init|=
literal|"preflight"
decl_stmt|;
DECL|field|ACTION_RECONFIGURE
name|String
name|ACTION_RECONFIGURE
init|=
literal|"reconfigure"
decl_stmt|;
DECL|field|ACTION_REGISTRY
name|String
name|ACTION_REGISTRY
init|=
literal|"registry"
decl_stmt|;
DECL|field|ACTION_RESOLVE
name|String
name|ACTION_RESOLVE
init|=
literal|"resolve"
decl_stmt|;
DECL|field|ACTION_RESOURCE
name|String
name|ACTION_RESOURCE
init|=
literal|"resource"
decl_stmt|;
DECL|field|ACTION_STATUS
name|String
name|ACTION_STATUS
init|=
literal|"status"
decl_stmt|;
DECL|field|ACTION_START
name|String
name|ACTION_START
init|=
literal|"start"
decl_stmt|;
DECL|field|ACTION_TOKENS
name|String
name|ACTION_TOKENS
init|=
literal|"tokens"
decl_stmt|;
DECL|field|ACTION_VERSION
name|String
name|ACTION_VERSION
init|=
literal|"version"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_AM_SUICIDE
name|String
name|DESCRIBE_ACTION_AM_SUICIDE
init|=
literal|"Tell the Slider Application Master to simulate a process failure by terminating itself"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_BUILD
name|String
name|DESCRIBE_ACTION_BUILD
init|=
literal|"Build a Slider cluster specification, but do not start it"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_CREATE
name|String
name|DESCRIBE_ACTION_CREATE
init|=
literal|"Create a live Slider application"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_DEPENDENCY
name|String
name|DESCRIBE_ACTION_DEPENDENCY
init|=
literal|"Slider AM and agent dependency (libraries) management"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_UPDATE
name|String
name|DESCRIBE_ACTION_UPDATE
init|=
literal|"Update template for a Slider application"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_UPGRADE
name|String
name|DESCRIBE_ACTION_UPGRADE
init|=
literal|"Rolling upgrade/downgrade the component/containerto a newer/previous version"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_DESTROY
name|String
name|DESCRIBE_ACTION_DESTROY
init|=
literal|"Destroy a stopped Slider application"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_EXISTS
name|String
name|DESCRIBE_ACTION_EXISTS
init|=
literal|"Probe for an application running"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_FLEX
name|String
name|DESCRIBE_ACTION_FLEX
init|=
literal|"Flex a Slider application"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_FREEZE
name|String
name|DESCRIBE_ACTION_FREEZE
init|=
literal|"Stop a running application"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_GETCONF
name|String
name|DESCRIBE_ACTION_GETCONF
init|=
literal|"Get the configuration of an application"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_KDIAG
name|String
name|DESCRIBE_ACTION_KDIAG
init|=
literal|"Diagnose Kerberos problems"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_KILL_CONTAINER
name|String
name|DESCRIBE_ACTION_KILL_CONTAINER
init|=
literal|"Kill a container in the application"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_HELP
name|String
name|DESCRIBE_ACTION_HELP
init|=
literal|"Print help information"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_LIST
name|String
name|DESCRIBE_ACTION_LIST
init|=
literal|"List running Slider applications"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_LOOKUP
name|String
name|DESCRIBE_ACTION_LOOKUP
init|=
literal|"look up a YARN application"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_NODES
name|String
name|DESCRIBE_ACTION_NODES
init|=
literal|"List the node information for the YARN cluster or a running application"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_MONITOR
name|String
name|DESCRIBE_ACTION_MONITOR
init|=
literal|"Monitor a running application"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_REGISTRY
name|String
name|DESCRIBE_ACTION_REGISTRY
init|=
literal|"Query the registry of a YARN application"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_RESOLVE
name|String
name|DESCRIBE_ACTION_RESOLVE
init|=
literal|"Resolve or list records in the YARN registry"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_STATUS
name|String
name|DESCRIBE_ACTION_STATUS
init|=
literal|"Get the status of an application"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_THAW
name|String
name|DESCRIBE_ACTION_THAW
init|=
literal|"Start a stopped application"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_VERSION
name|String
name|DESCRIBE_ACTION_VERSION
init|=
literal|"Print the Slider version information"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_INSTALL_PACKAGE
name|String
name|DESCRIBE_ACTION_INSTALL_PACKAGE
init|=
literal|"Install application package."
operator|+
literal|" Deprecated, use '"
operator|+
name|ACTION_PACKAGE
operator|+
literal|" "
operator|+
name|ClientArgs
operator|.
name|ARG_INSTALL
operator|+
literal|"'."
decl_stmt|;
DECL|field|DESCRIBE_ACTION_PACKAGE
name|String
name|DESCRIBE_ACTION_PACKAGE
init|=
literal|"Install/list/delete application packages and list app instances that use the packages"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_CLIENT
name|String
name|DESCRIBE_ACTION_CLIENT
init|=
literal|"Install the application client in the specified directory or obtain a client keystore or truststore"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_INSTALL_KEYTAB
name|String
name|DESCRIBE_ACTION_INSTALL_KEYTAB
init|=
literal|"Install the Kerberos keytab."
operator|+
literal|" Deprecated, use '"
operator|+
name|ACTION_KEYTAB
operator|+
literal|" "
operator|+
name|ClientArgs
operator|.
name|ARG_INSTALL
operator|+
literal|"'."
decl_stmt|;
DECL|field|DESCRIBE_ACTION_KEYTAB
name|String
name|DESCRIBE_ACTION_KEYTAB
init|=
literal|"Manage a Kerberos keytab file (install, delete, list) in the sub-folder 'keytabs' of the user's Slider base directory"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_DIAGNOSTIC
name|String
name|DESCRIBE_ACTION_DIAGNOSTIC
init|=
literal|"Diagnose the configuration of the running slider application and slider client"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_RESOURCE
name|String
name|DESCRIBE_ACTION_RESOURCE
init|=
literal|"Manage a file (install, delete, list) in the 'resources' sub-folder of the user's Slider base directory"
decl_stmt|;
block|}
end_interface

end_unit

