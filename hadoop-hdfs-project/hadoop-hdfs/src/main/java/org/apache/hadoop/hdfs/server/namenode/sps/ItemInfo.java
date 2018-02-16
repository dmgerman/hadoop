begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.sps
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
operator|.
name|sps
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

begin_comment
comment|/**  * ItemInfo is a file info object for which need to satisfy the policy. For  * internal satisfier service, it uses inode id which is Long datatype. For the  * external satisfier service, it uses the full string representation of the  * path.  */
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
DECL|class|ItemInfo
specifier|public
class|class
name|ItemInfo
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|startPath
specifier|private
name|T
name|startPath
decl_stmt|;
DECL|field|file
specifier|private
name|T
name|file
decl_stmt|;
DECL|field|retryCount
specifier|private
name|int
name|retryCount
decl_stmt|;
DECL|method|ItemInfo (T startPath, T file)
specifier|public
name|ItemInfo
parameter_list|(
name|T
name|startPath
parameter_list|,
name|T
name|file
parameter_list|)
block|{
name|this
operator|.
name|startPath
operator|=
name|startPath
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
comment|// set 0 when item is getting added first time in queue.
name|this
operator|.
name|retryCount
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|ItemInfo (final T startPath, final T file, final int retryCount)
specifier|public
name|ItemInfo
parameter_list|(
specifier|final
name|T
name|startPath
parameter_list|,
specifier|final
name|T
name|file
parameter_list|,
specifier|final
name|int
name|retryCount
parameter_list|)
block|{
name|this
operator|.
name|startPath
operator|=
name|startPath
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|this
operator|.
name|retryCount
operator|=
name|retryCount
expr_stmt|;
block|}
comment|/**    * Returns the start path of the current file. This indicates that SPS    * was invoked on this path.    */
DECL|method|getStartPath ()
specifier|public
name|T
name|getStartPath
parameter_list|()
block|{
return|return
name|startPath
return|;
block|}
comment|/**    * Returns the file for which needs to satisfy the policy.    */
DECL|method|getFile ()
specifier|public
name|T
name|getFile
parameter_list|()
block|{
return|return
name|file
return|;
block|}
comment|/**    * Returns true if the tracking path is a directory, false otherwise.    */
DECL|method|isDir ()
specifier|public
name|boolean
name|isDir
parameter_list|()
block|{
return|return
operator|!
name|startPath
operator|.
name|equals
argument_list|(
name|file
argument_list|)
return|;
block|}
comment|/**    * Get the attempted retry count of the block for satisfy the policy.    */
DECL|method|getRetryCount ()
specifier|public
name|int
name|getRetryCount
parameter_list|()
block|{
return|return
name|retryCount
return|;
block|}
comment|/**    * Increments the retry count.    */
DECL|method|increRetryCount ()
specifier|public
name|void
name|increRetryCount
parameter_list|()
block|{
name|this
operator|.
name|retryCount
operator|++
expr_stmt|;
block|}
block|}
end_class

end_unit

