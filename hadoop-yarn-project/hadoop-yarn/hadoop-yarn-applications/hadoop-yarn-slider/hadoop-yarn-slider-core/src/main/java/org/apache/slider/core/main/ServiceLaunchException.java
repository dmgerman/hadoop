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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnException
import|;
end_import

begin_comment
comment|/**  * A service launch exception that includes an exit code;  * when caught by the ServiceLauncher, it will convert that  * into a process exit code.  */
end_comment

begin_class
DECL|class|ServiceLaunchException
specifier|public
class|class
name|ServiceLaunchException
extends|extends
name|YarnException
implements|implements
name|ExitCodeProvider
implements|,
name|LauncherExitCodes
block|{
DECL|field|exitCode
specifier|private
specifier|final
name|int
name|exitCode
decl_stmt|;
comment|/**    * Create an exception with the specific exit code    * @param exitCode exit code    * @param cause cause of the exception    */
DECL|method|ServiceLaunchException (int exitCode, Throwable cause)
specifier|public
name|ServiceLaunchException
parameter_list|(
name|int
name|exitCode
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
name|this
operator|.
name|exitCode
operator|=
name|exitCode
expr_stmt|;
block|}
comment|/**    * Create an exception with the specific exit code and text    * @param exitCode exit code    * @param message message to use in exception    */
DECL|method|ServiceLaunchException (int exitCode, String message)
specifier|public
name|ServiceLaunchException
parameter_list|(
name|int
name|exitCode
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|this
operator|.
name|exitCode
operator|=
name|exitCode
expr_stmt|;
block|}
comment|/**    * Create an exception with the specific exit code, text and cause    * @param exitCode exit code    * @param message message to use in exception    * @param cause cause of the exception    */
DECL|method|ServiceLaunchException (int exitCode, String message, Throwable cause)
specifier|public
name|ServiceLaunchException
parameter_list|(
name|int
name|exitCode
parameter_list|,
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
expr_stmt|;
name|this
operator|.
name|exitCode
operator|=
name|exitCode
expr_stmt|;
block|}
comment|/**    * Get the exit code    * @return the exit code    */
annotation|@
name|Override
DECL|method|getExitCode ()
specifier|public
name|int
name|getExitCode
parameter_list|()
block|{
return|return
name|exitCode
return|;
block|}
block|}
end_class

end_unit

