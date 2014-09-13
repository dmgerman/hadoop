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
name|mapred
operator|.
name|nativetask
operator|.
name|buffer
operator|.
name|InputBuffer
import|;
end_import

begin_comment
comment|/**  * NativeDataSource loads data from upstream  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|NativeDataSource
specifier|public
interface|interface
name|NativeDataSource
block|{
comment|/**    * get input buffer    */
DECL|method|getInputBuffer ()
specifier|public
name|InputBuffer
name|getInputBuffer
parameter_list|()
function_decl|;
comment|/**    * set listener. When data from upstream arrives, the listener will be activated.    */
DECL|method|setDataReceiver (DataReceiver handler)
name|void
name|setDataReceiver
parameter_list|(
name|DataReceiver
name|handler
parameter_list|)
function_decl|;
comment|/**    * load data from upstream    */
DECL|method|loadData ()
specifier|public
name|void
name|loadData
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

