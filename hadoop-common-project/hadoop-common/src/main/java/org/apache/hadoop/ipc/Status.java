begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
package|;
end_package

begin_comment
comment|/**  * Status of a Hadoop IPC call.  */
end_comment

begin_enum
DECL|enum|Status
enum|enum
name|Status
block|{
DECL|enumConstant|SUCCESS
name|SUCCESS
argument_list|(
literal|0
argument_list|)
block|,
DECL|enumConstant|ERROR
name|ERROR
argument_list|(
literal|1
argument_list|)
block|,
DECL|enumConstant|FATAL
name|FATAL
argument_list|(
operator|-
literal|1
argument_list|)
block|;
DECL|field|state
name|int
name|state
decl_stmt|;
DECL|method|Status (int state)
specifier|private
name|Status
parameter_list|(
name|int
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
block|}
end_enum

end_unit

