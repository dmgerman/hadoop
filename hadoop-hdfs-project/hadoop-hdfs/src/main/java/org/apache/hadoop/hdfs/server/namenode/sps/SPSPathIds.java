begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.sps
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
operator|.
name|sps
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
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * A class which holds the SPS invoked path ids.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|SPSPathIds
specifier|public
class|class
name|SPSPathIds
block|{
comment|// List of pending dir to satisfy the policy
DECL|field|spsDirsToBeTraveresed
specifier|private
specifier|final
name|Queue
argument_list|<
name|Long
argument_list|>
name|spsDirsToBeTraveresed
init|=
operator|new
name|LinkedList
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Add the path id to queue.    */
DECL|method|add (long pathId)
specifier|public
specifier|synchronized
name|void
name|add
parameter_list|(
name|long
name|pathId
parameter_list|)
block|{
name|spsDirsToBeTraveresed
operator|.
name|add
argument_list|(
name|pathId
argument_list|)
expr_stmt|;
block|}
comment|/**    * Removes the path id.    */
DECL|method|remove (long pathId)
specifier|public
specifier|synchronized
name|void
name|remove
parameter_list|(
name|long
name|pathId
parameter_list|)
block|{
name|spsDirsToBeTraveresed
operator|.
name|remove
argument_list|(
name|pathId
argument_list|)
expr_stmt|;
block|}
comment|/**    * Clears all path ids.    */
DECL|method|clear ()
specifier|public
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
name|spsDirsToBeTraveresed
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**    * @return next path id available in queue.    */
DECL|method|pollNext ()
specifier|public
specifier|synchronized
name|Long
name|pollNext
parameter_list|()
block|{
return|return
name|spsDirsToBeTraveresed
operator|.
name|poll
argument_list|()
return|;
block|}
block|}
end_class

end_unit

