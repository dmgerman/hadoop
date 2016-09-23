begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Queue
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
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_comment
comment|/**  * A Class to track the block collection IDs for which physical storage movement  * needed as per the Namespace and StorageReports from DN.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BlockStorageMovementNeeded
specifier|public
class|class
name|BlockStorageMovementNeeded
block|{
DECL|field|storageMovementNeeded
specifier|private
specifier|final
name|Queue
argument_list|<
name|Long
argument_list|>
name|storageMovementNeeded
init|=
operator|new
name|LinkedList
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Add the block collection id to tracking list for which storage movement    * expected if necessary.    *    * @param blockCollectionID    *          - block collection id, which is nothing but inode id.    */
DECL|method|add (Long blockCollectionID)
specifier|public
specifier|synchronized
name|void
name|add
parameter_list|(
name|Long
name|blockCollectionID
parameter_list|)
block|{
name|storageMovementNeeded
operator|.
name|add
argument_list|(
name|blockCollectionID
argument_list|)
expr_stmt|;
block|}
comment|/**    * Gets the block collection id for which storage movements check necessary    * and make the movement if required.    *    * @return block collection ID    */
DECL|method|get ()
specifier|public
specifier|synchronized
name|Long
name|get
parameter_list|()
block|{
return|return
name|storageMovementNeeded
operator|.
name|poll
argument_list|()
return|;
block|}
block|}
end_class

end_unit

