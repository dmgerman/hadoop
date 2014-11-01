begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
package|;
end_package

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
name|FileSystem
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
name|security
operator|.
name|UserGroupInformation
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
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/**  * A utility to manage job submission files.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|JobSubmissionFiles
specifier|public
class|class
name|JobSubmissionFiles
block|{
DECL|field|LOG
specifier|private
specifier|final
specifier|static
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|JobSubmissionFiles
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// job submission directory is private!
DECL|field|JOB_DIR_PERMISSION
specifier|final
specifier|public
specifier|static
name|FsPermission
name|JOB_DIR_PERMISSION
init|=
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0700
argument_list|)
decl_stmt|;
comment|// rwx--------
comment|//job files are world-wide readable and owner writable
DECL|field|JOB_FILE_PERMISSION
specifier|final
specifier|public
specifier|static
name|FsPermission
name|JOB_FILE_PERMISSION
init|=
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0644
argument_list|)
decl_stmt|;
comment|// rw-r--r--
DECL|method|getJobSplitFile (Path jobSubmissionDir)
specifier|public
specifier|static
name|Path
name|getJobSplitFile
parameter_list|(
name|Path
name|jobSubmissionDir
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|jobSubmissionDir
argument_list|,
literal|"job.split"
argument_list|)
return|;
block|}
DECL|method|getJobSplitMetaFile (Path jobSubmissionDir)
specifier|public
specifier|static
name|Path
name|getJobSplitMetaFile
parameter_list|(
name|Path
name|jobSubmissionDir
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|jobSubmissionDir
argument_list|,
literal|"job.splitmetainfo"
argument_list|)
return|;
block|}
comment|/**    * Get the job conf path.    */
DECL|method|getJobConfPath (Path jobSubmitDir)
specifier|public
specifier|static
name|Path
name|getJobConfPath
parameter_list|(
name|Path
name|jobSubmitDir
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|jobSubmitDir
argument_list|,
literal|"job.xml"
argument_list|)
return|;
block|}
comment|/**    * Get the job jar path.    */
DECL|method|getJobJar (Path jobSubmitDir)
specifier|public
specifier|static
name|Path
name|getJobJar
parameter_list|(
name|Path
name|jobSubmitDir
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|jobSubmitDir
argument_list|,
literal|"job.jar"
argument_list|)
return|;
block|}
comment|/**    * Get the job distributed cache files path.    * @param jobSubmitDir    */
DECL|method|getJobDistCacheFiles (Path jobSubmitDir)
specifier|public
specifier|static
name|Path
name|getJobDistCacheFiles
parameter_list|(
name|Path
name|jobSubmitDir
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|jobSubmitDir
argument_list|,
literal|"files"
argument_list|)
return|;
block|}
comment|/**    * Get the job distributed cache path for log4j properties.    * @param jobSubmitDir    */
DECL|method|getJobLog4jFile (Path jobSubmitDir)
specifier|public
specifier|static
name|Path
name|getJobLog4jFile
parameter_list|(
name|Path
name|jobSubmitDir
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|jobSubmitDir
argument_list|,
literal|"log4j"
argument_list|)
return|;
block|}
comment|/**    * Get the job distributed cache archives path.    * @param jobSubmitDir     */
DECL|method|getJobDistCacheArchives (Path jobSubmitDir)
specifier|public
specifier|static
name|Path
name|getJobDistCacheArchives
parameter_list|(
name|Path
name|jobSubmitDir
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|jobSubmitDir
argument_list|,
literal|"archives"
argument_list|)
return|;
block|}
comment|/**    * Get the job distributed cache libjars path.    * @param jobSubmitDir     */
DECL|method|getJobDistCacheLibjars (Path jobSubmitDir)
specifier|public
specifier|static
name|Path
name|getJobDistCacheLibjars
parameter_list|(
name|Path
name|jobSubmitDir
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|jobSubmitDir
argument_list|,
literal|"libjars"
argument_list|)
return|;
block|}
comment|/**    * Initializes the staging directory and returns the path. It also    * keeps track of all necessary ownership& permissions    * @param cluster    * @param conf    */
DECL|method|getStagingDir (Cluster cluster, Configuration conf)
specifier|public
specifier|static
name|Path
name|getStagingDir
parameter_list|(
name|Cluster
name|cluster
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|Path
name|stagingArea
init|=
name|cluster
operator|.
name|getStagingAreaDir
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|stagingArea
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
name|realUser
decl_stmt|;
name|String
name|currentUser
decl_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
decl_stmt|;
name|realUser
operator|=
name|ugi
operator|.
name|getShortUserName
argument_list|()
expr_stmt|;
name|currentUser
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
expr_stmt|;
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|stagingArea
argument_list|)
condition|)
block|{
name|FileStatus
name|fsStatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|stagingArea
argument_list|)
decl_stmt|;
name|String
name|owner
init|=
name|fsStatus
operator|.
name|getOwner
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|owner
operator|.
name|equals
argument_list|(
name|currentUser
argument_list|)
operator|||
name|owner
operator|.
name|equals
argument_list|(
name|realUser
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"The ownership on the staging directory "
operator|+
name|stagingArea
operator|+
literal|" is not as expected. "
operator|+
literal|"It is owned by "
operator|+
name|owner
operator|+
literal|". The directory must "
operator|+
literal|"be owned by the submitter "
operator|+
name|currentUser
operator|+
literal|" or "
operator|+
literal|"by "
operator|+
name|realUser
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|fsStatus
operator|.
name|getPermission
argument_list|()
operator|.
name|equals
argument_list|(
name|JOB_DIR_PERMISSION
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Permissions on staging directory "
operator|+
name|stagingArea
operator|+
literal|" are "
operator|+
literal|"incorrect: "
operator|+
name|fsStatus
operator|.
name|getPermission
argument_list|()
operator|+
literal|". Fixing permissions "
operator|+
literal|"to correct value "
operator|+
name|JOB_DIR_PERMISSION
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setPermission
argument_list|(
name|stagingArea
argument_list|,
name|JOB_DIR_PERMISSION
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|fs
operator|.
name|mkdirs
argument_list|(
name|stagingArea
argument_list|,
operator|new
name|FsPermission
argument_list|(
name|JOB_DIR_PERMISSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|stagingArea
return|;
block|}
block|}
end_class

end_unit

