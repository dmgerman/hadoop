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
name|hdfs
operator|.
name|util
operator|.
name|EnumCounters
import|;
end_import

begin_comment
comment|/**  * The content types such as file, directory and symlink to be computed.  */
end_comment

begin_enum
DECL|enum|Content
specifier|public
enum|enum
name|Content
block|{
comment|/** The number of files. */
DECL|enumConstant|FILE
name|FILE
block|,
comment|/** The number of directories. */
DECL|enumConstant|DIRECTORY
name|DIRECTORY
block|,
comment|/** The number of symlinks. */
DECL|enumConstant|SYMLINK
name|SYMLINK
block|,
comment|/** The total of file length in bytes. */
DECL|enumConstant|LENGTH
name|LENGTH
block|,
comment|/** The total of disk space usage in bytes including replication. */
DECL|enumConstant|DISKSPACE
name|DISKSPACE
block|,
comment|/** The number of snapshots. */
DECL|enumConstant|SNAPSHOT
name|SNAPSHOT
block|,
comment|/** The number of snapshottable directories. */
DECL|enumConstant|SNAPSHOTTABLE_DIRECTORY
name|SNAPSHOTTABLE_DIRECTORY
block|;
comment|/** Content counts. */
DECL|class|Counts
specifier|public
specifier|static
class|class
name|Counts
extends|extends
name|EnumCounters
argument_list|<
name|Content
argument_list|>
block|{
DECL|method|newInstance ()
specifier|public
specifier|static
name|Counts
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|Counts
argument_list|()
return|;
block|}
DECL|method|Counts ()
specifier|private
name|Counts
parameter_list|()
block|{
name|super
argument_list|(
name|Content
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|FACTORY
specifier|private
specifier|static
specifier|final
name|EnumCounters
operator|.
name|Factory
argument_list|<
name|Content
argument_list|,
name|Counts
argument_list|>
name|FACTORY
init|=
operator|new
name|EnumCounters
operator|.
name|Factory
argument_list|<
name|Content
argument_list|,
name|Counts
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Counts
name|newInstance
parameter_list|()
block|{
return|return
name|Counts
operator|.
name|newInstance
argument_list|()
return|;
block|}
block|}
decl_stmt|;
comment|/** A map of counters for the current state and the snapshots. */
DECL|class|CountsMap
specifier|public
specifier|static
class|class
name|CountsMap
extends|extends
name|EnumCounters
operator|.
name|Map
argument_list|<
name|CountsMap
operator|.
name|Key
argument_list|,
name|Content
argument_list|,
name|Counts
argument_list|>
block|{
comment|/** The key type of the map. */
DECL|enum|Key
DECL|enumConstant|CURRENT
DECL|enumConstant|SNAPSHOT
specifier|public
enum|enum
name|Key
block|{
name|CURRENT
block|,
name|SNAPSHOT
block|}
DECL|method|CountsMap ()
name|CountsMap
parameter_list|()
block|{
name|super
argument_list|(
name|FACTORY
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_enum

end_unit

