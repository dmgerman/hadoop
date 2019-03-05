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
name|conf
operator|.
name|Configured
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
name|ReflectionUtils
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

begin_comment
comment|/**   * This interface is used for implementing different Trash policies.  * Provides factory method to create instances of the configured Trash policy.  */
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
DECL|class|TrashPolicy
specifier|public
specifier|abstract
class|class
name|TrashPolicy
extends|extends
name|Configured
block|{
DECL|field|fs
specifier|protected
name|FileSystem
name|fs
decl_stmt|;
comment|// the FileSystem
DECL|field|trash
specifier|protected
name|Path
name|trash
decl_stmt|;
comment|// path to trash directory
DECL|field|deletionInterval
specifier|protected
name|long
name|deletionInterval
decl_stmt|;
comment|// deletion interval for Emptier
comment|/**    * Used to setup the trash policy. Must be implemented by all TrashPolicy    * implementations.    * @param conf the configuration to be used    * @param fs the filesystem to be used    * @param home the home directory    * @deprecated Use {@link #initialize(Configuration, FileSystem)} instead.    */
annotation|@
name|Deprecated
DECL|method|initialize (Configuration conf, FileSystem fs, Path home)
specifier|public
specifier|abstract
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
name|Path
name|home
parameter_list|)
function_decl|;
comment|/**    * Used to setup the trash policy. Must be implemented by all TrashPolicy    * implementations. Different from initialize(conf, fs, home), this one does    * not assume trash always under /user/$USER due to HDFS encryption zone.    * @param conf the configuration to be used    * @param fs the filesystem to be used    */
DECL|method|initialize (Configuration conf, FileSystem fs)
specifier|public
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FileSystem
name|fs
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**    * Returns whether the Trash Policy is enabled for this filesystem.    */
DECL|method|isEnabled ()
specifier|public
specifier|abstract
name|boolean
name|isEnabled
parameter_list|()
function_decl|;
comment|/**     * Move a file or directory to the current trash directory.    * @return false if the item is already in the trash or trash is disabled    */
DECL|method|moveToTrash (Path path)
specifier|public
specifier|abstract
name|boolean
name|moveToTrash
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**     * Create a trash checkpoint.     */
DECL|method|createCheckpoint ()
specifier|public
specifier|abstract
name|void
name|createCheckpoint
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**     * Delete old trash checkpoint(s).    */
DECL|method|deleteCheckpoint ()
specifier|public
specifier|abstract
name|void
name|deleteCheckpoint
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Delete all checkpoints immediately, ie empty trash.    */
DECL|method|deleteCheckpointsImmediately ()
specifier|public
specifier|abstract
name|void
name|deleteCheckpointsImmediately
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the current working directory of the Trash Policy    * This API does not work with files deleted from encryption zone when HDFS    * data encryption at rest feature is enabled as rename file between    * encryption zones or encryption zone and non-encryption zone is not allowed.    *    * The caller is recommend to use the new API    * TrashPolicy#getCurrentTrashDir(Path path).    * It returns the trash location correctly for the path specified no matter    * the path is in encryption zone or not.    */
DECL|method|getCurrentTrashDir ()
specifier|public
specifier|abstract
name|Path
name|getCurrentTrashDir
parameter_list|()
function_decl|;
comment|/**    * Get the current trash directory for path specified based on the Trash    * Policy    * @param path path to be deleted    * @return current trash directory for the path to be deleted    * @throws IOException    */
DECL|method|getCurrentTrashDir (Path path)
specifier|public
name|Path
name|getCurrentTrashDir
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**     * Return a {@link Runnable} that periodically empties the trash of all    * users, intended to be run by the superuser.    */
DECL|method|getEmptier ()
specifier|public
specifier|abstract
name|Runnable
name|getEmptier
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get an instance of the configured TrashPolicy based on the value    * of the configuration parameter fs.trash.classname.    *    * @param conf the configuration to be used    * @param fs the file system to be used    * @param home the home directory    * @return an instance of TrashPolicy    * @deprecated Use {@link #getInstance(Configuration, FileSystem)} instead.    */
annotation|@
name|Deprecated
DECL|method|getInstance (Configuration conf, FileSystem fs, Path home)
specifier|public
specifier|static
name|TrashPolicy
name|getInstance
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
name|Path
name|home
parameter_list|)
block|{
name|Class
argument_list|<
name|?
extends|extends
name|TrashPolicy
argument_list|>
name|trashClass
init|=
name|conf
operator|.
name|getClass
argument_list|(
literal|"fs.trash.classname"
argument_list|,
name|TrashPolicyDefault
operator|.
name|class
argument_list|,
name|TrashPolicy
operator|.
name|class
argument_list|)
decl_stmt|;
name|TrashPolicy
name|trash
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|trashClass
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|trash
operator|.
name|initialize
argument_list|(
name|conf
argument_list|,
name|fs
argument_list|,
name|home
argument_list|)
expr_stmt|;
comment|// initialize TrashPolicy
return|return
name|trash
return|;
block|}
comment|/**    * Get an instance of the configured TrashPolicy based on the value    * of the configuration parameter fs.trash.classname.    *    * @param conf the configuration to be used    * @param fs the file system to be used    * @return an instance of TrashPolicy    */
DECL|method|getInstance (Configuration conf, FileSystem fs)
specifier|public
specifier|static
name|TrashPolicy
name|getInstance
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FileSystem
name|fs
parameter_list|)
block|{
name|Class
argument_list|<
name|?
extends|extends
name|TrashPolicy
argument_list|>
name|trashClass
init|=
name|conf
operator|.
name|getClass
argument_list|(
literal|"fs.trash.classname"
argument_list|,
name|TrashPolicyDefault
operator|.
name|class
argument_list|,
name|TrashPolicy
operator|.
name|class
argument_list|)
decl_stmt|;
name|TrashPolicy
name|trash
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|trashClass
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|trash
operator|.
name|initialize
argument_list|(
name|conf
argument_list|,
name|fs
argument_list|)
expr_stmt|;
comment|// initialize TrashPolicy
return|return
name|trash
return|;
block|}
block|}
end_class

end_unit

