begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.codec
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|codec
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|OmVolumeArgs
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|VolumeInfo
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
name|hdds
operator|.
name|utils
operator|.
name|db
operator|.
name|Codec
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|InvalidProtocolBufferException
import|;
end_import

begin_comment
comment|/**  * Codec to encode OmVolumeArgsCodec as byte array.  */
end_comment

begin_class
DECL|class|OmVolumeArgsCodec
specifier|public
class|class
name|OmVolumeArgsCodec
implements|implements
name|Codec
argument_list|<
name|OmVolumeArgs
argument_list|>
block|{
annotation|@
name|Override
DECL|method|toPersistedFormat (OmVolumeArgs object)
specifier|public
name|byte
index|[]
name|toPersistedFormat
parameter_list|(
name|OmVolumeArgs
name|object
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|object
argument_list|,
literal|"Null object can't be converted to byte array."
argument_list|)
expr_stmt|;
return|return
name|object
operator|.
name|getProtobuf
argument_list|()
operator|.
name|toByteArray
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|fromPersistedFormat (byte[] rawData)
specifier|public
name|OmVolumeArgs
name|fromPersistedFormat
parameter_list|(
name|byte
index|[]
name|rawData
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|rawData
argument_list|,
literal|"Null byte array can't converted to real object."
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|OmVolumeArgs
operator|.
name|getFromProtobuf
argument_list|(
name|VolumeInfo
operator|.
name|parseFrom
argument_list|(
name|rawData
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvalidProtocolBufferException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't encode the the raw data from the byte array"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

