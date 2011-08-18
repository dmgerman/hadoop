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
comment|// TODO: Weird. This is part of AM command line. Instead it should be a env.
DECL|field|AM_FAIL_COUNT_STRING
specifier|public
specifier|static
specifier|final
name|String
name|AM_FAIL_COUNT_STRING
init|=
literal|"<FAILCOUNT>"
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
DECL|field|LOG_DIR_EXPANSION_VAR
specifier|public
specifier|static
specifier|final
name|String
name|LOG_DIR_EXPANSION_VAR
init|=
literal|"<LOG_DIR>"
decl_stmt|;
block|}
end_interface

end_unit

