begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.ââSee the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.ââThe ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.ââYou may obtain a copy of the License at  *  * ââââ http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.metadata
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|metadata
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
name|java
operator|.
name|math
operator|.
name|BigInteger
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

begin_comment
comment|/**  * Encode and decode BigInteger.  */
end_comment

begin_class
DECL|class|BigIntegerCodec
specifier|public
class|class
name|BigIntegerCodec
implements|implements
name|Codec
argument_list|<
name|BigInteger
argument_list|>
block|{
annotation|@
name|Override
DECL|method|toPersistedFormat (BigInteger object)
specifier|public
name|byte
index|[]
name|toPersistedFormat
parameter_list|(
name|BigInteger
name|object
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|object
operator|.
name|toByteArray
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|fromPersistedFormat (byte[] rawData)
specifier|public
name|BigInteger
name|fromPersistedFormat
parameter_list|(
name|byte
index|[]
name|rawData
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BigInteger
argument_list|(
name|rawData
argument_list|)
return|;
block|}
block|}
end_class

end_unit

