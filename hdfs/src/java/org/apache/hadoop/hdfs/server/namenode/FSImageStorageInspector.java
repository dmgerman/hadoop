begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|Storage
operator|.
name|StorageDirectory
import|;
end_import

begin_comment
comment|/**  * Interface responsible for inspecting a set of storage directories and devising  * a plan to load the namespace from them.  */
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
DECL|class|FSImageStorageInspector
specifier|abstract
class|class
name|FSImageStorageInspector
block|{
comment|/**    * Inspect the contents of the given storage directory.    */
DECL|method|inspectDirectory (StorageDirectory sd)
specifier|abstract
name|void
name|inspectDirectory
parameter_list|(
name|StorageDirectory
name|sd
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * @return false if any of the storage directories have an unfinalized upgrade     */
DECL|method|isUpgradeFinalized ()
specifier|abstract
name|boolean
name|isUpgradeFinalized
parameter_list|()
function_decl|;
comment|/**    * Create a plan to load the image from the set of inspected storage directories.    * @throws IOException if not enough files are available (eg no image found in any directory)    */
DECL|method|createLoadPlan ()
specifier|abstract
name|LoadPlan
name|createLoadPlan
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * @return true if the directories are in such a state that the image should be re-saved    * following the load    */
DECL|method|needToSave ()
specifier|abstract
name|boolean
name|needToSave
parameter_list|()
function_decl|;
comment|/**    * A plan to load the namespace from disk, providing the locations from which to load    * the image and a set of edits files.    */
DECL|class|LoadPlan
specifier|abstract
specifier|static
class|class
name|LoadPlan
block|{
comment|/**      * Execute atomic move sequence in the chosen storage directories,      * in order to recover from an interrupted checkpoint.      * @return true if some recovery action was taken      */
DECL|method|doRecovery ()
specifier|abstract
name|boolean
name|doRecovery
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * @return the file from which to load the image data      */
DECL|method|getImageFile ()
specifier|abstract
name|File
name|getImageFile
parameter_list|()
function_decl|;
comment|/**      * @return a list of flies containing edits to replay      */
DECL|method|getEditsFiles ()
specifier|abstract
name|List
argument_list|<
name|File
argument_list|>
name|getEditsFiles
parameter_list|()
function_decl|;
comment|/**      * @return the storage directory containing the VERSION file that should be      * loaded.      */
DECL|method|getStorageDirectoryForProperties ()
specifier|abstract
name|StorageDirectory
name|getStorageDirectoryForProperties
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Will load image file: "
argument_list|)
operator|.
name|append
argument_list|(
name|getImageFile
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Will load edits files:"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|File
name|f
range|:
name|getEditsFiles
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
operator|.
name|append
argument_list|(
name|f
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"Will load metadata from: "
argument_list|)
operator|.
name|append
argument_list|(
name|getStorageDirectoryForProperties
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
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
block|}
end_class

end_unit

