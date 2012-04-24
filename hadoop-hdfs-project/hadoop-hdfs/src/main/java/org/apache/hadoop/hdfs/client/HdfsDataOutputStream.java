begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|client
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FSDataOutputStream
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
name|fs
operator|.
name|FileSystem
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
name|DFSOutputStream
import|;
end_import

begin_comment
comment|/**  * The Hdfs implementation of {@link FSDataOutputStream}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|HdfsDataOutputStream
specifier|public
class|class
name|HdfsDataOutputStream
extends|extends
name|FSDataOutputStream
block|{
DECL|method|HdfsDataOutputStream (DFSOutputStream out, FileSystem.Statistics stats, long startPosition)
specifier|public
name|HdfsDataOutputStream
parameter_list|(
name|DFSOutputStream
name|out
parameter_list|,
name|FileSystem
operator|.
name|Statistics
name|stats
parameter_list|,
name|long
name|startPosition
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|out
argument_list|,
name|stats
argument_list|,
name|startPosition
argument_list|)
expr_stmt|;
block|}
DECL|method|HdfsDataOutputStream (DFSOutputStream out, FileSystem.Statistics stats )
specifier|public
name|HdfsDataOutputStream
parameter_list|(
name|DFSOutputStream
name|out
parameter_list|,
name|FileSystem
operator|.
name|Statistics
name|stats
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|out
argument_list|,
name|stats
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the actual number of replicas of the current block.    *     * This can be different from the designated replication factor of the file    * because the namenode does not maintain replication for the blocks which are    * currently being written to. Depending on the configuration, the client may    * continue to write to a block even if a few datanodes in the write pipeline    * have failed, or the client may add a new datanodes once a datanode has    * failed.    *     * @return the number of valid replicas of the current block    */
DECL|method|getCurrentBlockReplication ()
specifier|public
specifier|synchronized
name|int
name|getCurrentBlockReplication
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
operator|(
name|DFSOutputStream
operator|)
name|getWrappedStream
argument_list|()
operator|)
operator|.
name|getCurrentBlockReplication
argument_list|()
return|;
block|}
block|}
end_class

end_unit

