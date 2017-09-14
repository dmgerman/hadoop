begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.client.params
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|client
operator|.
name|params
package|;
end_package

begin_comment
comment|/**  * Actions by client  */
end_comment

begin_interface
DECL|interface|SliderActions
specifier|public
interface|interface
name|SliderActions
block|{
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
DECL|field|ACTION_KEYTAB
name|String
name|ACTION_KEYTAB
init|=
literal|"keytab"
decl_stmt|;
DECL|field|ACTION_LIST
name|String
name|ACTION_LIST
init|=
literal|"list"
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
DECL|field|DESCRIBE_ACTION_BUILD
name|String
name|DESCRIBE_ACTION_BUILD
init|=
literal|"Build a service specification, but do not start it"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_CREATE
name|String
name|DESCRIBE_ACTION_CREATE
init|=
literal|"Create a service, it's equivalent to first invoke build and then start"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_DEPENDENCY
name|String
name|DESCRIBE_ACTION_DEPENDENCY
init|=
literal|"Yarn service framework dependency (libraries) management"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_UPDATE
name|String
name|DESCRIBE_ACTION_UPDATE
init|=
literal|"Update template for service"
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
literal|"Destroy a stopped service, service must be stopped first before destroying."
decl_stmt|;
DECL|field|DESCRIBE_ACTION_EXISTS
name|String
name|DESCRIBE_ACTION_EXISTS
init|=
literal|"Probe for a service running"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_FLEX
name|String
name|DESCRIBE_ACTION_FLEX
init|=
literal|"Flex a service's component by increasing or decreasing the number of containers."
decl_stmt|;
DECL|field|DESCRIBE_ACTION_FREEZE
name|String
name|DESCRIBE_ACTION_FREEZE
init|=
literal|"Stop a running service"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_KDIAG
name|String
name|DESCRIBE_ACTION_KDIAG
init|=
literal|"Diagnose Kerberos problems"
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
literal|"List running services"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_REGISTRY
name|String
name|DESCRIBE_ACTION_REGISTRY
init|=
literal|"Query the registry of a service"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_STATUS
name|String
name|DESCRIBE_ACTION_STATUS
init|=
literal|"Get the status of a service"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_THAW
name|String
name|DESCRIBE_ACTION_THAW
init|=
literal|"Start a service with pre-built specification or a previously stopped service"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_CLIENT
name|String
name|DESCRIBE_ACTION_CLIENT
init|=
literal|"Install the service client in the specified directory or obtain a client keystore or truststore"
decl_stmt|;
DECL|field|DESCRIBE_ACTION_KEYTAB
name|String
name|DESCRIBE_ACTION_KEYTAB
init|=
literal|"Manage a Kerberos keytab file (install, delete, list) in the sub-folder 'keytabs' of the user's Slider base directory"
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

