begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|nativetask
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|mapred
operator|.
name|nativetask
operator|.
name|util
operator|.
name|ReadWriteBuffer
import|;
end_import

begin_comment
comment|/**  * A Handler accept input, and give output can be used to transfer command and data  */
end_comment

begin_interface
DECL|interface|INativeHandler
specifier|public
interface|interface
name|INativeHandler
extends|extends
name|NativeDataTarget
extends|,
name|NativeDataSource
block|{
DECL|method|name ()
specifier|public
name|String
name|name
parameter_list|()
function_decl|;
DECL|method|getNativeHandler ()
specifier|public
name|long
name|getNativeHandler
parameter_list|()
function_decl|;
comment|/**    * init the native handler    */
DECL|method|init (Configuration conf)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * close the native handler    */
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * call command to downstream    *     * @param command    * @param parameter    * @return    * @throws IOException    */
DECL|method|call (Command command, ReadWriteBuffer parameter)
specifier|public
name|ReadWriteBuffer
name|call
parameter_list|(
name|Command
name|command
parameter_list|,
name|ReadWriteBuffer
name|parameter
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * @param handler    */
DECL|method|setCommandDispatcher (CommandDispatcher handler)
name|void
name|setCommandDispatcher
parameter_list|(
name|CommandDispatcher
name|handler
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

