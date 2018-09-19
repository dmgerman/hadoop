begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.common.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|submarine
operator|.
name|common
operator|.
name|fs
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_interface
DECL|interface|RemoteDirectoryManager
specifier|public
interface|interface
name|RemoteDirectoryManager
block|{
DECL|method|getJobStagingArea (String jobName, boolean create)
name|Path
name|getJobStagingArea
parameter_list|(
name|String
name|jobName
parameter_list|,
name|boolean
name|create
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getJobCheckpointDir (String jobName, boolean create)
name|Path
name|getJobCheckpointDir
parameter_list|(
name|String
name|jobName
parameter_list|,
name|boolean
name|create
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getModelDir (String modelName, boolean create)
name|Path
name|getModelDir
parameter_list|(
name|String
name|modelName
parameter_list|,
name|boolean
name|create
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getFileSystem ()
name|FileSystem
name|getFileSystem
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|getUserRootFolder ()
name|Path
name|getUserRootFolder
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

