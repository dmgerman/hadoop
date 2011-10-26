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
name|security
operator|.
name|UserGroupInformation
import|;
end_import

begin_comment
comment|/**  * This is the API for the applications comprising of constants that YARN sets  * up for the applications and the containers.  *   * TODO: Should also be defined in avro/pb IDLs  * TODO: Investigate the semantics and security of each cross-boundary refs.  */
end_comment

begin_interface
DECL|interface|ApplicationConstants
specifier|public
interface|interface
name|ApplicationConstants
block|{
comment|// TODO: They say tokens via env isn't good.
DECL|field|APPLICATION_MASTER_TOKEN_ENV_NAME
specifier|public
specifier|static
specifier|final
name|String
name|APPLICATION_MASTER_TOKEN_ENV_NAME
init|=
literal|"AppMasterTokenEnv"
decl_stmt|;
comment|// TODO: They say tokens via env isn't good.
DECL|field|APPLICATION_CLIENT_SECRET_ENV_NAME
specifier|public
specifier|static
specifier|final
name|String
name|APPLICATION_CLIENT_SECRET_ENV_NAME
init|=
literal|"AppClientTokenEnv"
decl_stmt|;
comment|/**    * The environment variable for CONTAINER_ID. Set in AppMaster environment    * only    */
DECL|field|AM_CONTAINER_ID_ENV
specifier|public
specifier|static
specifier|final
name|String
name|AM_CONTAINER_ID_ENV
init|=
literal|"AM_CONTAINER_ID"
decl_stmt|;
comment|/**    * The environment variable for NM_HTTP_ADDRESS. Set in AppMaster environment    * only    */
DECL|field|NM_HTTP_ADDRESS_ENV
specifier|public
specifier|static
specifier|final
name|String
name|NM_HTTP_ADDRESS_ENV
init|=
literal|"NM_HTTP_ADDRESS"
decl_stmt|;
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
DECL|field|LOCAL_DIR_ENV
specifier|public
specifier|static
specifier|final
name|String
name|LOCAL_DIR_ENV
init|=
literal|"YARN_LOCAL_DIRS"
decl_stmt|;
comment|/**    * The environmental variable for APPLICATION_WEB_PROXY_BASE. Set in     * ApplicationMaster's environment only. This states that for all non-relative    * web URLs in the app masters web UI what base should they have.    */
DECL|field|APPLICATION_WEB_PROXY_BASE_ENV
specifier|public
specifier|static
specifier|final
name|String
name|APPLICATION_WEB_PROXY_BASE_ENV
init|=
literal|"APPLICATION_WEB_PROXY_BASE"
decl_stmt|;
DECL|field|LOG_DIR_EXPANSION_VAR
specifier|public
specifier|static
specifier|final
name|String
name|LOG_DIR_EXPANSION_VAR
init|=
literal|"<LOG_DIR>"
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
comment|/**    * Classpath for typical applications.    */
DECL|field|APPLICATION_CLASSPATH
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|APPLICATION_CLASSPATH
init|=
operator|new
name|String
index|[]
block|{
literal|"$HADOOP_CONF_DIR"
block|,
literal|"$HADOOP_COMMON_HOME/share/hadoop/common/*"
block|,
literal|"$HADOOP_COMMON_HOME/share/hadoop/common/lib/*"
block|,
literal|"$HADOOP_HDFS_HOME/share/hadoop/hdfs/*"
block|,
literal|"$HADOOP_HDFS_HOME/share/hadoop/hdfs/lib/*"
block|,
literal|"$YARN_HOME/modules/*"
block|,
literal|"$YARN_HOME/lib/*"
block|}
decl_stmt|;
comment|/**    * Environment for Applications.    *     * Some of the environment variables for applications are<em>final</em>     * i.e. they cannot be modified by the applications.    */
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
comment|/**      * $YARN_HOME      */
DECL|enumConstant|YARN_HOME
name|YARN_HOME
argument_list|(
literal|"YARN_HOME"
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
DECL|method|$ ()
specifier|public
name|String
name|$
parameter_list|()
block|{
return|return
literal|"$"
operator|+
name|variable
return|;
block|}
block|}
block|}
end_interface

end_unit

