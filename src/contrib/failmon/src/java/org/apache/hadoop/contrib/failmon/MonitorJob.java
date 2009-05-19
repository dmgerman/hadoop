begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.contrib.failmon
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|contrib
operator|.
name|failmon
package|;
end_package

begin_comment
comment|/**********************************************************  * This class is a wrapper for a monitoring job.   *   **********************************************************/
end_comment

begin_class
DECL|class|MonitorJob
specifier|public
class|class
name|MonitorJob
block|{
DECL|field|job
name|Monitored
name|job
decl_stmt|;
DECL|field|type
name|String
name|type
decl_stmt|;
DECL|field|interval
name|int
name|interval
decl_stmt|;
DECL|field|counter
name|int
name|counter
decl_stmt|;
DECL|method|MonitorJob (Monitored _job, String _type, int _interval)
specifier|public
name|MonitorJob
parameter_list|(
name|Monitored
name|_job
parameter_list|,
name|String
name|_type
parameter_list|,
name|int
name|_interval
parameter_list|)
block|{
name|job
operator|=
name|_job
expr_stmt|;
name|type
operator|=
name|_type
expr_stmt|;
name|interval
operator|=
name|_interval
expr_stmt|;
name|counter
operator|=
name|_interval
expr_stmt|;
block|}
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|counter
operator|=
name|interval
expr_stmt|;
block|}
block|}
end_class

end_unit

