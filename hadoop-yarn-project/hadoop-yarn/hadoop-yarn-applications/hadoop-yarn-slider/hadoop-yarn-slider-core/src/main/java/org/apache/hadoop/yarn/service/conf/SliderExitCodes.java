begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.conf
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
name|conf
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
name|yarn
operator|.
name|service
operator|.
name|exceptions
operator|.
name|LauncherExitCodes
import|;
end_import

begin_interface
DECL|interface|SliderExitCodes
specifier|public
interface|interface
name|SliderExitCodes
extends|extends
name|LauncherExitCodes
block|{
comment|/**    * starting point for exit codes; not an exception itself    */
DECL|field|_EXIT_CODE_BASE
name|int
name|_EXIT_CODE_BASE
init|=
literal|64
decl_stmt|;
comment|/**    * service entered the failed state: {@value}    */
DECL|field|EXIT_YARN_SERVICE_FAILED
name|int
name|EXIT_YARN_SERVICE_FAILED
init|=
literal|65
decl_stmt|;
comment|/**    * service was killed: {@value}    */
DECL|field|EXIT_YARN_SERVICE_KILLED
name|int
name|EXIT_YARN_SERVICE_KILLED
init|=
literal|66
decl_stmt|;
comment|/**    * timeout on monitoring client: {@value}    */
DECL|field|EXIT_TIMED_OUT
name|int
name|EXIT_TIMED_OUT
init|=
literal|67
decl_stmt|;
comment|/**    * service finished with an error: {@value}    */
DECL|field|EXIT_YARN_SERVICE_FINISHED_WITH_ERROR
name|int
name|EXIT_YARN_SERVICE_FINISHED_WITH_ERROR
init|=
literal|68
decl_stmt|;
comment|/**    * the application instance is unknown: {@value}    */
DECL|field|EXIT_UNKNOWN_INSTANCE
name|int
name|EXIT_UNKNOWN_INSTANCE
init|=
literal|69
decl_stmt|;
comment|/**    * the application instance is in the wrong state for that operation: {@value}    */
DECL|field|EXIT_BAD_STATE
name|int
name|EXIT_BAD_STATE
init|=
literal|70
decl_stmt|;
comment|/**    * A spawned master process failed     */
DECL|field|EXIT_PROCESS_FAILED
name|int
name|EXIT_PROCESS_FAILED
init|=
literal|71
decl_stmt|;
comment|/**    * The instance failed -too many containers were    * failing or some other threshold was reached    */
DECL|field|EXIT_DEPLOYMENT_FAILED
name|int
name|EXIT_DEPLOYMENT_FAILED
init|=
literal|72
decl_stmt|;
comment|/**    * The application is live -and the requested operation    * does not work if the cluster is running    */
DECL|field|EXIT_APPLICATION_IN_USE
name|int
name|EXIT_APPLICATION_IN_USE
init|=
literal|73
decl_stmt|;
comment|/**    * There already is an application instance of that name    * when an attempt is made to create a new instance    */
DECL|field|EXIT_INSTANCE_EXISTS
name|int
name|EXIT_INSTANCE_EXISTS
init|=
literal|75
decl_stmt|;
comment|/**    * Exit code when the configurations in valid/incomplete: {@value}    */
DECL|field|EXIT_BAD_CONFIGURATION
name|int
name|EXIT_BAD_CONFIGURATION
init|=
literal|77
decl_stmt|;
block|}
end_interface

end_unit

