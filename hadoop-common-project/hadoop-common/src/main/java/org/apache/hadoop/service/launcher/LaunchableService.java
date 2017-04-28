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
name|java
operator|.
name|util
operator|.
name|List
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
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
name|service
operator|.
name|Service
import|;
end_import

begin_comment
comment|/**  * An interface which services can implement to have their  * execution managed by the ServiceLauncher.  *<p>  * The command line options will be passed down before the   * {@link Service#init(Configuration)} operation is invoked via an  * invocation of {@link LaunchableService#bindArgs(Configuration, List)}  * After the service has been successfully started via {@link Service#start()}  * the {@link LaunchableService#execute()} method is called to execute the   * service. When this method returns, the service launcher will exit, using  * the return code from the method as its exit option.  */
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
DECL|interface|LaunchableService
specifier|public
interface|interface
name|LaunchableService
extends|extends
name|Service
block|{
comment|/**    * Propagate the command line arguments.    *<p>    * This method is called before {@link #init(Configuration)};    * Any non-null configuration that is returned from this operation    * becomes the one that is passed on to that {@link #init(Configuration)}    * operation.    *<p>    * This permits implementations to change the configuration before    * the init operation. As the ServiceLauncher only creates    * an instance of the base {@link Configuration} class, it is    * recommended to instantiate any subclass (such as YarnConfiguration)    * that injects new resources.    *<p>    * @param config the initial configuration build up by the    * service launcher.    * @param args list of arguments passed to the command line    * after any launcher-specific commands have been stripped.    * @return the configuration to init the service with.    * Recommended: pass down the config parameter with any changes    * @throws Exception any problem    */
DECL|method|bindArgs (Configuration config, List<String> args)
name|Configuration
name|bindArgs
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**    * Run a service. This method is called after {@link Service#start()}.    *<p>    * The return value becomes the exit code of the launched process.    *<p>    * If an exception is raised, the policy is:    *<ol>    *<li>Any subset of {@link org.apache.hadoop.util.ExitUtil.ExitException}:    *   the exception is passed up unmodified.    *</li>    *<li>Any exception which implements    *   {@link org.apache.hadoop.util.ExitCodeProvider}:    *   A new {@link ServiceLaunchException} is created with the exit code    *   and message of the thrown exception; the thrown exception becomes the    *   cause.</li>    *<li>Any other exception: a new {@link ServiceLaunchException} is created    *   with the exit code {@link LauncherExitCodes#EXIT_EXCEPTION_THROWN} and    *   the message of the original exception (which becomes the cause).</li>    *</ol>    * @return the exit code    * @throws org.apache.hadoop.util.ExitUtil.ExitException an exception passed    *  up as the exit code and error text.    * @throws Exception any exception to report. If it provides an exit code    * this is used in a wrapping exception.    */
DECL|method|execute ()
name|int
name|execute
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

