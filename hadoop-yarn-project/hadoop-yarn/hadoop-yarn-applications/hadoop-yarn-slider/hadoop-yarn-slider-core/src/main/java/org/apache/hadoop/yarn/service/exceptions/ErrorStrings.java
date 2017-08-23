begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.exceptions
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
name|exceptions
package|;
end_package

begin_interface
DECL|interface|ErrorStrings
specifier|public
interface|interface
name|ErrorStrings
block|{
DECL|field|E_UNSTABLE_CLUSTER
name|String
name|E_UNSTABLE_CLUSTER
init|=
literal|"Unstable Application Instance :"
decl_stmt|;
DECL|field|E_CLUSTER_RUNNING
name|String
name|E_CLUSTER_RUNNING
init|=
literal|"Application Instance running"
decl_stmt|;
DECL|field|E_ALREADY_EXISTS
name|String
name|E_ALREADY_EXISTS
init|=
literal|"already exists"
decl_stmt|;
DECL|field|PRINTF_E_INSTANCE_ALREADY_EXISTS
name|String
name|PRINTF_E_INSTANCE_ALREADY_EXISTS
init|=
literal|"Application Instance \"%s\" already exists and is defined in %s"
decl_stmt|;
DECL|field|PRINTF_E_INSTANCE_DIR_ALREADY_EXISTS
name|String
name|PRINTF_E_INSTANCE_DIR_ALREADY_EXISTS
init|=
literal|"Application Instance dir already exists: %s"
decl_stmt|;
DECL|field|E_MISSING_PATH
name|String
name|E_MISSING_PATH
init|=
literal|"Missing path "
decl_stmt|;
DECL|field|E_INCOMPLETE_CLUSTER_SPEC
name|String
name|E_INCOMPLETE_CLUSTER_SPEC
init|=
literal|"Cluster specification is marked as incomplete: "
decl_stmt|;
DECL|field|E_UNKNOWN_INSTANCE
name|String
name|E_UNKNOWN_INSTANCE
init|=
literal|"Unknown application instance "
decl_stmt|;
DECL|field|E_DESTROY_CREATE_RACE_CONDITION
name|String
name|E_DESTROY_CREATE_RACE_CONDITION
init|=
literal|"created while it was being destroyed"
decl_stmt|;
DECL|field|E_UNKNOWN_ROLE
name|String
name|E_UNKNOWN_ROLE
init|=
literal|"Unknown role "
decl_stmt|;
comment|/**    * ERROR Strings    */
DECL|field|ERROR_NO_ACTION
name|String
name|ERROR_NO_ACTION
init|=
literal|"No action specified"
decl_stmt|;
DECL|field|ERROR_UNKNOWN_ACTION
name|String
name|ERROR_UNKNOWN_ACTION
init|=
literal|"Unknown command: "
decl_stmt|;
DECL|field|ERROR_NOT_ENOUGH_ARGUMENTS
name|String
name|ERROR_NOT_ENOUGH_ARGUMENTS
init|=
literal|"Not enough arguments for action: "
decl_stmt|;
DECL|field|ERROR_PARSE_FAILURE
name|String
name|ERROR_PARSE_FAILURE
init|=
literal|"Failed to parse "
decl_stmt|;
comment|/**    * All the remaining values after argument processing    */
DECL|field|ERROR_TOO_MANY_ARGUMENTS
name|String
name|ERROR_TOO_MANY_ARGUMENTS
init|=
literal|"Too many arguments"
decl_stmt|;
DECL|field|ERROR_DUPLICATE_ENTRY
name|String
name|ERROR_DUPLICATE_ENTRY
init|=
literal|"Duplicate entry for "
decl_stmt|;
DECL|field|E_APPLICATION_NOT_RUNNING
name|String
name|E_APPLICATION_NOT_RUNNING
init|=
literal|"Application not running"
decl_stmt|;
DECL|field|E_FINISHED_APPLICATION
name|String
name|E_FINISHED_APPLICATION
init|=
name|E_APPLICATION_NOT_RUNNING
operator|+
literal|": %s state=%s "
decl_stmt|;
DECL|field|E_NO_IMAGE_OR_HOME_DIR_SPECIFIED
name|String
name|E_NO_IMAGE_OR_HOME_DIR_SPECIFIED
init|=
literal|"Neither an image path nor binary home directory were specified"
decl_stmt|;
DECL|field|E_BOTH_IMAGE_AND_HOME_DIR_SPECIFIED
name|String
name|E_BOTH_IMAGE_AND_HOME_DIR_SPECIFIED
init|=
literal|"Both application image path and home dir have been provided"
decl_stmt|;
DECL|field|E_CONFIGURATION_DIRECTORY_NOT_FOUND
name|String
name|E_CONFIGURATION_DIRECTORY_NOT_FOUND
init|=
literal|"Configuration directory \"%s\" not found"
decl_stmt|;
block|}
end_interface

end_unit

