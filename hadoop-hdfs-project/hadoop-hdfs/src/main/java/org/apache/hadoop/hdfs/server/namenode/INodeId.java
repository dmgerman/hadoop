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
name|util
operator|.
name|SequentialNumber
import|;
end_import

begin_comment
comment|/**  * An id which uniquely identifies an inode. Id 1 to 16384 are reserved for  * potential future usage. The id won't be recycled and is not expected to wrap  * around in a very long time. Root inode id is always 16385. Id 0 is used for  * backward compatibility support.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|INodeId
specifier|public
class|class
name|INodeId
extends|extends
name|SequentialNumber
block|{
comment|/**    * The last reserved inode id. InodeIDs are allocated from LAST_RESERVED_ID +    * 1.    */
DECL|field|LAST_RESERVED_ID
specifier|public
specifier|static
specifier|final
name|long
name|LAST_RESERVED_ID
init|=
literal|1
operator|<<
literal|14
decl_stmt|;
comment|// 16384
DECL|field|ROOT_INODE_ID
specifier|public
specifier|static
specifier|final
name|long
name|ROOT_INODE_ID
init|=
name|LAST_RESERVED_ID
operator|+
literal|1
decl_stmt|;
comment|// 16385
DECL|field|INVALID_INODE_ID
specifier|public
specifier|static
specifier|final
name|long
name|INVALID_INODE_ID
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|INodeId ()
name|INodeId
parameter_list|()
block|{
name|super
argument_list|(
name|ROOT_INODE_ID
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

