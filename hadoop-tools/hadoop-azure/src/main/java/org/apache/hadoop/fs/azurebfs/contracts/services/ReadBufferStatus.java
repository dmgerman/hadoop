begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.contracts.services
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|contracts
operator|.
name|services
package|;
end_package

begin_comment
comment|/**  * The ReadBufferStatus for Rest AbfsClient  */
end_comment

begin_enum
DECL|enum|ReadBufferStatus
specifier|public
enum|enum
name|ReadBufferStatus
block|{
DECL|enumConstant|NOT_AVAILABLE
name|NOT_AVAILABLE
block|,
comment|// buffers sitting in readaheadqueue have this stats
DECL|enumConstant|READING_IN_PROGRESS
name|READING_IN_PROGRESS
block|,
comment|// reading is in progress on this buffer. Buffer should be in inProgressList
DECL|enumConstant|AVAILABLE
name|AVAILABLE
block|,
comment|// data is available in buffer. It should be in completedList
DECL|enumConstant|READ_FAILED
name|READ_FAILED
comment|// read completed, but failed.
block|}
end_enum

end_unit

