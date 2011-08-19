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

begin_class
DECL|class|Constants
specifier|public
class|class
name|Constants
block|{
DECL|field|OUTPUT
specifier|static
specifier|final
name|String
name|OUTPUT
init|=
literal|"output"
decl_stmt|;
DECL|field|HADOOP_WORK_DIR
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_WORK_DIR
init|=
literal|"HADOOP_WORK_DIR"
decl_stmt|;
DECL|field|JOBFILE
specifier|public
specifier|static
specifier|final
name|String
name|JOBFILE
init|=
literal|"job.xml"
decl_stmt|;
DECL|field|STDOUT_LOGFILE_ENV
specifier|public
specifier|static
specifier|final
name|String
name|STDOUT_LOGFILE_ENV
init|=
literal|"STDOUT_LOGFILE_ENV"
decl_stmt|;
DECL|field|STDERR_LOGFILE_ENV
specifier|public
specifier|static
specifier|final
name|String
name|STDERR_LOGFILE_ENV
init|=
literal|"STDERR_LOGFILE_ENV"
decl_stmt|;
block|}
end_class

end_unit

