begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.commit.magic
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
operator|.
name|magic
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
name|commit
operator|.
name|AbstractITCommitMRJob
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
name|files
operator|.
name|SuccessData
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
name|*
import|;
end_import

begin_comment
comment|/**  * Full integration test for the Magic Committer.  *  * There's no need to disable the committer setting for the filesystem here,  * because the committers are being instantiated in their own processes;  * the settings in {@link #applyCustomConfigOptions(Configuration)} are  * passed down to these processes.  */
end_comment

begin_class
DECL|class|ITMagicCommitMRJob
specifier|public
class|class
name|ITMagicCommitMRJob
extends|extends
name|AbstractITCommitMRJob
block|{
comment|/**    * Need consistency here.    * @return false    */
annotation|@
name|Override
DECL|method|useInconsistentClient ()
specifier|public
name|boolean
name|useInconsistentClient
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|committerName ()
specifier|protected
name|String
name|committerName
parameter_list|()
block|{
return|return
name|MagicS3GuardCommitter
operator|.
name|NAME
return|;
block|}
comment|/**    * Turn on the magic commit support for the FS, else nothing will work.    * @param conf configuration    */
annotation|@
name|Override
DECL|method|applyCustomConfigOptions (Configuration conf)
specifier|protected
name|void
name|applyCustomConfigOptions
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|MAGIC_COMMITTER_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check that the magic dir was cleaned up.    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|customPostExecutionValidation (Path destPath, SuccessData successData)
specifier|protected
name|void
name|customPostExecutionValidation
parameter_list|(
name|Path
name|destPath
parameter_list|,
name|SuccessData
name|successData
parameter_list|)
throws|throws
name|Exception
block|{
name|assertPathDoesNotExist
argument_list|(
literal|"No cleanup"
argument_list|,
operator|new
name|Path
argument_list|(
name|destPath
argument_list|,
name|MAGIC
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

