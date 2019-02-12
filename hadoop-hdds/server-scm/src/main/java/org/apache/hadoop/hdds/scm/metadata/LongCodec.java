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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|Longs
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
name|utils
operator|.
name|db
operator|.
name|Codec
import|;
end_import

begin_comment
comment|/**  * Codec for Persisting the DeletedBlocks.  */
end_comment

begin_class
DECL|class|LongCodec
specifier|public
class|class
name|LongCodec
implements|implements
name|Codec
argument_list|<
name|Long
argument_list|>
block|{
annotation|@
name|Override
DECL|method|toPersistedFormat (Long object)
specifier|public
name|byte
index|[]
name|toPersistedFormat
parameter_list|(
name|Long
name|object
parameter_list|)
block|{
return|return
name|Longs
operator|.
name|toByteArray
argument_list|(
name|object
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fromPersistedFormat (byte[] rawData)
specifier|public
name|Long
name|fromPersistedFormat
parameter_list|(
name|byte
index|[]
name|rawData
parameter_list|)
block|{
return|return
name|Longs
operator|.
name|fromByteArray
argument_list|(
name|rawData
argument_list|)
return|;
block|}
block|}
end_class

end_unit

