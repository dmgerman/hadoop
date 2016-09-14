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
name|LinkedList
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
name|StringUtils
import|;
end_import

begin_comment
comment|/**  * File size distribution visitor.  *   *<h3>Description.</h3>  * This is the tool for analyzing file sizes in the namespace image.  * In order to run the tool one should define a range of integers  *<tt>[0, maxSize]</tt> by specifying<tt>maxSize</tt> and a<tt>step</tt>.  * The range of integers is divided into segments of size<tt>step</tt>:   *<tt>[0, s<sub>1</sub>, ..., s<sub>n-1</sub>, maxSize]</tt>,  * and the visitor calculates how many files in the system fall into   * each segment<tt>[s<sub>i-1</sub>, s<sub>i</sub>)</tt>.   * Note that files larger than<tt>maxSize</tt> always fall into   * the very last segment.  *   *<h3>Input.</h3>  *<ul>  *<li><tt>filename</tt> specifies the location of the image file;</li>  *<li><tt>maxSize</tt> determines the range<tt>[0, maxSize]</tt> of files  * sizes considered by the visitor;</li>  *<li><tt>step</tt> the range is divided into segments of size step.</li>  *</ul>  *  *<h3>Output.</h3>  * The output file is formatted as a tab separated two column table:  * Size and NumFiles. Where Size represents the start of the segment,  * and numFiles is the number of files form the image which size falls in   * this segment.  */
end_comment

begin_class
DECL|class|FileDistributionVisitor
class|class
name|FileDistributionVisitor
extends|extends
name|TextWriterImageVisitor
block|{
DECL|field|elemS
specifier|final
specifier|private
name|LinkedList
argument_list|<
name|ImageElement
argument_list|>
name|elemS
init|=
operator|new
name|LinkedList
argument_list|<
name|ImageElement
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|MAX_SIZE_DEFAULT
specifier|private
specifier|final
specifier|static
name|long
name|MAX_SIZE_DEFAULT
init|=
literal|0x2000000000L
decl_stmt|;
comment|// 1/8 TB = 2^37
DECL|field|INTERVAL_DEFAULT
specifier|private
specifier|final
specifier|static
name|int
name|INTERVAL_DEFAULT
init|=
literal|0x200000
decl_stmt|;
comment|// 2 MB = 2^21
DECL|field|distribution
specifier|private
name|int
index|[]
name|distribution
decl_stmt|;
DECL|field|maxSize
specifier|private
name|long
name|maxSize
decl_stmt|;
DECL|field|step
specifier|private
name|int
name|step
decl_stmt|;
DECL|field|totalFiles
specifier|private
name|int
name|totalFiles
decl_stmt|;
DECL|field|totalDirectories
specifier|private
name|int
name|totalDirectories
decl_stmt|;
DECL|field|totalBlocks
specifier|private
name|int
name|totalBlocks
decl_stmt|;
DECL|field|totalSpace
specifier|private
name|long
name|totalSpace
decl_stmt|;
DECL|field|maxFileSize
specifier|private
name|long
name|maxFileSize
decl_stmt|;
DECL|field|current
specifier|private
name|FileContext
name|current
decl_stmt|;
DECL|field|inInode
specifier|private
name|boolean
name|inInode
init|=
literal|false
decl_stmt|;
DECL|field|formatOutput
specifier|private
name|boolean
name|formatOutput
init|=
literal|false
decl_stmt|;
comment|/**    * File or directory information.    */
DECL|class|FileContext
specifier|private
specifier|static
class|class
name|FileContext
block|{
DECL|field|path
name|String
name|path
decl_stmt|;
DECL|field|fileSize
name|long
name|fileSize
decl_stmt|;
DECL|field|numBlocks
name|int
name|numBlocks
decl_stmt|;
DECL|field|replication
name|int
name|replication
decl_stmt|;
block|}
DECL|method|FileDistributionVisitor (String filename, long maxSize, int step, boolean formatOutput)
specifier|public
name|FileDistributionVisitor
parameter_list|(
name|String
name|filename
parameter_list|,
name|long
name|maxSize
parameter_list|,
name|int
name|step
parameter_list|,
name|boolean
name|formatOutput
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|filename
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxSize
operator|=
operator|(
name|maxSize
operator|==
literal|0
condition|?
name|MAX_SIZE_DEFAULT
else|:
name|maxSize
operator|)
expr_stmt|;
name|this
operator|.
name|step
operator|=
operator|(
name|step
operator|==
literal|0
condition|?
name|INTERVAL_DEFAULT
else|:
name|step
operator|)
expr_stmt|;
name|this
operator|.
name|formatOutput
operator|=
name|formatOutput
expr_stmt|;
name|long
name|numIntervals
init|=
name|this
operator|.
name|maxSize
operator|/
name|this
operator|.
name|step
decl_stmt|;
if|if
condition|(
name|numIntervals
operator|>=
name|Integer
operator|.
name|MAX_VALUE
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Too many distribution intervals "
operator|+
name|numIntervals
argument_list|)
throw|;
name|this
operator|.
name|distribution
operator|=
operator|new
name|int
index|[
literal|1
operator|+
call|(
name|int
call|)
argument_list|(
name|numIntervals
argument_list|)
index|]
expr_stmt|;
name|this
operator|.
name|totalFiles
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|totalDirectories
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|totalBlocks
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|totalSpace
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|maxFileSize
operator|=
literal|0
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
block|{}
annotation|@
name|Override
DECL|method|finish ()
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
name|output
argument_list|()
expr_stmt|;
name|super
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finishAbnormally ()
name|void
name|finishAbnormally
parameter_list|()
throws|throws
name|IOException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"*** Image processing finished abnormally.  Ending ***"
argument_list|)
expr_stmt|;
name|output
argument_list|()
expr_stmt|;
name|super
operator|.
name|finishAbnormally
argument_list|()
expr_stmt|;
block|}
DECL|method|output ()
specifier|private
name|void
name|output
parameter_list|()
throws|throws
name|IOException
block|{
comment|// write the distribution into the output file
name|write
argument_list|(
operator|(
name|formatOutput
condition|?
literal|"Size Range"
else|:
literal|"Size"
operator|)
operator|+
literal|"\tNumFiles\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|distribution
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|distribution
index|[
name|i
index|]
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|formatOutput
condition|)
block|{
name|write
argument_list|(
operator|(
name|i
operator|==
literal|0
condition|?
literal|"["
else|:
literal|"("
operator|)
operator|+
name|StringUtils
operator|.
name|byteDesc
argument_list|(
operator|(
call|(
name|long
call|)
argument_list|(
name|i
operator|==
literal|0
condition|?
literal|0
else|:
name|i
operator|-
literal|1
argument_list|)
operator|*
name|step
operator|)
argument_list|)
operator|+
literal|", "
operator|+
name|StringUtils
operator|.
name|byteDesc
argument_list|(
call|(
name|long
call|)
argument_list|(
name|i
operator|==
name|distribution
operator|.
name|length
operator|-
literal|1
condition|?
name|maxFileSize
else|:
name|i
operator|*
name|step
argument_list|)
argument_list|)
operator|+
literal|"]\t"
operator|+
name|distribution
index|[
name|i
index|]
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|write
argument_list|(
operator|(
operator|(
name|long
operator|)
name|i
operator|*
name|step
operator|)
operator|+
literal|"\t"
operator|+
name|distribution
index|[
name|i
index|]
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"totalFiles = "
operator|+
name|totalFiles
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"totalDirectories = "
operator|+
name|totalDirectories
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"totalBlocks = "
operator|+
name|totalBlocks
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"totalSpace = "
operator|+
name|totalSpace
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"maxFileSize = "
operator|+
name|maxFileSize
argument_list|)
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
name|elemS
operator|.
name|pop
argument_list|()
decl_stmt|;
if|if
condition|(
name|elem
operator|!=
name|ImageElement
operator|.
name|INODE
operator|&&
name|elem
operator|!=
name|ImageElement
operator|.
name|INODE_UNDER_CONSTRUCTION
condition|)
return|return;
name|inInode
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|current
operator|.
name|numBlocks
operator|<
literal|0
condition|)
block|{
name|totalDirectories
operator|++
expr_stmt|;
return|return;
block|}
name|totalFiles
operator|++
expr_stmt|;
name|totalBlocks
operator|+=
name|current
operator|.
name|numBlocks
expr_stmt|;
name|totalSpace
operator|+=
name|current
operator|.
name|fileSize
operator|*
name|current
operator|.
name|replication
expr_stmt|;
if|if
condition|(
name|maxFileSize
operator|<
name|current
operator|.
name|fileSize
condition|)
name|maxFileSize
operator|=
name|current
operator|.
name|fileSize
expr_stmt|;
name|int
name|high
decl_stmt|;
if|if
condition|(
name|current
operator|.
name|fileSize
operator|>
name|maxSize
condition|)
name|high
operator|=
name|distribution
operator|.
name|length
operator|-
literal|1
expr_stmt|;
else|else
name|high
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
operator|(
name|double
operator|)
name|current
operator|.
name|fileSize
operator|/
name|step
argument_list|)
expr_stmt|;
if|if
condition|(
name|high
operator|>=
name|distribution
operator|.
name|length
condition|)
block|{
name|high
operator|=
name|distribution
operator|.
name|length
operator|-
literal|1
expr_stmt|;
block|}
name|distribution
index|[
name|high
index|]
operator|++
expr_stmt|;
if|if
condition|(
name|totalFiles
operator|%
literal|1000000
operator|==
literal|1
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Files processed: "
operator|+
name|totalFiles
operator|+
literal|"  Current: "
operator|+
name|current
operator|.
name|path
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|inInode
condition|)
block|{
switch|switch
condition|(
name|element
condition|)
block|{
case|case
name|INODE_PATH
case|:
name|current
operator|.
name|path
operator|=
operator|(
name|value
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|?
literal|"/"
else|:
name|value
operator|)
expr_stmt|;
break|break;
case|case
name|REPLICATION
case|:
name|current
operator|.
name|replication
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
name|NUM_BYTES
case|:
name|current
operator|.
name|fileSize
operator|+=
name|Long
operator|.
name|parseLong
argument_list|(
name|value
argument_list|)
expr_stmt|;
break|break;
default|default:
break|break;
block|}
block|}
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
name|elemS
operator|.
name|push
argument_list|(
name|element
argument_list|)
expr_stmt|;
if|if
condition|(
name|element
operator|==
name|ImageElement
operator|.
name|INODE
operator|||
name|element
operator|==
name|ImageElement
operator|.
name|INODE_UNDER_CONSTRUCTION
condition|)
block|{
name|current
operator|=
operator|new
name|FileContext
argument_list|()
expr_stmt|;
name|inInode
operator|=
literal|true
expr_stmt|;
block|}
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
name|elemS
operator|.
name|push
argument_list|(
name|element
argument_list|)
expr_stmt|;
if|if
condition|(
name|element
operator|==
name|ImageElement
operator|.
name|INODE
operator|||
name|element
operator|==
name|ImageElement
operator|.
name|INODE_UNDER_CONSTRUCTION
condition|)
name|inInode
operator|=
literal|true
expr_stmt|;
elseif|else
if|if
condition|(
name|element
operator|==
name|ImageElement
operator|.
name|BLOCKS
condition|)
name|current
operator|.
name|numBlocks
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

