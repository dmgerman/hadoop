begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.jobhistory
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|jobhistory
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
name|mapred
operator|.
name|JobConf
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|JobId
import|;
end_import

begin_comment
comment|/**  * Maintains information which may be used by the jobHistory indexing  * system.  */
end_comment

begin_class
DECL|class|JobIndexInfo
specifier|public
class|class
name|JobIndexInfo
block|{
DECL|field|submitTime
specifier|private
name|long
name|submitTime
decl_stmt|;
DECL|field|finishTime
specifier|private
name|long
name|finishTime
decl_stmt|;
DECL|field|user
specifier|private
name|String
name|user
decl_stmt|;
DECL|field|queueName
specifier|private
name|String
name|queueName
decl_stmt|;
DECL|field|jobName
specifier|private
name|String
name|jobName
decl_stmt|;
DECL|field|jobId
specifier|private
name|JobId
name|jobId
decl_stmt|;
DECL|field|numMaps
specifier|private
name|int
name|numMaps
decl_stmt|;
DECL|field|numReduces
specifier|private
name|int
name|numReduces
decl_stmt|;
DECL|field|jobStatus
specifier|private
name|String
name|jobStatus
decl_stmt|;
DECL|field|jobStartTime
specifier|private
name|long
name|jobStartTime
decl_stmt|;
DECL|method|JobIndexInfo ()
specifier|public
name|JobIndexInfo
parameter_list|()
block|{   }
DECL|method|JobIndexInfo (long submitTime, long finishTime, String user, String jobName, JobId jobId, int numMaps, int numReduces, String jobStatus)
specifier|public
name|JobIndexInfo
parameter_list|(
name|long
name|submitTime
parameter_list|,
name|long
name|finishTime
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|jobName
parameter_list|,
name|JobId
name|jobId
parameter_list|,
name|int
name|numMaps
parameter_list|,
name|int
name|numReduces
parameter_list|,
name|String
name|jobStatus
parameter_list|)
block|{
name|this
argument_list|(
name|submitTime
argument_list|,
name|finishTime
argument_list|,
name|user
argument_list|,
name|jobName
argument_list|,
name|jobId
argument_list|,
name|numMaps
argument_list|,
name|numReduces
argument_list|,
name|jobStatus
argument_list|,
name|JobConf
operator|.
name|DEFAULT_QUEUE_NAME
argument_list|)
expr_stmt|;
block|}
DECL|method|JobIndexInfo (long submitTime, long finishTime, String user, String jobName, JobId jobId, int numMaps, int numReduces, String jobStatus, String queueName)
specifier|public
name|JobIndexInfo
parameter_list|(
name|long
name|submitTime
parameter_list|,
name|long
name|finishTime
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|jobName
parameter_list|,
name|JobId
name|jobId
parameter_list|,
name|int
name|numMaps
parameter_list|,
name|int
name|numReduces
parameter_list|,
name|String
name|jobStatus
parameter_list|,
name|String
name|queueName
parameter_list|)
block|{
name|this
operator|.
name|submitTime
operator|=
name|submitTime
expr_stmt|;
name|this
operator|.
name|finishTime
operator|=
name|finishTime
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|jobName
operator|=
name|jobName
expr_stmt|;
name|this
operator|.
name|jobId
operator|=
name|jobId
expr_stmt|;
name|this
operator|.
name|numMaps
operator|=
name|numMaps
expr_stmt|;
name|this
operator|.
name|numReduces
operator|=
name|numReduces
expr_stmt|;
name|this
operator|.
name|jobStatus
operator|=
name|jobStatus
expr_stmt|;
name|this
operator|.
name|jobStartTime
operator|=
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|queueName
operator|=
name|queueName
expr_stmt|;
block|}
DECL|method|getSubmitTime ()
specifier|public
name|long
name|getSubmitTime
parameter_list|()
block|{
return|return
name|submitTime
return|;
block|}
DECL|method|setSubmitTime (long submitTime)
specifier|public
name|void
name|setSubmitTime
parameter_list|(
name|long
name|submitTime
parameter_list|)
block|{
name|this
operator|.
name|submitTime
operator|=
name|submitTime
expr_stmt|;
block|}
DECL|method|getFinishTime ()
specifier|public
name|long
name|getFinishTime
parameter_list|()
block|{
return|return
name|finishTime
return|;
block|}
DECL|method|setFinishTime (long finishTime)
specifier|public
name|void
name|setFinishTime
parameter_list|(
name|long
name|finishTime
parameter_list|)
block|{
name|this
operator|.
name|finishTime
operator|=
name|finishTime
expr_stmt|;
block|}
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
DECL|method|setUser (String user)
specifier|public
name|void
name|setUser
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
block|}
DECL|method|getQueueName ()
specifier|public
name|String
name|getQueueName
parameter_list|()
block|{
return|return
name|queueName
return|;
block|}
DECL|method|setQueueName (String queueName)
specifier|public
name|void
name|setQueueName
parameter_list|(
name|String
name|queueName
parameter_list|)
block|{
name|this
operator|.
name|queueName
operator|=
name|queueName
expr_stmt|;
block|}
DECL|method|getJobName ()
specifier|public
name|String
name|getJobName
parameter_list|()
block|{
return|return
name|jobName
return|;
block|}
DECL|method|setJobName (String jobName)
specifier|public
name|void
name|setJobName
parameter_list|(
name|String
name|jobName
parameter_list|)
block|{
name|this
operator|.
name|jobName
operator|=
name|jobName
expr_stmt|;
block|}
DECL|method|getJobId ()
specifier|public
name|JobId
name|getJobId
parameter_list|()
block|{
return|return
name|jobId
return|;
block|}
DECL|method|setJobId (JobId jobId)
specifier|public
name|void
name|setJobId
parameter_list|(
name|JobId
name|jobId
parameter_list|)
block|{
name|this
operator|.
name|jobId
operator|=
name|jobId
expr_stmt|;
block|}
DECL|method|getNumMaps ()
specifier|public
name|int
name|getNumMaps
parameter_list|()
block|{
return|return
name|numMaps
return|;
block|}
DECL|method|setNumMaps (int numMaps)
specifier|public
name|void
name|setNumMaps
parameter_list|(
name|int
name|numMaps
parameter_list|)
block|{
name|this
operator|.
name|numMaps
operator|=
name|numMaps
expr_stmt|;
block|}
DECL|method|getNumReduces ()
specifier|public
name|int
name|getNumReduces
parameter_list|()
block|{
return|return
name|numReduces
return|;
block|}
DECL|method|setNumReduces (int numReduces)
specifier|public
name|void
name|setNumReduces
parameter_list|(
name|int
name|numReduces
parameter_list|)
block|{
name|this
operator|.
name|numReduces
operator|=
name|numReduces
expr_stmt|;
block|}
DECL|method|getJobStatus ()
specifier|public
name|String
name|getJobStatus
parameter_list|()
block|{
return|return
name|jobStatus
return|;
block|}
DECL|method|setJobStatus (String jobStatus)
specifier|public
name|void
name|setJobStatus
parameter_list|(
name|String
name|jobStatus
parameter_list|)
block|{
name|this
operator|.
name|jobStatus
operator|=
name|jobStatus
expr_stmt|;
block|}
DECL|method|getJobStartTime ()
specifier|public
name|long
name|getJobStartTime
parameter_list|()
block|{
return|return
name|jobStartTime
return|;
block|}
DECL|method|setJobStartTime (long lTime)
specifier|public
name|void
name|setJobStartTime
parameter_list|(
name|long
name|lTime
parameter_list|)
block|{
name|this
operator|.
name|jobStartTime
operator|=
name|lTime
expr_stmt|;
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
literal|"JobIndexInfo [submitTime="
operator|+
name|submitTime
operator|+
literal|", finishTime="
operator|+
name|finishTime
operator|+
literal|", user="
operator|+
name|user
operator|+
literal|", jobName="
operator|+
name|jobName
operator|+
literal|", jobId="
operator|+
name|jobId
operator|+
literal|", numMaps="
operator|+
name|numMaps
operator|+
literal|", numReduces="
operator|+
name|numReduces
operator|+
literal|", jobStatus="
operator|+
name|jobStatus
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

