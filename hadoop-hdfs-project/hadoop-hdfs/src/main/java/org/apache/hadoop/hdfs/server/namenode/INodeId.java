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
comment|/**  * An id which uniquely identifies an inode  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|INodeId
class|class
name|INodeId
extends|extends
name|SequentialNumber
block|{
comment|/**    * The last reserved inode id. Reserve id 1 to 1000 for potential future    * usage. The id won't be recycled and is not expected to wrap around in a    * very long time. Root inode id will be 1001.    */
DECL|field|LAST_RESERVED_ID
specifier|public
specifier|static
specifier|final
name|long
name|LAST_RESERVED_ID
init|=
literal|1000L
decl_stmt|;
comment|/**    * The inode id validation of lease check will be skipped when the request    * uses GRANDFATHER_INODE_ID for backward compatibility.    */
DECL|field|GRANDFATHER_INODE_ID
specifier|public
specifier|static
specifier|final
name|long
name|GRANDFATHER_INODE_ID
init|=
literal|0
decl_stmt|;
DECL|method|INodeId ()
name|INodeId
parameter_list|()
block|{
name|super
argument_list|(
name|LAST_RESERVED_ID
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

