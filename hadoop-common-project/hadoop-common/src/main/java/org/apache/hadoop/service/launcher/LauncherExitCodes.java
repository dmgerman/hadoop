begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.service.launcher
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|service
operator|.
name|launcher
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

begin_comment
comment|/**  * Common Exit codes.  *<p>  * Codes with a YARN prefix are YARN-related.  *<p>  * Many of the exit codes are designed to resemble HTTP error codes,  * squashed into a single byte. e.g 44 , "not found" is the equivalent  * of 404. The various 2XX HTTP error codes aren't followed;  * the Unix standard of "0" for success is used.  *<pre>  *    0-10: general command issues  *   30-39: equivalent to the 3XX responses, where those responses are  *          considered errors by the application.  *   40-49: client-side/CLI/config problems  *   50-59: service-side problems.  *   60+  : application specific error codes  *</pre>  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|LauncherExitCodes
specifier|public
interface|interface
name|LauncherExitCodes
block|{
comment|/**    * Success: {@value}.    */
DECL|field|EXIT_SUCCESS
name|int
name|EXIT_SUCCESS
init|=
literal|0
decl_stmt|;
comment|/**    * Generic "false/fail" response: {@value}.    * The operation worked but the result was not "true" from the viewpoint    * of the executed code.    */
DECL|field|EXIT_FAIL
name|int
name|EXIT_FAIL
init|=
operator|-
literal|1
decl_stmt|;
comment|/**    * Exit code when a client requested service termination: {@value}.    */
DECL|field|EXIT_CLIENT_INITIATED_SHUTDOWN
name|int
name|EXIT_CLIENT_INITIATED_SHUTDOWN
init|=
literal|1
decl_stmt|;
comment|/**    * Exit code when targets could not be launched: {@value}.    */
DECL|field|EXIT_TASK_LAUNCH_FAILURE
name|int
name|EXIT_TASK_LAUNCH_FAILURE
init|=
literal|2
decl_stmt|;
comment|/**    * Exit code when a control-C, kill -3, signal was picked up: {@value}.    */
DECL|field|EXIT_INTERRUPTED
name|int
name|EXIT_INTERRUPTED
init|=
literal|3
decl_stmt|;
comment|/**    * Exit code when something happened but we can't be specific: {@value}.    */
DECL|field|EXIT_OTHER_FAILURE
name|int
name|EXIT_OTHER_FAILURE
init|=
literal|5
decl_stmt|;
comment|/**    * Exit code when the command line doesn't parse: {@value}, or    * when it is otherwise invalid.    *<p>    * Approximate HTTP equivalent: {@code 400 Bad Request}    */
DECL|field|EXIT_COMMAND_ARGUMENT_ERROR
name|int
name|EXIT_COMMAND_ARGUMENT_ERROR
init|=
literal|40
decl_stmt|;
comment|/**    * The request requires user authentication: {@value}.    *<p>    * approximate HTTP equivalent: Approximate HTTP equivalent: {@code 401 Unauthorized}    */
DECL|field|EXIT_UNAUTHORIZED
name|int
name|EXIT_UNAUTHORIZED
init|=
literal|41
decl_stmt|;
comment|/**    * Exit code when a usage message was printed: {@value}.    */
DECL|field|EXIT_USAGE
name|int
name|EXIT_USAGE
init|=
literal|42
decl_stmt|;
comment|/**    * Forbidden action: {@value}.    *<p>    * Approximate HTTP equivalent: Approximate HTTP equivalent: {@code 403: Forbidden}    */
DECL|field|EXIT_FORBIDDEN
name|int
name|EXIT_FORBIDDEN
init|=
literal|43
decl_stmt|;
comment|/**    * Something was not found: {@value}.    *<p>    * Approximate HTTP equivalent: {@code 404: Not Found}    */
DECL|field|EXIT_NOT_FOUND
name|int
name|EXIT_NOT_FOUND
init|=
literal|44
decl_stmt|;
comment|/**    * The operation is not allowed: {@value}.    *<p>    * Approximate HTTP equivalent: {@code 405: Not allowed}    */
DECL|field|EXIT_OPERATION_NOT_ALLOWED
name|int
name|EXIT_OPERATION_NOT_ALLOWED
init|=
literal|45
decl_stmt|;
comment|/**    * The command is somehow not acceptable: {@value}.    *<p>    * Approximate HTTP equivalent: {@code 406: Not Acceptable}    */
DECL|field|EXIT_NOT_ACCEPTABLE
name|int
name|EXIT_NOT_ACCEPTABLE
init|=
literal|46
decl_stmt|;
comment|/**    * Exit code on connectivity problems: {@value}.    *<p>    * Approximate HTTP equivalent: {@code 408: Request Timeout}    */
DECL|field|EXIT_CONNECTIVITY_PROBLEM
name|int
name|EXIT_CONNECTIVITY_PROBLEM
init|=
literal|48
decl_stmt|;
comment|/**    * Exit code when the configurations in valid/incomplete: {@value}.    *<p>    * Approximate HTTP equivalent: {@code 409: Conflict}    */
DECL|field|EXIT_BAD_CONFIGURATION
name|int
name|EXIT_BAD_CONFIGURATION
init|=
literal|49
decl_stmt|;
comment|/**    * Exit code when an exception was thrown from the service: {@value}.    *<p>    * Approximate HTTP equivalent: {@code 500 Internal Server Error}    */
DECL|field|EXIT_EXCEPTION_THROWN
name|int
name|EXIT_EXCEPTION_THROWN
init|=
literal|50
decl_stmt|;
comment|/**    * Unimplemented feature: {@value}.    *<p>    * Approximate HTTP equivalent: {@code 501: Not Implemented}    */
DECL|field|EXIT_UNIMPLEMENTED
name|int
name|EXIT_UNIMPLEMENTED
init|=
literal|51
decl_stmt|;
comment|/**    * Service Unavailable; it may be available later: {@value}.    *<p>    * Approximate HTTP equivalent: {@code 503 Service Unavailable}    */
DECL|field|EXIT_SERVICE_UNAVAILABLE
name|int
name|EXIT_SERVICE_UNAVAILABLE
init|=
literal|53
decl_stmt|;
comment|/**    * The application does not support, or refuses to support this    * version: {@value}.    *<p>    * If raised, this is expected to be raised server-side and likely due    * to client/server version incompatibilities.    *<p>    * Approximate HTTP equivalent: {@code 505: Version Not Supported}    */
DECL|field|EXIT_UNSUPPORTED_VERSION
name|int
name|EXIT_UNSUPPORTED_VERSION
init|=
literal|55
decl_stmt|;
comment|/**    * The service instance could not be created: {@value}.    */
DECL|field|EXIT_SERVICE_CREATION_FAILURE
name|int
name|EXIT_SERVICE_CREATION_FAILURE
init|=
literal|56
decl_stmt|;
comment|/**    * The service instance could not be created: {@value}.    */
DECL|field|EXIT_SERVICE_LIFECYCLE_EXCEPTION
name|int
name|EXIT_SERVICE_LIFECYCLE_EXCEPTION
init|=
literal|57
decl_stmt|;
block|}
end_interface

end_unit

