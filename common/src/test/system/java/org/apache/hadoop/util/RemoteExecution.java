begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

begin_interface
DECL|interface|RemoteExecution
specifier|public
interface|interface
name|RemoteExecution
block|{
DECL|method|executeCommand (String remoteHostName, String user, String command)
specifier|public
name|void
name|executeCommand
parameter_list|(
name|String
name|remoteHostName
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|command
parameter_list|)
throws|throws
name|Exception
function_decl|;
DECL|method|getExitCode ()
specifier|public
name|int
name|getExitCode
parameter_list|()
function_decl|;
DECL|method|getOutput ()
specifier|public
name|String
name|getOutput
parameter_list|()
function_decl|;
DECL|method|getCommandString ()
specifier|public
name|String
name|getCommandString
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

