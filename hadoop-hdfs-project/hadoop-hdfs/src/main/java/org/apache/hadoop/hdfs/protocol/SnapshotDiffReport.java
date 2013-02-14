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
name|util
operator|.
name|Arrays
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
name|List
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
name|hdfs
operator|.
name|DFSUtil
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
name|snapshot
operator|.
name|INodeDirectorySnapshottable
operator|.
name|SnapshotDiffInfo
import|;
end_import

begin_comment
comment|/**  * This class represents to end users the difference between two snapshots of   * the same directory, or the difference between a snapshot of the directory and  * its current state. Instead of capturing all the details of the diff, which   * is stored in {@link SnapshotDiffInfo}, this class only lists where the   * changes happened and their types.  */
end_comment

begin_class
DECL|class|SnapshotDiffReport
specifier|public
class|class
name|SnapshotDiffReport
block|{
DECL|field|LINE_SEPARATOR
specifier|private
specifier|final
specifier|static
name|String
name|LINE_SEPARATOR
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|,
literal|"\n"
argument_list|)
decl_stmt|;
comment|/**    * Types of the difference, which include CREATE, MODIFY, DELETE, and RENAME.    * Each type has a label for representation: +/M/-/R represent CREATE, MODIFY,    * DELETE, and RENAME respectively.    */
DECL|enum|DiffType
specifier|public
enum|enum
name|DiffType
block|{
DECL|enumConstant|CREATE
name|CREATE
argument_list|(
literal|"+"
argument_list|)
block|,
DECL|enumConstant|MODIFY
name|MODIFY
argument_list|(
literal|"M"
argument_list|)
block|,
DECL|enumConstant|DELETE
name|DELETE
argument_list|(
literal|"-"
argument_list|)
block|,
DECL|enumConstant|RENAME
name|RENAME
argument_list|(
literal|"R"
argument_list|)
block|;
DECL|field|label
specifier|private
name|String
name|label
decl_stmt|;
DECL|method|DiffType (String label)
specifier|private
name|DiffType
parameter_list|(
name|String
name|label
parameter_list|)
block|{
name|this
operator|.
name|label
operator|=
name|label
expr_stmt|;
block|}
DECL|method|getLabel ()
specifier|public
name|String
name|getLabel
parameter_list|()
block|{
return|return
name|label
return|;
block|}
DECL|method|getTypeFromLabel (String label)
specifier|public
specifier|static
name|DiffType
name|getTypeFromLabel
parameter_list|(
name|String
name|label
parameter_list|)
block|{
if|if
condition|(
name|label
operator|.
name|equals
argument_list|(
name|CREATE
operator|.
name|getLabel
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|CREATE
return|;
block|}
elseif|else
if|if
condition|(
name|label
operator|.
name|equals
argument_list|(
name|MODIFY
operator|.
name|getLabel
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|MODIFY
return|;
block|}
elseif|else
if|if
condition|(
name|label
operator|.
name|equals
argument_list|(
name|DELETE
operator|.
name|getLabel
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|DELETE
return|;
block|}
elseif|else
if|if
condition|(
name|label
operator|.
name|equals
argument_list|(
name|RENAME
operator|.
name|getLabel
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|RENAME
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
empty_stmt|;
comment|/**    * Representing the full path and diff type of a file/directory where changes    * have happened.    */
DECL|class|DiffReportEntry
specifier|public
specifier|static
class|class
name|DiffReportEntry
block|{
comment|/** The type of the difference. */
DECL|field|type
specifier|private
specifier|final
name|DiffType
name|type
decl_stmt|;
comment|/**      * The relative path (related to the snapshot root) of the file/directory      * where changes have happened      */
DECL|field|relativePath
specifier|private
specifier|final
name|byte
index|[]
name|relativePath
decl_stmt|;
DECL|method|DiffReportEntry (DiffType type, byte[] path)
specifier|public
name|DiffReportEntry
parameter_list|(
name|DiffType
name|type
parameter_list|,
name|byte
index|[]
name|path
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|relativePath
operator|=
name|path
expr_stmt|;
block|}
DECL|method|DiffReportEntry (DiffType type, byte[][] pathComponents)
specifier|public
name|DiffReportEntry
parameter_list|(
name|DiffType
name|type
parameter_list|,
name|byte
index|[]
index|[]
name|pathComponents
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|relativePath
operator|=
name|DFSUtil
operator|.
name|byteArray2bytes
argument_list|(
name|pathComponents
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|type
operator|.
name|getLabel
argument_list|()
operator|+
literal|"\t"
operator|+
name|getRelativePathString
argument_list|()
return|;
block|}
DECL|method|getType ()
specifier|public
name|DiffType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|getRelativePathString ()
specifier|public
name|String
name|getRelativePathString
parameter_list|()
block|{
name|String
name|path
init|=
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|relativePath
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|"."
return|;
block|}
else|else
block|{
return|return
literal|"."
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|path
return|;
block|}
block|}
DECL|method|getRelativePath ()
specifier|public
name|byte
index|[]
name|getRelativePath
parameter_list|()
block|{
return|return
name|relativePath
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object other)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|other
operator|!=
literal|null
operator|&&
name|other
operator|instanceof
name|DiffReportEntry
condition|)
block|{
name|DiffReportEntry
name|entry
init|=
operator|(
name|DiffReportEntry
operator|)
name|other
decl_stmt|;
return|return
name|type
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getType
argument_list|()
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|relativePath
argument_list|,
name|entry
operator|.
name|getRelativePath
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|hashCode
argument_list|(
name|relativePath
argument_list|)
return|;
block|}
block|}
comment|/** snapshot root full path */
DECL|field|snapshotRoot
specifier|private
specifier|final
name|String
name|snapshotRoot
decl_stmt|;
comment|/** start point of the diff */
DECL|field|fromSnapshot
specifier|private
specifier|final
name|String
name|fromSnapshot
decl_stmt|;
comment|/** end point of the diff */
DECL|field|toSnapshot
specifier|private
specifier|final
name|String
name|toSnapshot
decl_stmt|;
comment|/** list of diff */
DECL|field|diffList
specifier|private
specifier|final
name|List
argument_list|<
name|DiffReportEntry
argument_list|>
name|diffList
decl_stmt|;
DECL|method|SnapshotDiffReport (String snapshotRoot, String fromSnapshot, String toSnapshot, List<DiffReportEntry> entryList)
specifier|public
name|SnapshotDiffReport
parameter_list|(
name|String
name|snapshotRoot
parameter_list|,
name|String
name|fromSnapshot
parameter_list|,
name|String
name|toSnapshot
parameter_list|,
name|List
argument_list|<
name|DiffReportEntry
argument_list|>
name|entryList
parameter_list|)
block|{
name|this
operator|.
name|snapshotRoot
operator|=
name|snapshotRoot
expr_stmt|;
name|this
operator|.
name|fromSnapshot
operator|=
name|fromSnapshot
expr_stmt|;
name|this
operator|.
name|toSnapshot
operator|=
name|toSnapshot
expr_stmt|;
name|this
operator|.
name|diffList
operator|=
name|entryList
operator|!=
literal|null
condition|?
name|entryList
else|:
name|Collections
operator|.
expr|<
name|DiffReportEntry
operator|>
name|emptyList
argument_list|()
expr_stmt|;
block|}
comment|/** @return {@link #snapshotRoot}*/
DECL|method|getSnapshotRoot ()
specifier|public
name|String
name|getSnapshotRoot
parameter_list|()
block|{
return|return
name|snapshotRoot
return|;
block|}
comment|/** @return {@link #fromSnapshot} */
DECL|method|getFromSnapshot ()
specifier|public
name|String
name|getFromSnapshot
parameter_list|()
block|{
return|return
name|fromSnapshot
return|;
block|}
comment|/** @return {@link #toSnapshot} */
DECL|method|getLaterSnapshotName ()
specifier|public
name|String
name|getLaterSnapshotName
parameter_list|()
block|{
return|return
name|toSnapshot
return|;
block|}
comment|/** @return {@link #diffList} */
DECL|method|getDiffList ()
specifier|public
name|List
argument_list|<
name|DiffReportEntry
argument_list|>
name|getDiffList
parameter_list|()
block|{
return|return
name|diffList
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|str
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|from
init|=
name|fromSnapshot
operator|==
literal|null
operator|||
name|fromSnapshot
operator|.
name|isEmpty
argument_list|()
condition|?
literal|"current directory"
else|:
literal|"snapshot "
operator|+
name|fromSnapshot
decl_stmt|;
name|String
name|to
init|=
name|toSnapshot
operator|==
literal|null
operator|||
name|toSnapshot
operator|.
name|isEmpty
argument_list|()
condition|?
literal|"current directory"
else|:
literal|"snapshot "
operator|+
name|toSnapshot
decl_stmt|;
name|str
operator|.
name|append
argument_list|(
literal|"Diffence between snapshot "
operator|+
name|from
operator|+
literal|" and "
operator|+
name|to
operator|+
literal|" under directory "
operator|+
name|snapshotRoot
operator|+
literal|":"
operator|+
name|LINE_SEPARATOR
argument_list|)
expr_stmt|;
for|for
control|(
name|DiffReportEntry
name|entry
range|:
name|diffList
control|)
block|{
name|str
operator|.
name|append
argument_list|(
name|entry
operator|.
name|toString
argument_list|()
operator|+
name|LINE_SEPARATOR
argument_list|)
expr_stmt|;
block|}
return|return
name|str
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

