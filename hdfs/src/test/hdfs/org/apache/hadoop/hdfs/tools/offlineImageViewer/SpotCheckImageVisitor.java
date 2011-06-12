begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools.offlineImageViewer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
operator|.
name|offlineImageViewer
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * ImageVisitor to spot check an fsimage and generate several statistics  * about it that we can compare with known values to give a reasonable  * assertion that the image was processed correctly.  */
end_comment

begin_class
DECL|class|SpotCheckImageVisitor
class|class
name|SpotCheckImageVisitor
extends|extends
name|ImageVisitor
block|{
comment|// Statistics gathered by the visitor for Inodes and InodesUnderConstruction
DECL|class|ImageInfo
specifier|static
specifier|public
class|class
name|ImageInfo
block|{
DECL|field|totalNumBlocks
specifier|public
name|long
name|totalNumBlocks
init|=
literal|0
decl_stmt|;
comment|// Total number of blocks in section
DECL|field|pathNames
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|pathNames
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// All path names
DECL|field|totalFileSize
specifier|public
name|long
name|totalFileSize
init|=
literal|0
decl_stmt|;
comment|// Total size of all the files
DECL|field|totalReplications
specifier|public
name|long
name|totalReplications
init|=
literal|0
decl_stmt|;
comment|// Sum of all the replications
block|}
DECL|field|inodes
specifier|final
specifier|private
name|ImageInfo
name|inodes
init|=
operator|new
name|ImageInfo
argument_list|()
decl_stmt|;
DECL|field|INUCs
specifier|final
specifier|private
name|ImageInfo
name|INUCs
init|=
operator|new
name|ImageInfo
argument_list|()
decl_stmt|;
DECL|field|current
specifier|private
name|ImageInfo
name|current
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|visit (ImageElement element, String value)
name|void
name|visit
parameter_list|(
name|ImageElement
name|element
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|element
operator|==
name|ImageElement
operator|.
name|NUM_BYTES
condition|)
name|current
operator|.
name|totalFileSize
operator|+=
name|Long
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|element
operator|==
name|ImageElement
operator|.
name|REPLICATION
condition|)
name|current
operator|.
name|totalReplications
operator|+=
name|Long
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|element
operator|==
name|ImageElement
operator|.
name|INODE_PATH
condition|)
name|current
operator|.
name|pathNames
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|visitEnclosingElement (ImageElement element, ImageElement key, String value)
name|void
name|visitEnclosingElement
parameter_list|(
name|ImageElement
name|element
parameter_list|,
name|ImageElement
name|key
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|element
condition|)
block|{
case|case
name|INODES
case|:
name|current
operator|=
name|inodes
expr_stmt|;
break|break;
case|case
name|INODES_UNDER_CONSTRUCTION
case|:
name|current
operator|=
name|INUCs
expr_stmt|;
break|break;
case|case
name|BLOCKS
case|:
name|current
operator|.
name|totalNumBlocks
operator|+=
name|Long
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
break|break;
comment|// OK to not have a default, we're skipping most of the values
block|}
block|}
DECL|method|getINodesInfo ()
specifier|public
name|ImageInfo
name|getINodesInfo
parameter_list|()
block|{
return|return
name|inodes
return|;
block|}
DECL|method|getINUCsInfo ()
specifier|public
name|ImageInfo
name|getINUCsInfo
parameter_list|()
block|{
return|return
name|INUCs
return|;
block|}
comment|// Unnecessary visitor methods
annotation|@
name|Override
DECL|method|finish ()
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|finishAbnormally ()
name|void
name|finishAbnormally
parameter_list|()
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|leaveEnclosingElement ()
name|void
name|leaveEnclosingElement
parameter_list|()
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|start ()
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|visitEnclosingElement (ImageElement element)
name|void
name|visitEnclosingElement
parameter_list|(
name|ImageElement
name|element
parameter_list|)
throws|throws
name|IOException
block|{}
block|}
end_class

end_unit

