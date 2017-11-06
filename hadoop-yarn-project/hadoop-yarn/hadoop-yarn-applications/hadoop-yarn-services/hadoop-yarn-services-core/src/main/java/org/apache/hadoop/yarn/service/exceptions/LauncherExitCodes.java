begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
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

begin_comment
comment|/*  * Common Exit codes  *<p>  * Exit codes from 64 up are service specific.  *<p>  * Many of the exit codes are designed to resemble HTTP error codes,  * squashed into a single byte. e.g 44 , "not found" is the equivalent  * of 404  *<pre>  *    0-10: general command issues  *   30-39: equivalent to the 3XX responses, where those responses are  *          considered errors by the service.  *   40-49: request-related errors  *   50-59: server-side problems. These may be triggered by the request.  *   64-  : service specific error codes  *</pre>  */
end_comment

begin_interface
DECL|interface|LauncherExitCodes
specifier|public
interface|interface
name|LauncherExitCodes
block|{
comment|/**    * 0: success    */
DECL|field|EXIT_SUCCESS
name|int
name|EXIT_SUCCESS
init|=
literal|0
decl_stmt|;
comment|/**    * -1: generic "false" response. The operation worked but    * the result was not true    */
DECL|field|EXIT_FALSE
name|int
name|EXIT_FALSE
init|=
operator|-
literal|1
decl_stmt|;
comment|/**    * Exit code when a client requested service termination: {@value}    */
DECL|field|EXIT_CLIENT_INITIATED_SHUTDOWN
name|int
name|EXIT_CLIENT_INITIATED_SHUTDOWN
init|=
literal|1
decl_stmt|;
comment|/**    * Exit code when targets could not be launched: {@value}    */
DECL|field|EXIT_TASK_LAUNCH_FAILURE
name|int
name|EXIT_TASK_LAUNCH_FAILURE
init|=
literal|2
decl_stmt|;
comment|/**    * Exit code when a control-C, kill -3, signal was picked up: {@value}    */
DECL|field|EXIT_INTERRUPTED
name|int
name|EXIT_INTERRUPTED
init|=
literal|3
decl_stmt|;
comment|/**    * Exit code when a usage message was printed: {@value}    */
DECL|field|EXIT_USAGE
name|int
name|EXIT_USAGE
init|=
literal|4
decl_stmt|;
comment|/**    * Exit code when something happened but we can't be specific: {@value}    */
DECL|field|EXIT_OTHER_FAILURE
name|int
name|EXIT_OTHER_FAILURE
init|=
literal|5
decl_stmt|;
comment|/**    * Exit code on connectivity problems: {@value}    */
DECL|field|EXIT_MOVED
name|int
name|EXIT_MOVED
init|=
literal|31
decl_stmt|;
comment|/**    * found: {@value}.    *<p>    * This is low value as in HTTP it is normally a success/redirect;    * whereas on the command line 0 is the sole success code.    *<p>    *<code>302 Found</code>    */
DECL|field|EXIT_FOUND
name|int
name|EXIT_FOUND
init|=
literal|32
decl_stmt|;
comment|/**    * Exit code on a request where the destination has not changed    * and (somehow) the command specified that this is an error.    * That is, this exit code is somehow different from a "success"    * : {@value}    *<p>    *<code>304 Not Modified</code>   */
DECL|field|EXIT_NOT_MODIFIED
name|int
name|EXIT_NOT_MODIFIED
init|=
literal|34
decl_stmt|;
comment|/**    * Exit code when the command line doesn't parse: {@value}, or    * when it is otherwise invalid.    *<p>    *<code>400 BAD REQUEST</code>    */
DECL|field|EXIT_COMMAND_ARGUMENT_ERROR
name|int
name|EXIT_COMMAND_ARGUMENT_ERROR
init|=
literal|40
decl_stmt|;
comment|/**    * The request requires user authentication: {@value}    *<p>    *<code>401 Unauthorized</code>    */
DECL|field|EXIT_UNAUTHORIZED
name|int
name|EXIT_UNAUTHORIZED
init|=
literal|41
decl_stmt|;
comment|/**    * Forbidden action: {@value}    *<p>    *<code>403: Forbidden</code>    */
DECL|field|EXIT_FORBIDDEN
name|int
name|EXIT_FORBIDDEN
init|=
literal|43
decl_stmt|;
comment|/**    * Something was not found: {@value}    *<p>    *<code>404: NOT FOUND</code>    */
DECL|field|EXIT_NOT_FOUND
name|int
name|EXIT_NOT_FOUND
init|=
literal|44
decl_stmt|;
comment|/**    * The operation is not allowed: {@value}    *<p>    *<code>405: NOT ALLOWED</code>    */
DECL|field|EXIT_OPERATION_NOT_ALLOWED
name|int
name|EXIT_OPERATION_NOT_ALLOWED
init|=
literal|45
decl_stmt|;
comment|/**    * The command is somehow not acceptable: {@value}    *<p>    *<code>406: NOT ACCEPTABLE</code>    */
DECL|field|EXIT_NOT_ACCEPTABLE
name|int
name|EXIT_NOT_ACCEPTABLE
init|=
literal|46
decl_stmt|;
comment|/**    * Exit code on connectivity problems: {@value}    *<p>    *<code>408: Request Timeout</code>    */
DECL|field|EXIT_CONNECTIVITY_PROBLEM
name|int
name|EXIT_CONNECTIVITY_PROBLEM
init|=
literal|48
decl_stmt|;
comment|/**    * The request could not be completed due to a conflict with the current    * state of the resource.  {@value}    *<p>    *<code>409: conflict</code>    */
DECL|field|EXIT_CONFLICT
name|int
name|EXIT_CONFLICT
init|=
literal|49
decl_stmt|;
comment|/**    * internal error: {@value}    *<p>    *<code>500 Internal Server Error</code>    */
DECL|field|EXIT_INTERNAL_ERROR
name|int
name|EXIT_INTERNAL_ERROR
init|=
literal|50
decl_stmt|;
comment|/**    * Unimplemented feature: {@value}    *<p>    *<code>501: Not Implemented</code>    */
DECL|field|EXIT_UNIMPLEMENTED
name|int
name|EXIT_UNIMPLEMENTED
init|=
literal|51
decl_stmt|;
comment|/**    * Service Unavailable; it may be available later: {@value}    *<p>    *<code>503 Service Unavailable</code>    */
DECL|field|EXIT_SERVICE_UNAVAILABLE
name|int
name|EXIT_SERVICE_UNAVAILABLE
init|=
literal|53
decl_stmt|;
comment|/**    * The service does not support, or refuses to support this version: {@value}.    * If raised, this is expected to be raised server-side and likely due    * to client/server version incompatibilities.    *<p>    *<code> 505: Version Not Supported</code>    */
DECL|field|EXIT_UNSUPPORTED_VERSION
name|int
name|EXIT_UNSUPPORTED_VERSION
init|=
literal|55
decl_stmt|;
comment|/**    * Exit code when an exception was thrown from the service: {@value}    *<p>    *<code>5XX</code>    */
DECL|field|EXIT_EXCEPTION_THROWN
name|int
name|EXIT_EXCEPTION_THROWN
init|=
literal|56
decl_stmt|;
block|}
end_interface

end_unit

