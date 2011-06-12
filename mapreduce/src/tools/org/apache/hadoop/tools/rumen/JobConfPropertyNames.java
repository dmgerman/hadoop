begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
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
name|mapreduce
operator|.
name|MRJobConfig
import|;
end_import

begin_enum
DECL|enum|JobConfPropertyNames
specifier|public
enum|enum
name|JobConfPropertyNames
block|{
DECL|enumConstant|QUEUE_NAMES
name|QUEUE_NAMES
argument_list|(
literal|"mapred.job.queue.name"
argument_list|,
name|MRJobConfig
operator|.
name|QUEUE_NAME
argument_list|)
block|,
DECL|enumConstant|JOB_NAMES
name|JOB_NAMES
argument_list|(
literal|"mapred.job.name"
argument_list|,
name|MRJobConfig
operator|.
name|JOB_NAME
argument_list|)
block|,
DECL|enumConstant|TASK_JAVA_OPTS_S
name|TASK_JAVA_OPTS_S
argument_list|(
literal|"mapred.child.java.opts"
argument_list|)
block|,
DECL|enumConstant|MAP_JAVA_OPTS_S
name|MAP_JAVA_OPTS_S
argument_list|(
literal|"mapred.child.java.opts"
argument_list|,
name|MRJobConfig
operator|.
name|MAP_JAVA_OPTS
argument_list|)
block|,
DECL|enumConstant|REDUCE_JAVA_OPTS_S
name|REDUCE_JAVA_OPTS_S
argument_list|(
literal|"mapred.child.java.opts"
argument_list|,
name|MRJobConfig
operator|.
name|REDUCE_JAVA_OPTS
argument_list|)
block|;
DECL|field|candidates
specifier|private
name|String
index|[]
name|candidates
decl_stmt|;
DECL|method|JobConfPropertyNames (String... candidates)
name|JobConfPropertyNames
parameter_list|(
name|String
modifier|...
name|candidates
parameter_list|)
block|{
name|this
operator|.
name|candidates
operator|=
name|candidates
expr_stmt|;
block|}
DECL|method|getCandidates ()
specifier|public
name|String
index|[]
name|getCandidates
parameter_list|()
block|{
return|return
name|candidates
return|;
block|}
block|}
end_enum

end_unit

