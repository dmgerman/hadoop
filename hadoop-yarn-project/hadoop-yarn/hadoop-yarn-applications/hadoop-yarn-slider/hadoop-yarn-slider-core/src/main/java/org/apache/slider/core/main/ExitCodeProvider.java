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

begin_comment
comment|/**  * Get the exit code of an exception. Making it an interface allows  * us to retrofit exit codes onto existing classes  */
end_comment

begin_interface
DECL|interface|ExitCodeProvider
specifier|public
interface|interface
name|ExitCodeProvider
block|{
comment|/**    * Method to get the exit code    * @return the exit code    */
DECL|method|getExitCode ()
name|int
name|getExitCode
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

