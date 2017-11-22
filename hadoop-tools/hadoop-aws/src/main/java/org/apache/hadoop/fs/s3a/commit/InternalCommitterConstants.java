begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.commit
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
name|commit
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
name|s3a
operator|.
name|commit
operator|.
name|magic
operator|.
name|MagicS3GuardCommitterFactory
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
name|commit
operator|.
name|staging
operator|.
name|DirectoryStagingCommitterFactory
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
name|commit
operator|.
name|staging
operator|.
name|PartitionedStagingCommitterFactory
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
name|commit
operator|.
name|staging
operator|.
name|StagingCommitterFactory
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
name|s3a
operator|.
name|commit
operator|.
name|CommitConstants
operator|.
name|MAGIC
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
name|s3a
operator|.
name|commit
operator|.
name|CommitConstants
operator|.
name|MAGIC_COMMITTER_ENABLED
import|;
end_import

begin_comment
comment|/**  * These are internal constants not intended for public use.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|InternalCommitterConstants
specifier|public
specifier|final
class|class
name|InternalCommitterConstants
block|{
DECL|method|InternalCommitterConstants ()
specifier|private
name|InternalCommitterConstants
parameter_list|()
block|{   }
comment|/**    * This is the staging committer base class; only used for testing.    */
DECL|field|COMMITTER_NAME_STAGING
specifier|public
specifier|static
specifier|final
name|String
name|COMMITTER_NAME_STAGING
init|=
literal|"staging"
decl_stmt|;
comment|/**    * A unique identifier to use for this work: {@value}.    */
DECL|field|FS_S3A_COMMITTER_STAGING_UUID
specifier|public
specifier|static
specifier|final
name|String
name|FS_S3A_COMMITTER_STAGING_UUID
init|=
literal|"fs.s3a.committer.staging.uuid"
decl_stmt|;
comment|/**    * Directory committer factory: {@value}.    */
DECL|field|STAGING_COMMITTER_FACTORY
specifier|public
specifier|static
specifier|final
name|String
name|STAGING_COMMITTER_FACTORY
init|=
name|StagingCommitterFactory
operator|.
name|CLASSNAME
decl_stmt|;
comment|/**    * Directory committer factory: {@value}.    */
DECL|field|DIRECTORY_COMMITTER_FACTORY
specifier|public
specifier|static
specifier|final
name|String
name|DIRECTORY_COMMITTER_FACTORY
init|=
name|DirectoryStagingCommitterFactory
operator|.
name|CLASSNAME
decl_stmt|;
comment|/**    * Partitioned committer factory: {@value}.    */
DECL|field|PARTITION_COMMITTER_FACTORY
specifier|public
specifier|static
specifier|final
name|String
name|PARTITION_COMMITTER_FACTORY
init|=
name|PartitionedStagingCommitterFactory
operator|.
name|CLASSNAME
decl_stmt|;
comment|/**    * Magic committer factory: {@value}.    */
DECL|field|MAGIC_COMMITTER_FACTORY
specifier|public
specifier|static
specifier|final
name|String
name|MAGIC_COMMITTER_FACTORY
init|=
name|MagicS3GuardCommitterFactory
operator|.
name|CLASSNAME
decl_stmt|;
comment|/**    * Error text when the destination path exists and the committer    * must abort the job/task {@value}.    */
DECL|field|E_DEST_EXISTS
specifier|public
specifier|static
specifier|final
name|String
name|E_DEST_EXISTS
init|=
literal|"Destination path exists and committer conflict resolution mode is "
operator|+
literal|"\"fail\""
decl_stmt|;
comment|/** Error message for bad path: {@value}. */
DECL|field|E_BAD_PATH
specifier|public
specifier|static
specifier|final
name|String
name|E_BAD_PATH
init|=
literal|"Path does not represent a magic-commit path"
decl_stmt|;
comment|/** Error message if filesystem isn't magic: {@value}. */
DECL|field|E_NORMAL_FS
specifier|public
specifier|static
specifier|final
name|String
name|E_NORMAL_FS
init|=
literal|"Filesystem does not have support for 'magic' committer enabled"
operator|+
literal|" in configuration option "
operator|+
name|MAGIC_COMMITTER_ENABLED
decl_stmt|;
comment|/** Error message if the dest FS isn't S3A: {@value}. */
DECL|field|E_WRONG_FS
specifier|public
specifier|static
specifier|final
name|String
name|E_WRONG_FS
init|=
literal|"Output path is not on an S3A Filesystem"
decl_stmt|;
comment|/** Error message for a path without a magic element in the list: {@value}. */
DECL|field|E_NO_MAGIC_PATH_ELEMENT
specifier|public
specifier|static
specifier|final
name|String
name|E_NO_MAGIC_PATH_ELEMENT
init|=
literal|"No "
operator|+
name|MAGIC
operator|+
literal|" element in path"
decl_stmt|;
block|}
end_class

end_unit

