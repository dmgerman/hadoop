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
comment|/**    * Create a storage with {@link State#NORMAL}.    * @param storageID    */
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
argument_list|)
expr_stmt|;
block|}
DECL|method|DatanodeStorage (String sid, State s)
specifier|public
name|DatanodeStorage
parameter_list|(
name|String
name|sid
parameter_list|,
name|State
name|s
parameter_list|)
block|{
name|storageID
operator|=
name|sid
expr_stmt|;
name|state
operator|=
name|s
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
block|}
end_class

end_unit

