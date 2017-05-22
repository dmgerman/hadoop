begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
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
comment|/**  * This class defines a partial listing of a directory to support  * iterative directory listing.  */
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
DECL|class|DirectoryListing
specifier|public
class|class
name|DirectoryListing
block|{
DECL|field|partialListing
specifier|private
name|HdfsFileStatus
index|[]
name|partialListing
decl_stmt|;
DECL|field|remainingEntries
specifier|private
name|int
name|remainingEntries
decl_stmt|;
comment|/**    * constructor    * @param partialListing a partial listing of a directory    * @param remainingEntries number of entries that are left to be listed    */
DECL|method|DirectoryListing (HdfsFileStatus[] partialListing, int remainingEntries)
specifier|public
name|DirectoryListing
parameter_list|(
name|HdfsFileStatus
index|[]
name|partialListing
parameter_list|,
name|int
name|remainingEntries
parameter_list|)
block|{
if|if
condition|(
name|partialListing
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"partial listing should not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|partialListing
operator|.
name|length
operator|==
literal|0
operator|&&
name|remainingEntries
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Partial listing is empty but "
operator|+
literal|"the number of remaining entries is not zero"
argument_list|)
throw|;
block|}
name|this
operator|.
name|partialListing
operator|=
name|partialListing
expr_stmt|;
name|this
operator|.
name|remainingEntries
operator|=
name|remainingEntries
expr_stmt|;
block|}
comment|/**    * Get the partial listing of file status    * @return the partial listing of file status    */
DECL|method|getPartialListing ()
specifier|public
name|HdfsFileStatus
index|[]
name|getPartialListing
parameter_list|()
block|{
return|return
name|partialListing
return|;
block|}
comment|/**    * Get the number of remaining entries that are left to be listed    * @return the number of remaining entries that are left to be listed    */
DECL|method|getRemainingEntries ()
specifier|public
name|int
name|getRemainingEntries
parameter_list|()
block|{
return|return
name|remainingEntries
return|;
block|}
comment|/**    * Check if there are more entries that are left to be listed    * @return true if there are more entries that are left to be listed;    *         return false otherwise.    */
DECL|method|hasMore ()
specifier|public
name|boolean
name|hasMore
parameter_list|()
block|{
return|return
name|remainingEntries
operator|!=
literal|0
return|;
block|}
comment|/**    * Get the last name in this list    * @return the last name in the list if it is not empty; otherwise return null    */
DECL|method|getLastName ()
specifier|public
name|byte
index|[]
name|getLastName
parameter_list|()
block|{
if|if
condition|(
name|partialListing
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|partialListing
index|[
name|partialListing
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|getLocalNameInBytes
argument_list|()
return|;
block|}
block|}
end_class

end_unit

