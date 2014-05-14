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
name|AbstractMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_comment
comment|/**  * A DelimitedImageVisitor generates a text representation of the fsimage,  * with each element separated by a delimiter string.  All of the elements  * common to both inodes and inodes-under-construction are included. When   * processing an fsimage with a layout version that did not include an   * element, such as AccessTime, the output file will include a column  * for the value, but no value will be included.  *   * Individual block information for each file is not currently included.  *   * The default delimiter is tab, as this is an unlikely value to be included  * an inode path or other text metadata.  The delimiter value can be via the  * constructor.  */
end_comment

begin_class
DECL|class|DelimitedImageVisitor
class|class
name|DelimitedImageVisitor
extends|extends
name|TextWriterImageVisitor
block|{
DECL|field|defaultDelimiter
specifier|private
specifier|static
specifier|final
name|String
name|defaultDelimiter
init|=
literal|"\t"
decl_stmt|;
DECL|field|elemQ
specifier|final
specifier|private
name|LinkedList
argument_list|<
name|ImageElement
argument_list|>
name|elemQ
init|=
operator|new
name|LinkedList
argument_list|<
name|ImageElement
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|fileSize
specifier|private
name|long
name|fileSize
init|=
literal|0l
decl_stmt|;
comment|// Elements of fsimage we're interested in tracking
DECL|field|elementsToTrack
specifier|private
specifier|final
name|Collection
argument_list|<
name|ImageElement
argument_list|>
name|elementsToTrack
decl_stmt|;
comment|// Values for each of the elements in elementsToTrack
DECL|field|elements
specifier|private
specifier|final
name|AbstractMap
argument_list|<
name|ImageElement
argument_list|,
name|String
argument_list|>
name|elements
init|=
operator|new
name|HashMap
argument_list|<
name|ImageElement
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|delimiter
specifier|private
specifier|final
name|String
name|delimiter
decl_stmt|;
block|{
name|elementsToTrack
operator|=
operator|new
name|ArrayList
argument_list|<
name|ImageElement
argument_list|>
argument_list|()
expr_stmt|;
comment|// This collection determines what elements are tracked and the order
comment|// in which they are output
name|Collections
operator|.
name|addAll
parameter_list|(
name|elementsToTrack
parameter_list|,
name|ImageElement
operator|.
name|INODE_PATH
parameter_list|,
name|ImageElement
operator|.
name|REPLICATION
parameter_list|,
name|ImageElement
operator|.
name|MODIFICATION_TIME
parameter_list|,
name|ImageElement
operator|.
name|ACCESS_TIME
parameter_list|,
name|ImageElement
operator|.
name|BLOCK_SIZE
parameter_list|,
name|ImageElement
operator|.
name|NUM_BLOCKS
parameter_list|,
name|ImageElement
operator|.
name|NUM_BYTES
parameter_list|,
name|ImageElement
operator|.
name|NS_QUOTA
parameter_list|,
name|ImageElement
operator|.
name|DS_QUOTA
parameter_list|,
name|ImageElement
operator|.
name|PERMISSION_STRING
parameter_list|,
name|ImageElement
operator|.
name|USER_NAME
parameter_list|,
name|ImageElement
operator|.
name|GROUP_NAME
parameter_list|)
constructor_decl|;
block|}
DECL|method|DelimitedImageVisitor (String filename)
specifier|public
name|DelimitedImageVisitor
parameter_list|(
name|String
name|filename
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|filename
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|DelimitedImageVisitor (String outputFile, boolean printToScreen)
specifier|public
name|DelimitedImageVisitor
parameter_list|(
name|String
name|outputFile
parameter_list|,
name|boolean
name|printToScreen
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|outputFile
argument_list|,
name|printToScreen
argument_list|,
name|defaultDelimiter
argument_list|)
expr_stmt|;
block|}
DECL|method|DelimitedImageVisitor (String outputFile, boolean printToScreen, String delimiter)
specifier|public
name|DelimitedImageVisitor
parameter_list|(
name|String
name|outputFile
parameter_list|,
name|boolean
name|printToScreen
parameter_list|,
name|String
name|delimiter
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|outputFile
argument_list|,
name|printToScreen
argument_list|)
expr_stmt|;
name|this
operator|.
name|delimiter
operator|=
name|delimiter
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
comment|/**    * Reset the values of the elements we're tracking in order to handle    * the next file    */
DECL|method|reset ()
specifier|private
name|void
name|reset
parameter_list|()
block|{
name|elements
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|ImageElement
name|e
range|:
name|elementsToTrack
control|)
name|elements
operator|.
name|put
argument_list|(
name|e
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fileSize
operator|=
literal|0l
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|leaveEnclosingElement ()
name|void
name|leaveEnclosingElement
parameter_list|()
throws|throws
name|IOException
block|{
name|ImageElement
name|elem
init|=
name|elemQ
operator|.
name|pop
argument_list|()
decl_stmt|;
comment|// If we're done with an inode, write out our results and start over
if|if
condition|(
name|elem
operator|==
name|ImageElement
operator|.
name|INODE
operator|||
name|elem
operator|==
name|ImageElement
operator|.
name|INODE_UNDER_CONSTRUCTION
condition|)
block|{
name|writeLine
argument_list|()
expr_stmt|;
name|write
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Iterate through all the elements we're tracking and, if a value was    * recorded for it, write it out.    */
DECL|method|writeLine ()
specifier|private
name|void
name|writeLine
parameter_list|()
throws|throws
name|IOException
block|{
name|Iterator
argument_list|<
name|ImageElement
argument_list|>
name|it
init|=
name|elementsToTrack
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ImageElement
name|e
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|v
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|e
operator|==
name|ImageElement
operator|.
name|NUM_BYTES
condition|)
name|v
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|fileSize
argument_list|)
expr_stmt|;
else|else
name|v
operator|=
name|elements
operator|.
name|get
argument_list|(
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
name|write
argument_list|(
name|v
argument_list|)
expr_stmt|;
if|if
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
name|write
argument_list|(
name|delimiter
argument_list|)
expr_stmt|;
block|}
block|}
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
comment|// Explicitly label the root path
if|if
condition|(
name|element
operator|==
name|ImageElement
operator|.
name|INODE_PATH
operator|&&
name|value
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
name|value
operator|=
literal|"/"
expr_stmt|;
comment|// Special case of file size, which is sum of the num bytes in each block
if|if
condition|(
name|element
operator|==
name|ImageElement
operator|.
name|NUM_BYTES
condition|)
name|fileSize
operator|+=
name|Long
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|elements
operator|.
name|containsKey
argument_list|(
name|element
argument_list|)
operator|&&
name|element
operator|!=
name|ImageElement
operator|.
name|NUM_BYTES
condition|)
name|elements
operator|.
name|put
argument_list|(
name|element
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
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
block|{
name|elemQ
operator|.
name|push
argument_list|(
name|element
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
comment|// Special case as numBlocks is an attribute of the blocks element
if|if
condition|(
name|key
operator|==
name|ImageElement
operator|.
name|NUM_BLOCKS
operator|&&
name|elements
operator|.
name|containsKey
argument_list|(
name|ImageElement
operator|.
name|NUM_BLOCKS
argument_list|)
condition|)
name|elements
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|elemQ
operator|.
name|push
argument_list|(
name|element
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start ()
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
comment|/* Nothing to do */
block|}
block|}
end_class

end_unit

