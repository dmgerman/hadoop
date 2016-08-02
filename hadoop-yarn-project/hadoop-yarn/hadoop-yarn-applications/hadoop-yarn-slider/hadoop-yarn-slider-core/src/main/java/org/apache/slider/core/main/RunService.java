begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.core.main
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|main
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
comment|/**  * An interface which services can implement to have their  * execution managed by the ServiceLauncher.  * The command line options will be passed down before the   * {@link Service#init(Configuration)} operation is invoked via an  * invocation of {@link RunService#bindArgs(Configuration, String...)}  * After the service has been successfully started via {@link Service#start()}  * the {@link RunService#runService()} method is called to execute the   * service. When this method returns, the service launcher will exit, using  * the return code from the method as its exit option.  */
end_comment

begin_interface
DECL|interface|RunService
specifier|public
interface|interface
name|RunService
extends|extends
name|Service
block|{
comment|/**    * Propagate the command line arguments.    * This method is called before {@link Service#init(Configuration)};    * the configuration that is returned from this operation    * is the one that is passed on to the init operation.    * This permits implemenations to change the configuration before    * the init operation.n    *     *    * @param config the initial configuration build up by the    * service launcher.    * @param args argument list list of arguments passed to the command line    * after any launcher-specific commands have been stripped.    * @return the configuration to init the service with. This MUST NOT be null.    * Recommended: pass down the config parameter with any changes    * @throws Exception any problem    */
DECL|method|bindArgs (Configuration config, String... args)
name|Configuration
name|bindArgs
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**    * Run a service. This called after {@link Service#start()}    * @return the exit code    * @throws Throwable any exception to report    */
DECL|method|runService ()
name|int
name|runService
parameter_list|()
throws|throws
name|Throwable
function_decl|;
block|}
end_interface

end_unit

