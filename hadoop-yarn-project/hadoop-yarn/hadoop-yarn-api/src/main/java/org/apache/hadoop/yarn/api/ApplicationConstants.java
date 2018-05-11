begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Public
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Evolving
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
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
name|Shell
import|;
end_import

begin_comment
comment|/**  * This is the API for the applications comprising of constants that YARN sets  * up for the applications and the containers.  *  * TODO: Investigate the semantics and security of each cross-boundary refs.  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Evolving
DECL|interface|ApplicationConstants
specifier|public
interface|interface
name|ApplicationConstants
block|{
comment|/**    * The environment variable for APP_SUBMIT_TIME. Set in AppMaster environment    * only    */
DECL|field|APP_SUBMIT_TIME_ENV
specifier|public
specifier|static
specifier|final
name|String
name|APP_SUBMIT_TIME_ENV
init|=
literal|"APP_SUBMIT_TIME_ENV"
decl_stmt|;
comment|/**    * The cache file into which container token is written    */
DECL|field|CONTAINER_TOKEN_FILE_ENV_NAME
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_TOKEN_FILE_ENV_NAME
init|=
name|UserGroupInformation
operator|.
name|HADOOP_TOKEN_FILE_LOCATION
decl_stmt|;
comment|/**    * The environmental variable for APPLICATION_WEB_PROXY_BASE. Set in    * ApplicationMaster's environment only. This states that for all non-relative    * web URLs in the app masters web UI what base should they have.    */
DECL|field|APPLICATION_WEB_PROXY_BASE_ENV
specifier|public
specifier|static
specifier|final
name|String
name|APPLICATION_WEB_PROXY_BASE_ENV
init|=
literal|"APPLICATION_WEB_PROXY_BASE"
decl_stmt|;
comment|/**    * The temporary environmental variable for container log directory. This    * should be replaced by real container log directory on container launch.    */
DECL|field|LOG_DIR_EXPANSION_VAR
specifier|public
specifier|static
specifier|final
name|String
name|LOG_DIR_EXPANSION_VAR
init|=
literal|"<LOG_DIR>"
decl_stmt|;
comment|/**    * This constant is used to construct class path and it will be replaced with    * real class path separator(':' for Linux and ';' for Windows) by    * NodeManager on container launch. User has to use this constant to construct    * class path if user wants cross-platform practice i.e. submit an application    * from a Windows client to a Linux/Unix server or vice versa.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|field|CLASS_PATH_SEPARATOR
specifier|public
specifier|static
specifier|final
name|String
name|CLASS_PATH_SEPARATOR
init|=
literal|"<CPS>"
decl_stmt|;
comment|/**    * The following two constants are used to expand parameter and it will be    * replaced with real parameter expansion marker ('%' for Windows and '$' for    * Linux) by NodeManager on container launch. For example: {{VAR}} will be    * replaced as $VAR on Linux, and %VAR% on Windows. User has to use this    * constant to construct class path if user wants cross-platform practice i.e.    * submit an application from a Windows client to a Linux/Unix server or vice    * versa.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|field|PARAMETER_EXPANSION_LEFT
specifier|public
specifier|static
specifier|final
name|String
name|PARAMETER_EXPANSION_LEFT
init|=
literal|"{{"
decl_stmt|;
comment|/**    * User has to use this constant to construct class path if user wants    * cross-platform practice i.e. submit an application from a Windows client to    * a Linux/Unix server or vice versa.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|field|PARAMETER_EXPANSION_RIGHT
specifier|public
specifier|static
specifier|final
name|String
name|PARAMETER_EXPANSION_RIGHT
init|=
literal|"}}"
decl_stmt|;
DECL|field|STDERR
specifier|public
specifier|static
specifier|final
name|String
name|STDERR
init|=
literal|"stderr"
decl_stmt|;
DECL|field|STDOUT
specifier|public
specifier|static
specifier|final
name|String
name|STDOUT
init|=
literal|"stdout"
decl_stmt|;
comment|/**    * The type of launch for the container.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|enum|ContainerLaunchType
enum|enum
name|ContainerLaunchType
block|{
DECL|enumConstant|LAUNCH
name|LAUNCH
block|,
DECL|enumConstant|RELAUNCH
name|RELAUNCH
block|}
comment|/**    * Environment for Applications.    *    * Some of the environment variables for applications are<em>final</em>    * i.e. they cannot be modified by the applications.    */
DECL|enum|Environment
specifier|public
enum|enum
name|Environment
block|{
comment|/**      * $USER      * Final, non-modifiable.      */
DECL|enumConstant|USER
name|USER
argument_list|(
literal|"USER"
argument_list|)
block|,
comment|/**      * $LOGNAME      * Final, non-modifiable.      */
DECL|enumConstant|LOGNAME
name|LOGNAME
argument_list|(
literal|"LOGNAME"
argument_list|)
block|,
comment|/**      * $HOME      * Final, non-modifiable.      */
DECL|enumConstant|HOME
name|HOME
argument_list|(
literal|"HOME"
argument_list|)
block|,
comment|/**      * $PWD      * Final, non-modifiable.      */
DECL|enumConstant|PWD
name|PWD
argument_list|(
literal|"PWD"
argument_list|)
block|,
comment|/**      * $PATH      */
DECL|enumConstant|PATH
name|PATH
argument_list|(
literal|"PATH"
argument_list|)
block|,
comment|/**      * $SHELL      */
DECL|enumConstant|SHELL
name|SHELL
argument_list|(
literal|"SHELL"
argument_list|)
block|,
comment|/**      * $JAVA_HOME      */
DECL|enumConstant|JAVA_HOME
name|JAVA_HOME
argument_list|(
literal|"JAVA_HOME"
argument_list|)
block|,
comment|/**      * $CLASSPATH      */
DECL|enumConstant|CLASSPATH
name|CLASSPATH
argument_list|(
literal|"CLASSPATH"
argument_list|)
block|,
comment|/**      * $APP_CLASSPATH      */
DECL|enumConstant|APP_CLASSPATH
name|APP_CLASSPATH
argument_list|(
literal|"APP_CLASSPATH"
argument_list|)
block|,
comment|/**      * $LD_LIBRARY_PATH      */
DECL|enumConstant|LD_LIBRARY_PATH
name|LD_LIBRARY_PATH
argument_list|(
literal|"LD_LIBRARY_PATH"
argument_list|)
block|,
comment|/**      * $HADOOP_CONF_DIR      * Final, non-modifiable.      */
DECL|enumConstant|HADOOP_CONF_DIR
name|HADOOP_CONF_DIR
argument_list|(
literal|"HADOOP_CONF_DIR"
argument_list|)
block|,
comment|/**      * $HADOOP_COMMON_HOME      */
DECL|enumConstant|HADOOP_COMMON_HOME
name|HADOOP_COMMON_HOME
argument_list|(
literal|"HADOOP_COMMON_HOME"
argument_list|)
block|,
comment|/**      * $HADOOP_HDFS_HOME      */
DECL|enumConstant|HADOOP_HDFS_HOME
name|HADOOP_HDFS_HOME
argument_list|(
literal|"HADOOP_HDFS_HOME"
argument_list|)
block|,
comment|/**      * $MALLOC_ARENA_MAX      */
DECL|enumConstant|MALLOC_ARENA_MAX
name|MALLOC_ARENA_MAX
argument_list|(
literal|"MALLOC_ARENA_MAX"
argument_list|)
block|,
comment|/**      * $HADOOP_YARN_HOME      */
DECL|enumConstant|HADOOP_YARN_HOME
name|HADOOP_YARN_HOME
argument_list|(
literal|"HADOOP_YARN_HOME"
argument_list|)
block|,
comment|/**      * $CLASSPATH_PREPEND_DISTCACHE      * Private, Windows specific      */
DECL|enumConstant|Private
annotation|@
name|Private
DECL|enumConstant|CLASSPATH_PREPEND_DISTCACHE
name|CLASSPATH_PREPEND_DISTCACHE
argument_list|(
literal|"CLASSPATH_PREPEND_DISTCACHE"
argument_list|)
block|,
comment|/**      * $CONTAINER_ID      * Final, exported by NodeManager and non-modifiable by users.      */
DECL|enumConstant|CONTAINER_ID
name|CONTAINER_ID
argument_list|(
literal|"CONTAINER_ID"
argument_list|)
block|,
comment|/**      * $NM_HOST      * Final, exported by NodeManager and non-modifiable by users.      */
DECL|enumConstant|NM_HOST
name|NM_HOST
argument_list|(
literal|"NM_HOST"
argument_list|)
block|,
comment|/**      * $NM_HTTP_PORT      * Final, exported by NodeManager and non-modifiable by users.      */
DECL|enumConstant|NM_HTTP_PORT
name|NM_HTTP_PORT
argument_list|(
literal|"NM_HTTP_PORT"
argument_list|)
block|,
comment|/**      * $NM_PORT      * Final, exported by NodeManager and non-modifiable by users.      */
DECL|enumConstant|NM_PORT
name|NM_PORT
argument_list|(
literal|"NM_PORT"
argument_list|)
block|,
comment|/**      * $LOCAL_DIRS      * Final, exported by NodeManager and non-modifiable by users.      */
DECL|enumConstant|LOCAL_DIRS
name|LOCAL_DIRS
argument_list|(
literal|"LOCAL_DIRS"
argument_list|)
block|,
comment|/**      * $LOCAL_USER_DIRS      * Final, exported by NodeManager and non-modifiable by users.      */
DECL|enumConstant|LOCAL_USER_DIRS
name|LOCAL_USER_DIRS
argument_list|(
literal|"LOCAL_USER_DIRS"
argument_list|)
block|,
comment|/**      * $LOG_DIRS      * Final, exported by NodeManager and non-modifiable by users.      * Comma separate list of directories that the container should use for      * logging.      */
DECL|enumConstant|LOG_DIRS
name|LOG_DIRS
argument_list|(
literal|"LOG_DIRS"
argument_list|)
block|,
comment|/**      * $YARN_CONTAINER_RUNTIME_DOCKER_RUN_OVERRIDE_DISABLE      * Final, Docker run support ENTRY_POINT.      */
DECL|enumConstant|YARN_CONTAINER_RUNTIME_DOCKER_RUN_OVERRIDE_DISABLE
name|YARN_CONTAINER_RUNTIME_DOCKER_RUN_OVERRIDE_DISABLE
argument_list|(
literal|"YARN_CONTAINER_RUNTIME_DOCKER_RUN_OVERRIDE_DISABLE"
argument_list|)
block|;
DECL|field|variable
specifier|private
specifier|final
name|String
name|variable
decl_stmt|;
DECL|method|Environment (String variable)
specifier|private
name|Environment
parameter_list|(
name|String
name|variable
parameter_list|)
block|{
name|this
operator|.
name|variable
operator|=
name|variable
expr_stmt|;
block|}
DECL|method|key ()
specifier|public
name|String
name|key
parameter_list|()
block|{
return|return
name|variable
return|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|variable
return|;
block|}
comment|/**      * Expand the environment variable based on client OS environment variable      * expansion syntax (e.g. $VAR for Linux and %VAR% for Windows).      *<p>      * Note: Use $$() method for cross-platform practice i.e. submit an      * application from a Windows client to a Linux/Unix server or vice versa.      *</p>      * @return expanded environment variable.      */
DECL|method|$ ()
specifier|public
name|String
name|$
parameter_list|()
block|{
if|if
condition|(
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
return|return
literal|"%"
operator|+
name|variable
operator|+
literal|"%"
return|;
block|}
else|else
block|{
return|return
literal|"$"
operator|+
name|variable
return|;
block|}
block|}
comment|/**      * Expand the environment variable in platform-agnostic syntax. The      * parameter expansion marker "{{VAR}}" will be replaced with real parameter      * expansion marker ('%' for Windows and '$' for Linux) by NodeManager on      * container launch. For example: {{VAR}} will be replaced as $VAR on Linux,      * and %VAR% on Windows.      * @return expanded environment variable.      */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|$$ ()
specifier|public
name|String
name|$$
parameter_list|()
block|{
return|return
name|PARAMETER_EXPANSION_LEFT
operator|+
name|variable
operator|+
name|PARAMETER_EXPANSION_RIGHT
return|;
block|}
block|}
block|}
end_interface

end_unit

