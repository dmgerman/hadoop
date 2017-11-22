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

begin_comment
comment|/**  * Support for adding fault injection: all the failing committers in the IT  * tests must implement this.  */
end_comment

begin_interface
DECL|interface|CommitterFaultInjection
specifier|public
interface|interface
name|CommitterFaultInjection
block|{
DECL|field|COMMIT_FAILURE_MESSAGE
name|String
name|COMMIT_FAILURE_MESSAGE
init|=
literal|"oops"
decl_stmt|;
DECL|method|setFaults (Faults... faults)
name|void
name|setFaults
parameter_list|(
name|Faults
modifier|...
name|faults
parameter_list|)
function_decl|;
comment|/**    * Operations which can fail.    */
DECL|enum|Faults
enum|enum
name|Faults
block|{
DECL|enumConstant|abortJob
name|abortJob
block|,
DECL|enumConstant|abortTask
name|abortTask
block|,
DECL|enumConstant|cleanupJob
name|cleanupJob
block|,
DECL|enumConstant|commitJob
name|commitJob
block|,
DECL|enumConstant|commitTask
name|commitTask
block|,
DECL|enumConstant|getWorkPath
name|getWorkPath
block|,
DECL|enumConstant|needsTaskCommit
name|needsTaskCommit
block|,
DECL|enumConstant|setupJob
name|setupJob
block|,
DECL|enumConstant|setupTask
name|setupTask
block|,   }
block|}
end_interface

end_unit

