begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|protocol
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
name|hdfs
operator|.
name|StorageType
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
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeInfo
import|;
end_import

begin_class
DECL|class|DatanodeInfoWithStorage
specifier|public
class|class
name|DatanodeInfoWithStorage
extends|extends
name|DatanodeInfo
block|{
DECL|field|storageID
specifier|private
specifier|final
name|String
name|storageID
decl_stmt|;
DECL|field|storageType
specifier|private
specifier|final
name|StorageType
name|storageType
decl_stmt|;
DECL|method|DatanodeInfoWithStorage (DatanodeInfo from, String storageID, StorageType storageType)
specifier|public
name|DatanodeInfoWithStorage
parameter_list|(
name|DatanodeInfo
name|from
parameter_list|,
name|String
name|storageID
parameter_list|,
name|StorageType
name|storageType
parameter_list|)
block|{
name|super
argument_list|(
name|from
argument_list|)
expr_stmt|;
name|this
operator|.
name|storageID
operator|=
name|storageID
expr_stmt|;
name|this
operator|.
name|storageType
operator|=
name|storageType
expr_stmt|;
block|}
DECL|method|getStorageID ()
specifier|public
name|String
name|getStorageID
parameter_list|()
block|{
return|return
name|storageID
return|;
block|}
DECL|method|getStorageType ()
specifier|public
name|StorageType
name|getStorageType
parameter_list|()
block|{
return|return
name|storageType
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
comment|// allows this class to be used interchangeably with DatanodeInfo
return|return
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
comment|// allows this class to be used interchangeably with DatanodeInfo
return|return
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"DatanodeInfoWithStorage["
operator|+
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|","
operator|+
name|storageID
operator|+
literal|","
operator|+
name|storageType
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

