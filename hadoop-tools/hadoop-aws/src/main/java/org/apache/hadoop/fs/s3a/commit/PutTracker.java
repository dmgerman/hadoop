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
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|PartETag
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

begin_comment
comment|/**  * Multipart put tracker.  * Base class does nothing except declare that any  * MPU must complete in the {@code close()} operation.  *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|PutTracker
specifier|public
class|class
name|PutTracker
block|{
comment|/** The destination. */
DECL|field|destKey
specifier|private
specifier|final
name|String
name|destKey
decl_stmt|;
comment|/**    * Instantiate.    * @param destKey destination key    */
DECL|method|PutTracker (String destKey)
specifier|public
name|PutTracker
parameter_list|(
name|String
name|destKey
parameter_list|)
block|{
name|this
operator|.
name|destKey
operator|=
name|destKey
expr_stmt|;
block|}
comment|/**    * Startup event.    * @return true if the multipart should start immediately.    * @throws IOException any IO problem.    */
DECL|method|initialize ()
specifier|public
name|boolean
name|initialize
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Flag to indicate that output is not immediately visible after the stream    * is closed. Default: false.    * @return true if the output's visibility will be delayed.    */
DECL|method|outputImmediatelyVisible ()
specifier|public
name|boolean
name|outputImmediatelyVisible
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**    * Callback when the upload is is about to complete.    * @param uploadId Upload ID    * @param parts list of parts    * @param bytesWritten bytes written    * @return true if the commit is to be initiated immediately.    * False implies the output stream does not need to worry about    * what happens.    * @throws IOException I/O problem or validation failure.    */
DECL|method|aboutToComplete (String uploadId, List<PartETag> parts, long bytesWritten)
specifier|public
name|boolean
name|aboutToComplete
parameter_list|(
name|String
name|uploadId
parameter_list|,
name|List
argument_list|<
name|PartETag
argument_list|>
name|parts
parameter_list|,
name|long
name|bytesWritten
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|true
return|;
block|}
comment|/**    * get the destination key. The default implementation returns the    * key passed in: there is no adjustment of the destination.    * @return the destination to use in PUT requests.    */
DECL|method|getDestKey ()
specifier|public
name|String
name|getDestKey
parameter_list|()
block|{
return|return
name|destKey
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
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"DefaultPutTracker{"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"destKey='"
argument_list|)
operator|.
name|append
argument_list|(
name|destKey
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'}'
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
end_class

end_unit

