begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.retry
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|retry
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
name|ipc
operator|.
name|RemoteException
import|;
end_import

begin_interface
DECL|interface|UnreliableInterface
specifier|public
interface|interface
name|UnreliableInterface
block|{
DECL|class|UnreliableException
specifier|public
specifier|static
class|class
name|UnreliableException
extends|extends
name|Exception
block|{
comment|// no body
block|}
DECL|class|FatalException
specifier|public
specifier|static
class|class
name|FatalException
extends|extends
name|UnreliableException
block|{
comment|// no body
block|}
DECL|method|alwaysSucceeds ()
name|void
name|alwaysSucceeds
parameter_list|()
throws|throws
name|UnreliableException
function_decl|;
DECL|method|alwaysFailsWithFatalException ()
name|void
name|alwaysFailsWithFatalException
parameter_list|()
throws|throws
name|FatalException
function_decl|;
DECL|method|alwaysFailsWithRemoteFatalException ()
name|void
name|alwaysFailsWithRemoteFatalException
parameter_list|()
throws|throws
name|RemoteException
function_decl|;
DECL|method|failsOnceThenSucceeds ()
name|void
name|failsOnceThenSucceeds
parameter_list|()
throws|throws
name|UnreliableException
function_decl|;
DECL|method|failsOnceThenSucceedsWithReturnValue ()
name|boolean
name|failsOnceThenSucceedsWithReturnValue
parameter_list|()
throws|throws
name|UnreliableException
function_decl|;
DECL|method|failsTenTimesThenSucceeds ()
name|void
name|failsTenTimesThenSucceeds
parameter_list|()
throws|throws
name|UnreliableException
function_decl|;
block|}
end_interface

end_unit

