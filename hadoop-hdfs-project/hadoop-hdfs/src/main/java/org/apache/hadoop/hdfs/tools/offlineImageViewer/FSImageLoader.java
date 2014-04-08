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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|FsPermission
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
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|ObjectMapper
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
name|base
operator|.
name|Preconditions
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
comment|/**  * FSImageLoader loads fsimage and provide methods to return JSON formatted  * file status of the namespace of the fsimage.  */
end_comment

begin_class
DECL|class|FSImageLoader
specifier|public
class|class
name|FSImageLoader
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|FSImageHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|stringTable
specifier|private
specifier|static
name|String
index|[]
name|stringTable
decl_stmt|;
DECL|field|inodes
specifier|private
specifier|static
name|Map
argument_list|<
name|Long
argument_list|,
name|FsImageProto
operator|.
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
specifier|static
name|Map
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
specifier|private
specifier|static
name|List
argument_list|<
name|FsImageProto
operator|.
name|INodeReferenceSection
operator|.
name|INodeReference
argument_list|>
DECL|field|refList
name|refList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|method|FSImageLoader ()
specifier|private
name|FSImageLoader
parameter_list|()
block|{}
comment|/**    * Load fsimage into the memory.    * @param inputFile the filepath of the fsimage to load.    * @return FSImageLoader    * @throws IOException if failed to load fsimage.    */
DECL|method|load (String inputFile)
specifier|public
specifier|static
name|FSImageLoader
name|load
parameter_list|(
name|String
name|inputFile
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|RandomAccessFile
name|file
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|inputFile
argument_list|,
literal|"r"
argument_list|)
decl_stmt|;
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
name|FsImageProto
operator|.
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
name|FsImageProto
operator|.
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
name|FsImageProto
operator|.
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
name|FsImageProto
operator|.
name|FileSummary
operator|.
name|Section
name|s1
parameter_list|,
name|FsImageProto
operator|.
name|FileSummary
operator|.
name|Section
name|s2
parameter_list|)
block|{
name|FSImageFormatProtobuf
operator|.
name|SectionName
name|n1
init|=
name|FSImageFormatProtobuf
operator|.
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
name|FSImageFormatProtobuf
operator|.
name|SectionName
name|n2
init|=
name|FSImageFormatProtobuf
operator|.
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
name|FsImageProto
operator|.
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
name|FSImageFormatProtobuf
operator|.
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
name|INODE_REFERENCE
case|:
name|loadINodeReferenceSection
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
return|return
operator|new
name|FSImageLoader
argument_list|()
return|;
block|}
DECL|method|loadINodeDirectorySection (InputStream in)
specifier|private
specifier|static
name|void
name|loadINodeDirectorySection
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Loading directory section"
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
name|FsImageProto
operator|.
name|INodeDirectorySection
operator|.
name|DirEntry
name|e
init|=
name|FsImageProto
operator|.
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
operator|+
name|e
operator|.
name|getRefChildrenCount
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
name|e
operator|.
name|getChildrenCount
argument_list|()
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
for|for
control|(
name|int
name|i
init|=
name|e
operator|.
name|getChildrenCount
argument_list|()
init|;
name|i
operator|<
name|l
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|refId
init|=
name|e
operator|.
name|getRefChildren
argument_list|(
name|i
operator|-
name|e
operator|.
name|getChildrenCount
argument_list|()
argument_list|)
decl_stmt|;
name|l
index|[
name|i
index|]
operator|=
name|refList
operator|.
name|get
argument_list|(
name|refId
argument_list|)
operator|.
name|getReferredId
argument_list|()
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
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Loaded directory (parent "
operator|+
name|e
operator|.
name|getParent
argument_list|()
operator|+
literal|") with "
operator|+
name|e
operator|.
name|getChildrenCount
argument_list|()
operator|+
literal|" children and "
operator|+
name|e
operator|.
name|getRefChildrenCount
argument_list|()
operator|+
literal|" reference children"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Loaded "
operator|+
name|dirmap
operator|.
name|size
argument_list|()
operator|+
literal|" directories"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|loadINodeReferenceSection (InputStream in)
specifier|private
specifier|static
name|void
name|loadINodeReferenceSection
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Loading inode reference section"
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
name|FsImageProto
operator|.
name|INodeReferenceSection
operator|.
name|INodeReference
name|e
init|=
name|FsImageProto
operator|.
name|INodeReferenceSection
operator|.
name|INodeReference
operator|.
name|parseDelimitedFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|refList
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Loaded inode reference named '"
operator|+
name|e
operator|.
name|getName
argument_list|()
operator|+
literal|"' referring to id "
operator|+
name|e
operator|.
name|getReferredId
argument_list|()
operator|+
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Loaded "
operator|+
name|refList
operator|.
name|size
argument_list|()
operator|+
literal|" inode references"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|loadINodeSection (InputStream in)
specifier|private
specifier|static
name|void
name|loadINodeSection
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|FsImageProto
operator|.
name|INodeSection
name|s
init|=
name|FsImageProto
operator|.
name|INodeSection
operator|.
name|parseDelimitedFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found "
operator|+
name|s
operator|.
name|getNumInodes
argument_list|()
operator|+
literal|" inodes in inode section"
argument_list|)
expr_stmt|;
block|}
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
name|FsImageProto
operator|.
name|INodeSection
operator|.
name|INode
name|p
init|=
name|FsImageProto
operator|.
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
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Loaded inode id "
operator|+
name|p
operator|.
name|getId
argument_list|()
operator|+
literal|" type "
operator|+
name|p
operator|.
name|getType
argument_list|()
operator|+
literal|" name '"
operator|+
name|p
operator|.
name|getName
argument_list|()
operator|.
name|toStringUtf8
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|loadStringTable (InputStream in)
specifier|private
specifier|static
name|void
name|loadStringTable
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|FsImageProto
operator|.
name|StringTableSection
name|s
init|=
name|FsImageProto
operator|.
name|StringTableSection
operator|.
name|parseDelimitedFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found "
operator|+
name|s
operator|.
name|getNumEntry
argument_list|()
operator|+
literal|" strings in string section"
argument_list|)
expr_stmt|;
block|}
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
name|FsImageProto
operator|.
name|StringTableSection
operator|.
name|Entry
name|e
init|=
name|FsImageProto
operator|.
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
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Loaded string "
operator|+
name|e
operator|.
name|getStr
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Return the JSON formatted list of the files in the specified directory.    * @param path a path specifies a directory to list    * @return JSON formatted file list in the directory    * @throws IOException if failed to serialize fileStatus to JSON.    */
DECL|method|listStatus (String path)
specifier|public
name|String
name|listStatus
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|fileStatusList
init|=
name|getFileStatusList
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"{\"FileStatuses\":{\"FileStatus\":[\n"
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|fileStatusMap
range|:
name|fileStatusList
control|)
block|{
if|if
condition|(
name|i
operator|++
operator|!=
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|mapper
operator|.
name|writeValueAsString
argument_list|(
name|fileStatusMap
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"\n]}}\n"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getFileStatusList (String path)
specifier|private
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|getFileStatusList
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|long
name|id
init|=
name|getINodeId
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|FsImageProto
operator|.
name|INodeSection
operator|.
name|INode
name|inode
init|=
name|inodes
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|inode
operator|.
name|getType
argument_list|()
operator|==
name|FsImageProto
operator|.
name|INodeSection
operator|.
name|INode
operator|.
name|Type
operator|.
name|DIRECTORY
condition|)
block|{
name|long
index|[]
name|children
init|=
name|dirmap
operator|.
name|get
argument_list|(
name|id
argument_list|)
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
operator|.
name|add
argument_list|(
name|getFileStatus
argument_list|(
name|inodes
operator|.
name|get
argument_list|(
name|cid
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|list
operator|.
name|add
argument_list|(
name|getFileStatus
argument_list|(
name|inode
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
comment|/**    * Return the INodeId of the specified path.    */
DECL|method|getINodeId (String strPath)
specifier|private
name|long
name|getINodeId
parameter_list|(
name|String
name|strPath
parameter_list|)
block|{
if|if
condition|(
name|strPath
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
return|return
name|INodeId
operator|.
name|ROOT_INODE_ID
return|;
block|}
name|String
index|[]
name|nameList
init|=
name|strPath
operator|.
name|split
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|nameList
operator|.
name|length
operator|>
literal|1
argument_list|,
literal|"Illegal path: "
operator|+
name|strPath
argument_list|)
expr_stmt|;
name|long
name|id
init|=
name|INodeId
operator|.
name|ROOT_INODE_ID
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|nameList
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|long
index|[]
name|children
init|=
name|dirmap
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|children
argument_list|,
literal|"File: "
operator|+
name|strPath
operator|+
literal|" is not found in the fsimage."
argument_list|)
expr_stmt|;
name|String
name|cName
init|=
name|nameList
index|[
name|i
index|]
decl_stmt|;
name|boolean
name|findChildren
init|=
literal|false
decl_stmt|;
for|for
control|(
name|long
name|cid
range|:
name|children
control|)
block|{
if|if
condition|(
name|cName
operator|.
name|equals
argument_list|(
name|inodes
operator|.
name|get
argument_list|(
name|cid
argument_list|)
operator|.
name|getName
argument_list|()
operator|.
name|toStringUtf8
argument_list|()
argument_list|)
condition|)
block|{
name|id
operator|=
name|cid
expr_stmt|;
name|findChildren
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|findChildren
argument_list|,
literal|"File: "
operator|+
name|strPath
operator|+
literal|" is not found in the fsimage."
argument_list|)
expr_stmt|;
block|}
return|return
name|id
return|;
block|}
DECL|method|getFileStatus (FsImageProto.INodeSection.INode inode, boolean printSuffix)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getFileStatus
parameter_list|(
name|FsImageProto
operator|.
name|INodeSection
operator|.
name|INode
name|inode
parameter_list|,
name|boolean
name|printSuffix
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
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
name|FsImageProto
operator|.
name|INodeSection
operator|.
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
name|map
operator|.
name|put
argument_list|(
literal|"accessTime"
argument_list|,
name|f
operator|.
name|getAccessTime
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"blockSize"
argument_list|,
name|f
operator|.
name|getPreferredBlockSize
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"group"
argument_list|,
name|p
operator|.
name|getGroupName
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"length"
argument_list|,
name|getFileSize
argument_list|(
name|f
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"modificationTime"
argument_list|,
name|f
operator|.
name|getModificationTime
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"owner"
argument_list|,
name|p
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"pathSuffix"
argument_list|,
name|printSuffix
condition|?
name|inode
operator|.
name|getName
argument_list|()
operator|.
name|toStringUtf8
argument_list|()
else|:
literal|""
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"permission"
argument_list|,
name|toString
argument_list|(
name|p
operator|.
name|getPermission
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"replication"
argument_list|,
name|f
operator|.
name|getReplication
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"type"
argument_list|,
name|inode
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"fileId"
argument_list|,
name|inode
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"childrenNum"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|map
return|;
block|}
case|case
name|DIRECTORY
case|:
block|{
name|FsImageProto
operator|.
name|INodeSection
operator|.
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
name|map
operator|.
name|put
argument_list|(
literal|"accessTime"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"blockSize"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"group"
argument_list|,
name|p
operator|.
name|getGroupName
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"length"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"modificationTime"
argument_list|,
name|d
operator|.
name|getModificationTime
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"owner"
argument_list|,
name|p
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"pathSuffix"
argument_list|,
name|printSuffix
condition|?
name|inode
operator|.
name|getName
argument_list|()
operator|.
name|toStringUtf8
argument_list|()
else|:
literal|""
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"permission"
argument_list|,
name|toString
argument_list|(
name|p
operator|.
name|getPermission
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"replication"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"type"
argument_list|,
name|inode
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"fileId"
argument_list|,
name|inode
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"childrenNum"
argument_list|,
name|dirmap
operator|.
name|get
argument_list|(
name|inode
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|map
return|;
block|}
case|case
name|SYMLINK
case|:
block|{
name|FsImageProto
operator|.
name|INodeSection
operator|.
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
name|map
operator|.
name|put
argument_list|(
literal|"accessTime"
argument_list|,
name|d
operator|.
name|getAccessTime
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"blockSize"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"group"
argument_list|,
name|p
operator|.
name|getGroupName
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"length"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"modificationTime"
argument_list|,
name|d
operator|.
name|getModificationTime
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"owner"
argument_list|,
name|p
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"pathSuffix"
argument_list|,
name|printSuffix
condition|?
name|inode
operator|.
name|getName
argument_list|()
operator|.
name|toStringUtf8
argument_list|()
else|:
literal|""
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"permission"
argument_list|,
name|toString
argument_list|(
name|p
operator|.
name|getPermission
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"replication"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"type"
argument_list|,
name|inode
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"symlink"
argument_list|,
name|d
operator|.
name|getTarget
argument_list|()
operator|.
name|toStringUtf8
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"fileId"
argument_list|,
name|inode
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"childrenNum"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|map
return|;
block|}
default|default:
return|return
literal|null
return|;
block|}
block|}
DECL|method|getFileSize (FsImageProto.INodeSection.INodeFile f)
specifier|private
name|long
name|getFileSize
parameter_list|(
name|FsImageProto
operator|.
name|INodeSection
operator|.
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
name|HdfsProtos
operator|.
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
DECL|method|toString (FsPermission permission)
specifier|private
name|String
name|toString
parameter_list|(
name|FsPermission
name|permission
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%o"
argument_list|,
name|permission
operator|.
name|toShort
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

