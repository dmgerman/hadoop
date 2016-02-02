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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
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
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_comment
comment|/**  * A PBImageDelimitedTextWriter generates a text representation of the PB fsimage,  * with each element separated by a delimiter string.  All of the elements  * common to both inodes and inodes-under-construction are included. When  * processing an fsimage with a layout version that did not include an  * element, such as AccessTime, the output file will include a column  * for the value, but no value will be included.  *  * Individual block information for each file is not currently included.  *  * The default delimiter is tab, as this is an unlikely value to be included in  * an inode path or other text metadata. The delimiter value can be via the  * constructor.  */
end_comment

begin_class
DECL|class|PBImageDelimitedTextWriter
specifier|public
class|class
name|PBImageDelimitedTextWriter
extends|extends
name|PBImageTextWriter
block|{
DECL|field|DEFAULT_DELIMITER
specifier|static
specifier|final
name|String
name|DEFAULT_DELIMITER
init|=
literal|"\t"
decl_stmt|;
DECL|field|DATE_FORMAT
specifier|private
specifier|static
specifier|final
name|String
name|DATE_FORMAT
init|=
literal|"yyyy-MM-dd HH:mm"
decl_stmt|;
DECL|field|dateFormatter
specifier|private
specifier|final
name|SimpleDateFormat
name|dateFormatter
init|=
operator|new
name|SimpleDateFormat
argument_list|(
name|DATE_FORMAT
argument_list|)
decl_stmt|;
DECL|field|delimiter
specifier|private
specifier|final
name|String
name|delimiter
decl_stmt|;
DECL|method|PBImageDelimitedTextWriter (PrintStream out, String delimiter, String tempPath)
name|PBImageDelimitedTextWriter
parameter_list|(
name|PrintStream
name|out
parameter_list|,
name|String
name|delimiter
parameter_list|,
name|String
name|tempPath
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|out
argument_list|,
name|tempPath
argument_list|)
expr_stmt|;
name|this
operator|.
name|delimiter
operator|=
name|delimiter
expr_stmt|;
block|}
DECL|method|formatDate (long date)
specifier|private
name|String
name|formatDate
parameter_list|(
name|long
name|date
parameter_list|)
block|{
return|return
name|dateFormatter
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|date
argument_list|)
argument_list|)
return|;
block|}
DECL|method|append (StringBuffer buffer, int field)
specifier|private
name|void
name|append
parameter_list|(
name|StringBuffer
name|buffer
parameter_list|,
name|int
name|field
parameter_list|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|delimiter
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
DECL|method|append (StringBuffer buffer, long field)
specifier|private
name|void
name|append
parameter_list|(
name|StringBuffer
name|buffer
parameter_list|,
name|long
name|field
parameter_list|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|delimiter
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
DECL|method|append (StringBuffer buffer, String field)
specifier|private
name|void
name|append
parameter_list|(
name|StringBuffer
name|buffer
parameter_list|,
name|String
name|field
parameter_list|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|delimiter
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getEntry (String parent, INode inode)
specifier|public
name|String
name|getEntry
parameter_list|(
name|String
name|parent
parameter_list|,
name|INode
name|inode
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|String
name|inodeName
init|=
name|inode
operator|.
name|getName
argument_list|()
operator|.
name|toStringUtf8
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
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
name|inodeName
operator|.
name|isEmpty
argument_list|()
condition|?
literal|"/"
else|:
name|inodeName
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|PermissionStatus
name|p
init|=
literal|null
decl_stmt|;
name|boolean
name|isDir
init|=
literal|false
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
name|INodeFile
name|file
init|=
name|inode
operator|.
name|getFile
argument_list|()
decl_stmt|;
name|p
operator|=
name|getPermission
argument_list|(
name|file
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
name|file
operator|.
name|getReplication
argument_list|()
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
name|formatDate
argument_list|(
name|file
operator|.
name|getModificationTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
name|formatDate
argument_list|(
name|file
operator|.
name|getAccessTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
name|file
operator|.
name|getPreferredBlockSize
argument_list|()
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
name|file
operator|.
name|getBlocksCount
argument_list|()
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
name|FSImageLoader
operator|.
name|getFileSize
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// NS_QUOTA
name|append
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// DS_QUOTA
break|break;
case|case
name|DIRECTORY
case|:
name|INodeDirectory
name|dir
init|=
name|inode
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
name|p
operator|=
name|getPermission
argument_list|(
name|dir
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Replication
name|append
argument_list|(
name|buffer
argument_list|,
name|formatDate
argument_list|(
name|dir
operator|.
name|getModificationTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
name|formatDate
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Access time.
name|append
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Block size.
name|append
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Num blocks.
name|append
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Num bytes.
name|append
argument_list|(
name|buffer
argument_list|,
name|dir
operator|.
name|getNsQuota
argument_list|()
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
name|dir
operator|.
name|getDsQuota
argument_list|()
argument_list|)
expr_stmt|;
name|isDir
operator|=
literal|true
expr_stmt|;
break|break;
case|case
name|SYMLINK
case|:
name|INodeSymlink
name|s
init|=
name|inode
operator|.
name|getSymlink
argument_list|()
decl_stmt|;
name|p
operator|=
name|getPermission
argument_list|(
name|s
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Replication
name|append
argument_list|(
name|buffer
argument_list|,
name|formatDate
argument_list|(
name|s
operator|.
name|getModificationTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
name|formatDate
argument_list|(
name|s
operator|.
name|getAccessTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Block size.
name|append
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Num blocks.
name|append
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Num bytes.
name|append
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// NS_QUOTA
name|append
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// DS_QUOTA
break|break;
default|default:
break|break;
block|}
assert|assert
name|p
operator|!=
literal|null
assert|;
name|String
name|dirString
init|=
name|isDir
condition|?
literal|"d"
else|:
literal|"-"
decl_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
name|dirString
operator|+
name|p
operator|.
name|getPermission
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
name|p
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
name|p
operator|.
name|getGroupName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getHeader ()
specifier|public
name|String
name|getHeader
parameter_list|()
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"Path"
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
literal|"Replication"
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
literal|"ModificationTime"
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
literal|"AccessTime"
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
literal|"PreferredBlockSize"
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
literal|"BlocksCount"
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
literal|"FileSize"
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
literal|"NSQUOTA"
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
literal|"DSQUOTA"
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
literal|"Permission"
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
literal|"UserName"
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buffer
argument_list|,
literal|"GroupName"
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

