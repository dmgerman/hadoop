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
name|mapred
operator|.
name|nativetask
operator|.
name|buffer
operator|.
name|OutputBuffer
import|;
end_import

begin_comment
comment|/**  * NativeDataTarge sends data to downstream  */
end_comment

begin_interface
DECL|interface|NativeDataTarget
specifier|public
interface|interface
name|NativeDataTarget
block|{
comment|/**    * send a signal to indicate that the data has been stored in output buffer    *     * @throws IOException    */
DECL|method|sendData ()
specifier|public
name|void
name|sendData
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Send a signal that there is no more data    *     * @throws IOException    */
DECL|method|finishSendData ()
specifier|public
name|void
name|finishSendData
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * get the output buffer.    *     * @return    */
DECL|method|getOutputBuffer ()
specifier|public
name|OutputBuffer
name|getOutputBuffer
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

