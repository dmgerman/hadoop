begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.ratis.shaded.com.google.protobuf
package|package
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|shaded
operator|.
name|com
operator|.
name|google
operator|.
name|protobuf
package|;
end_package

begin_comment
comment|/** Utilities for the shaded protobuf in Ratis. */
end_comment

begin_interface
DECL|interface|ShadedProtoUtil
specifier|public
interface|interface
name|ShadedProtoUtil
block|{
comment|/**    * @param bytes    * @return the wrapped shaded {@link ByteString} (no coping).    */
DECL|method|asShadedByteString (byte[] bytes)
specifier|static
name|ByteString
name|asShadedByteString
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
return|return
name|ByteString
operator|.
name|wrap
argument_list|(
name|bytes
argument_list|)
return|;
block|}
comment|/**    * @param shaded    * @return a {@link com.google.protobuf.ByteString} (require coping).    */
DECL|method|asByteString (ByteString shaded)
specifier|static
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ByteString
name|asByteString
parameter_list|(
name|ByteString
name|shaded
parameter_list|)
block|{
return|return
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|shaded
operator|.
name|asReadOnlyByteBuffer
argument_list|()
argument_list|)
return|;
block|}
block|}
end_interface

end_unit

