begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.s3guard
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|s3guard
package|;
end_package

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
name|s3a
operator|.
name|Tristate
import|;
end_import

begin_comment
comment|/**  * {@code PathMetadata} models path metadata stored in the  * {@link MetadataStore}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|PathMetadata
specifier|public
class|class
name|PathMetadata
extends|extends
name|ExpirableMetadata
block|{
DECL|field|fileStatus
specifier|private
specifier|final
name|FileStatus
name|fileStatus
decl_stmt|;
DECL|field|isEmptyDirectory
specifier|private
name|Tristate
name|isEmptyDirectory
decl_stmt|;
DECL|field|isDeleted
specifier|private
name|boolean
name|isDeleted
decl_stmt|;
comment|/**    * Create a tombstone from the current time.    * @param path path to tombstone    * @return the entry.    */
DECL|method|tombstone (Path path)
specifier|public
specifier|static
name|PathMetadata
name|tombstone
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|FileStatus
name|status
init|=
operator|new
name|FileStatus
argument_list|(
literal|0
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|now
argument_list|,
name|path
argument_list|)
decl_stmt|;
return|return
operator|new
name|PathMetadata
argument_list|(
name|status
argument_list|,
name|Tristate
operator|.
name|UNKNOWN
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**    * Creates a new {@code PathMetadata} containing given {@code FileStatus}.    * @param fileStatus file status containing an absolute path.    */
DECL|method|PathMetadata (FileStatus fileStatus)
specifier|public
name|PathMetadata
parameter_list|(
name|FileStatus
name|fileStatus
parameter_list|)
block|{
name|this
argument_list|(
name|fileStatus
argument_list|,
name|Tristate
operator|.
name|UNKNOWN
argument_list|)
expr_stmt|;
block|}
DECL|method|PathMetadata (FileStatus fileStatus, Tristate isEmptyDir)
specifier|public
name|PathMetadata
parameter_list|(
name|FileStatus
name|fileStatus
parameter_list|,
name|Tristate
name|isEmptyDir
parameter_list|)
block|{
name|this
argument_list|(
name|fileStatus
argument_list|,
name|isEmptyDir
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|PathMetadata (FileStatus fileStatus, Tristate isEmptyDir, boolean isDeleted)
specifier|public
name|PathMetadata
parameter_list|(
name|FileStatus
name|fileStatus
parameter_list|,
name|Tristate
name|isEmptyDir
parameter_list|,
name|boolean
name|isDeleted
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|fileStatus
argument_list|,
literal|"fileStatus must be non-null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|fileStatus
operator|.
name|getPath
argument_list|()
argument_list|,
literal|"fileStatus path must be"
operator|+
literal|" non-null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|fileStatus
operator|.
name|getPath
argument_list|()
operator|.
name|isAbsolute
argument_list|()
argument_list|,
literal|"path must"
operator|+
literal|" be absolute"
argument_list|)
expr_stmt|;
name|this
operator|.
name|fileStatus
operator|=
name|fileStatus
expr_stmt|;
name|this
operator|.
name|isEmptyDirectory
operator|=
name|isEmptyDir
expr_stmt|;
name|this
operator|.
name|isDeleted
operator|=
name|isDeleted
expr_stmt|;
block|}
comment|/**    * @return {@code FileStatus} contained in this {@code PathMetadata}.    */
DECL|method|getFileStatus ()
specifier|public
specifier|final
name|FileStatus
name|getFileStatus
parameter_list|()
block|{
return|return
name|fileStatus
return|;
block|}
comment|/**    * Query if a directory is empty.    * @return Tristate.TRUE if this is known to be an empty directory,    * Tristate.FALSE if known to not be empty, and Tristate.UNKNOWN if the    * MetadataStore does have enough information to determine either way.    */
DECL|method|isEmptyDirectory ()
specifier|public
name|Tristate
name|isEmptyDirectory
parameter_list|()
block|{
return|return
name|isEmptyDirectory
return|;
block|}
DECL|method|setIsEmptyDirectory (Tristate isEmptyDirectory)
name|void
name|setIsEmptyDirectory
parameter_list|(
name|Tristate
name|isEmptyDirectory
parameter_list|)
block|{
name|this
operator|.
name|isEmptyDirectory
operator|=
name|isEmptyDirectory
expr_stmt|;
block|}
DECL|method|isDeleted ()
specifier|public
name|boolean
name|isDeleted
parameter_list|()
block|{
return|return
name|isDeleted
return|;
block|}
DECL|method|setIsDeleted (boolean isDeleted)
name|void
name|setIsDeleted
parameter_list|(
name|boolean
name|isDeleted
parameter_list|)
block|{
name|this
operator|.
name|isDeleted
operator|=
name|isDeleted
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|PathMetadata
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|this
operator|.
name|fileStatus
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|PathMetadata
operator|)
name|o
operator|)
operator|.
name|fileStatus
argument_list|)
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
name|fileStatus
operator|.
name|hashCode
argument_list|()
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
literal|"PathMetadata{"
operator|+
literal|"fileStatus="
operator|+
name|fileStatus
operator|+
literal|"; isEmptyDirectory="
operator|+
name|isEmptyDirectory
operator|+
literal|"; isDeleted="
operator|+
name|isDeleted
operator|+
literal|'}'
return|;
block|}
comment|/**    * Log contents to supplied StringBuilder in a pretty fashion.    * @param sb target StringBuilder    */
DECL|method|prettyPrint (StringBuilder sb)
specifier|public
name|void
name|prettyPrint
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%-5s %-20s %-7d %-8s %-6s"
argument_list|,
name|fileStatus
operator|.
name|isDirectory
argument_list|()
condition|?
literal|"dir"
else|:
literal|"file"
argument_list|,
name|fileStatus
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|fileStatus
operator|.
name|getLen
argument_list|()
argument_list|,
name|isEmptyDirectory
operator|.
name|name
argument_list|()
argument_list|,
name|isDeleted
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|fileStatus
argument_list|)
expr_stmt|;
block|}
DECL|method|prettyPrint ()
specifier|public
name|String
name|prettyPrint
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|prettyPrint
argument_list|(
name|sb
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

