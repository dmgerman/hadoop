begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.providers.agent
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
operator|.
name|agent
package|;
end_package

begin_comment
comment|/*   */
end_comment

begin_interface
DECL|interface|AgentKeys
specifier|public
interface|interface
name|AgentKeys
block|{
DECL|field|AGENT_TAR
name|String
name|AGENT_TAR
init|=
literal|"slider-agent.tar.gz"
decl_stmt|;
DECL|field|PROVIDER_AGENT
name|String
name|PROVIDER_AGENT
init|=
literal|"agent"
decl_stmt|;
DECL|field|ROLE_NODE
name|String
name|ROLE_NODE
init|=
literal|"echo"
decl_stmt|;
comment|/**    * Template stored in the slider classpath -to use if there is    * no site-specific template    * {@value}    */
DECL|field|CONF_RESOURCE
name|String
name|CONF_RESOURCE
init|=
literal|"org/apache/slider/providers/agent/conf/"
decl_stmt|;
comment|/*  URL to talk back to Agent Controller*/
DECL|field|CONTROLLER_URL
name|String
name|CONTROLLER_URL
init|=
literal|"agent.controller.url"
decl_stmt|;
comment|/**    * The location of pre-installed agent path.    * This can be also be dynamically computed based on Yarn installation of agent.    */
DECL|field|PACKAGE_PATH
name|String
name|PACKAGE_PATH
init|=
literal|"agent.package.root"
decl_stmt|;
comment|/**    * The location of the script implementing the command.    */
DECL|field|SCRIPT_PATH
name|String
name|SCRIPT_PATH
init|=
literal|"agent.script"
decl_stmt|;
comment|/**    * Execution home for the agent.    */
DECL|field|APP_HOME
name|String
name|APP_HOME
init|=
literal|"app.home"
decl_stmt|;
DECL|field|APP_ROOT
name|String
name|APP_ROOT
init|=
literal|"site.global.app_root"
decl_stmt|;
DECL|field|APP_CLIENT_ROOT
name|String
name|APP_CLIENT_ROOT
init|=
literal|"client_root"
decl_stmt|;
comment|/**    * Runas user of the application    */
DECL|field|RUNAS_USER
name|String
name|RUNAS_USER
init|=
literal|"site.global.app_user"
decl_stmt|;
comment|/**    * Name of the service.    */
DECL|field|SERVICE_NAME
name|String
name|SERVICE_NAME
init|=
literal|"app.name"
decl_stmt|;
DECL|field|ARG_LABEL
name|String
name|ARG_LABEL
init|=
literal|"--label"
decl_stmt|;
DECL|field|ARG_HOST
name|String
name|ARG_HOST
init|=
literal|"--host"
decl_stmt|;
DECL|field|ARG_PORT
name|String
name|ARG_PORT
init|=
literal|"--port"
decl_stmt|;
DECL|field|ARG_SECURED_PORT
name|String
name|ARG_SECURED_PORT
init|=
literal|"--secured_port"
decl_stmt|;
DECL|field|ARG_ZOOKEEPER_QUORUM
name|String
name|ARG_ZOOKEEPER_QUORUM
init|=
literal|"--zk-quorum"
decl_stmt|;
DECL|field|ARG_ZOOKEEPER_REGISTRY_PATH
name|String
name|ARG_ZOOKEEPER_REGISTRY_PATH
init|=
literal|"--zk-reg-path"
decl_stmt|;
DECL|field|ARG_DEBUG
name|String
name|ARG_DEBUG
init|=
literal|"--debug"
decl_stmt|;
DECL|field|AGENT_MAIN_SCRIPT_ROOT
name|String
name|AGENT_MAIN_SCRIPT_ROOT
init|=
literal|"./infra/agent/slider-agent/"
decl_stmt|;
DECL|field|AGENT_JINJA2_ROOT
name|String
name|AGENT_JINJA2_ROOT
init|=
literal|"./infra/agent/slider-agent/jinja2"
decl_stmt|;
DECL|field|AGENT_MAIN_SCRIPT
name|String
name|AGENT_MAIN_SCRIPT
init|=
literal|"agent/main.py"
decl_stmt|;
DECL|field|APP_DEF
name|String
name|APP_DEF
init|=
literal|"application.def"
decl_stmt|;
DECL|field|APP_DEF_ORIGINAL
name|String
name|APP_DEF_ORIGINAL
init|=
literal|"application.def.original"
decl_stmt|;
DECL|field|ADDON_PREFIX
name|String
name|ADDON_PREFIX
init|=
literal|"application.addon."
decl_stmt|;
DECL|field|ADDONS
name|String
name|ADDONS
init|=
literal|"application.addons"
decl_stmt|;
DECL|field|AGENT_VERSION
name|String
name|AGENT_VERSION
init|=
literal|"agent.version"
decl_stmt|;
DECL|field|AGENT_CONF
name|String
name|AGENT_CONF
init|=
literal|"agent.conf"
decl_stmt|;
DECL|field|ADDON_FOR_ALL_COMPONENTS
name|String
name|ADDON_FOR_ALL_COMPONENTS
init|=
literal|"ALL"
decl_stmt|;
DECL|field|AGENT_INSTALL_DIR
name|String
name|AGENT_INSTALL_DIR
init|=
literal|"infra/agent"
decl_stmt|;
DECL|field|APP_DEFINITION_DIR
name|String
name|APP_DEFINITION_DIR
init|=
literal|"app/definition"
decl_stmt|;
DECL|field|ADDON_DEFINITION_DIR
name|String
name|ADDON_DEFINITION_DIR
init|=
literal|"addon/definition"
decl_stmt|;
DECL|field|AGENT_CONFIG_FILE
name|String
name|AGENT_CONFIG_FILE
init|=
literal|"infra/conf/agent.ini"
decl_stmt|;
DECL|field|AGENT_VERSION_FILE
name|String
name|AGENT_VERSION_FILE
init|=
literal|"infra/version"
decl_stmt|;
DECL|field|PACKAGE_LIST
name|String
name|PACKAGE_LIST
init|=
literal|"package_list"
decl_stmt|;
DECL|field|WAIT_HEARTBEAT
name|String
name|WAIT_HEARTBEAT
init|=
literal|"wait.heartbeat"
decl_stmt|;
DECL|field|PYTHON_EXE
name|String
name|PYTHON_EXE
init|=
literal|"python"
decl_stmt|;
DECL|field|CREATE_DEF_ZK_NODE
name|String
name|CREATE_DEF_ZK_NODE
init|=
literal|"create.default.zookeeper.node"
decl_stmt|;
DECL|field|HEARTBEAT_MONITOR_INTERVAL
name|String
name|HEARTBEAT_MONITOR_INTERVAL
init|=
literal|"heartbeat.monitor.interval"
decl_stmt|;
DECL|field|AGENT_INSTANCE_DEBUG_DATA
name|String
name|AGENT_INSTANCE_DEBUG_DATA
init|=
literal|"agent.instance.debug.data"
decl_stmt|;
DECL|field|AGENT_OUT_FILE
name|String
name|AGENT_OUT_FILE
init|=
literal|"slider-agent.out"
decl_stmt|;
DECL|field|KEY_AGENT_TWO_WAY_SSL_ENABLED
name|String
name|KEY_AGENT_TWO_WAY_SSL_ENABLED
init|=
literal|"ssl.server.client.auth"
decl_stmt|;
DECL|field|INFRA_RUN_SECURITY_DIR
name|String
name|INFRA_RUN_SECURITY_DIR
init|=
literal|"infra/run/security/"
decl_stmt|;
DECL|field|CERT_FILE_LOCALIZATION_PATH
name|String
name|CERT_FILE_LOCALIZATION_PATH
init|=
name|INFRA_RUN_SECURITY_DIR
operator|+
literal|"ca.crt"
decl_stmt|;
DECL|field|KEY_CONTAINER_LAUNCH_DELAY
name|String
name|KEY_CONTAINER_LAUNCH_DELAY
init|=
literal|"container.launch.delay.sec"
decl_stmt|;
DECL|field|TEST_RELAX_VERIFICATION
name|String
name|TEST_RELAX_VERIFICATION
init|=
literal|"test.relax.validation"
decl_stmt|;
DECL|field|DEFAULT_METAINFO_MAP_KEY
name|String
name|DEFAULT_METAINFO_MAP_KEY
init|=
literal|"DEFAULT_KEY"
decl_stmt|;
block|}
end_interface

end_unit

