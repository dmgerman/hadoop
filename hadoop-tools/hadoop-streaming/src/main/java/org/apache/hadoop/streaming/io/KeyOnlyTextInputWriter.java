begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.streaming.io
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|streaming
operator|.
name|io
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

begin_class
DECL|class|KeyOnlyTextInputWriter
specifier|public
class|class
name|KeyOnlyTextInputWriter
extends|extends
name|TextInputWriter
block|{
annotation|@
name|Override
DECL|method|writeKey (Object key)
specifier|public
name|void
name|writeKey
parameter_list|(
name|Object
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|writeUTF8
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|clientOut
operator|.
name|write
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeValue (Object value)
specifier|public
name|void
name|writeValue
parameter_list|(
name|Object
name|value
parameter_list|)
throws|throws
name|IOException
block|{}
block|}
end_class

end_unit

