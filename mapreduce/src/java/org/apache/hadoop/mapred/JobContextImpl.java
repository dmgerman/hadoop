begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/* Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|Progressable
import|;
end_import

begin_comment
comment|/**  * @deprecated Use {@link org.apache.hadoop.mapreduce.JobContext} instead.  */
end_comment

begin_class
annotation|@
name|Deprecated
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|JobContextImpl
specifier|public
class|class
name|JobContextImpl
extends|extends
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|task
operator|.
name|JobContextImpl
implements|implements
name|JobContext
block|{
DECL|field|job
specifier|private
name|JobConf
name|job
decl_stmt|;
DECL|field|progress
specifier|private
name|Progressable
name|progress
decl_stmt|;
DECL|method|JobContextImpl (JobConf conf, org.apache.hadoop.mapreduce.JobID jobId, Progressable progress)
name|JobContextImpl
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|JobID
name|jobId
parameter_list|,
name|Progressable
name|progress
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|,
name|jobId
argument_list|)
expr_stmt|;
name|this
operator|.
name|job
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|progress
operator|=
name|progress
expr_stmt|;
block|}
DECL|method|JobContextImpl (JobConf conf, org.apache.hadoop.mapreduce.JobID jobId)
name|JobContextImpl
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|JobID
name|jobId
parameter_list|)
block|{
name|this
argument_list|(
name|conf
argument_list|,
name|jobId
argument_list|,
name|Reporter
operator|.
name|NULL
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the job Configuration    *     * @return JobConf    */
DECL|method|getJobConf ()
specifier|public
name|JobConf
name|getJobConf
parameter_list|()
block|{
return|return
name|job
return|;
block|}
comment|/**    * Get the progress mechanism for reporting progress.    *     * @return progress mechanism     */
DECL|method|getProgressible ()
specifier|public
name|Progressable
name|getProgressible
parameter_list|()
block|{
return|return
name|progress
return|;
block|}
block|}
end_class

end_unit

