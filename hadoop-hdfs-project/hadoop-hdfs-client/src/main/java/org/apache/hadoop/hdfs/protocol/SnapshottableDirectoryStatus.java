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
name|Comparator
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
name|hdfs
operator|.
name|DFSUtilClient
import|;
end_import

begin_comment
comment|/**  * Metadata about a snapshottable directory  */
end_comment

begin_class
DECL|class|SnapshottableDirectoryStatus
specifier|public
class|class
name|SnapshottableDirectoryStatus
block|{
comment|/** Compare the statuses by full paths. */
DECL|field|COMPARATOR
specifier|public
specifier|static
specifier|final
name|Comparator
argument_list|<
name|SnapshottableDirectoryStatus
argument_list|>
name|COMPARATOR
init|=
operator|new
name|Comparator
argument_list|<
name|SnapshottableDirectoryStatus
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|SnapshottableDirectoryStatus
name|left
parameter_list|,
name|SnapshottableDirectoryStatus
name|right
parameter_list|)
block|{
name|int
name|d
init|=
name|DFSUtilClient
operator|.
name|compareBytes
argument_list|(
name|left
operator|.
name|parentFullPath
argument_list|,
name|right
operator|.
name|parentFullPath
argument_list|)
decl_stmt|;
return|return
name|d
operator|!=
literal|0
condition|?
name|d
else|:
name|DFSUtilClient
operator|.
name|compareBytes
argument_list|(
name|left
operator|.
name|dirStatus
operator|.
name|getLocalNameInBytes
argument_list|()
argument_list|,
name|right
operator|.
name|dirStatus
operator|.
name|getLocalNameInBytes
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/** Basic information of the snapshottable directory */
DECL|field|dirStatus
specifier|private
specifier|final
name|HdfsFileStatus
name|dirStatus
decl_stmt|;
comment|/** Number of snapshots that have been taken*/
DECL|field|snapshotNumber
specifier|private
specifier|final
name|int
name|snapshotNumber
decl_stmt|;
comment|/** Number of snapshots allowed. */
DECL|field|snapshotQuota
specifier|private
specifier|final
name|int
name|snapshotQuota
decl_stmt|;
comment|/** Full path of the parent. */
DECL|field|parentFullPath
specifier|private
specifier|final
name|byte
index|[]
name|parentFullPath
decl_stmt|;
DECL|method|SnapshottableDirectoryStatus (long modification_time, long access_time, FsPermission permission, String owner, String group, byte[] localName, long inodeId, int childrenNum, int snapshotNumber, int snapshotQuota, byte[] parentFullPath)
specifier|public
name|SnapshottableDirectoryStatus
parameter_list|(
name|long
name|modification_time
parameter_list|,
name|long
name|access_time
parameter_list|,
name|FsPermission
name|permission
parameter_list|,
name|String
name|owner
parameter_list|,
name|String
name|group
parameter_list|,
name|byte
index|[]
name|localName
parameter_list|,
name|long
name|inodeId
parameter_list|,
name|int
name|childrenNum
parameter_list|,
name|int
name|snapshotNumber
parameter_list|,
name|int
name|snapshotQuota
parameter_list|,
name|byte
index|[]
name|parentFullPath
parameter_list|)
block|{
name|this
operator|.
name|dirStatus
operator|=
operator|new
name|HdfsFileStatus
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|modification_time
argument_list|,
name|access_time
argument_list|,
name|permission
argument_list|,
name|owner
argument_list|,
name|group
argument_list|,
literal|null
argument_list|,
name|localName
argument_list|,
name|inodeId
argument_list|,
name|childrenNum
argument_list|,
literal|null
argument_list|,
name|HdfsConstants
operator|.
name|BLOCK_STORAGE_POLICY_ID_UNSPECIFIED
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|snapshotNumber
operator|=
name|snapshotNumber
expr_stmt|;
name|this
operator|.
name|snapshotQuota
operator|=
name|snapshotQuota
expr_stmt|;
name|this
operator|.
name|parentFullPath
operator|=
name|parentFullPath
expr_stmt|;
block|}
comment|/**    * @return Number of snapshots that have been taken for the directory    */
DECL|method|getSnapshotNumber ()
specifier|public
name|int
name|getSnapshotNumber
parameter_list|()
block|{
return|return
name|snapshotNumber
return|;
block|}
comment|/**    * @return Number of snapshots allowed for the directory    */
DECL|method|getSnapshotQuota ()
specifier|public
name|int
name|getSnapshotQuota
parameter_list|()
block|{
return|return
name|snapshotQuota
return|;
block|}
comment|/**    * @return Full path of the parent    */
DECL|method|getParentFullPath ()
specifier|public
name|byte
index|[]
name|getParentFullPath
parameter_list|()
block|{
return|return
name|parentFullPath
return|;
block|}
comment|/**    * @return The basic information of the directory    */
DECL|method|getDirStatus ()
specifier|public
name|HdfsFileStatus
name|getDirStatus
parameter_list|()
block|{
return|return
name|dirStatus
return|;
block|}
comment|/**    * @return Full path of the file    */
DECL|method|getFullPath ()
specifier|public
name|Path
name|getFullPath
parameter_list|()
block|{
name|String
name|parentFullPathStr
init|=
operator|(
name|parentFullPath
operator|==
literal|null
operator|||
name|parentFullPath
operator|.
name|length
operator|==
literal|0
operator|)
condition|?
literal|null
else|:
name|DFSUtilClient
operator|.
name|bytes2String
argument_list|(
name|parentFullPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentFullPathStr
operator|==
literal|null
operator|&&
name|dirStatus
operator|.
name|getLocalNameInBytes
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// root
return|return
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|parentFullPathStr
operator|==
literal|null
condition|?
operator|new
name|Path
argument_list|(
name|dirStatus
operator|.
name|getLocalName
argument_list|()
argument_list|)
else|:
operator|new
name|Path
argument_list|(
name|parentFullPathStr
argument_list|,
name|dirStatus
operator|.
name|getLocalName
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * Print a list of {@link SnapshottableDirectoryStatus} out to a given stream.    * @param stats The list of {@link SnapshottableDirectoryStatus}    * @param out The given stream for printing.    */
DECL|method|print (SnapshottableDirectoryStatus[] stats, PrintStream out)
specifier|public
specifier|static
name|void
name|print
parameter_list|(
name|SnapshottableDirectoryStatus
index|[]
name|stats
parameter_list|,
name|PrintStream
name|out
parameter_list|)
block|{
if|if
condition|(
name|stats
operator|==
literal|null
operator|||
name|stats
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
return|return;
block|}
name|int
name|maxRepl
init|=
literal|0
decl_stmt|,
name|maxLen
init|=
literal|0
decl_stmt|,
name|maxOwner
init|=
literal|0
decl_stmt|,
name|maxGroup
init|=
literal|0
decl_stmt|;
name|int
name|maxSnapshotNum
init|=
literal|0
decl_stmt|,
name|maxSnapshotQuota
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SnapshottableDirectoryStatus
name|status
range|:
name|stats
control|)
block|{
name|maxRepl
operator|=
name|maxLength
argument_list|(
name|maxRepl
argument_list|,
name|status
operator|.
name|dirStatus
operator|.
name|getReplication
argument_list|()
argument_list|)
expr_stmt|;
name|maxLen
operator|=
name|maxLength
argument_list|(
name|maxLen
argument_list|,
name|status
operator|.
name|dirStatus
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|maxOwner
operator|=
name|maxLength
argument_list|(
name|maxOwner
argument_list|,
name|status
operator|.
name|dirStatus
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|maxGroup
operator|=
name|maxLength
argument_list|(
name|maxGroup
argument_list|,
name|status
operator|.
name|dirStatus
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
name|maxSnapshotNum
operator|=
name|maxLength
argument_list|(
name|maxSnapshotNum
argument_list|,
name|status
operator|.
name|snapshotNumber
argument_list|)
expr_stmt|;
name|maxSnapshotQuota
operator|=
name|maxLength
argument_list|(
name|maxSnapshotQuota
argument_list|,
name|status
operator|.
name|snapshotQuota
argument_list|)
expr_stmt|;
block|}
name|StringBuilder
name|fmt
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|fmt
operator|.
name|append
argument_list|(
literal|"%s%s "
argument_list|)
expr_stmt|;
comment|// permission string
name|fmt
operator|.
name|append
argument_list|(
literal|"%"
operator|+
name|maxRepl
operator|+
literal|"s "
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|append
argument_list|(
operator|(
name|maxOwner
operator|>
literal|0
operator|)
condition|?
literal|"%-"
operator|+
name|maxOwner
operator|+
literal|"s "
else|:
literal|"%s"
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|append
argument_list|(
operator|(
name|maxGroup
operator|>
literal|0
operator|)
condition|?
literal|"%-"
operator|+
name|maxGroup
operator|+
literal|"s "
else|:
literal|"%s"
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|append
argument_list|(
literal|"%"
operator|+
name|maxLen
operator|+
literal|"s "
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|append
argument_list|(
literal|"%s "
argument_list|)
expr_stmt|;
comment|// mod time
name|fmt
operator|.
name|append
argument_list|(
literal|"%"
operator|+
name|maxSnapshotNum
operator|+
literal|"s "
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|append
argument_list|(
literal|"%"
operator|+
name|maxSnapshotQuota
operator|+
literal|"s "
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|append
argument_list|(
literal|"%s"
argument_list|)
expr_stmt|;
comment|// path
name|String
name|lineFormat
init|=
name|fmt
operator|.
name|toString
argument_list|()
decl_stmt|;
name|SimpleDateFormat
name|dateFormat
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd HH:mm"
argument_list|)
decl_stmt|;
for|for
control|(
name|SnapshottableDirectoryStatus
name|status
range|:
name|stats
control|)
block|{
name|String
name|line
init|=
name|String
operator|.
name|format
argument_list|(
name|lineFormat
argument_list|,
literal|"d"
argument_list|,
name|status
operator|.
name|dirStatus
operator|.
name|getPermission
argument_list|()
argument_list|,
name|status
operator|.
name|dirStatus
operator|.
name|getReplication
argument_list|()
argument_list|,
name|status
operator|.
name|dirStatus
operator|.
name|getOwner
argument_list|()
argument_list|,
name|status
operator|.
name|dirStatus
operator|.
name|getGroup
argument_list|()
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|status
operator|.
name|dirStatus
operator|.
name|getLen
argument_list|()
argument_list|)
argument_list|,
name|dateFormat
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|status
operator|.
name|dirStatus
operator|.
name|getModificationTime
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|status
operator|.
name|snapshotNumber
argument_list|,
name|status
operator|.
name|snapshotQuota
argument_list|,
name|status
operator|.
name|getFullPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|maxLength (int n, Object value)
specifier|private
specifier|static
name|int
name|maxLength
parameter_list|(
name|int
name|n
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
return|return
name|Math
operator|.
name|max
argument_list|(
name|n
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
DECL|class|Bean
specifier|public
specifier|static
class|class
name|Bean
block|{
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
DECL|field|snapshotNumber
specifier|private
specifier|final
name|int
name|snapshotNumber
decl_stmt|;
DECL|field|snapshotQuota
specifier|private
specifier|final
name|int
name|snapshotQuota
decl_stmt|;
DECL|field|modificationTime
specifier|private
specifier|final
name|long
name|modificationTime
decl_stmt|;
DECL|field|permission
specifier|private
specifier|final
name|short
name|permission
decl_stmt|;
DECL|field|owner
specifier|private
specifier|final
name|String
name|owner
decl_stmt|;
DECL|field|group
specifier|private
specifier|final
name|String
name|group
decl_stmt|;
DECL|method|Bean (String path, int snapshotNumber, int snapshotQuota, long modificationTime, short permission, String owner, String group)
specifier|public
name|Bean
parameter_list|(
name|String
name|path
parameter_list|,
name|int
name|snapshotNumber
parameter_list|,
name|int
name|snapshotQuota
parameter_list|,
name|long
name|modificationTime
parameter_list|,
name|short
name|permission
parameter_list|,
name|String
name|owner
parameter_list|,
name|String
name|group
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|snapshotNumber
operator|=
name|snapshotNumber
expr_stmt|;
name|this
operator|.
name|snapshotQuota
operator|=
name|snapshotQuota
expr_stmt|;
name|this
operator|.
name|modificationTime
operator|=
name|modificationTime
expr_stmt|;
name|this
operator|.
name|permission
operator|=
name|permission
expr_stmt|;
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
name|this
operator|.
name|group
operator|=
name|group
expr_stmt|;
block|}
DECL|method|getPath ()
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
DECL|method|getSnapshotNumber ()
specifier|public
name|int
name|getSnapshotNumber
parameter_list|()
block|{
return|return
name|snapshotNumber
return|;
block|}
DECL|method|getSnapshotQuota ()
specifier|public
name|int
name|getSnapshotQuota
parameter_list|()
block|{
return|return
name|snapshotQuota
return|;
block|}
DECL|method|getModificationTime ()
specifier|public
name|long
name|getModificationTime
parameter_list|()
block|{
return|return
name|modificationTime
return|;
block|}
DECL|method|getPermission ()
specifier|public
name|short
name|getPermission
parameter_list|()
block|{
return|return
name|permission
return|;
block|}
DECL|method|getOwner ()
specifier|public
name|String
name|getOwner
parameter_list|()
block|{
return|return
name|owner
return|;
block|}
DECL|method|getGroup ()
specifier|public
name|String
name|getGroup
parameter_list|()
block|{
return|return
name|group
return|;
block|}
block|}
block|}
end_class

end_unit

