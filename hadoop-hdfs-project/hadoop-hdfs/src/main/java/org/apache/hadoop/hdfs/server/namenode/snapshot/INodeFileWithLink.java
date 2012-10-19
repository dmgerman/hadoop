begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.snapshot
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
name|snapshot
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|INodeFile
import|;
end_import

begin_comment
comment|/**  * INodeFile with a link to the next element.  * This class is used to represent the original file that is snapshotted.  * The snapshot files are represented by {@link INodeFileSnapshot}.  * The link of all the snapshot files and the original file form a circular  * linked list so that all elements are accessible by any of the elements.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|INodeFileWithLink
specifier|public
class|class
name|INodeFileWithLink
extends|extends
name|INodeFile
block|{
DECL|field|next
specifier|private
name|INodeFileWithLink
name|next
decl_stmt|;
DECL|method|INodeFileWithLink (INodeFile f)
specifier|public
name|INodeFileWithLink
parameter_list|(
name|INodeFile
name|f
parameter_list|)
block|{
name|super
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
DECL|method|setNext (INodeFileWithLink next)
name|void
name|setNext
parameter_list|(
name|INodeFileWithLink
name|next
parameter_list|)
block|{
name|this
operator|.
name|next
operator|=
name|next
expr_stmt|;
block|}
DECL|method|getNext ()
name|INodeFileWithLink
name|getNext
parameter_list|()
block|{
return|return
name|next
return|;
block|}
block|}
end_class

end_unit

