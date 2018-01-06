begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
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
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|BatchedRemoteIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|htrace
operator|.
name|core
operator|.
name|TraceScope
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|htrace
operator|.
name|core
operator|.
name|Tracer
import|;
end_import

begin_comment
comment|/**  * OpenFilesIterator is a remote iterator that iterates over the open files list  * managed by the NameNode. Since the list is retrieved in batches, it does not  * represent a consistent view of all open files.  */
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
DECL|class|OpenFilesIterator
specifier|public
class|class
name|OpenFilesIterator
extends|extends
name|BatchedRemoteIterator
argument_list|<
name|Long
argument_list|,
name|OpenFileEntry
argument_list|>
block|{
comment|/** No path to be filtered by default. */
DECL|field|FILTER_PATH_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|FILTER_PATH_DEFAULT
init|=
literal|"/"
decl_stmt|;
comment|/**    * Open file types to filter the results.    */
DECL|enum|OpenFilesType
specifier|public
enum|enum
name|OpenFilesType
block|{
DECL|enumConstant|ALL_OPEN_FILES
name|ALL_OPEN_FILES
argument_list|(
operator|(
name|short
operator|)
literal|0x01
argument_list|)
block|,
DECL|enumConstant|BLOCKING_DECOMMISSION
name|BLOCKING_DECOMMISSION
argument_list|(
operator|(
name|short
operator|)
literal|0x02
argument_list|)
block|;
DECL|field|mode
specifier|private
specifier|final
name|short
name|mode
decl_stmt|;
DECL|method|OpenFilesType (short mode)
name|OpenFilesType
parameter_list|(
name|short
name|mode
parameter_list|)
block|{
name|this
operator|.
name|mode
operator|=
name|mode
expr_stmt|;
block|}
DECL|method|getMode ()
specifier|public
name|short
name|getMode
parameter_list|()
block|{
return|return
name|mode
return|;
block|}
DECL|method|valueOf (short num)
specifier|public
specifier|static
name|OpenFilesType
name|valueOf
parameter_list|(
name|short
name|num
parameter_list|)
block|{
for|for
control|(
name|OpenFilesType
name|type
range|:
name|OpenFilesType
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|type
operator|.
name|getMode
argument_list|()
operator|==
name|num
condition|)
block|{
return|return
name|type
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
DECL|field|namenode
specifier|private
specifier|final
name|ClientProtocol
name|namenode
decl_stmt|;
DECL|field|tracer
specifier|private
specifier|final
name|Tracer
name|tracer
decl_stmt|;
DECL|field|types
specifier|private
specifier|final
name|EnumSet
argument_list|<
name|OpenFilesType
argument_list|>
name|types
decl_stmt|;
comment|/** List files filtered by given path. */
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
DECL|method|OpenFilesIterator (ClientProtocol namenode, Tracer tracer, EnumSet<OpenFilesType> types, String path)
specifier|public
name|OpenFilesIterator
parameter_list|(
name|ClientProtocol
name|namenode
parameter_list|,
name|Tracer
name|tracer
parameter_list|,
name|EnumSet
argument_list|<
name|OpenFilesType
argument_list|>
name|types
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|HdfsConstants
operator|.
name|GRANDFATHER_INODE_ID
argument_list|)
expr_stmt|;
name|this
operator|.
name|namenode
operator|=
name|namenode
expr_stmt|;
name|this
operator|.
name|tracer
operator|=
name|tracer
expr_stmt|;
name|this
operator|.
name|types
operator|=
name|types
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|makeRequest (Long prevId)
specifier|public
name|BatchedEntries
argument_list|<
name|OpenFileEntry
argument_list|>
name|makeRequest
parameter_list|(
name|Long
name|prevId
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|TraceScope
name|ignored
init|=
name|tracer
operator|.
name|newScope
argument_list|(
literal|"listOpenFiles"
argument_list|)
init|)
block|{
return|return
name|namenode
operator|.
name|listOpenFiles
argument_list|(
name|prevId
argument_list|,
name|types
argument_list|,
name|path
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|elementToPrevKey (OpenFileEntry entry)
specifier|public
name|Long
name|elementToPrevKey
parameter_list|(
name|OpenFileEntry
name|entry
parameter_list|)
block|{
return|return
name|entry
operator|.
name|getId
argument_list|()
return|;
block|}
block|}
end_class

end_unit

