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
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_comment
comment|/**  * Class captures information of a storage in Datanode.  */
end_comment

begin_class
DECL|class|DatanodeStorage
specifier|public
class|class
name|DatanodeStorage
block|{
comment|/** The state of the storage. */
DECL|enum|State
specifier|public
enum|enum
name|State
block|{
DECL|enumConstant|NORMAL
name|NORMAL
block|,
DECL|enumConstant|READ_ONLY
name|READ_ONLY
block|}
DECL|field|storageID
specifier|private
specifier|final
name|String
name|storageID
decl_stmt|;
DECL|field|state
specifier|private
specifier|final
name|State
name|state
decl_stmt|;
DECL|field|storageType
specifier|private
specifier|final
name|StorageType
name|storageType
decl_stmt|;
comment|/**    * Create a storage with {@link State#NORMAL} and {@link StorageType#DEFAULT}.    *    * @param storageID    */
DECL|method|DatanodeStorage (String storageID)
specifier|public
name|DatanodeStorage
parameter_list|(
name|String
name|storageID
parameter_list|)
block|{
name|this
argument_list|(
name|storageID
argument_list|,
name|State
operator|.
name|NORMAL
argument_list|,
name|StorageType
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
DECL|method|DatanodeStorage (String sid, State s, StorageType sm)
specifier|public
name|DatanodeStorage
parameter_list|(
name|String
name|sid
parameter_list|,
name|State
name|s
parameter_list|,
name|StorageType
name|sm
parameter_list|)
block|{
name|this
operator|.
name|storageID
operator|=
name|sid
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|s
expr_stmt|;
name|this
operator|.
name|storageType
operator|=
name|sm
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
DECL|method|getState ()
specifier|public
name|State
name|getState
parameter_list|()
block|{
return|return
name|state
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
comment|/**    * Generate new storage ID. The format of this string can be changed    * in the future without requiring that old storage IDs be updated.    *    * @return unique storage ID    */
DECL|method|generateUuid ()
specifier|public
specifier|static
name|String
name|generateUuid
parameter_list|()
block|{
return|return
literal|"DS-"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object other)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|(
name|other
operator|==
literal|null
operator|)
operator|||
operator|!
operator|(
name|other
operator|instanceof
name|DatanodeStorage
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|DatanodeStorage
name|otherStorage
init|=
operator|(
name|DatanodeStorage
operator|)
name|other
decl_stmt|;
return|return
name|otherStorage
operator|.
name|getStorageID
argument_list|()
operator|.
name|compareTo
argument_list|(
name|getStorageID
argument_list|()
argument_list|)
operator|==
literal|0
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
return|return
name|getStorageID
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

