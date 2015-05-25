begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.erasurecode.codec
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|erasurecode
operator|.
name|codec
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
name|Configurable
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
name|io
operator|.
name|erasurecode
operator|.
name|coder
operator|.
name|ErasureCoder
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
name|io
operator|.
name|erasurecode
operator|.
name|grouper
operator|.
name|BlockGrouper
import|;
end_import

begin_comment
comment|/**  * Erasure Codec API that's to cover the essential specific aspects of a code.  * Currently it cares only block grouper and erasure coder. In future we may  * add more aspects here to make the behaviors customizable.  */
end_comment

begin_interface
DECL|interface|ErasureCodec
specifier|public
interface|interface
name|ErasureCodec
extends|extends
name|Configurable
block|{
comment|/**    * Create block grouper    * @return block grouper    */
DECL|method|createBlockGrouper ()
specifier|public
name|BlockGrouper
name|createBlockGrouper
parameter_list|()
function_decl|;
comment|/**    * Create Erasure Encoder    * @return erasure encoder    */
DECL|method|createEncoder ()
specifier|public
name|ErasureCoder
name|createEncoder
parameter_list|()
function_decl|;
comment|/**    * Create Erasure Decoder    * @return erasure decoder    */
DECL|method|createDecoder ()
specifier|public
name|ErasureCoder
name|createDecoder
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

