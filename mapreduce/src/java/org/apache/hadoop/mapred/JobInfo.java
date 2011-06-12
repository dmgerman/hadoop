begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|io
operator|.
name|Text
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
name|io
operator|.
name|Writable
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
name|io
operator|.
name|WritableUtils
import|;
end_import

begin_comment
comment|/**  * Represents the basic information that is saved per a job when the   * JobTracker receives a submitJob request. The information is saved  * so that the JobTracker can recover incomplete jobs upon restart.  */
end_comment

begin_class
DECL|class|JobInfo
class|class
name|JobInfo
implements|implements
name|Writable
block|{
DECL|field|id
specifier|private
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|JobID
name|id
decl_stmt|;
DECL|field|user
specifier|private
name|Text
name|user
decl_stmt|;
DECL|field|jobSubmitDir
specifier|private
name|Path
name|jobSubmitDir
decl_stmt|;
DECL|method|JobInfo ()
specifier|public
name|JobInfo
parameter_list|()
block|{}
DECL|method|JobInfo (org.apache.hadoop.mapreduce.JobID id, Text user, Path jobSubmitDir)
specifier|public
name|JobInfo
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|JobID
name|id
parameter_list|,
name|Text
name|user
parameter_list|,
name|Path
name|jobSubmitDir
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|jobSubmitDir
operator|=
name|jobSubmitDir
expr_stmt|;
block|}
comment|/**    * Get the job id.    */
DECL|method|getJobID ()
specifier|public
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|JobID
name|getJobID
parameter_list|()
block|{
return|return
name|id
return|;
block|}
comment|/**    * Get the configured job's user-name.    */
DECL|method|getUser ()
specifier|public
name|Text
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
comment|/**    * Get the job submission directory    */
DECL|method|getJobSubmitDir ()
specifier|public
name|Path
name|getJobSubmitDir
parameter_list|()
block|{
return|return
name|this
operator|.
name|jobSubmitDir
return|;
block|}
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|id
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|JobID
argument_list|()
expr_stmt|;
name|id
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|user
operator|=
operator|new
name|Text
argument_list|()
expr_stmt|;
name|user
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|jobSubmitDir
operator|=
operator|new
name|Path
argument_list|(
name|WritableUtils
operator|.
name|readString
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|id
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|user
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|jobSubmitDir
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

