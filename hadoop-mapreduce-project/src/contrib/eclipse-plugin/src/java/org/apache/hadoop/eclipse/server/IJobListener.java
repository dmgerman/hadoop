begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.eclipse.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|eclipse
operator|.
name|server
package|;
end_package

begin_comment
comment|/**  * Interface for updating/adding jobs to the MapReduce Server view.  */
end_comment

begin_interface
DECL|interface|IJobListener
specifier|public
interface|interface
name|IJobListener
block|{
DECL|method|jobChanged (HadoopJob job)
name|void
name|jobChanged
parameter_list|(
name|HadoopJob
name|job
parameter_list|)
function_decl|;
DECL|method|jobAdded (HadoopJob job)
name|void
name|jobAdded
parameter_list|(
name|HadoopJob
name|job
parameter_list|)
function_decl|;
DECL|method|jobRemoved (HadoopJob job)
name|void
name|jobRemoved
parameter_list|(
name|HadoopJob
name|job
parameter_list|)
function_decl|;
DECL|method|publishStart (JarModule jar)
name|void
name|publishStart
parameter_list|(
name|JarModule
name|jar
parameter_list|)
function_decl|;
DECL|method|publishDone (JarModule jar)
name|void
name|publishDone
parameter_list|(
name|JarModule
name|jar
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

