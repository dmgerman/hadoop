begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
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
name|BlockLocation
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
name|LocatedFileStatus
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_comment
comment|/**  * {@link LocatedFileStatus} extended to also carry ETag and object version ID.  */
end_comment

begin_class
DECL|class|S3ALocatedFileStatus
specifier|public
class|class
name|S3ALocatedFileStatus
extends|extends
name|LocatedFileStatus
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|3597192103662929338L
decl_stmt|;
DECL|field|eTag
specifier|private
specifier|final
name|String
name|eTag
decl_stmt|;
DECL|field|versionId
specifier|private
specifier|final
name|String
name|versionId
decl_stmt|;
DECL|field|isEmptyDirectory
specifier|private
specifier|final
name|Tristate
name|isEmptyDirectory
decl_stmt|;
DECL|method|S3ALocatedFileStatus (S3AFileStatus status, BlockLocation[] locations)
specifier|public
name|S3ALocatedFileStatus
parameter_list|(
name|S3AFileStatus
name|status
parameter_list|,
name|BlockLocation
index|[]
name|locations
parameter_list|)
block|{
name|super
argument_list|(
name|checkNotNull
argument_list|(
name|status
argument_list|)
argument_list|,
name|locations
argument_list|)
expr_stmt|;
name|this
operator|.
name|eTag
operator|=
name|status
operator|.
name|getETag
argument_list|()
expr_stmt|;
name|this
operator|.
name|versionId
operator|=
name|status
operator|.
name|getVersionId
argument_list|()
expr_stmt|;
name|isEmptyDirectory
operator|=
name|status
operator|.
name|isEmptyDirectory
argument_list|()
expr_stmt|;
block|}
DECL|method|getETag ()
specifier|public
name|String
name|getETag
parameter_list|()
block|{
return|return
name|eTag
return|;
block|}
DECL|method|getVersionId ()
specifier|public
name|String
name|getVersionId
parameter_list|()
block|{
return|return
name|versionId
return|;
block|}
comment|// equals() and hashCode() overridden to avoid FindBugs warning.
comment|// Base implementation is equality on Path only, which is still appropriate.
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
return|return
name|super
operator|.
name|equals
argument_list|(
name|o
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
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**    * Generate an S3AFileStatus instance, including etag and    * version ID, if present.    */
DECL|method|toS3AFileStatus ()
specifier|public
name|S3AFileStatus
name|toS3AFileStatus
parameter_list|()
block|{
return|return
operator|new
name|S3AFileStatus
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|isDirectory
argument_list|()
argument_list|,
name|isEmptyDirectory
argument_list|,
name|getLen
argument_list|()
argument_list|,
name|getModificationTime
argument_list|()
argument_list|,
name|getBlockSize
argument_list|()
argument_list|,
name|getOwner
argument_list|()
argument_list|,
name|getETag
argument_list|()
argument_list|,
name|getVersionId
argument_list|()
argument_list|)
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
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|super
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"[eTag='"
argument_list|)
operator|.
name|append
argument_list|(
name|eTag
operator|!=
literal|null
condition|?
name|eTag
else|:
literal|""
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", versionId='"
argument_list|)
operator|.
name|append
argument_list|(
name|versionId
operator|!=
literal|null
condition|?
name|versionId
else|:
literal|""
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|']'
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

