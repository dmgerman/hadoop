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
name|HdfsServerConstants
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
comment|/**    * Get the image files which should be loaded into the filesystem.    * @throws IOException if not enough files are available (eg no image found in any directory)    */
DECL|method|getLatestImages ()
specifier|abstract
name|List
argument_list|<
name|FSImageFile
argument_list|>
name|getLatestImages
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**     * Get the minimum tx id which should be loaded with this set of images.    */
DECL|method|getMaxSeenTxId ()
specifier|abstract
name|long
name|getMaxSeenTxId
parameter_list|()
function_decl|;
comment|/**    * @return true if the directories are in such a state that the image should be re-saved    * following the load    */
DECL|method|needToSave ()
specifier|abstract
name|boolean
name|needToSave
parameter_list|()
function_decl|;
comment|/**    * Record of an image that has been located and had its filename parsed.    */
DECL|class|FSImageFile
specifier|static
class|class
name|FSImageFile
block|{
DECL|field|sd
specifier|final
name|StorageDirectory
name|sd
decl_stmt|;
DECL|field|txId
specifier|final
name|long
name|txId
decl_stmt|;
DECL|field|file
specifier|private
specifier|final
name|File
name|file
decl_stmt|;
DECL|method|FSImageFile (StorageDirectory sd, File file, long txId)
name|FSImageFile
parameter_list|(
name|StorageDirectory
name|sd
parameter_list|,
name|File
name|file
parameter_list|,
name|long
name|txId
parameter_list|)
block|{
assert|assert
name|txId
operator|>=
literal|0
operator|||
name|txId
operator|==
name|HdfsServerConstants
operator|.
name|INVALID_TXID
operator|:
literal|"Invalid txid on "
operator|+
name|file
operator|+
literal|": "
operator|+
name|txId
assert|;
name|this
operator|.
name|sd
operator|=
name|sd
expr_stmt|;
name|this
operator|.
name|txId
operator|=
name|txId
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
block|}
DECL|method|getFile ()
name|File
name|getFile
parameter_list|()
block|{
return|return
name|file
return|;
block|}
DECL|method|getCheckpointTxId ()
specifier|public
name|long
name|getCheckpointTxId
parameter_list|()
block|{
return|return
name|txId
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
name|String
operator|.
name|format
argument_list|(
literal|"FSImageFile(file=%s, cpktTxId=%019d)"
argument_list|,
name|file
operator|.
name|toString
argument_list|()
argument_list|,
name|txId
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

