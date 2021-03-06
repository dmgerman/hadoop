begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|classification
operator|.
name|InterfaceStability
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
name|Writable
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
comment|/** Store the summary of a content (a directory or a file). */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|ContentSummary
specifier|public
class|class
name|ContentSummary
extends|extends
name|QuotaUsage
implements|implements
name|Writable
block|{
DECL|field|length
specifier|private
name|long
name|length
decl_stmt|;
DECL|field|fileCount
specifier|private
name|long
name|fileCount
decl_stmt|;
DECL|field|directoryCount
specifier|private
name|long
name|directoryCount
decl_stmt|;
comment|// These fields are to track the snapshot-related portion of the values.
DECL|field|snapshotLength
specifier|private
name|long
name|snapshotLength
decl_stmt|;
DECL|field|snapshotFileCount
specifier|private
name|long
name|snapshotFileCount
decl_stmt|;
DECL|field|snapshotDirectoryCount
specifier|private
name|long
name|snapshotDirectoryCount
decl_stmt|;
DECL|field|snapshotSpaceConsumed
specifier|private
name|long
name|snapshotSpaceConsumed
decl_stmt|;
DECL|field|erasureCodingPolicy
specifier|private
name|String
name|erasureCodingPolicy
decl_stmt|;
comment|/** We don't use generics. Instead override spaceConsumed and other methods       in order to keep backward compatibility. */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
extends|extends
name|QuotaUsage
operator|.
name|Builder
block|{
DECL|method|Builder ()
specifier|public
name|Builder
parameter_list|()
block|{     }
DECL|method|length (long length)
specifier|public
name|Builder
name|length
parameter_list|(
name|long
name|length
parameter_list|)
block|{
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|fileCount (long fileCount)
specifier|public
name|Builder
name|fileCount
parameter_list|(
name|long
name|fileCount
parameter_list|)
block|{
name|this
operator|.
name|fileCount
operator|=
name|fileCount
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|directoryCount (long directoryCount)
specifier|public
name|Builder
name|directoryCount
parameter_list|(
name|long
name|directoryCount
parameter_list|)
block|{
name|this
operator|.
name|directoryCount
operator|=
name|directoryCount
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|snapshotLength (long snapshotLength)
specifier|public
name|Builder
name|snapshotLength
parameter_list|(
name|long
name|snapshotLength
parameter_list|)
block|{
name|this
operator|.
name|snapshotLength
operator|=
name|snapshotLength
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|snapshotFileCount (long snapshotFileCount)
specifier|public
name|Builder
name|snapshotFileCount
parameter_list|(
name|long
name|snapshotFileCount
parameter_list|)
block|{
name|this
operator|.
name|snapshotFileCount
operator|=
name|snapshotFileCount
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|snapshotDirectoryCount (long snapshotDirectoryCount)
specifier|public
name|Builder
name|snapshotDirectoryCount
parameter_list|(
name|long
name|snapshotDirectoryCount
parameter_list|)
block|{
name|this
operator|.
name|snapshotDirectoryCount
operator|=
name|snapshotDirectoryCount
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|snapshotSpaceConsumed (long snapshotSpaceConsumed)
specifier|public
name|Builder
name|snapshotSpaceConsumed
parameter_list|(
name|long
name|snapshotSpaceConsumed
parameter_list|)
block|{
name|this
operator|.
name|snapshotSpaceConsumed
operator|=
name|snapshotSpaceConsumed
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|erasureCodingPolicy (String ecPolicy)
specifier|public
name|Builder
name|erasureCodingPolicy
parameter_list|(
name|String
name|ecPolicy
parameter_list|)
block|{
name|this
operator|.
name|erasureCodingPolicy
operator|=
name|ecPolicy
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|quota (long quota)
specifier|public
name|Builder
name|quota
parameter_list|(
name|long
name|quota
parameter_list|)
block|{
name|super
operator|.
name|quota
argument_list|(
name|quota
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|spaceConsumed (long spaceConsumed)
specifier|public
name|Builder
name|spaceConsumed
parameter_list|(
name|long
name|spaceConsumed
parameter_list|)
block|{
name|super
operator|.
name|spaceConsumed
argument_list|(
name|spaceConsumed
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|spaceQuota (long spaceQuota)
specifier|public
name|Builder
name|spaceQuota
parameter_list|(
name|long
name|spaceQuota
parameter_list|)
block|{
name|super
operator|.
name|spaceQuota
argument_list|(
name|spaceQuota
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|typeConsumed (long typeConsumed[])
specifier|public
name|Builder
name|typeConsumed
parameter_list|(
name|long
name|typeConsumed
index|[]
parameter_list|)
block|{
name|super
operator|.
name|typeConsumed
argument_list|(
name|typeConsumed
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|typeQuota (StorageType type, long quota)
specifier|public
name|Builder
name|typeQuota
parameter_list|(
name|StorageType
name|type
parameter_list|,
name|long
name|quota
parameter_list|)
block|{
name|super
operator|.
name|typeQuota
argument_list|(
name|type
argument_list|,
name|quota
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|typeConsumed (StorageType type, long consumed)
specifier|public
name|Builder
name|typeConsumed
parameter_list|(
name|StorageType
name|type
parameter_list|,
name|long
name|consumed
parameter_list|)
block|{
name|super
operator|.
name|typeConsumed
argument_list|(
name|type
argument_list|,
name|consumed
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|typeQuota (long typeQuota[])
specifier|public
name|Builder
name|typeQuota
parameter_list|(
name|long
name|typeQuota
index|[]
parameter_list|)
block|{
name|super
operator|.
name|typeQuota
argument_list|(
name|typeQuota
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|ContentSummary
name|build
parameter_list|()
block|{
comment|// Set it in case applications call QuotaUsage#getFileAndDirectoryCount.
name|super
operator|.
name|fileAndDirectoryCount
argument_list|(
name|this
operator|.
name|fileCount
operator|+
name|this
operator|.
name|directoryCount
argument_list|)
expr_stmt|;
return|return
operator|new
name|ContentSummary
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|field|length
specifier|private
name|long
name|length
decl_stmt|;
DECL|field|fileCount
specifier|private
name|long
name|fileCount
decl_stmt|;
DECL|field|directoryCount
specifier|private
name|long
name|directoryCount
decl_stmt|;
DECL|field|snapshotLength
specifier|private
name|long
name|snapshotLength
decl_stmt|;
DECL|field|snapshotFileCount
specifier|private
name|long
name|snapshotFileCount
decl_stmt|;
DECL|field|snapshotDirectoryCount
specifier|private
name|long
name|snapshotDirectoryCount
decl_stmt|;
DECL|field|snapshotSpaceConsumed
specifier|private
name|long
name|snapshotSpaceConsumed
decl_stmt|;
DECL|field|erasureCodingPolicy
specifier|private
name|String
name|erasureCodingPolicy
decl_stmt|;
block|}
comment|/** Constructor deprecated by ContentSummary.Builder*/
annotation|@
name|Deprecated
DECL|method|ContentSummary ()
specifier|public
name|ContentSummary
parameter_list|()
block|{}
comment|/** Constructor, deprecated by ContentSummary.Builder    *  This constructor implicitly set spaceConsumed the same as length.    *  spaceConsumed and length must be set explicitly with    *  ContentSummary.Builder    * */
annotation|@
name|Deprecated
DECL|method|ContentSummary (long length, long fileCount, long directoryCount)
specifier|public
name|ContentSummary
parameter_list|(
name|long
name|length
parameter_list|,
name|long
name|fileCount
parameter_list|,
name|long
name|directoryCount
parameter_list|)
block|{
name|this
argument_list|(
name|length
argument_list|,
name|fileCount
argument_list|,
name|directoryCount
argument_list|,
operator|-
literal|1L
argument_list|,
name|length
argument_list|,
operator|-
literal|1L
argument_list|)
expr_stmt|;
block|}
comment|/** Constructor, deprecated by ContentSummary.Builder */
annotation|@
name|Deprecated
DECL|method|ContentSummary ( long length, long fileCount, long directoryCount, long quota, long spaceConsumed, long spaceQuota)
specifier|public
name|ContentSummary
parameter_list|(
name|long
name|length
parameter_list|,
name|long
name|fileCount
parameter_list|,
name|long
name|directoryCount
parameter_list|,
name|long
name|quota
parameter_list|,
name|long
name|spaceConsumed
parameter_list|,
name|long
name|spaceQuota
parameter_list|)
block|{
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|fileCount
operator|=
name|fileCount
expr_stmt|;
name|this
operator|.
name|directoryCount
operator|=
name|directoryCount
expr_stmt|;
name|setQuota
argument_list|(
name|quota
argument_list|)
expr_stmt|;
name|setSpaceConsumed
argument_list|(
name|spaceConsumed
argument_list|)
expr_stmt|;
name|setSpaceQuota
argument_list|(
name|spaceQuota
argument_list|)
expr_stmt|;
block|}
comment|/** Constructor for ContentSummary.Builder*/
DECL|method|ContentSummary (Builder builder)
specifier|private
name|ContentSummary
parameter_list|(
name|Builder
name|builder
parameter_list|)
block|{
name|super
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|builder
operator|.
name|length
expr_stmt|;
name|this
operator|.
name|fileCount
operator|=
name|builder
operator|.
name|fileCount
expr_stmt|;
name|this
operator|.
name|directoryCount
operator|=
name|builder
operator|.
name|directoryCount
expr_stmt|;
name|this
operator|.
name|snapshotLength
operator|=
name|builder
operator|.
name|snapshotLength
expr_stmt|;
name|this
operator|.
name|snapshotFileCount
operator|=
name|builder
operator|.
name|snapshotFileCount
expr_stmt|;
name|this
operator|.
name|snapshotDirectoryCount
operator|=
name|builder
operator|.
name|snapshotDirectoryCount
expr_stmt|;
name|this
operator|.
name|snapshotSpaceConsumed
operator|=
name|builder
operator|.
name|snapshotSpaceConsumed
expr_stmt|;
name|this
operator|.
name|erasureCodingPolicy
operator|=
name|builder
operator|.
name|erasureCodingPolicy
expr_stmt|;
block|}
comment|/** @return the length */
DECL|method|getLength ()
specifier|public
name|long
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
DECL|method|getSnapshotLength ()
specifier|public
name|long
name|getSnapshotLength
parameter_list|()
block|{
return|return
name|snapshotLength
return|;
block|}
comment|/** @return the directory count */
DECL|method|getDirectoryCount ()
specifier|public
name|long
name|getDirectoryCount
parameter_list|()
block|{
return|return
name|directoryCount
return|;
block|}
DECL|method|getSnapshotDirectoryCount ()
specifier|public
name|long
name|getSnapshotDirectoryCount
parameter_list|()
block|{
return|return
name|snapshotDirectoryCount
return|;
block|}
comment|/** @return the file count */
DECL|method|getFileCount ()
specifier|public
name|long
name|getFileCount
parameter_list|()
block|{
return|return
name|fileCount
return|;
block|}
DECL|method|getSnapshotFileCount ()
specifier|public
name|long
name|getSnapshotFileCount
parameter_list|()
block|{
return|return
name|snapshotFileCount
return|;
block|}
DECL|method|getSnapshotSpaceConsumed ()
specifier|public
name|long
name|getSnapshotSpaceConsumed
parameter_list|()
block|{
return|return
name|snapshotSpaceConsumed
return|;
block|}
DECL|method|getErasureCodingPolicy ()
specifier|public
name|String
name|getErasureCodingPolicy
parameter_list|()
block|{
return|return
name|erasureCodingPolicy
return|;
block|}
annotation|@
name|Override
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|fileCount
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|directoryCount
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|getQuota
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|getSpaceConsumed
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|getSpaceQuota
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|length
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|fileCount
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|directoryCount
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|setQuota
argument_list|(
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
name|setSpaceConsumed
argument_list|(
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
name|setSpaceQuota
argument_list|(
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals (Object to)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|to
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|to
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|to
operator|instanceof
name|ContentSummary
condition|)
block|{
name|ContentSummary
name|right
init|=
operator|(
name|ContentSummary
operator|)
name|to
decl_stmt|;
return|return
name|getLength
argument_list|()
operator|==
name|right
operator|.
name|getLength
argument_list|()
operator|&&
name|getFileCount
argument_list|()
operator|==
name|right
operator|.
name|getFileCount
argument_list|()
operator|&&
name|getDirectoryCount
argument_list|()
operator|==
name|right
operator|.
name|getDirectoryCount
argument_list|()
operator|&&
name|getSnapshotLength
argument_list|()
operator|==
name|right
operator|.
name|getSnapshotLength
argument_list|()
operator|&&
name|getSnapshotFileCount
argument_list|()
operator|==
name|right
operator|.
name|getSnapshotFileCount
argument_list|()
operator|&&
name|getSnapshotDirectoryCount
argument_list|()
operator|==
name|right
operator|.
name|getSnapshotDirectoryCount
argument_list|()
operator|&&
name|getSnapshotSpaceConsumed
argument_list|()
operator|==
name|right
operator|.
name|getSnapshotSpaceConsumed
argument_list|()
operator|&&
name|getErasureCodingPolicy
argument_list|()
operator|.
name|equals
argument_list|(
name|right
operator|.
name|getErasureCodingPolicy
argument_list|()
argument_list|)
operator|&&
name|super
operator|.
name|equals
argument_list|(
name|to
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|equals
argument_list|(
name|to
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|long
name|result
init|=
name|getLength
argument_list|()
operator|^
name|getFileCount
argument_list|()
operator|^
name|getDirectoryCount
argument_list|()
operator|^
name|getSnapshotLength
argument_list|()
operator|^
name|getSnapshotFileCount
argument_list|()
operator|^
name|getSnapshotDirectoryCount
argument_list|()
operator|^
name|getSnapshotSpaceConsumed
argument_list|()
operator|^
name|getErasureCodingPolicy
argument_list|()
operator|.
name|hashCode
argument_list|()
decl_stmt|;
return|return
operator|(
operator|(
name|int
operator|)
name|result
operator|)
operator|^
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**    * Output format:    *<----12----><----12----><-------18------->    *    DIR_COUNT   FILE_COUNT       CONTENT_SIZE    */
DECL|field|SUMMARY_FORMAT
specifier|private
specifier|static
specifier|final
name|String
name|SUMMARY_FORMAT
init|=
literal|"%12s %12s %18s "
decl_stmt|;
DECL|field|SUMMARY_HEADER_FIELDS
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|SUMMARY_HEADER_FIELDS
init|=
operator|new
name|String
index|[]
block|{
literal|"DIR_COUNT"
block|,
literal|"FILE_COUNT"
block|,
literal|"CONTENT_SIZE"
block|}
decl_stmt|;
comment|/** The header string */
DECL|field|SUMMARY_HEADER
specifier|private
specifier|static
specifier|final
name|String
name|SUMMARY_HEADER
init|=
name|String
operator|.
name|format
argument_list|(
name|SUMMARY_FORMAT
argument_list|,
operator|(
name|Object
index|[]
operator|)
name|SUMMARY_HEADER_FIELDS
argument_list|)
decl_stmt|;
DECL|field|ALL_HEADER
specifier|private
specifier|static
specifier|final
name|String
name|ALL_HEADER
init|=
name|QUOTA_HEADER
operator|+
name|SUMMARY_HEADER
decl_stmt|;
comment|/** Return the header of the output.    * if qOption is false, output directory count, file count, and content size;    * if qOption is true, output quota and remaining quota as well.    *     * @param qOption a flag indicating if quota needs to be printed or not    * @return the header of the output    */
DECL|method|getHeader (boolean qOption)
specifier|public
specifier|static
name|String
name|getHeader
parameter_list|(
name|boolean
name|qOption
parameter_list|)
block|{
return|return
name|qOption
condition|?
name|ALL_HEADER
else|:
name|SUMMARY_HEADER
return|;
block|}
comment|/**    * Returns the names of the fields from the summary header.    *     * @return names of fields as displayed in the header    */
DECL|method|getHeaderFields ()
specifier|public
specifier|static
name|String
index|[]
name|getHeaderFields
parameter_list|()
block|{
return|return
name|SUMMARY_HEADER_FIELDS
return|;
block|}
comment|/**    * Returns the names of the fields used in the quota summary.    *     * @return names of quota fields as displayed in the header    */
DECL|method|getQuotaHeaderFields ()
specifier|public
specifier|static
name|String
index|[]
name|getQuotaHeaderFields
parameter_list|()
block|{
return|return
name|QUOTA_HEADER_FIELDS
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
return|return
name|toString
argument_list|(
literal|true
argument_list|)
return|;
block|}
comment|/** Return the string representation of the object in the output format.    * if qOption is false, output directory count, file count, and content size;    * if qOption is true, output quota and remaining quota as well.    *    * @param qOption a flag indicating if quota needs to be printed or not    * @return the string representation of the object   */
annotation|@
name|Override
DECL|method|toString (boolean qOption)
specifier|public
name|String
name|toString
parameter_list|(
name|boolean
name|qOption
parameter_list|)
block|{
return|return
name|toString
argument_list|(
name|qOption
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** Return the string representation of the object in the output format.    * For description of the options,    * @see #toString(boolean, boolean, boolean, boolean, List)    *     * @param qOption a flag indicating if quota needs to be printed or not    * @param hOption a flag indicating if human readable output if to be used    * @return the string representation of the object    */
DECL|method|toString (boolean qOption, boolean hOption)
specifier|public
name|String
name|toString
parameter_list|(
name|boolean
name|qOption
parameter_list|,
name|boolean
name|hOption
parameter_list|)
block|{
return|return
name|toString
argument_list|(
name|qOption
argument_list|,
name|hOption
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/** Return the string representation of the object in the output format.    * For description of the options,    * @see #toString(boolean, boolean, boolean, boolean, List)    *    * @param qOption a flag indicating if quota needs to be printed or not    * @param hOption a flag indicating if human readable output is to be used    * @param xOption a flag indicating if calculation from snapshots is to be    *                included in the output    * @return the string representation of the object    */
DECL|method|toString (boolean qOption, boolean hOption, boolean xOption)
specifier|public
name|String
name|toString
parameter_list|(
name|boolean
name|qOption
parameter_list|,
name|boolean
name|hOption
parameter_list|,
name|boolean
name|xOption
parameter_list|)
block|{
return|return
name|toString
argument_list|(
name|qOption
argument_list|,
name|hOption
argument_list|,
literal|false
argument_list|,
name|xOption
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Return the string representation of the object in the output format.    * For description of the options,    * @see #toString(boolean, boolean, boolean, boolean, List)    *    * @param qOption a flag indicating if quota needs to be printed or not    * @param hOption a flag indicating if human readable output if to be used    * @param tOption a flag indicating if display quota by storage types    * @param types Storage types to display    * @return the string representation of the object    */
DECL|method|toString (boolean qOption, boolean hOption, boolean tOption, List<StorageType> types)
specifier|public
name|String
name|toString
parameter_list|(
name|boolean
name|qOption
parameter_list|,
name|boolean
name|hOption
parameter_list|,
name|boolean
name|tOption
parameter_list|,
name|List
argument_list|<
name|StorageType
argument_list|>
name|types
parameter_list|)
block|{
return|return
name|toString
argument_list|(
name|qOption
argument_list|,
name|hOption
argument_list|,
name|tOption
argument_list|,
literal|false
argument_list|,
name|types
argument_list|)
return|;
block|}
comment|/** Return the string representation of the object in the output format.    * if qOption is false, output directory count, file count, and content size;    * if qOption is true, output quota and remaining quota as well.    * if hOption is false, file sizes are returned in bytes    * if hOption is true, file sizes are returned in human readable    * if tOption is true, display the quota by storage types    * if tOption is false, same logic with #toString(boolean,boolean)    * if xOption is false, output includes the calculation from snapshots    * if xOption is true, output excludes the calculation from snapshots    *    * @param qOption a flag indicating if quota needs to be printed or not    * @param hOption a flag indicating if human readable output is to be used    * @param tOption a flag indicating if display quota by storage types    * @param xOption a flag indicating if calculation from snapshots is to be    *                included in the output    * @param types Storage types to display    * @return the string representation of the object    */
DECL|method|toString (boolean qOption, boolean hOption, boolean tOption, boolean xOption, List<StorageType> types)
specifier|public
name|String
name|toString
parameter_list|(
name|boolean
name|qOption
parameter_list|,
name|boolean
name|hOption
parameter_list|,
name|boolean
name|tOption
parameter_list|,
name|boolean
name|xOption
parameter_list|,
name|List
argument_list|<
name|StorageType
argument_list|>
name|types
parameter_list|)
block|{
name|String
name|prefix
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|tOption
condition|)
block|{
return|return
name|getTypesQuotaUsage
argument_list|(
name|hOption
argument_list|,
name|types
argument_list|)
return|;
block|}
if|if
condition|(
name|qOption
condition|)
block|{
name|prefix
operator|=
name|getQuotaUsage
argument_list|(
name|hOption
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|xOption
condition|)
block|{
return|return
name|prefix
operator|+
name|String
operator|.
name|format
argument_list|(
name|SUMMARY_FORMAT
argument_list|,
name|formatSize
argument_list|(
name|directoryCount
operator|-
name|snapshotDirectoryCount
argument_list|,
name|hOption
argument_list|)
argument_list|,
name|formatSize
argument_list|(
name|fileCount
operator|-
name|snapshotFileCount
argument_list|,
name|hOption
argument_list|)
argument_list|,
name|formatSize
argument_list|(
name|length
operator|-
name|snapshotLength
argument_list|,
name|hOption
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|prefix
operator|+
name|String
operator|.
name|format
argument_list|(
name|SUMMARY_FORMAT
argument_list|,
name|formatSize
argument_list|(
name|directoryCount
argument_list|,
name|hOption
argument_list|)
argument_list|,
name|formatSize
argument_list|(
name|fileCount
argument_list|,
name|hOption
argument_list|)
argument_list|,
name|formatSize
argument_list|(
name|length
argument_list|,
name|hOption
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**    * Formats a size to be human readable or in bytes    * @param size value to be formatted    * @param humanReadable flag indicating human readable or not    * @return String representation of the size   */
DECL|method|formatSize (long size, boolean humanReadable)
specifier|private
name|String
name|formatSize
parameter_list|(
name|long
name|size
parameter_list|,
name|boolean
name|humanReadable
parameter_list|)
block|{
return|return
name|humanReadable
condition|?
name|StringUtils
operator|.
name|TraditionalBinaryPrefix
operator|.
name|long2String
argument_list|(
name|size
argument_list|,
literal|""
argument_list|,
literal|1
argument_list|)
else|:
name|String
operator|.
name|valueOf
argument_list|(
name|size
argument_list|)
return|;
block|}
block|}
end_class

end_unit

