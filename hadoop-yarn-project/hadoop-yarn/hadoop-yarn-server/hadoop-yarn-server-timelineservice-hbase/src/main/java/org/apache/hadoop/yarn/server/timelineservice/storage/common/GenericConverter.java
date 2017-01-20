begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|common
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
name|yarn
operator|.
name|server
operator|.
name|timeline
operator|.
name|GenericObjectMapper
import|;
end_import

begin_comment
comment|/**  * Uses GenericObjectMapper to encode objects as bytes and decode bytes as  * objects.  */
end_comment

begin_class
DECL|class|GenericConverter
specifier|public
specifier|final
class|class
name|GenericConverter
implements|implements
name|ValueConverter
block|{
DECL|field|INSTANCE
specifier|private
specifier|static
specifier|final
name|GenericConverter
name|INSTANCE
init|=
operator|new
name|GenericConverter
argument_list|()
decl_stmt|;
DECL|method|GenericConverter ()
specifier|private
name|GenericConverter
parameter_list|()
block|{   }
DECL|method|getInstance ()
specifier|public
specifier|static
name|GenericConverter
name|getInstance
parameter_list|()
block|{
return|return
name|INSTANCE
return|;
block|}
annotation|@
name|Override
DECL|method|encodeValue (Object value)
specifier|public
name|byte
index|[]
name|encodeValue
parameter_list|(
name|Object
name|value
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|GenericObjectMapper
operator|.
name|write
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|decodeValue (byte[] bytes)
specifier|public
name|Object
name|decodeValue
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|GenericObjectMapper
operator|.
name|read
argument_list|(
name|bytes
argument_list|)
return|;
block|}
block|}
end_class

end_unit

