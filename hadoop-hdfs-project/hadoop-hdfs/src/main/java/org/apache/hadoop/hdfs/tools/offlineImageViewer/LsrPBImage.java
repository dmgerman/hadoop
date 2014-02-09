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
name|BufferedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|RandomAccessFile
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
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
name|permission
operator|.
name|PermissionStatus
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
name|protocol
operator|.
name|proto
operator|.
name|HdfsProtos
operator|.
name|BlockProto
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
name|FSImageFormatPBINode
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
name|FSImageFormatProtobuf
operator|.
name|SectionName
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
name|FSImageUtil
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
name|FsImageProto
operator|.
name|FileSummary
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
name|FsImageProto
operator|.
name|INodeDirectorySection
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
name|FsImageProto
operator|.
name|INodeSection
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
name|FsImageProto
operator|.
name|INodeSection
operator|.
name|INode
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
name|FsImageProto
operator|.
name|INodeSection
operator|.
name|INodeDirectory
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
name|FsImageProto
operator|.
name|INodeSection
operator|.
name|INodeFile
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
name|FsImageProto
operator|.
name|INodeSection
operator|.
name|INodeSymlink
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
name|FsImageProto
operator|.
name|StringTableSection
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
name|INodeId
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
name|io
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|LimitInputStream
import|;
end_import

begin_comment
comment|/**  * This is the tool for analyzing file sizes in the namespace image. In order to  * run the tool one should define a range of integers<tt>[0, maxSize]</tt> by  * specifying<tt>maxSize</tt> and a<tt>step</tt>. The range of integers is  * divided into segments of size<tt>step</tt>:  *<tt>[0, s<sub>1</sub>, ..., s<sub>n-1</sub>, maxSize]</tt>, and the visitor  * calculates how many files in the system fall into each segment  *<tt>[s<sub>i-1</sub>, s<sub>i</sub>)</tt>. Note that files larger than  *<tt>maxSize</tt> always fall into the very last segment.  *  *<h3>Input.</h3>  *<ul>  *<li><tt>filename</tt> specifies the location of the image file;</li>  *<li><tt>maxSize</tt> determines the range<tt>[0, maxSize]</tt> of files  * sizes considered by the visitor;</li>  *<li><tt>step</tt> the range is divided into segments of size step.</li>  *</ul>  *  *<h3>Output.</h3> The output file is formatted as a tab separated two column  * table: Size and NumFiles. Where Size represents the start of the segment, and  * numFiles is the number of files form the image which size falls in this  * segment.  *   */
end_comment

begin_class
DECL|class|LsrPBImage
specifier|final
class|class
name|LsrPBImage
block|{
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|out
specifier|private
specifier|final
name|PrintWriter
name|out
decl_stmt|;
DECL|field|stringTable
specifier|private
name|String
index|[]
name|stringTable
decl_stmt|;
DECL|field|inodes
specifier|private
name|HashMap
argument_list|<
name|Long
argument_list|,
name|INodeSection
operator|.
name|INode
argument_list|>
name|inodes
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
DECL|field|dirmap
specifier|private
name|HashMap
argument_list|<
name|Long
argument_list|,
name|long
index|[]
argument_list|>
name|dirmap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
DECL|method|LsrPBImage (Configuration conf, PrintWriter out)
specifier|public
name|LsrPBImage
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|PrintWriter
name|out
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
block|}
DECL|method|visit (RandomAccessFile file)
specifier|public
name|void
name|visit
parameter_list|(
name|RandomAccessFile
name|file
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|FSImageUtil
operator|.
name|checkFileFormat
argument_list|(
name|file
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unrecognized FSImage"
argument_list|)
throw|;
block|}
name|FileSummary
name|summary
init|=
name|FSImageUtil
operator|.
name|loadSummary
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|FileInputStream
name|fin
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fin
operator|=
operator|new
name|FileInputStream
argument_list|(
name|file
operator|.
name|getFD
argument_list|()
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|FileSummary
operator|.
name|Section
argument_list|>
name|sections
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|summary
operator|.
name|getSectionsList
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|sections
argument_list|,
operator|new
name|Comparator
argument_list|<
name|FileSummary
operator|.
name|Section
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|FileSummary
operator|.
name|Section
name|s1
parameter_list|,
name|FileSummary
operator|.
name|Section
name|s2
parameter_list|)
block|{
name|SectionName
name|n1
init|=
name|SectionName
operator|.
name|fromString
argument_list|(
name|s1
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|SectionName
name|n2
init|=
name|SectionName
operator|.
name|fromString
argument_list|(
name|s2
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|n1
operator|==
literal|null
condition|)
block|{
return|return
name|n2
operator|==
literal|null
condition|?
literal|0
else|:
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|n2
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
name|n1
operator|.
name|ordinal
argument_list|()
operator|-
name|n2
operator|.
name|ordinal
argument_list|()
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
for|for
control|(
name|FileSummary
operator|.
name|Section
name|s
range|:
name|sections
control|)
block|{
name|fin
operator|.
name|getChannel
argument_list|()
operator|.
name|position
argument_list|(
name|s
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|InputStream
name|is
init|=
name|FSImageUtil
operator|.
name|wrapInputStreamForCompression
argument_list|(
name|conf
argument_list|,
name|summary
operator|.
name|getCodec
argument_list|()
argument_list|,
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|LimitInputStream
argument_list|(
name|fin
argument_list|,
name|s
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|SectionName
operator|.
name|fromString
argument_list|(
name|s
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
case|case
name|STRING_TABLE
case|:
name|loadStringTable
argument_list|(
name|is
argument_list|)
expr_stmt|;
break|break;
case|case
name|INODE
case|:
name|loadINodeSection
argument_list|(
name|is
argument_list|)
expr_stmt|;
break|break;
case|case
name|INODE_DIR
case|:
name|loadINodeDirectorySection
argument_list|(
name|is
argument_list|)
expr_stmt|;
break|break;
default|default:
break|break;
block|}
block|}
name|list
argument_list|(
literal|""
argument_list|,
name|INodeId
operator|.
name|ROOT_INODE_ID
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|fin
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|list (String parent, long dirId)
specifier|private
name|void
name|list
parameter_list|(
name|String
name|parent
parameter_list|,
name|long
name|dirId
parameter_list|)
block|{
name|INode
name|inode
init|=
name|inodes
operator|.
name|get
argument_list|(
name|dirId
argument_list|)
decl_stmt|;
name|listINode
argument_list|(
name|parent
operator|.
name|isEmpty
argument_list|()
condition|?
literal|"/"
else|:
name|parent
argument_list|,
name|inode
argument_list|)
expr_stmt|;
name|long
index|[]
name|children
init|=
name|dirmap
operator|.
name|get
argument_list|(
name|dirId
argument_list|)
decl_stmt|;
if|if
condition|(
name|children
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|String
name|newParent
init|=
name|parent
operator|+
name|inode
operator|.
name|getName
argument_list|()
operator|.
name|toStringUtf8
argument_list|()
operator|+
literal|"/"
decl_stmt|;
for|for
control|(
name|long
name|cid
range|:
name|children
control|)
block|{
name|list
argument_list|(
name|newParent
argument_list|,
name|cid
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|listINode (String parent, INode inode)
specifier|private
name|void
name|listINode
parameter_list|(
name|String
name|parent
parameter_list|,
name|INode
name|inode
parameter_list|)
block|{
switch|switch
condition|(
name|inode
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|FILE
case|:
block|{
name|INodeFile
name|f
init|=
name|inode
operator|.
name|getFile
argument_list|()
decl_stmt|;
name|PermissionStatus
name|p
init|=
name|FSImageFormatPBINode
operator|.
name|Loader
operator|.
name|loadPermission
argument_list|(
name|f
operator|.
name|getPermission
argument_list|()
argument_list|,
name|stringTable
argument_list|)
decl_stmt|;
name|out
operator|.
name|print
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"-%s %2s %8s %10s %10s %10d %s%s\n"
argument_list|,
name|p
operator|.
name|getPermission
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|f
operator|.
name|getReplication
argument_list|()
argument_list|,
name|p
operator|.
name|getUserName
argument_list|()
argument_list|,
name|p
operator|.
name|getGroupName
argument_list|()
argument_list|,
name|f
operator|.
name|getModificationTime
argument_list|()
argument_list|,
name|getFileSize
argument_list|(
name|f
argument_list|)
argument_list|,
name|parent
argument_list|,
name|inode
operator|.
name|getName
argument_list|()
operator|.
name|toStringUtf8
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|DIRECTORY
case|:
block|{
name|INodeDirectory
name|d
init|=
name|inode
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
name|PermissionStatus
name|p
init|=
name|FSImageFormatPBINode
operator|.
name|Loader
operator|.
name|loadPermission
argument_list|(
name|d
operator|.
name|getPermission
argument_list|()
argument_list|,
name|stringTable
argument_list|)
decl_stmt|;
name|out
operator|.
name|print
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"d%s  - %8s %10s %10s %10d %s%s\n"
argument_list|,
name|p
operator|.
name|getPermission
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|p
operator|.
name|getUserName
argument_list|()
argument_list|,
name|p
operator|.
name|getGroupName
argument_list|()
argument_list|,
name|d
operator|.
name|getModificationTime
argument_list|()
argument_list|,
literal|0
argument_list|,
name|parent
argument_list|,
name|inode
operator|.
name|getName
argument_list|()
operator|.
name|toStringUtf8
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|SYMLINK
case|:
block|{
name|INodeSymlink
name|d
init|=
name|inode
operator|.
name|getSymlink
argument_list|()
decl_stmt|;
name|PermissionStatus
name|p
init|=
name|FSImageFormatPBINode
operator|.
name|Loader
operator|.
name|loadPermission
argument_list|(
name|d
operator|.
name|getPermission
argument_list|()
argument_list|,
name|stringTable
argument_list|)
decl_stmt|;
name|out
operator|.
name|print
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"-%s  - %8s %10s %10s %10d %s%s -> %s\n"
argument_list|,
name|p
operator|.
name|getPermission
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|p
operator|.
name|getUserName
argument_list|()
argument_list|,
name|p
operator|.
name|getGroupName
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|parent
argument_list|,
name|inode
operator|.
name|getName
argument_list|()
operator|.
name|toStringUtf8
argument_list|()
argument_list|,
name|d
operator|.
name|getTarget
argument_list|()
operator|.
name|toStringUtf8
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
break|break;
block|}
block|}
DECL|method|getFileSize (INodeFile f)
specifier|private
name|long
name|getFileSize
parameter_list|(
name|INodeFile
name|f
parameter_list|)
block|{
name|long
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
name|BlockProto
name|p
range|:
name|f
operator|.
name|getBlocksList
argument_list|()
control|)
block|{
name|size
operator|+=
name|p
operator|.
name|getNumBytes
argument_list|()
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
DECL|method|loadINodeDirectorySection (InputStream in)
specifier|private
name|void
name|loadINodeDirectorySection
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|INodeDirectorySection
operator|.
name|DirEntry
name|e
init|=
name|INodeDirectorySection
operator|.
name|DirEntry
operator|.
name|parseDelimitedFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
comment|// note that in is a LimitedInputStream
if|if
condition|(
name|e
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|long
index|[]
name|l
init|=
operator|new
name|long
index|[
name|e
operator|.
name|getChildrenCount
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|l
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|l
index|[
name|i
index|]
operator|=
name|e
operator|.
name|getChildren
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|dirmap
operator|.
name|put
argument_list|(
name|e
operator|.
name|getParent
argument_list|()
argument_list|,
name|l
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
name|e
operator|.
name|getNumOfRef
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|INodeSection
operator|.
name|INodeReference
operator|.
name|parseDelimitedFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|loadINodeSection (InputStream in)
specifier|private
name|void
name|loadINodeSection
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|INodeSection
name|s
init|=
name|INodeSection
operator|.
name|parseDelimitedFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|s
operator|.
name|getNumInodes
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|INodeSection
operator|.
name|INode
name|p
init|=
name|INodeSection
operator|.
name|INode
operator|.
name|parseDelimitedFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|inodes
operator|.
name|put
argument_list|(
name|p
operator|.
name|getId
argument_list|()
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|loadStringTable (InputStream in)
specifier|private
name|void
name|loadStringTable
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|StringTableSection
name|s
init|=
name|StringTableSection
operator|.
name|parseDelimitedFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|stringTable
operator|=
operator|new
name|String
index|[
name|s
operator|.
name|getNumEntry
argument_list|()
operator|+
literal|1
index|]
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
name|s
operator|.
name|getNumEntry
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|StringTableSection
operator|.
name|Entry
name|e
init|=
name|StringTableSection
operator|.
name|Entry
operator|.
name|parseDelimitedFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|stringTable
index|[
name|e
operator|.
name|getId
argument_list|()
index|]
operator|=
name|e
operator|.
name|getStr
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

