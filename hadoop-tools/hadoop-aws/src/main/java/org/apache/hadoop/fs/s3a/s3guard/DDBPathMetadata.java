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
name|S3AFileStatus
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
comment|/**  * {@code DDBPathMetadata} wraps {@link PathMetadata} and adds the  * isAuthoritativeDir flag to provide support for authoritative directory  * listings in {@link DynamoDBMetadataStore}.  */
end_comment

begin_class
DECL|class|DDBPathMetadata
specifier|public
class|class
name|DDBPathMetadata
extends|extends
name|PathMetadata
block|{
DECL|field|isAuthoritativeDir
specifier|private
name|boolean
name|isAuthoritativeDir
decl_stmt|;
DECL|method|DDBPathMetadata (PathMetadata pmd)
specifier|public
name|DDBPathMetadata
parameter_list|(
name|PathMetadata
name|pmd
parameter_list|)
block|{
name|super
argument_list|(
name|pmd
operator|.
name|getFileStatus
argument_list|()
argument_list|,
name|pmd
operator|.
name|isEmptyDirectory
argument_list|()
argument_list|,
name|pmd
operator|.
name|isDeleted
argument_list|()
argument_list|,
name|pmd
operator|.
name|getLastUpdated
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|isAuthoritativeDir
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|setLastUpdated
argument_list|(
name|pmd
operator|.
name|getLastUpdated
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|DDBPathMetadata (S3AFileStatus fileStatus)
specifier|public
name|DDBPathMetadata
parameter_list|(
name|S3AFileStatus
name|fileStatus
parameter_list|)
block|{
name|super
argument_list|(
name|fileStatus
argument_list|)
expr_stmt|;
name|this
operator|.
name|isAuthoritativeDir
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|DDBPathMetadata (S3AFileStatus fileStatus, Tristate isEmptyDir, boolean isDeleted, long lastUpdated)
specifier|public
name|DDBPathMetadata
parameter_list|(
name|S3AFileStatus
name|fileStatus
parameter_list|,
name|Tristate
name|isEmptyDir
parameter_list|,
name|boolean
name|isDeleted
parameter_list|,
name|long
name|lastUpdated
parameter_list|)
block|{
name|super
argument_list|(
name|fileStatus
argument_list|,
name|isEmptyDir
argument_list|,
name|isDeleted
argument_list|,
name|lastUpdated
argument_list|)
expr_stmt|;
name|this
operator|.
name|isAuthoritativeDir
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|DDBPathMetadata (S3AFileStatus fileStatus, Tristate isEmptyDir, boolean isDeleted, boolean isAuthoritativeDir, long lastUpdated)
specifier|public
name|DDBPathMetadata
parameter_list|(
name|S3AFileStatus
name|fileStatus
parameter_list|,
name|Tristate
name|isEmptyDir
parameter_list|,
name|boolean
name|isDeleted
parameter_list|,
name|boolean
name|isAuthoritativeDir
parameter_list|,
name|long
name|lastUpdated
parameter_list|)
block|{
name|super
argument_list|(
name|fileStatus
argument_list|,
name|isEmptyDir
argument_list|,
name|isDeleted
argument_list|,
name|lastUpdated
argument_list|)
expr_stmt|;
name|this
operator|.
name|isAuthoritativeDir
operator|=
name|isAuthoritativeDir
expr_stmt|;
block|}
DECL|method|isAuthoritativeDir ()
specifier|public
name|boolean
name|isAuthoritativeDir
parameter_list|()
block|{
return|return
name|isAuthoritativeDir
return|;
block|}
DECL|method|setAuthoritativeDir (boolean authoritativeDir)
specifier|public
name|void
name|setAuthoritativeDir
parameter_list|(
name|boolean
name|authoritativeDir
parameter_list|)
block|{
name|isAuthoritativeDir
operator|=
name|authoritativeDir
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
return|return
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
return|;
block|}
DECL|method|hashCode ()
annotation|@
name|Override
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
DECL|method|toString ()
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"DDBPathMetadata{"
operator|+
literal|"isAuthoritativeDir="
operator|+
name|isAuthoritativeDir
operator|+
literal|", PathMetadata="
operator|+
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

