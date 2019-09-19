begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.s3.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|s3
operator|.
name|util
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
name|hdds
operator|.
name|client
operator|.
name|ReplicationFactor
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
name|client
operator|.
name|ReplicationType
import|;
end_import

begin_comment
comment|/**  * Maps S3 storage class values to Ozone replication values.  */
end_comment

begin_enum
DECL|enum|S3StorageType
specifier|public
enum|enum
name|S3StorageType
block|{
DECL|enumConstant|REDUCED_REDUNDANCY
name|REDUCED_REDUNDANCY
parameter_list|(
name|ReplicationType
operator|.
name|RATIS
parameter_list|,
name|ReplicationFactor
operator|.
name|ONE
parameter_list|)
operator|,
DECL|enumConstant|STANDARD
constructor|STANDARD(ReplicationType.RATIS
operator|,
constructor|ReplicationFactor.THREE
block|)
enum|;
end_enum

begin_decl_stmt
DECL|field|type
specifier|private
specifier|final
name|ReplicationType
name|type
decl_stmt|;
end_decl_stmt

begin_decl_stmt
DECL|field|factor
specifier|private
specifier|final
name|ReplicationFactor
name|factor
decl_stmt|;
end_decl_stmt

begin_expr_stmt
DECL|method|S3StorageType ( ReplicationType type, ReplicationFactor factor)
name|S3StorageType
argument_list|(
name|ReplicationType
name|type
argument_list|,
name|ReplicationFactor
name|factor
argument_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
block|;
name|this
operator|.
name|factor
operator|=
name|factor
block|;   }
DECL|method|getFactor ()
specifier|public
name|ReplicationFactor
name|getFactor
argument_list|()
block|{
return|return
name|factor
return|;
block|}
end_expr_stmt

begin_function
DECL|method|getType ()
specifier|public
name|ReplicationType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
end_function

begin_function
DECL|method|getDefault ()
specifier|public
specifier|static
name|S3StorageType
name|getDefault
parameter_list|()
block|{
return|return
name|STANDARD
return|;
block|}
end_function

begin_function
DECL|method|fromReplicationType ( ReplicationType replicationType, ReplicationFactor factor)
specifier|public
specifier|static
name|S3StorageType
name|fromReplicationType
parameter_list|(
name|ReplicationType
name|replicationType
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|)
block|{
if|if
condition|(
operator|(
name|replicationType
operator|==
name|ReplicationType
operator|.
name|STAND_ALONE
operator|)
operator|||
operator|(
name|factor
operator|==
name|ReplicationFactor
operator|.
name|ONE
operator|)
condition|)
block|{
return|return
name|S3StorageType
operator|.
name|REDUCED_REDUNDANCY
return|;
block|}
else|else
block|{
return|return
name|S3StorageType
operator|.
name|STANDARD
return|;
block|}
block|}
end_function

unit|}
end_unit

