begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
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
import|;
end_import

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|MRConstants
specifier|public
interface|interface
name|MRConstants
block|{
DECL|field|YARN_MR_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|YARN_MR_PREFIX
init|=
literal|"yarn.mapreduce.job."
decl_stmt|;
comment|// This should be the directory where splits file gets localized on the node
comment|// running ApplicationMaster.
DECL|field|JOB_SUBMIT_DIR
specifier|public
specifier|static
specifier|final
name|String
name|JOB_SUBMIT_DIR
init|=
literal|"jobSubmitDir"
decl_stmt|;
comment|// This should be the name of the localized job-configuration file on the node
comment|// running ApplicationMaster and Task
DECL|field|JOB_CONF_FILE
specifier|public
specifier|static
specifier|final
name|String
name|JOB_CONF_FILE
init|=
literal|"job.xml"
decl_stmt|;
comment|// This should be the name of the localized job-jar file on the node running
comment|// individual containers/tasks.
DECL|field|JOB_JAR
specifier|public
specifier|static
specifier|final
name|String
name|JOB_JAR
init|=
literal|"job.jar"
decl_stmt|;
DECL|field|HADOOP_MAPREDUCE_CLIENT_APP_JAR_NAME
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_MAPREDUCE_CLIENT_APP_JAR_NAME
init|=
literal|"hadoop-mapreduce-client-app-0.24.0-SNAPSHOT.jar"
decl_stmt|;
DECL|field|YARN_MAPREDUCE_APP_JAR_PATH
specifier|public
specifier|static
specifier|final
name|String
name|YARN_MAPREDUCE_APP_JAR_PATH
init|=
literal|"$YARN_HOME/modules/"
operator|+
name|HADOOP_MAPREDUCE_CLIENT_APP_JAR_NAME
decl_stmt|;
DECL|field|APPS_STAGING_DIR_KEY
specifier|public
specifier|static
specifier|final
name|String
name|APPS_STAGING_DIR_KEY
init|=
literal|"yarn.apps.stagingDir"
decl_stmt|;
comment|// The token file for the application. Should contain tokens for access to
comment|// remote file system and may optionally contain application specific tokens.
comment|// For now, generated by the AppManagers and used by NodeManagers and the
comment|// Containers.
DECL|field|APPLICATION_TOKENS_FILE
specifier|public
specifier|static
specifier|final
name|String
name|APPLICATION_TOKENS_FILE
init|=
literal|"appTokens"
decl_stmt|;
block|}
end_interface

end_unit

