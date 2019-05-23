begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.protocolPB
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|protocolPB
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
name|FileStatus
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
name|util
operator|.
name|StringInterner
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FSProtos
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Utility methods aiding conversion of fs data structures.  */
end_comment

begin_class
DECL|class|PBHelper
specifier|public
specifier|final
class|class
name|PBHelper
block|{
DECL|method|PBHelper ()
specifier|private
name|PBHelper
parameter_list|()
block|{
comment|// prevent construction
block|}
DECL|method|convert (FsPermissionProto proto)
specifier|public
specifier|static
name|FsPermission
name|convert
parameter_list|(
name|FsPermissionProto
name|proto
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
name|proto
operator|.
name|getPerm
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convert (FsPermission p)
specifier|public
specifier|static
name|FsPermissionProto
name|convert
parameter_list|(
name|FsPermission
name|p
parameter_list|)
throws|throws
name|IOException
block|{
name|FsPermissionProto
operator|.
name|Builder
name|bld
init|=
name|FsPermissionProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|bld
operator|.
name|setPerm
argument_list|(
name|p
operator|.
name|toShort
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|bld
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|convert (FileStatusProto proto)
specifier|public
specifier|static
name|FileStatus
name|convert
parameter_list|(
name|FileStatusProto
name|proto
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|path
decl_stmt|;
specifier|final
name|long
name|length
decl_stmt|;
specifier|final
name|boolean
name|isdir
decl_stmt|;
specifier|final
name|short
name|blockReplication
decl_stmt|;
specifier|final
name|long
name|blocksize
decl_stmt|;
specifier|final
name|long
name|mtime
decl_stmt|;
specifier|final
name|long
name|atime
decl_stmt|;
specifier|final
name|String
name|owner
decl_stmt|;
specifier|final
name|String
name|group
decl_stmt|;
specifier|final
name|FsPermission
name|permission
decl_stmt|;
specifier|final
name|Path
name|symlink
decl_stmt|;
switch|switch
condition|(
name|proto
operator|.
name|getFileType
argument_list|()
condition|)
block|{
case|case
name|FT_DIR
case|:
name|isdir
operator|=
literal|true
expr_stmt|;
name|symlink
operator|=
literal|null
expr_stmt|;
name|blocksize
operator|=
literal|0
expr_stmt|;
name|length
operator|=
literal|0
expr_stmt|;
name|blockReplication
operator|=
literal|0
expr_stmt|;
break|break;
case|case
name|FT_SYMLINK
case|:
name|isdir
operator|=
literal|false
expr_stmt|;
name|symlink
operator|=
operator|new
name|Path
argument_list|(
name|proto
operator|.
name|getSymlink
argument_list|()
argument_list|)
expr_stmt|;
name|blocksize
operator|=
literal|0
expr_stmt|;
name|length
operator|=
literal|0
expr_stmt|;
name|blockReplication
operator|=
literal|0
expr_stmt|;
break|break;
case|case
name|FT_FILE
case|:
name|isdir
operator|=
literal|false
expr_stmt|;
name|symlink
operator|=
literal|null
expr_stmt|;
name|blocksize
operator|=
name|proto
operator|.
name|getBlockSize
argument_list|()
expr_stmt|;
name|length
operator|=
name|proto
operator|.
name|getLength
argument_list|()
expr_stmt|;
name|int
name|brep
init|=
name|proto
operator|.
name|getBlockReplication
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|brep
operator|&
literal|0xffff0000
operator|)
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Block replication 0x%08x "
operator|+
literal|"doesn't fit in 16 bits."
argument_list|,
name|brep
argument_list|)
argument_list|)
throw|;
block|}
name|blockReplication
operator|=
operator|(
name|short
operator|)
name|brep
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unknown type: "
operator|+
name|proto
operator|.
name|getFileType
argument_list|()
argument_list|)
throw|;
block|}
name|path
operator|=
operator|new
name|Path
argument_list|(
name|proto
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|mtime
operator|=
name|proto
operator|.
name|getModificationTime
argument_list|()
expr_stmt|;
name|atime
operator|=
name|proto
operator|.
name|getAccessTime
argument_list|()
expr_stmt|;
name|permission
operator|=
name|convert
argument_list|(
name|proto
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
name|owner
operator|=
name|StringInterner
operator|.
name|weakIntern
argument_list|(
name|proto
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|group
operator|=
name|StringInterner
operator|.
name|weakIntern
argument_list|(
name|proto
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|flags
init|=
name|proto
operator|.
name|getFlags
argument_list|()
decl_stmt|;
name|FileStatus
name|fileStatus
init|=
operator|new
name|FileStatus
argument_list|(
name|length
argument_list|,
name|isdir
argument_list|,
name|blockReplication
argument_list|,
name|blocksize
argument_list|,
name|mtime
argument_list|,
name|atime
argument_list|,
name|permission
argument_list|,
name|owner
argument_list|,
name|group
argument_list|,
name|symlink
argument_list|,
name|path
argument_list|,
name|FileStatus
operator|.
name|attributes
argument_list|(
operator|(
name|flags
operator|&
name|FileStatusProto
operator|.
name|Flags
operator|.
name|HAS_ACL_VALUE
operator|)
operator|!=
literal|0
argument_list|,
operator|(
name|flags
operator|&
name|FileStatusProto
operator|.
name|Flags
operator|.
name|HAS_CRYPT_VALUE
operator|)
operator|!=
literal|0
argument_list|,
operator|(
name|flags
operator|&
name|FileStatusProto
operator|.
name|Flags
operator|.
name|HAS_EC_VALUE
operator|)
operator|!=
literal|0
argument_list|,
operator|(
name|flags
operator|&
name|FileStatusProto
operator|.
name|Flags
operator|.
name|SNAPSHOT_ENABLED_VALUE
operator|)
operator|!=
literal|0
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|fileStatus
return|;
block|}
DECL|method|convert (FileStatus stat)
specifier|public
specifier|static
name|FileStatusProto
name|convert
parameter_list|(
name|FileStatus
name|stat
parameter_list|)
throws|throws
name|IOException
block|{
name|FileStatusProto
operator|.
name|Builder
name|bld
init|=
name|FileStatusProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|bld
operator|.
name|setPath
argument_list|(
name|stat
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|stat
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|bld
operator|.
name|setFileType
argument_list|(
name|FileStatusProto
operator|.
name|FileType
operator|.
name|FT_DIR
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|stat
operator|.
name|isSymlink
argument_list|()
condition|)
block|{
name|bld
operator|.
name|setFileType
argument_list|(
name|FileStatusProto
operator|.
name|FileType
operator|.
name|FT_SYMLINK
argument_list|)
operator|.
name|setSymlink
argument_list|(
name|stat
operator|.
name|getSymlink
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|bld
operator|.
name|setFileType
argument_list|(
name|FileStatusProto
operator|.
name|FileType
operator|.
name|FT_FILE
argument_list|)
operator|.
name|setLength
argument_list|(
name|stat
operator|.
name|getLen
argument_list|()
argument_list|)
operator|.
name|setBlockReplication
argument_list|(
name|stat
operator|.
name|getReplication
argument_list|()
argument_list|)
operator|.
name|setBlockSize
argument_list|(
name|stat
operator|.
name|getBlockSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|bld
operator|.
name|setAccessTime
argument_list|(
name|stat
operator|.
name|getAccessTime
argument_list|()
argument_list|)
operator|.
name|setModificationTime
argument_list|(
name|stat
operator|.
name|getModificationTime
argument_list|()
argument_list|)
operator|.
name|setOwner
argument_list|(
name|stat
operator|.
name|getOwner
argument_list|()
argument_list|)
operator|.
name|setGroup
argument_list|(
name|stat
operator|.
name|getGroup
argument_list|()
argument_list|)
operator|.
name|setPermission
argument_list|(
name|convert
argument_list|(
name|stat
operator|.
name|getPermission
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|flags
init|=
literal|0
decl_stmt|;
name|flags
operator||=
name|stat
operator|.
name|hasAcl
argument_list|()
condition|?
name|FileStatusProto
operator|.
name|Flags
operator|.
name|HAS_ACL_VALUE
else|:
literal|0
expr_stmt|;
name|flags
operator||=
name|stat
operator|.
name|isEncrypted
argument_list|()
condition|?
name|FileStatusProto
operator|.
name|Flags
operator|.
name|HAS_CRYPT_VALUE
else|:
literal|0
expr_stmt|;
name|flags
operator||=
name|stat
operator|.
name|isErasureCoded
argument_list|()
condition|?
name|FileStatusProto
operator|.
name|Flags
operator|.
name|HAS_EC_VALUE
else|:
literal|0
expr_stmt|;
name|flags
operator||=
name|stat
operator|.
name|isSnapshotEnabled
argument_list|()
condition|?
name|FileStatusProto
operator|.
name|Flags
operator|.
name|SNAPSHOT_ENABLED_VALUE
else|:
literal|0
expr_stmt|;
name|bld
operator|.
name|setFlags
argument_list|(
name|flags
argument_list|)
expr_stmt|;
return|return
name|bld
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

