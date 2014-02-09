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
name|hdfs
operator|.
name|protocol
operator|.
name|proto
operator|.
name|ClientNamenodeProtocolProtos
operator|.
name|CacheDirectiveInfoExpirationProto
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
name|ClientNamenodeProtocolProtos
operator|.
name|CacheDirectiveInfoProto
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
name|ClientNamenodeProtocolProtos
operator|.
name|CachePoolInfoProto
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
name|CacheManagerSection
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
name|FilesUnderConstructionSection
operator|.
name|FileUnderConstructionEntry
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
name|NameSystemSection
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
name|SecretManagerSection
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
name|SnapshotDiffSection
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
name|SnapshotSection
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
name|io
operator|.
name|LimitInputStream
import|;
end_import

begin_comment
comment|/**  * This is the tool for analyzing file sizes in the namespace image. In order to  * run the tool one should define a range of integers<tt>[0, maxSize]</tt> by  * specifying<tt>maxSize</tt> and a<tt>step</tt>. The range of integers is  * divided into segments of size<tt>step</tt>:  *<tt>[0, s<sub>1</sub>, ..., s<sub>n-1</sub>, maxSize]</tt>, and the visitor  * calculates how many files in the system fall into each segment  *<tt>[s<sub>i-1</sub>, s<sub>i</sub>)</tt>. Note that files larger than  *<tt>maxSize</tt> always fall into the very last segment.  *  *<h3>Input.</h3>  *<ul>  *<li><tt>filename</tt> specifies the location of the image file;</li>  *<li><tt>maxSize</tt> determines the range<tt>[0, maxSize]</tt> of files  * sizes considered by the visitor;</li>  *<li><tt>step</tt> the range is divided into segments of size step.</li>  *</ul>  *  *<h3>Output.</h3> The output file is formatted as a tab separated two column  * table: Size and NumFiles. Where Size represents the start of the segment, and  * numFiles is the number of files form the image which size falls in this  * segment.  *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|PBImageXmlWriter
specifier|public
specifier|final
class|class
name|PBImageXmlWriter
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
DECL|method|PBImageXmlWriter (Configuration conf, PrintWriter out)
specifier|public
name|PBImageXmlWriter
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
name|out
operator|.
name|print
argument_list|(
literal|"<?xml version=\"1.0\"?>\n"
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
name|NS_INFO
case|:
name|dumpNameSection
argument_list|(
name|is
argument_list|)
expr_stmt|;
break|break;
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
name|dumpINodeSection
argument_list|(
name|is
argument_list|)
expr_stmt|;
break|break;
case|case
name|INODE_DIR
case|:
name|dumpINodeDirectorySection
argument_list|(
name|is
argument_list|)
expr_stmt|;
break|break;
case|case
name|FILES_UNDERCONSTRUCTION
case|:
name|dumpFileUnderConstructionSection
argument_list|(
name|is
argument_list|)
expr_stmt|;
break|break;
case|case
name|SNAPSHOT
case|:
name|dumpSnapshotSection
argument_list|(
name|is
argument_list|)
expr_stmt|;
break|break;
case|case
name|SNAPSHOT_DIFF
case|:
name|dumpSnapshotDiffSection
argument_list|(
name|is
argument_list|)
expr_stmt|;
break|break;
case|case
name|SECRET_MANAGER
case|:
name|dumpSecretManagerSection
argument_list|(
name|is
argument_list|)
expr_stmt|;
break|break;
case|case
name|CACHE_MANAGER
case|:
name|dumpCacheManagerSection
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
block|}
DECL|method|dumpCacheManagerSection (InputStream is)
specifier|private
name|void
name|dumpCacheManagerSection
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|print
argument_list|(
literal|"<CacheManagerSection>"
argument_list|)
expr_stmt|;
name|CacheManagerSection
name|s
init|=
name|CacheManagerSection
operator|.
name|parseDelimitedFrom
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|o
argument_list|(
literal|"nextDirectiveId"
argument_list|,
name|s
operator|.
name|getNextDirectiveId
argument_list|()
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
name|s
operator|.
name|getNumPools
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|CachePoolInfoProto
name|p
init|=
name|CachePoolInfoProto
operator|.
name|parseDelimitedFrom
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<pool>"
argument_list|)
expr_stmt|;
name|o
argument_list|(
literal|"poolName"
argument_list|,
name|p
operator|.
name|getPoolName
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"ownerName"
argument_list|,
name|p
operator|.
name|getOwnerName
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"groupName"
argument_list|,
name|p
operator|.
name|getGroupName
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"mode"
argument_list|,
name|p
operator|.
name|getMode
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"limit"
argument_list|,
name|p
operator|.
name|getLimit
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"maxRelativeExpiry"
argument_list|,
name|p
operator|.
name|getMaxRelativeExpiry
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"</pool>\n"
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
name|getNumPools
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|CacheDirectiveInfoProto
name|p
init|=
name|CacheDirectiveInfoProto
operator|.
name|parseDelimitedFrom
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<directive>"
argument_list|)
expr_stmt|;
name|o
argument_list|(
literal|"id"
argument_list|,
name|p
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"path"
argument_list|,
name|p
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"replication"
argument_list|,
name|p
operator|.
name|getReplication
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"pool"
argument_list|,
name|p
operator|.
name|getPool
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<expiration>"
argument_list|)
expr_stmt|;
name|CacheDirectiveInfoExpirationProto
name|e
init|=
name|p
operator|.
name|getExpiration
argument_list|()
decl_stmt|;
name|o
argument_list|(
literal|"millis"
argument_list|,
name|e
operator|.
name|getMillis
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"relatilve"
argument_list|,
name|e
operator|.
name|getIsRelative
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"</expiration>\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"</directive>\n"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
literal|"</CacheManagerSection>\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|dumpFileUnderConstructionSection (InputStream in)
specifier|private
name|void
name|dumpFileUnderConstructionSection
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|print
argument_list|(
literal|"<FileUnderConstructionSection>"
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|FileUnderConstructionEntry
name|e
init|=
name|FileUnderConstructionEntry
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
name|out
operator|.
name|print
argument_list|(
literal|"<inode>"
argument_list|)
expr_stmt|;
name|o
argument_list|(
literal|"id"
argument_list|,
name|e
operator|.
name|getInodeId
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"path"
argument_list|,
name|e
operator|.
name|getFullPath
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"</inode>\n"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
literal|"</FileUnderConstructionSection>\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|dumpINodeDirectory (INodeDirectory d)
specifier|private
name|void
name|dumpINodeDirectory
parameter_list|(
name|INodeDirectory
name|d
parameter_list|)
block|{
name|o
argument_list|(
literal|"mtime"
argument_list|,
name|d
operator|.
name|getModificationTime
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"permission"
argument_list|,
name|dumpPermission
argument_list|(
name|d
operator|.
name|getPermission
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|d
operator|.
name|hasDsQuota
argument_list|()
operator|&&
name|d
operator|.
name|hasNsQuota
argument_list|()
condition|)
block|{
name|o
argument_list|(
literal|"nsquota"
argument_list|,
name|d
operator|.
name|getNsQuota
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"dsquota"
argument_list|,
name|d
operator|.
name|getDsQuota
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|dumpINodeDirectorySection (InputStream in)
specifier|private
name|void
name|dumpINodeDirectorySection
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|print
argument_list|(
literal|"<INodeDirectorySection>"
argument_list|)
expr_stmt|;
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
name|out
operator|.
name|print
argument_list|(
literal|"<directory>"
argument_list|)
expr_stmt|;
name|o
argument_list|(
literal|"parent"
argument_list|,
name|e
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|long
name|id
range|:
name|e
operator|.
name|getChildrenList
argument_list|()
control|)
block|{
name|o
argument_list|(
literal|"inode"
argument_list|,
name|id
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
name|r
init|=
name|INodeSection
operator|.
name|INodeReference
operator|.
name|parseDelimitedFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|dumpINodeReference
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
literal|"</directory>\n"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
literal|"</INodeDirectorySection>\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|dumpINodeReference (INodeSection.INodeReference r)
specifier|private
name|void
name|dumpINodeReference
parameter_list|(
name|INodeSection
operator|.
name|INodeReference
name|r
parameter_list|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"<ref>"
argument_list|)
expr_stmt|;
name|o
argument_list|(
literal|"referredId"
argument_list|,
name|r
operator|.
name|getReferredId
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"name"
argument_list|,
name|r
operator|.
name|getName
argument_list|()
operator|.
name|toStringUtf8
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"dstSnapshotId"
argument_list|,
name|r
operator|.
name|getDstSnapshotId
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"lastSnapshotId"
argument_list|,
name|r
operator|.
name|getLastSnapshotId
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"</ref>\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|dumpINodeFile (INodeSection.INodeFile f)
specifier|private
name|void
name|dumpINodeFile
parameter_list|(
name|INodeSection
operator|.
name|INodeFile
name|f
parameter_list|)
block|{
name|o
argument_list|(
literal|"replication"
argument_list|,
name|f
operator|.
name|getReplication
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"mtime"
argument_list|,
name|f
operator|.
name|getModificationTime
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"atime"
argument_list|,
name|f
operator|.
name|getAccessTime
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"perferredBlockSize"
argument_list|,
name|f
operator|.
name|getPreferredBlockSize
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"permission"
argument_list|,
name|dumpPermission
argument_list|(
name|f
operator|.
name|getPermission
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|getBlocksCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"<blocks>"
argument_list|)
expr_stmt|;
for|for
control|(
name|BlockProto
name|b
range|:
name|f
operator|.
name|getBlocksList
argument_list|()
control|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"<block>"
argument_list|)
expr_stmt|;
name|o
argument_list|(
literal|"id"
argument_list|,
name|b
operator|.
name|getBlockId
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"genstamp"
argument_list|,
name|b
operator|.
name|getGenStamp
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"numBytes"
argument_list|,
name|b
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"</block>\n"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
literal|"</blocks>\n"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|f
operator|.
name|hasFileUC
argument_list|()
condition|)
block|{
name|INodeSection
operator|.
name|FileUnderConstructionFeature
name|u
init|=
name|f
operator|.
name|getFileUC
argument_list|()
decl_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<file-under-construction>"
argument_list|)
expr_stmt|;
name|o
argument_list|(
literal|"clientName"
argument_list|,
name|u
operator|.
name|getClientName
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"clientMachine"
argument_list|,
name|u
operator|.
name|getClientMachine
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"</file-under-construction>\n"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|dumpINodeSection (InputStream in)
specifier|private
name|void
name|dumpINodeSection
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
name|out
operator|.
name|print
argument_list|(
literal|"<INodeSection>"
argument_list|)
expr_stmt|;
name|o
argument_list|(
literal|"lastInodeId"
argument_list|,
name|s
operator|.
name|getLastInodeId
argument_list|()
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
name|out
operator|.
name|print
argument_list|(
literal|"<inode>"
argument_list|)
expr_stmt|;
name|o
argument_list|(
literal|"id"
argument_list|,
name|p
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"type"
argument_list|,
name|p
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"name"
argument_list|,
name|p
operator|.
name|getName
argument_list|()
operator|.
name|toStringUtf8
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|.
name|hasFile
argument_list|()
condition|)
block|{
name|dumpINodeFile
argument_list|(
name|p
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|p
operator|.
name|hasDirectory
argument_list|()
condition|)
block|{
name|dumpINodeDirectory
argument_list|(
name|p
operator|.
name|getDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|p
operator|.
name|hasSymlink
argument_list|()
condition|)
block|{
name|dumpINodeSymlink
argument_list|(
name|p
operator|.
name|getSymlink
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
literal|"</inode>\n"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
literal|"</INodeSection>\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|dumpINodeSymlink (INodeSymlink s)
specifier|private
name|void
name|dumpINodeSymlink
parameter_list|(
name|INodeSymlink
name|s
parameter_list|)
block|{
name|o
argument_list|(
literal|"permission"
argument_list|,
name|dumpPermission
argument_list|(
name|s
operator|.
name|getPermission
argument_list|()
argument_list|)
argument_list|)
operator|.
name|o
argument_list|(
literal|"target"
argument_list|,
name|s
operator|.
name|getTarget
argument_list|()
operator|.
name|toStringUtf8
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|dumpNameSection (InputStream in)
specifier|private
name|void
name|dumpNameSection
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|NameSystemSection
name|s
init|=
name|NameSystemSection
operator|.
name|parseDelimitedFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<NameSection>\n"
argument_list|)
expr_stmt|;
name|o
argument_list|(
literal|"genstampV1"
argument_list|,
name|s
operator|.
name|getGenstampV1
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"genstampV2"
argument_list|,
name|s
operator|.
name|getGenstampV2
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"genstampV1Limit"
argument_list|,
name|s
operator|.
name|getGenstampV1Limit
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"lastAllocatedBlockId"
argument_list|,
name|s
operator|.
name|getLastAllocatedBlockId
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"txid"
argument_list|,
name|s
operator|.
name|getTransactionId
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<NameSection>\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|dumpPermission (long permission)
specifier|private
name|String
name|dumpPermission
parameter_list|(
name|long
name|permission
parameter_list|)
block|{
return|return
name|FSImageFormatPBINode
operator|.
name|Loader
operator|.
name|loadPermission
argument_list|(
name|permission
argument_list|,
name|stringTable
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|dumpSecretManagerSection (InputStream is)
specifier|private
name|void
name|dumpSecretManagerSection
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|print
argument_list|(
literal|"<SecretManagerSection>"
argument_list|)
expr_stmt|;
name|SecretManagerSection
name|s
init|=
name|SecretManagerSection
operator|.
name|parseDelimitedFrom
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|o
argument_list|(
literal|"currentId"
argument_list|,
name|s
operator|.
name|getCurrentId
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"tokenSequenceNumber"
argument_list|,
name|s
operator|.
name|getTokenSequenceNumber
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"</SecretManagerSection>"
argument_list|)
expr_stmt|;
block|}
DECL|method|dumpSnapshotDiffSection (InputStream in)
specifier|private
name|void
name|dumpSnapshotDiffSection
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|print
argument_list|(
literal|"<SnapshotDiffSection>"
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|SnapshotDiffSection
operator|.
name|DiffEntry
name|e
init|=
name|SnapshotDiffSection
operator|.
name|DiffEntry
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
name|out
operator|.
name|print
argument_list|(
literal|"<diff>"
argument_list|)
expr_stmt|;
name|o
argument_list|(
literal|"inodeid"
argument_list|,
name|e
operator|.
name|getInodeId
argument_list|()
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|e
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|FILEDIFF
case|:
block|{
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
name|getNumOfDiff
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"<filediff>"
argument_list|)
expr_stmt|;
name|SnapshotDiffSection
operator|.
name|FileDiff
name|f
init|=
name|SnapshotDiffSection
operator|.
name|FileDiff
operator|.
name|parseDelimitedFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|o
argument_list|(
literal|"snapshotId"
argument_list|,
name|f
operator|.
name|getSnapshotId
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"size"
argument_list|,
name|f
operator|.
name|getFileSize
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"name"
argument_list|,
name|f
operator|.
name|getName
argument_list|()
operator|.
name|toStringUtf8
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"</filediff>\n"
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
name|DIRECTORYDIFF
case|:
block|{
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
name|getNumOfDiff
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"<dirdiff>"
argument_list|)
expr_stmt|;
name|SnapshotDiffSection
operator|.
name|DirectoryDiff
name|d
init|=
name|SnapshotDiffSection
operator|.
name|DirectoryDiff
operator|.
name|parseDelimitedFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|o
argument_list|(
literal|"snapshotId"
argument_list|,
name|d
operator|.
name|getSnapshotId
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"isSnapshotroot"
argument_list|,
name|d
operator|.
name|getIsSnapshotRoot
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"childrenSize"
argument_list|,
name|d
operator|.
name|getChildrenSize
argument_list|()
argument_list|)
operator|.
name|o
argument_list|(
literal|"name"
argument_list|,
name|d
operator|.
name|getName
argument_list|()
operator|.
name|toStringUtf8
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|d
operator|.
name|getCreatedListSize
argument_list|()
condition|;
operator|++
name|j
control|)
block|{
name|SnapshotDiffSection
operator|.
name|CreatedListEntry
name|ce
init|=
name|SnapshotDiffSection
operator|.
name|CreatedListEntry
operator|.
name|parseDelimitedFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<created>"
argument_list|)
expr_stmt|;
name|o
argument_list|(
literal|"name"
argument_list|,
name|ce
operator|.
name|getName
argument_list|()
operator|.
name|toStringUtf8
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"</created>\n"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|d
operator|.
name|getNumOfDeletedRef
argument_list|()
condition|;
operator|++
name|j
control|)
block|{
name|INodeSection
operator|.
name|INodeReference
name|r
init|=
name|INodeSection
operator|.
name|INodeReference
operator|.
name|parseDelimitedFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|dumpINodeReference
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
literal|"</dirdiff>\n"
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
default|default:
break|break;
block|}
name|out
operator|.
name|print
argument_list|(
literal|"</diff>"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
literal|"<SnapshotDiffSection>\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|dumpSnapshotSection (InputStream in)
specifier|private
name|void
name|dumpSnapshotSection
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|print
argument_list|(
literal|"<SnapshotSection>"
argument_list|)
expr_stmt|;
name|SnapshotSection
name|s
init|=
name|SnapshotSection
operator|.
name|parseDelimitedFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|o
argument_list|(
literal|"snapshotCounter"
argument_list|,
name|s
operator|.
name|getSnapshotCounter
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|s
operator|.
name|getSnapshottableDirCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"<snapshottableDir>"
argument_list|)
expr_stmt|;
for|for
control|(
name|long
name|id
range|:
name|s
operator|.
name|getSnapshottableDirList
argument_list|()
control|)
block|{
name|o
argument_list|(
literal|"dir"
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
literal|"</snapshottableDir>\n"
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
name|getNumSnapshots
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|SnapshotSection
operator|.
name|Snapshot
name|pbs
init|=
name|SnapshotSection
operator|.
name|Snapshot
operator|.
name|parseDelimitedFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|o
argument_list|(
literal|"snapshot"
argument_list|,
name|pbs
operator|.
name|getSnapshotId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
literal|"</SnapshotSection>\n"
argument_list|)
expr_stmt|;
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
DECL|method|o (final String e, final Object v)
specifier|private
name|PBImageXmlWriter
name|o
parameter_list|(
specifier|final
name|String
name|e
parameter_list|,
specifier|final
name|Object
name|v
parameter_list|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"<"
operator|+
name|e
operator|+
literal|">"
operator|+
name|v
operator|+
literal|"</"
operator|+
name|e
operator|+
literal|">"
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

