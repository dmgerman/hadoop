begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avro
operator|.
name|ipc
operator|.
name|AvroRemoteException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avro
operator|.
name|util
operator|.
name|Utf8
import|;
end_import

begin_interface
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|interface|AvroTestProtocol
specifier|public
interface|interface
name|AvroTestProtocol
block|{
DECL|class|Problem
specifier|public
specifier|static
class|class
name|Problem
extends|extends
name|AvroRemoteException
block|{
DECL|method|Problem ()
specifier|public
name|Problem
parameter_list|()
block|{}
block|}
DECL|method|ping ()
name|void
name|ping
parameter_list|()
function_decl|;
DECL|method|echo (Utf8 value)
name|Utf8
name|echo
parameter_list|(
name|Utf8
name|value
parameter_list|)
function_decl|;
DECL|method|add (int v1, int v2)
name|int
name|add
parameter_list|(
name|int
name|v1
parameter_list|,
name|int
name|v2
parameter_list|)
function_decl|;
DECL|method|error ()
name|int
name|error
parameter_list|()
throws|throws
name|Problem
function_decl|;
block|}
end_interface

end_unit

