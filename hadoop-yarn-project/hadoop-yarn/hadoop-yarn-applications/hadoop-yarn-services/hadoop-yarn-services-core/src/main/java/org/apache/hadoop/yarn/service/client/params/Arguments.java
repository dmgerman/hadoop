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
comment|/**  * Here are all the arguments that may be parsed by the client or server  * command lines.   *   * Important: Please keep the main list in alphabetical order  * so it is easier to see what arguments are there  */
end_comment

begin_interface
DECL|interface|Arguments
specifier|public
interface|interface
name|Arguments
block|{
DECL|field|ARG_FILE
name|String
name|ARG_FILE
init|=
literal|"--file"
decl_stmt|;
DECL|field|ARG_FILE_SHORT
name|String
name|ARG_FILE_SHORT
init|=
literal|"-f"
decl_stmt|;
DECL|field|ARG_BASE_PATH
name|String
name|ARG_BASE_PATH
init|=
literal|"--basepath"
decl_stmt|;
DECL|field|ARG_COMPONENT
name|String
name|ARG_COMPONENT
init|=
literal|"--component"
decl_stmt|;
DECL|field|ARG_COMPONENT_SHORT
name|String
name|ARG_COMPONENT_SHORT
init|=
literal|"--comp"
decl_stmt|;
DECL|field|ARG_COMPONENTS
name|String
name|ARG_COMPONENTS
init|=
literal|"--components"
decl_stmt|;
DECL|field|ARG_COMP_OPT
name|String
name|ARG_COMP_OPT
init|=
literal|"--compopt"
decl_stmt|;
DECL|field|ARG_COMP_OPT_SHORT
name|String
name|ARG_COMP_OPT_SHORT
init|=
literal|"--co"
decl_stmt|;
DECL|field|ARG_CONFIG
name|String
name|ARG_CONFIG
init|=
literal|"--config"
decl_stmt|;
DECL|field|ARG_CONTAINERS
name|String
name|ARG_CONTAINERS
init|=
literal|"--containers"
decl_stmt|;
DECL|field|ARG_DEBUG
name|String
name|ARG_DEBUG
init|=
literal|"--debug"
decl_stmt|;
DECL|field|ARG_DEFINE
name|String
name|ARG_DEFINE
init|=
literal|"-D"
decl_stmt|;
DECL|field|ARG_DELETE
name|String
name|ARG_DELETE
init|=
literal|"--delete"
decl_stmt|;
DECL|field|ARG_DEST
name|String
name|ARG_DEST
init|=
literal|"--dest"
decl_stmt|;
DECL|field|ARG_DESTDIR
name|String
name|ARG_DESTDIR
init|=
literal|"--destdir"
decl_stmt|;
DECL|field|ARG_EXAMPLE
name|String
name|ARG_EXAMPLE
init|=
literal|"--example"
decl_stmt|;
DECL|field|ARG_EXAMPLE_SHORT
name|String
name|ARG_EXAMPLE_SHORT
init|=
literal|"-e"
decl_stmt|;
DECL|field|ARG_FOLDER
name|String
name|ARG_FOLDER
init|=
literal|"--folder"
decl_stmt|;
DECL|field|ARG_FORCE
name|String
name|ARG_FORCE
init|=
literal|"--force"
decl_stmt|;
DECL|field|ARG_FORMAT
name|String
name|ARG_FORMAT
init|=
literal|"--format"
decl_stmt|;
DECL|field|ARG_GETCONF
name|String
name|ARG_GETCONF
init|=
literal|"--getconf"
decl_stmt|;
DECL|field|ARG_GETEXP
name|String
name|ARG_GETEXP
init|=
literal|"--getexp"
decl_stmt|;
DECL|field|ARG_GETFILES
name|String
name|ARG_GETFILES
init|=
literal|"--getfiles"
decl_stmt|;
DECL|field|ARG_HELP
name|String
name|ARG_HELP
init|=
literal|"--help"
decl_stmt|;
DECL|field|ARG_IMAGE
name|String
name|ARG_IMAGE
init|=
literal|"--image"
decl_stmt|;
DECL|field|ARG_INSTALL
name|String
name|ARG_INSTALL
init|=
literal|"--install"
decl_stmt|;
DECL|field|ARG_INTERNAL
name|String
name|ARG_INTERNAL
init|=
literal|"--internal"
decl_stmt|;
DECL|field|ARG_KEYLEN
name|String
name|ARG_KEYLEN
init|=
literal|"--keylen"
decl_stmt|;
DECL|field|ARG_KEYTAB
name|String
name|ARG_KEYTAB
init|=
literal|"--keytab"
decl_stmt|;
DECL|field|ARG_KEYTABINSTALL
name|String
name|ARG_KEYTABINSTALL
init|=
name|ARG_INSTALL
decl_stmt|;
DECL|field|ARG_KEYTABDELETE
name|String
name|ARG_KEYTABDELETE
init|=
name|ARG_DELETE
decl_stmt|;
DECL|field|ARG_KEYTABLIST
name|String
name|ARG_KEYTABLIST
init|=
literal|"--list"
decl_stmt|;
DECL|field|ARG_LIST
name|String
name|ARG_LIST
init|=
literal|"--list"
decl_stmt|;
DECL|field|ARG_LISTCONF
name|String
name|ARG_LISTCONF
init|=
literal|"--listconf"
decl_stmt|;
DECL|field|ARG_LISTEXP
name|String
name|ARG_LISTEXP
init|=
literal|"--listexp"
decl_stmt|;
DECL|field|ARG_LISTFILES
name|String
name|ARG_LISTFILES
init|=
literal|"--listfiles"
decl_stmt|;
DECL|field|ARG_LIVE
name|String
name|ARG_LIVE
init|=
literal|"--live"
decl_stmt|;
DECL|field|ARG_MANAGER
name|String
name|ARG_MANAGER
init|=
literal|"--manager"
decl_stmt|;
DECL|field|ARG_MANAGER_SHORT
name|String
name|ARG_MANAGER_SHORT
init|=
literal|"--m"
decl_stmt|;
DECL|field|ARG_MESSAGE
name|String
name|ARG_MESSAGE
init|=
literal|"--message"
decl_stmt|;
DECL|field|ARG_NAME
name|String
name|ARG_NAME
init|=
literal|"--name"
decl_stmt|;
DECL|field|ARG_OPTION
name|String
name|ARG_OPTION
init|=
literal|"--option"
decl_stmt|;
DECL|field|ARG_OPTION_SHORT
name|String
name|ARG_OPTION_SHORT
init|=
literal|"-O"
decl_stmt|;
DECL|field|ARG_OUTPUT
name|String
name|ARG_OUTPUT
init|=
literal|"--out"
decl_stmt|;
DECL|field|ARG_OUTPUT_SHORT
name|String
name|ARG_OUTPUT_SHORT
init|=
literal|"-o"
decl_stmt|;
DECL|field|ARG_OVERWRITE
name|String
name|ARG_OVERWRITE
init|=
literal|"--overwrite"
decl_stmt|;
DECL|field|ARG_PACKAGE
name|String
name|ARG_PACKAGE
init|=
literal|"--package"
decl_stmt|;
DECL|field|ARG_PATH
name|String
name|ARG_PATH
init|=
literal|"--path"
decl_stmt|;
DECL|field|ARG_PRINCIPAL
name|String
name|ARG_PRINCIPAL
init|=
literal|"--principal"
decl_stmt|;
DECL|field|ARG_QUEUE
name|String
name|ARG_QUEUE
init|=
literal|"--queue"
decl_stmt|;
DECL|field|ARG_SHORT_QUEUE
name|String
name|ARG_SHORT_QUEUE
init|=
literal|"-q"
decl_stmt|;
DECL|field|ARG_LIFETIME
name|String
name|ARG_LIFETIME
init|=
literal|"--lifetime"
decl_stmt|;
DECL|field|ARG_RESOURCE
name|String
name|ARG_RESOURCE
init|=
literal|"--resource"
decl_stmt|;
DECL|field|ARG_RESOURCE_MANAGER
name|String
name|ARG_RESOURCE_MANAGER
init|=
literal|"--rm"
decl_stmt|;
DECL|field|ARG_SECURE
name|String
name|ARG_SECURE
init|=
literal|"--secure"
decl_stmt|;
DECL|field|ARG_SERVICETYPE
name|String
name|ARG_SERVICETYPE
init|=
literal|"--servicetype"
decl_stmt|;
DECL|field|ARG_SERVICES
name|String
name|ARG_SERVICES
init|=
literal|"--services"
decl_stmt|;
DECL|field|ARG_SOURCE
name|String
name|ARG_SOURCE
init|=
literal|"--source"
decl_stmt|;
DECL|field|ARG_STATE
name|String
name|ARG_STATE
init|=
literal|"--state"
decl_stmt|;
DECL|field|ARG_SYSPROP
name|String
name|ARG_SYSPROP
init|=
literal|"-S"
decl_stmt|;
DECL|field|ARG_USER
name|String
name|ARG_USER
init|=
literal|"--user"
decl_stmt|;
DECL|field|ARG_UPLOAD
name|String
name|ARG_UPLOAD
init|=
literal|"--upload"
decl_stmt|;
DECL|field|ARG_VERBOSE
name|String
name|ARG_VERBOSE
init|=
literal|"--verbose"
decl_stmt|;
DECL|field|ARG_VERSION
name|String
name|ARG_VERSION
init|=
literal|"--version"
decl_stmt|;
DECL|field|ARG_WAIT
name|String
name|ARG_WAIT
init|=
literal|"--wait"
decl_stmt|;
comment|/*  STOP: DO NOT ADD YOUR ARGUMENTS HERE. GO BACK AND INSERT THEM IN THE  RIGHT PLACE IN THE LIST  */
comment|// Tha path in hdfs to be read by Service AM
DECL|field|ARG_SERVICE_DEF_PATH
name|String
name|ARG_SERVICE_DEF_PATH
init|=
literal|"-cluster-uri"
decl_stmt|;
block|}
end_interface

end_unit

